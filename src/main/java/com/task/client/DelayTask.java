package com.task.client;

/**
 * Created by Administrator on 2018/8/19 0019.
 */
public interface DelayTask {

    String bizName();

    void execute(String bizId);
}
