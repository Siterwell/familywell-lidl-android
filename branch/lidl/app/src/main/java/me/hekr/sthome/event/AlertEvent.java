package me.hekr.sthome.event;

/**
 * Created by Administrator on 2017/12/12.
 */

public class AlertEvent {

    private String content;
    private String deviceid;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }
}
