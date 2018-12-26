package com.zhaozhou.netty.demo.socket;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;


/**
 * Created by zhaozhou on 2018/12/26.
 */
public class SocketEchoClient {
    private static String LOCAL_HOST = "127.0.0.1";
    private static int SERVER_PORT = 9999;

    public static void main(String[] args){
        SocketEchoClient.startClient(SERVER_PORT);
    }

    private static void startClient(int serverPort){
        NioEventLoopGroup group = new NioEventLoopGroup(1);

        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                            nioSocketChannel.pipeline().addLast("lineDecodeHandler", new LineBasedFrameDecoder(1024, false, false))
                                    .addLast("stringDecoder",new StringDecoder())
                                    .addLast("echoClientHandler", new SocketEchoClientHandler());
                        }
                    });
            ChannelFuture cf = b.connect(LOCAL_HOST, SERVER_PORT).sync();
            System.out.println("client connect to server success! host:" + LOCAL_HOST + ":" + SERVER_PORT);
            cf.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            group.shutdownGracefully();
        }
    }

}
