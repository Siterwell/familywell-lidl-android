package me.hekr.sthome.xmipc;


import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


import me.hekr.sthome.R;
import me.hekr.sthome.common.CCPAppManager;
import me.hekr.sthome.common.SoftInputUtils;
import me.hekr.sthome.common.TopbarSuperActivity;
import me.hekr.sthome.commonBaseView.ECAlertDialog;
import me.hekr.sthome.commonBaseView.SlideListView;
import me.hekr.sthome.model.modeladapter.MonitorAdapter;
import me.hekr.sthome.model.modelbean.ClientUser;
import me.hekr.sthome.model.modelbean.MonitorBean;
import me.hekr.sthome.http.HekrUser;
import me.hekr.sthome.http.HekrUserAction;
import me.hekr.sthome.tools.LOG;
import me.hekr.sthome.tools.UnitTools;

public class ActivityGuideDeviceWifiManage extends TopbarSuperActivity implements OnClickListener,MonitorAdapter.DoneWithItemListener {

	private final String TAG = "WifiManage";
	private SlideListView slideListView;
	private MonitorAdapter monitorAdapter;
	private List<MonitorBean> lists;
	private HekrUserAction hekrUserAction;
	private com.alibaba.fastjson.JSONObject object;
	private ECAlertDialog alertDialog;
	private List<MonitorBean> list;



	@Override
	protected void onCreateInit() {


		initView();
	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_monitor_manage;
	}


	@Override
	public void onClick(View v) {

	}

	private void initView(){
		getTopBarView().setTopBarStatus(1, 1, getResources().getString(R.string.monitor_manage), null, new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		},null);

