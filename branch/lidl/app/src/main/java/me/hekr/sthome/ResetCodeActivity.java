package me.hekr.sthome;

import android.content.DialogInterface;
import android.content.Intent;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.litesuits.common.assist.Toastor;

import me.hekr.sthome.common.TopbarSuperActivity;
import me.hekr.sthome.commonBaseView.CodeEdit;
import me.hekr.sthome.commonBaseView.ECAlertDialog;
import me.hekr.sthome.commonBaseView.VerfyDialog;
import me.hekr.sthome.http.HekrUser;
import me.hekr.sthome.http.HekrUserAction;
import me.hekr.sthome.tools.UnitTools;

public class ResetCodeActivity extends TopbarSuperActivity implements View.OnClickListener{
    private EditText et_phone, et_code,et_email;
    private CodeEdit codeEdit,codeEdit_firm;
    private Button btn_get_code, btn_register;
    private HekrUserAction hekrUserAction;
    private String phone, pwd,con_pwd,email;
    private Toastor toastor;
    private LinearLayout liner_phone,liner_email;
    private int type = 1; //1代表手机 2代表邮箱


    @Override
    protected void onCreateInit() {
        initData();
        initView();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_resetcode;
    }

    private void initData() {
        hekrUserAction = HekrUserAction.getInstance(this);
        toastor = new Toastor(this);
        UnitTools tools = new UnitTools(this);
        String lan = tools.readLanguage();

        if("zh".equals(lan)){
            type =1;
        }else{
            type = 2;
        }

    }

    private void initView() {
        liner_phone = (LinearLayout)findViewById(R.id.liner_phone);
        liner_email = (LinearLayout)findViewById(R.id.liner_email);
        et_phone = (EditText) findViewById(R.id.et_phone);
        et_email = (EditText)findViewById(R.id.et_email);
        et_code = (EditText) findViewById(R.id.et_code);
        codeEdit = (CodeEdit) findViewById(R.id.code1);
        codeEdit_firm =(CodeEdit)findViewById(R.id.code2);
        btn_get_code = (Button) findViewById(R.id.btn_get_code);
        btn_register = (Button) findViewById(R.id.btn_register);
        btn_get_code.setOnClickListener(this);
        btn_register.setOnClickListener(this);

        getTopBarView().setTopBarStatus(1, 2, getResources().getString(R.string.reset_code), type == 1 ? getResources().getString(R.string.email_find) : getResources().getString(R.string.phone_find), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchType();
            }
        });

        UnitTools tools = new UnitTools(this);
        String lan = tools.readLanguage();
        if("zh".equals(lan)){
            liner_email.setVisibility(View.GONE);
            liner_phone.setVisibility(View.VISIBLE);
        }
        else if("fr".equals(lan)){
            liner_email.setVisibility(View.VISIBLE);
            liner_phone.setVisibility(View.GONE);
            getTopBarView().getSettingTxt().setVisibility(View.GONE);
        }
        else{
            liner_email.setVisibility(View.VISIBLE);
            liner_phone.setVisibility(View.GONE);
            getTopBarView().getSettingTxt().setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_get_code:
                if(type == 1){
                    phone = et_phone.getText().toString().trim();
                    if (!TextUtils.isEmpty(phone)) {

                        VerfyDialog dialog = new VerfyDialog();
                        dialog.showDialog(this,hekrUserAction,phone,HekrUserAction.CODE_TYPE_RE_REGISTER,1);
                        dialog.show();
                    } else {
                        toastor.showSingleLongToast(getResources().getString(R.string.please_input_phone));
                    }
                }else if(type == 2){
                    email = et_email.getText().toString().trim();
                    if (!TextUtils.isEmpty(email)) {

                        VerfyDialog dialog = new VerfyDialog();
                        dialog.showDialog(this,hekrUserAction,email,HekrUserAction.CODE_TYPE_RE_REGISTER,2);
                        dialog.show();
                    } else {
                        toastor.showSingleLongToast(getResources().getString(R.string.please_input_email));
                    }
                }


                break;

            case R.id.btn_register:

                if(type==1){
                    String code = et_code.getText().toString().trim();
                    pwd = codeEdit.getCodeEdit().getText().toString().trim();
                    con_pwd = codeEdit_firm.getCodeEdit().getText().toString().trim();
                    if (!TextUtils.isEmpty(code) && !TextUtils.isEmpty(pwd)) {
                        if(pwd.length()<10){
                            toastor.showSingleLongToast(getResources().getString(R.string.password_length));
                        }else if(!con_pwd.equals(pwd)){
                            toastor.showSingleLongToast(getResources().getString(R.string.password_two_different));
                        }
                        else{
                            reset(phone, pwd, code);

                        }


                    } else {
                        toastor.showSingleLongToast(getResources().getString(R.string.please_input_complete));
                    }
                }else{
                    String code = et_code.getText().toString().trim();
                    email = et_email.getText().toString().trim();
                    pwd = codeEdit.getCodeEdit().getText().toString().trim();
                    con_pwd = codeEdit.getCodeEdit().getText().toString().trim();
                    if (!TextUtils.isEmpty(code) && !TextUtils.isEmpty(email) ) {
                        if(pwd.length()<10){
                            toastor.showSingleLongToast(getResources().getString(R.string.password_length));
                        }else if(!con_pwd.equals(pwd)){
                            toastor.showSingleLongToast(getResources().getString(R.string.password_two_different));
                        }
                        else{
                            resetByEmail(email,pwd,code);

                        }
                    }
                    else{
                        toastor.showSingleLongToast(getResources().getString(R.string.please_input_complete));
                    }

                }



                break;
        }
    }


    private void reset(final String phoneNumber,final String pwd, String code) {
        hekrUserAction.resetPwd(phoneNumber,code,pwd, new HekrUser.ResetPwdListener() {
            @Override
            public void resetSuccess() {
                toastor.showSingleLongToast(getResources().getString(R.string.success_reset));
                finish();
            }

            @Override
            public void resetFail(int errorCode) {
                showError(errorCode);
            }
        });


    }

    private void resetByEmail(final String email,final String pwd,final String code) {
        hekrUserAction.resetPwdByEmail(email,code,pwd, new HekrUser.ResetPwdListener() {
            @Override
            public void resetSuccess() {
                ECAlertDialog D = ECAlertDialog.buildPositiveAlert(ResetCodeActivity.this, R.string.success_reset, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                D.show();
            }

            @Override
            public void resetFail(int errorCode) {
                showError(errorCode);
            }
        });

    }

    private void showError(int errorCode) {
        toastor.showSingleLongToast(UnitTools.errorCode2Msg(ResetCodeActivity.this,errorCode));
    }

    private void switchType(){
        switch (type){
            case 1:
                type=2;
                getTopBarView().setSettingTxt(getResources().getString(R.string.phone_find));
                et_phone.setText("");
                et_phone.clearFocus();
                et_code.setText("");
                et_code.clearFocus();
                liner_email.setVisibility(View.VISIBLE);
                liner_phone.setVisibility(View.GONE);
                break;
            case 2:
                type=1;
                getTopBarView().setSettingTxt(getResources().getString(R.string.email_find));
                et_email.setText("");
                et_email.clearFocus();
                liner_email.setVisibility(View.GONE);
                liner_phone.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }

    }


}
