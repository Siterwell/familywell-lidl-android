package me.hekr.sthome.service;

import android.content.Context;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.hekr.sthome.autoudp.ControllerWifi;

/**
 * Created by jishu0001 on 2016/12/24.
 */
public class SiterwellUtil {
    private static final String TAG = "SiterwellUtil";
    private Context context;
    private static ExecutorService executorService;

    public SiterwellUtil(Context context){
        this.context = context;
        synchronized (context){
            if(executorService == null)
                executorService = Executors.newFixedThreadPool(7);
        }
    }

    // TODO: [RYAN] don't call this until data encryption
    public void sendData(final String code){
            UDPSendData udpSendData = new UDPSendData(ControllerWifi.getInstance().ds, ControllerWifi.getInstance().targetip,code);
            executorService.execute(udpSendData);

    }

    public void sendData(final byte[] code){
        UDPSendData udpSendData = new UDPSendData(ControllerWifi.getInstance().ds, ControllerWifi.getInstance().targetip,code);
        executorService.execute(udpSendData);

    }
}
