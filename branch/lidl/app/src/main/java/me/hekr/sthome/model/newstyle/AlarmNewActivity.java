package me.hekr.sthome.model.newstyle;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;

import me.hekr.sthome.R;
import me.hekr.sthome.common.TopbarSuperActivity;
import me.hekr.sthome.model.modeladapter.OptionAdapter;
import me.hekr.sthome.model.modelbean.EquipmentBean;
import me.hekr.sthome.tools.NameSolve;
import me.hekr.sthome.wheelwidget.view.WheelView;

public class AlarmNewActivity extends TopbarSuperActivity {

    private ArrayList<String> itemList = new ArrayList<>();
    private WheelView wheelView;
    private ModelConditionPojo mcp = ModelConditionPojo.getInstance();
    private EquipmentBean device;
    private String a;

    @Override
    protected void onCreateInit() {
        initData();
        wheelView = findViewById(R.id.alarm_item);
        wheelView.setAdapter(new OptionAdapter(itemList,30));
        wheelView.addChangingListener(new WheelView.OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                switch (newValue){
                    case 0:
                        device.setState("0000BB00");//设置为测试报警
                        break;
                    case 1:
                        device.setState("00005500");//设置为报警
                        break;
                    case 2:
                        device.setState("0000AA00");//设置为正常
                        break;
                    default:break;
                }
            }
        });
        a = device.getState();
        if(a != null){
            if("0000BB00".equals(a)){
                wheelView.setCurrentItem(0);
            }else if("00005500".equals(a)){
                wheelView.setCurrentItem(1);
            }else if("0000AA00".equals(a)){
                wheelView.setCurrentItem(2);
            }
        }
        initViewGuider();
    }

    private void initViewGuider() {
        String name = "";
        if(TextUtils.isEmpty(device.getEquipmentName())){
            name = NameSolve.getDefaultName(this,device.getEquipmentDesc(),device.getEqid());
        }else{
            name = device.getEquipmentName();
        }
        getTopBarView().setTopBarStatus(1, 2, name, getResources().getString(R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mcp.position==-1){
                    Intent i = new Intent(AlarmNewActivity.this, ModelCellListActivity.class);
                    startActivity(i);
                }else {
                    mcp.position=-1;
                    Intent i = new Intent(AlarmNewActivity.this, NewGroup2Activity.class);
                    startActivity(i);
                }
                finish();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mcp.position==-1){
                    mcp.input.add(device);

                }else {
                    mcp.input.set(mcp.position,device);
                    mcp.position=-1;
                }
                Intent i = new Intent(AlarmNewActivity.this, NewGroup2Activity.class);
                startActivity(i);
                finish();
            }
        });
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_alarm_new;
    }

    private void initData() {
        String[] str = getResources().getStringArray(R.array.alarm);
        itemList.addAll(Arrays.asList(str));

        if(mcp.position!=-1){
            device = mcp.input.get(mcp.position);
        }else {
            device = mcp.device;
            device.setState("0000BB00");//设置为开
        }
    }
}
