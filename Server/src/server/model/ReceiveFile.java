package server.model;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ReceiveFile extends Thread{

    private ServerSocket connectSocket=null;
    private Socket socket=null;
    private JFrame frame;
    private Container contentPanel;
    private JProgressBar progressbar;
    private DataInputStream dis;
    private DataOutputStream dos;
    private RandomAccessFile rad;
    private JLabel label;

    public ReceiveFile(){
        frame=new JFrame("接收文件");
        try {
//		 	一初始化，就在等待连接socket
            connectSocket=new ServerSocket(1012);//发送方和接收方的端口必须一致
            socket=connectSocket.accept();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void run(){
        try {
            //是一个线程，这样也挺合理的
//			连接成功，获得io流
            System.out.println("1");
            dis=new DataInputStream(socket.getInputStream());
            dos=new DataOutputStream(socket.getOutputStream());
//			读取io流的UTF字符，这里是对应发送方的第一次发送，应该是发了个“ok”
            dis.readUTF();
            System.out.println("2");
//			显示提示框
            int permit=JOptionPane.showConfirmDialog(frame, "是否接收文件","文件传输请求：", JOptionPane.YES_NO_OPTION);
            System.out.println("22");
//			确认接收
            if (permit==JOptionPane.YES_OPTION) {
                System.out.println("3");
//				获得发来的文件名
                String filename=dis.readUTF();
//				回传一个ok
                dos.writeUTF("ok");
                System.out.println("4");
                dos.flush();
//				新建文件，名字就是发来的名字？每次都要新建？那岂不是会清空呢
                File file=new File(filename+".temp");
                System.out.println("5");

//				哦，这个不是RandomAccessFile文件，是对这个文件进行Random
                rad=new RandomAccessFile(filename+".temp", "rw");
                System.out.println("6");

                //获得文件大小
                long size=0;
//				如果文件已存在，获取现有大小
                if(file.exists()&& file.isFile()){
                    size=file.length();
                }
                System.out.println("7");

                //发送现有的大小
                dos.writeLong(size);
                System.out.println("8");
                dos.flush();

//				读取Long，也就是发送方发来的，他那边的这个文件的大小
                long allSize=dis.readLong();
                String rsp=dis.readUTF();

//				用来设置进度条的
                int barSize=(int)(allSize/1024);
                int barOffset=(int)(size/1024);

                //传输界面
                frame.setSize(300,120);
                contentPanel =frame.getContentPane();
                contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
                progressbar = new JProgressBar();//进度条

                label=new JLabel(filename+" 接收中");
                contentPanel.add(label);

                progressbar.setOrientation(JProgressBar.HORIZONTAL);
                progressbar.setMinimum(0);
                progressbar.setMaximum(barSize);
                progressbar.setValue(barOffset);
                progressbar.setStringPainted(true);
                progressbar.setPreferredSize(new Dimension(150, 20));
                progressbar.setBorderPainted(true);
                progressbar.setBackground(Color.pink);

                JButton cancel=new JButton("取消");

                JPanel barPanel=new JPanel();
                barPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

                barPanel.add(progressbar);
                barPanel.add(cancel);

                contentPanel.add(barPanel);

                cancel.addActionListener(new CancelActionListener());

                frame.setDefaultCloseOperation(
                        JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
//			    上面这些都是设置界面的，给服务器传输的时候不需要的


                //接收文件
                if (rsp.equals("ok")) {
                    rad.seek(size);
                    int length;
                    byte[] buf=new byte[1024];
                    while((length=dis.read(buf, 0, buf.length))!=-1){
                        rad.write(buf,0,length);
                        progressbar.setValue(++barOffset);
                    }
                    System.out.println("FileReceive end...");
                }

                label.setText(filename+" 结束接收");


                dis.close();
                dos.close();
                rad.close();
                frame.dispose();

                //文件重命名
                if (barOffset>=barSize) {
                    file.renameTo(new File(filename));
                }
            }else{
                dis.close();
                dos.close();
                frame.dispose();
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            label.setText(" 已取消接收，连接关闭！");
        }finally {
            frame.dispose();
        }
    }

    class CancelActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e){
            try {
                dis.close();
                dos.close();
                rad.close();
                JOptionPane.showMessageDialog(frame, "已取消接收，连接关闭！", "提示：", JOptionPane.INFORMATION_MESSAGE);
                label.setText(" 取消接收,连接关闭");
            } catch (IOException e1) {

            }
        }
    }

}
