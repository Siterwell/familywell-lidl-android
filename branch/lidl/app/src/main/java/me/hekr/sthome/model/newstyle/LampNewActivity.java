package me.hekr.sthome.model.newstyle;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import java.util.ArrayList;

import me.hekr.sthome.R;
import me.hekr.sthome.common.TopbarSuperActivity;
import me.hekr.sthome.model.modeladapter.OptionAdapter;
import me.hekr.sthome.model.modelbean.EquipmentBean;
import me.hekr.sthome.tools.LOG;
import me.hekr.sthome.tools.NameSolve;
import me.hekr.sthome.wheelwidget.view.WheelView;

/**
 * Created by jishu0001 on 2016/10/13.
 */
public class LampNewActivity extends SceneBaseActivity {
    private EquipmentBean device;
    String a,condition;
    private int position = -1;
    private ModelConditionPojo mcp = ModelConditionPojo.getInstance();
    private WheelView wheelView;
    private ArrayList<String> itemslist = new ArrayList<String>();

    @Override
    protected void onCreateInit() {
        try {
        initData();
        condition = device.getState();
        initView();
        initViewGuider();
    }catch (Exception e){
        LOG.I("ceshi","data is null");
    }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_new_lamp;
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
                    Intent i = new Intent(LampNewActivity.this, ModelCellListActivity.class);
                    startActivity(i);
                }else {
                    mcp.position=-1;
                    Intent i = new Intent(LampNewActivity.this, NewGroup2Activity.class);
                    startActivity(i);
                }
                finish();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mcp.position==-1){
                    mcp.output.add(device);

                }else {
                    mcp.output.set(mcp.position,device);
                    mcp.position=-1;
                }
                Intent i = new Intent(LampNewActivity.this, NewGroup2Activity.class);
                startActivity(i);
                finish();
            }
        });
    }


    private void initData() {

        String[] strs = getResources().getStringArray(R.array.lamp_actions);
        for(String ds:strs){
            itemslist.add(ds);
        }

        if(mcp.position!=-1){
            device = mcp.output.get(mcp.position);
        }else {
            device = mcp.device;
            device.setState("30ffffff");
        }
    }

    private void initView() {

        wheelView = (WheelView)findViewById(R.id.item);
        wheelView.setAdapter(new OptionAdapter(itemslist,30));
        wheelView.addChangingListener(new WheelView.OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                switch (newValue){
                    case 0:
                        device.setState("30ffffff");//设置为开
                        break;
                    case 1:
                        device.setState("31ffffff");//设置为关
                        break;
                    case 2:
                        device.setState("32ffffff");//设置为开
                        break;
                    case 3:
                        device.setState("33ffffff");//设置为关
                        break;
                    case 4:
                        device.setState("34ffffff");//设置为开
                        break;
                    case 5:
                        device.setState("35ffffff");//设置为关
                        break;
                    case 6:
                        device.setState("36ffffff");//设置为开
                        break;
                    case 7:
                        device.setState("37ffffff");//设置为关
                        break;
                    case 8:
                        device.setState("38ffffff");//设置为开
                        break;
                    case 9:
                        device.setState("39ffffff");//设置为关
                        break;
                    default:break;
                }
            }
        });


        if(!"".equals(condition)){
            String targit=condition.substring(0,2);
            if("30".equals(targit)){
                wheelView.setCurrentItem(0);
            }else if("31".equals(targit)){
                wheelView.setCurrentItem(1);
            } else if ("32".equals(targit)) {
                wheelView.setCurrentItem(2);
            }else if("33".equals(targit)){
                wheelView.setCurrentItem(3);
            }else if("34".equals(targit)){
                wheelView.setCurrentItem(4);
            }else if("35".equals(targit)){
                wheelView.setCurrentItem(5);
            } else if ("36".equals(targit)) {
                wheelView.setCurrentItem(6);
            }else if("37".equals(targit)){
                wheelView.setCurrentItem(7);
            }else if("38".equals(targit)){
                wheelView.setCurrentItem(8);
            }else if("39".equals(targit)){
                wheelView.setCurrentItem(9);
            }

        }

    }


}
