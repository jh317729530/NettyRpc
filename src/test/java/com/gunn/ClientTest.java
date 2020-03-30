package com.gunn;

import com.gunn.api.HelloService;
import com.gunn.remoting.NettyClient;
import com.gunn.remoting.Request;
import com.gunn.remoting.Response;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ClientTest {

    @Test
    public void testConnect() throws IOException {
        NettyClient.getInstance().connect();
        System.in.read();
    }

    @Test
    public void testSendRequest() {
        NettyClient client = NettyClient.getInstance();

        Request request = new Request();
        request.setRequestId(1);
        request.setServiceName(HelloService.class.getName());
        request.setMethodName("sayHello");
        request.setParamClassTypes(new Class[]{String.class});
        request.setParams(new Object[] {"test"});
        Response response = client.sendRequest(request);
        System.out.println("finish request");
        System.out.println(response);
        assert null != response && response.getStatus() == Response.OK;
    }
}
