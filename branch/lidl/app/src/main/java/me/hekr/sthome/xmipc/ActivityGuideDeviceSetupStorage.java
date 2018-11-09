package me.hekr.sthome.xmipc;

import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import com.lib.funsdk.support.FunError;
import com.lib.funsdk.support.FunSupport;
import com.lib.funsdk.support.OnFunDeviceOptListener;
import com.lib.funsdk.support.config.GeneralGeneral;
import com.lib.funsdk.support.config.OPStorageManager;
import com.lib.funsdk.support.config.StorageInfo;
import com.lib.funsdk.support.models.FunDevice;
import com.lib.funsdk.support.utils.FileUtils;
import com.lib.funsdk.support.utils.MyUtils;
import com.lib.sdk.struct.H264_DVR_FILE_DATA;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import me.hekr.sthome.R;
import me.hekr.sthome.common.TopbarIpcSuperActivity;
import me.hekr.sthome.commonBaseView.ECAlertDialog;
import me.hekr.sthome.commonBaseView.PorterDuffXfermodeView;
import me.hekr.sthome.tools.LOG;

/**
 * Created by Administrator on 2017/9/4.
 */

public class ActivityGuideDeviceSetupStorage extends TopbarIpcSuperActivity implements View.OnClickListener,OnFunDeviceOptListener {


    private final String TAG = ActivityGuideDeviceSetupStorage.class.getName();
    // 存储容量
    private TextView mTextStorageCapacity = null;
    // 剩余容量
    private TextView mTextStorageRemain = null;
    // 停止播放
    private RadioButton mRbRecordStop=null;
    // 循环播放
    private RadioButton mRbRecordCycle=null;
    // 格式化
    private Button mBtnFormat = null;

    private FunDevice mFunDevice = null;

    private OPStorageManager mOPStorageManager;

    private TextView mTextPercent;
    private PorterDuffXfermodeView porterDuffXfermodeView;
    private float percent = 0l;
    private DecimalFormat df = new DecimalFormat("#.0");
    /**
     * 本界面需要获取到的设备配置信息
     */
    private final String[] DEV_CONFIGS = {
            // SD卡存储容量信息
            StorageInfo.CONFIG_NAME,

            // 录像满时停止录像或循环录像
            GeneralGeneral.CONFIG_NAME
    };

    // 设置配置信息的时候,由于有多个,通过下面的列表来判断是否所有的配置都设置完成了
    private List<String> mSettingConfigs = new ArrayList<String>();

