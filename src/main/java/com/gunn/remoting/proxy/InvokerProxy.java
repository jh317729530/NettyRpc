package com.gunn.remoting.proxy;

import com.gunn.exception.RpcException;
import com.gunn.remoting.NettyClient;
import com.gunn.remoting.Request;
import com.gunn.remoting.Response;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

public class InvokerProxy implements InvocationHandler {

    private static final NettyClient nettyClient = NettyClient.getInstance();

    private static final AtomicInteger CURRENT_REQUEST_ID = new AtomicInteger(0);

    {
        if (!nettyClient.isConnected()) {
            nettyClient.connect();
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Request request = new Request();
        request.setRequestId(CURRENT_REQUEST_ID.incrementAndGet());
        request.setMethodName(method.getName());
        request.setServiceName(method.getDeclaringClass().getName());
        request.setParamClassTypes(method.getParameterTypes());
        request.setParams(args);
        Response response = nettyClient.sendRequest(request);

        if (Response.ERROR == response.getStatus()) {
            throw new RpcException(response.getErrorMsg());
        }

        return response.getResult();
    }
}
