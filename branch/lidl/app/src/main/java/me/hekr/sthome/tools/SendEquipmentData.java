package me.hekr.sthome.tools;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import me.hekr.sdk.Hekr;
import me.hekr.sdk.inter.HekrMsgCallback;
import me.hekr.sthome.autoudp.ControllerWifi;
import me.hekr.sthome.service.SiterwellUtil;

/**
 * Created by jishu0001 on 2016/11/16.
 */
public abstract class SendEquipmentData {
    private static final String TAG = SendEquipmentData.class.getSimpleName();
    private Context context;
    private SendCommand sc;
    private boolean wifiTag;

    public SendEquipmentData(Context context){
        this.context =context;
//        wifiTag = myApplication.isWifiTag();
        sc = new SendCommand(context);
    }

    /**
     * send code
     * @param groupCode
     */
    private void sendAction(String groupCode){
        LOG.I(TAG,"sendAction ====send groupCode=== " + groupCode);
        LOG.I(TAG,"sendAction ====domain=== " + ConnectionPojo.getInstance().domain);

        ControllerWifi controllerWifi = ControllerWifi.getInstance();
        wifiTag = controllerWifi.wifiTag;
        LOG.I(TAG,"====send tag=== "+wifiTag);
        if(wifiTag){
            LOG.I(TAG,"sendAction ==== UDP");
            new SiterwellUtil(context).sendData(groupCode);
        }else {
            LOG.I(TAG,"sendAction ==== TCP");
            try {

                Hekr.getHekrClient().sendMessage(new JSONObject(groupCode), new HekrMsgCallback() {
                    @Override
                    public void onReceived(String msg) {
                        LOG.I(TAG,"sendAction > onReceived > " + msg);
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
    /**
     * send control command
     * @param eqid
     * @param status2
     */
    public void sendEquipmentCommand(final String eqid, final String status2){//send equipment detail
        sendAction(sc.equipControl(eqid,status2));
    }

    /**
     * sendGateWaySilence
     */
    public void sendGateWaySilence(){//send equipment detail
        sendAction(sc.equipControl("0","00000000"));
    }

    protected abstract void sendEquipmentDataFailed();

    protected abstract void sendEquipmentDataSuccess();

    /**
     * increace equipment command
     */
    public void increaceEquipment(){
        sendAction(sc.equipIncreace());
    }

    /**
     * delete equipment
     * @param eqid
     */
    public void deleteEquipment(final String eqid){
        sendAction(sc.equipDelete(eqid));
    }


    /**
     * delete equipment
     * @param eqid
     */
    public void replaceEquipment(final String eqid){
        sendAction(sc.equipReplace(eqid));
    }



    /**
     * delete equipment
     * @param eqid
     */
    public void modifyEquipmentName(final String eqid,final String newname){
        sendAction(sc.modifyEquipmentName(eqid,newname));
    }


    public void getDeviceNameInfo(){
        sendAction(sc.getEquipmentName());
    }

    /**
     * syn get device status
     * @param deviceCRC
     */
    public void synGetDeviceStatus(String deviceCRC){
        sendAction(sc.synDeviceStatus(deviceCRC));
    }

    /**
     * syn get device name
     * @param deviceNameCRC
     */
    public void synGetDeviceName(String deviceNameCRC){
        sendAction(sc.synDeviceName(deviceNameCRC));
    }

    /**
     * cancel increase equipment
     */
    public void cancelIncreaseEq(){
        sendAction(sc.cancelEquipIncreace());
    }
}
