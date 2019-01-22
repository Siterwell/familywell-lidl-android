package me.hekr.sthome.tools;


import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jishu0001 on 2016/11/17.
 */
public class SendCommand {
    public static final int EQUIPMENT_CONTROL = 1;
    public static final int INCREACE_EQUIPMENT = 2;
    public static final int REPLACE_EQUIPMENT = 3;
    public static final int DELETE_EQUIPMENT = 4;
    public static final int DELETE_EQUIPMENT_DETAIL = -4;
    public static final int MODIFY_EQUIPMENT_NAME = 5;
    public static final int CHOOSE_SCENE_GROUP = 6;
    public static final int CANCEL_INCREACE_EQUIPMENT = 7;
    public static final int INCREACE_SCENE = 8;
    public static final int MODIFY_SCENE = 9;
    public static final int DELETE_SCENE = 10;
    public static final int GET_DEVICE_NAME = 14;
    public static final int GET_ALL_EQUIPMENT_STATUS = 15;
    public static final int GET_ALL_SCENE_INFO = 18;
    public static final int TIME_CHECK = 21;
    public static final int INCREACE_SCENE_GROUP = 23;
    public static final int MODIFY_SCENE_GROUP = 24;
    public static final int SYN_DEVICE_STATUS = 29;
    public static final int SYN_DEVICE_NAME = 30;
    public static final int SYN_SCENE = 31;
    public static final int SCENE_HANDLE = 32;
    public static final int SCENE_GROUP_DELETE = 33;
    public static final int MODEL_SWITCH_TIMER = 34;
    public static final int SWITCH_TIMER = -34;
    public static final int MODEL_TIMER_SYN = 35;
    public static final int UPLOAD_MODEL_TIMER = 36;
    public static final int MODEL_TIMER_DEL = 37;
    public static final int SEND_TIMEZONE = 251;



    public static int Command = 0;

    private Context context;

