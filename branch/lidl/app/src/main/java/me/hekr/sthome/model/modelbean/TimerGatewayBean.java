package me.hekr.sthome.model.modelbean;

import java.io.Serializable;

/**
 * Created by gc-0001 on 2017/5/15.
 */

public class TimerGatewayBean implements Serializable {
    private int enable;
    private String code;
    private String timerid;
    private String modeid;
    private String week;
    private String hour;
    private String min;
    private String modename;
    private String deviceid;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getEnable() {
        return enable;
    }

    public void setEnable(int enable) {
        this.enable = enable;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public String getModeid() {
        return modeid;
    }

    public void setModeid(String modeid) {
        this.modeid = modeid;
    }

    public String getTimerid() {
        return timerid;
    }

    public void setTimerid(String timerid) {
        this.timerid = timerid;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getModename() {
        return modename;
    }

    public void setModename(String modename) {
        this.modename = modename;
    }

    public String getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    @Override
    public String toString() {
        return "TimerGatewayBean{" +
                "enable=" + enable +
                ", code='" + code + '\'' +
                ", timerid='" + timerid + '\'' +
                ", modeid='" + modeid + '\'' +
                ", week='" + week + '\'' +
                ", hour='" + hour + '\'' +
                ", min='" + min + '\'' +
                ", modename='" + modename + '\'' +
                ", deviceid='" + deviceid + '\'' +
                '}';
    }
}
