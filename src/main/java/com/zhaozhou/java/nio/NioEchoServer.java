package com.zhaozhou.java.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * Created by zhaozhou on 2018/8/6.
 */
public class NioEchoServer {

    private static final int SELECTOR_TIMEOUT = 1000;

    public void start(int port){
        try {
            //打开服务socket
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

            //打开selector
            Selector selector = Selector.open();

            //服务监听port端口，配置为非阻塞模式
            serverSocketChannel.socket().setReuseAddress(true);
            serverSocketChannel.socket().bind(new InetSocketAddress(port));
            serverSocketChannel.configureBlocking(false);

            //将channel注册到selector中，并监听accept事件
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            long cnt = 1;
            while (true){
                if(selector.select(SELECTOR_TIMEOUT) == 0){
                    System.out.println("wait " + (cnt ++) + "s");
                    continue;
                }
                Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
                while (keyIterator.hasNext()){
                    SelectionKey key = keyIterator.next();
                    keyIterator.remove();

                    try {
                        if(key.isAcceptable()){
                            SocketChannel client = ((ServerSocketChannel)key.channel()).accept();
                            client.configureBlocking(false);

                            ByteBuffer buffer = ByteBuffer.allocate(1024);
                            client.register(selector, SelectionKey.OP_READ, buffer);
                            System.out.println("connect accept!!");
                        }

                        if(key.isReadable()){
                            SocketChannel client = (SocketChannel)key.channel();
                            ByteBuffer buffer = (ByteBuffer)key.attachment();
                            buffer.compact();
                            int count = client.read(buffer);
                            if(count <= -1){
                                client.close();
                            }else if(count > 0){
                                key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                                System.out.println("read count=" + count);
                                System.out.println("read data= " + new String(buffer.array()));
                            }
                        }


                        if(key.isValid() && key.isWritable()){
                            ByteBuffer buffer = (ByteBuffer)key.attachment();
                            buffer.flip();
                            ((SocketChannel)key.channel()).write(buffer);
                            if(!buffer.hasRemaining()){
                                key.interestOps(SelectionKey.OP_READ);
                            }
                            buffer.compact();
                        }
                    }catch (Exception e){
                        ((SocketChannel)key.channel()).close();
                        e.printStackTrace();
                    }

                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public static void main(String[] args){
        try {
            new NioEchoServer().start(9999);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
