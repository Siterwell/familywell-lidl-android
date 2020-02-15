package me.hekr.sthome.service;

import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import me.hekr.sthome.autoudp.ControllerWifi;

/**
 * Created by jishu0001 on 2016/12/27.
 */
public class SeartchWifiDataForSwitchServer {
    private final static String TAG = SeartchWifiDataForSwitchServer.class.getName();
    private static int conut;
    private static Timer timer;
    private TimerTask task;
    private static MyTaskCallback ds;
    public SeartchWifiDataForSwitchServer(MyTaskCallback ds2 ){
        ControllerWifi.getInstance().switch_server_ok = false;
        conut = 0;
        timer = new Timer();
        task = new MyTimerTask();
        this.ds =ds2;
    }

    private static class MyTimerTask extends TimerTask{

        @Override
        public void run() {

            try {
                Log.i(TAG," re - seatch  ===="+ conut);
                if(ControllerWifi.getInstance().switch_server_ok){
                    timer.cancel();
                    timer = null;
                    ds.operationSuccess();
                }else{
                    conut++;
                    ds.doReSendAction();
                    Log.i(TAG,"resend data   times ===" + conut);
                    if(conut >= 3){
                        if(timer == null){
                            conut = 0;
                            ControllerWifi.getInstance().switch_server_ok = false;
                            ds.operationFailed();
                        }else{
                            timer.cancel();
                            conut = 0;
                            timer = null;
                            ControllerWifi.getInstance().switch_server_ok = false;
                            ds.operationFailed();
                        }

                    }
                }
            }catch (Exception e){
                e.printStackTrace();
                conut = 0;
                timer = null;
                ControllerWifi.getInstance().switch_server_ok = false;
                ds.operationFailed();
            }

        }
    }

    public void startReSend(){

        timer.schedule(task,0,3000);

    }

    public  interface MyTaskCallback  {
        void operationFailed();

         void operationSuccess();

         void doReSendAction();
    }


}
