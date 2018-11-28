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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.litesuits.common.assist.Toastor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.hekr.sthome.common.TopbarSuperActivity;
import me.hekr.sthome.commonBaseView.CodeEdit;
import me.hekr.sthome.commonBaseView.ECAlertDialog;
import me.hekr.sthome.commonBaseView.VerfyDialog;
import me.hekr.sthome.http.HekrUser;
import me.hekr.sthome.http.HekrUserAction;
import me.hekr.sthome.tools.UnitTools;

public class RegisterActivity extends TopbarSuperActivity implements View.OnClickListener{
    private EditText et_phone, et_code,et_email;
    private CodeEdit codeEdit,codeEdit_firm;
    private Button btn_get_code, btn_register;
    private HekrUserAction hekrUserAction;
    private String phone, pwd,con_pwd,email;
    private Toastor toastor;
    private TextView tv_pid;
    private ImageView save_xieyi;
    private LinearLayout liner_phone,liner_email;
    private int type = 1; //1代表手机 2代表邮箱
    private int xieyi = 0;

    @Override
    protected void onCreateInit() {
        initData();
        initView();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_register;
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
        save_xieyi = (ImageView)findViewById(R.id.save_password);
        liner_phone = (LinearLayout)findViewById(R.id.liner_phone);
        liner_email = (LinearLayout)findViewById(R.id.liner_email);
        tv_pid = (TextView) findViewById(R.id.tv_pid);
        et_phone = (EditText) findViewById(R.id.et_phone);
        et_email = (EditText)findViewById(R.id.et_email);
        et_code = (EditText) findViewById(R.id.et_code);
        codeEdit = (CodeEdit) findViewById(R.id.code1);
        codeEdit_firm =(CodeEdit)findViewById(R.id.code2);
        btn_get_code = (Button) findViewById(R.id.btn_get_code);
        btn_register = (Button) findViewById(R.id.btn_register);
        btn_get_code.setOnClickListener(this);
        btn_register.setOnClickListener(this);
        btn_register.setEnabled(false);
        getTopBarView().setTopBarStatus(1, 2, getResources().getString(R.string.register), type == 1 ? getResources().getString(R.string.email_register) : getResources().getString(R.string.phone_register), new View.OnClickListener() {
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


        if(type==1){
            liner_email.setVisibility(View.GONE);
            liner_phone.setVisibility(View.VISIBLE);
        }else{
            liner_email.setVisibility(View.VISIBLE);
            liner_phone.setVisibility(View.GONE);
        }

        tv_pid.setText(getClickableSpan(0));
        tv_pid.setMovementMethod(LinkMovementMethod.getInstance());//必须设置否则无效
        save_xieyi.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_get_code:
                if(type == 1){
                    phone = et_phone.getText().toString().trim();
                    if (!TextUtils.isEmpty(phone)) {

                        VerfyDialog dialog = new VerfyDialog();
                        dialog.showDialog(this,hekrUserAction,phone,HekrUserAction.REGISTER_TYPE_PHONE,1);
                        dialog.show();
                    } else {
                        toastor.showSingleLongToast(getResources().getString(R.string.please_input_phone));
                    }
                }else if(type == 2){
                    email = et_email.getText().toString().trim();
                    if (!TextUtils.isEmpty(email)) {

                        VerfyDialog dialog = new VerfyDialog();
                        dialog.showDialog(this,hekrUserAction,email,HekrUserAction.REGISTER_TYPE_PHONE,2);
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

                            String pattern = "^(?![a-zA-Z]+$)(?![A-Z0-9]+$)(?![A-Z\\W_!@#$%^&*`~()-+=]+$)(?![a-z0-9]+$)(?![a-z\\W_!@#$%^&*`~()-+=]+$)(?![0-9\\W_!@#$%^&*`~()-+=]+$)[a-zA-Z0-9\\W_!@#$%^&*`~()-+=]{10,30}$";

                            Pattern r = Pattern.compile(pattern);
                            Matcher m = r.matcher(pwd);
                            if(m.matches()){
                                register(phone, pwd, code);
                            }else {
                                toastor.showSingleLongToast(getResources().getString(R.string.three_zifu));
                            }



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
                            String pattern = "^(?![a-zA-Z]+$)(?![A-Z0-9]+$)(?![A-Z\\W_!@#$%^&*`~()-+=]+$)(?![a-z0-9]+$)(?![a-z\\W_!@#$%^&*`~()-+=]+$)(?![0-9\\W_!@#$%^&*`~()-+=]+$)[a-zA-Z0-9\\W_!@#$%^&*`~()-+=]{10,30}$";

                            Pattern r = Pattern.compile(pattern);
                            Matcher m = r.matcher(pwd);
                            if(m.matches()){
                                registerByEmail(email,pwd,code);
                            }else {
                                toastor.showSingleLongToast(getResources().getString(R.string.three_zifu));
                            }
                        }
                    }
                    else{
                        toastor.showSingleLongToast(getResources().getString(R.string.please_input_complete));
                    }

                }



                break;
            case R.id.save_password:
                if(xieyi==0){
                    xieyi =1;
                    save_xieyi.setImageResource(R.drawable.save_pass_1);
                    btn_register.setEnabled(true);
                }else {
                    xieyi = 0;
                    save_xieyi.setImageResource(R.drawable.save_pass_0);
                    btn_register.setEnabled(false);
                }
                break;
        }
    }


