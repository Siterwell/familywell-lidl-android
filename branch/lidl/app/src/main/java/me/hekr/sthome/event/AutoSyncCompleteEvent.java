package me.hekr.sthome.event;

/**
 * Created by TracyHenry on 2018/5/9.
 */

public class AutoSyncCompleteEvent {

    private boolean flag_devices_empty;

    public boolean isFlag_devices_empty() {
        return flag_devices_empty;
    }

    public void setFlag_devices_empty(boolean flag_devices_empty) {
        this.flag_devices_empty = flag_devices_empty;
    }
}
