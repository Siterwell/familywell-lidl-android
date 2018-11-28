package me.hekr.sthome.equipment;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import me.hekr.sthome.R;
import me.hekr.sthome.common.TopbarSuperActivity;
import me.hekr.sthome.http.HekrUser;
import me.hekr.sthome.http.HekrUserAction;
import me.hekr.sthome.tools.UnitTools;

/**
 * Created by gc-0001 on 2017/4/21.
 */

public class ChangePasswordActivity extends TopbarSuperActivity {
    private EditText oldtext,newtext,confirmtext;
    @Override
    protected void onCreateInit() {
        initView();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_change_password;
    }

    private void initView(){

         getTopBarView().setTopBarStatus(1, 2, getResources().getString(R.string.modify_password), getResources().getString(R.string.ok), new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 hideSoftKeyboard();
                 finish();
             }
         }, new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 modify();
             }
         });
        oldtext = (EditText)findViewById(R.id.old_code);
        newtext = (EditText)findViewById(R.id.new_code);
        confirmtext = (EditText)findViewById(R.id.new_code_confirm);

    }


    private void modify(){
        String oldpsw = oldtext.getText().toString().trim();
        String newpsw = newtext.getText().toString().trim();
        String confirmpsw = confirmtext.getText().toString().trim();

        if(TextUtils.isEmpty(oldpsw)){
            Toast.makeText(this,getResources().getString(R.string.setting_old_password),Toast.LENGTH_SHORT).show();
            return;
        }else if(TextUtils.isEmpty(newpsw)){
            Toast.makeText(this,getResources().getString(R.string.setting_new_password_hint),Toast.LENGTH_SHORT).show();
            return;
        }else if(TextUtils.isEmpty(confirmpsw)){
            Toast.makeText(this,getResources().getString(R.string.setting_confirm_new_password_hint),Toast.LENGTH_SHORT).show();
            return;
        }else if(!confirmpsw.equals(newpsw)){
            Toast.makeText(this,getResources().getString(R.string.password_two_different),Toast.LENGTH_SHORT).show();
            return;
        }else if(confirmpsw.length()<10){
            Toast.makeText(this,getResources().getString(R.string.password_length),Toast.LENGTH_SHORT).show();
            return;
        }else{
             showProgressDialog(getResources().getString(R.string.modifying_newpassword));
            HekrUserAction.getInstance(this).changePassword(newpsw, oldpsw, new HekrUser.ChangePwdListener() {
                @Override
                public void changeSuccess() {
                    Toast.makeText(ChangePasswordActivity.this, getResources().getString(R.string.success),Toast.LENGTH_SHORT).show();
                    hideProgressDialog();
                    hideSoftKeyboard();
                    finish();
                }

                @Override
                public void changeFail(int errorCode) {
                    hideProgressDialog();
                    if(errorCode == 400014){
                        Toast.makeText(ChangePasswordActivity.this, getResources().getString(R.string.code_fault),Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(ChangePasswordActivity.this, UnitTools.errorCode2Msg(ChangePasswordActivity.this,errorCode),Toast.LENGTH_SHORT).show();
                    }

                }
            });

        }

    }
}