    public SendCommand(Context context){
        this.context = context;
    }
    private String jsonToString(String code){
        JSONObject json = null;
        try {
             json = new JSONObject(code);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    /**
     * equipment control
     * 1
     * @param eqid
     * @param status2
     * @return
     */
    public String equipControl(String eqid,String status2){
        if(ConnectionPojo.getInstance().msgid < 65536){
            ConnectionPojo.getInstance().msgid ++;
        }else {
            ConnectionPojo.getInstance().msgid = 0;
        }
        int ds = EQUIPMENT_CONTROL;
        String eqid2 = eqid;
        try {
            String version = ConnectionPojo.getInstance().binversion.substring(ConnectionPojo.getInstance().binversion.lastIndexOf(".")+1);
            if(Integer.parseInt(version)>14){
                ds = EQUIPMENT_CONTROL+100;
                int a = Integer.parseInt(eqid);
                a = (((~a)+65536)^0x123);
                eqid2 = String.valueOf(a^0x1234);

            }

        }catch (Exception e){
           e.printStackTrace();
        }

        String groupCode ="{ " +
                "    \"msgId\": "+ConnectionPojo.getInstance().msgid+",  " +
                "    \"action\": \"appSend\",  " +
                "    \"params\": { " +
                "        \"devTid\": \""+ ConnectionPojo.getInstance().deviceTid+"\", " +
                "        \"ctrlKey\": \""+ ConnectionPojo.getInstance().ctrlKey+"\",  " +
                "        \"appTid\": \""+ ConnectionPojo.getInstance().IMEI+"\", " +
                "        \"data\": {\n" +
                "            \"cmdId\": "+ds+",  " +
                "            \"device_ID\": " + eqid2 + "," +
                "            \"device_status\": \""+ status2 +"\""+
                "        } " +
                "    } " +
                "}";
        return jsonToString(groupCode);
    }


    /**
     * equipment replace
     * 1
     * @param eqid
     * @return
     */
    public String equipReplace(String eqid){

        if(ConnectionPojo.getInstance().msgid < 65536){
            ConnectionPojo.getInstance().msgid ++;
        }else {
            ConnectionPojo.getInstance().msgid = 0;
        }
        int ds = REPLACE_EQUIPMENT;
        String eqid2 = eqid;
        try {
            String version = ConnectionPojo.getInstance().binversion.substring(ConnectionPojo.getInstance().binversion.lastIndexOf(".")+1);
            if(Integer.parseInt(version)>14){
                ds = REPLACE_EQUIPMENT+100;
                int a = Integer.parseInt(eqid);
                a = (((~a)+65536)^0x123);
                eqid2 = String.valueOf(a^0x1234);

            }

        }catch (Exception e){
            e.printStackTrace();
        }
        String groupCode ="{ " +
                "    \"msgId\": "+ConnectionPojo.getInstance().msgid+",  " +
                "    \"action\": \"appSend\",  " +
                "    \"params\": { " +
                "        \"devTid\": \""+ ConnectionPojo.getInstance().deviceTid+"\", " +
                "        \"ctrlKey\": \""+ ConnectionPojo.getInstance().ctrlKey+"\",  " +
                "        \"appTid\": \""+ ConnectionPojo.getInstance().IMEI+"\", " +
                "        \"data\": {\n" +
                "            \"cmdId\": "+ds+",  " +
                "            \"device_ID\": " + eqid2 +
                "        } " +
                "    } " +
                "}";
        return jsonToString(groupCode);
    }

    /**
     * increace equipment
     * 2
     * @return
     */
    public String equipIncreace(){
        if(ConnectionPojo.getInstance().msgid < 65536){
            ConnectionPojo.getInstance().msgid ++;
        }else {
            ConnectionPojo.getInstance().msgid = 0;
        }
        String msg = "{\"msgId\": "+ConnectionPojo.getInstance().msgid+"," +
                "    \"action\": \"appSend\"," +
                "    \"params\": {\n" +
                "        \"devTid\": \""+ ConnectionPojo.getInstance().deviceTid+"\", " +
                "        \"ctrlKey\": \""+ ConnectionPojo.getInstance().ctrlKey+"\"," +
                "        \"appTid\": \""+ ConnectionPojo.getInstance().IMEI+"\", " +
                "        \"data\": {" +
                "            \"cmdId\": "+INCREACE_EQUIPMENT+
                "        } " +
                "    }}";
        return jsonToString(msg);
    }
    /**
     * equipment delete
     * 4
     * @param eqid
     * @return
     */
    public String equipDelete(String eqid){
        if(ConnectionPojo.getInstance().msgid < 65536){
            ConnectionPojo.getInstance().msgid ++;
        }else {
            ConnectionPojo.getInstance().msgid = 0;
        }
        int ds = DELETE_EQUIPMENT;
        String eqid2 = eqid;
        try {
            String version = ConnectionPojo.getInstance().binversion.substring(ConnectionPojo.getInstance().binversion.lastIndexOf(".")+1);
            if(Integer.parseInt(version)>14){
                ds = DELETE_EQUIPMENT+100;
                int a = Integer.parseInt(eqid);
                a = (((~a)+65536)^0x123);
                eqid2 = String.valueOf(a^0x1234);

            }

        }catch (Exception e){
            e.printStackTrace();
        }
        String cmd =" \"cmdId\": "+ds+", \n" +
                "            \"device_ID\": "+eqid2;
        String msg = "{\"msgId\": "+ConnectionPojo.getInstance().msgid+"," +
                "    \"action\": \"appSend\"," +
                "    \"params\": {\n" +
                "        \"devTid\": \""+ ConnectionPojo.getInstance().deviceTid+"\", " +
                "        \"ctrlKey\": \""+ ConnectionPojo.getInstance().ctrlKey+"\"," +
                "        \"appTid\": \""+ ConnectionPojo.getInstance().IMEI+"\", " +
                "        \"data\": {" +
                cmd +
                "        } " +
                "    }}";
        return jsonToString(msg);
    }

    /**
     * chose scene group
     * 6
     * @param position
     * @return
     */
    public String sceneGroupChose(int position ){

        if(ConnectionPojo.getInstance().msgid < 65536){
            ConnectionPojo.getInstance().msgid ++;
        }else {
            ConnectionPojo.getInstance().msgid = 0;
        }
        int ds = CHOOSE_SCENE_GROUP;
        int eqid2 = position;
        try {
            String version = ConnectionPojo.getInstance().binversion.substring(ConnectionPojo.getInstance().binversion.lastIndexOf(".")+1);
            if(Integer.parseInt(version)>14){
                ds = CHOOSE_SCENE_GROUP+100;
                int a = (((~position)+65536)^0x123);
                eqid2 = a^0x1234;

            }

        }catch (Exception e){
            e.printStackTrace();
        }
        String groupCode ="{" +
                "    \"msgId\": "+ConnectionPojo.getInstance().msgid+", " +
                "    \"action\": \"appSend\", " +
                "    \"params\": {" +
                "        \"devTid\": \""+ ConnectionPojo.getInstance().deviceTid+"\", " +
                "        \"ctrlKey\": \""+ ConnectionPojo.getInstance().ctrlKey+"\", " +
                "        \"appTid\": \""+ ConnectionPojo.getInstance().IMEI+"\", " +
                "        \"data\": {" +
                "            \"cmdId\": "+ds+", " +
                "            \"scene_type\": " + eqid2 +
                "        }" +
                "    }" +
                "}";
        return jsonToString(groupCode);
    }

    /**
     * increace scene
     * 8
     * @param deCode
     * @return
     */
    public String increaceScene(String deCode){
        if(ConnectionPojo.getInstance().msgid < 65536){
            ConnectionPojo.getInstance().msgid ++;
        }else {
            ConnectionPojo.getInstance().msgid = 0;
        }
        String groupCode ="{" +
                "    \"msgId\": "+ConnectionPojo.getInstance().msgid+", " +
                "    \"action\": \"appSend\", " +
                "    \"params\": {" +
                "        \"devTid\": \""+ ConnectionPojo.getInstance().deviceTid+"\", " +
                "        \"ctrlKey\": \"" + ConnectionPojo.getInstance().ctrlKey + "\", " +
                "        \"appTid\": \""+ ConnectionPojo.getInstance().IMEI+"\", " +
                "        \"data\": {" +
                "            \"cmdId\": 8, " +
                "            \"scene_type\": 0, " +
                "            \"scene_content\": " + deCode +
                "        }" +
                "    }" +
                "}";
        return jsonToString(groupCode);
    }

    /**
     * modify scene
     * 9
     * @param deCode
     * @return
     */
    public String modifyScene(String deCode){
        if(ConnectionPojo.getInstance().msgid < 65536){
            ConnectionPojo.getInstance().msgid ++;
        }else {
            ConnectionPojo.getInstance().msgid = 0;
        }
        String groupCode ="{" +
                "    \"msgId\": "+ConnectionPojo.getInstance().msgid+", " +
                "    \"action\": \"appSend\", " +
                "    \"params\": {" +
                "        \"devTid\": \""+ ConnectionPojo.getInstance().deviceTid+"\", " +
                "        \"ctrlKey\": \"" + ConnectionPojo.getInstance().ctrlKey + "\", " +
                "        \"appTid\": \""+ ConnectionPojo.getInstance().IMEI+"\", " +
                "        \"data\": {" +
                "            \"cmdId\": 9, " +
                "            \"scene_type\": 0, " +
                "            \"scene_content\": " + deCode +
                "        }" +
                "    }" +
                "}";
        return jsonToString(groupCode);
    }
    /**
     * delete scene
     * 10
     * @param id
     * @return
     */
    public String deleteScene(String id){
        if(ConnectionPojo.getInstance().msgid < 65536){
            ConnectionPojo.getInstance().msgid ++;
        }else {
            ConnectionPojo.getInstance().msgid = 0;
        }
        int ds = DELETE_SCENE;
        String eqid2 = id;
        try {
            String version = ConnectionPojo.getInstance().binversion.substring(ConnectionPojo.getInstance().binversion.lastIndexOf(".")+1);
            if(Integer.parseInt(version)>14){
                ds = DELETE_SCENE+100;
                int a = Integer.parseInt(id);
                a = (((~a)+65536)^0x123);
                eqid2 = String.valueOf(a^0x1234);

            }

        }catch (Exception e){
            e.printStackTrace();
        }
        String groupCode ="{" +
                "    \"msgId\": "+ConnectionPojo.getInstance().msgid+", " +
                "    \"action\": \"appSend\", " +
                "    \"params\": {" +
                "        \"devTid\": \""+ ConnectionPojo.getInstance().deviceTid+"\", " +
                "        \"ctrlKey\": \""+ ConnectionPojo.getInstance().ctrlKey+"\", " +
                "        \"appTid\": \""+ ConnectionPojo.getInstance().IMEI+"\", " +
                "        \"data\": {" +
                "            \"cmdId\": "+ds+", " +
                "            \"scene_type\": 0, " +
                "            \"scene_ID\": " + eqid2 +
                "        }" +
                "    }" +
                "}";
        return jsonToString(groupCode);
    }


    /**
     * check time
     * 21
     * @param timeCode
     * @return
     */
    public String timeCheck(String timeCode){
        if(ConnectionPojo.getInstance().msgid < 65536){
            ConnectionPojo.getInstance().msgid ++;
        }else {
            ConnectionPojo.getInstance().msgid = 0;
        }
        String groupCode ="{" +
                "    \"msgId\": "+ConnectionPojo.getInstance().msgid+", " +
                "    \"action\": \"appSend\", " +
                "    \"params\": {" +
                "        \"devTid\": \""+ ConnectionPojo.getInstance().deviceTid+"\", " +
                "        \"ctrlKey\": \""+ ConnectionPojo.getInstance().ctrlKey+"\", " +
                "        \"appTid\": \""+ ConnectionPojo.getInstance().IMEI+"\", " +
                "        \"data\": {" +
                "            \"cmdId\": "+TIME_CHECK+", " +
                "            \"time\": \"" + timeCode +"\""+
                "        }" +
                "    }" +
                "}";
        return jsonToString(groupCode);
    }


    /**
     * check time
     * 251
     * @param timezoneCode
     * @return
     */
    public String timeZoneCheck(int timezoneCode){
        if(ConnectionPojo.getInstance().msgid < 65536){
            ConnectionPojo.getInstance().msgid ++;
        }else {
            ConnectionPojo.getInstance().msgid = 0;
        }
        String groupCode ="{" +
                "    \"msgId\": "+ConnectionPojo.getInstance().msgid+", " +
                "    \"action\": \"appSend\", " +
                "    \"params\": {" +
                "        \"devTid\": \""+ ConnectionPojo.getInstance().deviceTid+"\", " +
                "        \"ctrlKey\": \""+ ConnectionPojo.getInstance().ctrlKey+"\", " +
                "        \"appTid\": \""+ ConnectionPojo.getInstance().IMEI+"\", " +
                "        \"data\": {" +
                "            \"cmdId\": 251, " +
                "            \"TimeZone\": " + timezoneCode +
                "        }" +
                "    }" +
                "}";
        return jsonToString(groupCode);
    }

    /**
     * increace scene group
     * 23
     * @param fullCode
     * @return
     */
    public String increaceSceneGroup(String fullCode){
        if(ConnectionPojo.getInstance().msgid < 65536){
            ConnectionPojo.getInstance().msgid ++;
        }else {
            ConnectionPojo.getInstance().msgid = 0;
        }
        String groupCode ="{" +
                "    \"msgId\": "+ConnectionPojo.getInstance().msgid+", " +
                "    \"action\": \"appSend\", " +
                "    \"params\": {" +
                "        \"devTid\": \""+ ConnectionPojo.getInstance().deviceTid+"\", " +
                "        \"ctrlKey\": \""+ ConnectionPojo.getInstance().ctrlKey+"\", " +
                "        \"appTid\": \""+ ConnectionPojo.getInstance().IMEI+"\", " +
                "        \"data\": {" +
                "            \"cmdId\": 23, " +
                "            \"scene_content\": \"" + fullCode +"\""+
                "        }" +
                "    }" +
                "}";
        return jsonToString(groupCode);
    }

    /**
     * modify scene group
     * 24
     * @param deCode
     * @return
     */
    public String modifySceneGroup(String deCode){
        if(ConnectionPojo.getInstance().msgid < 65536){
            ConnectionPojo.getInstance().msgid ++;
        }else {
            ConnectionPojo.getInstance().msgid = 0;
        }
        String groupCode ="{" +
                "    \"msgId\": "+ConnectionPojo.getInstance().msgid+", " +
                "    \"action\": \"appSend\", " +
                "    \"params\": {" +
                "        \"devTid\": \""+ ConnectionPojo.getInstance().deviceTid+"\", " +
                "        \"ctrlKey\": \""+ ConnectionPojo.getInstance().ctrlKey+"\", " +
                "        \"appTid\": \""+ ConnectionPojo.getInstance().IMEI+"\", " +
                "        \"data\": {" +
                "            \"cmdId\": 24, " +
                "            \"scene_content\": \"" + deCode +"\""+
                "        }" +
                "    }" +
                "}";
        return jsonToString(groupCode);
    }

    /**
     * modify equipment name
     * 5
     * @param deviceid
     * @param newname
     * @return
     */
    public String modifyEquipmentName(String deviceid, String newname){
        if(ConnectionPojo.getInstance().msgid < 65536){
            ConnectionPojo.getInstance().msgid ++;
        }else {
            ConnectionPojo.getInstance().msgid = 0;
        }
        int ds = MODIFY_EQUIPMENT_NAME;
        String eqid2 = deviceid;
        try {
            String version = ConnectionPojo.getInstance().binversion.substring(ConnectionPojo.getInstance().binversion.lastIndexOf(".")+1);
            if(Integer.parseInt(version)>14){
                ds = MODIFY_EQUIPMENT_NAME+100;
                int a = Integer.parseInt(deviceid);
                a = (((~a)+65536)^0x123);
                eqid2 = String.valueOf(a^0x1234);

            }

        }catch (Exception e){
            e.printStackTrace();
        }
        String groupCode ="{" +
                "    \"msgId\": "+ConnectionPojo.getInstance().msgid+", " +
                "    \"action\": \"appSend\", " +
                "    \"params\": {" +
                "        \"devTid\": \""+ ConnectionPojo.getInstance().deviceTid+"\", " +
                "        \"ctrlKey\": \"" + ConnectionPojo.getInstance().ctrlKey + "\", " +
                "        \"appTid\": \""+ ConnectionPojo.getInstance().IMEI+"\", " +
                "        \"data\": {" +
                "            \"cmdId\": "+ds+", " +
                "            \"device_ID\": "+eqid2+", " +
                "            \"device_name\": \"" + newname +"\""+
                "        }" +
                "    }" +
                "}";
        return jsonToString(groupCode);
    }

    /**
     * get equipment name
     * 14
     * @return
     */
    public String getEquipmentName(){
        if(ConnectionPojo.getInstance().msgid < 65536){
            ConnectionPojo.getInstance().msgid ++;
        }else {
            ConnectionPojo.getInstance().msgid = 0;
        }
        String groupCode ="{" +
                "    \"msgId\": "+ConnectionPojo.getInstance().msgid+", " +
                "    \"action\": \"appSend\", " +
                "    \"params\": {" +
                "        \"devTid\": \""+ ConnectionPojo.getInstance().deviceTid+"\", " +
                "        \"ctrlKey\": \"" + ConnectionPojo.getInstance().ctrlKey + "\", " +
                "        \"appTid\": \""+ ConnectionPojo.getInstance().IMEI+"\", " +
                "        \"data\": {" +
                "            \"cmdId\": 14, " +
                "            \"device_ID\": 0"+
                "        }" +
                "    }" +
                "}";
        return jsonToString(groupCode);
    }

    /**
     * syn device status
     *29
     * @param statusCRC
     * @return
     */
    public String synDeviceStatus(String statusCRC){
        if(ConnectionPojo.getInstance().msgid < 65536){
            ConnectionPojo.getInstance().msgid ++;
        }else {
            ConnectionPojo.getInstance().msgid = 0;
        }
        String groupCode ="{" +
                "    \"msgId\": "+ConnectionPojo.getInstance().msgid+", " +
                "    \"action\": \"appSend\", " +
                "    \"params\": {" +
                "        \"devTid\": \""+ ConnectionPojo.getInstance().deviceTid+"\", " +
                "        \"ctrlKey\": \""+ ConnectionPojo.getInstance().ctrlKey+"\", " +
                "        \"appTid\": \""+ ConnectionPojo.getInstance().IMEI+"\", " +
                "        \"data\": {" +
                "            \"cmdId\": 29, " +
                "            \"device_status\": \"" + statusCRC +"\""+
                "        }" +
                "    }" +
                "}";
        return jsonToString(groupCode);
    }
    /**
     * syn device name
     *30
     * @param nameCRC
     * @return
     */
    public String synDeviceName(String nameCRC){
        if(ConnectionPojo.getInstance().msgid < 65536){
            ConnectionPojo.getInstance().msgid ++;
        }else {
            ConnectionPojo.getInstance().msgid = 0;
        }
        String groupCode ="{" +
                "    \"msgId\": "+ConnectionPojo.getInstance().msgid+", " +
                "    \"action\": \"appSend\", " +
                "    \"params\": {" +
                "        \"devTid\": \""+ ConnectionPojo.getInstance().deviceTid+"\", " +
                "        \"ctrlKey\": \""+ ConnectionPojo.getInstance().ctrlKey+"\", " +
                "        \"appTid\": \""+ ConnectionPojo.getInstance().IMEI+"\", " +
                "        \"data\": {" +
                "            \"cmdId\": 30, " +
                "            \"answer_content\": \"" + nameCRC +"\""+
                "        }" +
                "    }" +
                "}";
        return jsonToString(groupCode);
    }
    /**
     * syc scene
     *31
     * @param groupId,sceneGCRC,sceneCRC
     * @return
     */
    public String synScene(String groupId,String sceneGCRC,String sceneCRC){
        if(ConnectionPojo.getInstance().msgid < 65536){
            ConnectionPojo.getInstance().msgid ++;
        }else {
            ConnectionPojo.getInstance().msgid = 0;
        }
        int ds = SYN_SCENE;
        String eqid2 = groupId;
        try {
            String version = ConnectionPojo.getInstance().binversion.substring(ConnectionPojo.getInstance().binversion.lastIndexOf(".")+1);
            if(Integer.parseInt(version)>14){
                ds = SYN_SCENE+100;
                int a = Integer.parseInt(groupId);
                a = (((~a)+65536)^0x123);
                eqid2 = String.valueOf(a^0x1234);

            }

        }catch (Exception e){
            e.printStackTrace();
        }
        String groupCode ="{" +
                "    \"msgId\": "+ConnectionPojo.getInstance().msgid+", " +
                "    \"action\": \"appSend\", " +
                "    \"params\": {" +
                "        \"devTid\": \""+ ConnectionPojo.getInstance().deviceTid+"\", " +
                "        \"ctrlKey\": \""+ ConnectionPojo.getInstance().ctrlKey+"\", " +
                "        \"appTid\": \""+ ConnectionPojo.getInstance().IMEI+"\", " +
                "        \"data\": {" +
                "            \"cmdId\": "+ds+", " +
                "            \"sence_group\": " + eqid2 +","+
                "            \"answer_content\": \"" + sceneGCRC +"\","+
                "            \"scene_content\": \"" + sceneCRC +"\""+
                "        }" +
                "    }" +
                "}";
        return jsonToString(groupCode);
    }

    /**
     * Scene handle
     *31
     * @param mid
     * @return
     */
    public String sceneHandle(String mid){
        if(ConnectionPojo.getInstance().msgid < 65536){
            ConnectionPojo.getInstance().msgid ++;
        }else {
            ConnectionPojo.getInstance().msgid = 0;
        }
        int ds = SCENE_HANDLE;
        String eqid2 = mid;
        try {
            String version = ConnectionPojo.getInstance().binversion.substring(ConnectionPojo.getInstance().binversion.lastIndexOf(".")+1);
            if(Integer.parseInt(version)>14){
                ds = SCENE_HANDLE+100;
                int a = Integer.parseInt(mid);
                a = (((~a)+65536)^0x123);
                eqid2 = String.valueOf(a^0x1234);

            }

        }catch (Exception e){
            e.printStackTrace();
        }
        String groupCode ="{" +
                "    \"msgId\": "+ConnectionPojo.getInstance().msgid+", " +
                "    \"action\": \"appSend\", " +
                "    \"params\": {" +
                "        \"devTid\": \""+ ConnectionPojo.getInstance().deviceTid+"\", " +
                "        \"ctrlKey\": \""+ ConnectionPojo.getInstance().ctrlKey+"\", " +
                "        \"appTid\": \""+ ConnectionPojo.getInstance().IMEI+"\", " +
                "        \"data\": {" +
                "            \"cmdId\": "+ds+", " +
                "            \"indexed\": " + eqid2 +
                "        }" +
                "    }" +
                "}";
        return jsonToString(groupCode);
    }

    /**
     * Scene handle
     *7
     * @return
     */
    public String cancelEquipIncreace(){
        if(ConnectionPojo.getInstance().msgid < 65536){
            ConnectionPojo.getInstance().msgid ++;
        }else {
            ConnectionPojo.getInstance().msgid = 0;
        }
        String groupCode ="{" +
                "    \"msgId\": "+ConnectionPojo.getInstance().msgid+", " +
                "    \"action\": \"appSend\", " +
                "    \"params\": {" +
                "        \"devTid\": \""+ ConnectionPojo.getInstance().deviceTid+"\", " +
                "        \"ctrlKey\": \""+ ConnectionPojo.getInstance().ctrlKey+"\", " +
                "        \"appTid\": \""+ ConnectionPojo.getInstance().IMEI+"\", " +
                "        \"data\": {" +
                "            \"cmdId\": 7" +
                "        }" +
                "    }" +
                "}";
        return jsonToString(groupCode);
    }

    /**
     * delete scene group
     *33
     * @param groupId,sceneGCRC,sceneCRC
     * @return
     */
    public String deleteSceneGroup(String groupId){
        if(ConnectionPojo.getInstance().msgid < 65536){
            ConnectionPojo.getInstance().msgid ++;
        }else {
            ConnectionPojo.getInstance().msgid = 0;
        }
        int ds = SCENE_GROUP_DELETE;
        String eqid2 = groupId;
        try {
            String version = ConnectionPojo.getInstance().binversion.substring(ConnectionPojo.getInstance().binversion.lastIndexOf(".")+1);
            if(Integer.parseInt(version)>14){
                ds = SCENE_GROUP_DELETE+100;
                int a = Integer.parseInt(groupId);
                a = (((~a)+65536)^0x123);
                eqid2 = String.valueOf(a^0x1234);

            }

        }catch (Exception e){
            e.printStackTrace();
        }
        String groupCode ="{" +
                "    \"msgId\": "+ConnectionPojo.getInstance().msgid+", " +
                "    \"action\": \"appSend\", " +
                "    \"params\": {" +
                "        \"devTid\": \""+ ConnectionPojo.getInstance().deviceTid+"\", " +
                "        \"ctrlKey\": \""+ ConnectionPojo.getInstance().ctrlKey+"\", " +
                "        \"appTid\": \""+ ConnectionPojo.getInstance().IMEI+"\", " +
                "        \"data\": {" +
                "            \"cmdId\": "+ds+", " +
                "            \"sence_group\": " + eqid2 +
                "        }" +
                "    }" +
                "}";
        return jsonToString(groupCode);
    }

    /**
     * increace a scene group timer
     *34
     * @param code
     * @return
     */
    public String increaceGroupTimer(String code){
        if(ConnectionPojo.getInstance().msgid < 65536){
            ConnectionPojo.getInstance().msgid ++;
        }else {
            ConnectionPojo.getInstance().msgid = 0;
        }
        String groupCode ="{" +
                "    \"msgId\": "+ConnectionPojo.getInstance().msgid+", " +
                "    \"action\": \"appSend\", " +
                "    \"params\": {" +
                "        \"devTid\": \""+ ConnectionPojo.getInstance().deviceTid+"\", " +
                "        \"ctrlKey\": \""+ ConnectionPojo.getInstance().ctrlKey+"\", " +
                "        \"appTid\": \""+ ConnectionPojo.getInstance().IMEI+"\", " +
                "        \"data\": {" +
                "            \"cmdId\": 34, " +
                "            \"time\": \"" + code +
                "\"}" +
                "    }" +
                "}";
        return jsonToString(groupCode);
    }

    /**
     * scene group timer syn
     *35
     * @param code
     * @return
     */
    public String synGroupTimer(String code){
        if(ConnectionPojo.getInstance().msgid < 65536){
            ConnectionPojo.getInstance().msgid ++;
        }else {
            ConnectionPojo.getInstance().msgid = 0;
        }
        String groupCode ="{" +
                "    \"msgId\": "+ConnectionPojo.getInstance().msgid+", " +
                "    \"action\": \"appSend\", " +
                "    \"params\": {" +
                "        \"devTid\": \""+ ConnectionPojo.getInstance().deviceTid+"\", " +
                "        \"ctrlKey\": \""+ ConnectionPojo.getInstance().ctrlKey+"\", " +
                "        \"appTid\": \""+ ConnectionPojo.getInstance().IMEI+"\", " +
                "        \"data\": {" +
                "            \"cmdId\": 35, " +
                "            \"time\": \"" + code +
                "\"}" +
                "    }" +
                "}";
        return jsonToString(groupCode);
    }

    /**
     * delete scene group timer
     *37
     * @param code
     * @return
     */
    public String deleteGroupTimer(int code){
        if(ConnectionPojo.getInstance().msgid < 65536){
            ConnectionPojo.getInstance().msgid ++;
        }else {
            ConnectionPojo.getInstance().msgid = 0;
        }
        int ds = MODEL_TIMER_DEL;
        int eqid2 = code;
        try {
            String version = ConnectionPojo.getInstance().binversion.substring(ConnectionPojo.getInstance().binversion.lastIndexOf(".")+1);
            if(Integer.parseInt(version)>14){
                ds = MODEL_TIMER_DEL+100;
                int a = (((~code)+65536)^0x123);
                eqid2 = a^0x1234;

            }

        }catch (Exception e){
            e.printStackTrace();
        }
        String groupCode ="{" +
                "    \"msgId\": "+ConnectionPojo.getInstance().msgid+", " +
                "    \"action\": \"appSend\", " +
                "    \"params\": {" +
                "        \"devTid\": \""+ ConnectionPojo.getInstance().deviceTid+"\", " +
                "        \"ctrlKey\": \""+ ConnectionPojo.getInstance().ctrlKey+"\", " +
                "        \"appTid\": \""+ ConnectionPojo.getInstance().IMEI+"\", " +
                "        \"data\": {" +
                "            \"cmdId\": "+ds+", " +
                "            \"device_ID\": " + eqid2 +
                "        }" +
                "    }" +
                "}";
        return jsonToString(groupCode);
    }


    /**
     * udp应答
     * 251
     * @param
     * @return
     */
    public String appAnswerOk(){
        String Code ="{" +
                "  \"answer\": "+"\"APP_answer_OK\""+
                "}";
        return jsonToString(Code);
    }

    public static void clearCommnad(){
        Command = 0;
    }
}