    private void register(final String phoneNumber,final String pwd, String code) {
        hekrUserAction.registerByPhone(phoneNumber, pwd, code, new HekrUser.RegisterListener() {
            @Override
            public void registerSuccess(String uid) {
                toastor.showSingleLongToast(getResources().getString(R.string.success_register));
                Intent intent = new Intent();
                intent.putExtra("username",phoneNumber);
                intent.putExtra("password",pwd);
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void registerFail(int errorCode) {
                showError(errorCode);
            }
        });
    }

    private void registerByEmail(final String email,final String pwd,final String code) {
        hekrUserAction.registerByEmail(email, pwd,code,  new HekrUser.RegisterListener() {
            @Override
            public void registerSuccess(String uid) {
                ECAlertDialog D = ECAlertDialog.buildPositiveAlert(RegisterActivity.this, R.string.check_email, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.putExtra("username",email);
                        intent.putExtra("password",pwd);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });
                D.setTitleVisibility(View.GONE);
                D.show();
            }

            @Override
            public void registerFail(int errorCode) {
                showError(errorCode);
            }
        });
    }

    private void showError(int errorCode) {
        toastor.showSingleLongToast(UnitTools.errorCode2Msg(RegisterActivity.this,errorCode));
    }

    private void switchType(){
        switch (type){
            case 1:
                type=2;
                getTopBarView().setSettingTxt(getResources().getString(R.string.phone_register));
                et_phone.setText("");
                et_phone.clearFocus();
                et_code.setText("");
                et_code.clearFocus();
                liner_email.setVisibility(View.VISIBLE);
                liner_phone.setVisibility(View.GONE);
                break;
            case 2:
                type=1;
                getTopBarView().setSettingTxt(getResources().getString(R.string.email_register));
                et_email.setText("");
                et_email.clearFocus();
                liner_email.setVisibility(View.GONE);
                liner_phone.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }

    }


    private SpannableString getClickableSpan(int start) {
        View.OnClickListener l = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, ServeIntroActivity.class));
            }
        };

        SpannableString spanableInfo = new SpannableString(
                getResources().getString(R.string.serve_instruction));

        int end = spanableInfo.length();
        spanableInfo.setSpan(new Clickable(l), start, end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanableInfo;
    }

    /**
     * 内部类，用于截获点击富文本后的事件
     */
    class Clickable extends ClickableSpan implements View.OnClickListener {
        private final View.OnClickListener mListener;

        public Clickable(View.OnClickListener mListener) {
            this.mListener = mListener;
        }

        @Override
        public void onClick(View v) {
            mListener.onClick(v);
        }
        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(getResources().getColor(R.color.black));
            ds.setUnderlineText(true);    //去除超链接的下划线
        }
    }

}
