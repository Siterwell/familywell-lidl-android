package me.hekr.sthome.xmipc;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.lib.FunSDK;
import com.lib.SDKCONST;
import com.lib.funsdk.support.FunError;
import com.lib.funsdk.support.FunSupport;
import com.lib.funsdk.support.OnFunDeviceOptListener;
import com.lib.funsdk.support.OnFunDeviceRecordListener;
import com.lib.funsdk.support.config.OPCompressPic;
import com.lib.funsdk.support.models.FunDevRecordFile;
import com.lib.funsdk.support.models.FunDevice;
import com.lib.funsdk.support.models.FunFileData;
import com.lib.funsdk.support.utils.UIFactory;
import com.lib.funsdk.support.widget.FunVideoView;
import com.lib.sdk.struct.H264_DVR_FILE_DATA;
import com.lib.sdk.struct.H264_DVR_FINDINFO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import me.hekr.sthome.R;
import me.hekr.sthome.commonBaseView.CCPButton;
import me.hekr.sthome.commonBaseView.DateSelectActivity;
import me.hekr.sthome.commonBaseView.ProgressDialog;
import me.hekr.sthome.tools.SystemTintManager;
import me.hekr.sthome.tools.UnitTools;

/**
 * 设备远程访问录像列表,并播放录像
 * 前置: 在上一个Activity-ActivityGuideDeviceCamera中已经登入设备,不需要再重复登入设备
 * 1. 注册录像文件搜索结果监听 - 在搜索完成后以回调的方式返回
 * 2. 按照时间(日期)获取远程设备的录像列表  - onSearchFile()
 * 3. 在回调中处理录像列表结果 - onRequestRecordListSuccess()
 * 4. 播放录像文件  - playRecordVideoByTime()
 * 5. 退出注销监听  - onDestroy()
 */

