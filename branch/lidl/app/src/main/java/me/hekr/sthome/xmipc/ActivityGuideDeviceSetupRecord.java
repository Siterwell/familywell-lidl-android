package me.hekr.sthome.xmipc;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.lib.SDKCONST;
import com.lib.funsdk.support.FunError;
import com.lib.funsdk.support.FunLog;
import com.lib.funsdk.support.FunSupport;
import com.lib.funsdk.support.OnFunDeviceOptListener;
import com.lib.funsdk.support.config.CloudStorage;
import com.lib.funsdk.support.config.RecordParam;
import com.lib.funsdk.support.config.RecordParamEx;
import com.lib.funsdk.support.config.SimplifyEncode;
import com.lib.funsdk.support.models.FunDevType;
import com.lib.funsdk.support.models.FunDevice;
import com.lib.funsdk.support.utils.DeviceConfigType;
import com.lib.funsdk.support.utils.MyUtils;
import com.lib.sdk.struct.H264_DVR_FILE_DATA;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.hekr.sthome.R;
import me.hekr.sthome.common.TopbarIpcSuperActivity;
import me.hekr.sthome.commonBaseView.SettingIpcItem;

/**
 * Created by Administrator on 2017/8/31.
 */

public class ActivityGuideDeviceSetupRecord extends TopbarIpcSuperActivity implements View.OnClickListener,OnFunDeviceOptListener, AdapterView.OnItemSelectedListener, SeekBar.OnSeekBarChangeListener {






   private SettingIpcItem settingIpcItem_preRecord,settingIpcItem_length,settingIpcItem_type,settingIpcItem_auto;

    private FunDevice mFunDevice = null;

    private int mCloudPosition = 0;

    /**
     * 本界面需要获取到的设备配置信息
     */
    private final String[] DEV_CONFIGS_FOR_CAMARA = {
            // 获取参数:SimplifyEncode -> audioEable
            SimplifyEncode.CONFIG_NAME,

            // 获取参数:RecordParam
            RecordParam.CONFIG_NAME,

            RecordParamEx.CONFIG_NAME,

            // 获取参数:CloudStorage
            // CloudStorage.CONFIG_NAME
    };

    private final String[] DEV_CONFIGS_FOR_CHANNELS = {
            // 获取参数:RecordParam
            RecordParam.CONFIG_NAME,
    };

    private String[] DEV_CONFIGS = DEV_CONFIGS_FOR_CHANNELS;

    // 设置配置信息的时候,由于有多个,通过下面的列表来判断是否所有的配置都设置完成了
    private List<String> mSettingConfigs = new ArrayList<String>();


    @Override
    protected void onCreateInit() {
        initView();
    }

