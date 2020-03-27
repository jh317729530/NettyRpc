package com.gunn.impl;

import com.gunn.api.HelloService;

public class HelloServiceImpl implements HelloService {


    @Override
    public String sayHello(String name) {
        return name + ", hello";
    }

}
