package com.client.view;


import com.client.common.Message;
import com.client.tools.ChatManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

public class List extends JFrame implements ActionListener, MouseListener {
//    public static void main(String[] args) {
//        List list = new List(myId);
//    }

    public List(String myId){
        initview(myId);
    }

    JScrollPane roll;
    JPanel list;
    JLabel []elements;
    private String MyId;
    JButton sendfile;

    public void reloadList(Message msg){
//        接受到发来的在线好友列表，放在friendOnlineList
        String friendOnlineList =  msg.getText();
//        String get_ID = msg.getReceiver();
        String friend[] = friendOnlineList.split( " ");
        for (int i = 0; i < friend.length; i++){
//            然后重新加载list
            elements[ Integer.parseInt(friend[i]) - 1 ].setEnabled(true);
        }
    }

    private void initview(String myId){
        list = new JPanel(new GridLayout(10,1,4,4));
//        sendfile = new JButton("发送离线文件");
//        list.add(sendfile);
//        sendfile.addActionListener(this);
        elements = new JLabel[10];
        for(int i = 0;i<elements.length;i++){
            elements[i]= new JLabel(i+1+"", new ImageIcon("D:\\Code_in_IDEA\\Client\\unnamed.jpg"),JLabel.LEFT);
            elements[i].addMouseListener(this);
            elements[i].setEnabled(false);
            if (elements[i].getText().equals(myId)){
                elements[i].setEnabled(true);
            }
            list.add(elements[i]);
        }

        roll = new JScrollPane(list);
        this.add(roll,"Center");
        this.setSize(250,650);
        this.setTitle("用户：" + myId);
        this.MyId = myId;
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==sendfile){
//            FileSendTest.main();
//            SendFile sf=new SendFile();
//            sf.start();

//            String cmd = "java -jar D:\\Code_in_IDEA\\Client\\out\\artifacts\\Client_jar\\Client.jar";
//            try {
//                Runtime.getRuntime().exec(cmd);
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
        }

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if(e.getClickCount()==2)
        {
            //监听到双击，得到该好友编号
            String friendId = ((JLabel)e.getSource()).getText();
//            System.out.println("你点击的朋友编号是"+friendNum);
//            新建一个chat对象
            Chat chat = null;
            try {
                chat = new Chat(this.MyId,friendId);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            //把聊天界面加入到chat管理
            ChatManager.addChat(this.MyId+" "+friendId, chat);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        JLabel jl = (JLabel)e.getSource();
        jl.setForeground(Color.red);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        JLabel jl = (JLabel)e.getSource();
        jl.setForeground(Color.black);
    }
}
