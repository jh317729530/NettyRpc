package com.gunn;

import com.gunn.api.HelloService;
import com.gunn.impl.HelloServiceImpl;
import com.gunn.remoting.proxy.InvokerProxy;
import com.gunn.service.Service;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Proxy;

public class InvokerProxyTest {


    @Before
    public void before() {
        Service<HelloServiceImpl> helloServiceService = new Service<>(HelloService.class);
        helloServiceService.setRef(new HelloServiceImpl());
        helloServiceService.export();
    }

    @Test
    public void testInvoke() {
        InvokerProxy invokerProxy = new InvokerProxy();
        HelloService helloService = (HelloService) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{HelloService.class}, invokerProxy);
        String result = helloService.sayHello("myname");
        System.out.println(result);
        assert null != result && "myname, hello".equals(result);
    }
}
