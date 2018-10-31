package me.hekr.sthome.tools;

import android.content.Context;

import me.hekr.sthome.R;


/**
 * Created by gc-0001 on 2017/4/1.
 */
public class NameSolve {

    public static final String DOOR_CHECK = "DOOR_CHECK";
    public static final String SOCKET = "SOCKET";
    public static final String PIR_CHECK = "PIR_CHECK";
    public static final String SOS_KEY = "SOS_KEY";
    public static final String SM_ALARM = "SM_ALARM";
    public static final String CO_ALARM = "CO_ALARM";
    public static final String WT_ALARM = "WT_ALARM";
    public static final String TH_CHECK = "TH_CHECK";
    public static final String LAMP = "LAMP";
    public static final String GUARD = "GUARD";
    public static final String VALVE = "VALVE";
    public static final String CURTAIN = "CURTAIN";
    public static final String BUTTON = "BUTTON";
    public static final String CXSM_ALARM = "CXSM_ALARM";
    public static final String GAS_ALARM = "GAS_ALARM";
    public static final String THERMAL_ALARM = "THERMAL_ALARM";
    public static final String MODE_BUTTON = "MODE_BUTTON";
    public static final String LOCK  = "LOCK";
    public static final String TWO_SOCKET="TWO_SOCKET";
    public static final String TEMP_CONTROL="TEMP_CONTROL";
    public static final String DIMMING_MODULE="DIMMING_MODULE";


    public static String getEqType(String Eqipmentdesc){
        if ("0101".equals(Eqipmentdesc) || "1101".equals(Eqipmentdesc) || "2101".equals(Eqipmentdesc)) {
            return DOOR_CHECK;

        } else if ("0200".equals(Eqipmentdesc) || "1200".equals(Eqipmentdesc) || "2200".equals(Eqipmentdesc)) {
            return SOCKET;
        } else if ("0300".equals(Eqipmentdesc) || "1300".equals(Eqipmentdesc) || "2300".equals(Eqipmentdesc)) {
            return SOS_KEY;
        } else if ("0100".equals(Eqipmentdesc) || "1100".equals(Eqipmentdesc) || "2100".equals(Eqipmentdesc)) {
            return PIR_CHECK;
        } else if ("0000".equals(Eqipmentdesc) || "1000".equals(Eqipmentdesc) || "2000".equals(Eqipmentdesc)
                || "0008".equals(Eqipmentdesc) || "1008".equals(Eqipmentdesc) || "2008".equals(Eqipmentdesc)) {
            return CO_ALARM;
        }else if ("0002".equals(Eqipmentdesc) ||
                "1002".equals(Eqipmentdesc) ||
                "2002".equals(Eqipmentdesc) ||
                "1006".equals(Eqipmentdesc)
                || "000A".equals(Eqipmentdesc) || "100A".equals(Eqipmentdesc) || "200A".equals(Eqipmentdesc)) {
            return GAS_ALARM;
        } else if ("0001".equals(Eqipmentdesc) || "1001".equals(Eqipmentdesc) || "2001".equals(Eqipmentdesc)
                || "0009".equals(Eqipmentdesc) || "1009".equals(Eqipmentdesc) || "2009".equals(Eqipmentdesc)) {
            return SM_ALARM;
        } else if ("0004".equals(Eqipmentdesc) || "1004".equals(Eqipmentdesc) || "2004".equals(Eqipmentdesc)
                || "000C".equals(Eqipmentdesc) || "100C".equals(Eqipmentdesc) || "200C".equals(Eqipmentdesc)) {
            return WT_ALARM;
        } else if ("0102".equals(Eqipmentdesc) || "1102".equals(Eqipmentdesc) || "2102".equals(Eqipmentdesc)) {
            return TH_CHECK;
        } else if ("020A".equals(Eqipmentdesc) || "120A".equals(Eqipmentdesc) || "220A".equals(Eqipmentdesc)) {
            return LAMP;
        } else if ("0210".equals(Eqipmentdesc) || "1210".equals(Eqipmentdesc) || "2210".equals(Eqipmentdesc)) {
            return GUARD;
        } else if ("0208".equals(Eqipmentdesc) || "1208".equals(Eqipmentdesc) || "2208".equals(Eqipmentdesc)) {
            return VALVE;
        } else if ("0301".equals(Eqipmentdesc) || "1301".equals(Eqipmentdesc) || "2301".equals(Eqipmentdesc)) {
            return BUTTON;
        } else if ("0209".equals(Eqipmentdesc) || "1209".equals(Eqipmentdesc) || "2209".equals(Eqipmentdesc)) {
            return CURTAIN;
        } else if ("0005".equals(Eqipmentdesc) || "1109".equals(Eqipmentdesc) || "2109".equals(Eqipmentdesc)
                || "000D".equals(Eqipmentdesc) || "100D".equals(Eqipmentdesc) || "200D".equals(Eqipmentdesc)) {
            return CXSM_ALARM;
        } else if ("0003".equals(Eqipmentdesc) || "1003".equals(Eqipmentdesc) || "2003".equals(Eqipmentdesc)
                || "000B".equals(Eqipmentdesc) || "100B".equals(Eqipmentdesc) || "200B".equals(Eqipmentdesc)) {
            return THERMAL_ALARM;
        }else if ("0305".equals(Eqipmentdesc)) {
            return MODE_BUTTON;
        }else if ("1213".equals(Eqipmentdesc)) {
            return LOCK;
        }else if ("0201".equals(Eqipmentdesc) || "1201".equals(Eqipmentdesc) || "2201".equals(Eqipmentdesc)) {
            return TWO_SOCKET;
        }else if ("0215".equals(Eqipmentdesc) || "1215".equals(Eqipmentdesc) || "2215".equals(Eqipmentdesc)) {
            return TEMP_CONTROL;
        }else if ("0214".equals(Eqipmentdesc) || "1214".equals(Eqipmentdesc) || "2214".equals(Eqipmentdesc)) {
            return DIMMING_MODULE;
        }
        return "";
    }

