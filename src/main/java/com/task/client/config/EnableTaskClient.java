package com.task.client.config;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Created by Administrator on 2018/8/20 0020.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({TaskClientAutoConfiguration.class})
public @interface EnableTaskClient {
}
