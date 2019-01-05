package com.zhaozhou.netty.demo.ssl.twoway;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class SslTowWayClientHandler extends SimpleChannelInboundHandler<String> {

	 @Override
    public void messageReceived(ChannelHandlerContext ctx, String msg) throws Exception {
        System.err.print("recv:" + msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    	cause.printStackTrace();
        ctx.close();
    }

}
