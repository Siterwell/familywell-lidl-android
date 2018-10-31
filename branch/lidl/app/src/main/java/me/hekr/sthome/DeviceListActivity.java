package me.hekr.sthome;

import android.content.DialogInterface;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import me.hekr.sdk.Hekr;
import me.hekr.sthome.autoudp.ControllerWifi;
import me.hekr.sthome.common.TopbarSuperActivity;
import me.hekr.sthome.commonBaseView.ECAlertDialog;
import me.hekr.sthome.commonBaseView.ProgressDialog;
import me.hekr.sthome.commonBaseView.RefreshableView2;
import me.hekr.sthome.commonBaseView.SlideListView;
import me.hekr.sthome.event.STEvent;
import me.hekr.sthome.http.HekrUser;
import me.hekr.sthome.http.HekrUserAction;
import me.hekr.sthome.http.bean.DcInfo;
import me.hekr.sthome.http.bean.DeviceBean;
import me.hekr.sthome.main.MainActivity;
import me.hekr.sthome.model.modeladapter.ModifyAdapeter;
import me.hekr.sthome.model.modelbean.MyDeviceBean;
import me.hekr.sthome.model.modelbean.SceneBean;
import me.hekr.sthome.model.modelbean.SysModelBean;
import me.hekr.sthome.model.modeldb.DeviceDAO;
import me.hekr.sthome.model.modeldb.SceneDAO;
import me.hekr.sthome.model.modeldb.SysmodelDAO;
import me.hekr.sthome.tools.ConnectionPojo;
import me.hekr.sthome.tools.UnitTools;


public class DeviceListActivity extends TopbarSuperActivity implements ModifyAdapeter.DoneWithItemListener{
    private final String TAG = "DeviceListActivity";
    private List<MyDeviceBean> lists = new ArrayList<>();
    private SlideListView lv;
    private ModifyAdapeter mo;
    private ECAlertDialog alertDialog;
    private DeviceDAO DDO;
    private SysmodelDAO SDO;
    private SceneDAO sceneDAO;
    private RefreshableView2 refreshableView;
    private String tid;
    private ProgressDialog progressDialog;
    private String choosetoDeviceid;
    private View empty;

    @Override
    protected void onCreateInit() {
        EventBus.getDefault().register(this);
        initViewGuider();
        getTopBarView().setSettingBg(R.drawable.edit_right);
        initView();
        getDevices();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_device_list;
    }

