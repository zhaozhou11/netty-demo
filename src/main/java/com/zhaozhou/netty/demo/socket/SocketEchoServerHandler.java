package com.zhaozhou.netty.demo.socket;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.net.InetSocketAddress;

/**
 * Created by zhaozhou on 2018/12/26.
 */
@ChannelHandler.Sharable
public class SocketEchoServerHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress address = (InetSocketAddress)ctx.channel().remoteAddress();
        System.out.println("---- client active: host=" + address.getHostName() + ",port=" + address.getPort() + "----");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress address = (InetSocketAddress)ctx.channel().remoteAddress();
        System.out.println("---- client inactive: host=" + address.getHostName() + ",port=" + address.getPort() + "----");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof String){
            String str = (String)msg;
            ctx.writeAndFlush(Unpooled.copiedBuffer(str.getBytes()));
            System.out.println("recv form client:" + str);
            return;
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent) evt;
            DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
            String dt = DateTime.now().toString(format);
            switch (event.state()){
                case ALL_IDLE:
                    System.out.println(dt + ":服务端与客户端读写超时！");
                    break;
                case READER_IDLE:
                    System.out.println(dt + ":服务端读超时！");
                    break;
                case WRITER_IDLE:
                    System.out.println(dt + ":服务端写超时！");
                    break;
            }
        }
    }
}
