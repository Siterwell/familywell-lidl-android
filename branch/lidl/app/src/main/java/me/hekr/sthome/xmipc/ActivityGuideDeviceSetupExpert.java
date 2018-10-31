package me.hekr.sthome.xmipc;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.lib.funsdk.support.FunError;
import com.lib.funsdk.support.FunSupport;
import com.lib.funsdk.support.OnFunDeviceOptListener;
import com.lib.funsdk.support.config.CameraClearFog;
import com.lib.funsdk.support.config.CameraParam;
import com.lib.funsdk.support.models.FunDevice;
import com.lib.funsdk.support.utils.DeviceConfigType;
import com.lib.funsdk.support.utils.MyUtils;
import com.lib.sdk.struct.H264_DVR_FILE_DATA;

import java.util.ArrayList;
import java.util.List;

import me.hekr.sthome.R;
import me.hekr.sthome.common.TopbarIpcSuperActivity;
import me.hekr.sthome.commonBaseView.SettingIpcItem;


public class ActivityGuideDeviceSetupExpert extends TopbarIpcSuperActivity implements OnClickListener, OnFunDeviceOptListener, OnItemSelectedListener {




	private SettingIpcItem settingIpcItem_mSpinnerExposureTime;
	private SettingIpcItem settingIpcItem_mSpinnerSceneMode;
	private SettingIpcItem settingIpcItem_mSpinnerColorMode;
	private SettingIpcItem getSettingIpcItem_mSpinnerElectronicSlowShutter;

	private FunDevice mFunDevice = null;
	
	/**
	 * 本界面需要获取到的设备配置信息
	 */
	private final String[] DEV_CONFIGS = {

			CameraParam.CONFIG_NAME,

			//CameraParamEx.CONFIG_NAME,

			CameraClearFog.CONFIG_NAME
	};
	
	// 设置配置信息的时候,由于有多个,通过下面的列表来判断是否所有的配置都设置完成了
	private List<String> mSettingConfigs = new ArrayList<String>();


	@Override
	protected void onCreateInit() {

		textView_title.setText(getResources().getString(R.string.device_setup_other));
		textView_setting.setVisibility(View.VISIBLE);
		textView_setting.setOnClickListener(this);
		textView_back.setOnClickListener(this);

		settingIpcItem_mSpinnerExposureTime = (SettingIpcItem)findViewById(R.id.setupExposureTimeSpinner);
		settingIpcItem_mSpinnerSceneMode = (SettingIpcItem)findViewById(R.id.setupSceneModeSpinner);
		settingIpcItem_mSpinnerColorMode = (SettingIpcItem)findViewById(R.id.setupColorModeSpinner);
		getSettingIpcItem_mSpinnerElectronicSlowShutter = (SettingIpcItem)findViewById(R.id.setupElectronicSlowShutterSpinner);

		String[] exposureTime = getResources().getStringArray(R.array.device_setup_exposure_time_values);
		ArrayAdapter<String> adapterExposureTime = new ArrayAdapter<String>(this, R.layout.right_spinner_item, exposureTime);
		adapterExposureTime.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		settingIpcItem_mSpinnerExposureTime.getSpinner().setAdapter(adapterExposureTime);
		Integer[] defValues1 = { 0, 1, 2, 3, 4, 5, 6, 7, 8 };
		settingIpcItem_mSpinnerExposureTime.getSpinner().setTag(defValues1);
		settingIpcItem_mSpinnerExposureTime.getSpinner().setOnItemSelectedListener(this);
		String[] scenceMode = getResources().getStringArray(R.array.device_setup_scene_mode_values);
		ArrayAdapter<String> adapterSceneMode = new ArrayAdapter<String>(this, R.layout.right_spinner_item, scenceMode);
		adapterSceneMode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		settingIpcItem_mSpinnerSceneMode.getSpinner().setAdapter(adapterSceneMode);
		Integer[] defValues2 = { 0, 1, 2 };
		settingIpcItem_mSpinnerSceneMode.getSpinner().setTag(defValues2);
		settingIpcItem_mSpinnerSceneMode.getSpinner().setOnItemSelectedListener(this);

		String[] electronicSlowShutter = getResources().getStringArray(R.array.device_setup_electronic_slow_shutter_values);
		ArrayAdapter<String> adapterElectronicSlowShutter = new ArrayAdapter<String>(this, R.layout.right_spinner_item, electronicSlowShutter);
		adapterElectronicSlowShutter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		getSettingIpcItem_mSpinnerElectronicSlowShutter.getSpinner().setAdapter(adapterElectronicSlowShutter);
		Integer[] defValues3 = { 0, 1, 2 };
		getSettingIpcItem_mSpinnerElectronicSlowShutter.getSpinner().setTag(defValues3);
		getSettingIpcItem_mSpinnerElectronicSlowShutter.getSpinner().setOnItemSelectedListener(this);
		String[] colorMode = getResources().getStringArray(R.array.device_setup_color_mode_values);
		ArrayAdapter<String> adapterColorMode = new ArrayAdapter<String>(this, R.layout.right_spinner_item, colorMode);
		adapterColorMode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		settingIpcItem_mSpinnerColorMode.getSpinner().setAdapter(adapterColorMode);
		Integer[] defValues4 = { 1, 2 };
		settingIpcItem_mSpinnerColorMode.getSpinner().setTag(defValues4);
		settingIpcItem_mSpinnerColorMode.getSpinner().setOnItemSelectedListener(this);



		int devId = getIntent().getIntExtra("FUN_DEVICE_ID", 0);
		FunDevice funDevice = FunSupport.getInstance().findDeviceById(devId);
		if ( null == funDevice ) {
			finish();
			return;
		}
		mFunDevice = funDevice;

		// 注册设备操作监听
		FunSupport.getInstance().registerOnFunDeviceOptListener(this);

		// 获取报警配置信息
		tryGetExpertConfig();
	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_device_setup_expert;
	}


