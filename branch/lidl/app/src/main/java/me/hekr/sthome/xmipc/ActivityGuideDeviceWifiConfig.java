package me.hekr.sthome.xmipc;


import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lib.funsdk.support.FunSupport;
import com.lib.funsdk.support.OnFunDeviceWiFiConfigListener;
import com.lib.funsdk.support.models.FunDevice;
import com.zbar.lib.CaptureActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.hekr.sthome.R;
import me.hekr.sthome.common.CCPAppManager;
import me.hekr.sthome.common.TopbarSuperActivity;
import me.hekr.sthome.http.HekrCodeUtil;
import me.hekr.sthome.http.HekrUser;
import me.hekr.sthome.http.HekrUserAction;
import me.hekr.sthome.main.MainActivity;
import me.hekr.sthome.model.modelbean.ClientUser;
import me.hekr.sthome.model.modelbean.MonitorBean;


public class ActivityGuideDeviceWifiConfig extends TopbarSuperActivity implements OnClickListener, OnFunDeviceWiFiConfigListener {


	private final String TAG = "WifiConfig";
	private EditText mEditSN = null;
	private Button mBtnSNAdd = null;
	private Button mBtnScanCode = null;
	private List<MonitorBean> list;
	private HekrUserAction hekrUserAction;
	private com.alibaba.fastjson.JSONObject object;

	private Toast mToast = null;

	@Override
	protected void onCreateInit() {
		list = new ArrayList<MonitorBean>();
		hekrUserAction = HekrUserAction.getInstance(this);
		mEditSN = (EditText)findViewById(R.id.editSN);

		mBtnSNAdd = (Button)findViewById(R.id.btnWifiQuickAdd);
		mBtnScanCode = (Button)findViewById(R.id.btnScanCode);
		mBtnSNAdd.setOnClickListener(this);
		mBtnScanCode.setOnClickListener(this);

		FunSupport.getInstance().registerOnFunDeviceWiFiConfigListener(this);
		getTopBarView().setTopBarStatus(1, 1, getResources().getString(R.string.wifi_config), null, new OnClickListener() {
			@Override
			public void onClick(View v) {
             hideSoftKeyboard();
				finish();
			}
		},null);

	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_device_set_wifi;
	}


	@Override
	protected void onDestroy() {

		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.btnWifiQuickAdd:
			{
				showProgressDialog(getResources().getString(R.string.wait));
				startSNaddResult();
			}
			break;
			case R.id.btnScanCode:
			{
				 startActivityForResult(new Intent(this, CaptureActivity.class),1);
			}
			break;
		}
	}

	/*
	@method startSNaddResult
	@autor Administrator
	@time 2017/10/11 14:36
	@email xuejunju_4595@qq.com
	*/
	private void startSNaddResult(){
		String code = mEditSN.getText().toString().trim();
		if(TextUtils.isEmpty(code)){
			hideProgressDialog();
			showToast(getResources().getString(R.string.camera_empety_sn));
			return;
		}

		List<MonitorBean> list = CCPAppManager.getClientUser().getMonitorList();
		if(list.size()>=15){
			hideProgressDialog();
			showToast(getResources().getString(R.string.monitor_so_many));
			return;
		}else {

			boolean flag = false;
			for(MonitorBean monitorBean:list){
				if(!TextUtils.isEmpty(monitorBean.getDevid())&&monitorBean.getDevid().equals(code)){
					flag = true;
					break;
				}
			}
			if(flag){
				showToast(getResources().getString(R.string.camera_already_added));
				hideSoftKeyboard();
				Intent intent = new Intent(this, MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}else{
				adddevice(code,code);
			}

		}




	}

	@Override
	public void onDeviceWiFiConfigSetted(FunDevice funDevice) {


		if ( null != funDevice ) {

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
					Intent intent = new Intent(this, MainActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				}else{
                    adddevice(funDevice.getDevSn(),funDevice.getDevName());
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
				Toast.makeText(ActivityGuideDeviceWifiConfig.this,devname+getResources().getString(R.string.add_success),Toast.LENGTH_LONG).show();
				hideProgressDialog();
				hideSoftKeyboard();
				Intent intent = new Intent(ActivityGuideDeviceWifiConfig.this, MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}

			@Override
			public void setProfileFail(int errorCode) {
				hideProgressDialog();
				Toast.makeText(ActivityGuideDeviceWifiConfig.this, HekrCodeUtil.errorCode2Msg(errorCode),Toast.LENGTH_LONG).show();
			}
		});

	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode!=RESULT_OK) return;

		if(requestCode==1){
			Pattern pattern = Pattern.compile("^[A-Za-z0-9]{16}$");

			String sn = data.getExtras().getString("SN");
			Matcher m = pattern.matcher(sn);
			if( !m.matches() ){ //匹配不到,說明輸入的不符合條件
              showToast(R.string.sn_is_illegal);
			}else{
				if(!TextUtils.isEmpty(sn))
					mEditSN.setText(sn);
			}


		}
	}
}
