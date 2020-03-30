package com.gunn.remoting;

import lombok.Data;

/**
 * 请求的格式
 */
@Data
public class Request {

    /**
     * 记录每次请求的id，每个请求id唯一
     */
    private int requestId;

    /**
     * 请求的服务名称
     */
    private String serviceName;

    /**
     * 请求的方法
     */
    private String methodName;

    /**
     * 请求参数类型
     */
    private Class[] paramClassTypes;

    /**
     * 请求的参数
     */
    private Object[] params;

}
