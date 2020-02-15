package me.hekr.sthome.service;

import android.text.TextUtils;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import me.hekr.sthome.autoudp.ControllerWifi;

/**
 * Created by jishu0001 on 2016/12/27.
 */
public class SeartchWifiDataForConfig {
    private final static String TAG = SeartchWifiDataForConfig.class.getName();
    private static int conut;
    private static Timer timer;
    private TimerTask task;
    private static MyTaskCallback ds;
    public SeartchWifiDataForConfig(MyTaskCallback ds2 ){
        ControllerWifi.getInstance().deviceTid = null;
        ControllerWifi.getInstance().bind = null;
        ControllerWifi.getInstance().ctrlKey = null;

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
                if(!TextUtils.isEmpty(ControllerWifi.getInstance().deviceTid) && !TextUtils.isEmpty(ControllerWifi.getInstance().bind)){
                    timer.cancel();
                    timer = null;
                    ds.operationSuccess();
                }else{
                    conut++;
                    ds.doReSendAction();
                    Log.i(TAG,"resend data   times ===" + conut);
                    if(conut >= 20){
                        if(timer == null){
                            conut = 0;
                            ds.operationFailed();
                        }else{
                            timer.cancel();
                            conut = 0;
                            timer = null;
                            ds.operationFailed();
                        }

                    }
                }
            }catch (Exception e){
                e.printStackTrace();
                conut = 0;
                timer = null;
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