		slideListView = (SlideListView) findViewById(R.id.monitors);
		slideListView.initSlideMode(SlideListView.MOD_RIGHT);
		initData();
		monitorAdapter = new MonitorAdapter(this, lists,slideListView,this);
		slideListView.setAdapter(monitorAdapter);
	}

	private void initData(){
		hekrUserAction = HekrUserAction.getInstance(this);
		ClientUser clientUser = CCPAppManager.getClientUser();
		LOG.I(TAG,clientUser.toString());
		lists = new ArrayList<MonitorBean>();
		lists = CCPAppManager.getClientUser().getMonitorList();
		LOG.I(TAG, CCPAppManager.getClientUser().getMonitorList().toString());
//		List<MonitorBean> jsonArray = new ArrayList<>();
//		for(int i=0;i<5;i++){
//			MonitorBean bean = new MonitorBean();
//			bean.setDevid(String.valueOf(i+1));
//			bean.setName("我的摄像头"+(i+1));
//			jsonArray.add(bean);
//		}
//		LOG.I(TAG,jsonArray.toString());
//		com.alibaba.fastjson.JSONObject object = new com.alibaba.fastjson.JSONObject();
//		com.alibaba.fastjson.JSONObject object2 = new com.alibaba.fastjson.JSONObject();
//		object2.put("monitor",jsonArray.toString());
//		object.put("extraProperties",object2);
//
//		hekrUserAction.setProfile(object, new HekrUser.SetProfileListener() {
//			@Override
//			public void setProfileSuccess() {
//				Toast.makeText(ActivityGuideDeviceWifiManage.this, "设置成功",Toast.LENGTH_LONG).show();
//
//			}
//
//			@Override
//			public void setProfileFail(int errorCode) {
//				Toast.makeText(ActivityGuideDeviceWifiManage.this, HekrCodeUtil.errorCode2Msg(errorCode),Toast.LENGTH_LONG).show();
//			}
//		});


	}


	@Override
	public void delete(int position) {
		 list =  new ArrayList<>();
		   list =  lists;

		  if(list!=null && list.size()>position){
			  list.remove(position);
			  LOG.I(TAG,list.toString());
			  object = new com.alibaba.fastjson.JSONObject();
			  com.alibaba.fastjson.JSONObject object2 = new com.alibaba.fastjson.JSONObject();
			  object2.put("monitor",list.toString());
			  object.put("extraProperties",object2);

			  hekrUserAction.setProfile(object, new HekrUser.SetProfileListener() {
				  @Override
				  public void setProfileSuccess() {
					  LOG.I(TAG,"修改后的数据："+list.toString());
					  ClientUser clientUser = CCPAppManager.getClientUser();
					  clientUser.setMonitor(list.toString());
					  CCPAppManager.setClientUser(clientUser);
					  lists = CCPAppManager.getClientUser().getMonitorList();
					  monitorAdapter.refreshLists(lists);
					  slideListView.scrollBack();
				  }

				  @Override
				  public void setProfileFail(int errorCode) {
                      Toast.makeText(ActivityGuideDeviceWifiManage.this, UnitTools.errorCode2Msg(ActivityGuideDeviceWifiManage.this,errorCode),Toast.LENGTH_LONG).show();
				  }
			  });
		  }
	}

	@Override
	public void done(final int position) {

		alertDialog = ECAlertDialog.buildAlert(ActivityGuideDeviceWifiManage.this, getResources().getString(R.string.update_name), getResources().getString(R.string.cancel), getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
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

					if(newname.length()<=10){
						alertDialog.setDismissFalse(true);

						list = lists;
						for(int i=0;i<list.size();i++){
							if(i==position){
								list.get(position).setName(newname);
								object = new com.alibaba.fastjson.JSONObject();
								com.alibaba.fastjson.JSONObject object2 = new com.alibaba.fastjson.JSONObject();
								object2.put("monitor",list.toString());
								object.put("extraProperties",object2);

								hekrUserAction.setProfile(object, new HekrUser.SetProfileListener() {
									@Override
									public void setProfileSuccess() {
										Toast.makeText(ActivityGuideDeviceWifiManage.this, getResources().getString(R.string.success),Toast.LENGTH_LONG).show();
										ClientUser clientUser = CCPAppManager.getClientUser();
										clientUser.setMonitor(list.toString());
										CCPAppManager.setClientUser(clientUser);
										lists = CCPAppManager.getClientUser().getMonitorList();
										monitorAdapter.refreshLists(lists);
										slideListView.scrollBack();
									}

									@Override
									public void setProfileFail(int errorCode) {
										Toast.makeText(ActivityGuideDeviceWifiManage.this, UnitTools.errorCode2Msg(ActivityGuideDeviceWifiManage.this,errorCode),Toast.LENGTH_LONG).show();
									}
								});
								break;

							}
						}

					}else{
						alertDialog.setDismissFalse(false);
						Toast.makeText(ActivityGuideDeviceWifiManage.this,getResources().getString(R.string.name_is_too_long),Toast.LENGTH_SHORT).show();
					}


				}
				else{
					alertDialog.setDismissFalse(false);
					Toast.makeText(ActivityGuideDeviceWifiManage.this,getResources().getString(R.string.name_is_null),Toast.LENGTH_SHORT).show();
				}

			}
		});
		alertDialog.setContentView(R.layout.edit_alert);
		alertDialog.setTitle( getResources().getString(R.string.update_name));
		EditText text = (EditText) alertDialog.getContent().findViewById(R.id.tet);
			text.setText(lists.get(position).getName());
			text.setSelection(lists.get(position).getName().length());
		alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				SoftInputUtils.hideSoftInput(ActivityGuideDeviceWifiManage.this);
			}
		});


		alertDialog.show();
	}


	private void adddevice(){
		list = lists;
		 MonitorBean monitorBean = new MonitorBean();

		 monitorBean.setDevid(String.valueOf(CCPAppManager.getClientUser().getMonitorList().size()+1));
         monitorBean.setName("我的摄像头"+String.valueOf(CCPAppManager.getClientUser().getMonitorList().size()+1));
		list.add(monitorBean);

		object = new com.alibaba.fastjson.JSONObject();
		com.alibaba.fastjson.JSONObject object2 = new com.alibaba.fastjson.JSONObject();
		object2.put("monitor",list.toString());
		object.put("extraProperties",object2);

		hekrUserAction.setProfile(object, new HekrUser.SetProfileListener() {
			@Override
			public void setProfileSuccess() {
				Toast.makeText(ActivityGuideDeviceWifiManage.this, "添加成功",Toast.LENGTH_LONG).show();
				LOG.I(TAG,"修改后的数据："+list.toString());
				ClientUser clientUser = CCPAppManager.getClientUser();
				clientUser.setMonitor(list.toString());
				CCPAppManager.setClientUser(clientUser);
				lists = CCPAppManager.getClientUser().getMonitorList();
				monitorAdapter.refreshLists(lists);
				slideListView.scrollBack();
			}

			@Override
			public void setProfileFail(int errorCode) {
				Toast.makeText(ActivityGuideDeviceWifiManage.this, UnitTools.errorCode2Msg(ActivityGuideDeviceWifiManage.this,errorCode),Toast.LENGTH_LONG).show();
			}
		});

	}
}
