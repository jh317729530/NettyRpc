package com.gunn.remoting;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.CountDownLatch;

public class ServiceInvokeResultHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Response response = JSON.parseObject(msg.toString(),Response.class);
        NettyClient client = NettyClient.getInstance();
        client.getResponses().put(response.getRequestId(), response);
        CountDownLatch countDownLatch = client.getRequestWaiting().get(response.getRequestId());
        countDownLatch.countDown();
    }
}
