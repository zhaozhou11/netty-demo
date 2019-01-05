package com.zhaozhou.netty.demo.ssl;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;

/**
 * Created by zhaozhou on 2019/1/4.
 */
public class SslContextFactory {

    private static final String PROTOCOL = "TLS";

    private static SSLContext SERVER_CONTEXT;//服务器安全套接字协议

    private static SSLContext CLIENT_CONTEXT;//客户端安全套接字协议


    public static SSLContext getServerContext(String pkPath,String caPath, String passwd){
        if(SERVER_CONTEXT!=null) return SERVER_CONTEXT;
        InputStream in =null;
        InputStream tIN = null;

        try{
            //密钥管理器
            KeyManagerFactory kmf = null;
            if(pkPath!=null){
                KeyStore ks = KeyStore.getInstance("JKS");
                in = new FileInputStream(pkPath);
                ks.load(in, passwd.toCharArray());

                kmf = KeyManagerFactory.getInstance("SunX509");
                kmf.init(ks, passwd.toCharArray());
            }
            //信任库
            TrustManagerFactory tf = null;
            if (caPath != null) {
                KeyStore tks = KeyStore.getInstance("JKS");
                tIN = new FileInputStream(caPath);
                tks.load(tIN, passwd.toCharArray());
                tf = TrustManagerFactory.getInstance("SunX509");
                tf.init(tks);
            }

            SERVER_CONTEXT= SSLContext.getInstance(PROTOCOL);

            //初始化此上下文
            //参数一：认证的密钥      参数二：对等信任认证  参数三：伪随机数生成器 。 由于单向认证，服务端不用验证客户端，所以第二个参数为null
            //单向认证？无需验证客户端证书
            if(tf == null){
                SERVER_CONTEXT.init(kmf.getKeyManagers(),null, null);
            }
            //双向认证，需要验证客户端证书
            else{
                SERVER_CONTEXT.init(kmf.getKeyManagers(),tf.getTrustManagers(), null);
            }


        }catch(Exception e){
            throw new Error("Failed to initialize the server-side SSLContext", e);
        }finally{
            if(in !=null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                in = null;
            }

            if (tIN != null){
                try {
                    tIN.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                tIN = null;
            }
        }

        return SERVER_CONTEXT;
    }


    public static SSLContext getClientContext(String pkPath,String caPath, String passwd){
        if(CLIENT_CONTEXT!=null) return CLIENT_CONTEXT;

        InputStream in = null;
        InputStream tIN = null;
        try{
            KeyManagerFactory kmf = null;
            if (pkPath != null) {
                KeyStore ks = KeyStore.getInstance("JKS");
                in = new FileInputStream(pkPath);
                ks.load(in, passwd.toCharArray());
                kmf = KeyManagerFactory.getInstance("SunX509");
                kmf.init(ks, passwd.toCharArray());
            }

            TrustManagerFactory tf = null;
            if (caPath != null) {
                KeyStore tks = KeyStore.getInstance("JKS");
                tIN = new FileInputStream(caPath);
                tks.load(tIN, passwd.toCharArray());
                tf = TrustManagerFactory.getInstance("SunX509");
                tf.init(tks);
            }

            CLIENT_CONTEXT = SSLContext.getInstance(PROTOCOL);

            //初始化此上下文
            //参数一：认证的密钥      参数二：对等信任认证  参数三：伪随机数生成器 。 由于单向认证，服务端不用验证客户端，所以第二个参数为null
            //单向认证？无需验证服务端证书
            if(tf == null){
                //设置信任证书
                CLIENT_CONTEXT.init(null,tf == null ? null : tf.getTrustManagers(), null);
            }
            //双向认证，需要验证客户端证书
            else{
                CLIENT_CONTEXT.init(kmf.getKeyManagers(),tf.getTrustManagers(), null);
            }
        }catch(Exception e){
            throw new Error("Failed to initialize the client-side SSLContext");
        }finally{
            if(in !=null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                in = null;
            }

            if (tIN != null){
                try {
                    tIN.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                tIN = null;
            }
        }

        return CLIENT_CONTEXT;
    }

}
