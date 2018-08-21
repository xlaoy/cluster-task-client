package com.task.client.register;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.InstanceInfo;
import com.task.client.DelayTask;
import com.task.client.exception.TaskClientException;
import com.task.client.support.SendServerRequestHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Created by Administrator on 2018/8/21 0021.
 */
public class DelayTaskRegister {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private SendServerRequestHelper requestHelper;
    private ApplicationInfoManager infoManager;
    private ApplicationContext context;

    private static final String DELAY_TASK_REGISTER_URL = "/task_server/register_delay_task";
    private static final String DELAY_TASK_CANCEL_URL = "/task_server/cancel_delay_task";

    public DelayTaskRegister(SendServerRequestHelper requestHelper, ApplicationInfoManager infoManager, ApplicationContext context) {
        this.requestHelper = requestHelper;
        this.infoManager = infoManager;
        this.context = context;
    }

    /**
     * 注册延迟任务
     * @param data
     */
    public String register(DelayTaskData data) {
        List<String> idlist = this.register(Arrays.asList(data));
        return idlist.get(0);
    }

    /**
     * 注册延迟任务
     * @param dataList
     */
    public List<String> register(List<DelayTaskData> dataList) {
        if(CollectionUtils.isEmpty(dataList)) {
            throw new TaskClientException("注册延迟任务列表不能为空");
        }
        DelayRegisterDTO dto = new DelayRegisterDTO();
        InstanceInfo instanceInfo = infoManager.getInfo();
        dto.setRegisterServiceName(instanceInfo.getAppName());
        dto.setRegisterHostAndPort(instanceInfo.getIPAddr() + ":" + instanceInfo.getPort());
        List<DelayRegisterDTO.DelayInfo> delayInfoList = new ArrayList<>();
        for(DelayTaskData data : dataList) {
            if(StringUtils.isEmpty(data.getBizName())) {
                throw new TaskClientException("bizName不能为空");
            }
            if(StringUtils.isEmpty(data.getBizParameters())) {
                throw new TaskClientException("bizParameters不能为空");
            }
            if(data.getExecuteTime() == null) {
                throw new TaskClientException("executeTime不能为空");
            }
            DelayRegisterDTO.DelayInfo delayInfo = new DelayRegisterDTO.DelayInfo();
            delayInfo.setBizName(data.getBizName());
            delayInfo.setBizParameters(data.getBizParameters());
            delayInfo.setExecuteTime(data.getExecuteTime());
            if(StringUtils.isEmpty(data.getExecuteServiceName())) {
                delayInfo.setExecuteServiceName(dto.getRegisterServiceName());
            } else {
                delayInfo.setExecuteServiceName(data.getExecuteServiceName());
            }
            delayInfoList.add(delayInfo);
        }
        dto.setDelayInfoList(delayInfoList);
        DelayRegisterResultDTO resultDTO = requestHelper.sendRequest(DELAY_TASK_REGISTER_URL, dto, DelayRegisterResultDTO.class);
        if(!DelayRegisterResultDTO.SUCCESS.equals(resultDTO.getCode())) {
            throw new TaskClientException(resultDTO.getMessage());
        }
        return resultDTO.getTaskIdList();
    }

    /**
     * 取消延迟任务
     * @param taskId
     */
    public void cancel(String taskId) {
        this.cancel(Arrays.asList(taskId));
    }

    /**
     * 取消延迟任务
     * @param taskIdList
     */
    public void cancel(List<String> taskIdList) {
        if(CollectionUtils.isEmpty(taskIdList)) {
            throw new TaskClientException("取消延迟任务taskIdList不能为空");
        }
        requestHelper.sendRequest(DELAY_TASK_CANCEL_URL, taskIdList);
    }

    /**
     * 检查
     */
    public void check() {
        List<String> bizNameList = new ArrayList<>();
        Map<String, DelayTask> beanMap = context.getBeansOfType(DelayTask.class);
        if(!CollectionUtils.isEmpty(beanMap)) {
            beanMap.forEach((key, value) -> {
                if(bizNameList.contains(value.bizName())) {
                    throw new TaskClientException("延迟任务执行器的bizName=" + value.bizName() + "出现多个，请检查");
                } else {
                    bizNameList.add(value.bizName());
                }
            });
        }
    }
}
