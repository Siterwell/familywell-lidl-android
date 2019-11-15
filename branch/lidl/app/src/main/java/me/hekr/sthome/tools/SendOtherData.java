package me.hekr.sthome.tools;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import me.hekr.sdk.Hekr;
import me.hekr.sdk.inter.HekrMsgCallback;
import me.hekr.sthome.autoudp.ControllerWifi;
import me.hekr.sthome.service.SiterwellUtil;

/**
 * Created by jishu0001 on 2016/11/17.
 */
public class SendOtherData {
    private static final String TAG ="SendOtherData";
    private Context context;
    private boolean wifiTag;
    private SendCommand sc;

    public SendOtherData(Context context){
        this.context =context;
//        wifiTag = myApplication.isWifiTag();
        sc = new SendCommand(context);
    }
    /**
     * send code
     * @param groupCode
     */
    private void sendAction(String groupCode){
        ControllerWifi controllerWifi = ControllerWifi.getInstance();
        wifiTag = controllerWifi.wifiTag;
        Log.i(TAG,"===send tag==="+wifiTag);
        if(wifiTag){
            if(ConnectionPojo.getInstance().encryption){
                Log.i(TAG,"Udp before encryption:"+groupCode);
                byte[] encode = ByteUtil.getAllEncryption(groupCode);
                new SiterwellUtil(context).sendData(encode);
            }else {
                new SiterwellUtil(context).sendData(groupCode);
            }
        }else {
            try {
                Hekr.getHekrClient().sendMessage(new JSONObject(groupCode), new HekrMsgCallback() {
                    @Override
                    public void onReceived(String msg) {

                    }

                    @Override
                    public void onTimeout() {

                    }

                    @Override
                    public void onError(int errorCode, String message) {
                        LOG.E(TAG,"sendAction > onError > " + message);
                    }
                }, ConnectionPojo.getInstance().domain);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    private String nowData(){
        Calendar c = Calendar.getInstance();
        int weekday = c.get(Calendar.DAY_OF_WEEK);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);
        int sec = c.get(Calendar.SECOND);

        String wd = "";
        if(weekday == 1){
            weekday = 7;
            wd = "0" + weekday;
        }else{
            weekday -= 1;
            wd = "0" + weekday;
        }

        String hour1 ="";
        String hour2 = Integer.toHexString(hour);
        if(hour2.length()<2){
            hour1 ="0"+hour2;
        }else{
            hour1 = hour2;
        }

        String min1 ="";
        String min2 =Integer.toHexString(min);
        if(min2.length()<2){
            min1 ="0"+min2;
        }else{
            min1 = min2;
        }
        String sec1 ="";
        String sec2 =Integer.toHexString(sec);
        if(sec2.length()<2){
            sec1 ="0"+sec2;
        }else{
            sec1 =sec2;
        }

        String timeCode = wd+hour1+min1+sec1;

        return timeCode;
    }

    /**
     * time check
     *
     */
    public void timeCheck(){
//        myApplication.setFaceback(false);
//        ConnectionPojo connectionPojo = ConnectionPojo.getInstance();
//        connectionPojo.faceBack = false;
//        sendAction(sc.timeCheck(nowData()));
//        try {
//            MsgUtil.sendMsg(context, deviceTid, new JSONObject(sc.timeCheck(nowData())), new DataReceiverListener() {
//                @Override
//                public void onReceiveSuccess(String msg) {
//                    //接收返回命令
//                }
//                @Override
//                public void onReceiveTimeout() {
//                    //命令接收超时
//                }
//            },false);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        sendAction(sc.timeCheck(nowData()));
    }

    public void timeZoneCheck(int timezone){
        new SiterwellUtil(context).sendData(sc.timeZoneCheck(timezone));
    }


    /**
     * faceback
     */
    public void dataGetOk(){
       sendAction("APP_answer_OK");
    }

    /**
     * methodname:addTimerModel
     * 作者：Henry on 2017/5/16 10:06
     * 邮箱：xuejunju_4595@qq.com
     * 参数:timer
     * 返回:
     */
    public void addTimerModel(final String timer){
//        myApplication.setFaceback(false);
        sendAction(sc.increaceGroupTimer(timer));
    }

    /**
     * methodname:deleteTimerModel
     * 作者：Henry on 2017/5/16 10:06
     * 邮箱：xuejunju_4595@qq.com
     * 参数:timer
     * 返回:
     */
    public void deleteTimerModel(final int timer){
//        myApplication.setFaceback(false);
        sendAction(sc.deleteGroupTimer(timer));
    }

    /**
     * methodname:deleteTimerModel
     * 作者：Henry on 2017/5/16 10:06
     * 邮箱：xuejunju_4595@qq.com
     * 参数:timer
     * 返回:
     */
    public void syncTimerModel(final String timer){
//        myApplication.setFaceback(false);
        sendAction(sc.synGroupTimer(timer));
    }
}
