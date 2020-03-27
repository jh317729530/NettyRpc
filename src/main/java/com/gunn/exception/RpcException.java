package com.gunn.exception;

public class RpcException extends RuntimeException {

    private String errorMsg;

    public RpcException(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
