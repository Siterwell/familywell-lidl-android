package me.hekr.sthome.crc;


import android.content.Context;
import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import me.hekr.sthome.DragFolderwidget.ApplicationInfo;
import me.hekr.sthome.model.ResolveTimer;
import me.hekr.sthome.model.modelbean.EquipmentBean;
import me.hekr.sthome.model.modelbean.SceneBean;
import me.hekr.sthome.model.modelbean.ShortcutBean;
import me.hekr.sthome.model.modelbean.SysModelBean;
import me.hekr.sthome.model.modelbean.TimerGatewayBean;
import me.hekr.sthome.model.modeldb.EquipDAO;
import me.hekr.sthome.model.modeldb.SceneDAO;
import me.hekr.sthome.model.modeldb.ShortcutDAO;
import me.hekr.sthome.model.modeldb.SysmodelDAO;
import me.hekr.sthome.model.modeldb.TimerDAO;
import me.hekr.sthome.tools.ByteUtil;
import me.hekr.sthome.tools.LOG;

/**
 * Created by jishu0001 on 2017/2/9.
 */

public class CoderUtils {
    private static final String TAG = CoderUtils.class.getName();


    /*
    @method getStringFromAscii
    @autor Administrator
    @time 2017/6/30 9:51
    @email xuejunju_4595@qq.com
    从ascii 码转成GBK编码的String类型变量，若格式不对则返回NUll
    */
    public static String getStringFromAscii(String input){



        try {
            if(input.length()!=32){
                return "";
            }
            byte[]a = ByteUtil.hexStr2Bytes(input);
            String name  = new String(a,"GBK");
            if(name.indexOf("$")==-1){
                return "";
            }
            int index = -1;
            if(name.indexOf("@")!=-1){
                index = name.lastIndexOf("@");
            }

            return  name.substring(index + 1, name.indexOf("$"));

        }catch (Exception e){
            e.printStackTrace();
            return "";
        }


    }


    /**
     * the scene name to code
     * @param input
     * @return
     */

