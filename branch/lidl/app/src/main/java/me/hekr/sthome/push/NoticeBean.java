package me.hekr.sthome.push;

/**
 * Created by jishu0001 on 2017/3/1.
 */

public class NoticeBean {
    private String id;
    private String type;
    private String mid;
    private String eqid;
    private String equipmenttype;
    private String eqstatus;
    private String desc;
    private String deviceid;
    private String activitytime;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getEqid() {
        return eqid;
    }

    public void setEqid(String eqid) {
        this.eqid = eqid;
    }

    public String getEquipmenttype() {
        return equipmenttype;
    }

    public void setEquipmenttype(String equipmenttype) {
        this.equipmenttype = equipmenttype;
    }

    public String getEqstatus() {
        return eqstatus;
    }

    public void setEqstatus(String eqstatus) {
        this.eqstatus = eqstatus;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    public String getActivitytime() {
        return activitytime;
    }

    public void setActivitytime(String activitytime) {
        this.activitytime = activitytime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "NoticeBean{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", mid='" + mid + '\'' +
                ", eqid='" + eqid + '\'' +
                ", equipmenttype='" + equipmenttype + '\'' +
                ", eqstatus='" + eqstatus + '\'' +
                ", desc='" + desc + '\'' +
                ", deviceid='" + deviceid + '\'' +
                ", activitytime='" + activitytime + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
