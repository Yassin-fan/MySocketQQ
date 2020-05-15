package com.client.model;

import com.client.common.User;

public class ClientLocal {

    public boolean checkIdAndPwd(User u){
        System.out.println("进入local");
//        创建一个CToS，即创建socket，再验证能否登陆
        ClientToServer clientToServer = new ClientToServer();
        System.out.println("得到验证结果：" + clientToServer.SendLoginInfoToSever(u));
        return clientToServer.SendLoginInfoToSever(u);
    }

}
