package com.task.client;

import java.util.Map;

/**
 * Created by Administrator on 2018/8/19 0019.
 */
public interface SecheduledTask {

    String cron();

    void execute(String parameters);
}
