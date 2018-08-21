package com.task.client.register;


import java.util.List;

/**
 * Created by Administrator on 2018/8/19 0019.
 */
public class DelayRegisterResultDTO {

    public static final String SUCCESS = "success";

    private String code;

    private String message;

    private List<String> taskIdList;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getTaskIdList() {
        return taskIdList;
    }

    public void setTaskIdList(List<String> taskIdList) {
        this.taskIdList = taskIdList;
    }
}
