package com.gunn.remoting;

import lombok.Data;

/**
 * 响应
 */
@Data
public class Response {

    /**
     * ok.
     */
    public static final byte OK = 20;

    public static final byte ERROR = 80;

    private int requestId;

    private byte status = OK;

    private String errorMsg;

    /**
     * 请求结果
     */
    private Object result;
}
