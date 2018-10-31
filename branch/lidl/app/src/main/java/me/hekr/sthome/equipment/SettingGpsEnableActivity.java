package me.hekr.sthome.equipment;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.greenrobot.eventbus.EventBus;

import java.io.InvalidClassException;

import me.hekr.sthome.R;
import me.hekr.sthome.common.TopbarSuperActivity;
import me.hekr.sthome.event.ThcheckEvent;
import me.hekr.sthome.tools.ECPreferenceSettings;
import me.hekr.sthome.tools.ECPreferences;

/**
 * Created by TracyHenry on 2018/3/9.
 */

public class SettingGpsEnableActivity extends TopbarSuperActivity implements View.OnClickListener{
    private ImageView imageView_no_gps;
    private ImageView imageView_phone_gps;
    private LinearLayout linearLayout_no_gps;
    private LinearLayout linearLayout_phone_gps;

    @Override
    protected void onCreateInit() {
        getTopBarView().setTopBarStatus(1, 2, getResources().getString(R.string.gps_setting), null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        },null);
        imageView_no_gps = (ImageView)findViewById(R.id.accessory_checked1);
        imageView_phone_gps= (ImageView)findViewById(R.id.accessory_checked2);
        linearLayout_no_gps = (LinearLayout)findViewById(R.id.content1);
        linearLayout_phone_gps = (LinearLayout)findViewById(R.id.content2);
        linearLayout_no_gps.setOnClickListener(this);
        linearLayout_phone_gps.setOnClickListener(this);
        if("yes".equals(getGpsSetting())){
            imageView_no_gps.setVisibility(View.GONE);
            imageView_phone_gps.setVisibility(View.VISIBLE);
        }else if("no".equals(getGpsSetting())){
            imageView_no_gps.setVisibility(View.VISIBLE);
            imageView_phone_gps.setVisibility(View.GONE);
        }

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_switch_gps;
    }

    private String getGpsSetting(){

        SharedPreferences sharedPreferences = ECPreferences.getSharedPreferences();
        ECPreferenceSettings flag = ECPreferenceSettings.SETTINGS_PGS_SETTING;
        String autoflag = sharedPreferences.getString(flag.getId(), (String) flag.getDefaultValue());
        return autoflag;
    }

    @Override
    public void onClick(View view) {
          switch (view.getId()){
              case R.id.content1:
                  showProgressDialog(getResources().getString(R.string.wait));
                  try {
                      ECPreferences.savePreference(ECPreferenceSettings.SETTINGS_PGS_SETTING, "no", true);
                  } catch (InvalidClassException e) {
                      e.printStackTrace();
                  }
                  new Thread(){
                      @Override
                      public void run() {
                          try {
                              Thread.sleep(1000);
                          } catch (InterruptedException e) {
                              e.printStackTrace();
                          }
                          handler.sendEmptyMessage(1);
                      }
                  }.start();
                  break;
              case R.id.content2:
                  showProgressDialog(getResources().getString(R.string.wait));
                  try {
                      ECPreferences.savePreference(ECPreferenceSettings.SETTINGS_PGS_SETTING, "yes", true);
                  } catch (InvalidClassException e) {
                      e.printStackTrace();
                  }
                  new Thread(){
                      @Override
                      public void run() {
                          try {
                              Thread.sleep(1000);
                          } catch (InterruptedException e) {
                              e.printStackTrace();
                          }
                          handler.sendEmptyMessage(1);
                      }
                  }.start();
                  break;

          }
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    ThcheckEvent thcheckEvent = new ThcheckEvent();
                    EventBus.getDefault().post(thcheckEvent);
                    finish();
                    break;
            }
        }
    };

}
