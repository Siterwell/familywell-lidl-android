package me.hekr.sthome.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import me.hekr.sthome.AddDeviceActivity;
import me.hekr.sthome.DragFolderwidget.ApplicationInfo;
import me.hekr.sthome.DragFolderwidget.CancellableQueueTimer;
import me.hekr.sthome.DragFolderwidget.Controller;
import me.hekr.sthome.DragFolderwidget.FolderContentView;
import me.hekr.sthome.DragFolderwidget.FolderInfo;
import me.hekr.sthome.DragFolderwidget.FolderScrollView;
import me.hekr.sthome.DragFolderwidget.FolderView;
import me.hekr.sthome.DragFolderwidget.HitTestResult3;
import me.hekr.sthome.DragFolderwidget.IPageView;
import me.hekr.sthome.DragFolderwidget.IconMover;
import me.hekr.sthome.DragFolderwidget.ItemInfo;
import me.hekr.sthome.DragFolderwidget.LayoutCalculator;
import me.hekr.sthome.DragFolderwidget.MyContentView;
import me.hekr.sthome.DragFolderwidget.MyScrollView;
import me.hekr.sthome.DragFolderwidget.ObjectPool;
import me.hekr.sthome.R;
import me.hekr.sthome.commonBaseView.CCPTabView;
import me.hekr.sthome.commonBaseView.ECAlertDialog;
import me.hekr.sthome.commonBaseView.TopBarView;
import me.hekr.sthome.equipment.EquipmentState;
import me.hekr.sthome.equipment.detail.ButtonDetailActivity;
import me.hekr.sthome.equipment.detail.Channel2SocketDetailActivity;
import me.hekr.sthome.equipment.detail.CoDetailActivity;
import me.hekr.sthome.equipment.detail.CurtainDetailActivity;
import me.hekr.sthome.equipment.detail.CxSmDetailActivity;
import me.hekr.sthome.equipment.detail.DimmingModuleDetailActivity;
import me.hekr.sthome.equipment.detail.DoorDetailActivity;
import me.hekr.sthome.equipment.detail.GasDetailActivity;
import me.hekr.sthome.equipment.detail.GuardDetailActivity;
import me.hekr.sthome.equipment.detail.LampDetailActivity;
import me.hekr.sthome.equipment.detail.LockDetailActivity;
import me.hekr.sthome.equipment.detail.ModeButtonDetailActivity;
import me.hekr.sthome.equipment.detail.PirDetailActivity;
import me.hekr.sthome.equipment.detail.SmDetailActivity;
import me.hekr.sthome.equipment.detail.SocketDetailActivity;
import me.hekr.sthome.equipment.detail.SosDetailActivity;
import me.hekr.sthome.equipment.detail.THCheckDetailActivity;
import me.hekr.sthome.equipment.detail.TempControlDetail2Activity;
import me.hekr.sthome.equipment.detail.ThermalDetailActivity;
import me.hekr.sthome.equipment.detail.ValveDetailActivity;
import me.hekr.sthome.equipment.detail.WaterDetailActivity;
import me.hekr.sthome.event.STEvent;
import me.hekr.sthome.event.ThcheckEvent;
import me.hekr.sthome.model.modelbean.EquipmentBean;
import me.hekr.sthome.model.modeldb.EquipDAO;
import me.hekr.sthome.model.modeldb.PackDAO;
import me.hekr.sthome.model.modeldb.ShortcutDAO;
import me.hekr.sthome.tools.ConnectionPojo;
import me.hekr.sthome.tools.LOG;
import me.hekr.sthome.tools.NameSolve;
import me.hekr.sthome.tools.SendCommand;
import me.hekr.sthome.tools.SendEquipmentData;
import me.hekr.sthome.tools.UnitTools;


/**
 * Created by xjj on 2016/12/6.
 */

@SuppressLint("ValidFragment")
public class DeviceFragment extends Fragment {
    private static final String TAG = "DeviceFragment";
    private LinearLayout scrollView;
    private int screenWidth;
    private int screenHeight;
    private RelativeLayout mFrame;
    private MyScrollView myScrollView;
    private List<ApplicationInfo> list = new ArrayList<ApplicationInfo>();
    private LayoutCalculator lc;
    private ObjectPool pp;
    private Handler handler;
    private float touchSlop;
    private HitTestResult3 hitTest2 = new HitTestResult3();
    private HitTestResult3 hitTest3 = new HitTestResult3();
    private IconMover mover;
    private RelativeLayout mContainer;
    private RelativeLayout lay_empty;
    private MyContentView page;
    private FolderView mFolderView;
    private int openFolderIndex = -1;
    private RelativeLayout mTouchController;
    private View view = null;
    private TopBarView topBarView;
    private SetPagerView setPagerView;
    private EquipDAO ED;
    private ShortcutDAO shortcutDAO;
    private SendEquipmentData sed;
    private EquipmentBean deleteEq;
    private Boolean touchon = false;
    private CCPTabView repalce_tabview,delete_tabview;
    private LinearLayout top_lay;
    private EquipmentBean deleteBean;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(view==null)
        {
            view = inflater.inflate(R.layout.fragment_equip_list, null);
            EventBus.getDefault().register(this);
            initGuider();
            initView();
        }
        //缓存的rootView需要判断是否已经被加载过parent,如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误
        ViewGroup viewparent = (ViewGroup)view.getParent();
        if(viewparent!=null)
        {
            viewparent.removeView(view);
        }



