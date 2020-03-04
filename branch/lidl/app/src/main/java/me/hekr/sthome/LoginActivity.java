package me.hekr.sthome;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.litesuits.common.assist.Toastor;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InvalidClassException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.hekr.sdk.Constants;
import me.hekr.sdk.Hekr;
import me.hekr.sdk.inter.HekrCallback;
import me.hekr.sdk.utils.CacheUtil;
import me.hekr.sthome.common.CCPAppManager;
import me.hekr.sthome.commonBaseView.CodeEdit;
import me.hekr.sthome.commonBaseView.LoginLogPopupwindow;
import me.hekr.sthome.commonBaseView.ProgressDialog;
import me.hekr.sthome.http.HekrUser;
import me.hekr.sthome.http.HekrUserAction;
import me.hekr.sthome.http.bean.UserBean;
import me.hekr.sthome.main.MainActivity;
import me.hekr.sthome.model.modelbean.ClientUser;
import me.hekr.sthome.tools.AccountUtil;
import me.hekr.sthome.tools.ConnectionPojo;
import me.hekr.sthome.tools.ECPreferenceSettings;
import me.hekr.sthome.tools.ECPreferences;
import me.hekr.sthome.tools.EncryptUtil;
import me.hekr.sthome.tools.LOG;
import me.hekr.sthome.tools.SystemTintManager;
import me.hekr.sthome.tools.UnitTools;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener,LoginLogPopupwindow.ItemOperationListener {
    private EditText et_phone;
    private CodeEdit codeEdit;
    private String phone, pwd;
    private Toastor toastor;
    private ImageView rem_img;
    private boolean isauto;
    private RelativeLayout root;

    private ProgressDialog progressDialog;
    private LoginLogPopupwindow loginLogPopupwindow;
    private ImageButton showLogButton;
    private RelativeLayout userLayout;
    private String log;
    private ArrayList<LoginLogPopupwindow.UserBean> userlist;
    private final int REQUEST_REGISTER = 1;
    private final static String TAG = LoginActivity.class.getName();

    private Disposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        tcpGetDomain();
        LOG.I(TAG,"打开app的标识为："+ConnectionPojo.getInstance().open_app);
        LOG.D(TAG, "[RYAN] onCreate");

        if (AccountUtil.forceLogout()) {
            // Force logout because of updated data encryption algorithm
            HekrUserAction.getInstance(this).userLogout();
            CCPAppManager.setClientUser(null);

            initData();
            initView();
            initLog();
            initSystemBar();
        } else {
            try{
                if (CCPAppManager.getUserId()==null) {
                    initData();
                    initView();
                    initLog();
                    initSystemBar();
                } else {
                    login();
                }
            } catch (Exception e){
                initData();
                initView();
                initLog();
                initSystemBar();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null) {
            disposable.dispose();
        }
    }

    private void initData() {
        toastor = new Toastor(this);
       // hekrUserAction = HekrUserAction.getInstance(this);
        isauto = isAutologin();
        phone = AccountUtil.getUsername();
        pwd = AccountUtil.getPassword();

    }

    private void initLog(){
        UnitTools tools = new UnitTools(this);
        log =  tools.readUserLog();

        if(TextUtils.isEmpty(log)){
            showLogButton.setVisibility(View.GONE);
        }else{
            userlist = new ArrayList<LoginLogPopupwindow.UserBean>();
            try {
                JSONArray array = new JSONArray(log);

                if(array.length()>0){
                    showLogButton.setVisibility(View.VISIBLE);
                    for(int i=0;i<array.length();i++){

                        LoginLogPopupwindow.UserBean bean = new LoginLogPopupwindow.UserBean();
                        bean.setUsername(array.getJSONObject(i).getString("username"));
                        bean.setPwd(array.getJSONObject(i).getString("pwd"));
                        userlist.add(bean);
                    }
                }
                else{
                    showLogButton.setVisibility(View.GONE);
                }



            } catch (JSONException e) {
                LOG.I("ceshi","string is null");
            }
        }


    }

    private boolean isAutologin(){

        SharedPreferences sharedPreferences = ECPreferences.getSharedPreferences();
        ECPreferenceSettings flag = ECPreferenceSettings.SETTINGS_REMEMBER_PASSWORD;
        boolean autoflag = sharedPreferences.getBoolean(flag.getId(), (boolean) flag.getDefaultValue());
        return autoflag;
    }

    private void initView() {

        root = findViewById(R.id.root);
        et_phone = findViewById(R.id.et_phone);
        codeEdit = findViewById(R.id.codeedit);
        rem_img = findViewById(R.id.save_password);
        showLogButton = findViewById(R.id.arrow);
        userLayout = findViewById(R.id.liner_phone);
        showLogButton.setOnClickListener(this);

        ImageView logo_img = findViewById(R.id.imageView1);
        logo_img.setImageResource(R.drawable.login_logo);
//        btn_qq = (Button) findViewById(R.id.btn_qq);
//        btn_wechat = (Button) findViewById(R.id.btn_wechat);
//        btn_weibo = (Button) findViewById(R.id.btn_weibo);

        findViewById(R.id.rem_text).setOnClickListener(this);
        rem_img.setOnClickListener(this);
        if(isauto){
            rem_img.setImageResource(R.drawable.save_pass_1);
        }else {
            rem_img.setImageResource(R.drawable.save_pass_0);
        }
        et_phone.setText(phone);
        if (isauto) {
            codeEdit.getCodeEdit().setText(pwd);
        }
        findViewById(R.id.btn_login).setOnClickListener(this);
//        btn_wechat.setOnClickListener(this);
//        btn_weibo.setOnClickListener(this);
//        btn_qq.setOnClickListener(this);
        findViewById(R.id.regist).setOnClickListener(this);
        findViewById(R.id.reset_code).setOnClickListener(this);
    }




    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                phone = et_phone.getText().toString().trim();
                pwd = codeEdit.getCodeEdit().getText().toString().trim();
                UnitTools unitTools = new UnitTools(this);
                unitTools.writeUserInro(phone,pwd);

                if (!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(pwd)) {
                    progressDialog = new ProgressDialog(LoginActivity.this);
                    progressDialog.setPressText(getResources().getString(R.string.wait));
                    progressDialog.show();

                    Hekr.getHekrUser().login(phone, pwd, new HekrCallback() {
                        @Override
                        public void onSuccess() {

                            final String id =  Hekr.getHekrUser().getUserId();

                            UserBean userBean = new UserBean(phone, pwd, CacheUtil.getUserToken(), CacheUtil.getString(Constants.REFRESH_TOKEN,""));
                            HekrUserAction.getInstance(LoginActivity.this).setUserCache(userBean);
                            HekrUserAction.getInstance(LoginActivity.this).getProfile(new HekrUser.GetProfileListener() {
                                @RequiresApi(api = Build.VERSION_CODES.O)
                                @Override
                                public void getProfileSuccess(Object object) {
                                    JSONObject d = JSON.parseObject(object.toString());
                                    LOG.I("ceshi",object.toString());
                                    ClientUser user = new ClientUser();
                                    user.setId(id);
                                    if(!TextUtils.isEmpty(d.getString("birthday"))) user.setBirthday(d.getLong("birthday"));
                                    user.setDescription(d.getString("description"));
                                    user.setEmail(d.getString("email"));
                                    user.setFirstName(d.getString("firstName"));
                                    user.setLastName(d.getString("lastName"));
                                    user.setGender(d.getString("gender"));
                                    user.setPhoneNumber(d.getString("phoneNumber"));
                                    if(!TextUtils.isEmpty(d.getString("updateDate")))  user.setUpdateDate(d.getLong("updateDate"));
                                    user.setMonitor(d.getJSONObject("extraProperties").getString("monitor"));
                                    CCPAppManager.setClientUser(user);
                                    try {
                                        ECPreferences.savePreference(ECPreferenceSettings.SETTINGS_FORCE_LOGOUT, false, true);
                                        ECPreferences.savePreference(ECPreferenceSettings.SETTINGS_REMEMBER_PASSWORD, isauto, true);
                                        ECPreferences.savePreference(ECPreferenceSettings.SETTINGS_USERNAME, EncryptUtil.encrypt(phone),true);
                                        ECPreferences.savePreference(ECPreferenceSettings.SETTINGS_PASSWORD, EncryptUtil.encrypt(pwd),true);
                                    } catch (InvalidClassException e) {
                                        e.printStackTrace();
                                    }

                                    if(isauto) {
                                        addToLog(phone,pwd);
                                    }
                                    else {
                                        addToLog(phone,"");
                                    }

                                    if(progressDialog!=null&progressDialog.isShowing()){
                                        progressDialog.dismiss();
                                    }
                                    Intent intent = new Intent(LoginActivity.this, InitActivity.class);
                                    intent.putExtra("login_flag",true);
                                    startActivity(intent);
                                    finish();
                                }

                                @Override
                                public void getProfileFail(int errorCode) {
                                    if(progressDialog!=null&progressDialog.isShowing()){
                                        progressDialog.dismiss();
                                    }
                                    toastor.showSingleLongToast(UnitTools.errorCode2Msg(LoginActivity.this,errorCode));
                                }
                            });

                        }

                        @Override
                        public void onError(int errorCode, String message) {
                            try {
                                JSONObject d = JSON.parseObject(message);
                                int code = d.getInteger("code");
                                toastor.showSingleLongToast(UnitTools.errorCode2Msg(LoginActivity.this,code));
                            }catch (Exception e){
                                e.printStackTrace();
                                toastor.showSingleLongToast(UnitTools.errorCode2Msg(LoginActivity.this,errorCode));
                            }finally {
                                if(progressDialog!=null&progressDialog.isShowing()){
                                    progressDialog.dismiss();
                                }
                            }

                        }
                    });

                } else {
                    toastor.showSingleLongToast(getResources().getString(R.string.please_input_complete));
                }
                break;
            case R.id.regist:
                Intent intent = new Intent(this, RegisterActivity.class);
                startActivityForResult(intent,REQUEST_REGISTER);
                break;
            case R.id.reset_code:
                startActivity(new Intent(LoginActivity.this, ResetCodeActivity.class));
                break;
            case R.id.rem_text:
            case R.id.save_password:
                if(isauto){
                    rem_img.setImageResource(R.drawable.save_pass_0);
                }else{
                    rem_img.setImageResource(R.drawable.save_pass_1);
                }
                isauto = !isauto;
                break;
            case R.id.arrow:
                initLog();
                showPopUp(userLayout);
                break;
//            case R.id.btn_qq:
//                Intent intent = new Intent(LoginActivity.this, HekrOAuthLoginActivity.class);
//                intent.putExtra(HekrOAuthLoginActivity.OAUTH_TYPE, HekrUserAction.OAUTH_QQ);
//                startActivityForResult(intent, HekrUserAction.OAUTH_QQ);
//                break;
//            case R.id.btn_wechat:
//                Intent intent2 = new Intent(LoginActivity.this, HekrOAuthLoginActivity.class);
//                intent2.putExtra(HekrOAuthLoginActivity.OAUTH_TYPE, HekrUserAction.OAUTH_WECHAT);
//                startActivityForResult(intent2, HekrUserAction.OAUTH_WECHAT);
//                break;
//            case R.id.btn_weibo:
//                Intent intent3 = new Intent(LoginActivity.this, HekrOAuthLoginActivity.class);
//                intent3.putExtra(HekrOAuthLoginActivity.OAUTH_TYPE, HekrUserAction.OAUTH_SINA);
//                startActivityForResult(intent3, HekrUserAction.OAUTH_SINA);
//                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode!=RESULT_OK) return;

        if(requestCode==REQUEST_REGISTER){
            String name = data.getExtras().getString("username");
            String psw = data.getExtras().getString("password");
            if(!TextUtils.isEmpty(name)){
                et_phone.setText(name);
            }

            if(!TextUtils.isEmpty(psw)){
                codeEdit.getCodeEdit().setText(psw);
            }


        }

    }


    public void initSystemBar(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            root.setFitsSystemWindows(true);//需要把根布局设置为这个属性 子布局则不会占用状态栏位置
            root.setClipToPadding(true);//需要把根布局设置为这个属性 子布局则不会占用状态栏位置

            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            // hide nav bar
//                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE);

        }
        SystemTintManager tintManager = new SystemTintManager(this);// 创建状态栏的管理实例
