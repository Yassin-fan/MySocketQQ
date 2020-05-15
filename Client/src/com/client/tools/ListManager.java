package com.client.tools;

import com.client.view.Chat;
import com.client.view.List;

import java.util.HashMap;

public class ListManager {

    private static HashMap hashMap = new HashMap<String, List>();

    public static void addList(String MyId, List list){
        hashMap.put(MyId, list);
    }

    public static List getList(String MyId){
        return (List) hashMap.get(MyId);
    }
}
