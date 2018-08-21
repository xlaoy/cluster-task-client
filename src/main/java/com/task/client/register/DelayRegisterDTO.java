package com.task.client.register;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2018/8/19 0019.
 */
public class DelayRegisterDTO {

    //注册服务名称
    private String registerServiceName;
    //注册ip端口
    private String registerHostAndPort;
    //
    private List<DelayInfo> delayInfoList;

    public String getRegisterServiceName() {
        return registerServiceName;
    }

    public void setRegisterServiceName(String registerServiceName) {
        this.registerServiceName = registerServiceName;
    }

    public String getRegisterHostAndPort() {
        return registerHostAndPort;
    }

    public void setRegisterHostAndPort(String registerHostAndPort) {
        this.registerHostAndPort = registerHostAndPort;
    }

    public List<DelayInfo> getDelayInfoList() {
        return delayInfoList;
    }

    public void setDelayInfoList(List<DelayInfo> delayInfoList) {
        this.delayInfoList = delayInfoList;
    }

    public static class DelayInfo {
        //执行服务名称
        private String executeServiceName;
        //业务名称
        private String bizName;
        //业务参数
        private String bizParameters;
        //执行时间
        private Date executeTime;

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

}
