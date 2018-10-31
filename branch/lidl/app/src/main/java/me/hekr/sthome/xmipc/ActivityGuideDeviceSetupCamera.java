package me.hekr.sthome.xmipc;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.lib.funsdk.support.FunError;
import com.lib.funsdk.support.FunSupport;
import com.lib.funsdk.support.OnFunDeviceOptListener;
import com.lib.funsdk.support.config.AVEncVideoWidget;
import com.lib.funsdk.support.config.CameraParam;
import com.lib.funsdk.support.config.CameraParamEx;
import com.lib.funsdk.support.config.ChannelTitle;
import com.lib.funsdk.support.config.FVideoOsdLogo;
import com.lib.funsdk.support.config.PowerSocketImage;
import com.lib.funsdk.support.config.SimplifyEncode;
import com.lib.funsdk.support.models.FunDevice;
import com.lib.funsdk.support.models.FunDeviceSocket;
import com.lib.funsdk.support.utils.DeviceConfigType;
import com.lib.sdk.struct.H264_DVR_FILE_DATA;
import com.lib.sdk.struct.SDK_TitleDot;

import java.util.ArrayList;
import java.util.List;

import me.hekr.sthome.R;
import me.hekr.sthome.common.TopbarIpcSuperActivity;
import me.hekr.sthome.commonBaseView.SettingIpcItem;

/**
 * 图像配置
 *
 */
