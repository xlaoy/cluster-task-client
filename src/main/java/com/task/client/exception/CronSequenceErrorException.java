package com.task.client.exception;

/**
 * Created by Administrator on 2018/8/23 0023.
 */
public class CronSequenceErrorException extends RuntimeException{

    public CronSequenceErrorException() {
    }

    public CronSequenceErrorException(String message) {
        super(message);
    }
}
