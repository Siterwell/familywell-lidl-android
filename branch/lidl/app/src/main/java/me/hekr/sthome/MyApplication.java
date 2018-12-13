package me.hekr.sthome;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Process;
import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.igexin.sdk.PushManager;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.util.List;

import io.fabric.sdk.android.Fabric;
import me.hekr.sdk.HekrSDK;
import me.hekr.sthome.push.GTPushService;
import me.hekr.sthome.push.RGTIntentService;
import me.hekr.sthome.service.SiterService;
import me.hekr.sthome.tools.CrashHandler;
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
        //推送服务初始化
        PushManager.getInstance().initialize(this.getApplicationContext(), GTPushService.class);
    // 注册 intentService 后 PushDemoReceiver 无效, sdk 会使用 DemoIntentService 传递数据,
    // AndroidManifest 对应保留一个即可(如果注册 DemoIntentService, 可以去掉 PushDemoReceiver, 如果注册了
    // IntentService, 必须在 AndroidManifest 中声明)
    PushManager.getInstance().registerPushIntentService(this.getApplicationContext(), RGTIntentService.class);
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
        CrashHandler.getInstance().init(getApplicationContext());
        Intent intent = new Intent(this, SiterService.class);
        startService(intent);
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
