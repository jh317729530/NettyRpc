package com.gunn.remoting;

import com.alibaba.fastjson.JSON;
import com.gunn.exception.RpcException;
import com.gunn.service.Service;
import com.gunn.service.ServiceRepository;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@ChannelHandler.Sharable
public class ServiceInvokeHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Request request = JSON.parseObject(msg.toString(), Request.class);

        System.out.println("RPC客户端请求接口:" + request.getServiceName() + "   方法名:" + request.getMethodName());
        Response response = new Response();
        response.setRequestId(request.getRequestId());
        try {
            Object result = this.handleRequest(request);
            response.setResult(result);
        } catch (Throwable e) {
            e.printStackTrace();
            response.setStatus(Response.ERROR);
            response.setErrorMsg(e.toString());
            System.out.println("请求发生异常");
        }
        ctx.writeAndFlush(response);
    }

    private Object handleRequest(Request request) {
        Service service = ServiceRepository.getServiceByName(request.getServiceName());
        if (null == service) {
            throw new RpcException("找不到服务！");
        }

        Method method;
        try {
            method = service.getInterfaceClass().getMethod(request.getMethodName(), request.getParamClassTypes());
        } catch (NoSuchMethodException e) {
            throw new RpcException("找不到方法！");
        }

        Object ref = service.getRef();
        try {
           return method.invoke(ref, request.getParams());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RpcException("服务方法定义无权限");
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            throw new RpcException("服务调用内部发生错误");
        }
    }
}
