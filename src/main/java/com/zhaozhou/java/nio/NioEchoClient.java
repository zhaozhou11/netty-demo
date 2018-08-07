package com.zhaozhou.java.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;

/**
 * Created by zhaozhou on 2018/8/6.
 */
public class NioEchoClient {
    private static final int port = 9999;

    private SocketChannel client = null;

    public SocketChannel getClient() {
        return client;
    }

    public void start(int port){
        try {
            client = SocketChannel.open();
            Selector selector = Selector.open();

            boolean success = client.connect(new InetSocketAddress(port));
            client.configureBlocking(false);
            if(success){
                client.register(selector, SelectionKey.OP_CONNECT | SelectionKey.OP_READ);

                while (true){
                    selector.select();
                    Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
                    while (keyIterator.hasNext()){
                        SelectionKey key = keyIterator.next();
                        keyIterator.remove();

                        if(key.isConnectable()){
                            key.interestOps(SelectionKey.OP_READ);
                        }

                        if(key.isReadable()){
                            ByteBuffer buffer = ByteBuffer.allocate(1024);
                            int cnt = ((SocketChannel)key.channel()).read(buffer);
                            if(cnt <= -1){
                                client.close();
                                break;
                            }else if(cnt > 0){
                                System.out.println("read count=" + cnt);
                                System.out.println("read data=" + new String(buffer.array()));
                            }
                        }
                    }
                }

            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public static void main(String[] args){

        final NioEchoClient client = new NioEchoClient();

        Thread thread = new Thread(new Runnable() {
            public void run() {
                Scanner scanner = new Scanner(System.in);
                while (scanner.hasNextLine()){
                    String line = scanner.nextLine();
                    if(line.equals("zhaozhou")){
                        break;
                    }

                    try {
                        if(client.getClient().isConnected()){
                            ByteBuffer buffer = ByteBuffer.allocate(line.getBytes().length + 2);
                            buffer.put(line.getBytes());
                            buffer.flip();
                            client.getClient().write(buffer);
                        }else{
                            System.out.println("还未连接上服务器！");
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });


        thread.start();
        client.start(9999);

    }
}
