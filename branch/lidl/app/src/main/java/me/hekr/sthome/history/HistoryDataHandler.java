package me.hekr.sthome.history;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;

import me.hekr.sthome.R;
import me.hekr.sthome.tools.NameSolve;

/**
 * Created by ST-020111 on 2017/3/24.
 */

public class HistoryDataHandler {

    /**
     * 得到现在时间
     *
     * @return 字符串 yyyyMMdd HHmmss
     */
    public static String getStringToday() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(currentTime);
        SimpleDateFormat formatter1 = new SimpleDateFormat("HH:mm:ss.SSS+Z");
        String dateString1 = formatter1.format(currentTime);
        return dateString+"T"+dateString1;
    }

    /**
     * 得到现在小时
     */
    public static String getHour() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        String hour;
        hour = dateString.substring(11, 13);
        return hour;
    }

    public static String getAlert(Context context,String equipmenttype, String eqstatus){

        try {
            String alertstatus = eqstatus.substring(4,6);
            String battery = eqstatus.substring(2,4);
            if(NameSolve.DOOR_CHECK.equals(NameSolve.getEqType(equipmenttype))){
                  if("55".equals(alertstatus)){
                     return context.getResources().getStringArray(R.array.door_actions)[0];
                  }else if("AA".equals(alertstatus)){
                      if(Integer.parseInt(battery,16)<15){
                          return context.getResources().getString(R.string.low_battery);
                      }else {
                          return context.getResources().getStringArray(R.array.door_actions)[1];
                      }
                  }else if("66".equals(alertstatus)){
                      return context.getResources().getStringArray(R.array.door_actions)[2];
                  }else if("FF".equals(alertstatus)){
                      return context.getResources().getString(R.string.offline);
                  }else {
                      if(Integer.parseInt(battery,16)<15){
                          return context.getResources().getString(R.string.low_battery);
                      }else {
                          return context.getResources().getString(R.string.alarm);
                      }
                  }
            }else if(NameSolve.SOS_KEY.equals(NameSolve.getEqType(equipmenttype))){
                if("55".equals(alertstatus)){
                    return context.getResources().getStringArray(R.array.sos_signs)[0];
                }else if("66".equals(alertstatus)){
                    return context.getResources().getStringArray(R.array.sos_signs)[1];
                }else if("AA".equals(alertstatus)){
                    if(Integer.parseInt(battery,16)<15){
                        return context.getResources().getString(R.string.low_battery);
                    }else {
                        return context.getResources().getStringArray(R.array.door_actions)[1];
                    }
                }else if("FF".equals(alertstatus)){
                    return context.getResources().getString(R.string.offline);
                }else {
                    if(Integer.parseInt(battery,16)<15){
                        return context.getResources().getString(R.string.low_battery);
                    }else {
                        return context.getResources().getString(R.string.alarm);
                    }
                }
            }else if(NameSolve.PIR_CHECK.equals(NameSolve.getEqType(equipmenttype))){
                if("55".equals(alertstatus)){
                    return context.getResources().getStringArray(R.array.pir_signs)[0];
                }else if("AA".equals(alertstatus)){
                    if(Integer.parseInt(battery,16)<15){
                        return context.getResources().getString(R.string.low_battery);
                    }else {
                        return context.getResources().getStringArray(R.array.door_actions)[1];
                    }
                }else if("FF".equals(alertstatus)){
                    return context.getResources().getString(R.string.offline);
                }else {
                    if(Integer.parseInt(battery,16)<15){
                        return context.getResources().getString(R.string.low_battery);
                    }else {
                        return context.getResources().getString(R.string.alarm);
                    }
                }
            }else if(NameSolve.SM_ALARM.equals(NameSolve.getEqType(equipmenttype))){
                if("BB".equals(alertstatus)){
                    return context.getResources().getString(R.string.cxalert7);
                }else if("55".equals(alertstatus)){
                    return context.getResources().getString(R.string.sthalert);
                }else if("FF".equals(alertstatus)){
                    return context.getResources().getString(R.string.offline);
                }else {
                    if("AA".equals(alertstatus) && Integer.parseInt(battery,16)<15){
                        return context.getResources().getString(R.string.low_battery);
                    }else{
                    return context.getResources().getString(R.string.alarm);
                }

                }
            }else if(NameSolve.CO_ALARM.equals(NameSolve.getEqType(equipmenttype))){
                if("BB".equals(alertstatus)){
                    return context.getResources().getString(R.string.cxalert7);
                }else if("55".equals(alertstatus)){
                    return context.getResources().getString(R.string.sthalert);
                }else if("FF".equals(alertstatus)){
                    return context.getResources().getString(R.string.offline);
                }else {
                    if("AA".equals(alertstatus) && Integer.parseInt(battery,16)<15){
                        return context.getResources().getString(R.string.low_battery);
                    }else{
                    return context.getResources().getString(R.string.alarm);
                }
                }
            }else if(NameSolve.WT_ALARM.equals(NameSolve.getEqType(equipmenttype))){
                if("BB".equals(alertstatus)){
                    return context.getResources().getString(R.string.cxalert7);
                }else if("55".equals(alertstatus)){
                    return context.getResources().getString(R.string.sthalert);
                }else if("FF".equals(alertstatus)){
                    return context.getResources().getString(R.string.offline);
                }else {
                    if("AA".equals(alertstatus) && Integer.parseInt(battery,16)<15){
                        return context.getResources().getString(R.string.low_battery);
                    }else{
                    return context.getResources().getString(R.string.alarm);
                }
                }
            }else if(NameSolve.TH_CHECK.equals(NameSolve.getEqType(equipmenttype))){
                if("AA".equals(alertstatus) && Integer.parseInt(battery,16)<15){
                    return context.getResources().getString(R.string.low_battery);
                }else if("FF".equals(alertstatus)){
                    return context.getResources().getString(R.string.offline);
                }else{
                    if(Integer.parseInt(battery,16)<15){
                        return context.getResources().getString(R.string.low_battery);
                    }else {
                        return context.getResources().getString(R.string.alarm);
                    }
                }
            }else if(NameSolve.CXSM_ALARM.equals(NameSolve.getEqType(equipmenttype))){
                if("17".equals(alertstatus)){
                    return context.getResources().getString(R.string.cxalert7);
                }else if("19".equals(alertstatus)){
                    return context.getResources().getString(R.string.cxalert9);
                }else if("12".equals(alertstatus)){
                    return context.getResources().getString(R.string.cxalert2);
                }else if("15".equals(alertstatus)){
                    return context.getResources().getString(R.string.cxalert5);
                }else if("1B".equals(alertstatus)){
                    return context.getResources().getString(R.string.cxalert11);
                }else if("FF".equals(alertstatus)){
                    return context.getResources().getString(R.string.offline);
                }else {

                    if("AA".equals(alertstatus) && Integer.parseInt(battery,16)<15){
                        return context.getResources().getString(R.string.low_battery);
                    }else{
                    return context.getResources().getString(R.string.alarm);
                }
                }
            }else if(NameSolve.GAS_ALARM.equals(NameSolve.getEqType(equipmenttype))){
                if("BB".equals(alertstatus)){
                    return context.getResources().getString(R.string.cxalert7);
                }else if("55".equals(alertstatus)){
                    return context.getResources().getString(R.string.sthalert);
                }else if("FF".equals(alertstatus)){
                    return context.getResources().getString(R.string.offline);
                }else {
                    if("AA".equals(alertstatus) && Integer.parseInt(battery,16)<15){
                        return context.getResources().getString(R.string.low_battery);
                    }else{
                    return context.getResources().getString(R.string.alarm);
                }
                }
            }else if(NameSolve.THERMAL_ALARM.equals(NameSolve.getEqType(equipmenttype))){
                if("BB".equals(alertstatus)){
                    return context.getResources().getString(R.string.cxalert7);
                }else if("55".equals(alertstatus)){
                    return context.getResources().getString(R.string.sthalert);
                }else if("FF".equals(alertstatus)){
                    return context.getResources().getString(R.string.offline);
                }else {
                    if("AA".equals(alertstatus) && Integer.parseInt(battery,16)<15){
                        return context.getResources().getString(R.string.low_battery);
                    }else{
                    return context.getResources().getString(R.string.alarm);
                }
                }
            }else if(NameSolve.MODE_BUTTON.equals(NameSolve.getEqType(equipmenttype))){
                if("55".equals(alertstatus)){
                    return context.getResources().getStringArray(R.array.sos_signs)[0];
                }else if("66".equals(alertstatus)){
                    return context.getResources().getStringArray(R.array.sos_signs)[1];
                }else if("FF".equals(alertstatus)){
                    return context.getResources().getString(R.string.offline);
                }else {
                    if("AA".equals(alertstatus) && Integer.parseInt(battery,16)<15){
                        return context.getResources().getString(R.string.low_battery);
                    }else{
                    return context.getResources().getString(R.string.alarm);
                }
                }
            }else if(NameSolve.LOCK.equals(NameSolve.getEqType(equipmenttype))){
                if("50".equals(alertstatus)){
                    return context.getResources().getStringArray(R.array.lock_input)[0];
                }else if("51".equals(alertstatus)){
                    return context.getResources().getStringArray(R.array.lock_input)[1];
                }else if("52".equals(alertstatus)){
                    return context.getResources().getStringArray(R.array.lock_input)[2];
                }else if("53".equals(alertstatus)){
                    return context.getResources().getStringArray(R.array.lock_input)[3];
                }else if("10".equals(alertstatus)){
                    return context.getResources().getStringArray(R.array.lock_input)[4];
                }else if("20".equals(alertstatus)){
                    return context.getResources().getStringArray(R.array.lock_input)[5];
                }else if("30".equals(alertstatus)){
                    return context.getResources().getStringArray(R.array.lock_input)[6];
                }else if("FF".equals(alertstatus)){
                    return context.getResources().getString(R.string.offline);
                }else {
                    if("AA".equals(alertstatus) && Integer.parseInt(battery,16)<15){
                        return context.getResources().getString(R.string.low_battery);
                    }else{
                    return context.getResources().getString(R.string.alarm);
                }
                }
            }else if(NameSolve.BUTTON.equals(NameSolve.getEqType(equipmenttype))){
                if("FF".equals(alertstatus)){
                    return context.getResources().getString(R.string.offline);
                }else {
                    if("AA".equals(alertstatus) && Integer.parseInt(battery,16)<15){
                        return context.getResources().getString(R.string.low_battery);
                    }else{
                        return context.getResources().getString(R.string.alarm);
                    }
                }
            }else if(NameSolve.SOCKET.equals(NameSolve.getEqType(equipmenttype))){
                if("FF".equals(alertstatus)){
                    return context.getResources().getString(R.string.offline);
                }else {
                    return context.getResources().getString(R.string.alarm);
                }
            }else if(NameSolve.TWO_SOCKET.equals(NameSolve.getEqType(equipmenttype))){
                if("FF".equals(alertstatus)){
                    return context.getResources().getString(R.string.offline);
                }else {
                    return context.getResources().getString(R.string.alarm);
                }
            }else if(NameSolve.DIMMING_MODULE.equals(NameSolve.getEqType(equipmenttype))){
                if("FF".equals(alertstatus)){
                    return context.getResources().getString(R.string.offline);
                }else {
                    if("AA".equals(alertstatus) && Integer.parseInt(battery,16)<15){
                        return context.getResources().getString(R.string.low_battery);
                    }else{
                        return context.getResources().getString(R.string.alarm);
                    }
                }
            }else if(NameSolve.TEMP_CONTROL.equals(NameSolve.getEqType(equipmenttype))){
                if("FF".equals(alertstatus)){
                    return context.getResources().getString(R.string.offline);
                }else {
                    if("AA".equals(alertstatus) && Integer.parseInt(battery,16)<15){
                        return context.getResources().getString(R.string.low_battery);
                    }else{
                        return context.getResources().getString(R.string.alarm);
                    }
                }
            }else if("0000".equals(equipmenttype)){
                if("00000000".equals(eqstatus)){
                    return context.getResources().getString(R.string.electric_city_break_off);
                }else if("00000001".equals(eqstatus)){
                    return context.getResources().getString(R.string.electric_city_normal);
                }else if("00000002".equals(eqstatus)){
                    return context.getResources().getString(R.string.battery_normal);
                }else if("00000003".equals(eqstatus)){
                    return context.getResources().getString(R.string.battery_low);
                }else if("00000004".equals(eqstatus)){
                    return context.getResources().getString(R.string.alert_elder_care);
                }else if("FFFFFFFF".equals(eqstatus)){
                    return context.getResources().getString(R.string.offline);
                }else {
                   return context.getResources().getString(R.string.alarm);
                }
            }
            else{
                return context.getResources().getString(R.string.alarm);
            }

        }catch (Exception e){
            return "";
        }


    }
}
