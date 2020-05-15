package com.client.model;

import com.client.common.Message;
import com.client.common.MessageKind;
import com.client.tools.ChatManager;
import com.client.tools.ListManager;
import com.client.view.Chat;
import com.client.view.List;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class ClientToServerThread extends Thread{

    public Socket getSocket() {
        return socket;
    }

    private Socket socket;

    public ClientToServerThread(Socket s){
        this.socket = s;
    }

    @Override
    public void run() {
        while (true){

//            ClientToServerThread线程的作用，就是根据接收到的msg进行操作

            try {
//                建立io流，得到msg
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                Message msg = (Message) objectInputStream.readObject();

//              根据三种msg类型：文字、在线好友列表的返回、文件，进行操作
                if (msg.getMsgkind().equals(MessageKind.message_text)){
                    ChatManager.getChat(msg.getReceiver() + " " + msg.getSender()).ShowMessage(msg);
                }
                else if (msg.getMsgkind().equals(MessageKind.message_ret_FriendList)){
                    String friendOnlineList =  msg.getText();
                    String get_ID = msg.getReceiver();
                    String friend[] = friendOnlineList.split( " ");
                    List list = ListManager.getList(get_ID);
                    if (list != null){
                        list.reloadList(msg);
                    }
                }
                else if(msg.getMsgkind().equals(MessageKind.message_file)){  //文件
                    // 把从服务器得到的消息显示到该显示的聊天界面
                    Chat chat = ChatManager.getChat(msg.getReceiver() + " " + msg.getSender());
                    chat.receiveFile(msg);
                    chat.ShowMessage(msg);
                }

//                chat.ShowMessage(msg);
//                String show = msg.getSender() + "在" + msg.getSendTime() + " 对 " + msg.getReceiver()
//                        +" 说： " + msg.getText() + "\r\n";
//                System.out.println(show);
//                ChatBox.append(show);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
