package me.hekr.sthome.xmipc;

import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;

import com.lib.funsdk.support.FunSupport;
import com.lib.funsdk.support.models.FunDevice;

import me.hekr.sthome.R;
import me.hekr.sthome.common.TopbarIpcSuperActivity;

/**
 * Created by Administrator on 2017/8/31.
 */

public class ActivityGuideSettingExpert extends TopbarIpcSuperActivity implements View.OnClickListener{
    private RelativeLayout relativeLayout_record,relativeLayout_alarm,relativeLayout_image,relativeLayout_other;
    private int id;
    private FunDevice mFunDevice;
    @Override
    protected void onCreateInit() {
        initView();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_pic_setting_expert;
    }

    private void initView(){
        id = getIntent().getIntExtra("FUN_DEVICE_ID", 0);
        mFunDevice = FunSupport.getInstance().findDeviceById(id);
        if ( null == mFunDevice ) {
            finish();
            return;
        }

        textView_back.setOnClickListener(this);
        textView_title.setText(getResources().getString(R.string.device_setup_expert));
        relativeLayout_record = (RelativeLayout)findViewById(R.id.recordsetting);
        relativeLayout_alarm  = (RelativeLayout)findViewById(R.id.alarmsetting);
        relativeLayout_image  = (RelativeLayout)findViewById(R.id.imagesetting);
        relativeLayout_other  = (RelativeLayout)findViewById(R.id.othersetting);
        relativeLayout_record.setOnClickListener(this);
        relativeLayout_alarm.setVisibility(View.GONE);
        relativeLayout_image.setVisibility(View.GONE);
        relativeLayout_other.setVisibility(View.GONE);
        relativeLayout_alarm.setOnClickListener(this);
        relativeLayout_image.setOnClickListener(this);
        relativeLayout_other.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
         switch (view.getId()){
             case R.id.backBtnInTopLayout:
                 finish();
                 break;
             case R.id.recordsetting:
                 Intent i  = new Intent(this,ActivityGuideDeviceSetupRecord.class);
                 i.putExtra("FUN_DEVICE_ID",id);
                 startActivity(i);
                 break;
             case R.id.alarmsetting:
                 Intent i2  = new Intent(this,ActivityGuideDeviceSetupAlarm.class);
                 i2.putExtra("FUN_DEVICE_ID",id);
                 startActivity(i2);
                 break;
             case R.id.imagesetting:
                 Intent i3  = new Intent(this,ActivityGuideDeviceSetupCamera.class);
                 i3.putExtra("FUN_DEVICE_ID",id);
                 startActivity(i3);
                 break;
             case R.id.othersetting:
                 Intent i4  = new Intent(this,ActivityGuideDeviceSetupExpert.class);
                 i4.putExtra("FUN_DEVICE_ID",id);
                 startActivity(i4);
                 break;
         }
    }
}
