package com.itcast.nio.chat;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

/**
 * 聊天服务端
 */
public class ChatServer {
    /**
     * 服务端监听的通道
     */
    private ServerSocketChannel listenerChannel;
    /**
     * 选择器
     */
    private Selector selector;

    public ChatServer() {
        setup();
    }

    private void setup() {
        try {
            //开启服务端监听通道
            listenerChannel = ServerSocketChannel.open();
            //创建选择器
            selector = Selector.open();
            //绑定端口
            listenerChannel.bind(new InetSocketAddress(ChatConfig.PORT));
            //设置为非阻塞方式
            listenerChannel.configureBlocking(false);
            //将选择器绑定到监听端口，并监听accept事件
            listenerChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("------------------ 服务端 <准备完毕> ------------------");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 开启服务
     */
    public void start() {
        try {
            while (true) {
                //不断监听，2秒一次循环
                if (selector.select(2000) == 0) {
                    //没有客户端就绪，进行下一轮监听
                    //System.out.println("Server：没有客户端就绪，继续监听");
                    continue;
                }
                //循环取出事件，并处理
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    if (key.isAcceptable()) {
                        //请求连接事件，获取客户端通道
                        SocketChannel channel = listenerChannel.accept();
                        //配置成非阻塞方式
                        channel.configureBlocking(false);
                        //让选择器监听客户端通道，并监听读取事件
                        channel.register(selector, SelectionKey.OP_READ);
                        String clientName = channel.getRemoteAddress().toString().substring(1);
                        System.out.println(clientName + " 用户，上线了...");
                    } else if (key.isReadable()) {
                        //读取事件
                        readClientMsg(key);
                    }
                    //处理完毕，移除事件，避免重复处理
                    iterator.remove();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取客户端发来的消息
     */
    private void readClientMsg(SelectionKey key) throws Exception {
        //获取客户端的通道
        SocketChannel clientChannel = (SocketChannel) key.channel();
        //创建缓冲区
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        //读取客户端发来的消息
        int count = clientChannel.read(buffer);
        //读取到了消息
        if (count > 0) {
            String msg = new String(buffer.array());
            printMsgInfo(msg);
            //转发给所有客户端
            broadCast(clientChannel, msg);
        }
    }

    /**
     * 广播消息给所有客户端
     *
     * @param except 发送消息的客户端
     * @param msg    发送的消息
     */
    public void broadCast(SocketChannel except, String msg) throws Exception {
        //System.out.println("Server：转发 “" + msg + "” 消息给其他客户端...");
        //先筛选出非当前发送的客户端
        for (SelectionKey key : selector.keys()) {
            SelectableChannel clientChannel = key.channel();
            if (clientChannel instanceof SocketChannel) {
                SocketChannel channel = (SocketChannel) clientChannel;
                if (channel != except) {
                    //发送给其他客户端
                    ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());
                    channel.write(buffer);
                }
            }
        }
    }

    /**
     * 打印消息
     */
    private void printMsgInfo(String msg) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("[" + sdf.format(new Date()) + "] -> " + msg);
    }

    public static void main(String[] args) {
        //创建服务端
        ChatServer chatServer = new ChatServer();
        chatServer.start();
    }
}