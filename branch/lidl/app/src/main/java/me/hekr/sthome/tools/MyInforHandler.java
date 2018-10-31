package me.hekr.sthome.tools;

import android.os.Handler;
import android.os.Message;


/**
 * Created by jishu0001 on 2016/11/29.
 */
public abstract class MyInforHandler extends Handler {
    private static final  int GETBACK_SUCCESS = 1;
    private static final int GETBACK_FAILED = 2;

    public void handleMessage(Message msg){
        super.handleMessage(msg);
//        Log.i(TAG, (String)msg.obj);
        switch (msg.what){
            case GETBACK_SUCCESS:
                operationSuccess();
                break;
            case GETBACK_FAILED:
                operationFailed();
                break;
            default:
                break;
        }
    }

    protected abstract void operationSuccess();
    protected abstract void operationFailed();
}
