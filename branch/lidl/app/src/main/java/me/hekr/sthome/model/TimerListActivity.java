package me.hekr.sthome.model;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import me.hekr.sthome.R;
import me.hekr.sthome.common.TopbarSuperActivity;
import me.hekr.sthome.commonBaseView.ECAlertDialog;
import me.hekr.sthome.commonBaseView.RefreshableView2;
import me.hekr.sthome.crc.CoderUtils;
import me.hekr.sthome.event.STEvent;
import me.hekr.sthome.model.addsmodel.AddTimerAcitivity;
import me.hekr.sthome.model.modeladapter.TimerGatewayAdapeter;
import me.hekr.sthome.model.modelbean.TimerGatewayBean;
import me.hekr.sthome.model.modeldb.TimerDAO;
import me.hekr.sthome.tools.ConnectionPojo;
import me.hekr.sthome.tools.SendCommand;
import me.hekr.sthome.tools.SendOtherData;

/**
 * Created by gc-0001 on 2017/5/15.
 */

public class TimerListActivity extends TopbarSuperActivity implements TimerGatewayAdapeter.switchItemListener{

    private final String TAG = "TimerListActivity";

    private RefreshableView2 refreshableView;
    private ListView listView;
    private TimerGatewayAdapeter timerGatewayAdapeter;
    private List<TimerGatewayBean> timerlist;
    private TimerDAO timerDAO;
    private SendOtherData sendOtherData;
    private int index_of_operation = -1;
    private int switch_of_operation = 0;
    private int index_of_delete = -1;
    private String code = "";
    private boolean end_timer = false;
    private int count = 0;
    private Timer timer = null;
    private MyTask timerTask;
    @Override
    protected void onCreateInit() {
        initdata();
        initView();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_timerlist;
    }

    private void initView(){
        EventBus.getDefault().register(this);
        listView = (ListView)findViewById(R.id.timelist);
        View  empty = findViewById(R.id.empty);
        TextView textView_em  = (TextView)empty.findViewById(R.id.textempty);
        textView_em.setText(getResources().getString(R.string.timer_list_empty));
        listView.setEmptyView(empty);
        refreshableView = (RefreshableView2)findViewById(R.id.refresh);
        getTopBarView().setTopBarStatus(1, 1, getResources().getString(R.string.timer_list), null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(timerlist.size()<32){
                    startActivity(new Intent(TimerListActivity.this, AddTimerAcitivity.class));
                }else {
                    Toast.makeText(TimerListActivity.this,getResources().getString(R.string.most_timer),Toast.LENGTH_LONG).show();
                }

            }
        });
        timerGatewayAdapeter = new TimerGatewayAdapeter(this,timerlist,this);
        listView.setAdapter(timerGatewayAdapeter);
        refreshableView.setOnRefreshListener(new RefreshableView2.PullToRefreshListener() {
            @Override
            public void onRefresh() {
                sendOtherData.syncTimerModel(CoderUtils.getTimeCRC(TimerListActivity.this, ConnectionPojo.getInstance().deviceTid));
                timer = new Timer();
                timerTask = new MyTask();
                timer.schedule(timerTask,0,1000);
            }
        }, 2);

        new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.sendMessage(handler.obtainMessage(1));
            }
        }.start();
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    refreshableView.setRrefresh();
                    break;
                case 2:
                    refreshableView.finishRefreshing();
                    timerlist = timerDAO.findAllTimerOrderByTime(ConnectionPojo.getInstance().deviceTid);
                    timerGatewayAdapeter.refreshList(timerlist);
                    break;
            }
        }
    };

    private void initdata(){
        timerDAO = new TimerDAO(this);
        timerlist = new ArrayList<>();
        sendOtherData = new SendOtherData(this);
    }


    @Override
    public void switchclick(int position) {
        index_of_operation = position;
        if(timerlist.get(position).getEnable()==1){
            switch_of_operation = 0;
              SendCommand.Command = SendCommand.SWITCH_TIMER;
             TimerGatewayBean timerGatewayBean = timerlist.get(position);
             timerGatewayBean.setEnable(0);
             code = ResolveTimer.getCode(timerGatewayBean);
              sendOtherData.addTimerModel(code);
        }else{
            switch_of_operation = 1;
            SendCommand.Command = SendCommand.SWITCH_TIMER;
            TimerGatewayBean timerGatewayBean = timerlist.get(position);
            timerGatewayBean.setEnable(1);
            code = ResolveTimer.getCode(timerGatewayBean);
            sendOtherData.addTimerModel(code);
        }

    }

    @Override
    public void longclick(final int position2) {
        ECAlertDialog elc = ECAlertDialog.buildAlert(TimerListActivity.this,getResources().getString(R.string.delete_or_not), getResources().getString(R.string.cancel), getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SendCommand.Command = SendCommand.MODEL_TIMER_DEL;
                index_of_delete = position2;
                sendOtherData.deleteTimerModel(Integer.parseInt(timerlist.get(position2).getTimerid()));
            }
        });
        elc.show();
    }

    @Override
    public void click(int position) {
        Intent intent =new Intent(TimerListActivity.this,AddTimerAcitivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("timerid",timerlist.get(position).getTimerid());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        timerlist = timerDAO.findAllTimerOrderByTime(ConnectionPojo.getInstance().deviceTid);
        Log.i(TAG,timerlist.toString());
        timerGatewayAdapeter.refreshList(timerlist);

    }


    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if(timer!=null){
            timer.cancel();
            timer = null;
        }

    }

    @Subscribe          //订阅事件FirstEvent
    public  void onEventMainThread(STEvent event){

        if(event.getRefreshevent()==4){
            Log.i(TAG,"时间同步结束2");
            end_timer = true;


        }

        if(event.getEvent()== SendCommand.MODEL_TIMER_DEL){
           Toast.makeText(TimerListActivity.this,getResources().getString(R.string.delete_success),Toast.LENGTH_LONG).show();
           timerDAO.delete(timerlist.get(index_of_delete).getTimerid(), ConnectionPojo.getInstance().deviceTid);
           timerlist.remove(index_of_delete);
           timerGatewayAdapeter.refreshList(timerlist);
            SendCommand.clearCommnad();
        }else if(event.getEvent()== SendCommand.SWITCH_TIMER){
           Toast.makeText(TimerListActivity.this,getResources().getString(R.string.operation_success),Toast.LENGTH_LONG).show();
           timerlist.get(index_of_operation).setEnable(switch_of_operation);
           timerlist.get(index_of_operation).setCode(code);
           timerDAO.updateEnable(timerlist.get(index_of_operation));
           timerGatewayAdapeter.refreshList(timerlist);
            SendCommand.clearCommnad();
        }
    }


    class MyTask extends TimerTask {
        @Override
        public void run() {

            if(count>=5){
                timer.cancel();
                timer = null;

                handler.sendMessage(handler.obtainMessage(2));
            }else{
                count++;

                if(end_timer == true){
                    count = 0;
                    end_timer = false;
                    timer.cancel();
                    timer = null;
                    handler.sendMessage(handler.obtainMessage(2));
                }
            }

        }
    }

}
