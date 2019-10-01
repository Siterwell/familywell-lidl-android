package me.hekr.sthome;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.util.List;

import io.fabric.sdk.android.Fabric;
import me.hekr.sdk.HekrSDK;
import me.hekr.sthome.service.SiterService;
import me.hekr.sthome.tools.LOG;
import me.hekr.sthome.tools.UnitTools;

/**
 * Created by hekr_jds on 6/30 0030.
 **/
public class MyApplication extends MultiDexApplication {

    private static MyApplication mApp;
    public static Activity sActivity;
    // user your appid the key.
    private static final String APP_ID = "2882303761517832975";
    // user your appid the key.
    private static final String APP_KEY = "5651783247975";


    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
//        FunSupport.getInstance().init(getApplicationContext());
        HekrSDK.init(getApplicationContext(), R.raw.config);
        HekrSDK.enableDebug(true);

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .memoryCacheExtraOptions(80, 80)
                .denyCacheImageMultipleSizesInMemory()
                //.writeDebugLogs()
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .build();



        ImageLoader.getInstance().init(config);

        mApp = this;
        this.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            }

            @Override
            public void onActivityStarted(Activity activity) {
                sActivity=activity;

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
        UnitTools tools = new UnitTools(this);
        String d = tools.readLanguage();
        LOG.I("ceshi","语言为:"+d);
//        CrashHandler.getInstance().init(getApplicationContext());

        Intent intent = new Intent(this, SiterService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }



    public static Context getAppContext() {
        return mApp;
    }

    public static Resources getAppResources() {
        return mApp.getResources();
    }

    public static Activity getActivity(){
        return sActivity;
    }


    private boolean shouldInit() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

}
