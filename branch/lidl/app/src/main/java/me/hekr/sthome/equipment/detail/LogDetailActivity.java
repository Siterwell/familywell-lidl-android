package me.hekr.sthome.equipment.detail;

import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import me.hekr.sthome.MyApplication;
import me.hekr.sthome.R;
import me.hekr.sthome.commonBaseView.MultiDirectionSlidingDrawer;
import me.hekr.sthome.commonBaseView.ProgressDialog;
import me.hekr.sthome.commonBaseView.PullListView;
import me.hekr.sthome.event.LogoutEvent;
import me.hekr.sthome.history.HistoryAdapter;
import me.hekr.sthome.http.HekrUser;
import me.hekr.sthome.http.HekrUserAction;
import me.hekr.sthome.model.modelbean.EquipmentBean;
import me.hekr.sthome.model.modelbean.NoticeBean;
import me.hekr.sthome.tools.ConnectionPojo;
import me.hekr.sthome.tools.SystemTintManager;
import me.hekr.sthome.tools.UnitTools;

/**
 * Created by ryanhsueh on 2019/8/27
 */
public abstract class LogDetailActivity extends AppCompatActivity implements
        MultiDirectionSlidingDrawer.OnDrawerOpenListener,
        MultiDirectionSlidingDrawer.OnDrawerCloseListener {

    protected EquipmentBean device;
    protected View root;

    protected MultiDirectionSlidingDrawer drawer;
    protected TextView textView_log;
    protected PullListView listView;
    protected ImageView imageView_cancel;
    protected Button btn_clear;
    protected List<NoticeBean> noticeBeanList;
    protected List<NoticeBean> noticeBeanFilterList;
    protected ProgressDialog progressDialog;
    protected HistoryAdapter historyAdapter;
    protected int page;
    protected View empty;
    protected SystemTintManager tintManager;

    protected abstract void doStatusShow(String aaaa);

    protected void initLogHistoryDrawer() {
        textView_log = findViewById(R.id.title);
        textView_log.setVisibility(View.GONE);
        drawer = findViewById(R.id.drawer1);
        drawer.setOnDrawerOpenListener(this);
        drawer.setOnDrawerCloseListener(this);
        listView =  drawer.findViewById(R.id.logs);
        listView.setPullLoadEnable(false);
        imageView_cancel = findViewById(R.id.cancel);
        imageView_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.close();
            }
        });
        btn_clear = findViewById(R.id.clear);
        btn_clear.setVisibility(View.GONE);
        noticeBeanList = new ArrayList<>();
        noticeBeanFilterList = new ArrayList<>();
        historyAdapter = new HistoryAdapter(this,noticeBeanFilterList);
        listView.setAdapter(historyAdapter);
        empty = findViewById(R.id.empty);
        listView.setEmptyView(empty);
        tintManager = new SystemTintManager(this);// 创建状态栏的管理实例
    }

    @Override
    public void onDrawerOpened() {
        getHistory();
        tintManager.setStatusBarDarkMode2(true,this);
        root.setBackgroundColor(getResources().getColor(R.color.white));
    }

    @Override
    public void onDrawerClosed() {
        page = 0;
        tintManager.setStatusBarDarkMode2(false,this);
        doStatusShow(device.getState());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if(drawer.isOpened()){
                drawer.animateClose();
                return true;
            }else {
                return super.onKeyDown(keyCode, event);
            }
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    private void getHistory(){
        if(page == 0){
            progressDialog =  new ProgressDialog(this);
            progressDialog.setPressText(getResources().getText(R.string.wait));
            progressDialog.show();
        }

        HekrUserAction.getInstance(this).getAlarmHistoryList(this, ConnectionPojo.getInstance().deviceTid, ConnectionPojo.getInstance().ctrlKey, ConnectionPojo.getInstance().propubkey, page, new HekrUser.GetDeviceHistoryListener() {
            @Override
            public void getSuccess(List<NoticeBean> list, int pagenumber, boolean last) {
                if(pagenumber == 0){
                    noticeBeanList.clear();
                }
                noticeBeanList.addAll(list);
                if(pagenumber<10){
                    if(!last&&list.size()==20){
                        //防止以前的告警数据造成解析失效
                        page = pagenumber + 1;
                        getHistory();
                    }else {
                        if(progressDialog!=null && progressDialog.isShowing()){
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                        page = 0;
                        filterNotice();

                    }
                }else {
                    if(progressDialog!=null && progressDialog.isShowing()){
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    page = 0;
                    filterNotice();
                }
            }

            @Override
            public void getFail(int errorCode) {
                if(progressDialog!=null && progressDialog.isShowing()){
                    progressDialog.dismiss();
                    progressDialog = null;
                }
                filterNotice();
                Toast.makeText(MyApplication.getAppContext(), UnitTools.errorCode2Msg(MyApplication.getAppContext(), errorCode),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void filterNotice(){
        noticeBeanFilterList.clear();
        for(NoticeBean noticeBean:noticeBeanList){
            if(!TextUtils.isEmpty( noticeBean.getEqid())&&noticeBean.getEqid().equals(device.getEqid())&&!TextUtils.isEmpty(noticeBean.getEquipmenttype())&&noticeBean.getEquipmenttype().equals(device.getEquipmentDesc())){
                noticeBeanFilterList.add(noticeBean);
            }
        }
        historyAdapter.refreshList(noticeBeanFilterList);
    }
}
