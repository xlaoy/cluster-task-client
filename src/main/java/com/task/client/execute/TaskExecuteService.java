package com.task.client.execute;

import com.task.client.SecheduledTask;
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
public class TaskExecuteService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ApplicationContext context;
    @Autowired
    SendServerRequestHelper requestHelper;

    private static final String TASK_SERVER_FEEDBACK_URL = "/task_server/task_feedback/";

    /**
     * 执行定时任务
     * @param requestDTO
     */
    @Async
    public void executeSecheduled(SecheduledRequestDTO requestDTO) {
        SecheduledResultDTO resultDTO = new SecheduledResultDTO();
        SecheduledTask task = this.getSecheduledTask(requestDTO.getClassName());
        if(task == null) {
            resultDTO.setStatus(SecheduledResultDTO.EXECUTE_FAILURE);
            resultDTO.setResult(requestDTO.getClassName() + "在spring容器里面没找到bean");
            logger.error(requestDTO.getClassName() + "在spring容器里面没找到bean");
        } else {
            try {
                task.execute(requestDTO.getParameters());
                resultDTO.setStatus(SecheduledResultDTO.EXECUTE_SUCCESS);
                resultDTO.setResult("success");
            } catch (Exception e) {
                logger.error("定时任务执行异常，logId=" + requestDTO.getLogId());
                resultDTO.setStatus(SecheduledResultDTO.EXECUTE_FAILURE);
                resultDTO.setResult(e.getMessage());
            }
        }
        if(!StringUtils.isEmpty(requestDTO.getLogId())) {
            try {
                requestHelper.sendRequest(TASK_SERVER_FEEDBACK_URL + requestDTO.getLogId(), resultDTO);
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

}
