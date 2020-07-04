package com.itcast.nio.socket;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * NIO网络通信服务器端
 */
public class NIOServer {
    public static void main(String[] args) throws Exception {
        //1.得到一个ServerSocketChannel
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //2.得到一个选择器
        Selector selector = Selector.open();
        //3.绑定端口号
        serverSocketChannel.bind(new InetSocketAddress(9999));
        //4.设置非阻塞方式
        serverSocketChannel.configureBlocking(false);
        //5.把ServerSocketChannel注册给Selector
        //SelectionKey.OP_ACCEPT事件，就是客户端连接事件
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        //6.干活
        while (true) {
            //6.1监控客户端连接，并设置2秒超时时间，就是2秒后，如果没有发现有客户端就绪，则代表服务端是空闲
            int clientChannelCount = selector.select(2000);
            //如果返回客户端通道，如果是0，就是没有客户端就绪，则继续循环监控，2秒进行一次
            if (clientChannelCount == 0) {
                System.out.println("Server：没有客户端就绪，继续等待客户端连接...");
                continue;
            }
            //6.2 有客户端就绪了，得到SelectionKey，判断通道里的事件
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectionKeys.iterator();
            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                //客户端连接请求事件
                if (key.isAcceptable()) {
                    System.out.println("Server：发生连接事件 => OP_ACCEPT");
                    //获取和客户端之间的通道
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    //配置非阻塞
                    socketChannel.configureBlocking(false);
                    //让选择器监控这个通道的读事件，第三个参数是客户端传过来的附件，发送一个缓冲区对象
                    socketChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));
                } else if (key.isReadable()) {
                    //客户端读事件，获取客户端通道
                    SocketChannel channel = (SocketChannel) key.channel();
                    //获取附件对象
                    ByteBuffer buffer = (ByteBuffer) key.attachment();
                    //将通道的数据，放到缓冲区中
                    channel.read(buffer);
                    String data = new String(buffer.array());
                    System.out.println("Server：客户端发来数据 => " + data);
                }
                //6.3 手动从集合中移除当前的Key，防止重复处理
                keyIterator.remove();
            }
        }
    }
}