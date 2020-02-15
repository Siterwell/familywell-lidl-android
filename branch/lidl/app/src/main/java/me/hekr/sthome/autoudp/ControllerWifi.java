package me.hekr.sthome.autoudp;

import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by jishu0001 on 2016/12/20.
 */
public class ControllerWifi {
    public boolean wifiTag;
    public InetAddress targetip;
    public String deviceTid;
    public String bind;
    public String ctrlKey;
    public DatagramSocket ds;
    public boolean choose_gateway;
    public boolean switch_server_ok;

    private static ControllerWifi instance = null;
    private ControllerWifi(){

    }
    public static ControllerWifi getInstance(){
        if (instance == null) {
//            synchronized (ConnectionPojo.class) {
//                if (instance == null) {
            return instance = new ControllerWifi();
//                }
//            }
        }
        return instance;
    }
}
