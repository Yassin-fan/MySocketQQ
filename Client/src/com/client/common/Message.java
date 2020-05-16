package com.client.common;

public class Message implements java.io.Serializable {



    public String getMsgkind() {
        return msgkind;
    }

    public void setMsgkind(String msgkind) {
        this.msgkind = msgkind;
    }

    private String msgkind;
    private String sendTime;
    private String sender;
    private String receiver;
    private String fname;//文件名
    private String text;

//    public byte[] getByteslist() {
//        return byteslist;
//    }
//
//    public void setByteslist(byte[] byteslist) {
//        this.byteslist = byteslist;
//    }
//
//    private byte[] byteslist;

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }



    public String getFile_inside() {
        return file_inside;
    }

    public void setFile_inside(String file_inside) {
        this.file_inside = file_inside;
    }

    private String file_inside;//文件内容

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }



}
