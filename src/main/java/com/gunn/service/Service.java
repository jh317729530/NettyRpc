package com.gunn.service;

import lombok.Data;

/**
 * 封装服务信息
 */
@Data
public class Service<T> {

    /**
     * 服务的接口类型
     */
    private Class interfaceClass;

    /**
     * 服务提供者对象
     */
    private T ref;

    /**
     * 服务名称，默认根据接口名
     */
    private String name;

    public Service(Class interfaceClass) {
        this.interfaceClass = interfaceClass;
        this.name = interfaceClass.getName();
    }

    public Service(String name, Class interfaceClass) {
        this.name = name;
        this.interfaceClass = interfaceClass;
    }

    /**
     * 暴露服务
     */
    public void export() {
        // 暴露服务的几个过程
        // 1. 存储服务提供的对象，并能够方便查找，先不考虑服务依赖的问题
        ServiceRepository.registerService(this.name, this);
        // 2. 开启服务端监听，如果有多个服务提供者时，不必重复开启服务端，只需执行1和2

    }
}
