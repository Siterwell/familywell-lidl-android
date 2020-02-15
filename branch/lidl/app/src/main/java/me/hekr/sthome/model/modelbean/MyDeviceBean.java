package me.hekr.sthome.model.modelbean;


import me.hekr.sthome.http.bean.DeviceBean;

/**
 * Created by gc-0001 on 2017/2/27.
 */
public class MyDeviceBean extends DeviceBean {

    int choice = 0;

    public int getChoice() {
        return choice;
    }

    public void setChoice(int choice) {
        this.choice = choice;
    }
}