    private void initView(){

        textView_title.setText(getResources().getString(R.string.param_setting));
        textView_setting.setOnClickListener(this);
        textView_back.setOnClickListener(this);
        textView_setting.setVisibility(View.GONE);
        textView_back.setOnClickListener(this);
        imageButton_setting.setVisibility(View.VISIBLE);
        imageButton_setting.setBackgroundResource(R.mipmap.seleect);
        imageButton_setting.setOnClickListener(this);
        settingIpcItem_preRecord = (SettingIpcItem)findViewById(R.id.yulu);
        settingIpcItem_length    = (SettingIpcItem)findViewById(R.id.luxiangshichang);
        settingIpcItem_type      = (SettingIpcItem)findViewById(R.id.luxiangtype);
        settingIpcItem_auto      = (SettingIpcItem)findViewById(R.id.luxiangauto);

        settingIpcItem_preRecord.getSeekBar().setOnSeekBarChangeListener(this);
        settingIpcItem_length.getSeekBar().setOnSeekBarChangeListener(this);

        String[] recordMode = getResources().getStringArray(R.array.device_setup_record_mode_values);
        ArrayAdapter<String> adapterRecordMode = new ArrayAdapter<String>(this, R.layout.right_spinner_item, recordMode);
        adapterRecordMode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        settingIpcItem_type.getSpinner().setAdapter(adapterRecordMode);
        Integer[] defValues = {1, 0, 2};
        settingIpcItem_type.getSpinner().setTag(defValues);
        settingIpcItem_type.getSpinner().setOnItemSelectedListener(this);
        int devId = getIntent().getIntExtra("FUN_DEVICE_ID", 0);
        FunDevice funDevice = FunSupport.getInstance().findDeviceById(devId);
        if ( null == funDevice ) {
            finish();
            return;
        }


        mFunDevice = funDevice;


        try {
            if (mFunDevice.channel.nChnCount == 1) {
                DEV_CONFIGS = DEV_CONFIGS_FOR_CAMARA;
            }
        }catch (NullPointerException e){
            finish();
            return;
        }


        // 注册设备操作监听
        FunSupport.getInstance().registerOnFunDeviceOptListener(this);

        // 获取报警配置信息
        tryGetRecordConfig();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_device_setup_record;
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

    private String getStringRecordMode(int i) {
        if (i == 0) {
            return "ClosedRecord";
        } else if (i == 1) {
            return "ManualRecord";
        } else {
            return "ConfigRecord";
        }
    }

    private int getIntRecordMode(String s) {
        if (s.equals("ClosedRecord")) {
            return 0;
        } else if (s.equals("ManualRecord")) {
            return 1;
        } else {
            return 2;
        }
    }


    private void refreshRecordConfig() {

        SimplifyEncode simplifyEncode = (SimplifyEncode)mFunDevice.getConfig(SimplifyEncode.CONFIG_NAME);
        if ( null != simplifyEncode ) {
            settingIpcItem_auto.setChecked(simplifyEncode.mainFormat.AudioEnable);
        }

        RecordParam recordParam = (RecordParam)mFunDevice.getConfig(RecordParam.CONFIG_NAME);
        if ( null != recordParam ) {

            settingIpcItem_preRecord.setDetailText( String.valueOf(recordParam.getPreRecordTime()) + getString(R.string.device_setup_record_second));
            settingIpcItem_preRecord.getSeekBar().setProgress(recordParam.getPreRecordTime());
            settingIpcItem_length.setDetailText( String.valueOf(recordParam.getPacketLength()) + getString(R.string.device_setup_record_minute));
            settingIpcItem_length.getSeekBar().setProgress(recordParam.getPacketLength());

            if(getIntRecordMode(recordParam.getRecordMode()) == 2)
            {
                boolean bNoramlRecord = MyUtils.getIntFromHex(recordParam.mask[0][0]) == 7;
                FunLog.i("setup record", "TTT--->" + recordParam.recordMode + "bNoramlRecord"
                        + (bNoramlRecord ? 1 : 2));
                settingIpcItem_type.getSpinner().setSelection(getSpinnerPosition(settingIpcItem_type.getSpinner(), bNoramlRecord ? 1 : 2));
            }
            else
            {
                settingIpcItem_type.getSpinner().setSelection(getSpinnerPosition(settingIpcItem_type.getSpinner(), getIntRecordMode(recordParam.getRecordMode())));
            }
        }

//		if(mFunDevice.getDevType() == FunDevType.EE_DEV_SMALLEYE)
//		{
//			findViewById(R.id.layoutSetupRecordCloud).setVisibility(View.VISIBLE);
//			findViewById(R.id.layoutRecordCloudUnderLine).setVisibility(View.VISIBLE);
//			CloudStorage cloudStorage = (CloudStorage)mFunDevice.getConfig(CloudStorage.CONFIG_NAME);
//			if ( null != cloudStorage ) {
//				setCloudValue(cloudStorage);
//			}
//		}
    }

    private void tryGetRecordConfig() {
        if ( null != mFunDevice ) {

            showWaitDialog();

            for ( String configName : DEV_CONFIGS ) {

                // 删除老的配置信息
                mFunDevice.invalidConfig(configName);

                if(mFunDevice.getDevType() == FunDevType.EE_DEV_SMALLEYE || configName != CloudStorage.CONFIG_NAME)
                {
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
    }

    /**
     * 判断是否所有需要的配置都获取到了
     * @return
     */
    private boolean isAllConfigGetted() {
        for ( String configName : DEV_CONFIGS ) {
            if ( null == mFunDevice.getConfig(configName) ) {
                if(mFunDevice.getDevType() == FunDevType.EE_DEV_SMALLEYE || configName != CloudStorage.CONFIG_NAME)
                {
                    return false;
                }
            }
        }
        return true;
    }

    private void trySaveRecordConfig() {
        boolean beSimplifyEncodeChanged = false;
        SimplifyEncode simplifyEncode = (SimplifyEncode)mFunDevice.getConfig(SimplifyEncode.CONFIG_NAME);
        if ( null != simplifyEncode ) {

            if ( simplifyEncode.mainFormat.AudioEnable
                    != settingIpcItem_auto.isChecked() ) {

                simplifyEncode.mainFormat.AudioEnable = settingIpcItem_auto.isChecked();

                beSimplifyEncodeChanged = true;
            }
        }

        boolean beRecordParamChanged = false;
        RecordParam recordParam = (RecordParam)mFunDevice.getConfig(RecordParam.CONFIG_NAME);
        if ( null != recordParam )
        {
            if(settingIpcItem_preRecord.getSeekBar().getProgress() != recordParam.getPreRecordTime())
            {
                recordParam.setPreRecordTime(settingIpcItem_preRecord.getSeekBar().getProgress());
                beRecordParamChanged = true;
            }

            if(settingIpcItem_length.getSeekBar().getProgress() != recordParam.getPacketLength())
            {
                recordParam.setPacketLength(settingIpcItem_length.getSeekBar().getProgress());
                beRecordParamChanged = true;
            }

            int mode = getSpinnerValue(settingIpcItem_type.getSpinner());

//			if(!getStringRecordMode(mode).equals(recordParam.getRecordMode()))
//			{
            recordParam.recordMode = getStringRecordMode(mode == 1 ? 2 : mode);
            // 如果是联动配置的话，把普通录像去掉
            for (int i = 0; i < SDKCONST.NET_N_WEEKS; ++i) {
                recordParam.mask[i][0] = MyUtils.getHexFromInt(mode == 2 ? 6 : 7);
            }
            beRecordParamChanged = true;
//			}
        }


        if ( beSimplifyEncodeChanged
                || beRecordParamChanged) {
            showWaitDialog();

            // 保存SimplifyEncode
            if ( beSimplifyEncodeChanged ) {
                synchronized (mSettingConfigs) {
                    mSettingConfigs.add(simplifyEncode.getConfigName());
                }

                FunSupport.getInstance().requestDeviceSetConfig(mFunDevice, simplifyEncode);
            }

            if ( beRecordParamChanged ) {
                synchronized (mSettingConfigs) {
                    mSettingConfigs.add(recordParam.getConfigName());
                }

                FunSupport.getInstance().requestDeviceSetConfig(mFunDevice, recordParam);
            }

        } else {
            Toast.makeText(this,getResources().getString(R.string.device_alarm_no_change),Toast.LENGTH_LONG).show();
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

            refreshRecordConfig();
        }
    }


    @Override
    public void onDeviceGetConfigFailed(FunDevice funDevice, Integer errCode) {

        Toast.makeText(this, FunError.getErrorStr(errCode),Toast.LENGTH_LONG).show();
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
        Toast.makeText(this, FunError.getErrorStr(errCode),Toast.LENGTH_LONG).show();
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
    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {


        if(seekBar.equals(settingIpcItem_preRecord.getSeekBar()))
        {
            settingIpcItem_preRecord.setDetailText( String.valueOf(progress) + getString(R.string.device_setup_record_second));
        }
        else if(seekBar.equals(settingIpcItem_length.getSeekBar()))
        {
            settingIpcItem_length.setDetailText( String.valueOf(progress) + getString(R.string.device_setup_record_minute));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStopTrackingTouch(SeekBar arg0) {
        // TODO Auto-generated method stub

    }


    @Override
    public void onDeviceFileListGetFailed(FunDevice funDevice) {
        // TODO Auto-generated method stub

    }

    /**
     *  判断某个字符串是否存在于数组中
     *  用来判断该配置是否通道相关
     *  @param stringArray 原数组
     *  @param source 查找的字符串
     *  @return 是否找到
     */
    public static boolean contains(String[] stringArray, String source) {
        // 转换为list
        List<String> tempList = Arrays.asList(stringArray);

        // 利用list的包含方法,进行判断
        return tempList.contains(source);
    }

    @Override
    public void onClick(View view) {
          switch (view.getId()){
              case R.id.backBtnInTopLayout:
              case R.id.backBtnInTopLayout2:
                  finish();
                  break;
              case R.id.SettingBtnInTopLayoutText:
              case R.id.SettingBtnInTopLayout:
                  trySaveRecordConfig();
                  break;
          }
    }
}
