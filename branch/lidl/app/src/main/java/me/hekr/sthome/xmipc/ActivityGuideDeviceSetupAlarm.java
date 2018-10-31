package me.hekr.sthome.xmipc;


import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;

import com.lib.DevSDK;
import com.lib.funsdk.support.FunError;
import com.lib.funsdk.support.FunSupport;
import com.lib.funsdk.support.OnFunDeviceOptListener;
import com.lib.funsdk.support.config.AlarmOut;
import com.lib.funsdk.support.config.DetectBlind;
import com.lib.funsdk.support.config.DetectMotion;
import com.lib.funsdk.support.config.LocalAlarm;
import com.lib.funsdk.support.config.PowerSocketArm;
import com.lib.funsdk.support.models.FunDevice;
import com.lib.funsdk.support.models.FunDeviceSocket;
import com.lib.funsdk.support.utils.DeviceConfigType;
import com.lib.sdk.struct.H264_DVR_FILE_DATA;

import java.util.ArrayList;
import java.util.List;

import me.hekr.sthome.R;
import me.hekr.sthome.common.TopbarIpcSuperActivity;
import me.hekr.sthome.commonBaseView.SettingIpcItem;


/**
 * 报警配置
 *
 */
public class ActivityGuideDeviceSetupAlarm extends TopbarIpcSuperActivity implements OnClickListener, OnFunDeviceOptListener,OnItemSelectedListener {




	private SettingIpcItem settingIpcItem_SwitchMotion,settingIpcItem_SwitchMotionRecord,settingIpcItem_SwitchMotionCapture,settingIpcItem_SwitchMotionPushMsg,
			settingIpcItem_SwitchBlock,settingIpcItem_SpinnerDetectionLevel;

	
	private FunDevice mFunDevice = null;

	
	/**
	 * 本界面需要获取到的设备配置信息(插座类)
	 */
	private final String[] DEV_CONFIGS_FOR_SOCKET = {
			// 插座类报警
			PowerSocketArm.CONFIG_NAME
	};
	
	/**
	 * 本界面需要获取到的设备配置信息(监控类)
	 */
	private final String[] DEV_CONFIGS_FOR_CAMERA = {
			// 移动侦测
			DetectMotion.CONFIG_NAME,
			
			// 视频遮掉
			DetectBlind.CONFIG_NAME,
			
			// 报警输入
			LocalAlarm.CONFIG_NAME,
			AlarmOut.CONFIG_NAME,
	};
	
	private final String[] DEV_CONFIGS_FOR_CHANNELS = {
			//移动侦测
			DetectMotion.CONFIG_NAME,
			
			//视频遮挡
			DetectBlind.CONFIG_NAME,
			AlarmOut.CONFIG_NAME
	};
	
	private String[] DEV_CONFIGS = null;
	
	// 设置配置信息的时候,由于有多个,通过下面的列表来判断是否所有的配置都设置完成了
	private List<String> mSettingConfigs = new ArrayList<String>();


	@Override
	protected void onCreateInit() {

		textView_title.setText(getResources().getString(R.string.device_opt_alarm_config));
		textView_back.setOnClickListener(this);
		textView_setting.setVisibility(View.VISIBLE);
		textView_setting.setOnClickListener(this);
		textView_setting.setVisibility(View.GONE);
		textView_back.setVisibility(View.GONE);
        imageButton_back.setVisibility(View.VISIBLE);
		imageButton_back.setOnClickListener(this);
        imageButton_setting.setVisibility(View.VISIBLE);
		imageButton_setting.setBackgroundResource(R.drawable.ic_common_check);
		imageButton_setting.setOnClickListener(this);
		settingIpcItem_SwitchMotion = (SettingIpcItem)findViewById(R.id.layoutVideoBlockAlarmRecord);
		settingIpcItem_SwitchMotionRecord = (SettingIpcItem)findViewById(R.id.layoutMotionDetectionAlarmRecord);
		settingIpcItem_SwitchMotionCapture = (SettingIpcItem)findViewById(R.id.layoutMotionDetectionAlarmCapture);
		settingIpcItem_SwitchMotionPushMsg = (SettingIpcItem)findViewById(R.id.layoutMotionDetectionAlarmPushMsg);
		settingIpcItem_SwitchBlock = (SettingIpcItem)findViewById(R.id.layoutVideoBlock);
		settingIpcItem_SpinnerDetectionLevel =(SettingIpcItem)findViewById(R.id.layoutMotionDetectionAlarmLevel);

		String[] alarmLevels = getResources().getStringArray(R.array.device_setup_alarm_level);
		ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, R.layout.right_spinner_item, alarmLevels);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		settingIpcItem_SpinnerDetectionLevel.getSpinner().setAdapter(adapter);
		settingIpcItem_SpinnerDetectionLevel.getSpinner().setOnItemSelectedListener(this);


