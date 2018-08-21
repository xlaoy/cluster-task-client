package com.task.client.execute;


/**
 * Created by Administrator on 2018/8/19 0019.
 */

public class TaskResultDTO {

    public static final String EXECUTE_SUCCESS = "execute_success";
    public static final String EXECUTE_FAILURE = "execute_failure";

    //结果
    private String status;
    //
    private String result;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
