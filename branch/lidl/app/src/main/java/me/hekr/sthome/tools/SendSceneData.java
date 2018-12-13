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
public abstract class SendSceneData {
    private static final String TAG = "SendSceneData";
    private Context context;
    private SendCommand sc;
    private boolean wifiTag;

    public SendSceneData(Context context){
        this.context =context;
        sc = new SendCommand(context);
    }
    protected abstract void sendEquipmentDataFailed();

    protected abstract void sendEquipmentDataSuccess();


    /**
     * send code
     * @param groupCode
     */
    private void sendAction(String groupCode){
        ControllerWifi controllerWifi = ControllerWifi.getInstance();
        wifiTag = controllerWifi.wifiTag;
        Log.i(TAG,"===send tag==="+wifiTag);
        if(wifiTag){
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

    /**
     * increace scene
     * @param deCode
     */
    public void increaceScene(final String deCode){
        sendAction(sc.increaceScene(deCode));
    }

    /**
     * modify scene
     * @param deCode
     */
    public void modifyScene(final String deCode){
        sendAction(sc.modifyScene(deCode));
    }

    /**
     * delete scene
     * @param id
     */
    public void deleteScene(final String id){
        sendAction(sc.deleteScene(id));
    }

    /**
     * syn get scene information
     * @param groupId
     * @param sceneGCRC
     * @param sceneCRC
     */
    public void synGetSceneInformation(String groupId,String sceneGCRC,String sceneCRC){
        sendAction(sc.synScene(groupId,sceneGCRC,sceneCRC));
    }

    /**
     * handleScene
     * @param mid
     */
    public void handleScene(final String mid){
        sendAction(sc.sceneHandle(mid));
    }
}
