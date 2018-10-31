package me.hekr.sthome.model.modelbean;

import java.io.Serializable;

/**
 * Created by jishu0001 on 2016/8/19.
 */
public class EquipmentBean implements Serializable {
    protected int equipmentId;
    protected String equipmentName;
    protected String equipmentDesc;
    protected String activityTime;
    protected String state;
    protected String eqid;
    protected int packId;
    protected String deviceid;

    public int getPackId() {
        return packId;
    }

    public void setPackId(int packid) {
        this.packId = packid;
    }


    public String getEqid() {
        return eqid;
    }

    public void setEqid(String eqid) {
        this.eqid = eqid;
    }


    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(int equipmentId) {
        this.equipmentId = equipmentId;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
    }

    public String getEquipmentDesc() {
        return equipmentDesc;
    }

    public void setEquipmentDesc(String equipmentDesc) {
        this.equipmentDesc = equipmentDesc;
    }


    public String getActivityTime() {
        return activityTime;
    }

    public void setActivityTime(String activityTime) {
        this.activityTime = activityTime;
    }

    public String getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    @Override
    public String toString() {
        return "EquipmentBean{" +
                "equipmentId=" + equipmentId +
                ", equipmentName='" + equipmentName + '\'' +
                ", equipmentDesc='" + equipmentDesc + '\'' +
                ", activityTime='" + activityTime + '\'' +
                ", state='" + state + '\'' +
                ", eqid='" + eqid + '\'' +
                ", packId=" + packId +
                ", deviceid='" + deviceid + '\'' +
                '}';
    }
}
