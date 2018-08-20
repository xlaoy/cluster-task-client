package com.task.client.execute;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.client.config.TaskClientProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Created by Administrator on 2018/8/19 0019.
 */
@Component
public class TaskExecuteService {

    @Autowired
    private ThreadPoolTaskExecutor tpTaskExecutor;
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

    /**
     * 执行定时任务
     * @param requestDTO
     */
    public void executeSecheduled(SecheduledRequestDTO requestDTO) {
        SecheduledExecuter secheduledExecuter = new SecheduledExecuter();
        secheduledExecuter.setRequestDTO(requestDTO);
        secheduledExecuter.setContext(context);
        secheduledExecuter.setRestTemplate(restTemplate);
        secheduledExecuter.setProperties(properties);
        secheduledExecuter.setLoadBalancerClient(loadBalancerClient);
        secheduledExecuter.setMapper(mapper);
        tpTaskExecutor.execute(secheduledExecuter);
    }
}
