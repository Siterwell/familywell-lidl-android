package me.hekr.sthome.updateApp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.util.concurrent.ExecutionException;

import me.hekr.sthome.MyApplication;
import me.hekr.sthome.R;
import me.hekr.sthome.commonBaseView.ECAlertDialog;
import me.hekr.sthome.commonBaseView.SettingItem;
import me.hekr.sthome.service.NetWorkUtils;
import me.hekr.sthome.tools.Config;
import me.hekr.sthome.tools.ECPreferenceSettings;
import me.hekr.sthome.tools.ECPreferences;

/**
 * ClassName:UpdateAppAuto
 * 作者：Henry on 2017/3/30 13:52
 * 邮箱：xuejunju_4595@qq.com
 * 描述:自动更新，HTT访问服务器地址文件中的版本号大小，与APP的版本号进行比较，若大于本地版本则弹出对话框
 */
public class UpdateAppAuto {
    private final String TAG = "UpdateAppAuto";
    private Context context;
    private Handler handlerUpdate;
    private final static int DOWN_UPDATE = 11;
    private int progress = 0;
    private static boolean flag_checkupdate = false;
    private int count=0;
    private SettingItem updateSetitem;
    private boolean isFlag_checkupdate;
    public UpdateAppAuto(Context context) {
        Log.i(TAG,"UpdateAppAuto create");
        this.context = context;
        handlerUpdate = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        getUpdateInfo();
                        break;
                    case 3:
                        confirm((Config.UpdateInfo) msg.obj);
                        break;
                }
            }
        };
    }


    public UpdateAppAuto(Context context, SettingItem updateSetitem2, boolean flag_checkupdate) {
        Log.i(TAG,"UpdateAppAuto create");
        this.context = context;
        this.updateSetitem = updateSetitem2;
        this.isFlag_checkupdate = flag_checkupdate;
        handlerUpdate = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        getUpdateInfo();
                        break;
                    case 3:
                        if(NetWorkUtils.getNetWorkType(MyApplication.getAppContext()) == 4){
                            updateSetitem.setNewUpdateVisibility(true);
                            updateSetitem.setTag((Config.UpdateInfo) msg.obj);
                        }
                        break;
                }
            }
        };

        if(isFlag_checkupdate){
            updateSetitem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                            if(updateSetitem.getNewUpdateVisibility()==View.VISIBLE && !UpdateService.flag_updating){


                                Config.UpdateInfo info = (Config.UpdateInfo)v.getTag();
                                confirm(info);
                            }


                }
            });
        }

    }


    private void confirm(Config.UpdateInfo info) {
        String appname =  context.getPackageName();
        Log.i(TAG,"appname:"+appname);

        int verCode = Config.getVerCode(context, context.getPackageName());
        String verName = Config.getVerName(context, context.getPackageName());

        String ds = String.format(context.getResources().getString(R.string.update_alert),verName,verCode,info.name,info.code);

        try {
            ECAlertDialog dialog = ECAlertDialog.buildAlert(MyApplication.getActivity(),ds,context.getResources().getString(R.string.cancel), context.getResources().getString(R.string.ok) , new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {


                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
//                        Toast.makeText(context,context.getResources().getString(R.string.background_update),Toast.LENGTH_LONG).show();
//                        dialog.dismiss();
//                        Intent intent = new Intent(context,UpdateService.class);
//                        intent.putExtra("Key_App_Name", Config.UPDATE_APKNAME);
//                        intent.putExtra("Key_Down_Url", Config.ApkUrl);
//                        context.startService(intent);
                    String url = Config.ApkUrl;
                    if(!"hekr.me".equals(getDomain())){
                        url = "https://play.google.com/store/apps/details?id="+context.getPackageName();
                    }
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse(url);
                    intent.setData(content_url);
                    MyApplication.getActivity().startActivity(intent);

                }
            });
            dialog.show();
        }catch (Exception e){
            e.printStackTrace();
        }


    }


    private void update() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(Environment
                        .getExternalStorageDirectory(), Config.UPDATE_APKNAME)),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    public void getUpdateInfo(){
        // get app latest version number from Google Play
        VersionCheckerTask versionChecker = new VersionCheckerTask();
        try {
            Document document = versionChecker.execute().get();
            Element element = document.select("div:matchesOwn(^Current Version$)").first().parent().select("span").first();
            String version = element.text();
            int code = (int) (Float.parseFloat(version)*1000);

//            Log.d(TAG, "[RYAN] getUpdateInfo > version: " + version + ", code: " + code);

            Config.UpdateInfo ds = new Config.UpdateInfo();
            ds.setCode(code);
            ds.setName(version);
            if (Config.getVerCode(context, context.getPackageName()) < code) {
                handlerUpdate.sendMessage(handlerUpdate.obtainMessage(3, ds));
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void initCheckUpate(){

        if(!flag_checkupdate) {
            flag_checkupdate = true;
            new Thread() {
                public void run() {

                    while(count<2)
                    {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        count++;
                    }
                    count = 0;
                    handlerUpdate.sendMessage(handlerUpdate.obtainMessage(1));

                }
            }.start();
        }

    }

    private String getDomain(){

        SharedPreferences sharedPreferences = ECPreferences.getSharedPreferences();
        ECPreferenceSettings flag = ECPreferenceSettings.SETTINGS_DOMAIN;
        String autoflag = sharedPreferences.getString(flag.getId(), (String) flag.getDefaultValue());
        return autoflag;
    }

}
