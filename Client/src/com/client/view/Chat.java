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
    JButton sendButton,pictureButton,fileButton;
    JPanel buttonBox;
    String MyId;
    String Client;
    String Flie;

//    发送文件模块要用到的
    private JFrame frame;
    private Container contentPanel;
    private JProgressBar progressbar;
//    private ObjectInputStream dis;
//    private ObjectOutputStream dos;
    private RandomAccessFile rad;
    private JLabel label;

    private void initview(String myId , String client) throws IOException {

        ChatBox = new JTextArea();
        ChatBox.setEditable(false);
        msgtext = new JTextField(15);
        buttonBox = new JPanel();
        sendButton = new JButton("发送消息");
        sendButton.addActionListener(this);
//        pictureButton = new JButton("选择图片");
        fileButton = new JButton("选择文件");
        fileButton.addActionListener(this);

        buttonBox.add(msgtext);
        buttonBox.add(sendButton);
//        buttonBox.add(pictureButton);
        buttonBox.add(fileButton);

        this.add(ChatBox,"Center");
        this.add(buttonBox,"South");
        this.setSize(350,350);
        this.setTitle(myId + " 正在和" + client + "聊天");
        this.MyId = myId;
        this.Client = client;
//        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == sendButton){
//            点击发送消息的按钮
            Message msg = new Message();
            msg.setMsgkind(MessageKind.message_text);
            msg.setSender(this.MyId);
            msg.setReceiver(this.Client);
            msg.setSendTime(new Date().toString());
            msg.setText(msgtext.getText());
//            上面是msg的设置

            try {
                SocketManagerMap socketManagerMap = new SocketManagerMap();
//                利用本地的用户名，获得自己和服务器连接的socket
                ObjectOutputStream outputStream = new ObjectOutputStream(
                        socketManagerMap.getThread(MyId).getSocket().getOutputStream());
//                发送消息
                outputStream.writeObject(msg);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            msgtext.setText("");
        }

        if (e.getSource() == fileButton){
//            SendFile sf=new SendFile(MyId,Client);
//            sf.start();

//            Message msg = new Message();
//            msg.setMsgkind(MessageKind.message_file);
//            msg.setSender(this.MyId);
//            msg.setReceiver(this.Client);
//            msg.setSendTime(new Date().toString());
//            SocketManagerMap socketManagerMap = new SocketManagerMap();
//                利用本地的用户名，获得自己和服务器连接的socket
//            ObjectOutputStream outputStream = null;
//            try {
//                outputStream = new ObjectOutputStream(
//                        socketManagerMap.getThread(MyId).getSocket().getOutputStream());
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
////                发送消息
//            try {
//                outputStream.writeObject(msg);
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }

            String cmd = "java -jar D:\\Code_in_IDEA\\Client\\Send.jar";
            try {
                Runtime.getRuntime().exec(cmd);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }


//        文件传输
//        if (e.getSource() == fileButton){
//            System.out.println("1");
//            frame =new JFrame("文件传输");
//            JFileChooser fc = new JFileChooser();
//            int status=fc.showOpenDialog(null);
//
//            if (status==JFileChooser.APPROVE_OPTION) {//检测到按键是“确认提交”
//                //获得文件路径
//                System.out.println("2");
//
//                String path=fc.getSelectedFile().getPath();
//                try {
////				获得输出输出流
//                    System.out.println("222");
//                    SocketManagerMap socketManagerMap = new SocketManagerMap();
//                    ObjectOutputStream dos = new ObjectOutputStream(
//                            socketManagerMap.getThread(MyId).getSocket().getOutputStream());
//                    ObjectInputStream dis=new ObjectInputStream(
//                            socketManagerMap.getThread(MyId).getSocket().getInputStream());
//
////                    这里是我自己加的，这时候要先判断是发送文件吧？是的。
////                    发送、接收、时间、类型
//                    System.out.println("3");
//
//                    Message m = new Message();
//                    m.setSender(MyId);
//                    m.setReceiver(Client);
//                    m.setSendTime(new Date().toString());
//                    m.setMsgkind(MessageKind.message_file);
//                    dos.writeObject(m);
//                    System.out.println("4");
//
//
////				发送“ok”，先建立连接
//                    dos.writeUTF("ok");
////				新建RandomAccessFile类型的文件，path是已获取到的
//                    rad=new RandomAccessFile(path, "r");
////				打开此文件
//                    File file=new File(path);
////				建立缓冲区
//                    byte[] buf=new byte[1024];
////				传送文件名
//                    dos.writeUTF(file.getName());
////				flush强制将缓冲区发送。其实是因为，有时候你用wirte了，但是电脑存在缓冲区，等会儿再发。和你的要求不一致
//                    dos.flush();
////				那，上面flush结束后，其实就是，发送了“ok”和文件名
//
//                    System.out.println("5");
//
////				等啊等，等到了对面发来的readUTF，作为rsp
//                    String rsp=dis.readUTF();
//
////				如果对面发来的是“ok”
//                    if (rsp.equals("ok")) {
//
//                        System.out.println("6");
//
////					还获取到了对面发来的Long，代表接收方那边的同名文件的大小
//                        long size=dis.readLong();//读取文件已发送的大小
////					我就把我这边RandomAccessFile文件的长度给他发过去，再写个“ok”
//                        dos.writeLong(rad.length());
//                        dos.writeUTF("ok");
//                        dos.flush();
//
////					这个时候是要等待么？一会儿看看对面的逻辑
//
////					利用对面发来的size，作为偏移量，知道我要从哪开始写了
//                        long offset=size;//字节偏移量
//
////					用我这边的文件的长度，除以1024，作为barsize
//                        int barSize=(int) (rad.length()/1024);
////					  用获取到的对面的文件长度，除以1024，作为baroffset
//                        int barOffset=(int)(offset/1024);
//                        //传输界面
//                        frame.setSize(380,120);
//                        contentPanel = frame.getContentPane();
//                        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
//                        progressbar = new JProgressBar();//进度条
//
//                        label=new JLabel(file.getName()+" 发送中");
//                        contentPanel.add(label);
//
//                        progressbar.setOrientation(JProgressBar.HORIZONTAL);
//                        progressbar.setMinimum(0);
//                        progressbar.setMaximum(barSize);
//                        progressbar.setValue(barOffset);
//                        progressbar.setStringPainted(true);
//                        progressbar.setPreferredSize(new Dimension(150, 20));
//                        progressbar.setBorderPainted(true);
//                        progressbar.setBackground(Color.pink);
//
//                        JButton cancel=new JButton("取消");
//
//                        JPanel barPanel=new JPanel();
//                        barPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
//
//                        barPanel.add(progressbar);
//                        barPanel.add(cancel);
//
//                        contentPanel.add(barPanel);
//
//                        cancel.addActionListener(new CancelActionListener());
//
//                        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//                        frame.setVisible(true);
//
//                        //上面这么多，就是设置了传输界面
//
//
//                        //从文件指定位置开始传输
//                        int length;
//                        //如果我本地的这个文件，比你的偏移大，那说明没传完，我就找到我要从哪里开始
//                        if (offset<rad.length()) {
//                            rad.seek(offset);
//                            while((length=rad.read(buf))>0){
//                                dos.write(buf,0,length);
//                                progressbar.setValue(++barOffset);
//                                dos.flush();
//                            }
//                        }
////					直到文件上传结束，提示完成
//                        label.setText(file.getName()+" 发送完成");
//                    }
////				关闭输入输出流，关闭文件
//                    dis.close();
//                    dos.close();
//                    rad.close();
//                } catch (FileNotFoundException ex) {
//                    ex.printStackTrace();
//                } catch (IOException ex) {
//                    ex.printStackTrace();
//                }
//
//            }
//
//
//        }

//        if (e.getSource() == fileButton){
////            发送文件按钮
////            System.out.println("点击了文件按钮");;
//            Message m = new Message();
//            m.setSender(MyId);
//            m.setReceiver(Client);
//            m.setSendTime(new Date().toString());
//            m.setMsgkind(MessageKind.message_file);
////            System.out.println(m.getMsgkind());
////            主要是调用了FileChooser()这个函数
//            FileChooser();
//            File f = new File(Flie);
//            m.setFile_inside(getContent(f));
//            m.setFname(f.getName());
////            System.out.println("选定的文件是 " + m.getFname());
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


    }


    //接收文件的方法
    public void receiveFile(Message m){
        File receivefile = new File("D:\\textfile\\receive\\"+m.getFname());
        try {
            OutputStream output = new FileOutputStream(receivefile);
            byte[] contentBytes = new byte[m.getFile_inside().getBytes().length];
            contentBytes = m.getFile_inside().replaceAll("\n", "\r\n").getBytes();
//            for(int i =0;i<contentBytes.length;i++){
//                System.out.print(contentBytes[i]+" ");
//            }
            try {
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

        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        // chooser.showDialog(new JLabel(), "发送");
        File file = chooser.getSelectedFile();//选到了文件
        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            Flie = f.getAbsolutePath();
        }
        if (returnVal == JFileChooser.CANCEL_OPTION) {
        }
        return file;
    }

    public static String getContent(File file) {
        String content = "";
        try {
            // 创建输入流
            InputStream input = new FileInputStream(file);
            // 开辟一块缓存区域
            byte[] contentBytes = new byte[input.available()];//缓存区的大小，是输入流的可用
            // 读取所有的字节到缓存中
            for (int i = 0; i < contentBytes.length; i++) {
                contentBytes[i] = (byte) input.read();
            }
            // 根据字节数组创建字符串
            //将缓存区的东西，做成一个字符串，返回
            content = new String(contentBytes);
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
//        this.ChatBox.append(show);
        String fileshow = msg.getSender() + "在" + msg.getSendTime() + " 对 " + msg.getReceiver()
                +" 发了一个文件： "  +msg.getFname() + "\r\n";
        if (msg.getMsgkind().equals(MessageKind.message_text))
            this.ChatBox.append(show);
        if (msg.getMsgkind().equals(MessageKind.message_file))
            this.ChatBox.append(fileshow);
    }


    class CancelActionListener implements ActionListener{
        public void actionPerformed(ActionEvent e3){
            try {
//				监听到取消发送
                label.setText(" 取消发送,连接关闭");
                JOptionPane.showMessageDialog(frame, "取消发送给，连接关闭!", "提示：", JOptionPane.INFORMATION_MESSAGE);
//                dis.close();
//                dos.close();
                rad.close();
                frame.dispose();
            } catch (IOException e1) {

            }
        }
    }
}
