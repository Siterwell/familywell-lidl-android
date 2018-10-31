package me.hekr.sthome.xmipc;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

import me.hekr.sthome.R;
import me.hekr.sthome.common.TopbarIpcSuperActivity;
import me.hekr.sthome.service.NetWorkUtils;

/**
 * Created by st on 2017/12/25.
 */

public class ActivityGuideDeviceBeforeWifiConfig extends TopbarIpcSuperActivity implements View.OnClickListener{

    private Button btn_next;

    @Override
    protected void onCreateInit() {
         textView_title.setText(R.string.add_my_ipc);
         textView_back.setOnClickListener(this);
        btn_next= (Button)findViewById(R.id.next);
        btn_next.setOnClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_ipc_before_wifi_config;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.backBtnInTopLayout:
                finish();
                break;
            case R.id.next:

                if(NetWorkUtils.getNetWorkType(this)<4){
                    showToast(R.string.no_wifi);
                }else{
                    Intent intent = new Intent(this,ActivityGuideDeviceWifiConfigNew.class);
                    startActivity(intent);
                }

                break;

        }
    }
}
