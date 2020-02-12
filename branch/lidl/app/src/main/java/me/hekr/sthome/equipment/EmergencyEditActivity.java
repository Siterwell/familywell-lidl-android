package me.hekr.sthome.equipment;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import me.hekr.sthome.R;
import me.hekr.sthome.common.CCPAppManager;
import me.hekr.sthome.common.TopbarSuperActivity;
import me.hekr.sthome.http.HekrUser;
import me.hekr.sthome.http.HekrUserAction;
import me.hekr.sthome.model.modelbean.MonitorBean;
import me.hekr.sthome.tools.UnitTools;

/**
 * Created by gc-0001 on 2017/2/9.
 */
public class EmergencyEditActivity extends TopbarSuperActivity {
    private HekrUserAction hekrUserAction;
    private EditText et_emergency;
    private String phone = CCPAppManager.getClientUser().getDescription();

    @Override
    protected void onCreateInit() {
        initview();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_emergency_edit;
    }

    private void initview(){
        hekrUserAction = HekrUserAction.getInstance(this);
        et_emergency = (EditText)findViewById(R.id.et_emergency);
        getTopBarView().setTopBarStatus(1,
                2,
                getResources().getString(R.string.set_emergency_call),
                getResources().getString(R.string.ok),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hideSoftKeyboard();
                        finish();
                    }
                },
                new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                 List<MonitorBean> list = CCPAppManager.getClientUser().getMonitorList();
                        com.alibaba.fastjson.JSONObject object = new com.alibaba.fastjson.JSONObject();
                    com.alibaba.fastjson.JSONObject object2 = new com.alibaba.fastjson.JSONObject();
                    object2.put("monitor",list.toString());
                    object.put("extraProperties",object2);
                        object.put("description",et_emergency.getText().toString().trim());
                        hekrUserAction.setProfile(object, new HekrUser.SetProfileListener() {
                            @Override
                            public void setProfileSuccess() {
                                String rusult =et_emergency.getText().toString().trim();
//                                CCPAppManager.getClientUser(EmergencyEditActivity.this).setDescription(et_emergency.getText().toString().trim());
                                CCPAppManager.getClientUser().setDescription(rusult);
                                hideSoftKeyboard();
                                Toast.makeText(EmergencyEditActivity.this,getResources().getString(R.string.success),Toast.LENGTH_SHORT).show();
                                finish();
                            }

                            @Override
                            public void setProfileFail(int errorCode) {
                                Toast.makeText(EmergencyEditActivity.this, UnitTools.errorCode2Msg(EmergencyEditActivity.this,errorCode),Toast.LENGTH_SHORT).show();
                            }
                        });

                }
            }
        );

        if(!TextUtils.isEmpty(phone)){
            et_emergency.setHint(phone);
        }
    }
}
