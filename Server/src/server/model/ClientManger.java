package server.model;

import java.util.HashMap;
import java.util.Iterator;

public class ClientManger {

    public static HashMap hashMap = new HashMap<String , ConnectWithClient>();

    public void addThread(String clientid, ConnectWithClient connectWithClient){
        hashMap.put(clientid, connectWithClient);
    }

    public static ConnectWithClient getThread(String clientid){
        return (ConnectWithClient) hashMap.get(clientid);
    }

    public static String getOnlie(){
        Iterator iterator = hashMap.keySet().iterator();
        String onlinelist = "";
        while (iterator.hasNext()){
            onlinelist = onlinelist + iterator.next().toString() + " ";
        }
        return onlinelist;

    }


}
