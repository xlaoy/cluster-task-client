package com.task.client.execute;


/**
 * Created by Administrator on 2018/8/19 0019.
 */

public class SecheduledRequestDTO {

    //类名
    private String className;
    //
    private String logId;
    //参数
    private String parameters;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }
}