    public static String getDefaultName(Context context, String devType, String eqid){

        try {

            if ("0101".equals(devType) || "1101".equals(devType) || "2101".equals(devType)) {      //menci
                return context.getResources().getString(R.string.door) + eqid;
            } else if ("0200".equals(devType) || "1200".equals(devType) || "2200".equals(devType)) {   //socket
                return context.getResources().getString(R.string.socket) + eqid;
            } else if ("0100".equals(devType) || "1100".equals(devType) || "2100".equals(devType)) {  //pir
                return context.getResources().getString(R.string.pir) + eqid;
            } else if ("0300".equals(devType) || "1300".equals(devType) || "2300".equals(devType)) {  //sod
                return context.getResources().getString(R.string.soskey) + eqid;
            } else if ("0001".equals(devType) || "1001".equals(devType) || "2001".equals(devType)
                    || "0009".equals(devType) || "1009".equals(devType) || "2009".equals(devType)) {  //sm
                return context.getResources().getString(R.string.smalarm) + eqid;
            } else if ("0000".equals(devType) || "1000".equals(devType) || "2000".equals(devType)
                    || "0008".equals(devType) || "1008".equals(devType) || "2008".equals(devType)) {  //end
                return context.getResources().getString(R.string.coalarm) + eqid;
            } else if ("0004".equals(devType) || "1004".equals(devType) || "2004".equals(devType)
                    || "000C".equals(devType) || "100C".equals(devType) || "200C".equals(devType)) {  //end
                return context.getResources().getString(R.string.wt) + eqid;
            } else if ("0102".equals(devType) || "1102".equals(devType) || "2102".equals(devType)) {  //end
                return context.getResources().getString(R.string.thcheck) + eqid;
            } else if ("020A".equals(devType) || "120A".equals(devType) || "220A".equals(devType)) {   //socket
                return context.getResources().getString(R.string.lamp) + eqid;
            } else if ("0210".equals(devType) || "1210".equals(devType) || "2210".equals(devType)) {   //socket
                return context.getResources().getString(R.string.guardor) + eqid;
            } else if ("0208".equals(devType) || "1208".equals(devType) || "2208".equals(devType)) {   //socket
                return context.getResources().getString(R.string.valve) + eqid;
            } else if ("0209".equals(devType) || "1209".equals(devType) || "2209".equals(devType)) {   //socket
                return context.getResources().getString(R.string.curtain) + eqid;
            } else if ("0301".equals(devType) || "1301".equals(devType) || "2301".equals(devType)) {   //socket
                return context.getResources().getString(R.string.button) + eqid;
            } else if ("0005".equals(devType) || "1005".equals(devType) || "2005".equals(devType)
                    || "000D".equals(devType) || "100D".equals(devType) || "200D".equals(devType)) {  //sm
                return context.getResources().getString(R.string.cxsmalarm) + eqid;
            } else if ("0002".equals(devType) || "1002".equals(devType) || "2002".equals(devType)||
                    "1006".equals(devType)
                    || "000A".equals(devType) || "100A".equals(devType) || "200A".equals(devType)) {  //end
                return context.getResources().getString(R.string.gasalarm) + eqid;
            }else if ("0003".equals(devType) || "1003".equals(devType) || "2003".equals(devType)
                    || "000B".equals(devType) || "100B".equals(devType) || "200B".equals(devType)) {  //end
                return context.getResources().getString(R.string.thermalalarm) + eqid;
            }else if("0305".equals(devType)){
                return context.getResources().getString(R.string.mode_button) + eqid;
            }else if ("1213".equals(devType)) {  //end
                return context.getResources().getString(R.string.lock) + eqid;
            }else if ("0201".equals(devType) || "1201".equals(devType) || "2201".equals(devType)) {
                return context.getResources().getString(R.string.two_channel_socket) + eqid;
            }else if ("0215".equals(devType) || "1215".equals(devType) || "2215".equals(devType)) {
                return context.getResources().getString(R.string.temp_controler) + eqid;
            }else if ("0214".equals(devType) || "1214".equals(devType) || "2214".equals(devType)) {
                return context.getResources().getString(R.string.dimming_module) + eqid;
            }
        }catch (Exception e){
            return "";
        }
        return "";
    }

}
