package com.itcast.nio.chat;

import java.io.IOException;

public class ChatUser2 {
    public static void main(String[] args) throws IOException {
        ChatClient chatClient = new ChatClient("小罗");
        chatClient.start();
    }
}