    public static String getAscii(String input){
        int countf = 0;

        try {
            countf = 15 - input.getBytes("GBK").length;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        StringBuffer buffer = new StringBuffer();
        for(int i=0;i<countf;i++){
            buffer.append("@");
        }
        String newname = buffer.toString()+input+"$";

        byte[] nameBt = new byte[16];
        try {
            nameBt = newname.getBytes("GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String ds = "";
        for(int i=0;i<nameBt.length;i++){
            String str = ByteUtil.convertByte2HexString(nameBt[i]);
            ds+=str;
        }

        return ds;
    }


    public static String getEncrypt(String input){
        String ds = "";
        try {

        byte[] nameBt = new byte[input.getBytes("GBK").length];
        try {
            nameBt = input.getBytes("GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        for(int i=0;i<nameBt.length;i++){
            nameBt[i] = (byte)(0x24 ^nameBt[i]);
            String str = ByteUtil.convertByte2HexString(nameBt[i]);
            ds+=str;
        }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return ds;
    }


    public static String getDecrypt(String input){



        try {
            byte[]a = ByteUtil.hexStr2Bytes(input);

            for(int i=0;i < a.length;i++){
                a[i] = (byte)(0x24 ^a[i]);
            }

            String name  = new String(a,"GBK");

            return  name;

        }catch (Exception e){
            e.printStackTrace();
            return "";
        }


    }

    /**
     * syn get device status
     */
    public static String getEqCRC(Context context,String deviceid) {

            EquipDAO ED = new EquipDAO(context);
            List<ApplicationInfo> allEquipmentInformation = ED.findAllEq(deviceid);
            if (allEquipmentInformation.size() > 0) {
                Collections.sort(allEquipmentInformation, new Comparator() {

                    @Override
                    public int compare(Object o, Object t1) {
                        EquipmentBean a1 = (EquipmentBean) o;
                        EquipmentBean a2 = (EquipmentBean) t1;
                        return Integer.parseInt(a1.getEqid()) - Integer.parseInt(a2.getEqid());
                    }
                });


                int listLength = Integer.parseInt(allEquipmentInformation.get(allEquipmentInformation.size() - 1).getEqid());

                List<String> eqid = new ArrayList<>();
                for (EquipmentBean e : allEquipmentInformation) {
                    eqid.add(e.getEqid());
                }
                String statusCRC = "";
                for (int i = 1; i <= listLength; i++) {
                    if (eqid.contains(String.valueOf(i))) {
                        String crc = ByteUtil.CRCmakerChar(allEquipmentInformation.get(eqid.indexOf(String.valueOf(i))).getState());
                        statusCRC += crc;
                    } else {
                        statusCRC += "0000";
                    }
                }
                LOG.I(TAG, "ceshi crc " + statusCRC);

                String oooo = "0", num = "";
                if (Integer.toHexString(listLength*2+2).length() < 4) {
                    for (int i = 0; i < 4 - Integer.toHexString(listLength*2+2).length() - 1; i++) {
                        oooo += 0;
                    }
                    num = oooo + Integer.toHexString(listLength*2+2);
                } else {
                    num = Integer.toHexString(listLength*2+2);
                }

                return (num + statusCRC);
            } else {
                LOG.I(TAG, "there is no equipment exist");
                return "00020000";
            }

    }


    /**
     * syn get device name
     */
    public static String getEqNameCRC(Context context,String deviceid) {
           EquipDAO ED = new EquipDAO(context);
            List<ApplicationInfo> allEquipmentInformation = ED.findAllEq(deviceid);
            if (allEquipmentInformation.size() > 0) {
                Collections.sort(allEquipmentInformation, new Comparator() {

                    @Override
                    public int compare(Object o, Object t1) {
                        EquipmentBean a1 = (EquipmentBean) o;
                        EquipmentBean a2 = (EquipmentBean) t1;
                        return Integer.parseInt(a1.getEqid()) - Integer.parseInt(a2.getEqid());
                    }
                });
                int listLength = Integer.parseInt(allEquipmentInformation.get(allEquipmentInformation.size() - 1).getEqid());
                List<String> eqid = new ArrayList<>();
                for (EquipmentBean e : allEquipmentInformation) {
                    eqid.add(e.getEqid());
                }
                String statusCRC = "";
                for (int i = 1; i <= listLength; i++) {
                    if (eqid.contains(String.valueOf(i))) {
                        if(!TextUtils.isEmpty(allEquipmentInformation.get(eqid.indexOf(String.valueOf(i))).getEquipmentName())){
                            ByteUtil byteUtil = new ByteUtil();
                            String asciiName = CoderUtils.getAscii(allEquipmentInformation.get(eqid.indexOf(String.valueOf(i))).getEquipmentName());
                            String crc = byteUtil.CRCmaker(asciiName);
                            statusCRC += crc;
                        }else{
                            statusCRC += "0000";
                        }

                    } else {
                        statusCRC += "0000";
                    }
                }
                LOG.I(TAG, "ceshi crc " + statusCRC);

                String oooo = "0", num = "";
                if (Integer.toHexString(listLength*2+2).length() < 4) {
                    for (int i = 0; i < 4 - Integer.toHexString(listLength*2+2).length() - 1; i++) {
                        oooo += 0;
                    }
                    num = oooo + Integer.toHexString(listLength*2+2);
                } else {
                    num = Integer.toHexString(listLength*2+2);
                }

                return (num + statusCRC);
            } else {
                return("00020000");
            }


    }




    /**
     * syn get scene list crc
     * @param context
     * @return
     */
    public static String getSceneCRC(Context context,String deviceid){

        SceneDAO SD = new SceneDAO(context);
        List<SceneBean> sceneList = SD.findAllAmWithoutDefault(deviceid);
        if(sceneList != null && sceneList.size()>0) {

            String oooo = "0", num = "";
            int codeLength = 2;
            Collections.sort(sceneList, new Comparator() {

                @Override
                public int compare(Object o, Object t1) {
                    SceneBean a1 = (SceneBean) o;
                    SceneBean a2 = (SceneBean) t1;
                    return Integer.parseInt(a1.getMid()) - Integer.parseInt(a2.getMid());
                }
            });
            int listLength = Integer.parseInt(sceneList.get(sceneList.size() - 1).getMid());
            List<String> eqid = new ArrayList<>();
            for (SceneBean e : sceneList) {
                eqid.add(e.getMid());
            }
            String sceneCRC = "";
            for (int i = 1; i <= listLength; i++) {
                codeLength += 2;
                if (eqid.contains(String.valueOf(i))) {
                    ByteUtil byteUtil = new ByteUtil();
                    String ds = sceneList.get(eqid.indexOf(String.valueOf(i))).getCode();

                    String crc = byteUtil.CRCmakerCharAndCode(ds);
                    sceneCRC += crc;
                } else {
                    sceneCRC += "0000";
                }
            }
            LOG.I(TAG, "ceshi crc " + sceneCRC);

            int totalLength = codeLength ;
            Integer.toHexString(totalLength);
            if (Integer.toHexString(totalLength).length() < 4) {
                for (int i = 0; i < 4 - Integer.toHexString(totalLength).length()-1; i++) {
                    oooo += 0;
                }
                num = oooo + Integer.toHexString(totalLength);
            } else {
                num = Integer.toHexString(codeLength);
            }
            return num + sceneCRC;
        }else {
            return "00040000";
        }

    }

    /**
     * get
     * @param context
     * @return
     */
    public static String getSceneGroupCRC(Context context,String deviceid){
        String getSceneGroupCRC="",num="";

        SysmodelDAO SD = new SysmodelDAO(context);
        ShortcutDAO shortcutDAO = new ShortcutDAO(context);
        List<SysModelBean> slist = SD.findAllSys(deviceid);

        if(slist.size()>0){
            int codeLength = 2;
            List<String> sid = new ArrayList<>();
            for(SysModelBean e : slist){
                sid.add(e.getSid());
            }
            for(int i = 0 ; i <Integer.parseInt(slist.get(slist.size()-1).getSid())+1 ; i++) {//for here come with "0" , used slist.size()
                codeLength += 2;
                if (sid.contains(Integer.toString(i))) {
                    SceneDAO SED = new SceneDAO(context);
                    List<SceneBean> sysSceneList = SED.findScenceListBySid(String.valueOf(sid.indexOf(String.valueOf(i))),deviceid);
                    SysModelBean sysModelBean = SD.findBySid(Integer.toString(i),deviceid);
                    String name = sysModelBean.getModleName();
                    int length = 0;

                    length = 0;
                    length += 2;//the total num length

                    String id2 = String.valueOf(sysModelBean.getSid());
                    length += 1;//the scene id

                    String btnNum = "";
                    List<ShortcutBean> shortcutBeans = shortcutDAO.findShorcutsBysid(deviceid,sysModelBean.getSid());

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

                    String color = sysModelBean.getColor();
                    length+=1;
                    //self-define scene num
                    length += 1;
                    int scene = 0;
                    //scene id
                    String sceneCode = "";
                    for (int j = 0; j < sysSceneList.size(); j++) {
                        scene++;
                        length++;
                        String singleCode = "";

                        if (Integer.toHexString(Integer.parseInt(sysSceneList.get(j).getMid())).length() < 2) {  //new mid
                            singleCode = "0" + Integer.toHexString(Integer.parseInt(sysSceneList.get(j).getMid()));
                        } else {
                            singleCode = Integer.toHexString(Integer.parseInt(sysSceneList.get(j).getMid()));
                        }
                        sceneCode += singleCode;
                    }
                    String oooo = "0";
                    if (Integer.toHexString(length + 32).length() < 4) {
                        for (int k = 0; k < 4 - Integer.toHexString(length + 32).length() - 1; k++) {
                            oooo += 0;
                        }
                        oooo += Integer.toHexString(length + 32);
                    } else {
                        oooo = Integer.toHexString(length + 32);
                    }

                    String oo = "0";
                    if (Integer.toHexString(scene).length() < 2) {
                        oo = oo + Integer.toHexString(scene);
                    } else {
                        oo = Integer.toHexString(scene);
                    }

                    String ds = CoderUtils.getAscii(name);

                    String fullCode = oooo + "0" + id2 + ds + btnNum + oo + shortcut + sceneCode + color;
                    getSceneGroupCRC += ByteUtil.CRCmakerCharAndCode(fullCode);

                }else {
                    getSceneGroupCRC += "0000";
                }
            }
            String oooo = "0";
            int totalLength = codeLength ;
            Integer.toHexString(totalLength);
            if (Integer.toHexString(totalLength).length() < 4) {
                for (int i = 0; i < 4 - Integer.toHexString(totalLength).length()-1; i++) {
                    oooo += 0;
                }
                num = oooo + Integer.toHexString(totalLength);
            } else {
                num = Integer.toHexString(codeLength);
            }
            return num + getSceneGroupCRC;
        }else{
            return "00040000";
        }

    }

    /**
     * get
     * @param context
     * @return
     */
    public static String getTimeCRC(Context context,String deviceid){
        String getSceneGroupCRC="",num="";

        TimerDAO SD = new TimerDAO(context);
        List<TimerGatewayBean> slist = SD.findAllTimer(deviceid);

        if(slist.size()>0) {
            int codeLength = 2;
            List<String> tid = new ArrayList<>();
            for (TimerGatewayBean e : slist) {
                tid.add(e.getTimerid());
            }


            for (int i = 0; i < Integer.parseInt(slist.get(slist.size() - 1).getTimerid()) + 1; i++) {//for here come with "0" , used slist.size()
                codeLength += 2;
                if (tid.contains(Integer.toString(i))) {
                    TimerGatewayBean timerGatewayBean = SD.findByTid(Integer.toString(i),deviceid);
                    getSceneGroupCRC += ResolveTimer.getCRCCode(timerGatewayBean);
                } else {
                    getSceneGroupCRC += "0000";
                }
            }


            String oooo = "0";

            if (Integer.toHexString(codeLength).length() < 4) {
                for (int i = 0; i < 4 - Integer.toHexString(codeLength).length() - 1; i++) {
                    oooo += 0;
                }
                num = oooo + Integer.toHexString(codeLength);
            } else {
                num = Integer.toHexString(codeLength);
            }
            return num + getSceneGroupCRC;
        }else {
            return "0000";
        }
    }

    public static void main(String args[]) {

        String abc = "@@$$$";
        String def = "6464000000";
        String ds = getDecrypt(def);
        System.out.print("点对点"+ds);
    }

}
