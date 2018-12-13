package me.hekr.sthome.equipment;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSONObject;
import com.google.firebase.iid.FirebaseInstanceId;
import com.igexin.sdk.PushManager;

import org.greenrobot.eventbus.EventBus;

import me.hekr.sthome.R;
import me.hekr.sthome.common.TopbarSuperActivity;
import me.hekr.sthome.event.STEvent;
import me.hekr.sthome.http.HekrUserAction;
import me.hekr.sthome.tools.UnitTools;

/**
 * Created by gc-0001 on 2017/2/9.
 */
public class SwitchLanActivity extends TopbarSuperActivity implements View.OnClickListener{
    private static final String TAG ="SwitchLanActivity";
    private LinearLayout content1;
    private LinearLayout content2;
    private LinearLayout content3;
    private LinearLayout content4;
    private String lan_now;
    private ImageView zh_sel;
    private ImageView en_sel;
    private ImageView de_sel;
    private ImageView fr_sel;
    private static final int SWITCH_SUCCESS = 1;
    private static final int SWITCH_FAIL = 2;

    @Override
    protected void onCreateInit() {
        initdata();
        initview();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_switch_lan;
    }

    private void initview(){
        getTopBarView().setTopBarStatus(1, 2, getResources().getString(R.string.switch_lan), null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }, null);
        content1 = (LinearLayout)findViewById(R.id.content1);
        content2 = (LinearLayout)findViewById(R.id.content2);
        content3 = (LinearLayout)findViewById(R.id.content3);
        content4 = (LinearLayout)findViewById(R.id.content4);
        zh_sel   = (ImageView)findViewById(R.id.accessory_checked1);
        en_sel   = (ImageView)findViewById(R.id.accessory_checked2);
        fr_sel   = (ImageView)findViewById(R.id.accessory_checked3);
        de_sel   =  (ImageView)findViewById(R.id.accessory_checked4);
        content1.setOnClickListener(this);
        content2.setOnClickListener(this);
        content3.setOnClickListener(this);
        content4.setOnClickListener(this);
        if("zh".equals(lan_now)){
            zh_sel.setImageResource(R.mipmap.seleect);
            en_sel.setImageResource(-1);
            de_sel.setImageResource(-1);
            fr_sel.setImageResource(-1);
        }else if("de".equals(lan_now)){

            de_sel.setImageResource(R.mipmap.seleect);
            en_sel.setImageResource(-1);
            zh_sel.setImageResource(-1);
            fr_sel.setImageResource(-1);
        }else if("fr".equals(lan_now)){

            fr_sel.setImageResource(R.mipmap.seleect);
            en_sel.setImageResource(-1);
            zh_sel.setImageResource(-1);
            de_sel.setImageResource(-1);
        }
        else{
            en_sel.setImageResource(R.mipmap.seleect);
            de_sel.setImageResource(-1);
            zh_sel.setImageResource(-1);
            fr_sel.setImageResource(-1);
        }
    }

    @Override
    public void onClick(View v) {
          switch (v.getId()){
              case R.id.content1:
                  reBindPush("zh");
                  break;
              case R.id.content2:
                  reBindPush("en");
                  break;
              case R.id.content3:
                  reBindPush("fr");
                  break;
              case R.id.content4:
                  reBindPush("de");
                  break;
              default:break;
          }
    }

    private void initdata(){
        UnitTools tools = new UnitTools(this);
        lan_now = tools.readLanguage();
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SWITCH_SUCCESS:
                    hideProgressDialog();
                    String lan = (String)msg.obj;
                    UnitTools tools1 = new UnitTools(SwitchLanActivity.this);
                    tools1.shiftLanguage(SwitchLanActivity.this,lan);
                    finish();
                    STEvent stEvent = new STEvent();
                    stEvent.setRefreshevent(8);
                    EventBus.getDefault().post(stEvent);
                    break;
                case SWITCH_FAIL:
                    hideProgressDialog();
                    showToast(R.string.switch_fail);
                    break;
            }

        }

    };

    private void reBindPush(String lan){

        showProgressDialog(getResources().getString(R.string.wait));
       final  Message message = Message.obtain();
        message.obj = lan;
        message.what = SWITCH_SUCCESS;
        String fcmtoken = FirebaseInstanceId.getInstance().getToken();
        if(TextUtils.isEmpty(fcmtoken)){

            String token = PushManager.getInstance().getClientid(this);
            JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
            jsonObject.put("clientId",token);
            jsonObject.put("locale",lan);
            jsonObject.put("pushPlatform","GETUI");
            HekrUserAction.getInstance(SwitchLanActivity.this).postHekrData("https://user-openapi.hekreu.me/user/pushTagBind", jsonObject.toString(), new HekrUserAction.GetHekrDataListener() {
                @Override
                public void getSuccess(Object object) {
                    handler.sendMessageDelayed(message,1000);
                }

                @Override
                public void getFail(int errorCode) {
                    handler.sendEmptyMessageDelayed(SWITCH_FAIL,1000);
                }
            });

        }else{
            JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();

            jsonObject.put("clientId",fcmtoken);
            jsonObject.put("locale",lan);
            jsonObject.put("pushPlatform","FCM");
            HekrUserAction.getInstance(SwitchLanActivity.this).postHekrData("https://user-openapi.hekreu.me/user/pushTagBind", jsonObject.toString(), new HekrUserAction.GetHekrDataListener() {
                @Override
                public void getSuccess(Object object) {
                    handler.sendMessageDelayed(message,1000);
                }

                @Override
                public void getFail(int errorCode) {
                    handler.sendEmptyMessageDelayed(SWITCH_FAIL,1000);
                }
            });
        }


    }

}
