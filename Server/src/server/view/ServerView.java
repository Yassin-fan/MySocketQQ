package server.view;

import server.model.FileReceiveTest;
import server.model.ReceiveFile;
import server.model.ServerModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class ServerView extends JFrame implements ActionListener {

    public ServerView(){
        initview();
    }

    public static void main(String[] args) {
        ServerView serverview = new ServerView();
    }

    JPanel buttonBox;
    JTextArea text;
    JButton start, stop, show;

    private void initview() {
        buttonBox = new JPanel();
//        text = new JTextArea();
        start = new JButton("启动服务器");
        start.addActionListener(this);
//        stop = new JButton("关闭服务器");
//        show = new JButton("显示用户列表");
        start.addActionListener(this);
//        stop.addActionListener(this);
//        show.addActionListener(this);

        buttonBox.add(start);
//        buttonBox.add(stop);
//        buttonBox.add(show);

        this.add(buttonBox, "Center");
//        this.add(text, "Center");
//        this.setDefaultCloseOperation(3);
        this.setSize(350, 200);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == start){

            String cmd = "java -jar D:\\Code_in_IDEA\\Client\\Receive.jar";
            try {
                System.out.println("启动！");
                Runtime.getRuntime().exec(cmd);
                new ServerModel().run();
//                System.out.println("启动！");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