public class ActivityGuideDeviceSetupCamera extends TopbarIpcSuperActivity
		implements OnClickListener, OnFunDeviceOptListener, OnItemSelectedListener {

	// OSD(水印内容)
	private EditText mEditOSDContent = null;
	// 清晰度
	private SettingIpcItem settingIpcItem_Definition;
	// OSD水印开关
	private SettingIpcItem settingIpcItem_SwitchOSD;
	// OSD时间开关
	private SettingIpcItem settingIpcItem_TimeSwitchOSD;
	// 背光补偿
	private SettingIpcItem settingIpcItem_SwitchBLCMode;
	// 宽动态
	private SettingIpcItem settingIpcItem_SwitchWideDynamic;
	// 降噪
	private SettingIpcItem settingIpcItem_SpinnerNoiseReduction;
	// 电子防抖
	private SettingIpcItem settingIpcItem_SwitchAntiShake;
	// 测光模式
	private SettingIpcItem settingIpcItem_SpinnerMetering;

	private FunDevice mFunDevice = null;
	/**
	 * 本界面需要的获取的设备配置信息（插座类）
	 * 
	 */
	private final String[] DEV_CONFIGS_FOR_SOCKET = { PowerSocketImage.CONFIG_NAME, };

	/**
	 * 本界面需要获取到的设备配置信息（监控类）
	 */
	private final String[] DEV_CONFIGS_FOR_CAMERA = {
			// 获取参数:SimplifyEncode -> 清晰度
			SimplifyEncode.CONFIG_NAME,

			// 获取参数:FVideoOsdLogo 
			FVideoOsdLogo.CONFIG_NAME,

			// 获取参数:CameraParam -> 图像上下翻转/图像左右翻转/背光补偿/降噪
			CameraParam.CONFIG_NAME,

			// 获取参数:CameraParamEx -> 电子防抖/测光模式/宽动态

			CameraParamEx.CONFIG_NAME,

			// OSD水印内容
			// ChannelTitle.CONFIG_NAME,
			// ChannelTitle
			AVEncVideoWidget.CONFIG_NAME };
	private String[] DEV_CONFIGS = null;
	// 设置配置信息的时候,由于有多个,通过下面的列表来判断是否所有的配置都设置完成了
	private List<String> mSettingConfigs = new ArrayList<String>();
	
	private ChannelTitle mSetChannelTitle = null;
	private SDK_TitleDot mTitleDot = null;


	@Override
	protected void onCreateInit() {

		textView_back.setOnClickListener(this);
		textView_setting.setOnClickListener(this);
		textView_title.setText(getResources().getString(R.string.device_setup_image));
		textView_setting.setVisibility(View.GONE);
		textView_back.setVisibility(View.GONE);
		imageButton_back.setVisibility(View.VISIBLE);
		imageButton_back.setOnClickListener(this);
		imageButton_setting.setVisibility(View.VISIBLE);
		imageButton_setting.setBackgroundResource(R.drawable.ic_common_check);
		imageButton_setting.setOnClickListener(this);
		settingIpcItem_Definition = (SettingIpcItem)findViewById(R.id.qingxidu);
		settingIpcItem_SwitchOSD  = (SettingIpcItem)findViewById(R.id.titleCameraOSD);
		settingIpcItem_TimeSwitchOSD= (SettingIpcItem)findViewById(R.id.timetitleCameraOSD);
		settingIpcItem_SwitchBLCMode= (SettingIpcItem)findViewById(R.id.SwitchCameraBLCMode);
		settingIpcItem_SwitchWideDynamic= (SettingIpcItem)findViewById(R.id.SwitchCameraWideDynamic);
		settingIpcItem_SpinnerNoiseReduction= (SettingIpcItem)findViewById(R.id.spinnerCameraNoiseReduction);
		settingIpcItem_SwitchAntiShake= (SettingIpcItem)findViewById(R.id.SwitchCameraAntiShake);
		settingIpcItem_SpinnerMetering= (SettingIpcItem)findViewById(R.id.spinnerCameraMetering);

		String[] definitions = getResources().getStringArray(R.array.device_setup_camera_definition_values);
		ArrayAdapter<String> adapterDefinition = new ArrayAdapter<String>(this, R.layout.right_spinner_item,
				definitions);
		adapterDefinition.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		settingIpcItem_Definition.getSpinner().setAdapter(adapterDefinition);
		Integer[] defValues = { 6, 5, 4, 3, 2, 1 };
		settingIpcItem_Definition.getSpinner().setTag(defValues);
		settingIpcItem_Definition.getSpinner().setOnItemSelectedListener(this);
		mEditOSDContent = (EditText) findViewById(R.id.editCameraOSDContent);


		String[] noiseReds = getResources().getStringArray(R.array.device_setup_camera_noise_reduction_values);
		ArrayAdapter<String> adapterNoise = new ArrayAdapter<String>(this, R.layout.right_spinner_item, noiseReds);
		adapterNoise.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		settingIpcItem_SpinnerNoiseReduction.getSpinner().setAdapter(adapterNoise);
		Integer[] noiseValues = { 0, 2, 4 };
		settingIpcItem_SpinnerNoiseReduction.getSpinner().setTag(noiseValues);
		settingIpcItem_SpinnerNoiseReduction.getSpinner().setOnItemSelectedListener(this);

		String[] meterings = getResources().getStringArray(R.array.device_setup_camera_metering_values);
		ArrayAdapter<String> adapterMetering = new ArrayAdapter<String>(this, R.layout.right_spinner_item, meterings);
		adapterMetering.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		settingIpcItem_SpinnerMetering.getSpinner().setAdapter(adapterMetering);
		Integer[] meteringValues = { 0, 1, 2 };
		settingIpcItem_SpinnerMetering.getSpinner().setTag(meteringValues);
		settingIpcItem_SpinnerMetering.getSpinner().setOnItemSelectedListener(this);
		int devId = getIntent().getIntExtra("FUN_DEVICE_ID", 0);
		mFunDevice = FunSupport.getInstance().findDeviceById(devId);
		if (null == mFunDevice) {
			finish();
			return;
		}
		if (mFunDevice instanceof FunDeviceSocket) {
			// 插座类的设备报警
			DEV_CONFIGS = DEV_CONFIGS_FOR_SOCKET;
		} else {
			// 监控类的设备报警
			DEV_CONFIGS = DEV_CONFIGS_FOR_CAMERA;
		}
		// 注册设备操作监听
		FunSupport.getInstance().registerOnFunDeviceOptListener(this);

		// 获取报警配置信息
		tryGetCameraConfig();
	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_device_setup_camera;
	}

	@Override
	protected void onDestroy() {

		// 注销监听
		FunSupport.getInstance().removeOnFunDeviceOptListener(this);

		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.backBtnInTopLayout:
			case R.id.backBtnInTopLayout2:
			finish();
			break;
			case R.id.SettingBtnInTopLayoutText:
			case R.id.SettingBtnInTopLayout:
			trySaveCameraConfig();
			break;
		}
	}

	private int getSpinnerValue(Spinner spinner) {
		Integer[] values = (Integer[]) spinner.getTag();
		int position = spinner.getSelectedItemPosition();
		if (position >= 0 && position < values.length) {
			return values[position];
		}
		return 0;
	}
	
	private int getSpinnerPosition(Spinner spinner, int value) {
		Integer[] values = (Integer[]) spinner.getTag();
		for (int i = 0; i < values.length; i++) {
			if (values[i] == value) {
				return i;
			}
		}
		return 0;
	}

	private boolean isCurrentUsefulConfig(String configName) {
		for (int i = 0; i < DEV_CONFIGS.length; i++) {
			if (DEV_CONFIGS[i].equals(configName)) {
				return true;
			}
		}
		return false;
	}

	private void refreshCameraConfig() {

			SimplifyEncode simplifyEncode = (SimplifyEncode) mFunDevice.getConfig(SimplifyEncode.CONFIG_NAME);
			if (null != simplifyEncode) {
				// 清晰度
				settingIpcItem_Definition.getSpinner()
						.setSelection(getSpinnerPosition(settingIpcItem_Definition.getSpinner(), simplifyEncode.mainFormat.video.Quality));
			}

			CameraParam cameraParam = (CameraParam) mFunDevice.getConfig(CameraParam.CONFIG_NAME);
			if (null != cameraParam) {

				// 背光补偿
				settingIpcItem_SwitchBLCMode.setChecked(cameraParam.getBLCMode());

				// 降噪
				settingIpcItem_SpinnerNoiseReduction.getSpinner()
						.setSelection(getSpinnerPosition(settingIpcItem_SpinnerNoiseReduction.getSpinner(), cameraParam.Night_nfLevel / 2 * 2));
			}

			CameraParamEx cameraParamEx = (CameraParamEx) mFunDevice.getConfig(CameraParamEx.CONFIG_NAME);
			if (null != cameraParamEx) {
				// 电子防抖
				settingIpcItem_SwitchAntiShake.setChecked(cameraParamEx.getDis());

				// 测光模式
				settingIpcItem_SpinnerMetering.getSpinner().setSelection(getSpinnerPosition(settingIpcItem_SpinnerMetering.getSpinner(), cameraParamEx.AeMeansure));

				// 宽动态
				settingIpcItem_SwitchWideDynamic.setChecked(cameraParamEx.getWideDynamic());
			}

			FVideoOsdLogo fVideoOsd = (FVideoOsdLogo) mFunDevice.getConfig(FVideoOsdLogo.CONFIG_NAME);
			AVEncVideoWidget avEnc = (AVEncVideoWidget) mFunDevice.getConfig(AVEncVideoWidget.CONFIG_NAME);
			if (null != avEnc) {
				// 水印开关
				settingIpcItem_SwitchOSD.setChecked(avEnc.channelTitleAttribute.EncodeBlend);
				// 时间开关
				settingIpcItem_TimeSwitchOSD.setChecked(avEnc.timeTitleAttribute.EncodeBlend);
			}

			if (null != avEnc) {
				mEditOSDContent.setText(avEnc.getChannelTitle());
//				setOSDName(avEnc.getChannelTitle());
			}

	}

	private void tryGetCameraConfig() {
		if (null != mFunDevice) {

			showWaitDialog();

			for (String configName : DEV_CONFIGS) {

				// 删除老的配置信息
				mFunDevice.invalidConfig(configName);

				//根据是否需要传通道号 重新搜索新的配置信息
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
	 * 
	 * @return
	 */
	private boolean isAllConfigGetted() {
		for (String configName : DEV_CONFIGS) {
			if (null == mFunDevice.getConfig(configName)) {
				return false;
			}
		}
		return true;
	}

	private void trySaveCameraConfig() {
		mSetChannelTitle = null;

			boolean beSimplifyEncodeChanged = false;
			SimplifyEncode simplifyEncode = (SimplifyEncode) mFunDevice.getConfig(SimplifyEncode.CONFIG_NAME);
			if (null != simplifyEncode) {
				// 清晰度
				if (simplifyEncode.mainFormat.video.Quality != getSpinnerValue(settingIpcItem_Definition.getSpinner())) {
					simplifyEncode.mainFormat.video.Quality = getSpinnerValue(settingIpcItem_Definition.getSpinner());
					beSimplifyEncodeChanged = true;
				}
			}

			boolean beCameraParamChanged = false;
			CameraParam cameraParam = (CameraParam) mFunDevice.getConfig(CameraParam.CONFIG_NAME);
			if (null != cameraParam) {

				// 背光补偿
				if (settingIpcItem_SwitchBLCMode.isChecked() != cameraParam.getBLCMode()) {
					cameraParam.setBLCMode(settingIpcItem_SwitchBLCMode.isChecked());
					beCameraParamChanged = true;
				}

				// 降噪
				if (cameraParam.Night_nfLevel != getSpinnerValue(settingIpcItem_SpinnerNoiseReduction.getSpinner())) {
					cameraParam.Night_nfLevel = getSpinnerValue(settingIpcItem_SpinnerNoiseReduction.getSpinner());
					beCameraParamChanged = true;
				}

			}

			boolean beCameraParamExChanged = false;
			CameraParamEx cameraParamEx = (CameraParamEx) mFunDevice.getConfig(CameraParamEx.CONFIG_NAME);
			if (null != cameraParamEx) {
				// 电子防抖
				if (settingIpcItem_SwitchAntiShake.isChecked() != cameraParamEx.getDis()) {
					cameraParamEx.setDis(settingIpcItem_SwitchAntiShake.isChecked());
					beCameraParamExChanged = true;
				}

				// 测光模式
				if (cameraParamEx.AeMeansure != getSpinnerValue(settingIpcItem_SpinnerMetering.getSpinner())) {
					cameraParamEx.AeMeansure = getSpinnerValue(settingIpcItem_SpinnerMetering.getSpinner());
					beCameraParamExChanged = true;
				}

				// 宽动态
				if (settingIpcItem_SwitchWideDynamic.isChecked() != cameraParamEx.getWideDynamic()) {
					cameraParamEx.setWideDynamic(settingIpcItem_SwitchWideDynamic.isChecked());
					beCameraParamExChanged = true;
				}
			}

			boolean beFVideoOsdLogoChanged = false;
			FVideoOsdLogo fVideoOsd = (FVideoOsdLogo) mFunDevice.getConfig(FVideoOsdLogo.CONFIG_NAME);
			if (null != fVideoOsd) {
				
			}

			AVEncVideoWidget avEnc = (AVEncVideoWidget) mFunDevice.getConfig(AVEncVideoWidget.CONFIG_NAME);
			boolean beChannelTitleChanged = false;
			if (null != avEnc) {
				//Switch of OSD
				if (settingIpcItem_SwitchOSD.isChecked() != avEnc.channelTitleAttribute.EncodeBlend) {
					avEnc.channelTitleAttribute.EncodeBlend = settingIpcItem_SwitchOSD.isChecked();
					beChannelTitleChanged = true;
				}
				// switch of time 
				if (settingIpcItem_TimeSwitchOSD.isChecked() != avEnc.timeTitleAttribute.EncodeBlend) {
					avEnc.timeTitleAttribute.EncodeBlend = settingIpcItem_TimeSwitchOSD.isChecked();
					beChannelTitleChanged = true;
				}
				//text of OSD
				String osdContent = mEditOSDContent.getText().toString().trim();
				if (!osdContent.equals(avEnc.getChannelTitle())) {
					avEnc.setChannelTitle(osdContent);
					beChannelTitleChanged = true;
				}
			}

			mSettingConfigs.clear();

			if (beSimplifyEncodeChanged || beCameraParamChanged || beCameraParamExChanged || beFVideoOsdLogoChanged
					|| beChannelTitleChanged) {
				showWaitDialog();

				// 保存SimplifyEncode
				if (beSimplifyEncodeChanged) {
					synchronized (mSettingConfigs) {
						mSettingConfigs.add(simplifyEncode.getConfigName());
					}

					FunSupport.getInstance().requestDeviceSetConfig(mFunDevice, simplifyEncode);
				}

				// 保存CameraParam
				if (beCameraParamChanged) {
					synchronized (mSettingConfigs) {
						mSettingConfigs.add(cameraParam.getConfigName());
					}

					FunSupport.getInstance().requestDeviceSetConfig(mFunDevice, cameraParam);
				}

				// 保存CameraParamEx
				if (beCameraParamExChanged) {
					synchronized (mSettingConfigs) {
						mSettingConfigs.add(cameraParamEx.getConfigName());
					}

					FunSupport.getInstance().requestDeviceSetConfig(mFunDevice, cameraParamEx);
				}

				// 保存FVideoOsdLogo
				if (beFVideoOsdLogoChanged) {
					synchronized (mSettingConfigs) {
						mSettingConfigs.add(fVideoOsd.getConfigName());
					}

					FunSupport.getInstance().requestDeviceSetConfig(mFunDevice, fVideoOsd);
				}

				// 保存ChannelTitle
				if (beChannelTitleChanged) {
					synchronized (mSettingConfigs) {
						mSettingConfigs.add(avEnc.getConfigName());
						
						mSetChannelTitle = new ChannelTitle();
						mSetChannelTitle.setChannelTitle(avEnc.getChannelTitle());
						
						// 最后需要设置一个点阵信息
						//mSettingConfigs.add(ChannelTitleDotSet.CONFIG_NAME);
					}
					
					FunSupport.getInstance().requestDeviceSetConfig(mFunDevice, avEnc);
					
					if ( null != mSetChannelTitle ) {
						FunSupport.getInstance().requestDeviceCmdGeneral(mFunDevice, mSetChannelTitle);
					}
				}
			} else {
				showToast(R.string.device_alarm_no_change);
			}

	}


	
	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {

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
	public void onDeviceGetConfigSuccess(FunDevice funDevice, String configName, int nSeq) {
		if (null != mFunDevice 
				&& funDevice.getId() == mFunDevice.getId() 
				&& isCurrentUsefulConfig(configName)) {
			if (isAllConfigGetted()) {
				hideWaitDialog();
			}

			refreshCameraConfig();
		}

	}

	@Override
	public void onDeviceGetConfigFailed(FunDevice funDevice, Integer errCode) {
		showToast(FunError.getErrorStr(errCode));
	}

	@Override
	public void onDeviceSetConfigSuccess(final FunDevice funDevice, final String configName) {
		if (null != mFunDevice && funDevice.getId() == mFunDevice.getId()) {
			synchronized (mSettingConfigs) {
				if (mSettingConfigs.contains(configName)) {
					mSettingConfigs.remove(configName);
				}

				if (mSettingConfigs.size() == 0) {
					hideWaitDialog();
				}
			}

			refreshCameraConfig();
		}
	}

	@Override
	public void onDeviceSetConfigFailed(final FunDevice funDevice, final String configName, final Integer errCode) {
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