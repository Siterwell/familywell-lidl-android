package me.hekr.sthome.model.newstyle;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import me.hekr.sthome.R;
import me.hekr.sthome.common.TopbarSuperActivity;

/**
 * Created by gc-0001 on 2017/4/21.
 */

public class ChooseConditionActivity extends TopbarSuperActivity implements View.OnClickListener{
    private RelativeLayout oneCondition,allCondition;
    private ImageView oneimg,allimg;
    private boolean flag = false;

    @Override
    protected void onCreateInit() {
        initView();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_choose_condition;
    }

    private void initView(){
         getTopBarView().setTopBarStatus(1, 1, getResources().getString(R.string.choose_condition), null, new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 finish();
             }
         },null);
        oneCondition = (RelativeLayout)findViewById(R.id.one_condition);
        allCondition = (RelativeLayout)findViewById(R.id.all_condition);
        oneimg = (ImageView)findViewById(R.id.checkstatus1);
        allimg = (ImageView)findViewById(R.id.checkstatus);
        flag = getIntent().getBooleanExtra("condition",false);
        if(!flag){
            oneimg.setImageResource(R.drawable.g_select);
            allimg.setImageResource(R.drawable.g_unselect);
        }else{
            oneimg.setImageResource(R.drawable.g_unselect);
            allimg.setImageResource(R.drawable.g_select);
        }
        oneCondition.setOnClickListener(this);
        allCondition.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
         switch (v.getId()){
             case R.id.one_condition:
                 Intent aintent = new Intent(this, NewGroup2Activity.class);
                 aintent.putExtra("condition","00");
                 setResult(RESULT_OK,aintent); //这理有2个参数(int resultCode, Intent intent)
                 finish();
                 break;
             case R.id.all_condition:
                 Intent aintent2 = new Intent(this, NewGroup2Activity.class);
                 aintent2.putExtra("condition","ff");
                 setResult(RESULT_OK,aintent2); //这理有2个参数(int resultCode, Intent intent)
                 finish();
                 break;
         }
    }
}
