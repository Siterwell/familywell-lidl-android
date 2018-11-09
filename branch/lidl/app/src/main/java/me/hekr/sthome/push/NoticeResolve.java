package me.hekr.sthome.push;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.util.TextUtils;
import me.hekr.sdk.Constants;
import me.hekr.sthome.R;
import me.hekr.sthome.http.HekrUserAction;
import me.hekr.sthome.model.modelbean.EquipmentBean;
import me.hekr.sthome.model.modelbean.MyDeviceBean;
import me.hekr.sthome.model.modelbean.SceneBean;

import me.hekr.sthome.model.modeldb.DeviceDAO;
import me.hekr.sthome.model.modeldb.EquipDAO;
import me.hekr.sthome.model.modeldb.SceneDAO;
import me.hekr.sthome.tools.ConnectionPojo;
import me.hekr.sthome.tools.LOG;
import me.hekr.sthome.tools.NameSolve;


/**
 * Created by jishu0001 on 2017/3/2.
 */

public class NoticeResolve implements INoticeResolve {
    public static final String TAG = NoticeResolve.class.getName();
    private Context context;

    public NoticeResolve(Context context){
        this.context = context;
    }


    @Override
    public NoticeBean resolveNoticeCode(String code) {
        NoticeBean noticeBean = new NoticeBean();
        if(code.length()>6) {
            noticeBean.setDesc(code);
            noticeBean.setType(code.substring(4, 6));
            if ("AC".equals(noticeBean.getType())) {
                resolveScene(noticeBean,code);
            } else if ("AD".equals(noticeBean.getType())) {
                resolveEquipment(noticeBean,code);
            }
        }else {
            LOG.I(TAG,"code error");
        }
        return noticeBean;

    }

    @Override
    public void resolveScene(NoticeBean noticeBean, String code) {
        if(code.length()>6){
            noticeBean.setDesc(code);
            noticeBean.setType(code.substring(4, 6));
            noticeBean.setMid(String.valueOf(Integer.parseInt(code.substring(6,8),16)));
        }else {
            LOG.I(TAG,"code error");
        }
    }

    @Override
    public void resolveEquipment(NoticeBean noticeBean, String code) {
        if(code.length()>6){
            noticeBean.setDesc(code);
            noticeBean.setType(code.substring(4, 6));
            noticeBean.setEqid(String.valueOf(Integer.parseInt(code.substring(6,10),16)));
            noticeBean.setEquipmenttype(code.substring(10,14));
            noticeBean.setEqstatus(code.substring(14,22));
        }else {
            LOG.I(TAG,"code error");
        }
    }


