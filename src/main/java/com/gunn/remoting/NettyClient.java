package com.gunn.remoting;

import com.gunn.util.UnsafeUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Data;
import sun.misc.Unsafe;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

@Data
public class NettyClient {

    private static NettyClient CLIENT;

    private Bootstrap bootstrap;

    private Channel channel;

    private ChannelFuture connectFuture = null;

    private volatile int connectStatus = NOT_CONNECT;

    private static final int NOT_CONNECT = -1;

    private static final int CONNECTING = 0;

    private static final int CONNECTED = 1;

    private static final int CONNECT_FAIL = 2;

    private static long bindStatusOffset = UnsafeUtils.calcFieldOffset(NettyClient.class,"connectStatus");

    private static final Unsafe unsafe = UnsafeUtils.getUnsafe();

    private ConcurrentHashMap<Integer, CountDownLatch> requestWaiting = new ConcurrentHashMap<>();

    private ConcurrentHashMap<Integer, Response> responses = new ConcurrentHashMap<>();

    public static NettyClient getInstance() {
        if (null == CLIENT) {
            synchronized (NettyServer.class) {
                if (null == CLIENT) {
                    CLIENT = new NettyClient();
                }
            }
        }
        return CLIENT;
    }

    private NettyClient() {
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        this.bootstrap = new Bootstrap();
        bootstrap
                .group(workerGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        nioSocketChannel.pipeline().addLast(new JSONEncoder());
                        nioSocketChannel.pipeline().addLast(new JSONDecoder());
                        nioSocketChannel.pipeline().addLast(new ServiceInvokeResultHandler());
                    }
                });


    }

    public boolean isConnected() {
        if (CONNECTING == connectStatus) {
            try {
                connectFuture.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return connectFuture.isSuccess();
        }

        if (NOT_CONNECT == connectStatus || CONNECT_FAIL == connectStatus) {
            return false;
        }
        return true;
    }

    /**
     * 开始连接服务端
     *
     * @return
     */
    public void connect() {
        if (!isConnected() && unsafe.compareAndSwapInt(this,bindStatusOffset,NOT_CONNECT,CONNECTING)) {
            connectFuture = bootstrap.connect("localhost",18080).addListener(future -> {
                if (future.isSuccess()) {
                    unsafe.compareAndSwapInt(this, bindStatusOffset, CONNECTING, CONNECTED);
                    this.channel = connectFuture.channel();
                    System.out.println("Client connect success!");
                } else {
                    unsafe.compareAndSwapInt(this, bindStatusOffset, CONNECTING, CONNECT_FAIL);
                    System.out.println("Client connect failed");
                }
            });
        }
    }

    public Response sendRequest(Request request) {
        if (!isConnected()) {
            connect();

            try {
                connectFuture.sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
                Response response = new Response();
                response.setErrorMsg("连接到服务器失败！");
                response.setStatus(Response.ERROR);
                return response;
            }
        }

        System.out.println(channel);
        if (null != channel && channel.isActive()) {
            CountDownLatch countDownLatch = new CountDownLatch(1);
            requestWaiting.put(request.getRequestId(), countDownLatch);
            channel.writeAndFlush(request);
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
                Response response = new Response();
                response.setErrorMsg("请求超时！");
                response.setStatus(Response.ERROR);
                return response;
            }finally {
                requestWaiting.remove(request.getRequestId());
            }
            return responses.get(request.getRequestId());
        } else {
            Response response = new Response();
            response.setErrorMsg("未连接到服务端");
            response.setStatus(Response.ERROR);
            return response;
        }
    }
}
