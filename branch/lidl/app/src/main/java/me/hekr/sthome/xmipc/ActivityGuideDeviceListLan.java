package me.hekr.sthome.xmipc;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.lib.FunSDK;
import com.lib.funsdk.support.FunDevicePassword;
import com.lib.funsdk.support.FunError;
import com.lib.funsdk.support.FunSupport;
import com.lib.funsdk.support.OnFunDeviceListener;
import com.lib.funsdk.support.OnFunDeviceOptListener;
import com.lib.funsdk.support.models.FunDevice;
import com.lib.funsdk.support.models.FunLoginType;
import com.lib.sdk.struct.H264_DVR_FILE_DATA;

import java.util.ArrayList;
import java.util.List;


import me.hekr.sthome.R;
import me.hekr.sthome.common.CCPAppManager;
import me.hekr.sthome.common.TopbarIpcSuperActivity;
import me.hekr.sthome.commonBaseView.ECAlertDialog;
import me.hekr.sthome.http.HekrCodeUtil;
import me.hekr.sthome.http.HekrUser;
import me.hekr.sthome.http.HekrUserAction;
import me.hekr.sthome.main.MainActivity;
import me.hekr.sthome.model.modelbean.ClientUser;
import me.hekr.sthome.model.modelbean.MonitorBean;
import me.hekr.sthome.tools.UnitTools;


/**
 *  搜索显示同一个局域网内的设备列表
 * 1. 切换访问模式为本地访问  - FunSDK.SysInitLocal()
 * 2. 注册设备列表更新监听  - FunSupport.registerOnFunDeviceListener()
 * 3. 搜索局域网内的设备 - FunSDK.DevSearchDevice()
 * 4. 搜索结果通过监听返回  - onLanDeviceListChanged()/标识设备列表变化了,界面可以刷新了
 * 5. 退出并注销监听
 */
public class ActivityGuideDeviceListLan extends TopbarIpcSuperActivity implements OnClickListener, OnFunDeviceListener,OnFunDeviceOptListener {


	private final static String TAG  = ActivityGuideDeviceListLan.class.getName();
	private ListView mListView = null;
	private LocalNetIPCAdapter mAdapter = null;
	private List<FunDevice> mLanDeviceList = new ArrayList<FunDevice>();
	private View empty_view;
	private ECAlertDialog ecAlertDialog;
	private final int MESSAGE_REFRESH_DEVICE_STATUS = 0x100;
	private FunDevice funDevice;
	private List<MonitorBean> list;
	private com.alibaba.fastjson.JSONObject object;
	


