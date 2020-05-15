package com.client;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class SendFile extends Thread{

    private ServerSocket connectSocket=null;
    private Socket socket=null;
    private JFrame frame;
    private Container contentPanel;
    private JProgressBar progressbar;
    private DataInputStream dis;
    private DataOutputStream dos;
    private RandomAccessFile rad;
    private JLabel label;

	public SendFile(){
			frame=new JFrame("文件传输");
		 try {
            socket=new Socket("localhost", 8080);
			 } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
        }
    }

	public void run(){
//		是一个线程，这样也挺合理的
		System.out.println("1");

		JFileChooser fc = new JFileChooser();
		int status=fc.showOpenDialog(null);
		System.out.println("2");

  		if (status==JFileChooser.APPROVE_OPTION) {//检测到按键是“确认提交”
	  		//获得文件路径
			String path=fc.getSelectedFile().getPath();
			try {
				System.out.println("3");
//				获得输出输出流
				System.out.println(socket);
				dos=new DataOutputStream(socket.getOutputStream());
				dis=new DataInputStream(socket.getInputStream());
//				发送“ok”，先建立连接
				dos.writeUTF("ok");
				System.out.println("4");

//				新建RandomAccessFile类型的文件，path是已获取到的
				rad=new RandomAccessFile(path, "r");
//				在此路径上，新建，和要发送的文件虽然同名，但是格式后缀不同？
				File file=new File(path);
				System.out.println("5");

//				建立缓冲区
				byte[] buf=new byte[1024];
//				传送文件名
				dos.writeUTF(file.getName());
//				flush强制将缓冲区发送。其实是因为，有时候你用wirte了，但是电脑存在缓冲区，等会儿再发。和你的要求不一致
				dos.flush();
//				那，上面flush结束后，其实就是，发送了“ok”和文件名

//				等啊等，等到了对面发来的readUTF，作为rsp
				String rsp=dis.readUTF();
				System.out.println("6");

//				如果对面发来的是“ok”
				if (rsp.equals("ok")) {
//					还获取到了对面发来的Long，代表接收方那边的同名文件的大小
					  long size=dis.readLong();//读取文件已发送的大小

//					我就把我这边RandomAccessFile文件的长度给他发过去，再写个“ok”
					dos.writeLong(rad.length());
						dos.writeUTF("ok");
					dos.flush();
					System.out.println("7");

//					这个时候是要等待么？一会儿看看对面的逻辑

//					利用对面发来的size，作为偏移量，知道我要从哪开始写了
					  long offset=size;//字节偏移量

//					用我这边的文件的长度，除以1024，作为barsize
					  int barSize=(int) (rad.length()/1024);
//					  用获取到的对面的文件长度，除以1024，作为baroffset
					  int barOffset=(int)(offset/1024);

					  //传输界面
					frame.setSize(380,120);
					  contentPanel = frame.getContentPane();
					  contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
					 progressbar = new JProgressBar();//进度条

					   label=new JLabel(file.getName()+" 发送中");
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

					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					frame.setVisible(true);

					//上面这么多，就是设置了传输界面


					//从文件指定位置开始传输
					int length;
					//如果我本地的这个文件，比你的偏移大，那说明没传完，我就找到我要从哪里开始
					if (offset<rad.length()) {
							 rad.seek(offset);
						while((length=rad.read(buf))>0){
							   dos.write(buf,0,length);
							progressbar.setValue(++barOffset);
							dos.flush();
						}
					}
//					直到文件上传结束，提示完成
					label.setText(file.getName()+" 发送完成");
                }
//				关闭输入输出流，关闭文件
				dis.close();
				dos.close();
				rad.close();
			} catch (IOException e) {
                // TODO Auto-generated catch block
				label.setText(" 取消发送,连接关闭");
			}finally {
				frame.dispose();
			}

		}
    }

    class CancelActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e3){
			try {
//				监听到取消发送
				label.setText(" 取消发送,连接关闭");
				JOptionPane.showMessageDialog(frame, "取消发送给，连接关闭!", "提示：", JOptionPane.INFORMATION_MESSAGE);
				dis.close();
				dos.close();
				rad.close();
				frame.dispose();
				socket.close();
			} catch (IOException e1) {

			}
        }
    }

}