package com.task.client.execute;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.client.SecheduledTask;
import com.task.client.config.TaskClientProperties;
import com.task.client.exception.TaskClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2018/8/20 0020.
 */
public class SecheduledExecuter implements Runnable {

    private Logger logger = LoggerFactory.getLogger(SecheduledExecuter.class);

    private SecheduledRequestDTO requestDTO;
    private ApplicationContext context;
    private RestTemplate restTemplate;
    private LoadBalancerClient loadBalancerClient;
    private TaskClientProperties properties;
    private ObjectMapper mapper;

    private static final String TASK_SERVER_SECHEDULED_FEEDBACK_URL = "/task_server/secheduled_feedback/";


    public void setMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public void setLoadBalancerClient(LoadBalancerClient loadBalancerClient) {
        this.loadBalancerClient = loadBalancerClient;
    }

    public void setProperties(TaskClientProperties properties) {
        this.properties = properties;
    }

    public void setRequestDTO(SecheduledRequestDTO requestDTO) {
        this.requestDTO = requestDTO;
    }

    public void setContext(ApplicationContext context) {
        this.context = context;
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void run() {
        SecheduledResponseDTO responseDTO = new SecheduledResponseDTO();
        SecheduledTask task = this.getSecheduledTask(requestDTO.getClassName());
        if(task == null) {
            responseDTO.setStatus(SecheduledResponseDTO.EXECUTE_FAILURE);
            responseDTO.setResult(requestDTO.getClassName() + "在spring容器里面没找到bean");
            logger.error(requestDTO.getClassName() + "在spring容器里面没找到bean");
        } else {
            try {
                task.execute(requestDTO.getParameters());
                responseDTO.setStatus(SecheduledResponseDTO.EXECUTE_SUCCESS);
                responseDTO.setResult("success");
            } catch (Exception e) {
                logger.error("定时任务执行异常，logId=" + requestDTO.getLogId());
                responseDTO.setStatus(SecheduledResponseDTO.EXECUTE_FAILURE);
                responseDTO.setResult(e.getMessage());
            }
        }
        if(!StringUtils.isEmpty(requestDTO.getLogId())) {
            this.sendExecuteResult(requestDTO.getLogId(), responseDTO);
        }
    }

    private SecheduledTask getSecheduledTask(String className) {
        Map<String, SecheduledTask> beanMap = context.getBeansOfType(SecheduledTask.class);
        if(!CollectionUtils.isEmpty(beanMap)) {
            Set<Map.Entry<String, SecheduledTask>> set = beanMap.entrySet();
            for(Map.Entry<String, SecheduledTask> entry : set) {
                if(className.equals(entry.getValue().getClass().getName())) {
                    return entry.getValue();
                }
            }
        }
        return null;
    }

    private void sendExecuteResult(String logId, SecheduledResponseDTO responseDTO) {
        ServiceInstance instance = this.loadBalancerClient.choose(properties.getTaskServerName().toUpperCase());
        if(instance == null) {
            throw new TaskClientException("反馈定时任务没找到定时任务server，taskServerName=" + properties.getTaskServerName());
        }
        URI registerUri = URI.create(String.format("http://%s:%s" + TASK_SERVER_SECHEDULED_FEEDBACK_URL + logId, instance.getHost(), instance.getPort()));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        byte[] bytes = (properties.getUsername() + ":" + properties.getPassword()).getBytes(StandardCharsets.UTF_8);
        String authToken = new String(Base64.getEncoder().encode(bytes), StandardCharsets.UTF_8);
        headers.add("Authorization", "Basic " + authToken);
        try {
            HttpEntity<String> registerEntity = new HttpEntity<>(mapper.writeValueAsString(responseDTO), headers);
            restTemplate.postForEntity(registerUri, registerEntity, String.class);
        } catch (IOException e) {
            logger.error("反馈定时任务执行结果错误", e);
        }
    }

}
