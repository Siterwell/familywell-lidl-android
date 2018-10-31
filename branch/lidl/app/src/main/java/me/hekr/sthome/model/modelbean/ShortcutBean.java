package me.hekr.sthome.model.modelbean;

/**
 * Created by Administrator on 2017/10/27.
 * 情景开关
 */

public class ShortcutBean {


    private String src_sid;
    private String des_sid;
    private String deviceid;
    private String eqid;
    private int delay;

    public String getSrc_sid() {
        return src_sid;
    }

    public void setSrc_sid(String src_sid) {
        this.src_sid = src_sid;
    }


    public String getDes_sid() {
        return des_sid;
    }

    public void setDes_sid(String des_sid) {
        this.des_sid = des_sid;
    }


    public String getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    public String getEqid() {
        return eqid;
    }

    public void setEqid(String eqid) {
        this.eqid = eqid;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    @Override
    public String toString() {
        return "ShortcutBean{" +
                "src_sid='" + src_sid + '\'' +
                ", des_sid='" + des_sid + '\'' +
                ", deviceid='" + deviceid + '\'' +
                ", eqid='" + eqid + '\'' +
                ", delay=" + delay +
                '}';
    }
}
