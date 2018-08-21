package com.task.client.execute;


/**
 * Created by Administrator on 2018/8/19 0019.
 */
public class DelayRequestDTO {

    //
    private String logId;
    //业务名称
    private String bizName;
    //业务参数
    private String bizParameters;

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
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
}