		int devId = getIntent().getIntExtra("FUN_DEVICE_ID", 0);
		FunDevice funDevice = FunSupport.getInstance().findDeviceById(devId);
		if ( null == funDevice ) {
			finish();
			return;
		}

//		mBtnSave = (ImageButton)setNavagateRightButton(R.layout.imagebutton_save);
//		mBtnSave.setOnClickListener(this);

		mFunDevice = funDevice;

		if ( mFunDevice instanceof FunDeviceSocket) {
			// 插座类的设备报警
			DEV_CONFIGS = DEV_CONFIGS_FOR_SOCKET;
		} else {
			// 监控类的设备报警
			DEV_CONFIGS = DEV_CONFIGS_FOR_CHANNELS;

			//如果是单通道，设置以下配置
			if ((mFunDevice.channel != null) && (mFunDevice.channel.nChnCount == 1)) {
				DEV_CONFIGS = DEV_CONFIGS_FOR_CAMERA;
			}
		}


		// 注册设备操作监听
		FunSupport.getInstance().registerOnFunDeviceOptListener(this);

		// 获取报警配置信息
		tryGetAlarmConfig();
	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_device_setup_alarm;
	}


	@Override
	protected void onDestroy() {
		
		// 注销监听
		FunSupport.getInstance().removeOnFunDeviceOptListener(this);
		
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {

			case R.id.backBtnInTopLayout:
			case R.id.backBtnInTopLayout2:
				finish();
				break;
			case R.id.SettingBtnInTopLayoutText:
			case R.id.SettingBtnInTopLayout:
				trySaveAlarmConfig();
				break;
		}
	}
	
	/**
	 * Level报警灵敏度转换,界面低级/中级/高级(0,1,2),需要和实际级别做一个转换
	 * @param level
	 * @return
	 */
	private int changeLevelToUI(int level) {
		int uiLevel = (level == 0 ? 1 : (level % 2 + level / 2)) - 1;
		return Math.max(0, uiLevel);
	}
	
	private int changeLevelToDetect(int uiLevel) {
		return (uiLevel+1) * 2;
	}
	
	private void refreshAlarmConfig() {
		if ( mFunDevice instanceof FunDeviceSocket) {
			// 插座类
			PowerSocketArm socketArm = ((FunDeviceSocket) mFunDevice).getPowerSocketArm();
			if ( null != socketArm ) {
				settingIpcItem_SwitchMotion.setChecked(socketArm.getGuard() > 0);
			}
		} else {
			// 监控类
			// 移动侦测
			DetectMotion detectMotion = (DetectMotion)mFunDevice.getConfig(DetectMotion.CONFIG_NAME);
			if ( null != detectMotion ) {
				settingIpcItem_SwitchMotion.setChecked(detectMotion.Enable);
				settingIpcItem_SwitchMotionRecord.setChecked(detectMotion.event.RecordEnable);
				settingIpcItem_SwitchMotionCapture.setChecked(detectMotion.event.SnapEnable);
				settingIpcItem_SwitchMotionPushMsg.setChecked(detectMotion.event.MessageEnable);
				settingIpcItem_SpinnerDetectionLevel.getSpinner().setSelection(changeLevelToUI(detectMotion.Level));
			}
			
			// 视频遮挡
			DetectBlind detectBlind = (DetectBlind)mFunDevice.getConfig(DetectBlind.CONFIG_NAME);
			if ( null != detectBlind ) {
				settingIpcItem_SwitchBlock.setChecked(detectBlind.Enable);
			}

		}
	}

	
	private void tryGetAlarmConfig() {
		if ( null != mFunDevice ) {
			
			showWaitDialog();
			
			for ( String configName : DEV_CONFIGS ) {
				
				// 删除老的配置信息
				mFunDevice.invalidConfig(configName);
				
				// 重新搜索新的配置信息
				if (contains(DeviceConfigType.DeviceConfigCommon, configName)) {
					 FunSupport.getInstance().requestDeviceConfig(mFunDevice,
					 configName);
				}else if (contains(DeviceConfigType.DeviceConfigByChannel, configName)) {
					FunSupport.getInstance().requestDeviceConfig(mFunDevice, configName, mFunDevice.CurrChannel);
				}
			}
		}
	}
	
	private boolean isCurrentUsefulConfig(String configName) {
		for ( int i = 0; i < DEV_CONFIGS.length; i ++ ) {
			if ( DEV_CONFIGS[i].equals(configName) ) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 判断是否所有需要的配置都获取到了
	 * @return
	 */
	private boolean isAllConfigGetted() {
		for ( String configName : DEV_CONFIGS ) {
			if ( null == mFunDevice.getConfig(configName) ) {
				return false;
			}
		}
		return true;
	}

	private void trySaveAlarmConfig() {
		try {
			if ( mFunDevice instanceof FunDeviceSocket) {
				// 插座类
				boolean enable = settingIpcItem_SwitchMotion.isChecked();
				int nGuard = enable?1:0;

				PowerSocketArm socketArm = (PowerSocketArm)mFunDevice.checkConfig(PowerSocketArm.CONFIG_NAME);

				if ( nGuard != socketArm.getGuard() ) {
					socketArm.setGuard(enable?1:0);

					showWaitDialog();

					FunSupport.getInstance().requestDeviceSetConfig(mFunDevice, socketArm);
				} else {
					showToast(R.string.device_alarm_no_change);
				}
			} else {
				// 监控类
				boolean beMotionChanged = false;
				boolean beBlockChanged = false;

				DetectMotion detectMotion = (DetectMotion)mFunDevice.getConfig(DetectMotion.CONFIG_NAME);
				if ( null != detectMotion ) {
					if ( settingIpcItem_SwitchMotion.isChecked() != detectMotion.Enable ) {
						detectMotion.Enable = settingIpcItem_SwitchMotion.isChecked();
						beMotionChanged = true;
					}

					if ( settingIpcItem_SwitchMotionRecord.isChecked() != detectMotion.event.RecordEnable ) {
						detectMotion.event.RecordEnable = settingIpcItem_SwitchMotionRecord.isChecked();
						detectMotion.event.RecordMask = DevSDK.SetSelectHex(
								detectMotion.event.RecordMask, mFunDevice.CurrChannel,
								detectMotion.event.RecordEnable);
						beMotionChanged = true;
					}

					if ( settingIpcItem_SwitchMotionCapture.isChecked() != detectMotion.event.SnapEnable ) {
						detectMotion.event.SnapEnable = settingIpcItem_SwitchMotionCapture.isChecked();
						detectMotion.event.SnapShotMask = DevSDK.SetSelectHex(
								detectMotion.event.SnapShotMask, mFunDevice.CurrChannel,
								detectMotion.event.SnapEnable);
						beMotionChanged = true;
					}

					if ( settingIpcItem_SwitchMotionPushMsg.isChecked() != detectMotion.event.MessageEnable ) {
						detectMotion.event.MessageEnable = settingIpcItem_SwitchMotionPushMsg.isChecked();
						beMotionChanged = true;
					}

					if ( settingIpcItem_SpinnerDetectionLevel.getSpinner().getSelectedItemPosition()
							!= changeLevelToUI(detectMotion.Level) ) {
						detectMotion.Level = changeLevelToDetect(
								settingIpcItem_SpinnerDetectionLevel.getSpinner().getSelectedItemPosition());
						beMotionChanged = true;
					}
				}

				DetectBlind detectBlind = (DetectBlind)mFunDevice.getConfig(DetectBlind.CONFIG_NAME);
				if ( null != detectBlind ) {
					if ( settingIpcItem_SwitchBlock.isChecked() != detectBlind.Enable ) {
						detectBlind.Enable = settingIpcItem_SwitchBlock.isChecked();
						beBlockChanged = true;
					}

				}

				mSettingConfigs.clear();

				if ( beBlockChanged
						|| beMotionChanged) {
					showWaitDialog();

					if ( beMotionChanged ) {
						synchronized (mSettingConfigs) {
							mSettingConfigs.add(detectMotion.getConfigName());
						}

						FunSupport.getInstance().requestDeviceSetConfig(mFunDevice, detectMotion);
					}

					if ( beBlockChanged ) {

						synchronized (mSettingConfigs) {
							mSettingConfigs.add(detectBlind.getConfigName());
						}

						FunSupport.getInstance().requestDeviceSetConfig(mFunDevice, detectBlind);
					}


				} else {
					showToast(R.string.device_alarm_no_change);
				}
			}
		}catch (IndexOutOfBoundsException e){
			showToast(R.string.failed);
		}

	}
	

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int position,
							   long arg3) {
		
	}


	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onDeviceLoginSuccess(FunDevice funDevice) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onDeviceLoginFailed(FunDevice funDevice, Integer errCode) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onDeviceGetConfigSuccess(FunDevice funDevice,
										 String configName, int nSeq) {
		if ( null != mFunDevice 
				&& funDevice.getId() == mFunDevice.getId()
				&& isCurrentUsefulConfig(configName) ) {
			if ( isAllConfigGetted() ) {
				hideWaitDialog();
			}
			
			refreshAlarmConfig();
		}
	}


	@Override
	public void onDeviceGetConfigFailed(FunDevice funDevice, Integer errCode) {
		hideWaitDialog();
		showToast(FunError.getErrorStr(errCode));
	}


	@Override
	public void onDeviceSetConfigSuccess(final FunDevice funDevice,
			final String configName) {
		if ( null != mFunDevice 
				&& funDevice.getId() == mFunDevice.getId() ) {
			synchronized (mSettingConfigs) {
				if ( mSettingConfigs.contains(configName) ) {
					mSettingConfigs.remove(configName);
				}
				
				if ( mSettingConfigs.size() == 0 ) {
					// 所有的设置修改都已经完成
					hideWaitDialog();
				}
			}
			
			refreshAlarmConfig();
		}
	}


	@Override
	public void onDeviceSetConfigFailed(final FunDevice funDevice,
										final String configName, final Integer errCode) {
		showToast(FunError.getErrorStr(errCode));
	}

	@Override
	public void onDeviceChangeInfoSuccess(final FunDevice funDevice) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void onDeviceChangeInfoFailed(final FunDevice funDevice, final Integer errCode) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void onDeviceOptionSuccess(final FunDevice funDevice, final String option) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void onDeviceOptionFailed(final FunDevice funDevice, final String option, final Integer errCode) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onDeviceFileListChanged(FunDevice funDevice) {
		// TODO Auto-generated method stub
		
	}

    @Override
    public void onDeviceFileListChanged(FunDevice funDevice, H264_DVR_FILE_DATA[] datas) {

    }


	@Override
	public void onDeviceFileListGetFailed(FunDevice funDevice) {
		// TODO Auto-generated method stub
		
	}

}
