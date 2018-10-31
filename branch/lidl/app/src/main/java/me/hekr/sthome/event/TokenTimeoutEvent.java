package me.hekr.sthome.event;

/**
 * Created by Administrator on 2017/12/11.
 */

public class TokenTimeoutEvent {
    //1代表accesstoken过期，2代表refreshtoken过期
    private int type = 0;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
