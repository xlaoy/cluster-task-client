package com.task.client.execute;

import com.task.client.DelayTask;
import com.task.client.SecheduledTask;
import com.task.client.config.ServerURL;
import com.task.client.support.SendServerRequestHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2018/8/19 0019.
 */
@Component
public class ClientTaskExecuteService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ApplicationContext context;
    @Autowired
    SendServerRequestHelper requestHelper;



    /**
     * 执行定时任务
     * @param requestDTO
     */
    @Async
    public void executeSecheduled(SecheduledRequestDTO requestDTO) {
        TaskResultDTO resultDTO = new TaskResultDTO();
        SecheduledTask task = this.getSecheduledTask(requestDTO.getClassName());
        if(task == null) {
            resultDTO.setStatus(TaskResultDTO.EXECUTE_FAILURE);
            resultDTO.setResult(requestDTO.getClassName() + "在spring容器里面没找到bean");
            logger.error(requestDTO.getClassName() + "在spring容器里面没找到bean");
        } else {
            try {
                task.execute(requestDTO.getParameters());
                resultDTO.setStatus(TaskResultDTO.EXECUTE_SUCCESS);
                resultDTO.setResult("success");
            } catch (Exception e) {
                logger.error("定时任务执行异常，logId=" + requestDTO.getLogId());
                resultDTO.setStatus(TaskResultDTO.EXECUTE_FAILURE);
                resultDTO.setResult(e.getMessage());
            }
        }
        if(!StringUtils.isEmpty(requestDTO.getLogId())) {
            try {
                requestHelper.sendRequest(ServerURL.TASK_SERVER_FEEDBACK_URL + requestDTO.getLogId(), resultDTO);
            } catch (Exception e) {
                logger.error("定时任务发送反馈请求异常，logId=" + requestDTO.getLogId());
            }
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


    /**
     * 执行延迟任务
     * @param requestDTO
     */
    @Async
    void executeDelay(DelayRequestDTO requestDTO) {
        TaskResultDTO resultDTO = new TaskResultDTO();
        DelayTask task = this.getDelayTask(requestDTO.getBizName());
        if(task == null) {
            resultDTO.setStatus(TaskResultDTO.EXECUTE_FAILURE);
            resultDTO.setResult(requestDTO.getBizName() + "在spring容器里面没找到bean");
            logger.error(requestDTO.getBizName() + "在spring容器里面没找到bean");
        } else {
            try {
                task.execute(requestDTO.getBizName());
                resultDTO.setStatus(TaskResultDTO.EXECUTE_SUCCESS);
                resultDTO.setResult("success");
            } catch (Exception e) {
                logger.error("延迟任务执行异常，logId=" + requestDTO.getLogId());
                resultDTO.setStatus(TaskResultDTO.EXECUTE_FAILURE);
                resultDTO.setResult(e.getMessage());
            }
        }
        if(!StringUtils.isEmpty(requestDTO.getLogId())) {
            try {
                requestHelper.sendRequest(ServerURL.TASK_SERVER_FEEDBACK_URL + requestDTO.getLogId(), resultDTO);
            } catch (Exception e) {
                logger.error("延迟任务发送反馈请求异常，logId=" + requestDTO.getLogId());
            }
        }
    }

    private DelayTask getDelayTask(String bizName) {
        Map<String, DelayTask> beanMap = context.getBeansOfType(DelayTask.class);
        if(!CollectionUtils.isEmpty(beanMap)) {
            Set<Map.Entry<String, DelayTask>> set = beanMap.entrySet();
            for(Map.Entry<String, DelayTask> entry : set) {
                if(bizName.equals(entry.getValue().bizName())) {
                    return entry.getValue();
                }
            }
        }
        return null;
    }
}
