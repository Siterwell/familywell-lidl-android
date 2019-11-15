package me.hekr.sthome.configuration.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import me.hekr.sthome.DeviceListActivity;
import me.hekr.sthome.R;
import me.hekr.sthome.common.TopbarSuperActivity;
import me.hekr.sthome.commonBaseView.ProgressWheel;
import me.hekr.sthome.main.MainActivity;
import me.hekr.sthome.tools.LOG;

/**
 * Created by gc-0001 on 2017/2/17.
 */
public class EsptouchSuccessActivity extends TopbarSuperActivity {
    private ProgressWheel progressWheelInterpolated;
    private Timer timer = null;
    private MyTask timerTask;
    private String choosetoDeviceid;
    private final static String TAG ="EsptouchSuccessActivity";
    @Override
    protected void onCreateInit() {
        init();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_gou;
    }

    private void init(){
        Toast.makeText(this,getResources().getString(R.string.success_Esptouch),Toast.LENGTH_SHORT).show();
        getTopBarView().setTopBarStatus(1, 1, getResources().getString(R.string.success_Esptouch), null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }, null);

        choosetoDeviceid = getIntent().getStringExtra("devid");
        progressWheelInterpolated = (ProgressWheel) findViewById(R.id.interpolated);
        progressWheelInterpolated.setBarColor(Color.argb(255, 51, 167, 255));
        progressWheelInterpolated.setRimColor(Color.TRANSPARENT);


        setProgress(1.0f);


    }



    private void setProgress(float progress) {
        progressWheelInterpolated.setCallback(new ProgressWheel.ProgressCallback() {
            @Override
            public void onProgressUpdate(float progress) {
                LOG.I("ceshi","progress:"+progress);
                if(progress==1f){
                    progressWheelInterpolated.beginDrawTick();
                    timer = new Timer();
                    timerTask = new MyTask();
                    timer.schedule(timerTask,1000,1000);
                }
            }
        });

        progressWheelInterpolated.setProgress(progress);
    }

    class MyTask extends TimerTask {
        @Override
        public void run() {
            handler.sendMessage(handler.obtainMessage(1));

        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if(timer!=null){
                        if(timerTask!=null){
                            timerTask =null;
                        }
                        timer.cancel();
                        timer = null;
                        MainActivity.flag_checkfireware = true;
                        Log.i(TAG,"配网成功，切换到 choosetoDeviceid:"+choosetoDeviceid);
                        Intent intent = new Intent(EsptouchSuccessActivity.this, DeviceListActivity.class);
                        intent.putExtra("devid",choosetoDeviceid);
                        startActivity(intent);
                        finish();
                    }


                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(timer!=null){
            timer.cancel();
            timer = null;
        }
        if(timerTask!=null){
            timerTask =null;
        }
    }
}
