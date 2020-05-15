package server.model;


import com.client.common.Message;
import com.client.common.MessageKind;
import com.client.common.User;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerModel {

    public void run() {
//        功能：建立socket连接，判断能否登陆，新建和客户端对应的ConnectWithClient，并存入hashmap
        try {
            // 在1000监听
            System.out.println("服务器在监听");
            ServerSocket serverSocket = new ServerSocket(1000);
            while (true) {
                // 阻塞 等待连接
                Socket socket = serverSocket.accept();
                // 接收发来的信息
                System.out.println("连接成功");
                ObjectInputStream ois = new ObjectInputStream(
                        socket.getInputStream());
                ObjectOutputStream oos = new ObjectOutputStream(
                        socket.getOutputStream());

                User u = (User) ois.readObject();
                Message m = new Message();
                if (u.getPwd().equals("123")) {
//                    成功登陆
                    m.setMsgkind(MessageKind.message_login_success);
                    oos.writeObject(m);
//                    新建对应的线程ConnectWithClient
                    ConnectWithClient connectWithClient = new ConnectWithClient(socket);
//                    存入管理器
                    ClientManger.hashMap.put(u.getId() , connectWithClient);
                    connectWithClient.start();
                    connectWithClient.TellOthersToReloadList(u.getId());
                } else {
                    m.setMsgkind(MessageKind.message_login_fail);
                    oos.writeObject(m);
                    socket.close();
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("e is "+ e);
        }
    }

}
