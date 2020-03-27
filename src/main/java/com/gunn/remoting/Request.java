package com.gunn.remoting;

import lombok.Data;

/**
 * 请求的格式
 */
@Data
public class Request {

    /**
     * 请求的服务名称
     */
    private String serviceName;

    /**
     * 请求的方法
     */
    private String methodName;

    /**
     * 请求的参数
     */
    private Object[] params;

}
