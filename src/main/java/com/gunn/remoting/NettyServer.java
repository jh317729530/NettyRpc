package com.gunn.remoting;

import com.gunn.util.UnsafeUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Data;
import sun.misc.Unsafe;

@Data
public class NettyServer {

    private ServerBootstrap serverBootstrap;

    private static final int PORT = 18080;

    private ChannelFuture bindFuture;

    // 端口绑定的状态，初始为未绑定的状态
    private volatile int bindStatus = NOT_BIND;

    private static final int NOT_BIND = -1;

    private static final int BINDING = 0;

    private static final int BINDED = 1;

    private static final int BIND_FAILED = 2;

    private static long bindStatusOffset = UnsafeUtils.calcFieldOffset(NettyServer.class,"bindStatus");

    private static final Unsafe unsafe = UnsafeUtils.getUnsafe();

    private NettyServer() {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        this.serverBootstrap = new ServerBootstrap();
        serverBootstrap
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
//                        nioSocketChannel.pipeline().addLast(new FirstServerHandler());
                    }
                });

    }

    private static NettyServer SERVER;

    public static NettyServer getInstance() {
        if (null == SERVER) {
            synchronized (NettyServer.class) {
                if (null == SERVER) {
                    SERVER = new NettyServer();
                }
            }
        }
        return SERVER;
    }

    /**
     * 开始监听
     *
     * @return
     */
    public void bind() {
        if (!isBind() && unsafe.compareAndSwapInt(this,bindStatusOffset,NOT_BIND,BINDING)) {
            bindFuture = serverBootstrap.bind(PORT).addListener(future -> {
                if (future.isSuccess()) {
                    unsafe.compareAndSwapInt(this, bindStatusOffset, BINDING, BINDED);
                    System.out.println("Server bind success!");
                } else {
                    unsafe.compareAndSwapInt(this, bindStatusOffset, BINDING, BIND_FAILED);
                    System.out.println("Server bind failed");
                }
            });
        }
    }

    /**
     * 获取服务端是否已经绑定
     *
     * @return
     */
    public boolean isBind() {
        if (BINDING == bindStatus) {
            try {
                bindFuture.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return bindFuture.isSuccess();
        }

        if (NOT_BIND == bindStatus || BIND_FAILED == bindStatus) {
            return false;
        }
        return true;
    }

}
