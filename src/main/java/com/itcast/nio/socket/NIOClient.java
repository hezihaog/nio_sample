package com.itcast.nio.socket;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * NIO网络通信客户端
 */
public class NIOClient {
    public static void main(String[] args) throws Exception {
        //1.得到一个网络通道
        SocketChannel channel = SocketChannel.open();
        //2.设置阻塞方式，false为非阻塞
        channel.configureBlocking(false);
        //3.提供服务器端的ip地址、端口
        InetSocketAddress socketAddress = new InetSocketAddress("127.0.0.1", 9999);
        //4.连接服务器端，如果连接不上，不断重试
        if (!channel.connect(socketAddress)) {
            //重试次数
            int retryCount = 0;
            //nio的非阻塞，让重连的过程中，while循环还能同时进行
            while (!channel.finishConnect()) {
                retryCount++;
                System.out.println("Client：正在连接... count => " + retryCount);
            }
        }
        //5.得到一个缓冲区，并存入数据
        String msg = "hello server！";
        //建立写数据的缓冲区
        ByteBuffer writeBuffer = ByteBuffer.wrap(msg.getBytes());
        //6.发送数据
        channel.write(writeBuffer);

        //一直循环保证程序不退出
        System.in.read();
    }
}