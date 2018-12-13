package me.hekr.sthome.tools;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import me.hekr.sdk.Hekr;
import me.hekr.sdk.inter.HekrMsgCallback;
import me.hekr.sthome.autoudp.ControllerWifi;
import me.hekr.sthome.service.SiterwellUtil;

/**
 * Created by jishu0001 on 2016/11/17.
 */
public abstract class SendSceneGroupData {
    private Context context;
    private SendCommand sc;
    private static final String TAG = "SendSceneGroupData";

    public SendSceneGroupData(Context context){
        this.context =context;
        sc = new SendCommand(context);
    }
    /**
     * send code
     * @param groupCode
     */
    private void sendAction(String groupCode){
        Log.i(TAG,"===send tag==="+ControllerWifi.getInstance().wifiTag);
        if(ControllerWifi.getInstance().wifiTag){
            new SiterwellUtil(context).sendData(groupCode);
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
    protected abstract void sendEquipmentDataFailed();

    protected abstract void sendEquipmentDataSuccess();


    /**
     * scene group chose
     * @param position
     */
    public void sceneGroupChose(final int position){
        sendAction(sc.sceneGroupChose(position));
    }

    /**
     * increace scene group
     * @param fullCode
     */
    public void increaceSceneGroup(final String fullCode){

        sendAction(sc.increaceSceneGroup(fullCode));
    }

    /**
     * modify scene group
     * @param deCode
     */
    public void modifySceneGroup(final String deCode){
        sendAction(sc.modifySceneGroup(deCode));
    }

    /**
     * delete scene group
     * @param sceneGroupid
     */
    public void deleteSceneGroup(final String sceneGroupid){
        sendAction(sc.deleteSceneGroup(sceneGroupid));
    }

}
