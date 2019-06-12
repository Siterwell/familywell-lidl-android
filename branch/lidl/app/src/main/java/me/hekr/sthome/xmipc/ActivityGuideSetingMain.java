package me.hekr.sthome.xmipc;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.lib.funsdk.support.FunSupport;
import com.lib.funsdk.support.OnFunDeviceListener;
import com.lib.funsdk.support.config.BaseConfig;
import com.lib.funsdk.support.config.SystemInfo;
import com.lib.funsdk.support.models.FunDevType;
import com.lib.funsdk.support.models.FunDevice;

import java.util.List;
import java.util.Map;

import me.hekr.sthome.R;
import me.hekr.sthome.common.CCPAppManager;
import me.hekr.sthome.common.TopbarIpcSuperActivity;
import me.hekr.sthome.commonBaseView.ECAlertDialog;
import me.hekr.sthome.model.modelbean.MonitorBean;

/**
 * Created by Administrator on 2017/8/30.
 */

public class ActivityGuideSetingMain extends TopbarIpcSuperActivity implements View.OnClickListener{
   private final static String TAG = ActivityGuideSetingMain.class.getName();
    private RelativeLayout commonsetting_liner,expertsetting_liner,codesetting_liner,storage_liner,relativeLayout_common,relativeLayout_wireless;
    private int id;
    private FunDevice mFunDevice;
    private ECAlertDialog alertDialog;
    private String devid;
    private List<MonitorBean> lists;
    private List<MonitorBean> list;
    private com.alibaba.fastjson.JSONObject object;


    @Override
    protected void onCreateInit() {
        initview();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_pic_setting_main;
    }



    private void initview(){
        lists = CCPAppManager.getClientUser().getMonitorList();
        id = getIntent().getIntExtra("deviceid", 0);
        mFunDevice = FunSupport.getInstance().findDeviceById(id);
        if ( null == mFunDevice ) {
            finish();
            return;
        }

        devid = getIntent().getStringExtra("devid");
        textView_title.setText(getResources().getString(R.string.setting));
        commonsetting_liner = (RelativeLayout)findViewById(R.id.common);
        codesetting_liner = (RelativeLayout)findViewById(R.id.codesetting);
        storage_liner = (RelativeLayout)findViewById(R.id.storage);
        expertsetting_liner =(RelativeLayout)findViewById(R.id.expertsetting);
        relativeLayout_common = (RelativeLayout)findViewById(R.id.tongyong);
        relativeLayout_wireless = (RelativeLayout)findViewById(R.id.wireless);
        textView_back.setOnClickListener(this);
        commonsetting_liner.setOnClickListener(this);
        codesetting_liner.setOnClickListener(this);
        storage_liner.setOnClickListener(this);
        expertsetting_liner.setOnClickListener(this);
        relativeLayout_common.setOnClickListener(this);
        relativeLayout_wireless.setOnClickListener(this);
        // 注册设备操作回调
        SystemInfo baseConfig = (SystemInfo)mFunDevice.getConfig(SystemInfo.CONFIG_NAME);
        if(baseConfig!=null){
            if(FunDevType.getType(baseConfig.getDeviceType())==FunDevType.EE_DEV_GUN_MONITOR){
                relativeLayout_wireless.setVisibility(View.VISIBLE);
            }
        }

    }


    @Override
    public void onClick(View view) {
         switch (view.getId()){
             case R.id.backBtnInTopLayout:
                 finish();
                 break;
             case R.id.codesetting:
                 Intent h = new Intent(this,ActivityGuideDeviceChangePassw.class);
                 h.putExtra("FUN_DEVICE_ID",id);
                 startActivity(h);
                 break;
             case R.id.common:
                 Intent h2 = new Intent(this,ActivityGuideSetingCommon.class);
                 h2.putExtra("FUN_DEVICE_ID",id);
                 h2.putExtra("FUN_DEVICE_SN",devid);
                 startActivity(h2);
                 break;
             case R.id.storage:
                 Intent h3 = new Intent(this,ActivityGuideDeviceSetupStorage.class);
                 h3.putExtra("FUN_DEVICE_ID",id);
                 startActivity(h3);
                 break;
             case R.id.tongyong:
                 Intent h4 = new Intent(this,ActivityGuideSetingInfo.class);
                 h4.putExtra("FUN_DEVICE_ID",id);
                 startActivity(h4);
                 break;
             case R.id.expertsetting:
                 Intent j = new Intent(this,ActivityGuideSettingExpert.class);
                 j.putExtra("FUN_DEVICE_ID",id);
                 startActivity(j);
                 break;
             case R.id.wireless:
                 Intent intent = new Intent(this,ActivityGuideDeviceWifiConfigNew.class);
                 intent.putExtra("wireless",true);
                 startActivity(intent);
                 break;
         }
    }

}
