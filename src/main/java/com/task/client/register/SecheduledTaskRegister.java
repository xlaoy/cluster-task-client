package com.task.client.register;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.InstanceInfo;
import com.task.client.config.TaskClientProperties;
import com.task.client.exception.TaskClientException;
import com.task.client.SecheduledTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Base64;

/**
 * Created by Administrator on 2018/8/19 0019.
 */
public class SecheduledTaskRegister {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private TaskClientProperties properties;
    private ApplicationInfoManager infoManager;
    private ApplicationContext context;
    private LoadBalancerClient loadBalancerClient;
    private ObjectMapper mapper;

    private static final String TASK_SERVER_REGISTER_URL = "/task_server/register_secheduled_task";

    public void setProperties(TaskClientProperties properties) {
        this.properties = properties;
    }

    public void setInfoManager(ApplicationInfoManager infoManager) {
        this.infoManager = infoManager;
    }

    public void setContext(ApplicationContext context) {
        this.context = context;
    }

    public void setLoadBalancerClient(LoadBalancerClient loadBalancerClient) {
        this.loadBalancerClient = loadBalancerClient;
    }

    public void setMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }


    public void register() {
        if(!properties.isRegister()) {
            logger.warn("服务配置定时任务不能注册");
            return;
        }
        if(StringUtils.isEmpty(properties.getTaskServerName())) {
            throw new TaskClientException("定时任务server的服务应用名称配置task.client.taskServerName不能为空");
        }
        SecheduledRegisterDTO registerDTO = this.getSecheduledRegisterDTO();
        if(!CollectionUtils.isEmpty(registerDTO.getSecheduledInfoList())) {
            this.sendRegisterRequest(properties, registerDTO);
        } else {
            logger.warn("定时任务列表为空，系统不注册定时任务");
        }
    }

    private SecheduledRegisterDTO getSecheduledRegisterDTO() {
        SecheduledRegisterDTO registerDTO = new SecheduledRegisterDTO();
        InstanceInfo instanceInfo = infoManager.getInfo();
        registerDTO.setServiceName(instanceInfo.getAppName());
        registerDTO.setHostAndPort(instanceInfo.getIPAddr() + ":" + instanceInfo.getPort());
        List<SecheduledRegisterDTO.SecheduledInfo> secheduledInfoList = new ArrayList<>();
        Map<String, SecheduledTask> beanMap = context.getBeansOfType(SecheduledTask.class);
        if(!CollectionUtils.isEmpty(beanMap)) {
            beanMap.forEach((key, value) -> {
                //检验表达式是否配置正确
                new CronSequenceGenerator(value.cron()).next(new Date());
                Class clazz = value.getClass();
                SecheduledRegisterDTO.SecheduledInfo secheduledInfo = new SecheduledRegisterDTO.SecheduledInfo();
                secheduledInfo.setClassName(clazz.getName());
                secheduledInfo.setCron(value.cron());
                secheduledInfoList.add(secheduledInfo);
            });
        }
        registerDTO.setSecheduledInfoList(secheduledInfoList);
        return registerDTO;
    }

    private void sendRegisterRequest(TaskClientProperties properties, SecheduledRegisterDTO registerDTO) {
        ServiceInstance instance = this.loadBalancerClient.choose(properties.getTaskServerName().toUpperCase());
        if(instance == null) {
            throw new TaskClientException("没找到定时任务server，taskServerName=" + properties.getTaskServerName());
        }
        URI registerUri = URI.create(String.format("http://%s:%s" + TASK_SERVER_REGISTER_URL, instance.getHost(), instance.getPort()));
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        byte[] bytes = (properties.getUsername() + ":" + properties.getPassword()).getBytes(StandardCharsets.UTF_8);
        String authToken = new String(Base64.getEncoder().encode(bytes), StandardCharsets.UTF_8);
        headers.add("Authorization", "Basic " + authToken);
        try {
            HttpEntity<String> registerEntity = new HttpEntity<>(mapper.writeValueAsString(registerDTO), headers);
            restTemplate.postForEntity(registerUri, registerEntity, void.class);
        } catch (IOException e) {
            logger.error("注册定时任务错误", e);
            throw new TaskClientException("注册定时任务错误");
        }

    }
}
