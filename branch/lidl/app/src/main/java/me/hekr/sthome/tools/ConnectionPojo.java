package me.hekr.sthome.tools;

/**
 * Created by jishu0001 on 2016/11/23.
 */
public class ConnectionPojo {
    public String IMEI;
    public String deviceTid;
    public String bind;
    public String ctrlKey;
    public String propubkey;
    public String domain;
    public String binversion;
    public int index_home = 0;
    public int folderid = 0;
    public int open_app = 0;
    public int msgid = 0;

    private static ConnectionPojo instance = null;
    private ConnectionPojo (){

    }
    public static ConnectionPojo getInstance(){
        if (instance == null) {
//            synchronized (ConnectionPojo.class) {
//                if (instance == null) {
                    return instance = new ConnectionPojo();
//                }
//            }
        }
        return instance;
    }
}