    @Override
    public String dateGetOneDay() {
        SimpleDateFormat s=new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(0l);
        String curDate = s.format(c.getTime());  //��ǰ����
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);
        TimeZone timeZone = c.getTimeZone();
        //     2017-03-20T09%3A45%3A00.000%2B0800
        String hour1 = hour<10 ? "0"+hour : ""+hour;
        String min1 = (min<10 ? "0"+min : ""+min);
        SimpleDateFormat zz = new SimpleDateFormat("Z");
        String where2 = zz.format(c.getTime());
        String where3 = where2.substring(1);
        return curDate+"T"+hour1+":"+min1+":00.000-"+where3;
    }


    @Override
    public List<NoticeBean> getJsonArrayResolve(String string){
        String dataFormat = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(dataFormat);
        List<NoticeBean> result = new ArrayList<NoticeBean>();
        try {
            JSONObject jsonObject = JSONObject.parseObject(string);
            int i = jsonObject.getIntValue("totalElements");
            if(i > 0){
                JSONArray jsonArray = jsonObject.getJSONArray("content");
                for (int n=0 ; n< jsonArray.size(); n ++ ){
                    NoticeBean nb = new NoticeBean();
                    JSONObject child = (JSONObject) jsonArray.get(n);
                    String nid = child.getString("id");
                    String desc = child.getJSONObject("data").getString("answer_content");

                     NoticeBean buf = resolveNoticeCode(desc);
                    nb.setType(buf.getType());
                    nb.setDesc(nid);
                    if("AD".equals(nb.getType())){
                        nb.setEqid(buf.getEqid());
                        nb.setEquipmenttype(buf.getEquipmenttype());
                        nb.setEqstatus(buf.getEqstatus());
                        nb.setName(equipmentNameToShow(nb.getEqid(),nb.getEquipmenttype()));
                    }else if("AC".equals(nb.getType())){
                        nb.setMid(buf.getMid());
                        nb.setName(sceneNameToShow(nb.getMid()));
                    }
                    nb.setDeviceid(ConnectionPojo.getInstance().deviceTid);
                    Date date = new Date(child.getLong("reportTime"));
                    nb.setActivitytime(sdf.format(date));
                    LOG.I(TAG,nb.toString());
                    result.add(nb);
                }
            }

        } catch (JSONException e) {
              e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public String getAlarmInforFromHrke(int page,HekrUserAction.GetHekrDataListener getHekrDataListener) {
        DeviceDAO DDO = new DeviceDAO(context);
        MyDeviceBean myDeviceBean = DDO.findByChoice(1);
            if(myDeviceBean!=null){
                LOG.I(TAG,"ppk======"+myDeviceBean.getProductPublicKey());
                BasicHeader header = new BasicHeader("X-Hekr-ProdPubKey",myDeviceBean.getProductPublicKey());
                String url = Constants.UrlUtil.BASE_USER_URL+"api/v1/notification?type=WARNING&" +
                        "ctrlKey="+myDeviceBean.getCtrlKey()+"" +
                        "&startTime="+dateGetOneDay()+
                        "&page="+page+
                        "&size=20" ;
                HekrUserAction.getInstance(context).getHekrData(url, new Header[]{header}, getHekrDataListener);
            }

        return null;
    }


    public String equipmentNameToShow(String eqid,String type){
        EquipDAO ED = new EquipDAO(context);
        EquipmentBean equipmentBean = ED.findByeqid(eqid, ConnectionPojo.getInstance().deviceTid);
        String name = null;
        if(equipmentBean != null  && equipmentBean.getEquipmentDesc() != null){
            if(TextUtils.isEmpty(equipmentBean.getEquipmentName())){
                name = NameSolve.getDefaultName(context,type,eqid);
            }else {
                name = equipmentBean.getEquipmentName();
            }
        }else {
            name = NameSolve.getDefaultName(context,type,eqid);
        }

        return name;
    }

    public String sceneNameToShow(String mid ){
        String name = null;
        SceneDAO SD = new SceneDAO(context);
        SceneBean sb = SD.findScenceBymid(mid, ConnectionPojo.getInstance().deviceTid);
        if (sb != null ){
            if(sb.getName() == null){
                name = context.getResources().getString(R.string.scene)+mid;
            }else {
                name = sb.getName();
            }
        }else {
            name = context.getResources().getString(R.string.scene)+mid;
        }
        return name;
    }


    public static void main(String args[]) {
        String a ="{\"content\":\n" +
                "[{\"warningId\":\"58db0575e4b0270db4bc9e8d\"," +
                "\"uid\":\"66081263874\"," +
                "\"mid\":\"01f41370a5ab\"," +
                "\"pid\":\"01808478427\"," +
                "\"devTid\":\"ST_5ccf7f1ecf9a\"," +
                "\"logo\":\"https://c-img-s.hekr.me/4f81e366de3544da8b15921257e3748d/1466504406897/door1.png\"," +
                "\"deviceName\":\"������\"," +
                "\"productName\":{\"zh_CN\":\"\",\"en_US\":\"\"}," +
                "\"categoryName\":{\"zh_CN\":\"�ҾӼ�װ/������\"," +
                "\"en_US\":\"Misc/Alarm\"}," +
                "\"params\":[]," +
                "\"subject\":\"������ϢALARM\"," +
                "\"content\":\"���յ���һ��������Ϣ����鿴\\nYou received an alarm message, please check\"," +
                "\"reportTime\":1490748789194," +
                "\"subDevTid\":null," +
                "\"original\":true," +
                "\"read\":true}]," +
                "\"last\":true," +
                "\"totalPages\":1," +
                "\"totalElements\":1," +
                "\"first\":true," +
                "\"sort\":null," +
                "\"numberOfElements\":1," +
                "\"size\":20," +
                "\"number\":0}";

//        INoticeResolve noticeResolve = new NoticeResolve(con);
//        noticeResolve.getJsonArrayResolve(a);
    }
}
