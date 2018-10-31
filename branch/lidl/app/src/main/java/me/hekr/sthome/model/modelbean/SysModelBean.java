package me.hekr.sthome.model.modelbean;

/**
 * Created by jishu0001 on 2016/8/19.
 */
public class SysModelBean {
    private int modleId;
    private String modleName;
    private String modleDesc;
    private String chice;
    private String sid;
    private String deviceid;
    private String color;

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    public String getChice() {
        return chice;
    }

    public void setChice(String chice) {
        this.chice = chice;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }


    public int getModleId() {
        return modleId;
    }

    public void setModleId(int modleId) {
        this.modleId = modleId;
    }

    public String getModleName() {
        return modleName;
    }

    public void setModleName(String modleName) {
        this.modleName = modleName;
    }

    public String getModleDesc() {
        return modleDesc;
    }

    public void setModleDesc(String modleDesc) {
        this.modleDesc = modleDesc;
    }

    @Override
    public String toString() {
        return "SysModelBean{" +
                "modleId=" + modleId +
                ", modleName='" + modleName + '\'' +
                ", modleDesc='" + modleDesc + '\'' +
                ", chice='" + chice + '\'' +
                ", sid='" + sid + '\'' +
                ", deviceid='" + deviceid + '\'' +
                ", color='" + color + '\'' +
                '}';
    }
}