public class ActivityGuideDeviceRecordList extends AppCompatActivity
        implements OnFunDeviceRecordListener
        ,OnFunDeviceOptListener, OnItemClickListener, OnPreparedListener
        , OnSeekBarChangeListener,OnErrorListener,View.OnClickListener{

    private final int REQUEST_SELECT_DATE  = 1;
    private FunDevice mFunDevice = null;
    private Calendar calendar;
    private DeviceCameraRecordAdapter mRecordByTimeAdapter;
    private DeviceCameraPicAdapter mRecordByFileAdapter;

    private ListView mRecordList = null;
    
    private FunVideoView mVideoView = null;
    
    private RelativeLayout mLayoutProgress = null;
    private TextView mTextCurrTime = null;
    private TextView mTextDuration = null;
    private SeekBar mSeekBar = null;
    private final int MESSAGE_AUTO_HIDE_CONTROL_BAR = 0x103;
    private final int MESSAGE_REFRESH_PROGRESS = 0x100;
    private final int MESSAGE_SEEK_PROGRESS = 0x101;
    private final int MESSAGE_SET_IMAGE = 0x102;
    // 自动隐藏底部的操作控制按钮栏的时间
    private final int AUTO_HIDE_CONTROL_BAR_DURATION = 10000;
    private int MaxProgress;
    private CCPButton ccpButton_fullsreen;
    private TextView textView_back;
    private TextView textView_title;
    private ImageButton imageButton_setting;
    private ProgressDialog mProgressDialog;
    private SystemTintManager systemTintManager;
    private RelativeLayout root;
    private RelativeLayout mLayoutVideoWnd = null;
    private RelativeLayout mLayoutTop = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_record_list);
        systemTintManager = new SystemTintManager(this);
        int devId = getIntent().getIntExtra("FUN_DEVICE_ID", 0);
        boolean byFile = getIntent().getBooleanExtra("BY_FILE", true);
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

        calendar = Calendar.getInstance();
        mLayoutTop = (RelativeLayout)findViewById(R.id.layoutTop);
        root = (RelativeLayout)findViewById(R.id.root);
        mLayoutVideoWnd = (RelativeLayout)findViewById(R.id.layoutPlayWnd);
        mRecordList = (ListView) findViewById(R.id.lv_records);
        mRecordList.setOnItemClickListener(this);

        mLayoutProgress = (RelativeLayout)findViewById(R.id.videoProgressArea);
        mTextCurrTime = (TextView)findViewById(R.id.videoProgressCurrentTime);
        mTextDuration = (TextView)findViewById(R.id.videoProgressDurationTime);
        mSeekBar = (SeekBar)findViewById(R.id.videoProgressSeekBar);
        mSeekBar.setOnSeekBarChangeListener(this);

        mVideoView = (FunVideoView)findViewById(R.id.funRecVideoView);
        mVideoView.setOnPreparedListener(this);
        mVideoView.setOnErrorListener(this);
        mVideoView.setOnTouchListener(new VideoToucher());
        ccpButton_fullsreen= (CCPButton) findViewById(R.id.btnScreenRatio);
        ccpButton_fullsreen.setCCPButtonImageResource(R.mipmap.fullsreen);
        ccpButton_fullsreen.setOnClickListener(this);
        // 1. 注册录像文件搜索结果监听 - 在搜索完成后以回调的方式返回
        FunSupport.getInstance().registerOnFunDeviceRecordListener(this);
        FunSupport.getInstance().registerOnFunDeviceOptListener(this);

        // 2. 按照时间(日期)获取远程设备的录像列表  - onSearchFile()
        onSearchFile();
        textView_back = (TextView)findViewById(R.id.backBtnInTopLayout);
        textView_title = (TextView)findViewById(R.id.textViewInTopLayout);
        imageButton_setting = (ImageButton)findViewById(R.id.SettingBtnInTopLayout);
        textView_back.setOnClickListener(this);
        textView_title.setText(getResources().getString(R.string.device_opt_record_list));
        textView_title.setOnClickListener(this);
        imageButton_setting.setVisibility(View.VISIBLE);
        imageButton_setting.setBackgroundResource(R.mipmap.icon_date);
        imageButton_setting.setOnClickListener(this);
        showVideoControlBar();
        showAsPortrait();
    }


    @Override
    protected void onDestroy() {
    	
    	// 停止视频播放
    	if ( null != mVideoView ) {
    		mVideoView.stopPlayback();
    	}
    	
    	// 5. 退出注销监听
    	FunSupport.getInstance().removeOnFunDeviceRecordListener(this);
        FunSupport.getInstance().removeOnFunDeviceOptListener(this);
    	
    	if ( null != mHandler ) {
    		mHandler.removeCallbacksAndMessages(null);
    		mHandler = null;
    	}
    	
        super.onDestroy();
        
    }
    
    private int MasktoInt(int channel){
    	int MaskofChannel = 0;
    	MaskofChannel = (1 << channel) | MaskofChannel;
    	System.out.println("TTTT-------maskofchannel = " + MaskofChannel);
    	return MaskofChannel;
    }

    private void onSearchFile() {
        showProgressDialog(getResources().getString(R.string.wait));

        int time[] = { calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DATE) };
            H264_DVR_FINDINFO info = new H264_DVR_FINDINFO();
            info.st_1_nFileType = SDKCONST.FileType.SDK_RECORD_ALL;
            info.st_2_startTime.st_0_dwYear = time[0];
            info.st_2_startTime.st_1_dwMonth = time[1];
            info.st_2_startTime.st_2_dwDay = time[2];
            info.st_2_startTime.st_3_dwHour = 0;
            info.st_2_startTime.st_4_dwMinute = 0;
            info.st_2_startTime.st_5_dwSecond = 0;
            info.st_3_endTime.st_0_dwYear = time[0];
            info.st_3_endTime.st_1_dwMonth = time[1];
            info.st_3_endTime.st_2_dwDay = time[2];
            info.st_3_endTime.st_3_dwHour = 23;
            info.st_3_endTime.st_4_dwMinute = 59;
            info.st_3_endTime.st_5_dwSecond = 59;
            info.st_0_nChannelN0 = mFunDevice.CurrChannel;
            FunSupport.getInstance().requestDeviceFileList(mFunDevice, info);


    }
    
    private void playRecordVideoByTime(FunDevRecordFile recordFile) {
    	
    	mVideoView.stopPlayback();
    	
    	showProgressDialog(getResources().getString(R.string.wait));

		int[] time = { calendar.get(Calendar.YEAR),
				calendar.get(Calendar.MONTH) + 1,
				calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0 };
		// 只给定起始时间播放
//		int absTime = FunSDK.ToTimeType(time) + recordFile.recStartTime;
//    	mVideoView.playRecordByTime(mFunDevice.getDevSn(), absTime);

		// 给定起始时间和结束时间播放
		int fromTime = FunSDK.ToTimeType(time) + recordFile.recStartTime;
		int toTime = FunSDK.ToTimeType(time) + recordFile.recEndTime;
		mVideoView.playRecordByTime(mFunDevice.getDevSn(), fromTime, toTime, mFunDevice.CurrChannel);

        //打开声音
        mVideoView.setMediaSound(true);
    }

    private void playRecordVideoByFile(FunFileData recordFile) {
        mVideoView.stopPlayback();
        showProgressDialog(getResources().getString(R.string.wait));
        mVideoView.playRecordByFile(mFunDevice.getDevSn(), recordFile.getFileData(), mFunDevice.CurrChannel);
        mVideoView.setMediaSound(true);
    }
    
    private void seekRecordVideo(int progress) {
    	if ( null != mVideoView ) {
				int seekposbyfile = (progress*100)/MaxProgress;
				mVideoView.seekbyfile(seekposbyfile);
    	}
    }

    private void refreshPlayInfo() {
//        if (byFile) {
//            mSeekBar.setEnabled(false);
//        } else {
//            mSeekBar.setEnabled(true);
//        }
        int startTm = mVideoView.getStartTime();
        int endTm = mVideoView.getEndTime();
        MaxProgress = endTm-startTm;
        Log.i("startTm","TTTT----" + startTm);
        Log.i("endTm","TTTT----" + endTm);
        Log.i("MaxProgress","TTT----" + MaxProgress);
        if (startTm > 0 && endTm > startTm) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
            mTextCurrTime.setText(sdf.format(new Date((long) startTm * 1000)));
            mTextDuration.setText(sdf.format(new Date((long) endTm * 1000)));
            mSeekBar.setMax(endTm - startTm);
            mSeekBar.setProgress(0);
            mLayoutProgress.setVisibility(View.VISIBLE);

            resetProgressInterval();
        } else {
            mLayoutProgress.setVisibility(View.GONE);

            cleanProgressInterval();
        }
    }
    
    private void resetProgressInterval() {
    	if ( null != mHandler ) {
    		mHandler.removeMessages(MESSAGE_REFRESH_PROGRESS);
    		mHandler.sendEmptyMessageDelayed(MESSAGE_REFRESH_PROGRESS, 500);
    	}
    }
    
    private void cleanProgressInterval() {
    	if ( null != mHandler ) {
    		mHandler.removeMessages(MESSAGE_REFRESH_PROGRESS);
    	}
    }
    
    private void refreshProgress() {
    	int posTm = mVideoView.getPosition();
    	if ( posTm > 0 ) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
            mTextCurrTime.setText(sdf.format(new Date((long)posTm*1000)));
            Log.i("TTTT","TTTT----" + sdf.format(new Date((long)posTm*1000)));

    		mSeekBar.setProgress(posTm - mVideoView.getStartTime());
    	}
    }
    
    private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case MESSAGE_REFRESH_PROGRESS:
				{
					refreshProgress();
					resetProgressInterval();
				}
				break;
			case MESSAGE_SEEK_PROGRESS:
				{
					seekRecordVideo(msg.arg1);
				}
				break;
			case MESSAGE_SET_IMAGE:
			{
				if (mRecordByFileAdapter != null) {
					mRecordByFileAdapter.setBitmapTempPath((String) msg.obj);
				}
			}
			break;
            case MESSAGE_AUTO_HIDE_CONTROL_BAR:
            {
                hideVideoControlBar();
            }
            break;
			}
		}
    	
    };


    @Override
    public void onRequestRecordListSuccess(List<FunDevRecordFile> files) {

        if (files == null || files.size() == 0) {
            Toast.makeText(this,getResources().getString(R.string.device_camera_video_list_empty),Toast.LENGTH_LONG).show();
        }

        // 3. 在回调中处理录像列表结果 - onRequestRecordListSuccess()
        // 显示录像文件列表

        mRecordByTimeAdapter = new DeviceCameraRecordAdapter(this, files);
        mRecordList.setAdapter(mRecordByTimeAdapter);

        hideProgressDialog();
        
        // 如果录像存在,默认开始播放第一段录像
        if ( files.size() > 0 ) {
        	mRecordByTimeAdapter.setPlayingIndex(0);
        	playRecordVideoByTime(files.get(0));
        }
    }

    @Override
    public void onDeviceFileListChanged(FunDevice funDevice, H264_DVR_FILE_DATA[] datas) {
        hideProgressDialog();
        List<FunFileData> files = new ArrayList<FunFileData>();

        if (null != funDevice
                && null != mFunDevice
                && funDevice.getId() == mFunDevice.getId()) {

            for (H264_DVR_FILE_DATA data : datas) {
                FunFileData funFileData = new FunFileData(data, new OPCompressPic());
                files.add(funFileData);
            }

            if (files.size() == 0) {
                Toast.makeText(this,getResources().getString(R.string.device_camera_video_list_empty),Toast.LENGTH_LONG).show();
            } else {
                mRecordByFileAdapter = new DeviceCameraPicAdapter(this, mRecordList, mFunDevice, files);
                mRecordList.setAdapter(mRecordByFileAdapter);
                if (mRecordByFileAdapter != null) {
                    mRecordByFileAdapter.release();
                }
            }

            // 如果录像存在,默认开始播放第一段录像
            if ( files.size() > 0 ) {
                playRecordVideoByFile(files.get(0));
                mRecordByFileAdapter.setPlayingIndex(0);
            }
        }


    }

    @Override
    public void onRequestRecordListFailed(Integer errCode) {
        hideProgressDialog();
        Toast.makeText(this, FunError.getErrorStr(errCode),Toast.LENGTH_LONG).show();
    }

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

            if (null != mRecordByFileAdapter) {
                FunFileData recordFile = mRecordByFileAdapter.getRecordFile(position);
                if (null != recordFile) {
                    mRecordByFileAdapter.setPlayingIndex(position);
                    playRecordVideoByFile(recordFile);
                }
            }


	}


	@Override
	public void onPrepared(MediaPlayer mp) {
		hideProgressDialog();
		refreshPlayInfo();
		String path = mVideoView.captureImage(null);
		Message message = Message.obtain();
		message.what = MESSAGE_SET_IMAGE;
		message.obj = path;
		mHandler.sendMessageDelayed(message, 200);
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,  
            boolean fromUser) {
		if ( fromUser ) {
			if ( null != mHandler ) {
				mHandler.removeMessages(MESSAGE_SEEK_PROGRESS);
				Message msg = new Message();
				msg.what = MESSAGE_SEEK_PROGRESS;
				msg.arg1 = progress;
				mHandler.sendMessageDelayed(msg, 300);
			}
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
    public void onDeviceLoginSuccess(FunDevice funDevice) {

    }

    @Override
    public void onDeviceLoginFailed(FunDevice funDevice, Integer errCode) {

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
	public void onDeviceFileListGetFailed(FunDevice funDevice) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		// TODO Auto-generated method stub
		// 播放失败
				Toast.makeText(this,getResources().getString(R.string.media_play_error)
						+ " : " 
						+ FunError.getErrorStr(extra),Toast.LENGTH_LONG).show();
		return true;
	}

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.backBtnInTopLayout:
                onBackPressed();
                break;
            case R.id.SettingBtnInTopLayout:
                Intent intent = new Intent(this, DateSelectActivity.class);
                startActivityForResult(intent,REQUEST_SELECT_DATE);
                break;
            case R.id.btnScreenRatio:
                switchOrientation();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode!=RESULT_OK) return;

        if(requestCode==REQUEST_SELECT_DATE){
            int result_year = data.getExtras().getInt("year");
            int result_month = data.getExtras().getInt("month");
            int result_day   = data.getExtras().getInt("day");

            calendar.set(result_year, result_month, result_day);
            onSearchFile();

        }

    }

    protected void showProgressDialog(String title){
        mProgressDialog = new ProgressDialog(this);

        mProgressDialog.setCanceledOnTouchOutside(false);

        mProgressDialog.show();

    }

    protected void hideProgressDialog(){
        if(mProgressDialog!=null&&mProgressDialog.isShowing()){
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        // 如果当前是横屏，返回时先回到竖屏
        if (getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            return;
        }
        finish();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // 检测屏幕的方向：纵向或横向
        if (getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE) {
            // 当前为横屏， 在此处添加额外的处理代码
            showAsLandscape();
        }
        else if(getResources().getConfiguration().orientation
                ==Configuration.ORIENTATION_PORTRAIT) {
            // 当前为竖屏， 在此处添加额外的处理代码
            showAsPortrait();
        }

        super.onConfigurationChanged(newConfig);
    }



    @Override
    protected void onResume() {
        systemTintManager.setStatusBarDarkMode(true,this);
        super.onResume();
    }



    private void showVideoControlBar() {
        if (mLayoutProgress.getVisibility() != View.VISIBLE) {
            TranslateAnimation ani = new TranslateAnimation(0, 0, UIFactory.dip2px(this, 42), 0);
            ani.setDuration(200);
            mLayoutProgress.startAnimation(ani);
            mLayoutProgress.setVisibility(View.VISIBLE);
        }

        if (getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE) {
            // 横屏情况下,顶部标题栏也动画显示
            TranslateAnimation ani = new TranslateAnimation(0, 0, -UIFactory.dip2px(this, 48), 0);
            ani.setDuration(200);
            mLayoutTop.startAnimation(ani);
            mLayoutTop.setVisibility(View.VISIBLE);
        } else {
            mLayoutTop.setVisibility(View.VISIBLE);
        }

        // 显示后设置10秒后自动隐藏
        mHandler.removeMessages(MESSAGE_AUTO_HIDE_CONTROL_BAR);
        mHandler.sendEmptyMessageDelayed(MESSAGE_AUTO_HIDE_CONTROL_BAR, AUTO_HIDE_CONTROL_BAR_DURATION);
    }

    private void hideVideoControlBar() {
        if (mLayoutProgress.getVisibility() != View.GONE) {
            TranslateAnimation ani = new TranslateAnimation(0, 0, 0, UIFactory.dip2px(this, 42));
            ani.setDuration(200);
            mLayoutProgress.startAnimation(ani);
            mLayoutProgress.setVisibility(View.GONE);
        }

        if (getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE) {
            // 横屏情况下,顶部标题栏也隐藏
            TranslateAnimation ani = new TranslateAnimation(0, 0, 0, -UIFactory.dip2px(this, 48));
            ani.setDuration(200);
            mLayoutTop.startAnimation(ani);
            mLayoutTop.setVisibility(View.GONE);
        }

        // 隐藏后清空自动隐藏的消息
        mHandler.removeMessages(MESSAGE_AUTO_HIDE_CONTROL_BAR);
    }

    private void showAsLandscape() {
        systemTintManager.setStatusBarAlpha(0.0f);
        root.setPadding(0,0,0,0);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 视频窗口全屏显示
        RelativeLayout.LayoutParams lpWnd = (RelativeLayout.LayoutParams) mLayoutVideoWnd.getLayoutParams();
        lpWnd.height = ViewGroup.LayoutParams.MATCH_PARENT;
        lpWnd.topMargin = 0;
        mLayoutVideoWnd.setLayoutParams(lpWnd);

        // 上面标题半透明背景
        mLayoutTop.setBackgroundColor(0x00000000);
        ccpButton_fullsreen.setCCPButtonImageResource(R.mipmap.smallsreen);
        mRecordList.setVisibility(View.GONE);

        RelativeLayout.LayoutParams lpctrl = (RelativeLayout.LayoutParams) mLayoutProgress.getLayoutParams();
        lpctrl.leftMargin = UIFactory.dip2px(this, 120);
        lpctrl.rightMargin = UIFactory.dip2px(this,120);
        lpctrl.bottomMargin = UIFactory.dip2px(this,30);
        mLayoutProgress.setLayoutParams(lpctrl);
    }

    private void showAsPortrait() {
        systemTintManager.setStatusBarAlpha(1.0f);
        //沉浸式设置支持API19
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int statusheight = UnitTools.getStatusBarHeight(this);
            root.setPadding(0,statusheight,0,0);
        }

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 还原上面标题栏背景
        mLayoutTop.setBackgroundColor(getResources().getColor(R.color.white));
        mLayoutTop.setVisibility(View.VISIBLE);

        // 视频显示为小窗口
        RelativeLayout.LayoutParams lpWnd = (RelativeLayout.LayoutParams) mLayoutVideoWnd.getLayoutParams();
        lpWnd.height = (int)getResources().getDimension(R.dimen.funcview_height);
        lpWnd.topMargin = UIFactory.dip2px(this, 48);
        // lpWnd.addRule(RelativeLayout.BELOW, mLayoutTop.getId());
        mLayoutVideoWnd.setLayoutParams(lpWnd);
        ccpButton_fullsreen.setCCPButtonImageResource(R.mipmap.fullsreen);
        mRecordList.setVisibility(View.VISIBLE);

        RelativeLayout.LayoutParams lpctrl = (RelativeLayout.LayoutParams) mLayoutProgress.getLayoutParams();
        lpctrl.leftMargin = UIFactory.dip2px(this, 0);
        lpctrl.rightMargin = UIFactory.dip2px(this, 0);
        lpctrl.bottomMargin = UIFactory.dip2px(this,0);
        mLayoutProgress.setLayoutParams(lpctrl);

    }

    /**
     * 切换视频全屏/小视频窗口(以切横竖屏切换替代)
     */
    private void switchOrientation() {
        // 横竖屏切换
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                && getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE) {
            // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        } else if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    private class VideoToucher implements View.OnTouchListener {

        private long firClick = 0;
        private long secClick = 0;


        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int iAction = event.getAction();
             if (iAction == MotionEvent.ACTION_UP || iAction == MotionEvent.ACTION_CANCEL) { // 弹起


                    secClick = System.currentTimeMillis();
                    if (firClick > 0) {
                        if (secClick - firClick < 500) {
                            // double click

                        }else{
                            if (mLayoutProgress.getVisibility() == View.VISIBLE) {
                                hideVideoControlBar();
                            } else {
                                showVideoControlBar();
                            }
                        }
                    }
                    firClick = secClick;
            }
            return false;
        }
    }


}