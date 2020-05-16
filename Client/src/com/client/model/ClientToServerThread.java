package com.client.model;

import com.client.common.Message;
import com.client.common.MessageKind;
import com.client.tools.ChatManager;
import com.client.tools.ListManager;
import com.client.view.Chat;
import com.client.view.List;

import java.io.*;
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

//                原版接收文件！！！
//
//                else if(msg.getMsgkind().equals(MessageKind.message_file)){  //文件
//                    // 把从服务器得到的消息显示到该显示的聊天界面
//                    Chat chat = ChatManager.getChat(msg.getReceiver() + " " + msg.getSender());
//                    chat.receiveFile(msg);
//                    chat.ShowMessage(msg);
//                }


                else if(msg.getMsgkind().equals(MessageKind.message_file)) {  //文件
//                    获取到chat，一会儿要发一个提示呢
                    Chat chat = ChatManager.getChat(msg.getReceiver() + " " + msg.getSender());
                    //                    客户端此时已经收到msg，跳转到此处。准备接收文件了。
//                    第一步，先获取到DataInput
                    System.out.println("准备建立DataIn");
                    DataInputStream dis = new DataInputStream(socket.getInputStream());
                    System.out.println("建立成功，准备读取");

                    String fileName = dis.readUTF();
                    System.out.println("读到名字了" + fileName);
                    long fileLength = dis.readLong();
                    System.out.println("读到了名字和长度");
//                    再新建一个文件，然后获得输出流，用来写
                    System.out.println("准备获得文件输出流，用来写");
                    File file = new File("D:\\R\\" + fileName);
                    FileOutputStream fos = new FileOutputStream(file);
                    System.out.println("得到成功，准备新建缓冲区");

                    byte[] sendBytes = new byte[1024];
                    int transLen = 0;
                    System.out.println("建立成功，准备写入");

//                    为啥是死循环？
                    int count=-1,sum=0;

                    while((count=dis.read(sendBytes))!=-1){
                        fos.write(sendBytes,0,count);
                        sum+=count;
//                        System.out.println("已接收" + sum + "比特");
                        if(sum==fileLength)
                            break;
                        System.out.println("能不能成功跳出？");
                    }
                    fos.flush();
                    System.out.println("写入完成！");
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
