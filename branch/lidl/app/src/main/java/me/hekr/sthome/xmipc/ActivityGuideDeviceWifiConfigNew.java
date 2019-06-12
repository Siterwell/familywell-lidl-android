package me.hekr.sthome.xmipc;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lib.funsdk.support.FunSupport;
import com.lib.funsdk.support.FunWifiPassword;
import com.lib.funsdk.support.OnFunDeviceWiFiConfigListener;
import com.lib.funsdk.support.models.FunDevice;
import com.lib.funsdk.support.utils.DeviceWifiManager;
import com.lib.funsdk.support.utils.MyUtils;
import com.lib.funsdk.support.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;


import me.hekr.sthome.R;
import me.hekr.sthome.common.CCPAppManager;
import me.hekr.sthome.common.TopbarSuperActivity;
import me.hekr.sthome.http.HekrUser;
import me.hekr.sthome.http.HekrUserAction;
import me.hekr.sthome.main.MainActivity;
import me.hekr.sthome.model.modelbean.ClientUser;
import me.hekr.sthome.model.modelbean.MonitorBean;
import me.hekr.sthome.tools.UnitTools;


public class ActivityGuideDeviceWifiConfigNew extends TopbarSuperActivity implements OnClickListener, OnFunDeviceWiFiConfigListener {


	private final String TAG = "WifiConfig";
	private EditText mEditWifiSSID = null;
	private EditText mEditWifiPasswd = null;
	private Button mBtnSetting = null;
	private List<MonitorBean> list;
	private HekrUserAction hekrUserAction;
	private com.alibaba.fastjson.JSONObject object;

	private Toast mToast = null;
	private boolean wireless = false;

	@Override
	protected void onCreateInit() {
		wireless = getIntent().getBooleanExtra("wireless",false);
		list = new ArrayList<MonitorBean>();
		hekrUserAction = HekrUserAction.getInstance(this);
		mEditWifiSSID = (EditText)findViewById(R.id.editWifiSSID);
		mEditWifiPasswd = (EditText)findViewById(R.id.editWifiPasswd);

		mBtnSetting = (Button)findViewById(R.id.btnWifiQuickSetting);
		mBtnSetting.setOnClickListener(this);

		String currSSID = getConnectWifiSSID();
		mEditWifiSSID.setText(currSSID);
		mEditWifiPasswd.setText(FunWifiPassword.getInstance().getPassword(currSSID));
		FunSupport.getInstance().registerOnFunDeviceWiFiConfigListener(this);
		String title = wireless?getResources().getString(R.string.wireless_config):getResources().getString(R.string.wifi_config);
		getTopBarView().setTopBarStatus(1, 1, title, null, new OnClickListener() {
			@Override
			public void onClick(View v) {
             hideSoftKeyboard();
				finish();
			}
		},null);

	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_device_set_wifi_new;
	}


