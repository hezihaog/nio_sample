package com.itcast.nio.file;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class TestNIO {
    /**
     * 测试NIO写数据，写数据到本地文件
     */
    @Test
    public void test1() throws Exception {
        //1.创建输出流
        FileOutputStream outputStream = new FileOutputStream("basic.txt");
        //2.从流中获取一个通道
        FileChannel channel = outputStream.getChannel();
        //3.提供一个缓冲区
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        //4.往缓存区内写数据
        String data = "hello nio";
        buffer.put(data.getBytes());
        //5.写完后，需要翻转缓冲区，让指针回到第一个位置
        buffer.flip();
        //6.把缓冲区的数据写到通道中
        channel.write(buffer);
        //7.关闭流
        outputStream.close();
    }

    /**
     * 测试NIO读取数据，从本地文件读取数据
     */
    @Test
    public void test2() throws Exception {
        File file = new File("basic.txt");
        //1.创建输入流
        FileInputStream inputStream = new FileInputStream(file);
        //2.获取流中的通道
        FileChannel channel = inputStream.getChannel();
        //3.提供一个缓冲区
        ByteBuffer buffer = ByteBuffer.allocate((int) file.length());
        //4.从通道中读取数据，并存储到缓冲区中
        channel.read(buffer);
        //5.打印文件内容
        byte[] data = buffer.array();
        System.out.println(new String(data));
        //6.关闭流
        inputStream.close();
    }

    /**
     * 测试NIO，实现文件复制
     */
    @Test
    public void test3() throws Exception {
        //1.创建2个流
        FileInputStream inputStream = new FileInputStream("basic.txt");
        FileOutputStream outputStream = new FileOutputStream("basic_copy.txt");
        //2.获取2个流的通道
        FileChannel sourceFileChannel = inputStream.getChannel();
        FileChannel targetFileChannel = outputStream.getChannel();
        //3.复制，transferFrom()，从哪个通道获取数据，从哪开始到，到哪里结束
        targetFileChannel.transferFrom(sourceFileChannel, 0, sourceFileChannel.size());
        //3.复制的另外一种方式，和transferFrom()是相反的，transferTo，复制到哪个通道，从哪开始到，到哪里结束
        //sourceFileChannel.transferTo(0, sourceFileChannel.size(), targetFileChannel);
        //4.关闭流
        inputStream.close();
        outputStream.close();
    }
}