package me.hekr.sthome.service;

import java.net.DatagramSocket;

/**
 * Created by gc-0001 on 2017/1/16.
 */
public class SocketContant {

    public DatagramSocket ds;

    private static SocketContant instance = null;
    private SocketContant (){

    }
    public static SocketContant getInstance(){
        if (instance == null) {
//            synchronized (ConnectionPojo.class) {
//                if (instance == null) {
            return instance = new SocketContant();
//                }
//            }
        }
        return instance;
    }
}
