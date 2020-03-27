package com.gunn;

import com.gunn.api.HelloService;
import com.gunn.impl.HelloServiceImpl;
import com.gunn.remoting.NettyServer;
import com.gunn.service.Service;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ServiceTest {

    @Test
    public void testExport() throws IOException, InterruptedException {
        Service<HelloServiceImpl> helloServiceService = new Service<>(HelloService.class);
        helloServiceService.setRef(new HelloServiceImpl());
        helloServiceService.export();
        NettyServer instance = NettyServer.getInstance();

        TimeUnit.SECONDS.sleep(2);
        System.in.read();
    }
}
