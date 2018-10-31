package me.hekr.sthome.configuration.activity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import me.hekr.sthome.R;
import me.hekr.sthome.common.TopbarSuperActivity;

/**
 * Created by gc-0001 on 2017/2/10.
 */
public class BeforeConfigEsptouchActivity extends TopbarSuperActivity implements View.OnClickListener{
    private Button next_btn;
    private ImageView image;
    private AnimationDrawable ad;

    @Override
    protected void onCreateInit() {
        initview();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_before_config_esptouch;
    }

    private void initview(){
        next_btn = (Button)findViewById(R.id.next);
        image =(ImageView)findViewById(R.id.imageView1);
        image.setBackgroundResource(R.drawable.config_tishi);
        ad = (AnimationDrawable) image.getBackground();
        next_btn.setOnClickListener(this);
        getTopBarView().setTopBarStatus(1, 1, getResources().getString(R.string.net_configuration), null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 finish();
            }
        },null);



        image.post(new Runnable()
        {

            @Override

            public void run()

            {

                ad.start();

            }

        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.next:
                Intent tent = new Intent(BeforeConfigEsptouchActivity.this, EsptouchDemoActivity.class);
                startActivity(tent);
                finish();
                break;
            default:break;
        }
    }

}
