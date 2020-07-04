package com.itcast.nio.chat;

import java.io.IOException;

public class ChatUser1 {
    public static void main(String[] args) throws IOException {
        ChatClient chatClient = new ChatClient("小何");
        chatClient.start();
    }
}