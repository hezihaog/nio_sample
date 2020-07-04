package com.itcast.nio.chat;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

/**
 * 聊天客户端
 */
public class ChatClient {
    /**
     * 和服务端之间交流通道
     */
    private SocketChannel socketChannel;
    private String clientName;

    public ChatClient() throws IOException {
        this("");
    }

    /**
     * @param clientName 客户端名称
     */
    public ChatClient(String clientName) throws IOException {
        this.clientName = clientName;
        setup();
    }

    /**
     * 配置
     */
    private void setup() throws IOException {
        //创建一个通道
        socketChannel = SocketChannel.open();
        //配置为非阻塞方式
        socketChannel.configureBlocking(false);
        //服务端地址
        InetSocketAddress serverAddress = new InetSocketAddress(ChatConfig.HOST, ChatConfig.PORT);
        //连接服务端
        if (!socketChannel.connect(serverAddress)) {
            //连接失败，不断重试
            while (!socketChannel.finishConnect()) {
                System.out.println("Client：重试连接...");
            }
        }
        //获取客户端的ip和端口，作为用户名
        clientName = clientName + "（" + socketChannel.getLocalAddress().toString().substring(1) + "）";
        System.out.println("------------------ 客户端" + clientName + " <准备完毕> ------------------");
    }

    /**
     * 发送消息
     *
     * @param msg 消息内容
     */
    public void sendMsg(String msg) throws IOException {
        //输入了结束指令
        if ("bye".equals(msg)) {
            socketChannel.close();
            return;
        }
        msg = clientName + "说：" + msg;
        //发送消息给服务端
        ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());
        socketChannel.write(buffer);
    }

    /**
     * 从服务端拉取信息
     */
    public void receiveMsg() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int size = socketChannel.read(buffer);
        //读取到了
        if (size > 0) {
            String msg = new String(buffer.array());
            System.out.println("我收到了消息：" + msg);
        }
    }

    /**
     * 开启客户端
     */
    public void start() throws IOException {
        //另起一个线程，不断循环监听服务端的消息
        new Thread(() -> {
            try {
                receiveMsg();
                Thread.sleep(2000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        String tip = "请输入您要发送的消息：";
        //等待控制台输入
        System.out.println(tip);
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            //将输入的消息发送
            String msg = scanner.nextLine();
            sendMsg(msg);
            System.out.println("====> 发送成功 <====");
            System.out.println(tip);
        }
    }
}