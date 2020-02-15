package me.hekr.sthome.event;

/**
 * Created by Administrator on 2017/7/3.
 */

/*
@class STEvent
@autor Administrator
@time 2017/7/3 15:54
@email xuejunju_4595@qq.com
事件传递解析
*/
public class STEvent {
    private  int event;

    private  int refreshevent; //刷新事件，除了协议外的；1代表刷新头部文字；2代表主界面（mainactivity）弹出progressdialog; 3代表成功获取数据后取消progressdialog;4代表情景刷新结束;5代表刷新设备列表；6代表获取数据失败取消progressdialog;7代表网关列表弹出progressdialog;8代表切换语言跳转activity;9代表绑定FCM广播;10代表刷新温度;11代表绑定个推广播;12代表绑定华为推送广播;13代表绑定小米推送广播;
    private int serviceevent;  //服务事件，是activity向SiterService传递的 1代表后台发送同步情景命令;2代表发送同步设备名称命令;3代表发送同步情景命令;4代表下拉刷新发送同步情景命令；5代表udp获取绑定设备的信息（devid和bindkey）;6代表开始搜索局域网；7代表网络信号变化广播；8.代表网关切换后台命令
    private String progressText;//progressdialog文字
    private String eq_status;   //设备状态
    private String eq_id;       //设备id;
    private String eq_type;     //设备类型;
    private String current_deviceid;    //当前网关;
    private int nettype; //网络变化的种类,0代表无网，1-3代表3,4G，4代表WIFI
    private String ssid;  // 网络变化时获取的ssid
    private String fcm_token; //

    public String getFcm_token() {
        return fcm_token;
    }

    public void setFcm_token(String fcm_token) {
        this.fcm_token = fcm_token;
    }

    public int getEvent() {
        return event;
    }

    public void setEvent(int event) {
        this.event = event;
    }

    public int getRefreshevent() {
        return refreshevent;
    }

    public void setRefreshevent(int refreshevent) {
        this.refreshevent = refreshevent;
    }

    public String getProgressText() {
        return progressText;
    }

    public void setProgressText(String progressText) {
        this.progressText = progressText;
    }

    public String getEq_status() {
        return eq_status;
    }

    public void setEq_status(String eq_status) {
        this.eq_status = eq_status;
    }

    public int getServiceevent() {
        return serviceevent;
    }

    public void setServiceevent(int serviceevent) {
        this.serviceevent = serviceevent;
    }

    public String getEq_id() {
        return eq_id;
    }

    public void setEq_id(String eq_id) {
        this.eq_id = eq_id;
    }

    public String getEq_type() {
        return eq_type;
    }

    public void setEq_type(String eq_type) {
        this.eq_type = eq_type;
    }

    public String getCurrent_deviceid() {
        return current_deviceid;
    }

    public void setCurrent_deviceid(String current_deviceid) {
        this.current_deviceid = current_deviceid;
    }

    public int getNettype() {
        return nettype;
    }

    public void setNettype(int nettype) {
        this.nettype = nettype;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }
}
