package com.zhaozhou.netty.demo.socket;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Scanner;

/**
 * Created by zhaozhou on 2018/12/26.
 */
@ChannelHandler.Sharable
public class SocketEchoClientHandler extends ChannelInboundHandlerAdapter{
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof String){
            String str = (String)msg;
            System.out.println("recv form server:" + str);
            return;
        }
    }


    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        System.out.println("client is active,now you can input!");
        try {
            new Thread(new Runnable() {
                public void run() {
                    Scanner scanner = new Scanner(System.in);
                    while (scanner.hasNextLine()){
                        String line = scanner.nextLine();
                        if(line.startsWith("##stop")){
                            System.out.println("close client!");
                            ctx.close();
                            break;
                        }
                        line += System.getProperty("line.separator");
                        System.out.println(line);
                        ctx.writeAndFlush(Unpooled.copiedBuffer(line.getBytes()));
                    }
                }
            }).start();
        }catch (Exception e){
            e.printStackTrace();
            ctx.close();
        }
    }
}
