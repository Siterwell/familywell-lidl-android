package me.hekr.sthome.commonBaseView;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lib.funsdk.support.FunSupport;
import com.lib.funsdk.support.OnFunDeviceOptListener;
import com.lib.funsdk.support.config.DevCmdOPSCalendarInMonth;
import com.lib.funsdk.support.models.FunDevice;
import com.lib.sdk.struct.H264_DVR_FILE_DATA;

import java.util.Calendar;

import me.hekr.sthome.R;
import me.hekr.sthome.common.TopbarIpcSuperActivity;

/**
 * Created by Administrator on 2017/9/7.
 */

public class DateSelectActivity extends TopbarIpcSuperActivity implements View.OnClickListener,OnFunDeviceOptListener {

    private ImageView iv_left;
    private ImageView iv_right;
    private TextView tv_date;
    private MonthDateView monthDateView;
    private FunDevice mFunDevice = null;
    private final String TAG = DateSelectActivity.class.getName();
    private Calendar calendar;

    @Override
    protected void onCreateInit() {
        initView();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_date;
    }

    private void initView(){

        int devId = getIntent().getIntExtra("FUN_DEVICE_ID", 0);
        FunDevice funDevice = FunSupport.getInstance().findDeviceById(devId);
        if (devId==0) {
            funDevice = FunSupport.getInstance().mCurrDevice;
        }
        if (null == funDevice) {
            finish();
            return;
        } else {
            mFunDevice = funDevice;
        }
        calendar = (Calendar)getIntent().getSerializableExtra("Date");
        textView_title.setText(getResources().getString(R.string.select_date));
        textView_back.setOnClickListener(this);
        textView_setting.setVisibility(View.VISIBLE);
        textView_setting.setText(getString(R.string.dialog_btn_confim));
        textView_setting.setOnClickListener(this);

        iv_left = (ImageView) findViewById(R.id.iv_left);
        iv_right = (ImageView) findViewById(R.id.iv_right);
        monthDateView = (MonthDateView) findViewById(R.id.monthDateView);
        tv_date = (TextView) findViewById(R.id.date_text);
        monthDateView.setTextView(tv_date);
        monthDateView.setToday(calendar);
        iv_left.setOnClickListener(this);
        iv_right.setOnClickListener(this);
        // 1. 注册录像文件搜索结果监听 - 在搜索完成后以回调的方式返回

        // 注册设备操作监听
        FunSupport.getInstance().registerOnFunDeviceOptListener(this);
        // 获取时间
        DevCmdOPSCalendarInMonth devCmdOPSCalendarInMonth = new DevCmdOPSCalendarInMonth();
        devCmdOPSCalendarInMonth.setYear(calendar.get(Calendar.YEAR));
        devCmdOPSCalendarInMonth.setMonth(calendar.get(Calendar.MONTH)+1);
        FunSupport.getInstance().requestDeviceCmdGeneral(
                mFunDevice, devCmdOPSCalendarInMonth);

    }

    @Override
    public void onClick(View view) {
         switch (view.getId()){
             case R.id.backBtnInTopLayout:
                 finish();
                 break;
             case R.id.SettingBtnInTopLayoutText:
                 Intent intent = new Intent();
                 intent.putExtra("year",monthDateView.getmSelYear());
                 intent.putExtra("month",monthDateView.getmSelMonth());
                 intent.putExtra("day",monthDateView.getmSelDay());
                 setResult(RESULT_OK, intent);
                 finish();
                 break;
             case R.id.iv_left:
                 monthDateView.onLeftClick();
                 int Year = monthDateView.getmSelYear();
                 int Month = monthDateView.getmSelMonth();
                 DevCmdOPSCalendarInMonth devCmdOPSCalendarInMonth = new DevCmdOPSCalendarInMonth();
                 devCmdOPSCalendarInMonth.setYear(Year);
                 devCmdOPSCalendarInMonth.setMonth(Month+1);
                 FunSupport.getInstance().requestDeviceCmdGeneral(
                         mFunDevice, devCmdOPSCalendarInMonth);
                 break;
             case R.id.iv_right:
                 monthDateView.onRightClick();
                 int Year2 = monthDateView.getmSelYear();
                 int Month2 = monthDateView.getmSelMonth();
                 DevCmdOPSCalendarInMonth devCmdOPSCalendarInMonth2 = new DevCmdOPSCalendarInMonth();
                 devCmdOPSCalendarInMonth2.setYear(Year2);
                 devCmdOPSCalendarInMonth2.setMonth(Month2+1);
                 FunSupport.getInstance().requestDeviceCmdGeneral(
                         mFunDevice, devCmdOPSCalendarInMonth2);
                 break;
         }
    }




    private int MasktoInt(int channel){
        int MaskofChannel = 0;
        MaskofChannel = (1 << channel) | MaskofChannel;
        System.out.println("TTTT-------maskofchannel = " + MaskofChannel);
        return MaskofChannel;
    }



    @Override
    protected void onDestroy() {
        // 注册设备操作监听
        FunSupport.getInstance().removeOnFunDeviceOptListener(this);
        super.onDestroy();
    }

    @Override
    public void onDeviceLoginSuccess(FunDevice funDevice) {

    }

    @Override
    public void onDeviceLoginFailed(FunDevice funDevice, Integer errCode) {

    }

    @Override
    public void onDeviceGetConfigSuccess(FunDevice funDevice, String configName, int nSeq) {
        if(DevCmdOPSCalendarInMonth.CONFIG_NAME.equals(configName)){
            DevCmdOPSCalendarInMonth devCmdOPSCalendarInMonth = (DevCmdOPSCalendarInMonth) mFunDevice
                    .getConfig(DevCmdOPSCalendarInMonth.CONFIG_NAME);

           monthDateView.setDaysHasThingList(devCmdOPSCalendarInMonth.getData());
        }
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
}
