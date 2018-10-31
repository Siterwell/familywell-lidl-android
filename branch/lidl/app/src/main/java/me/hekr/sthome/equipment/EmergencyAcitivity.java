package me.hekr.sthome.equipment;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import me.hekr.sthome.R;
import me.hekr.sthome.common.CCPAppManager;
import me.hekr.sthome.common.TopbarSuperActivity;
import me.hekr.sthome.commonBaseView.SettingItem;

/**
 * Created by gc-0001 on 2017/2/9.
 */
public class EmergencyAcitivity extends TopbarSuperActivity implements View.OnClickListener {
    private final String TAG = "EmergencyAcitivity";
    private SettingItem settingItem;
    private String phone;
    private Button button;

    @Override
    protected void onCreateInit() {

        init();
        initdata();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_emergency;
    }

    private void initdata(){
        phone = CCPAppManager.getClientUser().getDescription();
        if(TextUtils.isEmpty(phone)){
            settingItem.setDetailText(getResources().getString(R.string.not_setting));
        }else{
            settingItem.setDetailText(phone);
        }
        settingItem.setOnClickListener(this);
    }

    private void init(){
        settingItem = (SettingItem)findViewById(R.id.emergency);
        button = (Button)findViewById(R.id.btnConfirm);
        button.setOnClickListener(this);
        getTopBarView().setTopBarStatus(1, 1, getResources().getString(R.string.emergency_number),null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }, null);
    }

    @Override
    public void onClick(View v) {
         switch (v.getId()){
             case R.id.emergency:

                 if(TextUtils.isEmpty(phone)){
                     Toast.makeText(this,getResources().getString(R.string.please_input_emergency_call),Toast.LENGTH_SHORT).show();
                 }
                 else {

                     Intent intent = new Intent(Intent.ACTION_DIAL);
                     intent.setData(Uri.parse("tel:" + phone));
                     if (intent.resolveActivity(getPackageManager()) != null) {
                         startActivity(intent);
                     }
                 }

                 break;
             case R.id.btnConfirm:
                 startActivity(new Intent(EmergencyAcitivity.this,EmergencyEditActivity.class));
                 break;
         }
    }


    @Override
    protected void onResume() {
        super.onResume();
        // initSystemBar();
        initdata();
    }

}