	@Override
	protected void onCreateInit() {
		list = new ArrayList<MonitorBean>();
		empty_view = findViewById(R.id.empty);
		textView_title.setText(R.string.ipc_in_localnet);
		textView_back.setOnClickListener(this);
		textView_setting.setVisibility(View.GONE);
        imageButton_setting.setVisibility(View.VISIBLE);
		imageButton_setting.setOnClickListener(this);
		imageButton_setting.setBackgroundResource(R.drawable.edit);
		mListView = (ListView)findViewById(R.id.localnetlist);
		mListView.setEmptyView(empty_view);
		mAdapter = new LocalNetIPCAdapter(this, mLanDeviceList);
		// 局域网内设备不允许删除和重命名,不需要再连接AP
		mListView.setAdapter(mAdapter);

		// 刷新设备列表
		refreshLanDeviceList();

		// Demo，如果是进入设备列表就切换到本地模式,退出时切换回NET模式
		FunSupport.getInstance().setLoginType(FunLoginType.LOGIN_BY_LOCAL);

		// 监听设备类事件
		FunSupport.getInstance().registerOnFunDeviceListener(this);
		// 注册设备操作回调
		FunSupport.getInstance().registerOnFunDeviceOptListener(this);
		// 打开之后进行一次搜索
		requestToGetLanDeviceList();
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
					showWaitDialog();
					funDevice = mLanDeviceList.get(i);
					if (null != funDevice) {
						//Save the password to local file
						if(!funDevice.hasLogin() || !funDevice.hasConnected()) {
							// 重新登录
							FunSupport.getInstance().requestDeviceLogin(funDevice);
						}else {
							adddevice(funDevice.getDevSn(),funDevice.getDevName());
						}
					}
			}
		});
	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_localnet_ipc_list;
	}


	@Override
	protected void onDestroy() {
		
		// 注销设备事件监听
		FunSupport.getInstance().removeOnFunDeviceListener(this);

		FunSupport.getInstance().removeOnFunDeviceOptListener(this);
		// 切换回网络访问
		//FunSupport.getInstance().setLoginType(FunLoginType.LOGIN_BY_INTENTT);
		
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.backBtnInTopLayout:
			{
				// 返回/退出
				finish();
			}
			break;
		case R.id.SettingBtnInTopLayout:
			{
				// 刷新设备列表
				requestToGetLanDeviceList();
			}
			break;
		}
	}
	

	@Override
	protected void onPause() {
		super.onPause();
	}


	private void requestToGetLanDeviceList() {
		if ( !FunSupport.getInstance().requestLanDeviceList() ) {
			showToast(R.string.guide_message_error_call);
		} else {
			showWaitDialog();
		}
	}
	
	private void refreshLanDeviceList() {
		hideWaitDialog();
		
		mLanDeviceList.clear();
		
		mLanDeviceList.addAll(FunSupport.getInstance().getLanDeviceList());
		
		mAdapter.notifyDataSetChanged();
		
		// 延时100毫秒更新设备消息
		mHandler.removeMessages(MESSAGE_REFRESH_DEVICE_STATUS);
		if ( mLanDeviceList.size() > 0 ) {
			mHandler.sendEmptyMessageDelayed(MESSAGE_REFRESH_DEVICE_STATUS, 100);
		}
	}
	

	/**
	 * 以下函数实现来自OnFunDeviceListener()，监听设备列表变化
	 */
	@Override
	public void onDeviceListChanged() {
	}

	@Override
	public void onDeviceStatusChanged(final FunDevice funDevice) {
		if ( null != mAdapter ) {
			mAdapter.notifyDataSetChanged();
		}
	}
	
	@Override
	public void onDeviceAddedSuccess() {
		
	}


	@Override
	public void onDeviceAddedFailed(Integer errCode) {
		
	}


	@Override
	public void onDeviceRemovedSuccess() {
		
	}


	@Override
	public void onDeviceRemovedFailed(Integer errCode) {
		
	}
	
	@Override
	public void onAPDeviceListChanged() {
		
	}
	
	@Override
	public void onLanDeviceListChanged() {
		refreshLanDeviceList();
	}





	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case MESSAGE_REFRESH_DEVICE_STATUS:
				{
					FunSupport.getInstance().requestAllLanDeviceStatus();
				}
				break;
			}
		}
		
	};



	@Override
	public void onDeviceLoginSuccess(FunDevice funDevice) {
		hideSoftKeyboard();
		if ( null != funDevice ) {

			List<MonitorBean> list = CCPAppManager.getClientUser().getMonitorList();

			if(list.size()>=15){
				showToast(getResources().getString(R.string.monitor_so_many));
				hideWaitDialog();
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
					hideWaitDialog();
					Intent intent = new Intent(this, MainActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				}else{
					adddevice(funDevice.getDevSn(),funDevice.getDevName());
				}
			}
		}
	}

	@Override
	public void onDeviceLoginFailed(FunDevice funDevice, Integer errCode) {
		// 如果账号密码不正确,那么需要提示用户,输入密码重新登录
		hideWaitDialog();
		if (errCode == FunError.EE_DVR_PASSWORD_NOT_VALID) {
			showInputPasswordDialog();
		}
	}

	@Override
	public void onDeviceGetConfigSuccess(FunDevice funDevice, String configName, int nSeq) {

	}

	@Override
	public void onDeviceGetConfigFailed(FunDevice funDevice, Integer errCode) {

	}

	@Override
	public void onDeviceSetConfigSuccess(FunDevice funDevice, String configName) {

	}

	@Override
	public void onDeviceSetConfigFailed(FunDevice funDevice, String configName, Integer errCode) {

	}

	@Override
	public void onDeviceChangeInfoSuccess(FunDevice funDevice) {

	}

	@Override
	public void onDeviceChangeInfoFailed(FunDevice funDevice, Integer errCode) {

	}

	@Override
	public void onDeviceOptionSuccess(FunDevice funDevice, String option) {

	}

	@Override
	public void onDeviceOptionFailed(FunDevice funDevice, String option, Integer errCode) {

	}

	@Override
	public void onDeviceFileListChanged(FunDevice funDevice) {

	}

	@Override
	public void onDeviceFileListChanged(FunDevice funDevice, H264_DVR_FILE_DATA[] datas) {

	}

	@Override
	public void onDeviceFileListGetFailed(FunDevice funDevice) {

	}


	/**
	 * 显示输入设备密码对话框
	 */
	private void showInputPasswordDialog() {

		ecAlertDialog = ECAlertDialog.buildAlert(this, getResources().getString(R.string.device_login_input_password),getResources().getString(R.string.cancel),getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		}, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				EditText text = (EditText) ecAlertDialog.getContent().findViewById(R.id.tet);
				String newname = text.getText().toString().trim();
				onDeviceSaveNativePws(newname);
				if (null != funDevice) {
					// 重新登录
					FunSupport.getInstance().requestDeviceLogin(funDevice);
				}

			}
		});
		ecAlertDialog.setContentView(R.layout.edit_alert);
		ecAlertDialog.setTitle(getResources().getString(R.string.device_login_input_password));
		ecAlertDialog.show();


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

		HekrUserAction.getInstance(this).setProfile(object, new HekrUser.SetProfileListener() {
			@Override
			public void setProfileSuccess() {
				Log.i(TAG,"修改后的数据："+list.toString());
				ClientUser clientUser = CCPAppManager.getClientUser();
				clientUser.setMonitor(list.toString());
				CCPAppManager.setClientUser(clientUser);
				Toast.makeText(ActivityGuideDeviceListLan.this,devname+getResources().getString(R.string.add_success),Toast.LENGTH_LONG).show();
				hideWaitDialog();
				Intent intent = new Intent(ActivityGuideDeviceListLan.this, MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}

			@Override
			public void setProfileFail(int errorCode) {
				hideWaitDialog();
				Toast.makeText(ActivityGuideDeviceListLan.this, UnitTools.errorCode2Msg(ActivityGuideDeviceListLan.this,errorCode),Toast.LENGTH_LONG).show();
			}
		});

	}


	public void onDeviceSaveNativePws(String NativeLoginPsw) {
		FunDevicePassword.getInstance().saveDevicePassword(funDevice.getDevSn(),
				NativeLoginPsw);
		// 库函数方式本地保存密码
		if (FunSupport.getInstance().getSaveNativePassword()) {
			FunSDK.DevSetLocalPwd(funDevice.getDevSn(), "admin", NativeLoginPsw);
			// 如果设置了使用本地保存密码，则将密码保存到本地文件
		}
	}

}
