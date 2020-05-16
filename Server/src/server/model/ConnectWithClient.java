package server.model;

import com.client.common.Message;
import com.client.common.MessageKind;

import javax.sound.midi.Soundbank;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

public class ConnectWithClient extends Thread{
    Socket socket;

    private boolean flag = true;

    public ConnectWithClient(Socket socket){
        this.socket = socket;
    }



    private JFrame frame;
    private Container contentPanel;
    private JProgressBar progressbar;
    private RandomAccessFile rad;
    private JLabel label;

//    三个功能：
//    1.保存了和客户端建立的socket
//    2.告诉其他客户端，我上线了
//    3.一直监听收到的消息，根据收到的msg种类不同，处理不同操作


    public void TellOthersToReloadList(String myId){
//        ！！！
//        这个其实很有用，因为是对所有的在线的客户端发送消息。如果用在群发上，太棒了
//        String onlinelist = ClientManger.getOnlie();
        HashMap hm = ClientManger.hashMap;
        Iterator it = hm.keySet().iterator();
//        准备进行广播发送
        while(it.hasNext()){
            Message m = new Message();
            m.setText(myId);
            m.setMsgkind(MessageKind.message_ret_FriendList);
            //取出在线人的id
            String OnLineUserId = it.next().toString();
            try {
                ObjectOutputStream oos = new ObjectOutputStream(ClientManger.getThread(OnLineUserId).socket.getOutputStream());
                m.setReceiver(OnLineUserId);
                oos.writeObject(m);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private DataInputStream dis;
    private FileOutputStream fos;




    public void run(){
        while (flag){
            try {
//                System.out.println("获得到了输入流吗？");
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
//                System.out.println("得到了啊");
                Message msg = (Message) objectInputStream.readObject();

                if (msg.getMsgkind().equals(MessageKind.message_close)){
                    socket.close();
                    flag = false;
                    System.out.println("收到关闭的消息了，flag是"+flag);
                }

//                文本信息
                if (msg.getMsgkind().equals(MessageKind.message_text)){
//                    获得与接收方连接的线程
                    ConnectWithClient receiverThread = ClientManger.getThread(msg.getReceiver());
//                  receiver的connect线程中，包含了receiver的socket
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(receiverThread.socket.getOutputStream());
//                    单纯实现了一个转发消息的操作
                    objectOutputStream.writeObject(msg);
                }
//                列表获取信息
                else if (msg.getMsgkind().equals(MessageKind.message_get_FriendList)){
                    String onlinelist = ClientManger.getOnlie();
                    Message backmsg = new Message();
                    backmsg.setMsgkind(MessageKind.message_ret_FriendList);
                    backmsg.setText(onlinelist);
                    backmsg.setReceiver(msg.getSender());
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(backmsg);
                }

//                原版！！！！！！
////                文件发送信息
//                else if(msg.getMsgkind().equals(MessageKind.message_file)){
//                    ConnectWithClient receiverThread = ClientManger.getThread(msg.getReceiver());
//                    ObjectOutputStream oos = new ObjectOutputStream(receiverThread.socket.getOutputStream());
//                    oos.writeObject(msg);
//                }

                //                文件发送信息
                else if(msg.getMsgkind().equals(MessageKind.message_file)){
                    //此时已经收到先传来的msg了，我不管了。我要在这里接受文件
//                    System.out.println("进入了文件接收代码，准备获得和sender的线程");
                    ConnectWithClient senderThread = ClientManger.getThread(msg.getSender());
//                    先获得了data输入流
//                    System.out.println("得到线程，准备建立DataIn");
                    dis = new DataInputStream(senderThread.socket.getInputStream());
//                    System.out.println("建立成功，准备读取");
                    String fileName = dis.readUTF();
//                    System.out.println("读到名字了" + fileName);
                    long fileLength = dis.readLong();
//                    System.out.println("读到了名字和长度");
//                    再新建一个文件，然后获得输出流，用来写
//                    System.out.println("准备获得文件输出流，用来写");
                    File file = new File("D:\\" + fileName);
                    fos = new FileOutputStream(file);
//                    System.out.println("得到成功，准备新建缓冲区");

                    byte[] sendBytes = new byte[1024];
                    int transLen = 0;
//                    System.out.println("建立成功，准备写入");

//                    为啥是死循环？
                    int count=-1,sum=0;

                    while((count=dis.read(sendBytes))!=-1){
                        fos.write(sendBytes,0,count);
                        sum+=count;
//                        System.out.println("已接收" + sum + "比特");
                        if(sum==fileLength)
                            break;
//                        System.out.println("能不能成功跳出？");
                    }
                    fos.flush();
//                    此时，服务器已经成功收到，现在要准备发给接收方了
//                    1.先发送一个msg，是file格式
                    Message m = new Message();
                    m.setSender(msg.getSender());
                    m.setReceiver(msg.getReceiver());
                    m.setSendTime(new Date().toString());
                    m.setMsgkind(MessageKind.message_file);
                    m.setFname(fileName);

//                    2.获取接收方的socket，准备发送
                    ConnectWithClient receiverThread = ClientManger.getThread(msg.getReceiver());
                    System.out.println("准备建立接收方的Object");
                    ObjectOutputStream oos = new ObjectOutputStream(receiverThread.socket.getOutputStream());
                    System.out.println("建立成功！");
                    oos.writeObject(msg);
                    System.out.println("发送msg成功！");

//                    3.发送msg完了，准备发送文件了。这个时候的函数其实和chat里的发送一模一样
                    System.out.println("准备建立接收方的Data");
                    DataOutputStream dos = new DataOutputStream(receiverThread.socket.getOutputStream());
                    System.out.println("建立成功！");
//            发送文件名和长度
                    dos.writeUTF(file.getName());
                    dos.flush();
                    dos.writeLong(file.length());
                    dos.flush();
                    System.out.println("发送文件名和长度成功！");

//            新建缓冲区
                    byte[] sendToReceiverBytes = new byte[1024];
                    int Receiverlength = 0;
//            获取文件的输入流，为了读取文件
                    System.out.println("准备创建发送的文件输入流");
                    FileInputStream fis = new FileInputStream(file);
                    System.out.println("创建成功！");
                    while((Receiverlength = fis.read(sendToReceiverBytes, 0, sendToReceiverBytes.length)) > 0){
                        dos.write(sendToReceiverBytes, 0, Receiverlength);
                        dos.flush();
                    }
                    System.out.println("已经将文件全部发给客户端！");
//                dos.close();

//                    System.out.println("写入成功了啊");

//                    while(true){
////                        System.out.println("read用来获取输入的字节");
//                        int read = 0;
//                        read = dis.read(sendBytes);
////                        System.out.println(""获取到进行判断);
////                        没关系，此处有跳出
//                        if(read == -1)
//                            break;
//
//                        transLen += read;
////                        System.out.println("接收文件进度" + 100 * transLen/fileLength + "%...");
////                        写入
//                        fos.write(sendBytes, 0, read);
//                        fos.flush();
//                        System.out.println("写入成功了啊");
//                        System.out.println("但是没法再次获得输入流了，迷惑");
////                        continue;
////                        原来是在这里死循环了啊，怪不得呢。缕一下思路
////                        是因为没有结束标志位！！！！
//                    }

//                    ObjectOutputStream oos = new ObjectOutputStream(receiverThread.socket.getOutputStream());
//                    oos.writeObject(msg);
                }

//                else if(msg.getMsgkind().equals(MessageKind.message_file)){
//                    System.out.println("1");
//
//                    ObjectOutputStream dos = new ObjectOutputStream(socket.getOutputStream());
//                    frame=new JFrame("接收文件");
//
//                    System.out.println("2");
//
//
//                    objectInputStream.readUTF();
//
//                    System.out.println("3");
//
//                    int permit=JOptionPane.showConfirmDialog(frame, "是否接收文件","文件传输请求：", JOptionPane.YES_NO_OPTION);
//                    if (permit==JOptionPane.YES_OPTION) {
//                        System.out.println("4");
//
//
//                        String filename=objectInputStream.readUTF();
////				回传一个ok
//                        dos.writeUTF("ok");
//                        dos.flush();
////				新建文件，名字就是发来的名字？每次都要新建？那岂不是会清空呢
//                        File file=new File(filename+".temp");
//
////				哦，这个不是RandomAccessFile文件，是对这个文件进行Random
//                        rad=new RandomAccessFile(filename+".temp", "rw");
//                        //获得文件大小
//                        long size=0;
////				如果文件已存在，获取现有大小
//                        if(file.exists()&& file.isFile()){
//                            size=file.length();
//                        }
//
//                        //发送现有的大小
//                        dos.writeLong(size);
//                        dos.flush();
//
////				读取Long，也就是发送方发来的，他那边的这个文件的大小
//                        long allSize=objectInputStream.readLong();
//                        String rsp=objectInputStream.readUTF();
//
////				用来设置进度条的
//                        int barSize=(int)(allSize/1024);
//                        int barOffset=(int)(size/1024);
//
//                        //传输界面
//                        frame.setSize(300,120);
//                        contentPanel =frame.getContentPane();
//                        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
//                        progressbar = new JProgressBar();//进度条
//
//                        label=new JLabel(filename+" 接收中");
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
//                        frame.setDefaultCloseOperation(
//                                JFrame.EXIT_ON_CLOSE);
//                        frame.setVisible(true);
////			    上面这些都是设置界面的，给服务器传输的时候不需要的
//
//
//                        //接收文件
//                        if (rsp.equals("ok")) {
//                            rad.seek(size);
//                            int length;
//                            byte[] buf=new byte[1024];
//                            while((length=objectInputStream.read(buf, 0, buf.length))!=-1){
//                                rad.write(buf,0,length);
//                                progressbar.setValue(++barOffset);
//                            }
//                            System.out.println("FileReceive end...");
//                        }
//
//                        label.setText(filename+" 结束接收");
//                        rad.close();
//                        frame.dispose();
//
//                        //文件重命名
//                        if (barOffset>=barSize) {
//                            file.renameTo(new File(filename));
//                        }
//                    }else{
//                        frame.dispose();
//                    }
//                }


            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

    }



    class CancelActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e){
            try {
                rad.close();
                JOptionPane.showMessageDialog(frame, "已取消接收，连接关闭！", "提示：", JOptionPane.INFORMATION_MESSAGE);
                label.setText(" 取消接收,连接关闭");
            } catch (IOException e1) {

            }
        }
    }

}