    @Override
    protected void onCreateInit() {

        textView_back.setOnClickListener(this);

        textView_title.setText(getResources().getString(R.string.device_setup_storage));
        porterDuffXfermodeView = (PorterDuffXfermodeView)findViewById(R.id.porter_duff_xfermode_view);
        mTextPercent         = (TextView)findViewById(R.id.percent);
        mTextStorageCapacity = (TextView)findViewById(R.id.storage_has);
        mTextStorageRemain = (TextView)findViewById(R.id.storage_remain);
        mRbRecordStop=(RadioButton)findViewById(R.id.stoprecord);
        mRbRecordStop.setOnClickListener(this);
        mRbRecordCycle=(RadioButton)findViewById(R.id.cyclerecord);
        mRbRecordCycle.setOnClickListener(this);
        mBtnFormat = (Button)findViewById(R.id.btnStorageFormat);
        mBtnFormat.setOnClickListener(this);

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
        tryGetStorageConfig();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_pic_setting_storage;
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
            {
                // 返回/退出
                finish();
            }
            break;
            case R.id.stoprecord:
            {
                trySetOverWrite(false);
            }
            break;
            case R.id.cyclerecord:
            {
                trySetOverWrite(true);
            }
            break;
            case R.id.btnStorageFormat:		// 格式化
            {
                showFormatPartitionDialog();
            }
            break;
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


    private void tryGetStorageConfig() {
        if ( null != mFunDevice ) {

            showWaitDialog();

            for ( String configName : DEV_CONFIGS ) {

                // 删除老的配置信息
                mFunDevice.invalidConfig(configName);

                // 重新搜索新的配置信息
                FunSupport.getInstance().requestDeviceConfig(
                        mFunDevice, configName);
            }
        }
    }

    private void trySetOverWrite(boolean overWrite) {
        GeneralGeneral generalInfo = (GeneralGeneral)mFunDevice.getConfig(GeneralGeneral.CONFIG_NAME);
        if ( null != generalInfo ) {
            if ( overWrite ) {
                //录像满时，循环录像
                generalInfo.setOverWrite(GeneralGeneral.OverWriteType.OverWrite);
            } else {
                //录像满时，停止录像
                generalInfo.setOverWrite(GeneralGeneral.OverWriteType.StopRecord);
            }

            showWaitDialog();

            FunSupport.getInstance().requestDeviceSetConfig(mFunDevice, generalInfo);
        }
    }

    private void refreshStorageConfig() {
        StorageInfo storageInfo = (StorageInfo)mFunDevice.getConfig(StorageInfo.CONFIG_NAME);
        if ( null != storageInfo ) {
            int totalSpace = 0;
            int remainSpace = 0;
            List<StorageInfo.Partition> partitions = storageInfo.getPartitions();
            for ( StorageInfo.Partition partition : partitions ) {
                if ( partition.IsCurrent ) {
                    // 获取当前分区的大小
                    int partTotalSpace = MyUtils.getIntFromHex(partition.TotalSpace);
                    int partRemainSpace = MyUtils.getIntFromHex(partition.RemainSpace);


                    // 累加总大小
                    totalSpace += partTotalSpace;
                    remainSpace += partRemainSpace;
                }
            }
            LOG.I(TAG,"totalSpace:"+totalSpace);
            LOG.I(TAG,"remainSpace:"+remainSpace);
            if(totalSpace>0 || remainSpace > 0){
                percent = ((float) totalSpace / ((float)totalSpace+remainSpace));
                LOG.I(TAG,"percent:"+percent);
                porterDuffXfermodeView.setPercent(percent);

                mTextPercent.setText(df.format(percent*100)+"%");

                mTextStorageCapacity.setText(FileUtils.FormetFileSize(totalSpace, 2));
                mTextStorageRemain.setText(FileUtils.FormetFileSize(remainSpace, 2));
            }

        }

        GeneralGeneral generalInfo = (GeneralGeneral)mFunDevice.getConfig(GeneralGeneral.CONFIG_NAME);
        if ( null != generalInfo ) {
            if( generalInfo.getOverWrite() == GeneralGeneral.OverWriteType.OverWrite ) {
                mRbRecordCycle.setChecked(true);
            }
            else{
                mRbRecordStop.setChecked(true);
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

    /**
     * 请求格式化指定的分区
     * @param iPartition
     * @return
     */
    private boolean requestFormatPartition(int iPartition) {
        StorageInfo storageInfo = (StorageInfo)mFunDevice.getConfig(StorageInfo.CONFIG_NAME);
        if ( null != storageInfo && iPartition < storageInfo.PartNumber ) {
            if ( null == mOPStorageManager ) {
                mOPStorageManager = new OPStorageManager();
                mOPStorageManager.setAction("Clear");
                mOPStorageManager.setSerialNo(0);
                mOPStorageManager.setType("Data");
            }

            mOPStorageManager.setPartNo(iPartition);

            return FunSupport.getInstance().requestDeviceSetConfig(mFunDevice, mOPStorageManager);
        }

        return false;
    }

    private void showFormatPartitionDialog() {

        ECAlertDialog ecAlertDialog = ECAlertDialog.buildAlert(this, getResources().getString(R.string.device_setup_storage_format_tip), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if ( requestFormatPartition(0) ) {
                    showWaitDialog();
                }
            }
        });
        ecAlertDialog.show();
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

            refreshStorageConfig();
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

            if ( OPStorageManager.CONFIG_NAME.equals(configName)
                    && null != mOPStorageManager ) {
                // 请求格式化下一个分区
                if ( !requestFormatPartition(mOPStorageManager.getPartNo() + 1) ) {

                    // 所有分区格式化完成之后,重新获取设备磁盘信息
                    tryGetStorageConfig();
                }
            } else if ( GeneralGeneral.CONFIG_NAME.equals(configName) ) {
                // 设置录像满时，选择停止录像或循环录像成功
                hideWaitDialog();
                refreshStorageConfig();
            }
        }
    }

    @Override
    public void onDeviceSetConfigFailed(final FunDevice funDevice,
                                        final String configName, final Integer errCode) {
        showToast(FunError.getErrorStr(errCode));
        hideWaitDialog();
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