    private void initView() {
        progressDialog = new ProgressDialog(this);
        sceneDAO = new SceneDAO(this);
        DDO = new DeviceDAO(this);
        try {
            tid = DDO.findByChoice(1).getDevTid();
        }catch (NullPointerException e){
            tid = null;
            Log.i(TAG,"没有查到");
        }

        lv = (SlideListView) findViewById(R.id.eqlist);
        empty = findViewById(R.id.empty);
        TextView textView_em  = (TextView)empty.findViewById(R.id.textempty);
        textView_em.setText(getResources().getString(R.string.gateway_empty));
        lv.setEmptyView(empty);
        refreshableView = (RefreshableView2)findViewById(R.id.refresh);
        lists = DDO.findAllDevice();
        if(lists != null){
            mo = new ModifyAdapeter(this,lists,lv,this);
            lv.setAdapter(mo);
        }
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final DeviceBean deviceBean = lists.get(position);
                if(lv.getStatus()== SlideListView.MODE.CAN_EDIT){
                    mo.donghua_num = lv.getLastVisiblePosition()-lv.getFirstVisiblePosition()+1;
                    lv.setStatus(SlideListView.MODE.SCROLL_INIT);
                    mo.notifyDataSetChanged();
                    getTopBarView().setSettingBg(R.drawable.edit_right);
                    alertDialog = ECAlertDialog.buildAlert(DeviceListActivity.this, getResources().getString(R.string.update_name), getResources().getString(R.string.cancel), getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.setDismissFalse(true);
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            EditText text = (EditText) alertDialog.getContent().findViewById(R.id.tet);
                            final String newname = text.getText().toString().trim();

                            if(!TextUtils.isEmpty(newname)){

                                if(newname.length()<=20){
                                    alertDialog.setDismissFalse(true);
                                    HekrUserAction.getInstance(DeviceListActivity.this).renameDevice(deviceBean.getDevTid(), deviceBean.getCtrlKey(), newname, null, new HekrUser.RenameDeviceListener() {
                                        @Override
                                        public void renameDeviceSuccess() {
                                            DDO.updateDeivceName(deviceBean.getDevTid(),newname);
                                            getDevices();
                                            Toast.makeText(DeviceListActivity.this,getResources().getString(R.string.set_devname_success),Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void renameDeviceFail(int errorCode) {
                                            Toast.makeText(DeviceListActivity.this, UnitTools.errorCode2Msg(DeviceListActivity.this,errorCode),Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }else{
                                    alertDialog.setDismissFalse(false);
                                    Toast.makeText(DeviceListActivity.this,getResources().getString(R.string.name_is_too_long),Toast.LENGTH_SHORT).show();
                                }


                            }
                            else{
                                alertDialog.setDismissFalse(false);
                                Toast.makeText(DeviceListActivity.this,getResources().getString(R.string.name_is_null),Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                    alertDialog.setContentView(R.layout.edit_alert);
                    alertDialog.setTitle( getResources().getString(R.string.update_name));
                    EditText text = (EditText) alertDialog.getContent().findViewById(R.id.tet);
                    if("报警器".equals(deviceBean.getDeviceName())){
                        text.setText(getResources().getString(R.string.my_home));
                        text.setSelection(getResources().getString(R.string.my_home).length());
                    }else{
                        text.setText(deviceBean.getDeviceName());
                        text.setSelection(deviceBean.getDeviceName().length());
                    }



                    alertDialog.show();

                }else if(lv.getStatus()== SlideListView.MODE.SCROLL_INIT || lv.getStatus()== SlideListView.MODE.INIT){
                    saveNewDevice(deviceBean);
                }
            }
        });

        refreshableView.setOnRefreshListener(new RefreshableView2.PullToRefreshListener() {
            @Override
            public void onRefresh() {
                DeviceListActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getDevices();
                    }
                });

            }
        }, 0);
    }

    private void saveNewDevice(DeviceBean deviceBean) {

       DDO.updateDeivceChoice(deviceBean.getDevTid());

        if((tid != null && !tid.equals(deviceBean.getDevTid())) || tid == null){
            Log.i(TAG,"+++++++++++++++++++this is clear code");
            SysModelBean sysModelBean1 = new SysModelBean();
            SDO = new SysmodelDAO(this);
            sysModelBean1.setChice("N");
            sysModelBean1.setSid("0");
            sysModelBean1.setModleName("在家");
            sysModelBean1.setColor("F0");
            sysModelBean1.setDeviceid(deviceBean.getDevTid());

            SysModelBean sysModelBean2 = new SysModelBean();
            sysModelBean2.setChice("N");
            sysModelBean2.setSid("1");
            sysModelBean2.setModleName("离家");
            sysModelBean2.setColor("F1");
            sysModelBean2.setDeviceid(deviceBean.getDevTid());

            SysModelBean sysModelBean3 = new SysModelBean();
            sysModelBean3.setChice("N");
            sysModelBean3.setSid("2");
            sysModelBean3.setModleName("睡眠");
            sysModelBean3.setColor("F2");
            sysModelBean3.setDeviceid(deviceBean.getDevTid());

            SDO.addinit(sysModelBean1);
            SDO.addinit(sysModelBean2);
            SDO.addinit(sysModelBean3);

            SceneBean sceneBean = new SceneBean();
            sceneBean.setDeviceid(deviceBean.getDevTid());
            sceneBean.setSid("-1");
            sceneBean.setMid("129");
            sceneBean.setCode("");
            sceneBean.setName("");
            sceneDAO.addinit(sceneBean);

            SceneBean sceneBean2 = new SceneBean();
            sceneBean2.setDeviceid(deviceBean.getDevTid());
            sceneBean2.setSid("-1");
            sceneBean2.setMid("130");
            sceneBean2.setCode("");
            sceneBean2.setName("");
            sceneDAO.addinit(sceneBean2);

            SceneBean sceneBean3 = new SceneBean();
            sceneBean3.setDeviceid(deviceBean.getDevTid());
            sceneBean3.setSid("-1");
            sceneBean3.setMid("131");
            sceneBean3.setCode("");
            sceneBean3.setName("");
            sceneDAO.addinit(sceneBean3);

            SceneBean sceneBean4 = new SceneBean();
            sceneBean4.setDeviceid(deviceBean.getDevTid());
            sceneBean4.setSid("1");
            sceneBean4.setMid("129");
            sceneBean4.setCode("");
            sceneBean4.setName("");
            sceneDAO.addinit(sceneBean4);

            SceneBean sceneBean5 = new SceneBean();
            sceneBean5.setDeviceid(deviceBean.getDevTid());
            sceneBean5.setSid("1");
            sceneBean5.setMid("130");
            sceneBean5.setCode("");
            sceneBean5.setName("");
            sceneDAO.addinit(sceneBean5);

            SceneBean sceneBean6 = new SceneBean();
            sceneBean6.setDeviceid(deviceBean.getDevTid());
            sceneBean6.setSid("2");
            sceneBean6.setMid("130");
            sceneBean6.setCode("");
            sceneBean6.setName("");
            sceneDAO.addinit(sceneBean6);

            tid = deviceBean.getDevTid();
        }
        lists.clear();
        lists = DDO.findAllDevice();
        mo.refreshList(lists);

        String name = null;
        if("报警器".equals(deviceBean.getDeviceName())){
            name = getResources().getString(R.string.my_home);
        }else{
            name = deviceBean.getDeviceName();
        }
        Toast.makeText(DeviceListActivity.this,getResources().getString(R.string.connect_alert)+name+(deviceBean.isOnline()?getResources().getString(R.string.on_line):getResources().getString(R.string.off_line)),Toast.LENGTH_LONG).show();

        ControllerWifi.getInstance().wifiTag = false;
        ConnectionPojo.getInstance().deviceTid = deviceBean.getDevTid();
        ConnectionPojo.getInstance().bind = deviceBean.getBindKey();
        ConnectionPojo.getInstance().ctrlKey = deviceBean.getCtrlKey();
        ConnectionPojo.getInstance().IMEI =  Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        ConnectionPojo.getInstance().propubkey = deviceBean.getProductPublicKey();
        ConnectionPojo.getInstance().domain = deviceBean.getDcInfo().getConnectHost();
        ConnectionPojo.getInstance().binversion = deviceBean.getBinVersion();

        //刷新主页头部
        STEvent stEvent = new STEvent();
        stEvent.setRefreshevent(1);
        EventBus.getDefault().post(stEvent);

        //刷新主页数据
        STEvent stEvent2 = new STEvent();
        stEvent2.setRefreshevent(3);
        EventBus.getDefault().post(stEvent2);

        //开启搜索局域网服务
        STEvent stEvent3= new STEvent();
        stEvent3.setServiceevent(6);
        EventBus.getDefault().post(stEvent3);
        ControllerWifi.getInstance().choose_gateway =true;
        MainActivity.flag_checkfireware = true;
    }

    /**
     * 获取设备列表
     */
    private void getDevices() {
        HekrUserAction.getInstance(this).getDevices(0, 80, new HekrUser.GetDevicesListener() {
            @Override
            public void getDevicesSuccess(final List<DeviceBean> devicesLists) {
                Log.i(TAG,devicesLists.toString());
                DDO = new DeviceDAO(DeviceListActivity.this);
                DDO.deleteAll();
                DeviceBean deviceBean = null;
                HashSet<String> set = new HashSet<>();
                for (DeviceBean bean : devicesLists) {
                    set.add(bean.getDcInfo().getConnectHost());
                    MyDeviceBean bean2 = new MyDeviceBean();
                    bean2.setDevTid(bean.getDevTid());
                    bean2.setCtrlKey(bean.getCtrlKey());
                    bean2.setBindKey(bean.getBindKey());
                    bean2.setDeviceName(bean.getDeviceName());
                    bean2.setOnline(bean.isOnline());
                    bean2.setProductPublicKey(bean.getProductPublicKey());
                    DcInfo dcInfo = new DcInfo();
                    dcInfo.setConnectHost(bean.getDcInfo().getConnectHost());
                    bean2.setDcInfo(dcInfo);
                    bean2.setBinVersion(bean.getBinVersion());
                    bean2.setBinType(bean.getBinType());
                    if (!TextUtils.isEmpty(tid) && tid.equals(bean.getDevTid())) {
                        bean2.setChoice(1);
                    } else {
                        bean2.setChoice(0);
                    }
                    DDO.addDevice(bean2);

                   if(!TextUtils.isEmpty(choosetoDeviceid)&&bean2.getDevTid().equals(choosetoDeviceid)){
                       deviceBean = bean2;
                   }
                }
                if(devicesLists.size()>0) {
                    Hekr.getHekrClient().setHosts(set);
                }
                lists.clear();
                lists = DDO.findAllDevice();
                mo.refreshList(lists);
                getTopBarView().setSettingBg(R.drawable.edit_right);
                lv.setStatus(SlideListView.MODE.SCROLL_INIT);
                refreshableView.finishRefreshing();

                if(deviceBean!=null){
                    choosetoDeviceid = null;
                    saveNewDevice(deviceBean);
                }
                //刷新主页头部
                STEvent stEvent = new STEvent();
                stEvent.setRefreshevent(1);
                EventBus.getDefault().post(stEvent);
            }
            @Override
            public void getDevicesFail(int errorCode) {
                if(errorCode == 1){
                    ControllerWifi.getInstance().choose_gateway =true;
                    finish();
                }
                Toast.makeText(DeviceListActivity.this, UnitTools.errorCode2Msg(DeviceListActivity.this,errorCode),Toast.LENGTH_LONG).show();
                refreshableView.finishRefreshing();
            }
        });

    }


    private void initViewGuider() {
        choosetoDeviceid = getIntent().getStringExtra("devid");
        getTopBarView().setTopBarStatus(1, 1, getResources().getString(R.string.device_list), null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lv.getStatus()== SlideListView.MODE.INIT || lv.getStatus()== SlideListView.MODE.SCROLL_INIT){
                    mo.donghua_num = lv.getLastVisiblePosition()-lv.getFirstVisiblePosition()+1;
                    lv.setStatus(SlideListView.MODE.CAN_EDIT);
                    mo.notifyDataSetChanged();
                    getTopBarView().setSettingBg(R.drawable.ok);
                }else if(lv.getStatus()== SlideListView.MODE.CAN_EDIT){
                    mo.donghua_num = lv.getLastVisiblePosition()-lv.getFirstVisiblePosition()+1;
                    lv.setStatus(SlideListView.MODE.SCROLL_INIT);
                    mo.notifyDataSetChanged();
                    getTopBarView().setSettingBg(R.drawable.edit_right);
                }else if(lv.getStatus()== SlideListView.MODE.CAN_DELETE){
                    mo.donghua_num = lv.getLastVisiblePosition()-lv.getFirstVisiblePosition()+1;
                    lv.scrollBackEx();
                    mo.notifyDataSetChanged();
                    getTopBarView().setSettingBg(R.drawable.edit_right);
                }
            }
        });

    }
    /**
     * 解除绑定
     */
    private void delete_device(final DeviceBean deviceBean) {
        HekrUserAction.getInstance(this).deleteDevice(deviceBean.getDevTid(), deviceBean.getBindKey(), new HekrUser.DeleteDeviceListener() {
            @Override
            public void deleteDeviceSuccess() {
                Toast.makeText(DeviceListActivity.this,getResources().getString(R.string.delete_success),Toast.LENGTH_LONG).show();
                getDevices();

                if(TextUtils.isEmpty(ConnectionPojo.getInstance().deviceTid)){
                    ConnectionPojo.getInstance().bind = null;
                    ConnectionPojo.getInstance().deviceTid = null;
                    ConnectionPojo.getInstance().ctrlKey = null;
                    ConnectionPojo.getInstance().IMEI =  null;
                    ConnectionPojo.getInstance().propubkey = null;
                    ConnectionPojo.getInstance().domain = null;
                    ConnectionPojo.getInstance().binversion = null;
                    ControllerWifi.getInstance().wifiTag =false;
                }
               else if((!TextUtils.isEmpty(ConnectionPojo.getInstance().deviceTid)) &&  ConnectionPojo.getInstance().deviceTid.equals(deviceBean.getDevTid())){
                    ConnectionPojo.getInstance().bind = null;
                    ConnectionPojo.getInstance().deviceTid = null;
                    ConnectionPojo.getInstance().ctrlKey = null;
                    ConnectionPojo.getInstance().IMEI =  null;
                    ConnectionPojo.getInstance().propubkey = null;
                    ConnectionPojo.getInstance().domain = null;
                    ConnectionPojo.getInstance().binversion = null;
                    ControllerWifi.getInstance().wifiTag =false;
                }

                DDO.deleteByDeviceId(deviceBean.getDevTid());
                STEvent stEvent = new STEvent();
                stEvent.setRefreshevent(1);
                EventBus.getDefault().post(stEvent);

                STEvent stEvent2 = new STEvent();
                stEvent2.setRefreshevent(3);
                EventBus.getDefault().post(stEvent2);
            }

            @Override
            public void deleteDeviceFail(int errorCode) {
                Toast.makeText(DeviceListActivity.this, UnitTools.errorCode2Msg(DeviceListActivity.this,errorCode),Toast.LENGTH_LONG).show();
            }
        });
    }



