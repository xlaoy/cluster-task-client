package com.task.client.execute;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.client.SecheduledTask;
import com.task.client.config.TaskClientProperties;
import com.task.client.exception.TaskClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
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
 * Created by Administrator on 2018/8/19 0019.
 */
@Component
public class TaskExecuteService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ApplicationContext context;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private LoadBalancerClient loadBalancerClient;
    @Autowired
    private TaskClientProperties properties;
    @Autowired
    private ObjectMapper mapper;

    private static final String TASK_SERVER_SECHEDULED_FEEDBACK_URL = "/task_server/secheduled_feedback/";

    /**
     * 执行定时任务
     * @param requestDTO
     */
    @Async
    public void executeSecheduled(SecheduledRequestDTO requestDTO) {
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
            throw new TaskClientException("反馈任务没找到定时任务server，taskServerName=" + properties.getTaskServerName());
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
            logger.error("反馈任务执行结果错误", e);
        }
    }
}
