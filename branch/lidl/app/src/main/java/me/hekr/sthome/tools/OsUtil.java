package me.hekr.sthome.tools;

import android.app.ActivityManager;
import android.content.Context;
import android.text.TextUtils;

import java.util.List;

/**
 * Created by hekr_jds on 6/22 0022.
 **/
public class OsUtil {

    /**
     * 判断是否为本进程启动
     */
    public static boolean isProcessStartedBySelf(Context context) {
        String processName = OsUtil.getProcessName(context, android.os.Process.myPid());
        return !TextUtils.isEmpty(processName) && TextUtils.equals(context.getPackageName(), processName);
    }
    
    /**
     * 获取当前进程的名称
     */
    public static String getProcessName(Context cxt, int pid) {
        ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
        if (am != null) {
            List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
            if (runningApps == null) {
                return null;
            }
            for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
                if (procInfo.pid == pid) {
                    return procInfo.processName;
                }
            }
        }
        return null;
    }
}