//        tintManager.setNavigationBarTintEnabled(true);// 激活导航栏设置
        tintManager.setStatusBarDarkMode(true, this);//false 状态栏字体颜色是白色 true 颜色是黑色
    }
    private void login(){
        if(ConnectionPojo.getInstance().open_app == 1){
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }else{
            startActivity(new Intent(LoginActivity.this, InitActivity.class));
            finish();
        }


    }

    private void showPopUp(View v) {


        int width =  v.getMeasuredWidth();
        int heght = v.getMeasuredHeight();
        loginLogPopupwindow = new LoginLogPopupwindow(this,log,width,heght);
        loginLogPopupwindow.setItemOperationListener(this);
        loginLogPopupwindow.showAsDropDown(v);
        hideSoftKeyboard();
    }


    /**
     * hide inputMethod
     */
    public void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(inputMethodManager != null ) {
            View localView = this.getCurrentFocus();
            if(localView != null && localView.getWindowToken() != null ) {
                IBinder windowToken = localView.getWindowToken();
                inputMethodManager.hideSoftInputFromWindow(windowToken, 0);
            }
        }
    }

    @Override
    public void setNameAndPwd(String name, String pwd) {
        et_phone.setText(name);
        codeEdit.getCodeEdit().setText(pwd);
       if(loginLogPopupwindow!=null&&loginLogPopupwindow.isShowing()) loginLogPopupwindow.dismiss();

    }

    @Override
    public void deleteItem(int index) {
        if(loginLogPopupwindow!=null&&loginLogPopupwindow.isShowing()) loginLogPopupwindow.dismiss();
         if(userlist.size()>0) userlist.remove(index);
         String ds = userlist.toString();
        UnitTools tools = new UnitTools(this);
        tools.writeUserLog(ds);
        initLog();
    }

    private void addToLog(String username ,String pwd){
        boolean flag_repeat = false;
        UnitTools tools = new UnitTools(this);
        String log =  tools.readUserLog();
        ArrayList<LoginLogPopupwindow.UserBean> userlist = new ArrayList<LoginLogPopupwindow.UserBean>();
        if(TextUtils.isEmpty(log)){
            LoginLogPopupwindow.UserBean bean = new LoginLogPopupwindow.UserBean();
            bean.setUsername(username);
            bean.setPwd(pwd);
            userlist.add(0,bean);

        }else{
            try {
                JSONArray array = new JSONArray(log);
                for(int i=0;i<array.length();i++){
                    if(array.getJSONObject(i).getString("username").equals(username)&&array.getJSONObject(i).getString("pwd").equals(pwd)){
                        flag_repeat = true;
                    }
                    LoginLogPopupwindow.UserBean bean = new LoginLogPopupwindow.UserBean();
                    bean.setUsername(array.getJSONObject(i).getString("username"));
                    bean.setPwd(array.getJSONObject(i).getString("pwd"));
                    userlist.add(bean);
                }

                if(array.length()>=0&&array.length()<5){
                    if(!flag_repeat){
                        LoginLogPopupwindow.UserBean bean = new LoginLogPopupwindow.UserBean();
                        bean.setUsername(username);
                        bean.setPwd(pwd);
                        userlist.add(0,bean);
                    }
                } else{
                    if(!flag_repeat) {
                        LoginLogPopupwindow.UserBean bean = new LoginLogPopupwindow.UserBean();
                        bean.setUsername(username);
                        bean.setPwd(pwd);
                        userlist.add(0, bean);
                        userlist.remove(userlist.size() - 1);
                    }
                }
            } catch (JSONException e) {
                LOG.I("ceshi","string is null");
            }
        }
        tools.writeUserLog(userlist.toString());

    }

    private void tcpGetDomain(){

        Constants.setOnlineSite("hekreu.me");

        Observable.create(new ObservableOnSubscribe<String>() {

            @Override
            public void subscribe(ObservableEmitter<String> emitter) {
                try {
                    final String HOST="info.hekr.me";
                    //final String HOST="127.0.0.1";
                    Socket socket = new Socket();
                    socket.connect(new InetSocketAddress(HOST,91),5000);
                    OutputStream out = socket.getOutputStream();//获取服务端的输出流，为了向服务端输出数据
                    InputStream in = socket.getInputStream();//获取服务端的输入流，为了获取服务端输入的数据

                    PrintWriter bufw = new PrintWriter(out, true);
                    BufferedReader bufr = new BufferedReader(new InputStreamReader(in));
                    LOG.I(TAG,"发送啦");//打印服务端传来的数据
                    bufw.println("{\"action\":\"getAppDomain\"}");//发送数据给服务端
                    bufw.flush();

                    while (true) {
                        String line=null;
                        line=bufr.readLine();//读取服务端传来的数据
                        if(line==null)
                            break;
                        LOG.I(TAG,"服务端说:"+line);//打印服务端传来的数据
                        JSONObject jsonObject = JSONObject.parseObject(line);
                        JSONObject jsonObject1 = jsonObject.getJSONObject("dcInfo");
                        String domain = jsonObject1.getString("domain");
                        if(!TextUtils.isEmpty(domain)){
                            emitter.onNext(domain);
                            break;
                        }
                    }

                    out.close();
                    in.close();
                    bufw.close();
                    bufr.close();

                } catch (IOException e) {
                    emitter.onError(e.fillInStackTrace());
                }

            }
        }).subscribeOn(Schedulers.io())
        .subscribe(new Observer<String>() {

            private Disposable disposable;

            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(String domain) {
                try {
                    LOG.I(TAG,"获取到的domain:"+domain);
                    ECPreferences.savePreference(ECPreferenceSettings.SETTINGS_DOMAIN, domain, true);
                    Constants.setOnlineSite(domain);
                } catch (InvalidClassException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                disposable.dispose();
            }

            @Override
            public void onComplete() {
                disposable.dispose();
            }
        });

    }

}
