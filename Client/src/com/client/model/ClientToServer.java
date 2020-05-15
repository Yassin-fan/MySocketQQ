package com.client.model;

import com.client.common.Message;
import com.client.common.User;
import com.client.tools.SocketManagerMap;

import java.io.*;
import java.net.Socket;

public class ClientToServer {
    public Socket socket;


//    主要作用也就是建立socket，发送了个验证请求，启动ClientToServerThread线程，然后把线程加入管理器
    // 发送第一次请求
    public boolean SendLoginInfoToSever(Object o) {
        boolean b = false;
        try {
            socket = new Socket("localhost", 1000);
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(o);
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            try {
                Message ms = (Message) ois.readObject();
                // 这里是验证用户登录的地方
                if (ms.getMsgkind().equals("1")) {
                    b = true;
                    ClientToServerThread clientToServerThread = new ClientToServerThread(socket);
                    clientToServerThread.start();
//                    SocketManagerMap socketManagerMap =new SocketManagerMap();
                    SocketManagerMap.addThread( ((User)o).getId() , clientToServerThread );

                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
        return b;
    }
}
