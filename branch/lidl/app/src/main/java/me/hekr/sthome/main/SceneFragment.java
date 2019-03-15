package me.hekr.sthome.main;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import me.hekr.sthome.R;
import me.hekr.sthome.commonBaseView.ECAlertDialog;
import me.hekr.sthome.commonBaseView.RefreshableView;
import me.hekr.sthome.commonBaseView.SlideListView;
import me.hekr.sthome.commonBaseView.TopBarView;
import me.hekr.sthome.equipment.adapter.EqpAdapter;
import me.hekr.sthome.event.STEvent;
import me.hekr.sthome.history.DataFromSceneGroup;
import me.hekr.sthome.http.HekrUserAction;
import me.hekr.sthome.model.SysDetaiActivity;
import me.hekr.sthome.model.TimerListActivity;
import me.hekr.sthome.model.addsmodel.AddSystemActivity;
import me.hekr.sthome.model.modeladapter.ModleSysAdapter;
import me.hekr.sthome.model.modelbean.MyDeviceBean;
import me.hekr.sthome.model.modelbean.SceneBean;
import me.hekr.sthome.model.modelbean.SysModelBean;
import me.hekr.sthome.model.modeldb.DeviceDAO;
import me.hekr.sthome.model.modeldb.SceneDAO;
import me.hekr.sthome.model.modeldb.ShortcutDAO;
import me.hekr.sthome.model.modeldb.SysmodelDAO;
import me.hekr.sthome.model.newstyle.ModelConditionPojo;
import me.hekr.sthome.model.newstyle.NewGroup2Activity;
import me.hekr.sthome.model.newstyle.SceneCopyPojo;
import me.hekr.sthome.tools.ConnectionPojo;
import me.hekr.sthome.tools.LOG;
import me.hekr.sthome.tools.SendCommand;
import me.hekr.sthome.tools.SendSceneData;
import me.hekr.sthome.tools.SendSceneGroupData;
import me.hekr.sthome.tools.UnitTools;


/**
 * Created by xjj on 2016/12/6.
 */

@SuppressLint("ValidFragment")
public class SceneFragment extends Fragment implements EqpAdapter.DoneWithItemListener,ModleSysAdapter.DoneWithItemLisenter{

    private View view = null;

    private static final String TAG  = "ScenceFragment";
    private List<SysModelBean> slist =new ArrayList<SysModelBean>();
    private TextView addAct;
    private EqpAdapter<SceneBean> mAdapter;

    private SlideListView mlistView,sListView;
    private List<SceneBean> mlist;
    private SysmodelDAO SD;
    private ShortcutDAO shortcutDAO;
    private ModleSysAdapter msAdapter;
    private int mTouchNumber = -1;
    private int handleSceneGroupSid = -1;
    private SendSceneGroupData ssgd;
    private SendSceneData ssd;
    private TopBarView topBarView;
    private SceneDAO SED;
    public RefreshableView refreshableView;
    private LinearLayout root;

    public SceneFragment()
    {
        super();

    }

    public int getHandleSceneGroupSid() {
        return handleSceneGroupSid;
    }

