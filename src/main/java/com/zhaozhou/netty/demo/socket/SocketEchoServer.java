package com.zhaozhou.netty.demo.socket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * Created by zhaozhou on 2018/12/26.
 */
public class SocketEchoServer {
    private static  int PORT = 9999;


    public static void main(String[] args){
        SocketEchoServer.startServer(PORT);
    }





    private static void startServer(int port){
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap sb = new ServerBootstrap();
            sb.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 256)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast("lineDecodeHandler", new LineBasedFrameDecoder(1024, false, false))
                                    .addLast("stringDecoder", new StringDecoder())
                                    .addLast("heartBeatHandler", new IdleStateHandler(0,0, 30))
                                    .addLast("echoHandler", new SocketEchoServerHandler());
                        }
                    });
            ChannelFuture cf = sb.bind(PORT).sync();
            System.out.println("---- server bind success on port:" + PORT + " ----");
            cf.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
