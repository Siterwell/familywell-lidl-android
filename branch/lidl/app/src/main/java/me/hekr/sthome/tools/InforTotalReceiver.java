package me.hekr.sthome.tools;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.litesuits.android.log.Log;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import me.hekr.sthome.autoudp.ControllerWifi;
import me.hekr.sthome.event.AlertEvent;
import me.hekr.sthome.event.STEvent;
import me.hekr.sthome.event.TokenTimeoutEvent;
import me.hekr.sthome.http.SiterConstantsUtil;
import me.hekr.sthome.model.ResolveData;
import me.hekr.sthome.model.modeldb.DeviceDAO;
import me.hekr.sthome.model.modeldb.EquipDAO;
import me.hekr.sthome.service.NetWorkUtils;
import me.hekr.sthome.service.SiterService;

/**
 * Created by jishu0001 on 2016/11/18.
 */
public abstract class InforTotalReceiver extends BroadcastReceiver {
    private static final String TAG = "InforTotalReceiver";
    private SendOtherData sendOtherData;
    private ResolveData resolveData;


    public InforTotalReceiver(Context context,ResolveData resolveData){

        this.resolveData = resolveData;
        sendOtherData = new SendOtherData(context);
    }
    public void onReceive(Context context, Intent intent) {
        JSONObject json;
        JSONObject params,data;
        String msg = "";
        if(intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)){
            int d = NetWorkUtils.getNetWorkType(context);
            Log.i(TAG,"网络环境切换成:"+d);


            STEvent stEvent = new STEvent();
            stEvent.setServiceevent(7);
            stEvent.setNettype(d);
            if(d==4){
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                Log.i(TAG,"获取的当前WIFI信号:"+wifiInfo.getSSID());
                stEvent.setSsid(wifiInfo.getSSID());
            }
            EventBus.getDefault().post(stEvent);
            return;
        }
        else if(intent.getAction().equals(SiterConstantsUtil.ActionStrUtil.ACTION_WS_DATA_RECEIVE)){
            msg =intent.getStringExtra(SiterConstantsUtil.HEKR_WS_PAYLOAD);
            try {
                json = new JSONObject(msg);
                String action  = json.getString("action");
                params = json.getJSONObject("params");
                String devTids = params.getString("devTid");
                //若是内网则不需要解析当前网关收到的外网数据包，减轻数据接收负担
                if(ControllerWifi.getInstance().wifiTag && ConnectionPojo.getInstance().deviceTid.equals(devTids)
                        && !"devLogin".equals(action) && !"devLogout".equals(action)){
                    return;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }else if(intent.getAction().equals(SiterService.UDP_BROADCAST)){

            msg =intent.getStringExtra("message");
            if(msg.indexOf("ST_answer_OK")==-1){
                sendOtherData.dataGetOk();
            }

        }
        Log.i(TAG,"需解析的数据:"+msg);


        try {
                json = new JSONObject(msg);
                if("devSend".equals(json.getString("action"))){
                    int msgid = json.getInt("msgId");
                    params = json.getJSONObject("params");
                    String devTids = params.getString("devTid");
                    data = params.getJSONObject("data");
                    if (data.getInt("cmdId") == 11) {
                        if (data.getInt("answer_yes_or_no") == 2) {

                            if(SendCommand.Command!=SendCommand.INCREACE_EQUIPMENT && SendCommand.Command!=SendCommand.REPLACE_EQUIPMENT){
                                STEvent stEvent = new STEvent();
                                stEvent.setEvent(SendCommand.Command);
                                EventBus.getDefault().post(stEvent);
                            }

                        }
                    }
                    else if(data.getInt("cmdId") == 19 || data.getInt("cmdId") == 119){
                        String eqid;
                        if(data.getInt("cmdId")==19){
                            eqid=  data.getString("device_ID");
                        }else{
                            eqid =String .valueOf(ByteUtil.getDescryption(data.getInt("device_ID"),msgid));
                        }
                        String eqtype = data.getString("device_name");
                        String eq_status = data.getString("device_status");
                        EquipDAO ED = new EquipDAO(context);
                        int i = ED.isIdExists(eqid,devTids);
                        if(i<1){
                            if(SendCommand.Command==SendCommand.INCREACE_EQUIPMENT && devTids.equals(ConnectionPojo.getInstance().deviceTid)){

                                STEvent stEvent = new STEvent();
                                stEvent.setCurrent_deviceid(devTids);
                                stEvent.setEq_id(eqid);
                                stEvent.setEq_type(eqtype);
                                stEvent.setEq_status(eq_status);
                                stEvent.setEvent(SendCommand.INCREACE_EQUIPMENT);
                                EventBus.getDefault().post(stEvent);
                            }
                        }else{
                            if(SendCommand.Command==SendCommand.REPLACE_EQUIPMENT && devTids.equals(ConnectionPojo.getInstance().deviceTid)){
                                STEvent stEvent = new STEvent();
                                stEvent.setEvent(SendCommand.REPLACE_EQUIPMENT);
                                EventBus.getDefault().post(stEvent);
                            }else if(SendCommand.Command == SendCommand.INCREACE_EQUIPMENT && devTids.equals(ConnectionPojo.getInstance().deviceTid)){
                                STEvent stEvent = new STEvent();
                                stEvent.setCurrent_deviceid(devTids);
                                stEvent.setEq_id(eqid);
                                stEvent.setEq_type(eqtype);
                                stEvent.setEq_status(eq_status);
                                stEvent.setEvent(SendCommand.INCREACE_EQUIPMENT);
                                EventBus.getDefault().post(stEvent);

                            }
                        }

                        resolveData.updateEq(devTids,eqid,eqtype,eq_status);

                        if(ConnectionPojo.getInstance().deviceTid.equals(devTids)) {
                            STEvent stEvent = new STEvent();
                            stEvent.setCurrent_deviceid(devTids);
                            stEvent.setEq_id(eqid);
                            stEvent.setEq_type(eqtype);
                            stEvent.setEq_status(eq_status);
                            stEvent.setRefreshevent(5);
                            EventBus.getDefault().post(stEvent);
                        }
                    }else if(data.getInt("cmdId") == 22){
                        sendOtherData.timeCheck();
                    }else if(data.getInt("cmdId")==17){
                        String info = data.getString("answer_content");
                        resolveData.resolveEqName(info,devTids);
                        if(ConnectionPojo.getInstance().deviceTid.equals(devTids)) {
                            STEvent stEvent = new STEvent();
                            stEvent.setRefreshevent(5);
                            EventBus.getDefault().post(stEvent);
                        }
                    }else if(data.getInt("cmdId")==28 || data.getInt("cmdId")==128){//当前情景组数据
                        int now_group;
                        if(data.getInt("cmdId")==28){
                            now_group=  data.getInt("sence_group");
                        }else{
                            now_group =ByteUtil.getDescryption(data.getInt("sence_group"),msgid);
                        }
                        LOG.D(TAG, "[SCENE debug] onReceive > now_group = " + now_group + ", devTids = " + devTids);
                        resolveData.setNow_mode(now_group, devTids);
                    }else if(data.getInt("cmdId")==26 ||  data.getInt("cmdId")==126){//情景组数据
                        int group;
                        if(data.getInt("cmdId")==26){
                            group=  data.getInt("sence_group");
                        }else{
                            group =ByteUtil.getDescryption(data.getInt("sence_group"),msgid);
                        }
                        String answertent = data.getString("answer_content");
                        resolveData.putGroupMode(group,answertent);

                    }else if(data.getInt("cmdId")==27 || data.getInt("cmdId")==127){//情景数据

                        int scene_mid;
                        if(data.getInt("cmdId")==27){
                            scene_mid=  data.getInt("scene_type");
                        }else{
                            scene_mid =ByteUtil.getDescryption(data.getInt("scene_type"),msgid);
                        }
                        String ds = data.getString("scene_content");
                        if("OVER".equals(ds)){
                            resolveData.setScenes(devTids);
                        }else {
                            resolveData.putScene(scene_mid,ds);
                        }

                    }else if(data.getInt("cmdId")==25){
                        String content = data.getString("answer_content");
                        AlertEvent alertEvent =new AlertEvent();
                        alertEvent.setDeviceid(devTids);
                        alertEvent.setContent(content);
                        EventBus.getDefault().post(alertEvent);
                    }else if(data.getInt("cmdId")==36){
                        String info = data.getString("time");
                        resolveData.resolveTimer(info,devTids);
                    }

                }else if("appLoginResp".equals(json.getString("action"))){
                    int code = json.getInt("code");
                    if(code == 1400002){
                        TokenTimeoutEvent tokenTimeoutEvent = new TokenTimeoutEvent();
                        tokenTimeoutEvent.setType(1);
                        EventBus.getDefault().post(tokenTimeoutEvent);
                    }
                }else if("devLogout".equals(json.getString("action"))){
                    params = json.getJSONObject("params");
                    String devTids = params.getString("devTid");
                    if(ConnectionPojo.getInstance().deviceTid!=null && ConnectionPojo.getInstance().deviceTid.equals(devTids)){
                        DeviceDAO deviceDAO = new DeviceDAO(context);
                        deviceDAO.updateDeivceStatus(devTids,0);
                        STEvent stEvent = new STEvent();
                        stEvent.setRefreshevent(1);
                        EventBus.getDefault().post(stEvent);
                    }
                }else if("devLogin".equals(json.getString("action"))){
                    params = json.getJSONObject("params");
                    String devTids = params.getString("devTid");
                    if(ConnectionPojo.getInstance().deviceTid!=null && ConnectionPojo.getInstance().deviceTid.equals(devTids)){
                        DeviceDAO deviceDAO = new DeviceDAO(context);
                        deviceDAO.updateDeivceStatus(devTids,1);
                        STEvent stEvent = new STEvent();
                        stEvent.setRefreshevent(1);
                        EventBus.getDefault().post(stEvent);
                    }
                }
            }catch (JSONException e){
                Log.i(TAG,"data format is error");
            }catch (Exception e) {
                e.printStackTrace();
            }


    }



}
