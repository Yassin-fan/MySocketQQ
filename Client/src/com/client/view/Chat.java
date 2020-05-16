package com.client.view;

import com.client.common.Message;
import com.client.common.MessageKind;
import com.client.tools.SocketManagerMap;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Date;

public class Chat extends JFrame implements ActionListener{


    public Chat(String myId, String client) throws IOException {
        initview(myId,client);
    }

    JTextArea ChatBox;
    JTextField msgtext;
    JButton sendButton,onlinefine,fileButton;
    JPanel buttonBox;
    String MyId;
    String Client;
    String Flie;




    private FileInputStream fis;
    private DataOutputStream dos;


    private void initview(String myId , String client) throws IOException {

        ChatBox = new JTextArea();
        ChatBox.setEditable(false);
        msgtext = new JTextField(15);
        buttonBox = new JPanel();
        sendButton = new JButton("发送消息");
        sendButton.addActionListener(this);
        onlinefine = new JButton("在线文件");
        onlinefine.addActionListener(this);
        fileButton = new JButton("离线文件");
        fileButton.addActionListener(this);

        buttonBox.add(msgtext);
        buttonBox.add(sendButton);
        buttonBox.add(onlinefine);
        buttonBox.add(fileButton);

        this.add(ChatBox,"Center");
        this.add(buttonBox,"South");
        this.setSize(550,350);
        this.setTitle(myId + " 正在和" + client + "聊天");
        this.MyId = myId;
        this.Client = client;
//        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == sendButton){
            Message msg = new Message();
            msg.setMsgkind(MessageKind.message_text);
            msg.setSender(this.MyId);
            msg.setReceiver(this.Client);
            msg.setSendTime(new Date().toString());
            msg.setText(msgtext.getText());
            try {
                SocketManagerMap socketManagerMap = new SocketManagerMap();
                ObjectOutputStream outputStream = new ObjectOutputStream(
                        socketManagerMap.getThread(MyId).getSocket().getOutputStream());
                outputStream.writeObject(msg);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            msgtext.setText("");
        }

        if (e.getSource() == fileButton){
            String cmd = "java -jar D:\\Code_in_IDEA\\Client\\Send.jar";
            try {
                Runtime.getRuntime().exec(cmd);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            Message msg = new Message();
            msg.setMsgkind(MessageKind.message_text);
            msg.setSender(this.MyId);
            msg.setReceiver(this.Client);
            msg.setText("发送了离线文件");
            msg.setSendTime(new Date().toString());
            SocketManagerMap socketManagerMap = new SocketManagerMap();
//                利用本地的用户名，获得自己和服务器连接的socket
            ObjectOutputStream outputStream = null;
            try {
                outputStream = new ObjectOutputStream(
                        socketManagerMap.getThread(MyId).getSocket().getOutputStream());
                outputStream.writeObject(msg);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

//原版！！！！！！！
////        有个问题啊，就是其实只能传送txt？
//        if (e.getSource() == onlinefine){
////            发送在线文件按钮
//            Message m = new Message();
//            m.setSender(MyId);
//            m.setReceiver(Client);
//            m.setSendTime(new Date().toString());
//            m.setMsgkind(MessageKind.message_file);
//
////            主要看FileChooser()，getContent(f)这两个函数
////            FileChooser()使得全局变量file，就是选中的哪个
//            FileChooser();
////            选中这个文件
//            File f = new File(Flie);
////            准备读取内容
//            m.setFile_inside(getContent(f));
//
////            try {
////                m.setByteslist(getBytes(f));
////            } catch (IOException ex) {
////                System.out.println("你的构想不对");
////                ex.printStackTrace();
////            }
//
//            m.setFname(f.getName());
//            try {
////                获取自己的socket，发送
//                ObjectOutputStream oos = new ObjectOutputStream(
//                        SocketManagerMap.getThread(MyId).getSocket()
//                                .getOutputStream());
//                oos.writeObject(m);
//            } catch (IOException e1) {
//                e1.printStackTrace();
//            }
//            msgtext.setText("");
//            JOptionPane.showMessageDialog(this,"文件传输成功！");
//        }
//    }


//        有个问题啊，就是其实只能传送txt？
        if (e.getSource() == onlinefine){
            FileChooser();
//            选中了这个文件
            File f = new File(Flie);
            Message m = new Message();
            m.setSender(MyId);
            m.setReceiver(Client);
            m.setSendTime(new Date().toString());
            m.setMsgkind(MessageKind.message_file);
            m.setFname(f.getName());
//            先发送一个包
            System.out.println("此时准备先发送一个msg");
            ObjectOutputStream oos = null;
            try {
                oos = new ObjectOutputStream(
                            SocketManagerMap.getThread(MyId).getSocket()
                                    .getOutputStream());
                System.out.println("成功建立输出object流");
                oos.writeObject(m);
                System.out.println("发送msg成功");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            try {
                System.out.println("准备建立data的output");
                dos = new DataOutputStream(SocketManagerMap.getThread(MyId).getSocket()
                        .getOutputStream());
                System.out.println("建立成功！");
//            发送文件名和长度
                dos.writeUTF(f.getName());
                dos.flush();
                dos.writeLong(f.length());
                dos.flush();

//            新建缓冲区
                byte[] sendBytes = new byte[1024];
                int length = 0;
//            获取文件的输入流，为了读取文件
                System.out.println("准备创建文件输入流");
                fis = new FileInputStream(f);
                System.out.println("创建成功！");
                while((length = fis.read(sendBytes, 0, sendBytes.length)) > 0){
                    dos.write(sendBytes, 0, length);
                    dos.flush();
                }
                System.out.println("已经将文件全部发送过去了！");
//                dos.close();

            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            System.out.println("");



            JOptionPane.showMessageDialog(this,"文件传输成功！");
        }
    }

    //线程里一直监听，收到文件的msg，会调用接收函数，其实就是将收到的msg里的一个字符串转成了文件
    public void receiveFile(Message m){
//        新建文件
        File receivefile = new File("D:\\Code_in_IDEA\\Client\\"+m.getFname());
        try {
//            打开对此文件的输出流
            OutputStream output = new FileOutputStream(receivefile);
//            获取到msg里的那个字符串的长度，开辟一个字节数组
            byte[] contentBytes = new byte[m.getFile_inside().getBytes().length];
            System.out.println("收到输入");
            System.out.println("传来的字符串格式为：" + m.getFile_inside());
            System.out.println("翻译后的字符数组格式为：" + contentBytes);

//            System.out.println("msg里自带的字符数组格式为：" + m.getByteslist());

//            将\n换成\r\n，为啥啊？
            contentBytes = m.getFile_inside().replaceAll("\n", "\r\n").getBytes();
//            for(int i =0;i<contentBytes.length;i++){
//                System.out.print(contentBytes[i]+" ");
//            }
            try {
//                把字节数组写进去，完事了
                output.write(contentBytes);
                output.flush();
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public File FileChooser() {
//        新建选择框
        JFileChooser chooser = new JFileChooser();
//        选择
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        // chooser.showDialog(new JLabel(), "发送");
//        选中了一个文件
        File file = chooser.getSelectedFile();//选到了文件
//        判断一下
        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
//            新建文件类型，就是把选中的那个文件
            File f = chooser.getSelectedFile();
//            通过地址选中了
            Flie = f.getAbsolutePath();
        }
        if (returnVal == JFileChooser.CANCEL_OPTION) {
        }
        return file;
    }

//    public static byte[] getBytes(File file) throws IOException {
//        InputStream input = new FileInputStream(file);
//        // 开辟一块缓存区域
//        byte[] contentBytes = new byte[input.available()];//缓存区的大小，是输入流的可用
//        // 读取所有的字节到缓存中
//        for (int i = 0; i < contentBytes.length; i++) {
//            contentBytes[i] = (byte) input.read();
//        }
//        return contentBytes;
//    }

    public static String getContent(File file) {
        String content = "";
        try {
            // 创建文件的输入流
            InputStream input = new FileInputStream(file);
            // 开辟一块缓存区域
            byte[] contentBytes = new byte[input.available()];//缓存区的大小，是输入流的可用
            // 读取所有的字节到缓存中
            for (int i = 0; i < contentBytes.length; i++) {
                contentBytes[i] = (byte) input.read();
            }
            System.out.println("准备输出");
            System.out.println("字符数组格式为：" + contentBytes);

            // 根据字节数组创建字符串
            //将缓存区的东西，做成一个字符串，返回
            content = new String(contentBytes);
            System.out.println("字符串格式为：" + content);
            // 关闭流
            input.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return content;
    }




    public void ShowMessage(Message msg){
        String show = msg.getSender() + "在" + msg.getSendTime() + " 对 " + msg.getReceiver()
                +" 说： " + msg.getText() + "\r\n";
        String fileshow = msg.getSender() + "在" + msg.getSendTime() + " 对 " + msg.getReceiver()
                +" 发了一个文件： "  +msg.getFname() + "\r\n";
        if (msg.getMsgkind().equals(MessageKind.message_text))
            this.ChatBox.append(show);
        if (msg.getMsgkind().equals(MessageKind.message_file))
            this.ChatBox.append(fileshow);
    }

}
