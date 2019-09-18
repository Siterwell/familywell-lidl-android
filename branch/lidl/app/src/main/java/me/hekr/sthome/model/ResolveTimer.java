package me.hekr.sthome.model;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;

import me.hekr.sthome.R;
import me.hekr.sthome.model.modelbean.TimerGatewayBean;
import me.hekr.sthome.tools.ByteUtil;
import me.hekr.sthome.tools.UnitTools;

/**
 * Created by gc-0001 on 2017/5/16.
 */

public class ResolveTimer {
    private static final String TAG = "ResolveTimer";
    private String code ;
    private TimerGatewayBean timerGatewayBean;
    private boolean target;

    public ResolveTimer(String code,String deviceid){
        this.code = code;
        this.target = isTarget(code,deviceid);
    }

    private boolean isTarget(String code,String deviceid){
        if(TextUtils.isEmpty(code)) return false;

        if(code.length()!=16) return false;

        timerGatewayBean =new TimerGatewayBean();

        int timerid = Integer.parseInt(code.substring(0,2),16);
        int enable = Integer.parseInt(code.substring(2,4),16);
        int modeid = Integer.parseInt(code.substring(4,6),16);
        String week = code.substring(6,8);
        int hour = Integer.parseInt(code.substring(8,10),16);
        int min  = Integer.parseInt(code.substring(10,12),16);

        timerGatewayBean.setCode(code);
        timerGatewayBean.setMin(min>=10?String.valueOf(min):("0"+min));
        timerGatewayBean.setHour(hour>=10?String.valueOf(hour):("0"+hour));
        timerGatewayBean.setWeek(week);
        timerGatewayBean.setModeid(String.valueOf(modeid));
        timerGatewayBean.setEnable(enable);
        timerGatewayBean.setTimerid(String.valueOf(timerid));
        timerGatewayBean.setDeviceid(deviceid);
        return true;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isTarget() {
        return target;
    }

    public void setTarget(boolean target) {
        this.target = target;
    }

    public TimerGatewayBean getTimerGatewayBean() {
        return timerGatewayBean;
    }


    public static String getCode(TimerGatewayBean timerGatewayBean){
        String code = "";
        try {
            int timerid =  Integer.parseInt(timerGatewayBean.getTimerid());
            String oo = "0";
            if (Integer.toHexString(timerid).length() < 2) {
                oo = oo + Integer.toHexString(timerid);
            } else {
                oo = Integer.toHexString(timerid);
            }

            int enable = timerGatewayBean.getEnable();
            String enable00 = "0";
            if(enable == 1){
                enable00 +="1";
            }else{
                enable00 +="0";
            }

            int modeid = Integer.parseInt(timerGatewayBean.getModeid());
            String mode00 = "0";
            if (Integer.toHexString(modeid).length() < 2) {
                mode00 = mode00 + Integer.toHexString(modeid);
            } else {
                mode00 = Integer.toHexString(modeid);
            }

            String week00 = timerGatewayBean.getWeek();

            int hour = Integer.parseInt(timerGatewayBean.getHour());

            String hour00 = "0";
            if (Integer.toHexString(hour).length() < 2) {
                hour00 = hour00 + Integer.toHexString(hour);
            } else {
                hour00 = Integer.toHexString(hour);
            }


            int min = Integer.parseInt(timerGatewayBean.getMin());

            String min00 = "0";
            if (Integer.toHexString(min).length() < 2) {
                min00 = min00 + Integer.toHexString(min);
            } else {
                min00 = Integer.toHexString(min);
            }

            String fullcode = oo + enable00 + mode00 + week00 + hour00 + min00;
            String crc = ByteUtil.CRCmakerChar(fullcode);

            code = fullcode + crc;
        }catch (NullPointerException e){
            return "bean is null";
        }
        return code;
    }

    public static String getCRCCode(TimerGatewayBean timerGatewayBean){
        String crc = "";
        try {
            int timerid =  Integer.parseInt(timerGatewayBean.getTimerid());
            String oo = "0";
            if (Integer.toHexString(timerid).length() < 2) {
                oo = oo + Integer.toHexString(timerid);
            } else {
                oo = Integer.toHexString(timerid);
            }

            int enable = timerGatewayBean.getEnable();
            String enable00 = "0";
            if(enable == 1){
                enable00 +="1";
            }else{
                enable00 +="0";
            }

            int modeid = Integer.parseInt(timerGatewayBean.getModeid());
            String mode00 = "0";
            if (Integer.toHexString(modeid).length() < 2) {
                mode00 = mode00 + Integer.toHexString(modeid);
            } else {
                mode00 = Integer.toHexString(modeid);
            }

            String week00 = timerGatewayBean.getWeek();

            int hour = Integer.parseInt(timerGatewayBean.getHour());

            String hour00 = "0";
            if (Integer.toHexString(hour).length() < 2) {
                hour00 = hour00 + Integer.toHexString(hour);
            } else {
                hour00 = Integer.toHexString(hour);
            }


            int min = Integer.parseInt(timerGatewayBean.getMin());

            String min00 = "0";
            if (Integer.toHexString(min).length() < 2) {
                min00 = min00 + Integer.toHexString(min);
            } else {
                min00 = Integer.toHexString(min);
            }

            String fullcode = oo + enable00 + mode00 + week00 + hour00 + min00;
            crc = ByteUtil.CRCmakerChar(fullcode);
        }catch (NullPointerException e){
            return "bean is null";
        }
        return crc;
    }


    public static String getWeekinfo(String week,Context context){

        try {
            String weektime = "";
            byte ds = ByteUtil.hexStr2Bytes(week)[0];
            byte f = 0x00;
            for(int i=0;i<7;i++){

                f =   (byte)((0x02 << i) & ds);
                if(f!=0){
                    weektime += (context.getResources().getStringArray(R.array.week)[i] + ",");
                }
            }
            weektime = weektime.substring(0,weektime.length()-1);


            return weektime;
        }catch (Exception e){
            Log.i(TAG,"week is null");
            return "";
        }

    }

    public static String getWeekinfoHash(HashMap<Integer,Boolean> week,Context context,UnitTools tools){


           String weektime = "";
            for(int i=0;i<7;i++){


                if(week.get(i)){
                    weektime += (context.getResources().getStringArray(R.array.week)[i] + ("zh".equals( tools.readLanguage())?"、":","));
                }
            }
            weektime = weektime.substring(0,weektime.length()-1);


            return ("zh".equals( tools.readLanguage())?"这个时间点":"this time at ") + ("zh".equals( tools.readLanguage())?"周":"") + weektime + ("zh".equals( tools.readLanguage())?"重复了":" is repeat.");


    }


}
