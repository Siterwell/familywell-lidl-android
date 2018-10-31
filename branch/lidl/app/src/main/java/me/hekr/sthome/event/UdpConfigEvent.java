package me.hekr.sthome.event;

/**
 * Created by TracyHenry on 2018/2/10.
 */

public class UdpConfigEvent {

    private int flag_result;  //0代表Udp通讯失败，1代表网关没有连上服务器，2代表网关已连上服务器

    public int getFlag_result() {
        return flag_result;
    }

    public void setFlag_result(int flag_result) {
        this.flag_result = flag_result;
    }
}
