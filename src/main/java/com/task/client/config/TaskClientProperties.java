package com.task.client.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by Administrator on 2018/8/19 0019.
 */
@ConfigurationProperties(prefix = "task.client")
public class TaskClientProperties {

    private boolean register = true;

    private String taskServerName;

    private String username;

    private String password;

    public boolean isRegister() {
        return register;
    }

    public void setRegister(boolean register) {
        this.register = register;
    }

    public String getTaskServerName() {
        return taskServerName;
    }

    public void setTaskServerName(String taskServerName) {
        this.taskServerName = taskServerName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
