package me.hekr.sthome.configuration.activity;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;

import me.hekr.sthome.R;
import me.hekr.sthome.common.TopbarSuperActivity;

/**
 * Created by Administrator on 2017/12/19.
 */

public class GatewayTypeChooseActivity extends TopbarSuperActivity implements View.OnClickListener {

    private LinearLayout linearLayout_188,linearLayout_193;

    @Override
    protected void onCreateInit() {
        getTopBarView().setTopBarStatus(1, 1, getResources().getString(R.string.choose_gateway_type), null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        },null);
        linearLayout_188 = (LinearLayout)findViewById(R.id.content1);
        linearLayout_193 = (LinearLayout)findViewById(R.id.content2);
        linearLayout_188.setOnClickListener(this);
        linearLayout_193.setOnClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_choose_gateway_type;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.content1:
                Intent tent = new Intent(this, EsptouchDemoActivity.class);
                tent.putExtra("gatewaytype","GS188");
                startActivity(tent);
                finish();
                break;
            case R.id.content2:
                Intent tent2 = new Intent(this, EsptouchDemoActivity.class);
                tent2.putExtra("gatewaytype","GS193");
                startActivity(tent2);
                finish();
                break;
        }
    }
}
