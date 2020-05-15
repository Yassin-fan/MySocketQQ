package com.client.tools;

import com.client.model.ClientToServerThread;

import java.util.HashMap;

public class SocketManagerMap {

    private static HashMap hashMap = new HashMap<String, ClientToServerThread>();

    public static void addThread(String id,  ClientToServerThread clientToServerThread){
        hashMap.put(id,clientToServerThread);
    }

    public static ClientToServerThread getThread(String id){
        return (ClientToServerThread) hashMap.get(id);
    }
}
