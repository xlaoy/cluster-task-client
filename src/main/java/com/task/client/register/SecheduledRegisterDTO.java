package com.task.client.register;

import java.util.List;

/**
 * Created by Administrator on 2018/8/19 0019.
 */
public class SecheduledRegisterDTO {
    //服务名称
    private String serviceName;
    //ip端口
    private String hostAndPort;
    //
    private List<SecheduledInfo> secheduledInfoList;

    public static class SecheduledInfo {
        //类名称
        private String className;
        //执行时间
        private String cron;

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public String getCron() {
            return cron;
        }

        public void setCron(String cron) {
            this.cron = cron;
        }
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getHostAndPort() {
        return hostAndPort;
    }

    public void setHostAndPort(String hostAndPort) {
        this.hostAndPort = hostAndPort;
    }

    public List<SecheduledInfo> getSecheduledInfoList() {
        return secheduledInfoList;
    }

    public void setSecheduledInfoList(List<SecheduledInfo> secheduledInfoList) {
        this.secheduledInfoList = secheduledInfoList;
    }
}
