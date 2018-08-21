package com.task.client.register;

import java.util.Date;

/**
 * Created by Administrator on 2018/8/21 0021.
 */
public class DelayTaskData {

    //执行服务名称
    private String executeServiceName;
    //业务名称
    private String bizName;
    //业务参数
    private String bizParameters;
    //执行时间
    private Date executeTime;

    public static DelayTaskData bizName(String bizName) {
        DelayTaskData dto = new DelayTaskData();
        dto.setBizName(bizName);
        return dto;
    }

    public DelayTaskData bizParameters(String bizParameters) {
        this.setBizParameters(bizParameters);
        return this;
    }

    public DelayTaskData executeTime(Date executeTime) {
        this.setExecuteTime(executeTime);
        return this;
    }

    public DelayTaskData executeServiceName(String executeServiceName) {
        this.setExecuteServiceName(executeServiceName);
        return this;
    }

    public String getExecuteServiceName() {
        return executeServiceName;
    }

    public void setExecuteServiceName(String executeServiceName) {
        this.executeServiceName = executeServiceName;
    }

    public String getBizName() {
        return bizName;
    }

    public void setBizName(String bizName) {
        this.bizName = bizName;
    }

    public String getBizParameters() {
        return bizParameters;
    }

    public void setBizParameters(String bizParameters) {
        this.bizParameters = bizParameters;
    }

    public Date getExecuteTime() {
        return executeTime;
    }

    public void setExecuteTime(Date executeTime) {
        this.executeTime = executeTime;
    }
}
