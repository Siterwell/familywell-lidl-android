package me.hekr.sthome.model;

import android.content.Context;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import cz.msebera.android.httpclient.util.TextUtils;
import me.hekr.sthome.R;
import me.hekr.sthome.model.modelbean.EquipmentBean;
import me.hekr.sthome.model.modelbean.NoticeBean;
import me.hekr.sthome.model.modelbean.SceneBean;
import me.hekr.sthome.model.modeldb.EquipDAO;
import me.hekr.sthome.model.modeldb.SceneDAO;
import me.hekr.sthome.tools.ConnectionPojo;
import me.hekr.sthome.tools.NameSolve;

/**
 * Created by henry on 2018/12/11.
 */

public class ResolveHistory {
    private static final String TAG = "ResolveHistory";
    private String code ;
    public NoticeBean noticeBean;
    private boolean target;
    private Context context;
    private String devTid;
    private String id;
    private Long reportTime;
    String dataFormat = "yyyy-MM-dd HH:mm:ss";
    SimpleDateFormat sdf = new SimpleDateFormat(dataFormat);

    public ResolveHistory(Context context,String devTid,String code,String id,Long reportTime){
        this.context = context;
        this.code = code;
        this.devTid = devTid;
        this.id = id;
        this.reportTime = reportTime;
    }

    public boolean isTarget(){
        noticeBean = new NoticeBean();
        if(code.length()>6) {
            noticeBean.setType(code.substring(4, 6));
            if ("AC".equals(noticeBean.getType())) {
                resolveScene(noticeBean,code);
            } else if ("AD".equals(noticeBean.getType())) {
                resolveEquipment(noticeBean,code);
            }
            noticeBean.setDesc(id);
            if("AD".equals(noticeBean.getType())){
                noticeBean.setName(equipmentNameToShow(noticeBean.getEqid(),noticeBean.getEquipmenttype()));
            }else if("AC".equals(noticeBean.getType())){
                noticeBean.setName(sceneNameToShow(noticeBean.getMid()));
            }
            noticeBean.setDeviceid(devTid);
            Date date = new Date(reportTime);
            noticeBean.setActivitytime(sdf.format(date));
            Log.i(TAG,noticeBean.toString());
            target = true;
        }else {
            target = false;
        }
        return target;
    }


    /*
    @method dateGetOneDay
    @autor henry
    @time 2018/12/12 1:26 PM
    @email xuejunju_4595@qq.com
    @获取1970-1-1日的时间格式字符串
    */
    public static String dateGetOneDay() {
        SimpleDateFormat s=new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(0l);
        String curDate = s.format(c.getTime());
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

    /*
    @method getOneMonthMills
    @autor henry
    @time 2018/12/12 1:26 PM
    @email xuejunju_4595@qq.com
    获取30天以前的时间戳
    */
    public static long getOneMonthMills(){
        Calendar c = Calendar.getInstance();

        //过去一月
        c.setTime(new Date());
        c.add(Calendar.DATE, -30);

        return c.getTimeInMillis();
    }


    public void resolveScene(NoticeBean noticeBean, String code) {
        if(code.length()>6){
            noticeBean.setDesc(code);
            noticeBean.setType(code.substring(4, 6));
            noticeBean.setMid(String.valueOf(Integer.parseInt(code.substring(6,8),16)));
        }else {
            Log.i(TAG,"code error");
        }
    }

    public void resolveEquipment(NoticeBean noticeBean, String code) {
        if(code.length()>6){
            noticeBean.setDesc(code);
            noticeBean.setType(code.substring(4, 6));
            noticeBean.setEqid(String.valueOf(Integer.parseInt(code.substring(6,10),16)));
            noticeBean.setEquipmenttype(code.substring(10,14));
            noticeBean.setEqstatus(code.substring(14,22));
        }else {
            Log.i(TAG,"code error");
        }
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
}
