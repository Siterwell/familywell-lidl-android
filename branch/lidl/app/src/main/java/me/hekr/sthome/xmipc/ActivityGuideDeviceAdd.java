package me.hekr.sthome.xmipc;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;

import me.hekr.sthome.R;
import me.hekr.sthome.common.TopbarIpcSuperActivity;
import me.hekr.sthome.service.NetWorkUtils;

/**
 * Created by st on 2017/12/25.
 */

public class ActivityGuideDeviceAdd extends TopbarIpcSuperActivity implements View.OnClickListener{

    private LinearLayout linearLayout_add_new,linearLayout_add_share,linearLayout_add_local;


    @Override
    protected void onCreateInit() {
         textView_title.setText(R.string.add_camera);
         textView_back.setOnClickListener(this);
         textView_setting.setVisibility(View.GONE);
        linearLayout_add_new = (LinearLayout)findViewById(R.id.add_new_ipc);
        linearLayout_add_share = (LinearLayout)findViewById(R.id.add_share_ipc);
        linearLayout_add_local=(LinearLayout)findViewById(R.id.add_local_ipc);
        linearLayout_add_new.setOnClickListener(this);
        linearLayout_add_share.setOnClickListener(this);
        linearLayout_add_local.setOnClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_ipc;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.backBtnInTopLayout:
                finish();
                break;
            case R.id.add_new_ipc:
                        Intent intent = new Intent(this,ActivityGuideDeviceBeforeWifiConfig.class);
                        startActivity(intent);
                break;
            case R.id.add_share_ipc:
                Intent intent2 = new Intent(this,ActivityGuideDeviceWifiConfig.class);
                startActivity(intent2);
                break;
            case R.id.add_local_ipc:
                if(NetWorkUtils.getNetWorkType(this)<4){
                    showToast(R.string.no_wifi);
                }else{
                    Intent intent3 = new Intent(this,ActivityGuideDeviceListLan.class);
                    startActivity(intent3);
                }

                break;
            default:break;
        }
    }
}