	@Override
	protected void onDestroy() {
		
		stopQuickSetting();
		
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.btnWifiQuickSetting:
			{
				startQuickSetting();
			}
			break;
		}
	}

	// 开始快速配置
	private void startQuickSetting() {
		
		try {
			WifiManager wifiManage = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
			WifiInfo wifiInfo = wifiManage.getConnectionInfo();
			DhcpInfo wifiDhcp = wifiManage.getDhcpInfo();

			if (  null == wifiInfo ) {
				showToast(R.string.device_opt_set_wifi_info_error);
				return;
			}
			
			String ssid = wifiInfo.getSSID().replace("\"", "");
			ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = cm.getActiveNetworkInfo();
			if(ssid.contains("unknow")){

				ssid= networkInfo.getExtraInfo().replace("\"","");
			}
			if ( StringUtils.isStringNULL(ssid) ) {
				showToast(R.string.device_opt_set_wifi_info_error);
				return;
			}

			String wifiPwd = mEditWifiPasswd.getText().toString().trim();
			if (  StringUtils.isStringNULL(wifiPwd) ) {
				// 需要密码
				showToast(R.string.device_opt_set_wifi_info_error);
				return;
			}
			int pwdType = networkInfo.getType();
			StringBuffer data = new StringBuffer();
			data.append("S:").append(ssid).append("P:").append(wifiPwd).append("T:").append(pwdType);
			
			String submask;
			if (wifiDhcp.netmask == 0) {
				submask = "255.255.255.0";
			} else {
				submask = MyUtils.formatIpAddress(wifiDhcp.netmask);
			}
			
			String mac = wifiInfo.getMacAddress();
			StringBuffer info = new StringBuffer();
			info.append("gateway:").append(MyUtils.formatIpAddress(wifiDhcp.gateway)).append(" ip:")
					.append(MyUtils.formatIpAddress(wifiDhcp.ipAddress)).append(" submask:").append(submask)
					.append(" dns1:").append(MyUtils.formatIpAddress(wifiDhcp.dns1)).append(" dns2:")
					.append(MyUtils.formatIpAddress(wifiDhcp.dns2)).append(" mac:").append(mac)
					.append(" ");
			
			showProgressDialog(getResources().getString(R.string.wait));
			
			FunSupport.getInstance().startWiFiQuickConfig(ssid,
					data.toString(), info.toString(), 
					MyUtils.formatIpAddress(wifiDhcp.gateway),
					pwdType, 0, mac, -1);
			
			FunWifiPassword.getInstance().saveWifiPassword(ssid, wifiPwd);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void stopQuickSetting() {
		FunSupport.getInstance().stopWiFiQuickConfig();
	}

	private String getConnectWifiSSID() {
		try {
			WifiManager wifimanage=(WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);

			String ssidinit = wifimanage.getConnectionInfo().getSSID().replace("\"", "");
			if(ssidinit.contains("ssid")){
				ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo networkInfo = cm.getActiveNetworkInfo();
				ssidinit = networkInfo.getExtraInfo();
			}

			return ssidinit.replace("\"","");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}


	@Override
	public void onDeviceWiFiConfigSetted(FunDevice funDevice) {


		if ( null != funDevice ) {

			if(wireless){
				showToast(getResources().getString(R.string.success_set));
				finish();
			}else {
				List<MonitorBean> list = CCPAppManager.getClientUser().getMonitorList();

				if(list.size()>=15){
					showToast(getResources().getString(R.string.monitor_so_many));
					hideProgressDialog();
				}else{
					boolean flag = false;
					for(MonitorBean monitorBean:list){
						if(!TextUtils.isEmpty(monitorBean.getDevid())&&monitorBean.getDevid().equals(funDevice.getDevSn())){
							flag = true;
							break;
						}
					}
					if(flag){
						showToast(getResources().getString(R.string.already_monitor_add));
						hideProgressDialog();
						Intent intent = new Intent(ActivityGuideDeviceWifiConfigNew.this, MainActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
					}else{
						adddevice(funDevice.getDevSn(),funDevice.getDevName());
					}
				}
			}


		}
	}

	public void showToast(int resid){
		if ( resid > 0 ) {
			if ( null != mToast ) {
				mToast.cancel();
			}
			mToast = Toast.makeText(this, resid, Toast.LENGTH_SHORT);
			mToast.show();
		}
	}

	public void showToast(String text){
		if ( null != text ) {
			if ( null != mToast ) {
				mToast.cancel();
			}
			mToast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
			mToast.show();
		}
	}



	private void adddevice(String devsn,final  String devname){
		list.clear();
		list = CCPAppManager.getClientUser().getMonitorList();
		MonitorBean monitorBean = new MonitorBean();

		monitorBean.setDevid(devsn);
		monitorBean.setName(devname);
		list.add(monitorBean);

		object = new com.alibaba.fastjson.JSONObject();
		com.alibaba.fastjson.JSONObject object2 = new com.alibaba.fastjson.JSONObject();
		object2.put("monitor",list.toString());
		object.put("extraProperties",object2);

		hekrUserAction.setProfile(object, new HekrUser.SetProfileListener() {
			@Override
			public void setProfileSuccess() {
				Log.i(TAG,"修改后的数据："+list.toString());
				ClientUser clientUser = CCPAppManager.getClientUser();
				clientUser.setMonitor(list.toString());
				CCPAppManager.setClientUser(clientUser);
				Toast.makeText(ActivityGuideDeviceWifiConfigNew.this,devname+getResources().getString(R.string.add_success),Toast.LENGTH_LONG).show();
				hideProgressDialog();
				Intent intent = new Intent(ActivityGuideDeviceWifiConfigNew.this, MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}

			@Override
			public void setProfileFail(int errorCode) {
				hideProgressDialog();
				Toast.makeText(ActivityGuideDeviceWifiConfigNew.this, UnitTools.errorCode2Msg(ActivityGuideDeviceWifiConfigNew.this,errorCode),Toast.LENGTH_LONG).show();
			}
		});

	}

}