    public void setHandleSceneGroupSid(int handleSceneGroupSid) {
        this.handleSceneGroupSid = handleSceneGroupSid;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(view==null)
        {
            view = inflater.inflate(R.layout.fragment_modle, null);
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

    ssgd = new SendSceneGroupData(this.getActivity()) {
        @Override
        protected void sendEquipmentDataFailed() {
            LOG.I(TAG,"operation failed");
        }

        @Override
        protected void sendEquipmentDataSuccess() {
            LOG.I(TAG,"operation success");
        }
    };
    ssd = new SendSceneData(this.getActivity()) {
        @Override
        protected void sendEquipmentDataFailed() {
            LOG.I(TAG,"operation failed");
        }

        @Override
        protected void sendEquipmentDataSuccess() {
            LOG.I(TAG,"operation success");
        }
    };
    root       = (LinearLayout)view.findViewById(R.id.root);
    topBarView = (TopBarView)view.findViewById(R.id.top_bar);
    topBarView.setTopBarStatus(1, 1, 1, null, getResources().getString(R.string.system_scene), null, new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(TextUtils.isEmpty(ConnectionPojo.getInstance().deviceTid)){
                Toast.makeText(SceneFragment.this.getActivity(),getResources().getString(R.string.please_choose_device),Toast.LENGTH_LONG).show();
            }else{
                startActivity(new Intent(SceneFragment.this.getActivity(), TimerListActivity.class));
            }

        }
    }, new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (slist.size() < 8) {
                startActivity(new Intent(SceneFragment.this.getActivity(), AddSystemActivity.class));
            } else {
                Toast.makeText(SceneFragment.this.getActivity(), getResources().getString(R.string.get_max_scene_group), Toast.LENGTH_SHORT).show();
            }
        }
    });
    topBarView.getBackView().setImageResource(R.drawable.dingshi);
    //沉浸式设置支持API19
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        int top = UnitTools.getStatusBarHeight(getActivity());
        root.setPadding(0,top,0,0);
    }

}


    private void initView() {
        refreshableView  = (RefreshableView)view.findViewById(R.id.refr);

        addAct = (TextView) view.findViewById(R.id.btnActAdd);
        addAct.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                ModelConditionPojo mcp = ModelConditionPojo.getInstance();
                mcp.modify = false;
                SceneCopyPojo.getInstance().first = true;
                startActivity(new Intent(SceneFragment.this.getActivity(), NewGroup2Activity.class));
            }
        });
        mlist = new ArrayList<SceneBean>();
        SD = new SysmodelDAO(SceneFragment.this.getActivity());
        SED = new SceneDAO(this.getActivity());
        shortcutDAO = new ShortcutDAO(this.getActivity());
        mlistView = (SlideListView) view.findViewById(R.id.actModleList);
        mAdapter = new EqpAdapter<SceneBean>(this.getActivity(), R.layout.cell_scence_c,mlistView,this) {
            @Override
            protected void initListCell(final int i, View listCell, ViewGroup viewGroup) {
                final SceneBean ac = mAdapter.getItem(i);
                try {
                    ImageView arrow = (ImageView) listCell.findViewById(R.id.arrow);
                    String ds2 = ac.getCode().substring(46,48);
                    TextView number = (TextView) listCell.findViewById(R.id.cellScenceImage);
                    String ds = i<9?("0"+(i+1)):(""+(i+1));
                    number.setText(ds);
                    TextView nameD = (TextView) listCell.findViewById(R.id.cellScenceName);
                    nameD.setText(ac.getName());

                    if("129".equals(ac.getMid())){
                        nameD.setTextColor(getContext().getResources().getColor(R.color.text_desc));
                        nameD.setText(getResources().getString(R.string.pir_default_scene));
                        arrow.setVisibility(View.GONE);
                        Button done = (Button)listCell.findViewById(R.id.done);
                        done.setVisibility(View.GONE);
                    }else if("130".equals(ac.getMid())){
                        nameD.setTextColor(getContext().getResources().getColor(R.color.text_desc));
                        nameD.setText(getResources().getString(R.string.door_default_scene));
                        arrow.setVisibility(View.GONE);
                        Button done = (Button)listCell.findViewById(R.id.done);
                        done.setVisibility(View.GONE);
                    }else if("131".equals(ac.getMid())){
                        nameD.setTextColor(getContext().getResources().getColor(R.color.text_desc));
                        nameD.setText(getResources().getString(R.string.old_man_default_scene));
                        arrow.setVisibility(View.GONE);
                        Button done = (Button)listCell.findViewById(R.id.done);
                        done.setVisibility(View.GONE);
                    }else {
                        nameD.setTextColor(getContext().getResources().getColor(R.color.black));
                        arrow.setVisibility(View.VISIBLE);
                        if("ab".equals(ds2)||"AB".equals(ds2)){
                            Button done = (Button)listCell.findViewById(R.id.done);
                            done.setVisibility(View.VISIBLE);
                        }else{
                            Button done = (Button)listCell.findViewById(R.id.done);
                            done.setVisibility(View.INVISIBLE);
                        }
                    }
                    nameD.setSelected(true);


                }catch (Exception e){
                    TextView number = (TextView) listCell.findViewById(R.id.cellScenceImage);
                    String ds = i<9?("0"+(i+1)):(""+(i+1));
                    number.setText(ds);
                    ImageView arrow = (ImageView) listCell.findViewById(R.id.arrow);
                    arrow.setVisibility(View.GONE);
                    Button done = (Button)listCell.findViewById(R.id.done);
                    done.setVisibility(View.GONE);
                    TextView nameD = (TextView) listCell.findViewById(R.id.cellScenceName);
                    nameD.setTextColor(getContext().getResources().getColor(R.color.text_desc));
                    if("129".equals(ac.getMid())){
                        nameD.setText(getResources().getString(R.string.pir_default_scene));
                    }else if("130".equals(ac.getMid())){
                        nameD.setText(getResources().getString(R.string.door_default_scene));
                    }else if("131".equals(ac.getMid())){
                        nameD.setText(getResources().getString(R.string.old_man_default_scene));
                    }
                    nameD.setSelected(true);
                }

            }
        };
        mlistView.setAdapter(mAdapter);
        refreshableView.setOnRefreshListener(new RefreshableView.PullToRefreshListener() {
            @Override
            public void onRefresh() {
                LOG.D(TAG, "[RYAN] onRefresh !!!");
                STEvent stEvent = new STEvent();
                stEvent.setServiceevent(4);
                EventBus.getDefault().post(stEvent);

            }
        },1);

        sListView = view.findViewById(R.id.sysModleList);
        sListView.initSlideMode(SlideListView.MOD_FORBID);
        msAdapter = new ModleSysAdapter(getActivity(), slist,sListView,this);
        sListView.setAdapter(msAdapter);
        refresh();
    }


    private void addSyslist() {
        if(slist != null){
            slist.clear();
        }
        List<SysModelBean> l = SD.findAllSys(ConnectionPojo.getInstance().deviceTid);
        if(l != null&&l.size()>=3){
            for (SysModelBean b : l){
                slist.add(b);
            }
            LOG.I(TAG,"有系统情景"+slist.toString());
        }else if(l.size() == 0){
            LOG.I(TAG,"无系统情景");
            SysModelBean a = new SysModelBean();
            a.setModleId(-1);
            a.setModleName("Home");
            a.setSid("0");
            a.setColor("F0");
            SysModelBean b = new SysModelBean();
            b.setModleId(-2);
            b.setModleName("Away");
            b.setSid("1");
            b.setColor("F1");
            SysModelBean c = new SysModelBean();
            c.setModleId(-3);
            c.setModleName("Sleep");
            c.setSid("2");
            c.setColor("F2");
            slist.add(a);
            slist.add(b);
            slist.add(c);
        }
    }
    //delete scene code send
    private void deleteSence(SceneBean ac ) {
        SendCommand.Command = SendCommand.DELETE_SCENE;
        ssd.deleteScene(ac.getMid());
    }

    private void doneSence(SceneBean ac){
        // 等网关协议完善
        SendCommand.Command = SendCommand.SCENE_HANDLE;
        ssd.handleScene(ac.getMid());
    }

    @Override
    public void onResume(){
        super.onResume();
        refresh();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void refresh(){
        LOG.D(TAG, "[RYAN] refresh");

       if(!TextUtils.isEmpty( HekrUserAction.getInstance(this.getActivity()).getJWT_TOKEN()))
        try {
            addSyslist();
            msAdapter.refreshLists(slist);
            String ds = SD.findIdByChoice(ConnectionPojo.getInstance().deviceTid).getSid();
            mlist = SED.findAllAmBySidWithDefualt(ds, ConnectionPojo.getInstance().deviceTid);


            Collections.sort(mlist, new Comparator() {

                @Override
                public int compare(Object o, Object t1) {
                    SceneBean a1 = (SceneBean) o;
                    SceneBean a2 = (SceneBean) t1;
                    return Integer.parseInt(a1.getMid()) - Integer.parseInt(a2.getMid());
                }
            });

            mAdapter.refreshLists(mlist);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void showOfflineMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.current_gateway)).append(" ").append(getString(R.string.off_line));
        Toast.makeText(getContext(), sb.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void switchmode(int position) {
        if (ConnectionPojo.getInstance().deviceTid == null) {
            showOfflineMessage();
            return;
        }

        DeviceDAO DDO = new DeviceDAO(getContext());
        MyDeviceBean gateway = DDO.findByDeviceid(ConnectionPojo.getInstance().deviceTid);
        if (gateway != null && gateway.isOnline()) {
            //情景同步时禁止控制
            //  if(!ControllerSyncScene.getInstance().sync_server){
            SendCommand.Command = SendCommand.CHOOSE_SCENE_GROUP;
            setHandleSceneGroupSid(Integer.valueOf(slist.get(position).getSid()));
            ssgd.sceneGroupChose(getHandleSceneGroupSid());
            //  }
        } else {
            showOfflineMessage();
        }
    }

    @Override
    public void edit(int position) {
        if (ConnectionPojo.getInstance().deviceTid == null) {
            showOfflineMessage();
            return;
        }

        DeviceDAO DDO = new DeviceDAO(getContext());
        MyDeviceBean gateway = DDO.findByDeviceid(ConnectionPojo.getInstance().deviceTid);
        if (gateway != null && gateway.isOnline()) {
            Intent intent = new Intent(SceneFragment.this.getActivity(),SysDetaiActivity.class);
            intent.putExtra("sid",String.valueOf(slist.get(position).getSid()));
            SceneFragment.this.getActivity().startActivity(intent);
        } else {
            showOfflineMessage();
        }
    }

    @Override
    public void longclick(final int position) {
        if(position<=2){
            Toast.makeText(SceneFragment.this.getActivity(),getResources().getString(R.string.system_mode_not_allow_delete),Toast.LENGTH_LONG).show();
        }else{
           final SysModelBean smb = slist.get(position);
            if("Y".equals(smb.getChice())){
                Toast.makeText(SceneFragment.this.getActivity(),getResources().getString(R.string.current_mode_not_allow_delete),Toast.LENGTH_LONG).show();
            }else{
               SysModelBean s =  slist.get(position);
                String ds = String.format(getResources().getString(R.string.want_to_delete_confirm_eq),s.getModleName());
                ECAlertDialog elc = ECAlertDialog.buildAlert(SceneFragment.this.getActivity(),ds, getResources().getString(R.string.cancel), getResources().getString(R.string.ok), null, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mTouchNumber = position;
                        SendCommand.Command = SendCommand.SCENE_GROUP_DELETE;
                        setHandleSceneGroupSid(Integer.valueOf(slist.get(position).getSid()));
                        ssgd.deleteSceneGroup(smb.getSid());
                    }
                });
                elc.show();
            }

        }
    }

    @Override
    public void delete(final int position) {
        if(Integer.parseInt( mlist.get(position).getMid())>128){

            ECAlertDialog elc = ECAlertDialog.buildPositiveAlert(SceneFragment.this.getActivity(),R.string.default_scene_cannot_be_deleted, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            elc.show();
        }else {
            String ds = String.format(getResources().getString(R.string.want_to_delete_confirm_eq),mlist.get(position).getName());
            ECAlertDialog elc = ECAlertDialog.buildAlert(SceneFragment.this.getActivity(),ds, getResources().getString(R.string.cancel), getResources().getString(R.string.ok), null, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    final SceneBean device = mlist.get(position);
                    mTouchNumber = position;
                    deleteSence(device);
                }
            });
            elc.show();
        }

    }

    @Override
    public void editdone(final int position) {
        final SceneBean ac =  mAdapter.getItem(position);
        if("129".equals(ac.getMid())){
            ECAlertDialog elc = ECAlertDialog.buildPositiveAlert(SceneFragment.this.getActivity(),R.string.pir_default_scene_hint, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            elc.show();
        }else if("130".equals(ac.getMid())){
            ECAlertDialog elc = ECAlertDialog.buildPositiveAlert(SceneFragment.this.getActivity(),R.string.door_default_scene_hint, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            elc.show();
        }else if("131".equals(ac.getMid())){
            ECAlertDialog elc = ECAlertDialog.buildPositiveAlert(SceneFragment.this.getActivity(),R.string.old_man_default_scene_hint, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            elc.show();
        }else {
            Intent intent = new Intent(SceneFragment.this.getActivity(),NewGroup2Activity.class);
            ModelConditionPojo mcp = ModelConditionPojo.getInstance();
            mcp.modify = true;
            mcp.name = mAdapter.getItem(position).getName();
            mcp.sb = ac;
            startActivity(intent);
        }

    }

    @Override
    public void done(int position) {
        final SceneBean device = mlist.get(position);
        doneSence(device);
    }

    @Subscribe          //订阅事件Event
    public  void onEventMainThread(STEvent event){
          if(event.getEvent()== SendCommand.SCENE_GROUP_DELETE){
              SD.delete(String.valueOf(getHandleSceneGroupSid()), ConnectionPojo.getInstance().deviceTid);
              SED.deleteBySid(getHandleSceneGroupSid(), ConnectionPojo.getInstance().deviceTid);
              shortcutDAO.deleteAllShortcurt(String.valueOf(getHandleSceneGroupSid()), ConnectionPojo.getInstance().deviceTid);

              refresh();
              SendCommand.clearCommnad();
          }else if(event.getEvent()== SendCommand.DELETE_SCENE){
              SceneBean ac = mlist.get(mTouchNumber);
              SED.delete(ac);
              DataFromSceneGroup dfsg = new DataFromSceneGroup(SceneFragment.this.getActivity());
              dfsg.doSendSynCode();

              refresh();
              SendCommand.clearCommnad();
          }else if(event.getEvent()== SendCommand.SCENE_HANDLE){
              Toast.makeText(SceneFragment.this.getActivity(),getResources().getString(R.string.operation_success),Toast.LENGTH_SHORT).show();
              SendCommand.clearCommnad();
          }


          if(event.getRefreshevent() == SendCommand.REPLACE_EQUIPMENT){
             if(refreshableView != null)
                 refreshableView.finishRefreshing();
          }else if(event.getRefreshevent() == SendCommand.CHOOSE_SCENE_GROUP){
              if(refreshableView != null)
                  refreshableView.finishRefreshing();
          }

    }



}
