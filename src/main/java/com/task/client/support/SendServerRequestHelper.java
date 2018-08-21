package com.task.client.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.client.config.TaskClientProperties;
import com.task.client.exception.TaskClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Created by Administrator on 2018/8/21 0021.
 */
public class SendServerRequestHelper {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private TaskClientProperties properties;
    private LoadBalancerClient loadBalancerClient;
    private ObjectMapper mapper;
    private RestTemplate restTemplate;


    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void setProperties(TaskClientProperties properties) {
        this.properties = properties;
    }

    public TaskClientProperties getProperties() {
        return properties;
    }

    public void setLoadBalancerClient(LoadBalancerClient loadBalancerClient) {
        this.loadBalancerClient = loadBalancerClient;
    }

    public void setMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public void sendRequest(String url, Object object) {
        this.sendRequest(url, object, String.class);
    }

    public <T> T sendRequest(String url, Object object, Class<T> clazz) {
        ServiceInstance instance = this.loadBalancerClient.choose(properties.getTaskServerName().toUpperCase());
        if(instance == null) {
            throw new TaskClientException("没找到定时任务server，taskServerName=" + properties.getTaskServerName());
        }
        URI uri = URI.create("http://" + instance.getHost() + ":" + instance.getPort() + url);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        byte[] bytes = (properties.getUsername() + ":" + properties.getPassword()).getBytes(StandardCharsets.UTF_8);
        String authToken = new String(Base64.getEncoder().encode(bytes), StandardCharsets.UTF_8);
        headers.add("Authorization", "Basic " + authToken);
        try {
            HttpEntity<String> entity = new HttpEntity<>(mapper.writeValueAsString(object), headers);
            return restTemplate.postForEntity(uri, entity, clazz).getBody();
        } catch (IOException e) {
            logger.error("请求定时任务服务器异常", e);
            throw new TaskClientException("请求定时任务服务器异常");
        }
    }

}