	@Override
	protected void onDestroy() {
		
		// 注销监听
		FunSupport.getInstance().removeOnFunDeviceOptListener(this);
		
		super.onDestroy();
	}
	
	private int getSpinnerValue(Spinner spinner) {
		Integer[] values = (Integer[])spinner.getTag();
		int position = spinner.getSelectedItemPosition();
		if ( position >= 0 && position < values.length ) {
			return values[position];
		}
		return 0;
	}
	
	private int getSpinnerPosition(Spinner spinner, int value) {
		Integer[] values = (Integer[])spinner.getTag();
		for ( int i = 0; i < values.length; i ++ ) {
			if ( values[i] == value ) {
				return i;
			}
		}
		return 0;
	}
	
	private boolean isCurrentUsefulConfig(String configName) {
		for ( int i = 0; i < DEV_CONFIGS.length; i ++ ) {
			if ( DEV_CONFIGS[i].equals(configName) ) {
				return true;
			}
		}
		return false;
	}


	private int getEsShutterValue(CameraParam cfg)
	{
		int selectValue = 0;
		int esShutter = 0;
		
		if(cfg == null)
		{
			return 0;
		}
		esShutter = MyUtils.getIntFromHex(cfg.EsShutter);
		if(esShutter <= 0)
		{
			selectValue = 0;
		}
		else if(esShutter <= 3)
		{
			selectValue = 1;
		}
		else
		{
			selectValue = 2;
		}
		return selectValue;
	}
	
	private void refreshExpertConfig() {

		CameraParam cameraParam = (CameraParam)mFunDevice.getConfig(CameraParam.CONFIG_NAME);
		if ( null != cameraParam ) {

			settingIpcItem_mSpinnerExposureTime.getSpinner().setSelection(getSpinnerPosition(settingIpcItem_mSpinnerExposureTime.getSpinner(), cameraParam.exposureParam.Level));
			int selectValue = 0;
			selectValue = getEsShutterValue(cameraParam);
			getSettingIpcItem_mSpinnerElectronicSlowShutter.getSpinner().setSelection(getSpinnerPosition(getSettingIpcItem_mSpinnerElectronicSlowShutter.getSpinner(), selectValue));
			settingIpcItem_mSpinnerColorMode.getSpinner().setSelection(getSpinnerPosition(settingIpcItem_mSpinnerColorMode.getSpinner(), MyUtils.getIntFromHex(cameraParam.DayNightColor)));
			settingIpcItem_mSpinnerSceneMode.getSpinner().setSelection(getSpinnerPosition(settingIpcItem_mSpinnerSceneMode.getSpinner(), MyUtils.getIntFromHex(cameraParam.WhiteBalance)));
		}

	}
	
	private void tryGetExpertConfig() {
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

	private void trySaveExpertConfig() {

		boolean beCameraParamChanged = false;
		CameraParam cameraParam = (CameraParam)mFunDevice.getConfig(CameraParam.CONFIG_NAME);
		if ( null != cameraParam )
		{
			int value = getSpinnerValue(settingIpcItem_mSpinnerExposureTime.getSpinner());
			if(value != cameraParam.exposureParam.Level)
			{
				cameraParam.exposureParam.Level = value;
				beCameraParamChanged = true;
			}

			value = getSpinnerValue(settingIpcItem_mSpinnerSceneMode.getSpinner());
			if(!MyUtils.getHexFromInt(value).equals(cameraParam.WhiteBalance))
			{
				cameraParam.WhiteBalance = MyUtils.getHexFromInt(value);
				beCameraParamChanged = true;
			}
			
			value = getSpinnerValue(settingIpcItem_mSpinnerColorMode.getSpinner());
			if(!MyUtils.getHexFromInt(value).equals(cameraParam.DayNightColor))
			{
				cameraParam.DayNightColor = MyUtils.getHexFromInt(value);
				beCameraParamChanged = true;
			}
			
			value = getSpinnerValue(getSettingIpcItem_mSpinnerElectronicSlowShutter.getSpinner());
			if(value != getEsShutterValue(cameraParam))
			{
				cameraParam.EsShutter = MyUtils.getHexFromInt(value * 3);
				beCameraParamChanged = true;
			}
		}

		if ( beCameraParamChanged) {
			showWaitDialog();

			if ( beCameraParamChanged ) {
				synchronized (mSettingConfigs) {
					mSettingConfigs.add(cameraParam.getConfigName());
				}
				
				FunSupport.getInstance().requestDeviceSetConfig(mFunDevice, cameraParam);
			}
		} else {
			showToast(R.string.device_alarm_no_change);
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
			
			refreshExpertConfig();
		}
	}


	@Override
	public void onDeviceGetConfigFailed(FunDevice funDevice, Integer errCode) {
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
					hideWaitDialog();
				}
			}
			
			//refreshRecordConfig();
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

	@Override
	public void onClick(View view) {
		 switch (view.getId()){
			 case R.id.backBtnInTopLayout:
				 finish();
				 break;
			 case R.id.SettingBtnInTopLayoutText:
				 trySaveExpertConfig();
				 break;
		 }
	}
}
