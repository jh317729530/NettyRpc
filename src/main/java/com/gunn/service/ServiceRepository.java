package com.gunn.service;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务提供者仓库
 */
public class ServiceRepository {

    private final static ConcurrentHashMap<String, Service> SERVICES = new ConcurrentHashMap<>();

    /**
     * 注册服务
     */
    public static void registerService(String name, Service service) {
        SERVICES.put(name, service);
    }
}
