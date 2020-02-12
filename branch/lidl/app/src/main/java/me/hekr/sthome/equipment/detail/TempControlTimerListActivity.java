package me.hekr.sthome.equipment.detail;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import me.hekr.sthome.R;
import me.hekr.sthome.common.TopbarSuperActivity;
import me.hekr.sthome.commonBaseView.ECAlertDialog;
import me.hekr.sthome.event.STEvent;
import me.hekr.sthome.history.DataFromSceneGroup;
import me.hekr.sthome.main.SceneFragment;
import me.hekr.sthome.model.ResolveScene;
import me.hekr.sthome.model.modelbean.SceneBean;
import me.hekr.sthome.model.modelbean.TimerGatewayBean;
import me.hekr.sthome.model.modeldb.SceneDAO;
import me.hekr.sthome.model.modeldb.TimerDAO;
import me.hekr.sthome.tools.ConnectionPojo;
import me.hekr.sthome.tools.SendCommand;
import me.hekr.sthome.tools.SendSceneData;

/**
 * Created by tracyhenry on 2018/11/2.
 */

public class TempControlTimerListActivity extends TopbarSuperActivity implements Gs361TimerAdapeter.switchItemListener{
    private ListView listView;
    private Gs361TimerAdapeter timerGatewayAdapeter;
    private List<SceneBean> sceneBeanslist;
    private List<SceneBean> sceneBeanListInit;
    private SceneDAO sceneDAO;
    private String eqid = null;
    private SendSceneData sendSceneData;
    private ECAlertDialog ecAlertDialog_delete;
    private int mTouchNumber = -1;

    @Override
    protected void onCreateInit() {
        sendSceneData = new SendSceneData(this) {
            @Override
            protected void sendEquipmentDataFailed() {

            }

            @Override
            protected void sendEquipmentDataSuccess() {

            }
        };
        eqid =  getIntent().getStringExtra("eqid");
        getTopBarView().setTopBarStatus(1, 1, getResources().getString(R.string.timer_list), null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TempControlTimerListActivity.this,TempControlEditTimerActivity.class);
                intent.putExtra("eqid",eqid);
             startActivity(intent);
            }
        });
        initView();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_gs361_timerlist;
    }

    private void initView(){
        sceneDAO = new SceneDAO(this);
        sceneBeanslist = new ArrayList<>();
        EventBus.getDefault().register(this);
        listView = (ListView)findViewById(R.id.timelist);
        View  empty = findViewById(R.id.empty);
        TextView textView_em  = (TextView)empty.findViewById(R.id.textempty);
        textView_em.setText(getResources().getString(R.string.timer_list_empty));
        listView.setEmptyView(empty);
        timerGatewayAdapeter = new Gs361TimerAdapeter(this,sceneBeanslist,this);
        listView.setAdapter(timerGatewayAdapeter);

    }

    @Override
    public void switchclick(int position) {

    }

    @Override
    public void longclick(final int position) {
        ecAlertDialog_delete = ECAlertDialog.buildAlert(this, R.string.delete_or_not, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                  SceneBean sceneBean = sceneBeanslist.get(position);
                SendCommand.Command = SendCommand.DELETE_SCENE;
                mTouchNumber = position;
                  sendSceneData.deleteScene(sceneBean.getMid());

            }
        });
        ecAlertDialog_delete.show();
    }

    @Override
    public void click(int position) {
         Intent intent = new Intent(TempControlTimerListActivity.this,TempControlEditTimerActivity.class);
         SceneBean ds = sceneBeanslist.get(position);
         intent.putExtra("mid",ds.getMid());
         intent.putExtra("eqid",eqid);
         startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);


    }

    @Subscribe          //订阅事件FirstEvent
    public  void onEventMainThread(STEvent event){
        if(event.getEvent()== SendCommand.DELETE_SCENE){
            SceneBean ac = sceneBeanslist.get(mTouchNumber);
            sceneDAO.delete(ac);
            DataFromSceneGroup dfsg = new DataFromSceneGroup(TempControlTimerListActivity.this);
            dfsg.doSendSynCode();
            refresh();
            SendCommand.clearCommnad();
        }

    }

    private List<SceneBean>  getRightlist(List<SceneBean> lists){
        ArrayList<SceneBean> l = new ArrayList<>();
        for(SceneBean scne:lists){
            ResolveScene resolveScene = new ResolveScene(this,scne.getCode());
            if(resolveScene.isTarget() && resolveScene.getOutput().size()==1&&resolveScene.getOutput().get(0).getEqid().equals(eqid)
                    &&!resolveScene.getSp().getTimer().equals("000000")
                    &&resolveScene.getInput().size()==1){
                l.add(scne);
            }
        }

        return  l;
    }

    private void refresh(){
        sceneBeanListInit = sceneDAO.findAllAmWithoutDefault(ConnectionPojo.getInstance().deviceTid);
        sceneBeanslist = getRightlist(sceneBeanListInit);
        Collections.sort(sceneBeanslist, new Comparator() {

            @Override
            public int compare(Object o, Object t1) {
                SceneBean a1 = (SceneBean) o;
                SceneBean a2 = (SceneBean) t1;
                String hour1 = "",hour2="",min1 = "",min2 = "";
                ResolveScene resolveScene = new ResolveScene(TempControlTimerListActivity.this,a1.getCode());
                if(resolveScene.isTarget()){
                   String timer =  resolveScene.getSp().getTimer();
                    hour1 = timer.substring(timer.length()-4,timer.length()-2);
                    min1 = timer.substring(timer.length()-2);
                }

                ResolveScene resolveScene2 = new ResolveScene(TempControlTimerListActivity.this,a2.getCode());
                if(resolveScene2.isTarget()){
                    String timer =  resolveScene2.getSp().getTimer();
                    hour2 = timer.substring(timer.length()-4,timer.length()-2);
                    min2 = timer.substring(timer.length()-2);
                }
                if(Integer.parseInt(hour1) == Integer.parseInt(hour2)){
                    return Integer.parseInt(min1) - Integer.parseInt(min2);
                }else {
                    return Integer.parseInt(hour1) - Integer.parseInt(hour2);
                }

            }
        });
        timerGatewayAdapeter.refreshList(sceneBeanslist);
    }
}
