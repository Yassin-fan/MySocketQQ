package com.client.view;

//登录界面

import com.client.common.Message;
import com.client.common.MessageKind;
import com.client.model.ClientLocal;
import com.client.common.User;
import com.client.tools.ListManager;
import com.client.tools.SocketManagerMap;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class Login extends JFrame implements ActionListener {
    public static void main(String[] args) {
        Login login = new Login();
    }

    JLabel northJPandel;
    JPanel southJPandel;
    JButton info;

    JTabbedPane middle;
    JPanel logincard,registercard;
    JLabel id,password;
    JTextField idtext;
    JPasswordField passwordtext;
    JCheckBox rem_pwd;
    JButton login,forget,register;

    JLabel reg_id,reg_password;
    JTextField reg_idtext;
    JPasswordField reg_passwordtext;


    public void initView(){
        northJPandel = new JLabel(new ImageIcon("D:\\Code_in_IDEA\\Client\\logo1 (2).png"));

        southJPandel = new JPanel();
        info = new JButton("详情");
        southJPandel.add(info);

        logincard = new JPanel(new GridLayout(3,3));
        id = new JLabel("账号",JLabel.CENTER);
        password = new JLabel("密码",JLabel.CENTER);
        idtext = new JTextField();
        passwordtext = new JPasswordField();
        rem_pwd = new JCheckBox("记住密码");

        login = new JButton("登陆");
        login.addActionListener(this);

        forget = new JButton("忘记密码");
        logincard.add(id);
        logincard.add(idtext);
        logincard.add(login);
        logincard.add(password);
        logincard.add(passwordtext);
        logincard.add(forget);
        logincard.add(rem_pwd);

        registercard = new JPanel(new GridLayout(3,2));
        reg_id = new JLabel("账号",JLabel.CENTER);
        reg_password = new JLabel("密码",JLabel.CENTER);

        reg_idtext = new JTextField();
        reg_passwordtext = new JPasswordField();

        register = new JButton("注册");

        registercard.add(reg_id);
        registercard.add(reg_idtext);
        registercard.add(reg_password);
        registercard.add(reg_passwordtext);
        registercard.add(register);

        middle = new JTabbedPane();
        middle.add("登陆",logincard);
        middle.add("注册",registercard);

        this.add(northJPandel,"North");
        this.add(middle,"Center");
        this.add(southJPandel,"South");
        this.setSize(500,450);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setVisible(true);

    }

    public Login(){
        initView();
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==login){
//            新建本ClientLocal，只有一个功能，就是先调用CToS，创建socket，再验证能否登陆
            ClientLocal local = new ClientLocal();
            User u = new User();
            u.setId(idtext.getText().trim());
            u.setPwd( new String (passwordtext.getPassword()) );
//            System.out.println("得到用户密码：" + u.getId() + "," + u.getPwd());

            boolean rightpwd = local.checkIdAndPwd(u);
            System.out.println("得到状态："+ rightpwd);

            if (rightpwd){
                try {
//                    可以登录，新建列表，并存入列表管理器
                    List list = new List(idtext.getText().trim());
                    ListManager.addList(idtext.getText(),list);
                    ObjectOutputStream oos = new ObjectOutputStream(
                            SocketManagerMap.getThread(u.getId()).getSocket().getOutputStream()
                    );
//                    发送一个获取在线好友的消息
                    Message getList = new Message();
                    getList.setMsgkind(MessageKind.message_get_FriendList);
                    getList.setSender(u.getId());
                    oos.writeObject(getList);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                this.dispose();
            }else {
                JOptionPane.showMessageDialog(this,"用户名或密码错误！");
            }

        }
    }

}
