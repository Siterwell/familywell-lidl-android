package me.hekr.sthome;

import android.view.View;
import android.widget.TextView;

import me.hekr.sthome.common.TopbarSuperActivity;

/**
 * Created by gc-0001 on 2017/1/20.
 */
public class ServeIntroActivity extends TopbarSuperActivity {
    private TextView zh;

    @Override
    protected void onCreateInit() {
        initGuider();
        initView();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_about_intro;
    }

    private void initGuider(){
         getTopBarView().setTopBarStatus(1, 2, getResources().getString(R.string.serve), null, new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 finish();
             }
         },null);
    }

    private void initView(){
        zh = (TextView)findViewById(R.id.zh_intro);
        zh.setText(getResources().getString(R.string.server_intro));
    }
}