        return view;
    }
    private void initGuider() {

        topBarView = (TopBarView)view.findViewById(R.id.top_bar);
        topBarView.setTopBarStatus(1,1, 1,null, getResources().getString(R.string.equipment_list), null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ConnectionPojo.getInstance().deviceTid!=null){
                    STEvent stEvent = new STEvent();
                    stEvent.setServiceevent(2);
                    EventBus.getDefault().post(stEvent);
                }else {
                    Toast.makeText(DeviceFragment.this.getActivity(),getResources().getString(R.string.connect_equipment_alert),Toast.LENGTH_SHORT).show();
                }

            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sed.increaceEquipment();
                SendCommand.Command = SendCommand.INCREACE_EQUIPMENT;
                Intent intent  = new Intent(DeviceFragment.this.getActivity(), AddDeviceActivity.class);
                if(list!=null && openFolderIndex!=-1&&mFolderView!=null) {
                    LOG.I(TAG,"需要添加到的packID为："+((FolderInfo)list.get(openFolderIndex)).getPackId());
                    Bundle bundle = new Bundle();
                    bundle.putInt("folderid",((FolderInfo)list.get(openFolderIndex)).getPackId());
                    intent.putExtras(bundle);
                }
                startActivity(new Intent(intent));
                DeviceFragment.this.getActivity().overridePendingTransition(R.anim.openfrombottom, R.anim.stay);
            }
        });
        topBarView.getBackView().setImageResource(R.drawable.edit);
        //沉浸式设置支持API19
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int top = UnitTools.getStatusBarHeight(getActivity());
            ((LinearLayout)view.findViewById(R.id.coupon_popup)).setPadding(0,top,0,0);
        }
    }


    private void initView(){

        sed = new SendEquipmentData(this.getActivity()) {
            @Override
            protected void sendEquipmentDataFailed() {
//                Toast.makeText(EquipListActivity.this,"operation failed",Toast.LENGTH_LONG).show();
            }

            @Override
            protected void sendEquipmentDataSuccess() {
//                Toast.makeText(EquipListActivity.this,"operation success",Toast.LENGTH_LONG).show();
            }
        };

        ED = new EquipDAO(this.getActivity());
        shortcutDAO = new ShortcutDAO(this.getActivity());
        top_lay = view.findViewById(R.id.eq_option);
        mFrame =  view.findViewById(R.id.frame);
        lay_empty = view.findViewById(R.id.empty);
        repalce_tabview = view.findViewById(R.id.tihuan);
        delete_tabview  = view.findViewById(R.id.shanchu);
        repalce_tabview.setText(R.string.replace_equipment);
        repalce_tabview.setComP(R.mipmap.rep_eq);
        delete_tabview.setText(R.string.delete_equipment);
        delete_tabview.setComP(R.mipmap.del_eq);
        top_lay.setVisibility(View.GONE);
        scrollView =  view.findViewById(R.id.container);
        mContainer =  view.findViewById(R.id.springboard_container);
        mTouchController =  view.findViewById(R.id.touchController);
        myScrollView = view.findViewById(R.id.myscrollView);
        mTouchController.setOnTouchListener(scrollContainer_OnTouch);
        lc = new LayoutCalculator(this.getActivity());
        pp = new ObjectPool(this.getActivity(), lc);
        handler = new Handler();
        touchSlop = ViewConfiguration.get(this.getActivity()).getScaledTouchSlop();
        mover = new IconMover(view, lc, pp, handler);
        OnLayoutReady onLayoutReady = new OnLayoutReady();
        scrollView.getViewTreeObserver().addOnGlobalLayoutListener(onLayoutReady);
    }


    public IPageView getPage(boolean isFolder) {
        IPageView page = null;
        if (isFolder && mFolderView != null) {
            return mFolderView.getContentView();
        }

        View view = scrollView.getChildAt(0);
        if (view instanceof MyContentView)
            page = (MyContentView) view;
        return page;
    }

    /**
     * 重置Mycontent layout高度
     */
    public void initContentLayout() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) page.getLayoutParams();
        page.setIcons(list);
        params.height = page.getViewHeight();
        page.setLayoutParams(params);
    }

    private class OnLayoutReady implements ViewTreeObserver.OnGlobalLayoutListener {

        private OnLayoutReady() {
        }

        public void onGlobalLayout() {
            scrollView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            layoutReady();
            refresh();
        }
    }

    private void layoutReady() {
        screenWidth = mFrame.getWidth();
        screenHeight = mFrame.getHeight();
        lc.layoutReady(mFrame);
    }


    /*
    @method refresh
    @autor Administrator
    @time 2017/8/3 13:33
    @email xuejunju_4595@qq.com

    接收到数据后用于动态刷新数据，
    */
    public void refresh() {

        if(touchon) return;

        LOG.I("ceshi","刷新了哦");
        try {
            list.clear();
            controller.initData(list);
        } catch (Exception e) {
            LOG.I("ceshi", "data err");
        }


    }

    /*
    @method init
    @autor Administrator
    @time 2017/8/3 13:33
    @email xuejunju_4595@qq.com
    用于数据本地同步，在主页面的其他页面切换到此设备页的时候调用
    */
    public void init() {

        if(mFolderView==null) {
            LOG.I("ceshi","刷新了哦");
            try {
                list.clear();
                controller.initData(list);
            } catch (Exception e) {
                LOG.I("ceshi", "data err");
            }
        }else{
            mContainer.removeView(mFolderView);
            mFolderView = null;
            list.clear();
            controller.initData(list);
        }


    }

    private Controller controller = new Controller() {
        @Override
        public void initData(List<ApplicationInfo> list2) {

            List<ApplicationInfo> list = ED.findAllEqByNoPack(ConnectionPojo.getInstance().deviceTid);

            int size = list.size();
            for (int i = 0; i < size; i++) {
                ApplicationInfo equipment = list.get(i);

                if(NameSolve.DOOR_CHECK.equals(NameSolve.getEqType(equipment.getEquipmentDesc()))) {      //menci
                    equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.d10));
                    if(equipment.getState()!=null&& equipment.getState().length() == 8){
                        int quantity = EquipmentState.getDevBatteryLevel(equipment);

                        if (EquipmentState.isNormal(equipment)) {
//                    holder.s.setText("关门");
                            if(EquipmentState.isLowBattery(quantity)){
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.y10));
                            }else{
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.g10));
                            }
                        } else if (EquipmentState.isTriggered(equipment)) {
//                    holder.s.setText("开门");
                            equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e10));
                        }else if(EquipmentState.isDoorNotClosed(equipment)){
//                    holder.s.setText("门已打开");
                            equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e10));
                        }

                    }
                }else if(NameSolve.SOCKET.equals(NameSolve.getEqType(equipment.getEquipmentDesc()))){   //socket
                    equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.d7));
                    if (EquipmentState.isEquipmentStateAvalible(equipment)) {
                        String ddd = EquipmentState.getDevSecondState(equipment);
                        if ("01".equals(ddd)) {
//                    holder.s.setText("闭合");
                            equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e7));
                        } else if ("00".equals(ddd)) {
//                    holder.s.setText("断开");
                            equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.g7));
                        }
                    }
                }else if(NameSolve.TWO_SOCKET.equals(NameSolve.getEqType(equipment.getEquipmentDesc()))){   //socket
                    equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.d20));
                    if (EquipmentState.isEquipmentStateAvalible(equipment)) {
                        String ddd = equipment.getState().substring(4, 8);
                        if ("0301".equals(ddd) || "0302".equals(ddd) || "0303".equals(ddd)) {
//                    holder.s.setText("闭合");
                            equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e20));
                        } else if ("0300".equals(ddd)) {
//                    holder.s.setText("断开");
                            equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.g20));
                        }
                    }
                }else if(NameSolve.PIR_CHECK.equals(NameSolve.getEqType(equipment.getEquipmentDesc()))) {  //pir
                    equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.d1));
                    if (EquipmentState.isEquipmentStateAvalible(equipment)) {
                        int quantity = EquipmentState.getDevBatteryLevel(equipment);

                        String ddd = EquipmentState.getDevFirstState(equipment);
                        if (EquipmentState.isNormal(equipment)) {
//                    holder.s.setText("正常");
                            if(EquipmentState.isLowBattery(quantity)){
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.y1));
                            }else{
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.g1));
                            }

                        } else if (EquipmentState.isTriggered(equipment)) {
//                    holder.s.setText("有人");
                            equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e1));
                        }else if(EquipmentState.isBroken(equipment)){
//                    holder.s.setText("故障");
                            equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e1));
                        }else if(EquipmentState.isPirTeared(equipment)) {
//                    holder.s.setText("拆除");
                            equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e1));
                        }
                    }
                }else if(NameSolve.SOS_KEY.equals(NameSolve.getEqType(equipment.getEquipmentDesc()))) {  //sod
                    equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.d2));
                    if(EquipmentState.isEquipmentStateAvalible(equipment)){
                        int quantity = EquipmentState.getDevBatteryLevel(equipment);

                        String ddd = EquipmentState.getDevFirstState(equipment);
                        if (EquipmentState.isNormal(equipment)) {
//                    holder.s.setText("关门");
                            if(EquipmentState.isLowBattery(quantity)){
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.y2));
                            }else {
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.g2));
                            }

                        } else if (EquipmentState.isTriggered(equipment)) {
//                    holder.s.setText("开门");
                            equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e2));
                        }else if(EquipmentState.isDoorNotClosed(equipment)){
//                    holder.s.setText("门已打开");
                            equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e2));
                        }
                    }
                }else if(NameSolve.SM_ALARM.equals(NameSolve.getEqType(equipment.getEquipmentDesc()))) {  //sm
                    equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.d8));
                    if (EquipmentState.isEquipmentStateAvalible(equipment)) {
                        int quantity = EquipmentState.getDevBatteryLevel(equipment);

                        if (EquipmentState.isBroken(equipment)) {
                            equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e8));
                        } else if (EquipmentState.isTriggered(equipment)) {
//                    holder.s.setText("有人");
                            equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e8));
                        }else if(EquipmentState.isNormal(equipment)){
//                    holder.s.setText("故障");
                            if(EquipmentState.isLowBattery(quantity)){
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.y8));
                            }else{
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.g8));
                            }

                        }else if(EquipmentState.isTesting(equipment)) {
//                    holder.s.setText("拆除");
//                    holder.imageView.setImageResource(R.drawable.d1);
                            equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e8));
                        }else if(EquipmentState.isMute(equipment)){
                            equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e8));
                        }
                    }
                }else if(NameSolve.CO_ALARM.equals(NameSolve.getEqType(equipment.getEquipmentDesc()))){  //end
                    equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.d9));
                    if (EquipmentState.isEquipmentStateAvalible(equipment)) {
                        int quantity = EquipmentState.getDevBatteryLevel(equipment);
                        if (EquipmentState.isBroken(equipment)) {
                            equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e9));
                        } else if (EquipmentState.isTriggered(equipment)) {
                            //                    holder.s.setText("有人");
                            equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e9));
                        } else if (EquipmentState.isNormal(equipment)) {
                            //                    holder.s.setText("故障");
                            if(EquipmentState.isLowBattery(quantity)){
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.y9));
                            }else{
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.g9));
                            }
                        } else if (EquipmentState.isTesting(equipment)) {
                            //                    holder.s.setText("拆除");
                            //                    holder.imageView.setImageResource(R.drawable.d1);
                            equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e9));
                        } else if (EquipmentState.isMute(equipment)) {
                            equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e9));
                        }
                    }
                }else if(NameSolve.WT_ALARM.equals(NameSolve.getEqType(equipment.getEquipmentDesc()))){  //end
                    equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.d5));
                    if (EquipmentState.isEquipmentStateAvalible(equipment)) {
                        int quantity = EquipmentState.getDevBatteryLevel(equipment);
                        if (EquipmentState.isBroken(equipment)) {
                            equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e5));
                        } else if (EquipmentState.isTriggered(equipment)) {
                            //                    holder.s.setText("有人");
                            equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e5));
                        } else if (EquipmentState.isNormal(equipment)) {
                            //                    holder.s.setText("故障");
                            if(EquipmentState.isLowBattery(quantity)){
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.y5));
                            }else{
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.g5));
                            }
                        } else if (EquipmentState.isTesting(equipment)) {
                            //                    holder.s.setText("拆除");
                            //                    holder.imageView.setImageResource(R.drawable.d1);
                            equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e5));
                        } else if (EquipmentState.isMute(equipment)) {
                            equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e5));
                        }
                    }
                }else if(NameSolve.TH_CHECK.equals(NameSolve.getEqType(equipment.getEquipmentDesc()))){  //end
                    equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.d11));
                    if (EquipmentState.isEquipmentStateAvalible(equipment)) {
                        String ddd = equipment.getState().substring(2, 4);
                        String temp = EquipmentState.getDevFirstState(equipment);
                        String temp2 = Integer.toBinaryString(Integer.parseInt(temp,16));
                        String humidity = EquipmentState.getDevSecondState(equipment);
                        int realH = Integer.parseInt(humidity,16);
                        String realT;
                        if (temp2.length()==8){
                            realT = "-"+ (128 - Integer.parseInt(temp2.substring(1,temp2.length()),2));
                        }else{
                            realT = "" + Integer.parseInt(temp2,2);
                        }

                        if(Integer.parseInt(realT)>100 || Integer.parseInt(realT) < -40 || realH > 100 || realH < 0){
                            equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.d11));
                        }else{

                            int qqqq = Integer.parseInt(ddd,16);
                            if( qqqq <= 15 ){
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.y11));
                            }else{
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.g11));
                            }
                        }

                    }
                }else if(NameSolve.LAMP.equals(NameSolve.getEqType(equipment.getEquipmentDesc()))){   //socket
                    equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.d12));
                    if (EquipmentState.isEquipmentStateAvalible(equipment)) {
                        String ddd = EquipmentState.getDevSecondState(equipment);
                        if("38".equals(ddd)){
                            equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e12));
                        }else{
                            equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.g12));
                        }
                    }
                }else if(NameSolve.GUARD.equals(NameSolve.getEqType(equipment.getEquipmentDesc()))){   //socket
                    equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.d14));
                    if (EquipmentState.isEquipmentStateAvalible(equipment)) {
                        String ddd = EquipmentState.getDevSecondState(equipment);
                        if (EquipmentState.isTriggered(ddd)) {
//                    holder.s.setText("闭合");
                            equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e14));
                        } else if (EquipmentState.isNormal(ddd)) {
//                    holder.s.setText("断开");
                            equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.g14));
                        }
                    }
                }else if(NameSolve.VALVE.equals(NameSolve.getEqType(equipment.getEquipmentDesc()))){   //socket
                    equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.d15));
                    if (EquipmentState.isEquipmentStateAvalible(equipment)) {
                        String ddd = EquipmentState.getDevSecondState(equipment);
                        if ("01".equals(ddd)) {
//                    holder.s.setText("闭合");
                            equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e15));
                        } else if ("00".equals(ddd)) {
//                    holder.s.setText("断开");
                            equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.g15));
                        }
                    }
                }else if(NameSolve.CURTAIN.equals(NameSolve.getEqType(equipment.getEquipmentDesc()))){   //socket
                    equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.d13));
                    if (EquipmentState.isEquipmentStateAvalible(equipment)) {
                        String ddd = EquipmentState.getDevSecondState(equipment);
                        if ( ddd!=null && !"".equals(ddd)) {
                            equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.g13));
                        }
                    }
                }else if(NameSolve.BUTTON.equals(NameSolve.getEqType(equipment.getEquipmentDesc()))){   //socket
//            holder.imageView.setImageResource(mImage[16]);
                    equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.d18));
                    if (EquipmentState.isEquipmentStateAvalible(equipment)) {
                        int quantity = EquipmentState.getDevBatteryLevel(equipment);
                        if ("01".equals(EquipmentState.getDevFirstState(equipment)) || EquipmentState.isNormal(equipment)) {
                            if(EquipmentState.isLowBattery(quantity)){
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.y18));
                            }else{
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.g18));
                            }
                        }
                    }
                }else if(NameSolve.CXSM_ALARM.equals(NameSolve.getEqType(equipment.getEquipmentDesc()))) {  //sm
                    if (EquipmentState.isEquipmentStateAvalible(equipment)) {

                        String ddd = EquipmentState.getDevFirstState(equipment);
                        if ("12".equals(ddd)) {
                            equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.y8));
                        } else if ("17".equals(ddd)) {
                            equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e8));
                        } else if ("19".equals(ddd)) {
                            equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e8));
                        }else if ("15".equals(ddd)) {
                            equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.y8));
                        }else if ("1B".equals(ddd)) {
                            equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.g8));
                        }else if(EquipmentState.isNormal(ddd)){
                            int quantity = EquipmentState.getDevBatteryLevel(equipment);
                            if (EquipmentState.isLowBattery(quantity)) {
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.y8));
                            }else{
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.g8));
                            }
                        }else{
                            equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.d8));
                        }
                    }else {
                        equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.d8));
                    }
                }else if(NameSolve.GAS_ALARM.equals(NameSolve.getEqType(equipment.getEquipmentDesc()))){  //end
                    equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.d3));
                    if (EquipmentState.isEquipmentStateAvalible(equipment)) {
                        if (EquipmentState.isBroken(equipment)) {
                            equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e3));
                        } else if (EquipmentState.isTriggered(equipment)) {
                            //                    holder.s.setText("有人");
                            equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e3));
                        } else if (EquipmentState.isNormal(equipment)) {
                            //                    holder.s.setText("故障");
                            equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.g3));
                        } else if (EquipmentState.isTesting(equipment)) {
                            //                    holder.s.setText("拆除");
                            //                    holder.imageView.setImageResource(R.drawable.d1);
                            equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e3));
                        } else if (EquipmentState.isMute(equipment)) {
                            equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e3));
                        }
                    }
                }else if(NameSolve.THERMAL_ALARM.equals(NameSolve.getEqType(equipment.getEquipmentDesc()))){  //end
                    equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.d4));
                    if (EquipmentState.isEquipmentStateAvalible(equipment)) {
                        int quantity = EquipmentState.getDevBatteryLevel(equipment);
                        if (EquipmentState.isBroken(equipment)) {
                            equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e4));
                        } else if (EquipmentState.isTriggered(equipment)) {
                            //                    holder.s.setText("有人");
                            equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e4));
                        } else if (EquipmentState.isNormal(equipment)) {
                            //                    holder.s.setText("故障");
                            if(EquipmentState.isLowBattery(quantity)){
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.y4));
                            }else{
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.g4));
                            }
                        } else if (EquipmentState.isTesting(equipment)) {
                            //                    holder.s.setText("拆除");
                            //                    holder.imageView.setImageResource(R.drawable.d1);
                            equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e4));
                        } else if (EquipmentState.isMute(equipment)) {
                            equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e4));
                        }
                    }
                }else if(NameSolve.MODE_BUTTON.equals(NameSolve.getEqType(equipment.getEquipmentDesc()))){  //end
                    equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.d16));
                    if (EquipmentState.isEquipmentStateAvalible(equipment)) {
                        int quantity = EquipmentState.getDevBatteryLevel(equipment);
                        if (EquipmentState.isBroken(equipment)) {
                            equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e16));
                        } else if (EquipmentState.isTriggered(equipment)) {
                            //                    holder.s.setText("有人");
                            equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e16));
                        } else if (EquipmentState.isNormal(equipment)
                                || "01".equals(EquipmentState.getDevFirstState(equipment))
                                || "02".equals(EquipmentState.getDevFirstState(equipment))
                                || "04".equals(EquipmentState.getDevFirstState(equipment))
                                || "08".equals(EquipmentState.getDevFirstState(equipment))
                                ) {
                            //                    holder.s.setText("故障");
                            if(EquipmentState.isLowBattery(quantity)){
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.y16));
                            }else{
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.g16));
                            }
                        } else if (EquipmentState.isTesting(equipment)) {
                            //                    holder.s.setText("拆除");
                            //                    holder.imageView.setImageResource(R.drawable.d1);
                            equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e16));
                        } else if (EquipmentState.isMute(equipment)) {
                            equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e16));
                        }
                    }
                }else if(NameSolve.LOCK.equals(NameSolve.getEqType(equipment.getEquipmentDesc()))){  //end
                    equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.d19));
                    if (EquipmentState.isEquipmentStateAvalible(equipment)) {
                        String ds = EquipmentState.getDevFirstState(equipment);
                        int quantity = EquipmentState.getDevBatteryLevel(equipment);
                        if (EquipmentState.isNormal(ds) || "60".equals(ds) || "AB".equals(ds) || EquipmentState.isTriggered(ds) || "56".equals(ds)) {
                            if(EquipmentState.isLowBattery(quantity)){
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.y19));
                            }else{
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.g19));
                            }
                        } else if ("51".equals(ds) || "52".equals(ds) || "53".equals(ds)  || "10".equals(ds) || "20".equals(ds) || "30".equals(ds)) {
                            equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e19));
                        } else if ("40".equals(ds)) {
                            equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.y19));
                        }
                    }
                }else if(NameSolve.TEMP_CONTROL.equals(NameSolve.getEqType(equipment.getEquipmentDesc()))){  //end
                    equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.g14));
                    if (EquipmentState.isEquipmentStateAvalible(equipment)) {
                        int quantity = EquipmentState.getDevBatteryLevel(equipment);
                        if(EquipmentState.isLowBattery(quantity)){
                            equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.y14));
                        }else {
                            equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.g14));
                        }
                    }
                }else if(NameSolve.DIMMING_MODULE.equals(NameSolve.getEqType(equipment.getEquipmentDesc()))){  //end
                    equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.d21));
                    if (EquipmentState.isEquipmentStateAvalible(equipment)) {
                        int quantity = EquipmentState.getDevBatteryLevel(equipment);
                        String draw = EquipmentState.getDevFirstState(equipment);
                        int liangdu = Integer.parseInt(EquipmentState.getDevSecondState(equipment),16);
                        if(("00".equals(draw) || "01".equals(draw)) && (liangdu>=0&&liangdu<=100)){
                            if(EquipmentState.isLowBattery(quantity)){
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.y21));
                            }else {
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.g21));
                            }
                        }
                    }
                }

                ApplicationInfo info = equipment;
                list2.add(info);
            }

            PackDAO PDO = new PackDAO(DeviceFragment.this.getActivity());
            List<FolderInfo> listfold = PDO.findPackList(ConnectionPojo.getInstance().deviceTid);
            for(int j=0;j<listfold.size();j++){
                int packid = listfold.get(j).getPackId();
                List<ApplicationInfo> list2ds = PDO.findAppInfoList(packid, ConnectionPojo.getInstance().deviceTid);
                size = list2ds.size();
                for(int i=0 ; i<size ; i++){
                    ApplicationInfo equipment = list2ds.get(i);

                    if(NameSolve.DOOR_CHECK.equals(NameSolve.getEqType(equipment.getEquipmentDesc()))) {      //menci
                        equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.d10));
                        if(EquipmentState.isEquipmentStateAvalible(equipment)){
                            int quantity = EquipmentState.getDevBatteryLevel(equipment);

                            if (EquipmentState.isNormal(equipment)) {
//                    holder.s.setText("关门");
                                if(EquipmentState.isLowBattery(quantity)){
                                    equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.y10));
                                }else {
                                    equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.g10));
                                }

                            } else if (EquipmentState.isTriggered(equipment)) {
//                    holder.s.setText("开门");
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e10));
                            }else if("66".equals(EquipmentState.getDevFirstState(equipment))){
//                    holder.s.setText("门已打开");
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e10));
                            }

                        }
                    }else if(NameSolve.SOCKET.equals(NameSolve.getEqType(equipment.getEquipmentDesc()))){   //socket
                        equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.d7));
                        if (EquipmentState.isEquipmentStateAvalible(equipment)) {
                            String ddd = EquipmentState.getDevSecondState(equipment);
                            if ("01".equals(ddd)) {
//                    holder.s.setText("闭合");
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e7));
                            } else if ("00".equals(ddd)) {
//                    holder.s.setText("断开");
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.g7));
                            }
                        }
                    }else if(NameSolve.TWO_SOCKET.equals(NameSolve.getEqType(equipment.getEquipmentDesc()))){   //socket
                        equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.d20));
                        if (EquipmentState.isEquipmentStateAvalible(equipment)) {
                            String ddd = equipment.getState().substring(4, 8);
                            if ("0301".equals(ddd) || "0302".equals(ddd) || "0303".equals(ddd)) {
//                    holder.s.setText("闭合");
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e20));
                            } else if ("0300".equals(ddd)) {
//                    holder.s.setText("断开");
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.g20));
                            }
                        }
                    }else if(NameSolve.PIR_CHECK.equals(NameSolve.getEqType(equipment.getEquipmentDesc()))) {  //pir
                        equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.d1));
                        if (EquipmentState.isEquipmentStateAvalible(equipment)) {
                            int quantity = EquipmentState.getDevBatteryLevel(equipment);
                            if (EquipmentState.isNormal(equipment)) {
//                    holder.s.setText("正常");
                                if(EquipmentState.isLowBattery(quantity)){
                                    equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.y1));
                                }else {
                                    equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.g1));
                                }

                            } else if (EquipmentState.isTriggered(equipment)) {
//                    holder.s.setText("有人");
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e1));
                            }else if(EquipmentState.isBroken(equipment)){
//                    holder.s.setText("故障");
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e1));
                            }else if(EquipmentState.isPirTeared(equipment)) {
//                    holder.s.setText("拆除");
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e1));
                            }
                        }
                    }else if(NameSolve.SOS_KEY.equals(NameSolve.getEqType(equipment.getEquipmentDesc()))) {  //sod
                        equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.d2));
                        if(EquipmentState.isEquipmentStateAvalible(equipment)){
                            int quantity = EquipmentState.getDevBatteryLevel(equipment);

                            if (EquipmentState.isNormal(equipment)) {
//                    holder.s.setText("关门");
                                if(EquipmentState.isLowBattery(quantity)){
                                    equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.y2));
                                }else {
                                    equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.g2));
                                }

                            } else if (EquipmentState.isTriggered(equipment)) {
//                    holder.s.setText("开门");
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e2));
                            }else if("66".equals(EquipmentState.getDevFirstState(equipment))){
//                    holder.s.setText("门已打开");
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e2));
                            }
                        }
                    }else if(NameSolve.SM_ALARM.equals(NameSolve.getEqType(equipment.getEquipmentDesc()))) {  //sm
                        equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.d8));
                        if (EquipmentState.isEquipmentStateAvalible(equipment)) {
                            int quantity = EquipmentState.getDevBatteryLevel(equipment);

                            if (EquipmentState.isBroken(equipment)) {
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e8));
                            } else if (EquipmentState.isTriggered(equipment)) {
//                    holder.s.setText("有人");
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e8));
                            }else if(EquipmentState.isNormal(equipment)){
//                    holder.s.setText("故障");
                                if(EquipmentState.isLowBattery(quantity)){
                                    equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.y8));
                                }else {
                                    equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.g8));
                                }

                            }else if(EquipmentState.isTesting(equipment)) {
//                    holder.s.setText("拆除");
//                    holder.imageView.setImageResource(R.drawable.d1);
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e8));
                            }else if(EquipmentState.isMute(equipment)){
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e8));
                            }
                        }
                    }else if(NameSolve.CO_ALARM.equals(NameSolve.getEqType(equipment.getEquipmentDesc()))){  //end
                        equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.d9));
                        if (EquipmentState.isEquipmentStateAvalible(equipment)) {
                            int quantity = EquipmentState.getDevBatteryLevel(equipment);
                            if (EquipmentState.isBroken(equipment)) {
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e9));
                            } else if (EquipmentState.isTriggered(equipment)) {
                                //                    holder.s.setText("有人");
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e9));
                            } else if (EquipmentState.isNormal(equipment)) {
                                //                    holder.s.setText("故障");
                                if(EquipmentState.isLowBattery(quantity)){
                                    equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.y9));
                                }else {
                                    equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.g9));
                                }
                            } else if (EquipmentState.isTesting(equipment)) {
                                //                    holder.s.setText("拆除");
                                //                    holder.imageView.setImageResource(R.drawable.d1);
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e9));
                            } else if (EquipmentState.isMute(equipment)) {
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e9));
                            }
                        }
                    }else if(NameSolve.WT_ALARM.equals(NameSolve.getEqType(equipment.getEquipmentDesc()))){  //end
                        equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.d5));
                        if (EquipmentState.isEquipmentStateAvalible(equipment)) {
                            int quantity = EquipmentState.getDevBatteryLevel(equipment);
                            if (EquipmentState.isBroken(equipment)) {
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e5));
                            } else if (EquipmentState.isTriggered(equipment)) {
                                //                    holder.s.setText("有人");
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e5));
                            } else if (EquipmentState.isNormal(equipment)) {
                                //                    holder.s.setText("故障");
                                if(EquipmentState.isLowBattery(quantity)){
                                    equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.y5));
                                }else {
                                    equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.g5));
                                }
                            } else if (EquipmentState.isTesting(equipment)) {
                                //                    holder.s.setText("拆除");
                                //                    holder.imageView.setImageResource(R.drawable.d1);
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e5));
                            } else if (EquipmentState.isMute(equipment)) {
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e5));
                            }
                        }
                    }else if(NameSolve.TH_CHECK.equals(NameSolve.getEqType(equipment.getEquipmentDesc()))){  //end
                        equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.d11));
                        if (EquipmentState.isEquipmentStateAvalible(equipment)) {
                            String ddd = equipment.getState().substring(2, 4);
                            String temp = EquipmentState.getDevFirstState(equipment);
                            String humidity = EquipmentState.getDevSecondState(equipment);
                            int realH = Integer.parseInt(humidity,16);
                            String temp2 = Integer.toBinaryString(Integer.parseInt(temp,16));
                            String realT;
                            if (temp2.length()==8){
                                realT = "-"+ (128 - Integer.parseInt(temp2.substring(1,temp2.length()),2));
                            }else{
                                realT = "" + Integer.parseInt(temp2,2);
                            }


                            if(Integer.parseInt(realT)>100 || Integer.parseInt(realT) < -40 || realH > 100 || realH < 0){
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.d11));
                            }else{

                                int qqqq = Integer.parseInt(ddd,16);
                                if( qqqq <= 15 ){
                                    equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.y11));
                                }else{
                                    equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.g11));
                                }
                            }
                        }
                    }else if(NameSolve.LAMP.equals(NameSolve.getEqType(equipment.getEquipmentDesc()))){   //socket
                        equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.d12));
                        if (EquipmentState.isEquipmentStateAvalible(equipment)) {
                            String ddd = EquipmentState.getDevSecondState(equipment);
                            if("38".equals(ddd)){
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e12));
                            }else{
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.g12));
                            }
                        }
                    }else if(NameSolve.GUARD.equals(NameSolve.getEqType(equipment.getEquipmentDesc()))){   //socket
                        equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.g14));
                        if (EquipmentState.isEquipmentStateAvalible(equipment)) {
                            String ddd = EquipmentState.getDevSecondState(equipment);
                            if (EquipmentState.isTriggered(ddd)) {
//                    holder.s.setText("闭合");
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e14));
                            } else if (EquipmentState.isNormal(ddd)) {
//                    holder.s.setText("断开");
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.g14));
                            }
                        }
                    }else if(NameSolve.VALVE.equals(NameSolve.getEqType(equipment.getEquipmentDesc()))){   //socket
                        equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.d15));
                        if (EquipmentState.isEquipmentStateAvalible(equipment)) {
                            String ddd = EquipmentState.getDevSecondState(equipment);
                            if ("01".equals(ddd)) {
//                    holder.s.setText("闭合");
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e15));
                            } else if ("00".equals(ddd)) {
//                    holder.s.setText("断开");
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.g15));
                            }
                        }
                    }else if(NameSolve.CURTAIN.equals(NameSolve.getEqType(equipment.getEquipmentDesc()))){   //socket
                        equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.d13));
                        if (EquipmentState.isEquipmentStateAvalible(equipment)) {
                            String ddd = EquipmentState.getDevSecondState(equipment);
                            if ( ddd!=null && !"".equals(ddd)) {
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.g13));
                            }
                        }
                    }else if(NameSolve.BUTTON.equals(NameSolve.getEqType(equipment.getEquipmentDesc()))){   //socket
//            holder.imageView.setImageResource(mImage[16]);
                        equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.d18));
                        if (EquipmentState.isEquipmentStateAvalible(equipment)) {
                            int quantity = EquipmentState.getDevBatteryLevel(equipment);
                            if ("01".equals(EquipmentState.getDevFirstState(equipment)) || EquipmentState.isNormal(equipment)) {
                                if(EquipmentState.isLowBattery(quantity)){
                                    equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.y18));
                                }else {
                                    equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.g18));
                                }
                            }
                        }
                    }else if(NameSolve.CXSM_ALARM.equals(NameSolve.getEqType(equipment.getEquipmentDesc()))) {  //sm
                        equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.d8));
                        if (EquipmentState.isEquipmentStateAvalible(equipment)) {

                            String ddd = EquipmentState.getDevFirstState(equipment);
                            if ("11".equals(ddd)) {
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.y8));
                            } else if ("12".equals(ddd)) {
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.y8));
                            } else if ("13".equals(ddd)) {
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.y8));
                            } else if ("17".equals(ddd)) {
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e8));
                            } else if ("18".equals(ddd)) {
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e8));
                            }else if ("19".equals(ddd)) {
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e8));
                            }else if ("14".equals(ddd)) {
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.y8));
                            }else if ("15".equals(ddd)) {
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.y8));
                            }else if ("16".equals(ddd)) {
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.y8));
                            }else if ("1A".equals(ddd)) {
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.g8));
                            }else if ("1B".equals(ddd)) {
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.g8));
                            }else if(EquipmentState.isNormal(ddd)){
                                int quantity = EquipmentState.getDevBatteryLevel(equipment);
                                if (EquipmentState.isLowBattery(quantity)) {
                                    equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.y8));
                                }else{
                                    equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.g8));

                                }
                            }else{
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.d8));
                            }
                        }else {
                            equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.d8));
                        }
                    }else if(NameSolve.GAS_ALARM.equals(NameSolve.getEqType(equipment.getEquipmentDesc()))){  //end
                        equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.d3));
                        if (EquipmentState.isEquipmentStateAvalible(equipment)) {
                            if (EquipmentState.isBroken(equipment)) {
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e3));
                            } else if (EquipmentState.isTriggered(equipment)) {
                                //                    holder.s.setText("有人");
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e3));
                            } else if (EquipmentState.isNormal(equipment)) {
                                //                    holder.s.setText("故障");
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.g3));
                            } else if (EquipmentState.isTesting(equipment)) {
                                //                    holder.s.setText("拆除");
                                //                    holder.imageView.setImageResource(R.drawable.d1);
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e3));
                            } else if (EquipmentState.isMute(equipment)) {
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e3));
                            }
                        }
                    }else if(NameSolve.THERMAL_ALARM.equals(NameSolve.getEqType(equipment.getEquipmentDesc()))){  //end
                        equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.d4));
                        if (EquipmentState.isEquipmentStateAvalible(equipment)) {
                            int quantity = EquipmentState.getDevBatteryLevel(equipment);
                            if (EquipmentState.isBroken(equipment)) {
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e4));
                            } else if (EquipmentState.isTriggered(equipment)) {
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e4));
                            } else if (EquipmentState.isNormal(equipment)) {
                                if(EquipmentState.isLowBattery(quantity)){
                                    equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.y4));
                                }else {
                                    equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.g4));
                                }
                            } else if (EquipmentState.isTesting(equipment)) {
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e4));
                            } else if (EquipmentState.isMute(equipment)) {
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e4));
                            }
                        }
                    }else if(NameSolve.MODE_BUTTON.equals(NameSolve.getEqType(equipment.getEquipmentDesc()))){  //end
                        equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.d16));
                        if (EquipmentState.isEquipmentStateAvalible(equipment)) {
                            int quantity = EquipmentState.getDevBatteryLevel(equipment);
                            if (EquipmentState.isBroken(equipment)) {
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e16));
                            } else if (EquipmentState.isTriggered(equipment)) {
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e16));
                            } else if (EquipmentState.isNormal(equipment)
                                    || "01".equals(EquipmentState.getDevFirstState(equipment))
                                    || "02".equals(EquipmentState.getDevFirstState(equipment))
                                    || "04".equals(EquipmentState.getDevFirstState(equipment))
                                    || "08".equals(EquipmentState.getDevFirstState(equipment))
                                    ) {
                                if(EquipmentState.isLowBattery(quantity)){
                                    equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.y16));
                                }else {
                                    equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.g16));
                                }
                            } else if (EquipmentState.isTesting(equipment)) {
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e16));
                            } else if (EquipmentState.isMute(equipment)) {
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e16));
                            }
                        }
                    }else if(NameSolve.LOCK.equals(NameSolve.getEqType(equipment.getEquipmentDesc()))){
                        equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.d19));
                        if (EquipmentState.isEquipmentStateAvalible(equipment)) {
                            String ds = EquipmentState.getDevFirstState(equipment);
                            int quantity = EquipmentState.getDevBatteryLevel(equipment);
                            if (EquipmentState.isNormal(ds) || "60".equals(ds) || "AB".equals(ds) || EquipmentState.isTriggered(ds) || "56".equals(ds)) {
                                if(EquipmentState.isLowBattery(quantity)){
                                    equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.y19));
                                }else{
                                    equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.g19));
                                }
                            } else if ("51".equals(ds) || "52".equals(ds) || "53".equals(ds) || "10".equals(ds) || "20".equals(ds) || "30".equals(ds)) {
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e19));
                            } else if ("40".equals(ds)) {
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.y19));
                            }
                        }
                    }else if(NameSolve.TEMP_CONTROL.equals(NameSolve.getEqType(equipment.getEquipmentDesc()))){  //end
                        equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.g14));
                        if (EquipmentState.isEquipmentStateAvalible(equipment)) {
                            int quantity = EquipmentState.getDevBatteryLevel(equipment);
                            if(EquipmentState.isLowBattery(quantity)){
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.y14));
                            }else {
                                equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.g14));
                            }
                        }
                    }else if(NameSolve.DIMMING_MODULE.equals(NameSolve.getEqType(equipment.getEquipmentDesc()))){  //end
                        equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.d21));
                        if (EquipmentState.isEquipmentStateAvalible(equipment)) {
                            int quantity = EquipmentState.getDevBatteryLevel(equipment);
                            String draw = EquipmentState.getDevFirstState(equipment);
                            int liangdu = Integer.parseInt(EquipmentState.getDevSecondState(equipment),16);
                            if(("00".equals(draw) || "01".equals(draw)) && (liangdu>=0&&liangdu<=100)){
                                if(EquipmentState.isLowBattery(quantity)){
                                    equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.y21));
                                }else {
                                    equipment.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.g21));
                                }
                            }

                        }
                    }

                }
                listfold.get(j).setIcons(list2ds);
                list2.add(listfold.get(j).getOrder(),listfold.get(j));
            }


            if(list2 == null || list2.size() == 0){
                lay_empty.setVisibility(View.VISIBLE);
                myScrollView.setVisibility(View.GONE);
            }else{
                lay_empty.setVisibility(View.GONE);
                myScrollView.setVisibility(View.VISIBLE);
            }

            if(mFolderView!=null&&list2.size()>0){
                FolderInfo info  = (FolderInfo) list2.get(openFolderIndex);
                mFolderView.refresh(info);
            }else{
                loaded();

            }

        }

        @Override
        public void onAppClick(EquipmentBean device) {

            if(NameSolve.DOOR_CHECK.equals(NameSolve.getEqType(device.getEquipmentDesc()))) {      //menci
                startDetailActivity(device, DeviceFragment.this.getActivity(), DoorDetailActivity.class);
            }else if(NameSolve.SOCKET.equals(NameSolve.getEqType(device.getEquipmentDesc()))){   //chazuo
                startDetailActivity(device, DeviceFragment.this.getActivity(), SocketDetailActivity.class);
            }else if(NameSolve.PIR_CHECK.equals(NameSolve.getEqType(device.getEquipmentDesc()))) {  //pir
                startDetailActivity(device, DeviceFragment.this.getActivity(), PirDetailActivity.class);
            }else if(NameSolve.SOS_KEY.equals(NameSolve.getEqType(device.getEquipmentDesc()))) {  //sos
                Intent detail = new Intent(DeviceFragment.this.getActivity(), SosDetailActivity.class);
                //Bundle bundle = new Bundle();
                //bundle.putSerializable("device", device);
                detail.putExtra("device",device);
                startActivity(detail);
            }else if(NameSolve.SM_ALARM.equals(NameSolve.getEqType(device.getEquipmentDesc()))) {  //sm
                startDetailActivity(device, DeviceFragment.this.getActivity(), SmDetailActivity.class);
            }else if(NameSolve.CO_ALARM.equals(NameSolve.getEqType(device.getEquipmentDesc()))) {  //co
                startDetailActivity(device, DeviceFragment.this.getActivity(), CoDetailActivity.class);
            }else if(NameSolve.GAS_ALARM.equals(NameSolve.getEqType(device.getEquipmentDesc()))) {  //co
                startDetailActivity(device, DeviceFragment.this.getActivity(), GasDetailActivity.class);
            }else if(NameSolve.WT_ALARM.equals(NameSolve.getEqType(device.getEquipmentDesc()))) {  //water
                startDetailActivity(device, DeviceFragment.this.getActivity(), WaterDetailActivity.class);
            }else if(NameSolve.TH_CHECK.equals(NameSolve.getEqType(device.getEquipmentDesc()))) {  //temprature and hib
                startDetailActivity(device, DeviceFragment.this.getActivity(), THCheckDetailActivity.class);
            }else if(NameSolve.LAMP.equals(NameSolve.getEqType(device.getEquipmentDesc()))) {  //lamp
                startDetailActivity(device, DeviceFragment.this.getActivity(), LampDetailActivity.class);
            }else if(NameSolve.GUARD.equals(NameSolve.getEqType(device.getEquipmentDesc()))) {  //door guard
                startDetailActivity(device, DeviceFragment.this.getActivity(), GuardDetailActivity.class);
            }else if(NameSolve.VALVE.equals(NameSolve.getEqType(device.getEquipmentDesc()))) {  //door guard
                startDetailActivity(device, DeviceFragment.this.getActivity(), ValveDetailActivity.class);
            }else if(NameSolve.BUTTON.equals(NameSolve.getEqType(device.getEquipmentDesc()))) {  //door guard
                startDetailActivity(device, DeviceFragment.this.getActivity(), ButtonDetailActivity.class);
            }else if(NameSolve.CURTAIN.equals(NameSolve.getEqType(device.getEquipmentDesc()))) {  //door guard
                startDetailActivity(device, DeviceFragment.this.getActivity(), CurtainDetailActivity.class);
            }else if(NameSolve.CXSM_ALARM.equals(NameSolve.getEqType(device.getEquipmentDesc()))) {  //door guard
                startDetailActivity(device, DeviceFragment.this.getActivity(), CxSmDetailActivity.class);
            }else if(NameSolve.THERMAL_ALARM.equals(NameSolve.getEqType(device.getEquipmentDesc()))) {  //door guard
                startDetailActivity(device, DeviceFragment.this.getActivity(), ThermalDetailActivity.class);
            }else if(NameSolve.MODE_BUTTON.equals(NameSolve.getEqType(device.getEquipmentDesc()))) {  //door guard
                startDetailActivity(device, DeviceFragment.this.getActivity(), ModeButtonDetailActivity.class);
            }else if(NameSolve.LOCK.equals(NameSolve.getEqType(device.getEquipmentDesc()))) {  //door guard
                startDetailActivity(device, DeviceFragment.this.getActivity(), LockDetailActivity.class);
            }else if(NameSolve.TWO_SOCKET.equals(NameSolve.getEqType(device.getEquipmentDesc()))) {  //door guard
                startDetailActivity(device, DeviceFragment.this.getActivity(), Channel2SocketDetailActivity.class);
            }else if(NameSolve.TEMP_CONTROL.equals(NameSolve.getEqType(device.getEquipmentDesc()))) {  //door guard
                startDetailActivity(device, DeviceFragment.this.getActivity(), TempControlDetail2Activity.class);
            }else if(NameSolve.DIMMING_MODULE.equals(NameSolve.getEqType(device.getEquipmentDesc()))) {  //door guard
                startDetailActivity(device, DeviceFragment.this.getActivity(), DimmingModuleDetailActivity.class);
            }
        }

        private void startDetailActivity(EquipmentBean device, Context context, Class<?> cls) {
            Intent detail = new Intent(context, cls);
            Bundle bundle = new Bundle();
            bundle.putSerializable("device", device);
            detail.putExtras(bundle);
            startActivity(detail);
        }

        @Override
        public void onAppRemove(final ApplicationInfo app) {

            String ds = getResources().getString(R.string.delete_or_not_withdata);

            ECAlertDialog elc = ECAlertDialog.buildAlert(DeviceFragment.this.getActivity(),ds, getResources().getString(R.string.cancel), getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    deleteEq = app;
                    SendCommand.Command = SendCommand.DELETE_EQUIPMENT;
                    sed.deleteEquipment(deleteEq.getEqid());
                }
            });
            elc.show();

        }

    };


    public void loaded() {
        scrollView.removeAllViews();
        page = new MyContentView(DeviceFragment.this.getActivity());
        page.init(lc, pp);
        page.setIcons(list);
        scrollView.addView(page, LinearLayout.LayoutParams.MATCH_PARENT, page.getViewHeight());

    }

    private static double getDistance(float x1, float y1, float x2, float y2) {
        float x = x2 - x1;
        float y = y2 - y1;
        return Math.sqrt(x * x + y * y);
    }

    private View.OnTouchListener scrollContainer_OnTouch = new View.OnTouchListener() {
        private float x;
        private float y;
        private int scrollPointY;
        private int out_scrollPointY;
        IPageView currentPage;
        private CancellableQueueTimer jiggleModeWaiter;
        private CancellableQueueTimer moveIntoFolderWaiter;
        private CancellableQueueTimer startDragWaiter;
        private CancellableQueueTimer moveIconWaiter;
        private CancellableQueueTimer moveToDesktopWaiter;
        private boolean isDrag = false; //是否可以拖动
        private HitTestResult3 oldHitTest2 = new HitTestResult3();
        private FolderScrollView folderScrollView;
        private boolean isFolderActionDown = false;
        private boolean isDesktopActionDown = false;


        private Runnable startDragDetacher = new Runnable() {

            @Override
            public void run() {
                isDrag = true;
                if(mFolderView == null) top_lay.setVisibility(View.VISIBLE);
                if (currentPage != null) {
                    detachIcon(currentPage,currentPage.getSelectedIndex(), mFolderView != null);
                }
            }
        };
        private Runnable moveIconDetacher = new Runnable() {
            HitTestResult3 oldHitTest = new HitTestResult3();

            @Override
            public void run() {
                if (hitTest2.index >= 0) {
                    if (oldHitTest.index != hitTest2.index || oldHitTest.inIcon != hitTest2.inIcon) {
                        if (mover.isAboveFolder()) {
                            mover.bisideFolder();
                            currentPage.removeFolderBound();
                            if (moveIntoFolderWaiter != null) {
                                moveIntoFolderWaiter.cancel();
                                moveIntoFolderWaiter = null;
                            }
                        }
                        oldHitTest.index = hitTest2.index;
                        oldHitTest.inIcon = hitTest2.inIcon;
                    }
                    if (!hitTest2.inIcon) {
                        if (mover.isAboveFolder()) {
                            mover.bisideFolder();
                            currentPage.removeFolderBound();
                            if (moveIntoFolderWaiter != null) {
                                moveIntoFolderWaiter.cancel();
                                moveIntoFolderWaiter = null;
                            }
                        }
                        if (currentPage.setMoveTo(hitTest2.index)) {
//                            if (mFolderView != null) {
//                                mFolderView.initLayout();
//                            }
                            initContentLayout();
                            mover.setIndex(hitTest2.index);
                            mover.setsIndex(hitTest2.index);
                        } else {
                            mover.setIndex(mover.getsIndex());
                        }
                    } else {
                        if (!mover.isAboveFolder()) {
                            mover.aboveFolder();
                            mover.setIndex(hitTest2.index);
                            currentPage.createFolderBound(hitTest2.index);
                            if (moveIntoFolderWaiter == null) {
                                moveIntoFolderWaiter = new CancellableQueueTimer(handler,
                                        ViewConfiguration.getLongPressTimeout(), moveIntoFolderDetacher);
                            }
                        }
                    }
                } else {
                    mover.setIndex(mover.getsIndex());
                }
                moveIconWaiter = null;
            }
        };
        private Runnable moveIntoFolderDetacher = new Runnable() {

            @Override
            public void run() {
                if (hitTest2.index > 0) {
                    IPageView page = getPage(false);
                    ApplicationInfo info = page.getIcon(hitTest2.index);
                    if (info != null && info.getType() == ItemInfo.TYPE_FOLDER) {
                        FolderInfo folder = (FolderInfo) info;
                        openFolderIndex = hitTest2.index;
                        //openFolder(folder);
                    }
                }
                moveIntoFolderWaiter = null;
            }
        };

        private Runnable moveToDesktopDetacher = new Runnable() {

            @Override
            public void run() {

                try {
                    final ApplicationInfo app = mover.hook();
                    LOG.I("ceshi","mover.hook()"+app.toString());
                    EquipDAO dao = new EquipDAO(DeviceFragment.this.getActivity());
                    app.setPackId(0);
                    dao.updatePack(app);
                    IPageView page = getPage(true);
                    page.clearUp(null);
                    mFolderView.initLayout();
                    closeFolder();
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        };

        boolean onTouchFolder(View v, final MotionEvent ev) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    return actionDownFolder(ev);
                case MotionEvent.ACTION_MOVE:
                    return actionMoveFolder(ev);
                case MotionEvent.ACTION_CANCEL:
                    currentPage.deselect();
                case MotionEvent.ACTION_UP:
                    return actionUpFolder(ev);
            }
            return false;

        }

        private boolean actionDownFolder(final MotionEvent ev) {
            try {
                touchon = true;
                if (mover.isMoving()) {
                    return false;
                }
                x = ev.getX();
                y = ev.getY();
                isDrag = false;
                oldHitTest2.index = -1;
                oldHitTest2.inIcon = false;
                folderScrollView = mFolderView.getScrollView();
                mover.setAboveFolder(false);
                scrollPointY = folderScrollView.getScrollY();
                currentPage = getPage(true);
                if (x < mFolderView.getTranslateLeft() || y < mFolderView.getTranslateTop()
                        || x > folderScrollView.getWidth() + mFolderView.getTranslateLeft()
                        || y > folderScrollView.getHeight() + mFolderView.getTranslateTop()) {
                    closeFolder();
                }
                isDesktopActionDown = false;
                isFolderActionDown = true;
                if (currentPage != null) {
                    currentPage.hitTest3((int) x - mFolderView.getTranslateLeft(),
                            (int) y - mFolderView.getTranslateTop() + scrollPointY, hitTest3);
                    if (hitTest3.index >= 0) {
                        currentPage.select(hitTest3.index);
                        ApplicationInfo info = currentPage.getIcon(hitTest3.index);
                        if (info != null) {
                            if (startDragWaiter == null) {
                                startDragWaiter = new CancellableQueueTimer(handler, 200, startDragDetacher);
                            }
                        }
                    }

                }
            }catch (Exception e){

            }

            return true;
        }

        private boolean actionMoveFolder(final MotionEvent ev) {
            if (!isFolderActionDown) {
                folderScrollView = mFolderView.getScrollView();
                x = ev.getX();
                y = ev.getY();
                mover.setAboveFolder(false);
                scrollPointY = folderScrollView.getScrollY();
                currentPage = getPage(true);
                isFolderActionDown = true;
                isDesktopActionDown = false;
            }
            if (getDistance(ev.getX(), ev.getY(), x, y) <= touchSlop) {
                if (currentPage != null) {
                    if (isDrag) {
                        detachIcon(currentPage,currentPage.getSelectedIndex(), true);
                    }
                }
            } else {
                if (currentPage != null) {
                    currentPage.deselect();
                }
                if (startDragWaiter != null) {
                    startDragWaiter.cancel();
                    startDragWaiter = null;
                }
                hitTest3.buttonRemove = false;
            }
            if (mover.isMoving()) {
                Point point = new Point((int) ev.getX(), (int) ev.getY());
                mover.moveTo(point.x, point.y);
                mFolderView.invalidate(mover.getBounds());
                if (currentPage != null) {
                    if (point.x < mFolderView.getTranslateLeft() || point.y < mFolderView.getTranslateTop()
                            || point.x > folderScrollView.getWidth() + mFolderView.getTranslateLeft()
                            || point.y > folderScrollView.getHeight() + mFolderView.getTranslateTop()) {
                        if (moveToDesktopWaiter == null) {
                            moveToDesktopWaiter = new CancellableQueueTimer(handler,
                                    ViewConfiguration.getLongPressTimeout(), moveToDesktopDetacher);
                        }
                    } else {
                        if (moveToDesktopWaiter != null) {
                            moveToDesktopWaiter.cancel();
                            moveToDesktopWaiter = null;
                        }
                    }


                    if (point.y - mFolderView.getTranslateTop() < folderScrollView.getHeight() / 3) {
                        folderScrollView.scrollBy(0, -(int)getResources().getDimension(R.dimen.equipmentList_scroll_by));
                        scrollPointY = folderScrollView.getScrollY();
                    }
                    if (point.y - mFolderView.getTranslateTop() > folderScrollView.getHeight() * 2 / 3) {
                        mFolderView.getScrollView().scrollBy(0, (int)getResources().getDimension(R.dimen.equipmentList_scroll_by));
                        scrollPointY = folderScrollView.getScrollY();
                    }


                    int position = currentPage.hitTest2(point.x - mFolderView.getTranslateLeft(), point.y
                            - mFolderView.getTranslateTop() + scrollPointY, hitTest2, false);
                    if (position == 0) {
                        if (hitTest2.index >= 0 && point.x >= mFolderView.getTranslateLeft() && point.y >= mFolderView.getTranslateTop()
                                && point.x <= folderScrollView.getWidth() + mFolderView.getTranslateLeft()
                                && point.y <= folderScrollView.getHeight() + mFolderView.getTranslateTop() ) {
                            if (oldHitTest2.index != hitTest2.index || oldHitTest2.inIcon != hitTest2.inIcon) {
                                oldHitTest2.index = hitTest2.index;
                                oldHitTest2.inIcon = hitTest2.inIcon;
                                if (moveIconWaiter != null) {
                                    moveIconWaiter.cancel();
                                    moveIconWaiter = null;
                                }
                                moveIconWaiter = new CancellableQueueTimer(handler, 100, moveIconDetacher);
                            }
                        }
                    } else {
                        if (moveIconWaiter != null) {
                            moveIconWaiter.cancel();
                            moveIconWaiter = null;
                        }
                        if (mover.isAboveFolder()) {
                            mover.bisideFolder();
                            mover.setIndex(mover.getsIndex());
                            currentPage.removeFolderBound();
                            if (moveIntoFolderWaiter != null) {
                                moveIntoFolderWaiter.cancel();
                                moveIntoFolderWaiter = null;
                            }
                        }
                    }

                }
                return true;
            } else {
                folderScrollView.scrollTo(0, (int) (scrollPointY - (ev.getY() - y)));
                return false;
            }
        }

        private boolean actionUpFolder(final MotionEvent ev) {
            isDesktopActionDown = false;
            isFolderActionDown = false;
            if (jiggleModeWaiter != null) {
                jiggleModeWaiter.cancel();
                jiggleModeWaiter = null;
            }

            if (startDragWaiter != null) {
                startDragWaiter.cancel();
                startDragWaiter = null;
            }
            if (moveToDesktopWaiter != null) {
                moveToDesktopWaiter.cancel();
                moveToDesktopWaiter = null;
            }
            final IPageView currentPage = getPage(true);
            if (currentPage != null) {
                final int select = currentPage.getSelectedIndex();
                if (select >= 0) {
                    ApplicationInfo info = currentPage.getSelectedApp();
                    if (info != null) {
//                                if (!mFolderView.isJiggling()) {
                        EquipmentBean bean = new EquipmentBean();
                        bean.setEqid(info.getEqid());
                        bean.setState(info.getState());
                        bean.setEquipmentName(info.getEquipmentName());
                        bean.setEquipmentDesc(info.getEquipmentDesc());
                        bean.setDeviceid(ConnectionPojo.getInstance().deviceTid);
                        controller.onAppClick(bean);
//                                } else {
//                                    if (hitTest3.buttonRemove) {
//                                        //currentPage.removeApp(hitTest3.index);
//                                        controller.onAppRemove(info);
//                                    }
//                                }
                    }
                }
                currentPage.deselect();
            }
            if (mover.isMoving()) {
                final IPageView p = getPage(true);
                Point point = p.getIconLocation(mover.getIndex());
                final ApplicationInfo app = mover.hook();
                mover.stopMoving(point.x + mFolderView.getTranslateLeft(), point.y + mFolderView.getTranslateTop()
                        - scrollPointY+ (int)getResources().getDimension(R.dimen.toolbar_height)+(int)getResources().getDimension(R.dimen.equipmentList_instrution_height), new IconMover.OnMovingStopped() {
                    @Override
                    public void movingStopped(ApplicationInfo appInfo) {
                        p.clearUp(app);
                        setPagerView.setdrag(true);
                        ((FolderContentView)p).invalidate();
                        touchon = false;
                    }
                });
            }else {
                touchon = false;
            }
            return true;
        }

        @Override
        public boolean onTouch(View v, final MotionEvent ev) {
            hitTest2.index = -1;
            hitTest2.inIcon = false;
            hitTest2.buttonRemove = false;
            if (mFolderView != null) {
                return onTouchFolder(v, ev);
            }
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    return actionDownDevice(ev);
                case MotionEvent.ACTION_MOVE:
                    return actionMoveDevice(ev);
                case MotionEvent.ACTION_CANCEL:
                    if (currentPage != null) {
                        currentPage.deselect();
                    }
                case MotionEvent.ACTION_UP:
                    return actionUpDevice(ev);
            }
            return false;
        }

        private boolean actionDownDevice(final MotionEvent ev) {
            touchon = true;
            if (mover.isMoving()) {
                return false;
            }
            isDrag = false;
            oldHitTest2.index = -1;
            oldHitTest2.inIcon = false;
            mover.setAboveFolder(false);
            x = ev.getX();
            y = ev.getY();
            out_scrollPointY = myScrollView.getScrollY();
            currentPage = getPage(false);
            isDesktopActionDown = true;
            isFolderActionDown = false;
            if (currentPage != null) {
                currentPage.hitTest3((int) x, (int) y+out_scrollPointY, hitTest3);
                if (hitTest3.index >= 0) {
                    currentPage.select(hitTest3.index);
                    ApplicationInfo info = currentPage.getIcon(hitTest3.index);
                    if (info != null) {
                        if (startDragWaiter == null) {
                            startDragWaiter = new CancellableQueueTimer(handler, 200, startDragDetacher);
                        }
                        hitTest3.buttonRemove = false;
                    }
                }

            }
            return true;
        }

        private boolean actionMoveDevice(final MotionEvent ev) {
            currentPage = getPage(false);
            if (!isDesktopActionDown) {
                x = ev.getX();
                y = ev.getY();
                out_scrollPointY = myScrollView.getScrollY();
                isFolderActionDown = false;
                isDesktopActionDown = true;
            }
            //                   if (jma.isJiggling()) {
            if (getDistance(ev.getX(), ev.getY(), x, y) <= touchSlop) {
                if (currentPage != null) {
                    if (isDrag) {
                        detachIcon(currentPage,currentPage.getSelectedIndex(), false);
                    }
                }
            } else {
                if (currentPage != null) {
                    currentPage.deselect();
                }
                if (startDragWaiter != null) {
                    startDragWaiter.cancel();
                    startDragWaiter = null;
                }
                hitTest3.buttonRemove = false;
            }
            if (mover.isMoving()) {
                Point point = new Point((int) ev.getX(), (int) ev.getY());
                mover.moveTo(point.x, point.y);
                mFrame.invalidate(mover.getBounds());
                if (currentPage != null) {

                    if (point.y  < myScrollView.getHeight() / 3) {
                        myScrollView.scrollBy(0, -(int)getResources().getDimension(R.dimen.equipmentList_scroll_by));
                        out_scrollPointY = myScrollView.getScrollY();
                    }
                    if (point.y  > myScrollView.getHeight() * 2 / 3) {
                        myScrollView.scrollBy(0, (int)getResources().getDimension(R.dimen.equipmentList_scroll_by));
                        out_scrollPointY = myScrollView.getScrollY();
                    }


                    int position = currentPage.hitTest2(point.x, point.y+ out_scrollPointY, hitTest2,
                            mover.hook().getType() == ItemInfo.TYPE_FOLDER);
                    if (position == -1) {
                        return true;
                    } else if (position == 1) {
                        return true;
                    } else if (position == 0) {
                        if (hitTest2.index >= 0) {
                            if (oldHitTest2.index != hitTest2.index
                                    || oldHitTest2.inIcon != hitTest2.inIcon) {
                                oldHitTest2.index = hitTest2.index;
                                oldHitTest2.inIcon = hitTest2.inIcon;
                                if (moveIconWaiter != null) {
                                    moveIconWaiter.cancel();
                                    moveIconWaiter = null;
                                }
                                moveIconWaiter = new CancellableQueueTimer(handler, 100, moveIconDetacher);
                            }
                        }
                    } else {
                        if (moveIconWaiter != null) {
                            moveIconWaiter.cancel();
                            moveIconWaiter = null;
                        }
                        if (mover.isAboveFolder()) {
                            mover.bisideFolder();
                            mover.setIndex(mover.getsIndex());
                            currentPage.removeFolderBound();
                            if (moveIntoFolderWaiter != null) {
                                moveIntoFolderWaiter.cancel();
                                moveIntoFolderWaiter = null;
                            }
                        }
                    }
                }
                return true;
            } else {
                myScrollView.scrollTo(0, (int) (out_scrollPointY - (ev.getY() - y)));
                return false;
            }
        }

        private boolean actionUpDevice(final MotionEvent ev) {
            if (jiggleModeWaiter != null) {
                jiggleModeWaiter.cancel();
                jiggleModeWaiter = null;
            }
            if (startDragWaiter != null) {
                startDragWaiter.cancel();
                startDragWaiter = null;
            }
            isDesktopActionDown = false;
            isFolderActionDown = false;
            final IPageView currentPage = getPage(false);
            if (currentPage != null) {
                final int select = currentPage.getSelectedIndex();
                if (select >= 0) {
                    ApplicationInfo info = currentPage.getSelectedApp();
                    if (info != null) {
//                                if (!jma.isJiggling()) {
                        if (info.getType() == ItemInfo.TYPE_FOLDER) {
                            openFolderIndex = select;
                            openFolder((FolderInfo) info);
                        } else {
                            EquipmentBean bean = new EquipmentBean();
                            bean.setEqid(info.getEqid());
                            bean.setState(info.getState());
                            bean.setEquipmentName(info.getEquipmentName());
                            bean.setEquipmentDesc(info.getEquipmentDesc());
                            bean.setDeviceid(ConnectionPojo.getInstance().deviceTid);
                            controller.onAppClick(bean);
                        }
//                                } else {
//                                    if (hitTest3.buttonRemove&&info.getType()!=ItemInfo.TYPE_FOLDER) {
//                                        //currentPage.removeApp(hitTest3.index);
//                                        controller.onAppRemove(info);
//                                    }
//                                    if (info.getType() == ItemInfo.TYPE_FOLDER && !isDrag) {
//                                        openFolderIndex = select;
//                                        openFolder((FolderInfo) info);
//                                    }
//                                }

                    }
                }
                currentPage.deselect();
            }
            if (mover.isMoving()) {
                final int lasty = (int)ev.getY();
                final IPageView p = getPage(false);
                if (p != null) {
                    Point point = p.getIconLocation(mover.getIndex());
                    final ApplicationInfo app = mover.hook();

                    final int i = mover.getIndex();
                    if (!mover.isAboveFolder()) {
                        mover.stopMoving(point.x,
                                point.y-out_scrollPointY+(int)getResources().getDimension(R.dimen.toolbar_height)+(int)getResources().getDimension(R.dimen.equipmentList_instrution_height), new IconMover.OnMovingStopped() {
                                    @Override
                                    public void movingStopped(ApplicationInfo appInfo) {
                                        top_lay.setVisibility(View.GONE);
                                        p.clearUp(app);
                                        initContentLayout();
                                        setPagerView.setdrag(true);
                                        touchon = false;
                                        if(lasty<0&&((int)ev.getX())>screenWidth/2&&app.getType() == ItemInfo.TYPE_APP){

                                            String ds = String.format(getResources().getString(R.string.want_to_delete_confirm_eq),app.getEquipmentName());

                                            ECAlertDialog elc = ECAlertDialog.buildAlert(DeviceFragment.this.getActivity(), ds, getResources().getString(R.string.cancel), getResources().getString(R.string.ok), null, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    deleteBean = app;
                                                    SendCommand.Command = SendCommand.DELETE_EQUIPMENT;
                                                    sed.deleteEquipment(app.getEqid());
                                                }
                                            });
                                            elc.show();


                                        }else if(lasty<0&&((int)ev.getX())<screenWidth/2&&app.getType() == ItemInfo.TYPE_APP){

                                            String ds = String.format(getResources().getString(R.string.want_to_replace_confirm_eq),app.getEquipmentName());

                                            ECAlertDialog elc = ECAlertDialog.buildAlert(DeviceFragment.this.getActivity(), ds, getResources().getString(R.string.cancel), getResources().getString(R.string.ok), null, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    SendCommand.Command  = SendCommand.REPLACE_EQUIPMENT;
                                                    sed.replaceEquipment(app.getEqid());
                                                    Intent intent =new Intent(DeviceFragment.this.getActivity(),AddDeviceActivity.class);
                                                    Bundle bundle = new Bundle();
                                                    bundle.putString("eqid",app.getEqid());
                                                    intent.putExtras(bundle);
                                                    startActivity(intent);
                                                }
                                            });
                                            elc.show();
                                        }
                                    }
                                });

                    } else {
                        mover.setAboveFolder(false);
                        p.removeFolderBound();
                        mover.moveIntoFolder(point.x,
                                point.y-out_scrollPointY+ (int)getResources().getDimension(R.dimen.toolbar_height)+(int)getResources().getDimension(R.dimen.equipmentList_instrution_height), new IconMover.OnMovingStopped() {
                                    @Override
                                    public void movingStopped(ApplicationInfo appInfo) {
                                        LOG.I("ceshi","停止了");
                                        top_lay.setVisibility(View.GONE);
                                        p.addToFolder(i, app);
                                        p.clearUp(null);
                                        initContentLayout();
                                        setPagerView.setdrag(true);
                                        touchon = false;
                                    }
                                });
                    }
                }
            }else {
                touchon = false;
            }

            return true;
        }

        private void detachIcon(IPageView page, int index, boolean isFolder) {

            ApplicationInfo info = page.getIcon(index);
            if (info == null)
                return;
            page.deselect();
            Point point = page.getIconLocation(index);
            if (!mover.isMoving()) {
                setPagerView.setdrag(false);
                if (isFolder) {
                    mover.startMoving(info, point.x + mFolderView.getTranslateLeft(), mFolderView.getTranslateTop()
                            + point.y - folderScrollView.getScrollY()+ (int)getResources().getDimension(R.dimen.toolbar_height)+(int)getResources().getDimension(R.dimen.equipmentList_instrution_height), (int) x, (int) y);
                } else {
                    mover.startMoving(info, point.x, point.y- myScrollView.getScrollY()+ (int)getResources().getDimension(R.dimen.toolbar_height)+(int)getResources().getDimension(R.dimen.equipmentList_instrution_height), (int) x, (int) y);
                }
                mover.setIndex(index);
                mover.setsIndex(index);
            }
            page.setIconIntoPage(index, null);
        }
    };


    public void closeFolder() {
        //更新缓存分组名称
        try {
            PackDAO dao = new PackDAO(DeviceFragment.this.getActivity());
            FolderInfo ifno = (FolderInfo) list.get(openFolderIndex);
            ifno.setEquipmentName(mFolderView.getEditContent());
            dao.updateName(ifno);
            LOG.I("ceshi","更新名称为"+mFolderView.getEditContent());
        }catch (Exception e){

        }
        page = (MyContentView) getPage(false);
        Transformation transformation = new Transformation();
        if (mFolderView != null
                && !mFolderView.getAnimation().getTransformation(AnimationUtils.currentAnimationTimeMillis(),
                transformation)) {
            Point point = page.getIconLocation(openFolderIndex);
            Animation animation = pp.createAnimationOpenFolder(point.x, point.y, screenWidth, screenHeight, false);
            animation.setAnimationListener(new Animation.AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mContainer.removeView(mFolderView);
                    mFolderView = null;
                    getPage(false).clearUp(null);
                }
            });
            if (mFolderView != null) {
                mFolderView.doneEditingFolderName(true);
                mFolderView.startAnimation(animation);
            }
            Animation pageAnimation = pp.createAnimationPageShow(true);
            page.startAnimation(pageAnimation);
        }

        InputMethodManager inputMethodManager = (InputMethodManager) this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if(inputMethodManager != null ) {
            View localView = this.getActivity().getCurrentFocus();
            if(localView != null && localView.getWindowToken() != null ) {
                IBinder windowToken = localView.getWindowToken();
                inputMethodManager.hideSoftInputFromWindow(windowToken, 0);
            }
        }
    }

    private void openFolder(FolderInfo folderInfo) {
        if (mFolderView == null) {
            page = (MyContentView) getPage(false);
            int index;
            if (hitTest2.index != -1) {
                index = hitTest2.index;
            } else {
                index = page.getSelectedIndex();
            }
            Point point = page.getIconLocation(index);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            mFolderView = folderInfo.getFolderView(DeviceFragment.this.getActivity());
            if (mFolderView.getParent() == null) {
                mContainer.addView(mFolderView, params);
            }
            mFolderView.onReday(lc, pp, screenHeight);
            Animation folderAnimation = pp.createAnimationOpenFolder(point.x, point.y, screenWidth, screenHeight, true);
            mFolderView.startAnimation(folderAnimation);
            Animation pageAnimation = pp.createAnimationPageShow(false);
            page.startAnimation(pageAnimation);
            page.drawback = 0;
            page.removeFolderBound();
            page.clearUp(null);
            mFolderView.setEditingEnable(true);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    @Subscribe          //订阅事件Event
    public  void onEventMainThread(STEvent event){
        if(event.getEvent()== SendCommand.DELETE_EQUIPMENT){
            ED.deleteByEqid(deleteBean);
            if(NameSolve.MODE_BUTTON.equals(NameSolve.getEqType(deleteBean.getEquipmentDesc()))){
                shortcutDAO.deleteShortcurtByEqid(ConnectionPojo.getInstance().deviceTid,deleteBean.getEqid());
            }else if(NameSolve.TH_CHECK.equals(NameSolve.getEqType(deleteBean.getEquipmentDesc()))){
                ThcheckEvent thcheckEvent = new ThcheckEvent();
                EventBus.getDefault().post(thcheckEvent);
            }
            refresh();
            SendCommand.clearCommnad();
        }

    }

    static interface SetPagerView{
        void setdrag(boolean flag);
    }

    public void setInterfacePagerView(SetPagerView setPagerView){
        this.setPagerView = setPagerView;
    }

    public Boolean getTouchon() {
        return touchon;
    }
}
