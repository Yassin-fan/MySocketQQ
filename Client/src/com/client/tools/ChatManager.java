package com.client.tools;

import com.client.model.ClientToServerThread;
import com.client.view.Chat;

import java.util.HashMap;

public class ChatManager {

    private static HashMap hashMap = new HashMap<String, Chat>();

    public static void addChat(String MyIdAndOne, Chat chat){
        hashMap.put(MyIdAndOne, chat);
    }

    public static Chat getChat(String MyIdAndOne){
        return (Chat) hashMap.get(MyIdAndOne);
    }
}
