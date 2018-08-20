package com.task.client.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.appinfo.ApplicationInfoManager;
import com.task.client.register.SecheduledTaskRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Administrator on 2018/8/20 0020.
 */
@Configuration
@ComponentScan(basePackages = "com.task.client")
@EnableConfigurationProperties(TaskClientProperties.class)
public class TaskClientAutoConfiguration {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private TaskClientProperties properties;
    @Autowired
    private ApplicationInfoManager infoManager;
    @Autowired
    private ApplicationContext context;
    @Autowired
    private LoadBalancerClient loadBalancerClient;
    @Autowired
    private ObjectMapper mapper;

    @Bean
    public SecheduledTaskRegister secheduledTaskRegister() {
        SecheduledTaskRegister register = new SecheduledTaskRegister();
        register.setProperties(properties);
        register.setInfoManager(infoManager);
        register.setContext(context);
        register.setLoadBalancerClient(loadBalancerClient);
        register.setMapper(mapper);
        try {
            register.register();
        } catch (Exception e) {
            logger.error("", e);
        }
        return register;
    }

}
