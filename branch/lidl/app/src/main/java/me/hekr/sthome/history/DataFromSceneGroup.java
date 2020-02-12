package me.hekr.sthome.history;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import me.hekr.sthome.crc.CoderUtils;
import me.hekr.sthome.model.modelbean.SceneBean;
import me.hekr.sthome.model.modelbean.ShortcutBean;
import me.hekr.sthome.model.modelbean.SysModelBean;
import me.hekr.sthome.model.modeldb.SceneDAO;
import me.hekr.sthome.model.modeldb.ShortcutDAO;
import me.hekr.sthome.model.modeldb.SysmodelDAO;
import me.hekr.sthome.tools.ByteUtil;
import me.hekr.sthome.tools.ConnectionPojo;
import me.hekr.sthome.tools.SendSceneGroupData;

/**
 * Created by ST-020111 on 2017/4/2.
 */

public class DataFromSceneGroup {
    private static final String TAG = DataFromSceneGroup.class.getName();
    private String deviceTid;
    private Context context;
    private SendSceneGroupData ssgd;
    private List<SysModelBean> sceneList;
    public DataFromSceneGroup(Context context){
        this.context = context;
        sceneList = new ArrayList<>();
        deviceTid = ConnectionPojo.getInstance().deviceTid;
        getAllSceneId();
    }

    public List<SysModelBean> getAllSceneId(){
        SysmodelDAO SMD = new SysmodelDAO(context);
        sceneList = SMD.findAllSys(deviceTid);
        ssgd = new SendSceneGroupData(context) {
            @Override
            protected void sendEquipmentDataFailed() {

            }

            @Override
            protected void sendEquipmentDataSuccess() {

            }
        };
        return sceneList;
    }

    public void doSendSynCode(){
        for (SysModelBean smb : sceneList){
            Log.i(TAG,"for get syn data"+makeSceneCode(smb));
            ssgd.modifySceneGroup(makeSceneCode(smb));
        }
    }


    /**
     * get scene group from local sql
     * @return
     */
    public String makeSceneCode(SysModelBean smb){
        byte scene_default  = 0;
        ShortcutDAO shortcutDAO = new ShortcutDAO(context);
        int sceneGroupId = Integer.parseInt(smb.getSid());
        int length = 0;
        String name = null;
        if( sceneGroupId > 2){
            name = smb.getModleName();
        }else{
            switch (sceneGroupId){
                case 0:
                    name = "Home";
                    break;
                case 1:
                    name = "Away";
                    break;
                case 2:
                    name = "Sleep";
                    break;
                default:break;
            }
        }

        length+=2;//the total num length

        String id2 = smb.getSid();
        length += 1;//the scene id

//        String name3 ="00000000"+
//                "00000000" +
//                "00000000" +
//                "00000000" +
//                "00000000" +
//                "00000000" +
//                "00000000" +
//                "00000000";
//        length+=32;//the scene name length

        String btnNum = "";
        List<ShortcutBean> shortcutBeans = shortcutDAO.findShorcutsBysid(smb.getDeviceid(),smb.getSid());

        if (Integer.toHexString(shortcutBeans.size()).length()<2){  //new mid
            btnNum = "0"+Integer.toHexString(shortcutBeans.size());
        }else{
            btnNum =Integer.toHexString(shortcutBeans.size());
        }
        length+=1;//button num

        String shortcut = "";

        for (int j = 0;j<shortcutBeans.size();j++){



            String eqid = "";
            String dessid = "";
            if (Integer.toHexString(Integer.parseInt(shortcutBeans.get(j).getEqid())).length()<2){  //new mid
                eqid = "000"+Integer.toHexString(Integer.parseInt(shortcutBeans.get(j).getEqid()));
            }else{
                eqid ="00"+Integer.toHexString(Integer.parseInt(shortcutBeans.get(j).getEqid()));
            }

            if (Integer.toHexString(Integer.parseInt(shortcutBeans.get(j).getDes_sid())).length()<2){  //new mid
                dessid = "0"+Integer.toHexString(Integer.parseInt(shortcutBeans.get(j).getDes_sid()))+"000000";
            }else{
                dessid =Integer.toHexString(Integer.parseInt(shortcutBeans.get(j).getDes_sid()))+"000000";
            }

            shortcut+=(eqid+dessid+"00");
            length += 7;
        }


        String color = smb.getColor();
        if(color.indexOf("F")==-1){
            color = "F" + smb.getSid();
        }
        length+=1;//button num


        //self-define scene num
        length+=1;
        int scene =0;
        //scene id
        String sceneCode ="";
        SceneDAO SED = new SceneDAO(context);
        List<SceneBean> sceneLists = SED.findScenceListBySid(smb.getSid(),deviceTid);
        if(sceneLists != null && sceneLists.size()>0){
            for(int i = 0; i<sceneLists.size();i++){
                if(Integer.parseInt(sceneLists.get(i).getMid())<=128){
                    scene++;
                    length++;
                    String singleCode ="";
                    if (Integer.toHexString(Integer.parseInt(sceneLists.get(i).getMid())).length()<2){  //new mid
                        singleCode = "0"+Integer.toHexString(Integer.parseInt(sceneLists.get(i).getMid()));
                    }else{
                        singleCode =Integer.toHexString(Integer.parseInt(sceneLists.get(i).getMid()));
                    }
                    sceneCode += singleCode;
                }else {
                    if("129".equals(sceneLists.get(i).getMid())){
                        scene_default = (byte)(scene_default|0x81);
                    }else if("130".equals(sceneLists.get(i).getMid())){
                        scene_default = (byte)(scene_default|0x82);
                    }else if("131".equals(sceneLists.get(i).getMid())){
                        scene_default = (byte)(scene_default|0x84);
                    }
                }

            }
        }else {
            length+=0;
            sceneCode = "";
        }
        scene_default = (byte)(scene_default|0x80);
        length+=1;

        String oooo ="0";
        if(Integer.toHexString(length+32).length() < 4){
            for (int i = 0 ; i<4-Integer.toHexString(length+32).length()-1;i++ ){
                oooo += 0;
            }
            oooo += Integer.toHexString(length+32);
        }else{
            oooo = Integer.toHexString(length+32);
        }

        String oo = "0";
        if(Integer.toHexString(scene).length()<2){
            oo = oo + Integer.toHexString(scene);
        }else{
            oo = Integer.toHexString(scene);
        }
        String ds = CoderUtils.getAscii(name);
        String str_default_scene = ByteUtil.convertByte2HexString(scene_default);
        String fullCode = oooo +"0"+id2 + ds + btnNum + oo +shortcut+ sceneCode + color+str_default_scene;
        String deCode = fullCode;
        String crc = ByteUtil.CRCmakerCharAndCode(fullCode);
        Log.i(TAG,"fullCode:"+fullCode);
//        sendMsg(fullCode + crc);
        Log.i(TAG, " sysdetail CRC +++++++++++++++"+ crc);
        return deCode + crc;
    }
}