    @Override
    public void delete(int position) {
        getTopBarView().setSettingBg(R.drawable.edit_right);
        final MyDeviceBean deviceBean = lists.get(position);

        String ds2 = "报警器".equals(deviceBean.getDeviceName())?getResources().getString(R.string.my_home):deviceBean.getDeviceName();
        String ds = String.format(getResources().getString(R.string.delete_or_unbind_device),ds2,deviceBean.getDevTid());

        ECAlertDialog elc = ECAlertDialog.buildAlert(this,ds, getResources().getString(R.string.cancel), getResources().getString(R.string.ok), null, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mo.donghua_num = lv.getLastVisiblePosition()-lv.getFirstVisiblePosition()+1;
                lv.scrollBackEx();
                mo.notifyDataSetChanged();
                delete_device(deviceBean);

            }
        });
        elc.show();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    @Subscribe          //订阅事件Event
    public  void onEventMainThread(STEvent event){
            if(event.getRefreshevent()==7){
                progressDialog.setPressText(event.getProgressText());
                if(!progressDialog.isShowing()){
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                }
            }else if(event.getRefreshevent()==3){
                if(progressDialog.isShowing()){
                    progressDialog.dismiss();
                    finish();
                }
            }else if(event.getRefreshevent()==6){
                if(progressDialog.isShowing()){
                    progressDialog.dismiss();
                    finish();
                }
            }
    }

}
