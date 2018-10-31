package me.hekr.sthome.model.modelbean;

/**
 * Created by gc-0001 on 2016/12/14.
 */
public class SceneBean {

    private int id;
    private String name;
    private String code;
    private String checksum;
    private String sid;
    private String mid;
    private String deviceid;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    @Override
    public String toString() {
        return "SceneBean{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", checksum='" + checksum + '\'' +
                ", sid='" + sid + '\'' +
                ", mid='" + mid + '\'' +
                ", deviceid='" + deviceid + '\'' +
                '}';
    }
}
