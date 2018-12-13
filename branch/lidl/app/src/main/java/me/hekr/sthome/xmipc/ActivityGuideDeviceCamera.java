package me.hekr.sthome.xmipc;


import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lib.EPTZCMD;
import com.lib.FunSDK;
import com.lib.funsdk.support.FunDevicePassword;
import com.lib.funsdk.support.FunError;
import com.lib.funsdk.support.FunLog;
import com.lib.funsdk.support.FunPath;
import com.lib.funsdk.support.FunSupport;
import com.lib.funsdk.support.OnFunDeviceListener;
import com.lib.funsdk.support.OnFunDeviceOptListener;
import com.lib.funsdk.support.config.SimplifyEncode;
import com.lib.funsdk.support.config.SystemInfo;
import com.lib.funsdk.support.models.FunDevStatus;
import com.lib.funsdk.support.models.FunDevType;
import com.lib.funsdk.support.models.FunDevice;
import com.lib.funsdk.support.models.FunLoginType;
import com.lib.funsdk.support.models.FunStreamType;
import com.lib.funsdk.support.utils.TalkManager;
import com.lib.funsdk.support.utils.UIFactory;
import com.lib.funsdk.support.widget.FishEyeSettingPannel;
import com.lib.funsdk.support.widget.FunVideoView;
import com.lib.sdk.struct.H264_DVR_FILE_DATA;
import com.xmgl.vrsoft.VRSoftDefine;

import java.io.File;
import java.io.IOException;
import java.util.List;

import me.hekr.sthome.R;
import me.hekr.sthome.common.CCPAppManager;
import me.hekr.sthome.common.DialogWaitting;
import me.hekr.sthome.commonBaseView.CCPButton;
import me.hekr.sthome.commonBaseView.ECAlertDialog;
import me.hekr.sthome.commonBaseView.ECAlertDialog_ipc;
import me.hekr.sthome.commonBaseView.ECListDialog;
import me.hekr.sthome.commonBaseView.SuberPager;
import me.hekr.sthome.model.modelbean.MonitorBean;
import me.hekr.sthome.service.NetWorkUtils;
import me.hekr.sthome.tools.SystemTintManager;
import me.hekr.sthome.tools.UnitTools;

/**
 * Demo: 监控类设备播放控制等
 *
 *
 */
public class ActivityGuideDeviceCamera
				extends AppCompatActivity
				implements OnClickListener,
        OnFunDeviceOptListener,
							OnPreparedListener,
							OnErrorListener,
							OnInfoListener,OnFunDeviceListener,SuberPager.ItemOnClickListener,FunVideoView.SwitchViewListener {

    private final String TAG = "DeviceCamera";
	private RelativeLayout mLayoutTop = null;
	private TextView mTextTitle = null;
	private TextView mBtnBack = null;
	//private ImageButton mBtnSetup = null;

	private FunDevice mFunDevice = null;

	private RelativeLayout mLayoutVideoWnd = null;
	private FunVideoView mFunVideoView = null;
	private FrameLayout mVideoControlLayout = null;
	private LinearLayout mVideoControlLayout_inner = null;
	private TextView mTextStreamType = null;
	private Toast mToast = null;

	private View mSplitView = null;
	private RelativeLayout mLayoutRecording = null;
    private ImageButton settingBtn = null;
	private LinearLayout mLayoutControls = null;
	private RelativeLayout mBtnVoiceTalk = null;
	private RelativeLayout mBtnVoiceTalk2 = null;
	private ImageButton mBtnDevCapture = null;
	private ImageButton mBtnDevRecord = null;


	private TextView mTextVideoStat = null;
	private ECAlertDialog alertDialog = null;
	private ECAlertDialog wifiDialog = null;
	private DialogWaitting mWaitDialog = null;
	private String preset = null;
	private int mChannelCount;
	//private boolean isGetSysFirst = true;
    private boolean autocatureflag = true;
	private boolean autoplay = false;

	private final int MESSAGE_PLAY_MEDIA = 0x100;
	private final int MESSAGE_AUTO_HIDE_CONTROL_BAR = 0x102;
	private final int MESSAGE_TOAST_SCREENSHOT_PREVIEW = 0x103;
	private final int MESSAGE_OPEN_VOICE = 0x104;
	private final int MESSAGE_AUTO_CAPTURE = 0X105;

	private final int MESSAGE_DELAY_FINISH = 0x106;
	private final int CHECK_DELAY_LOADING = 0x101;
    private final int MESSAGE_AUTO_HIDE_DIRECTION  = 0x107;
	private final int MESSAGE_SECOND_QUERY_STATUS = 0x108; //第二次查询设备状态，为了兼容门铃唤醒
	// 自动隐藏底部的操作控制按钮栏的时间
	private final int AUTO_HIDE_CONTROL_BAR_DURATION = 10000;

	private TalkManager mTalkManager = null;

	private boolean mCanToPlay = false;

	public String NativeLoginPsw; //本地密码
	private RelativeLayout root;
//	private String dvname = null;
//	private String dvid = null;
	private MonitorBean monitorBean;
	private SuberPager suberPager;
	private CCPButton ccpButton_play,ccpButton_mute,ccpButton_maliu,ccpButton_snap,ccpButton_fullsreen;
	private SystemTintManager systemTintManager;
	private ImageView left_direct,right_direct,top_direct,down_direct;
	private int  counnt_query_door_bell = 0;
	private ImageView imageButton_switchbutton;
	private FishEyeSettingPannel fishEyeSettingPannel;
	private MediaPlayer mMediaPlayer;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_camera);
		systemTintManager = new SystemTintManager(this);
		String dvid = getIntent().getStringExtra("FUN_DEVICE_ID");
		String dvname = getIntent().getStringExtra("FUN_DEVICE_NAME");

		if(TextUtils.isEmpty(dvid)){
			finish();
			return;
		}
		root = (RelativeLayout)findViewById(R.id.root);
		mLayoutTop = (RelativeLayout) findViewById(R.id.layoutTop);
		suberPager = (SuberPager)findViewById(R.id.suberpager);
		suberPager.setItemOnClickListener(this);
		mTextTitle = (TextView) findViewById(R.id.textViewInTopLayout);

		mBtnBack = (TextView) findViewById(R.id.backBtnInTopLayout);
		mBtnBack.setOnClickListener(this);
		settingBtn = (ImageButton)findViewById(R.id.SettingBtnInTopLayout);
		settingBtn.setOnClickListener(this);
		settingBtn.setVisibility(View.GONE);
		mLayoutVideoWnd = (RelativeLayout) findViewById(R.id.layoutPlayWnd);
		mLayoutRecording = (RelativeLayout) findViewById(R.id.layout_recording);
		mTextVideoStat = (TextView) findViewById(R.id.textVideoStat);

		mBtnVoiceTalk = (RelativeLayout) findViewById(R.id.btnVoiceTalk);
		mBtnVoiceTalk2 = (RelativeLayout)findViewById(R.id.btnVoiceTalk2);
		mBtnDevCapture = (ImageButton) findViewById(R.id.btnDevCapture);
		mBtnDevRecord = (ImageButton) findViewById(R.id.btnDevRecord);
		mSplitView = findViewById(R.id.splitView);
        imageButton_switchbutton =(ImageView)findViewById(R.id.switch_fish_eye);
		imageButton_switchbutton.setVisibility(View.GONE);
		fishEyeSettingPannel = (FishEyeSettingPannel)findViewById(R.id.setting_fish_eye);
		fishEyeSettingPannel.setVisibility(View.GONE);
		fishEyeSettingPannel.getImageView_qiu().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mFunVideoView.setType(VRSoftDefine.XMVRShape.Shape_Ball);
			}
		});
		fishEyeSettingPannel.getImageView_banyuan1().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mFunVideoView.setType(VRSoftDefine.XMVRShape.Shape_Ball_Hat);
			}
		});
		fishEyeSettingPannel.getImageView_banyuan2().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mFunVideoView.setType(VRSoftDefine.XMVRShape.Shape_Ball_Bowl);
			}
		});
		fishEyeSettingPannel.getImageView_radius_fangkuai().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mFunVideoView.setType(VRSoftDefine.XMVRShape.Shape_Cylinder);
			}
		});
		fishEyeSettingPannel.getImageView_duoping().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mFunVideoView.setType(VRSoftDefine.XMVRShape.Shape_Grid_4R);
			}
		});
		fishEyeSettingPannel.getImageView_fangkuai().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mFunVideoView.setType(VRSoftDefine.XMVRShape.Shape_Rectangle);
			}
		});
		fishEyeSettingPannel.getImageView_shangxia().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mFunVideoView.setType(VRSoftDefine.XMVRShape.Shape_Rectangle_2R);
			}
		});
		mBtnVoiceTalk.setOnClickListener(this);
		mBtnVoiceTalk.setOnTouchListener(mIntercomTouchLs);
		mBtnVoiceTalk2.setOnClickListener(this);
		mBtnVoiceTalk2.setOnTouchListener(mIntercomTouchLs);
		mBtnDevCapture.setOnClickListener(this);
		mBtnDevRecord.setOnClickListener(this);
		imageButton_switchbutton.setOnClickListener(this);

		mLayoutControls = (LinearLayout) findViewById(R.id.layoutFunctionControl);
		mFunVideoView = (FunVideoView) findViewById(R.id.funVideoView);

		ccpButton_play = (CCPButton) findViewById(R.id.btnPlay);
		ccpButton_mute= (CCPButton) findViewById(R.id.btnStop);
		ccpButton_maliu= (CCPButton) findViewById(R.id.btnStream);
		ccpButton_snap= (CCPButton) findViewById(R.id.btnCapture);
		ccpButton_fullsreen= (CCPButton) findViewById(R.id.btnScreenRatio);

		left_direct = (ImageView)findViewById(R.id.left_direction);
		right_direct = (ImageView)findViewById(R.id.right_direction);
		top_direct   = (ImageView)findViewById(R.id.up_direction);
		down_direct = (ImageView)findViewById(R.id.down_direction);

		ccpButton_play.setCCPButtonImageResource(R.mipmap.play_auto);
		ccpButton_mute.setCCPButtonImageResource(R.mipmap.mute);
		ccpButton_maliu.setText(getResources().getString(R.string.stream_sd));
		ccpButton_snap.setCCPButtonImageResource(R.mipmap.screenshot);
		ccpButton_fullsreen.setCCPButtonImageResource(R.mipmap.fullsreen);
		ccpButton_play.setOnClickListener(this);
		ccpButton_mute.setOnClickListener(this);
		ccpButton_maliu.setOnClickListener(this);
		ccpButton_snap.setOnClickListener(this);
		ccpButton_fullsreen.setOnClickListener(this);

		mFunVideoView.setOnTouchListener(new OnVideoViewTouchListener());
		mFunVideoView.setOnPreparedListener(this);
		mFunVideoView.setOnErrorListener(this);
		mFunVideoView.setOnInfoListener(this);
		mFunVideoView.setmOnSwitchViewListener(this);
		mVideoControlLayout = (FrameLayout) findViewById(R.id.layoutVideoControl);
		mVideoControlLayout_inner = (LinearLayout)findViewById(R.id.layoutVideoControl2);
		mTextStreamType = (TextView) findViewById(R.id.textStreamStat);
		//mBtnSetup.setOnClickListener(this);

		// 注册设备操作回调
		FunSupport.getInstance().registerOnFunDeviceOptListener(this);
		// 注册设备操作回调
		FunSupport.getInstance().registerOnFunDeviceListener(this);
		// 设置登录方式为本地登录
		FunSupport.getInstance().setLoginType(FunLoginType.LOGIN_BY_LOCAL);
		mTextTitle.setText(TextUtils.isEmpty(dvname)?getResources().getString(R.string.dev_type_monitor):dvname);

		// 允许横竖屏切换
		// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);

		showVideoControlBar();



		mCanToPlay = false;


		showAsPortrait();
		initAutoCapture();
		mBtnVoiceTalk.setBackgroundResource(R.drawable.icon_voice_talk);
		monitorBean = new MonitorBean();
		monitorBean.setDevid(dvid);
		monitorBean.setName(dvname);
		requestDeviceStatus(monitorBean);

		initsubmonitor();
		Intent intent2 = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);   //, MediaStore.Images.Media.EXTERNAL_CONTENT_URI
		Uri uri2 = Uri.fromFile(new File(FunPath.DEFAULT_PATH));
		intent2.setData(uri2);
		ActivityGuideDeviceCamera.this.sendBroadcast(intent2);
	}

	private void startAlarm() {
		mMediaPlayer = MediaPlayer.create(this, getSystemDefultRingtoneUri());
		mMediaPlayer.setLooping(true);
		try {
			mMediaPlayer.prepare();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		mMediaPlayer.start();
	}

	//获取系统默认铃声的Uri
	private Uri getSystemDefultRingtoneUri() {
		return RingtoneManager.getActualDefaultRingtoneUri(this,
				RingtoneManager.TYPE_RINGTONE);
	}

	public void stopMusic(Context context)
			throws IOException {
		// paly music ...
		if (mMediaPlayer == null) {
			mMediaPlayer = MediaPlayer.create(this, getSystemDefultRingtoneUri());
		}
		if (mMediaPlayer.isPlaying()) {
			mMediaPlayer.stop();
		}
		mMediaPlayer.reset();
		mMediaPlayer = null;
	}

	private void initsubmonitor(){
		int index = 0;
		List<MonitorBean> list = CCPAppManager.getClientUser().getMonitorList();
		for(int i =0;i<list.size();i++){
			if(monitorBean.getDevid().equals(list.get(i).getDevid())){
				index = i;
				break;
			}
		}
		suberPager.setIndicatorCenter();
		suberPager.setData(list,index);

	}


	@Override
	protected void onDestroy() {

		stopMedia();

		FunSupport.getInstance().removeOnFunDeviceOptListener(this);
		FunSupport.getInstance().removeOnFunDeviceListener(this);

//		 if ( null != mFunDevice ) {
//		 FunSupport.getInstance().requestDeviceLogout(mFunDevice);
//		 }

		if (null != mHandler) {
			mHandler.removeCallbacksAndMessages(null);
			mHandler = null;
		}
		autocatureflag = false;
		super.onDestroy();
	}


	@Override
	protected void onResume() {
		systemTintManager.setStatusBarDarkMode(true,this);
		if (mCanToPlay) {

			int d = NetWorkUtils.getNetWorkType(this);
			if(autoplay || d==4){
				autoplay = true;
				playRealMedia();
			}else if(d > 0 && d < 4){
				ECAlertDialog ecAlertDialog = ECAlertDialog.buildAlert(ActivityGuideDeviceCamera.this, getResources().getString(R.string.gprs_hint), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {

					}
				}, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						autoplay = true;
						playRealMedia();
					}
				});
				ecAlertDialog.show();
			}

		}
//			 resumeMedia();
		checkdelete();
		super.onResume();
	}

	private void checkdelete(){
		try {
			List<MonitorBean> list= CCPAppManager.getClientUser().getMonitorList();
			boolean flag = false;
			for(int i=0;i<list.size();i++){
				if(list.get(i).getDevid().equals(mFunDevice.getDevSn())){
					flag = true;
					mTextTitle.setText(list.get(i).getName());
					break;
				}
			}
			if(!flag){
				finish();
			}
		}catch (NullPointerException e){
			e.printStackTrace();
		}

	}


	@Override
	protected void onPause() {

		stopTalk(0);
		stopMedia();
		if(mTalkManager!=null)
		mTalkManager.onStopTalk();
		mFunVideoView.setMediaSound(true);
//		 pauseMedia();
        if(wifiDialog!=null&&wifiDialog.isShowing()){
			wifiDialog.dismiss();
		}
		super.onPause();
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
	public void onClick(View v) {
		if (v.getId() >= 1000 && v.getId() < 1000 + mChannelCount) {
			mFunDevice.CurrChannel = v.getId() - 1000;
			mFunVideoView.stopPlayback();
			playRealMedia();
		}
		switch (v.getId()) {
		case R.id.backBtnInTopLayout: {
			// 返回/退出
			onBackPressed();
		}
			break;
		case R.id.btnPlay: // 开始播放
		{


			int result = NetWorkUtils.getNetWorkType(this);
			if(result>=4){
				mFunVideoView.stopPlayback();
				if(mFunDevice==null){
					requestDeviceStatus(monitorBean);
				}else{
					mHandler.sendEmptyMessageDelayed(MESSAGE_PLAY_MEDIA, 1000);
				}
			}else if(result<4 && result>0){
				wifiDialog = ECAlertDialog.buildAlert(ActivityGuideDeviceCamera.this, getResources().getString(R.string.gprs_hint), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {

					}
				}, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						mFunVideoView.stopPlayback();
						if(mFunDevice==null){
							requestDeviceStatus(monitorBean);
						}else{
							mHandler.sendEmptyMessageDelayed(MESSAGE_PLAY_MEDIA, 1000);
						}
					}
				});
				wifiDialog.show();
			}else {
				Toast.makeText(this,getResources().getString(R.string.network_timeout),Toast.LENGTH_LONG).show();
			}



		}
			break;
		case R.id.btnStop: // 停止播放
		{
			switchMute();
		}
			break;
		case R.id.btnStream: // 切换码流
		{
			int result = NetWorkUtils.getNetWorkType(this);
			if(result>=4){
				switchMediaStream();
			}else if(result<4 && result>0){
				wifiDialog = ECAlertDialog.buildAlert(ActivityGuideDeviceCamera.this, getResources().getString(R.string.gprs_hint), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {

					}
				}, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						switchMediaStream();
					}
				});
				wifiDialog.show();
			}else {
				Toast.makeText(this,getResources().getString(R.string.network_timeout),Toast.LENGTH_LONG).show();
			}

		}
			break;
		case R.id.btnCapture: // 截图
		{

			tryToCapture();
		}
			break;
		case R.id.btnDevCapture: // 远程设备图像列表
		{

			ECListDialog ecListDialog = new ECListDialog(this,getResources().getStringArray(R.array.check_pics));
			ecListDialog.setTitle(getResources().getString(R.string.check_pic));
			ecListDialog.setOnDialogItemClickListener(new ECListDialog.OnDialogItemClickListener() {
				@Override
				public void onDialogItemClick(Dialog d, int position) {

					switch (position){
						case 0:
							startPictureList();
							break;
						case 1:
							Intent intent2=new Intent(ActivityGuideDeviceCamera.this, ActivityLocalpicvideo.class);
							intent2.putExtra("path",mFunDevice.getDevSn());
							intent2.putExtra("pic_or_video",0);
							startActivity(intent2);
							break;
						default:
							break;
					}

				}
			});
			ecListDialog.show();


		}
			break;
		case R.id.btnDevRecord: // 远程设备录像列表
		{
			startRecordList();
		}
			break;
		case R.id.btnScreenRatio: // 横竖屏切换
		{
			switchOrientation();
		}
			break;
		case R.id.SettingBtnInTopLayout:
			Intent intent =new Intent(this, ActivityGuideSetingMain.class);
					intent.putExtra("devname",monitorBean.getName());
					intent.putExtra("devid",mFunDevice.getDevSn());
			        intent.putExtra("deviceid",mFunDevice.getId());
			startActivity(intent);
			break;
		case R.id.switch_fish_eye:
			if(fishEyeSettingPannel.getVisibility() == View.VISIBLE){
				imageButton_switchbutton.setImageResource(R.mipmap.qiu);
				PropertyValuesHolder valuesHolder = PropertyValuesHolder.ofFloat("scaleX", 1.0f, 0.0f);
				ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(fishEyeSettingPannel, valuesHolder);
				objectAnimator.setDuration(500).start();
				objectAnimator.addListener(new Animator.AnimatorListener() {
					@Override
					public void onAnimationStart(Animator animation) {
					}

					@Override
					public void onAnimationEnd(Animator animation) {
						fishEyeSettingPannel.setVisibility(View.GONE);
					}

					@Override
					public void onAnimationCancel(Animator animation) {

					}

					@Override
					public void onAnimationRepeat(Animator animation) {

					}
				});
			}else{
				fishEyeSettingPannel.setVisibility(View.VISIBLE);
				imageButton_switchbutton.setImageResource(R.mipmap.qiu2);
				PropertyValuesHolder valuesHolder = PropertyValuesHolder.ofFloat("scaleX", 0.0f, 1.0f);
				ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(fishEyeSettingPannel, valuesHolder);
				objectAnimator.setDuration(500).start();
				objectAnimator.addListener(new Animator.AnimatorListener() {
					@Override
					public void onAnimationStart(Animator animation) {
					}

					@Override
					public void onAnimationEnd(Animator animation) {

					}

					@Override
					public void onAnimationCancel(Animator animation) {

					}

					@Override
					public void onAnimationRepeat(Animator animation) {

					}
				});
			}

				break;
		}


	}

	private void switchMute(){
		if(mFunDevice!=null){
			SimplifyEncode simpEnc = (SimplifyEncode) mFunDevice.getConfig(SimplifyEncode.CONFIG_NAME);
			if(FunStreamType.STREAM_SECONDARY ==mFunVideoView.getStreamType()){
				  boolean flag = !simpEnc.extraFormat.AudioEnable;
					simpEnc.mainFormat.AudioEnable = flag;
					simpEnc.extraFormat.AudioEnable = flag;


			}else if(FunStreamType.STREAM_MAIN ==mFunVideoView.getStreamType()){
				boolean flag = !simpEnc.mainFormat.AudioEnable;
				simpEnc.mainFormat.AudioEnable = flag;
				simpEnc.extraFormat.AudioEnable = flag;
			}
			FunSupport.getInstance().requestDeviceSetConfig(mFunDevice, simpEnc);
		}


	}

	private void tryToRecord() {

		if (!mFunVideoView.isPlaying() || mFunVideoView.isPaused()) {
			showToast(R.string.media_record_failure_need_playing);
			return;
		}


		if (mFunVideoView.bRecord) {
			mFunVideoView.stopRecordVideo();
			mLayoutRecording.setVisibility(View.INVISIBLE);

			toastRecordSucess(mFunVideoView.getFilePath());
		} else {
			mFunVideoView.startRecordVideo(mFunDevice.getDevSn());
			mLayoutRecording.setVisibility(View.VISIBLE);
			showToast(R.string.media_record_start);
		}

	}

	/**
	 * 视频截图,并延时一会提示截图对话框
	 */
	private void tryToCapture() {
		if (!mFunVideoView.isPlaying()) {
			showToast(R.string.media_capture_failure_need_playing);
			return;
		}

		final String path = mFunVideoView.captureImage(mFunDevice.getDevSn());	//图片异步保存
		if (!TextUtils.isEmpty(path)) {
			Message message = new Message();
			message.what = MESSAGE_TOAST_SCREENSHOT_PREVIEW;
			message.obj = path;
			mHandler.sendMessageDelayed(message, 200);			//此处延时一定时间等待图片保存完成后显示，也可以在回调成功后显示
		}
	}

	/**
	 * 视频自动截图
	 */
	private void autoToCapture() {
		if (!mFunVideoView.isPlaying()) {
			//showToast(R.string.media_capture_failure_need_playing);
			Log.i(TAG,getResources().getString(R.string.media_capture_failure_need_playing));
			return;
		}

		final String path = mFunVideoView.autocaptureImage(FunPath.getAutoCapturePath(mFunDevice.getDevSn()));	//图片异步保存
		Log.i(TAG,"存储位置为:"+path);
	}


	/**
	 * 显示截图成功对话框
	 * @param path
	 */
	private void toastScreenShotPreview(final String path) {


		ECAlertDialog_ipc ecAlertDialog_ipc = ECAlertDialog_ipc.buildAlert(this, R.string.device_socket_capture_preview, R.string.delete, R.string.save, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				FunPath.deleteFile(path);
				showToast(R.string.device_socket_capture_delete_success);
			}
		}, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				File file = new File(path);
				File imgPath = new File(FunPath.PATH_CAPTURE_TEMP + File.separator+mFunDevice.getDevSn()+File.separator
						+ file.getName());
				if (imgPath.exists()) {
					String d = String.format(getResources().getString(R.string.device_socket_capture_save_success),path);
					showToast(d);
				}

				Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);   //, MediaStore.Images.Media.EXTERNAL_CONTENT_URI
				Uri uri = Uri.fromFile(new File(path));
				intent.setData(uri);
				ActivityGuideDeviceCamera.this.sendBroadcast(intent);
			}
		});
		View view = getLayoutInflater().inflate(R.layout.screenshot_preview, null, false);
		ImageView iv = (ImageView) view.findViewById(R.id.iv_screenshot_preview);
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = false;
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		options.inDither = true;
		Bitmap bitmap = BitmapFactory.decodeFile(path);
		iv.setImageBitmap(bitmap);
		ecAlertDialog_ipc.setContentView(view);
		ecAlertDialog_ipc.setContentPadding(0,0,0,0);
		ecAlertDialog_ipc.setTitle(getResources().getString(R.string.device_socket_capture_preview));
		ecAlertDialog_ipc.show();

	}

	/**
	 * 显示录像成功对话框
	 * @param path
	 */
	private void toastRecordSucess(final String path) {
		Intent intent2 = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);   //, MediaStore.Images.Media.EXTERNAL_CONTENT_URI
		Uri uri2 = Uri.fromFile(new File(path));
		intent2.setData(uri2);
		ActivityGuideDeviceCamera.this.sendBroadcast(intent2);
        new AlertDialog.Builder(this)
                .setTitle(R.string.device_sport_camera_record_success)
				.setMessage(getString(R.string.media_record_stop) + path)
				.setPositiveButton(R.string.device_sport_camera_record_success_open,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {

								Intent intent = new Intent("android.intent.action.VIEW");
								intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								String type = "video/*";
								Uri uri = Uri.fromFile(new File(path));
								intent.setDataAndType(uri, type);
								startActivity(intent);
								FunLog.e("test", "------------startActivity------" + uri.toString());
							}
						})
				.setNegativeButton(R.string.device_sport_camera_record_success_cancel,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
							}
						})
				.show();
	}

	private void showVideoControlBar() {
		if (mVideoControlLayout.getVisibility() != View.VISIBLE) {
			TranslateAnimation ani = new TranslateAnimation(0, 0, UIFactory.dip2px(this, 42), 0);
			ani.setDuration(200);
			mVideoControlLayout.startAnimation(ani);
			mVideoControlLayout.setVisibility(View.VISIBLE);
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
		if (mVideoControlLayout.getVisibility() != View.GONE) {
			TranslateAnimation ani = new TranslateAnimation(0, 0, 0, UIFactory.dip2px(this, 42));
			ani.setDuration(200);
			mVideoControlLayout.startAnimation(ani);
			mVideoControlLayout.setVisibility(View.GONE);
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

	private void hideDirection(){
		if(left_direct.getVisibility() == View.VISIBLE){
			left_direct.setVisibility(View.GONE);
		}

		if(right_direct.getVisibility() == View.VISIBLE){
			right_direct.setVisibility(View.GONE);
		}

		if(down_direct.getVisibility() == View.VISIBLE){
			down_direct.setVisibility(View.GONE);
		}

		if(top_direct.getVisibility() == View.VISIBLE){
			top_direct.setVisibility(View.GONE);
		}

		// 隐藏后清空自动隐藏的消息
		mHandler.removeMessages(MESSAGE_AUTO_HIDE_DIRECTION);
	}

	private void showAsLandscape() {
		systemTintManager.setStatusBarAlpha(0.0f);
		root.setPadding(0,0,0,0);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		FrameLayout.LayoutParams lpctrl = (FrameLayout.LayoutParams) mVideoControlLayout_inner.getLayoutParams();
		lpctrl.leftMargin = UIFactory.dip2px(this, 120);
		lpctrl.rightMargin = UIFactory.dip2px(this, 120);
		mVideoControlLayout_inner.setLayoutParams(lpctrl);
		// 隐藏底部的控制按钮区域
		mLayoutControls.setVisibility(View.GONE);

		// 视频窗口全屏显示
		RelativeLayout.LayoutParams lpWnd = (RelativeLayout.LayoutParams) mLayoutVideoWnd.getLayoutParams();
		lpWnd.height = LayoutParams.MATCH_PARENT;
		// lpWnd.removeRule(RelativeLayout.BELOW);
		lpWnd.topMargin = 0;
		mLayoutVideoWnd.setLayoutParams(lpWnd);

		// 上面标题半透明背景
		mLayoutTop.setBackgroundColor(0x00000000);
		ccpButton_fullsreen.setCCPButtonImageResource(R.mipmap.smallsreen);


		RelativeLayout.LayoutParams lpWnd2 = (RelativeLayout.LayoutParams) mTextStreamType.getLayoutParams();
		lpWnd2.topMargin = UIFactory.dip2px(this, 60);
		mTextStreamType.setLayoutParams(lpWnd2);
		mBtnVoiceTalk2.setVisibility(View.VISIBLE);
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
		// 显示底部的控制按钮区域

		FrameLayout.LayoutParams lpctrl = (FrameLayout.LayoutParams) mVideoControlLayout_inner.getLayoutParams();
		lpctrl.leftMargin = UIFactory.dip2px(this, 0);
		lpctrl.rightMargin = UIFactory.dip2px(this, 0);
		mVideoControlLayout_inner.setLayoutParams(lpctrl);

		mLayoutControls.setVisibility(View.VISIBLE);
		RelativeLayout.LayoutParams lpWnd2 = (RelativeLayout.LayoutParams) mTextStreamType.getLayoutParams();
		lpWnd2.topMargin = UIFactory.dip2px(this, 10);
		mTextStreamType.setLayoutParams(lpWnd2);
		mBtnVoiceTalk2.setVisibility(View.GONE);
		//settingBtn.setImageResource(R.drawable.setting2);
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



	@Override
	public void onDeviceListChanged() {

	}

	@Override
	public void onDeviceStatusChanged(FunDevice funDevice) {

		if(settingBtn.getVisibility()==View.GONE){
			settingBtn.setVisibility(View.VISIBLE);
		}
			mFunDevice = funDevice;

		int flag =  FunSupport.getInstance().DevSleepAwake(funDevice.getDevSn());
		Log.i(TAG,"唤醒门铃咯flag："+flag);
		Log.i(TAG,"设备类型："+funDevice.getDevType().name());
			if (funDevice.devStatus == FunDevStatus.STATUS_ONLINE) {
				// 如果设备在线,获取设备信息
				if ((funDevice.devType == null )) {
					funDevice.devType = FunDevType.EE_DEV_NORMAL_MONITOR;
				}


				if (null != mHandler) {
					mHandler.removeMessages(MESSAGE_DELAY_FINISH);
					mHandler.sendEmptyMessage(MESSAGE_DELAY_FINISH);
				}
			} else {
				// 设备不在线
//				if (null != mHandler) {
//					mHandler.removeMessages(MESSAGE_DELAY_FINISH);
//					mHandler.sendEmptyMessage(MESSAGE_DELAY_FINISH);
//				}
				if(counnt_query_door_bell == 1){
					counnt_query_door_bell = 0;
					Toast.makeText(ActivityGuideDeviceCamera.this, R.string.device_stauts_offline,Toast.LENGTH_LONG).show();
					mTextVideoStat.setVisibility(View.GONE);
				}else{
					mHandler.sendEmptyMessageDelayed(MESSAGE_SECOND_QUERY_STATUS,3000l);
				}


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

	}
	int nPTZCommand = -1;

	@Override
	public void onImageClick(int position) {
		stopMedia();
		suberPager.setData(CCPAppManager.getClientUser().getMonitorList(),position);

		MonitorBean monitorBean2 = CCPAppManager.getClientUser().getMonitorList().get(position);
		monitorBean.setDevid(monitorBean2.getDevid());
		monitorBean.setName(monitorBean2.getName());
		mTextTitle.setText(monitorBean.getName());
		requestDeviceStatus(monitorBean);
	}

	@Override
	public void switchFishEyes(boolean flag) {
		if(flag){
			imageButton_switchbutton.setVisibility(View.VISIBLE);
		}else {
			imageButton_switchbutton.setVisibility(View.GONE);
			fishEyeSettingPannel.setVisibility(View.GONE);
		}

	}

	private class OnVideoViewTouchListener implements OnTouchListener {

		private float x1;
		private float y1;
		private float x2;
		private float y2;
		private long firClick = 0;
		private long secClick = 0;
		private boolean isPTZ = false;
		private final static int HandMoveSize = 8;


		@Override
		public boolean onTouch(View v, MotionEvent event) {
			int iAction = event.getAction();
			Log.i(TAG,"iAction"+iAction);
			if (iAction == MotionEvent.ACTION_DOWN) { // 按下
				isPTZ = false;
				x1 = event.getX();
				y1 = event.getY();
			} else if (iAction == MotionEvent.ACTION_UP || iAction == MotionEvent.ACTION_CANCEL) { // 弹起
				Log.i(TAG,"nPTZCommand"+nPTZCommand);
				Log.i(TAG,"isPTZ"+isPTZ);


				if (isPTZ) {

					isPTZ = false;
					onContrlPTZ1(nPTZCommand, !isPTZ);
				} else {
					secClick = System.currentTimeMillis();
					if (firClick > 0) {
						if (secClick - firClick < 500) {
							// double click

						}else{
							if (mVideoControlLayout.getVisibility() == View.VISIBLE) {
								hideVideoControlBar();
							} else {
								showVideoControlBar();
							}
						}
					}
					firClick = secClick;

					onContrlPTZ1(nPTZCommand, !isPTZ);
				}
			} else if(iAction == MotionEvent.ACTION_MOVE){
				if (!isPTZ) {
					x2 = event.getX();
					y2 = event.getY();
					if (x2 - x1 > HandMoveSize) {
						// right
						isPTZ = true;
						nPTZCommand = EPTZCMD.PAN_LEFT;
						right_direct.setVisibility(View.VISIBLE);
						left_direct.setVisibility(View.GONE);
						top_direct.setVisibility(View.GONE);
						down_direct.setVisibility(View.GONE);
						mHandler.sendEmptyMessageDelayed(MESSAGE_AUTO_HIDE_DIRECTION, 3000);
						onContrlPTZ1(nPTZCommand, !isPTZ);
					} else if (x1 - x2 > HandMoveSize) {
						// left
						isPTZ = true;
						nPTZCommand = EPTZCMD.PAN_RIGHT;
						right_direct.setVisibility(View.GONE);
						left_direct.setVisibility(View.VISIBLE);
						top_direct.setVisibility(View.GONE);
						down_direct.setVisibility(View.GONE);
						mHandler.sendEmptyMessageDelayed(MESSAGE_AUTO_HIDE_DIRECTION, 3000);
						onContrlPTZ1(nPTZCommand, !isPTZ);
					} else if (y2 - y1 > HandMoveSize) {
						// down
						isPTZ = true;
						nPTZCommand = EPTZCMD.TILT_UP;
						left_direct.setVisibility(View.GONE);
						right_direct.setVisibility(View.GONE);
						down_direct.setVisibility(View.VISIBLE);
						top_direct.setVisibility(View.GONE);
						mHandler.sendEmptyMessageDelayed(MESSAGE_AUTO_HIDE_DIRECTION, 3000);
						onContrlPTZ1(nPTZCommand, !isPTZ);
					} else if (y1 - y2 > HandMoveSize) {
						// up
						isPTZ = true;
						nPTZCommand = EPTZCMD.TILT_DOWN;
						left_direct.setVisibility(View.GONE);
						right_direct.setVisibility(View.GONE);
						down_direct.setVisibility(View.GONE);
						top_direct.setVisibility(View.VISIBLE);
						mHandler.sendEmptyMessageDelayed(MESSAGE_AUTO_HIDE_DIRECTION, 3000);
						onContrlPTZ1(nPTZCommand, !isPTZ);
					}
				}
				if (!isPTZ) {
					x1 = event.getX();
					y1 = event.getY();
				}

			}
			return false;
		}


	}

	private void loginDevice() {
		//showWaitDialog();
		mTextVideoStat.setText(R.string.opening);
		mTextVideoStat.setVisibility(View.VISIBLE);
		FunSupport.getInstance().requestDeviceLogin(mFunDevice);
	}

	private void requestSystemInfo() {
		//showWaitDialog();
		mTextVideoStat.setText(R.string.opening);
		mTextVideoStat.setVisibility(View.VISIBLE);
		FunSupport.getInstance().requestDeviceConfig(mFunDevice, SystemInfo.CONFIG_NAME);
		FunSupport.getInstance().requestDeviceConfig(mFunDevice, SimplifyEncode.CONFIG_NAME);
	}


	private void startPictureList() {
		Intent intent = new Intent();
		intent.putExtra("FUN_DEVICE_ID", mFunDevice.getId());
		intent.putExtra("FILE_TYPE", "jpg");
		if (mFunDevice.devType == FunDevType.EE_DEV_NORMAL_MONITOR || mFunDevice.devType == FunDevType.EE_DEV_SMALLEYE) {
			intent.setClass(this, ActivityGuideDevicePictureList.class);
		}
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

	private void startRecordList() {
		Intent intent = new Intent();
		intent.putExtra("FUN_DEVICE_ID", mFunDevice.getId());
		intent.putExtra("FILE_TYPE", "h264;mp4");
		intent.setClass(this, ActivityGuideDeviceRecordListNew.class);
		//intent.setClass(this, ActivityGuideDeviceRecordList.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

	private void playRealMedia() {

		// 显示状态: 正在打开视频...
		//mTextVideoStat.setText(R.string.media_player_opening);
		//mTextVideoStat.setVisibility(View.VISIBLE);

		if (mFunDevice.isRemote) {
			mFunVideoView.setRealDevice(mFunDevice.getDevSn(), mFunDevice.CurrChannel);
		} else {
			String deviceIp = FunSupport.getInstance().getDeviceWifiManager().getGatewayIp();
			mFunVideoView.setRealDevice(deviceIp, mFunDevice.CurrChannel);
		}

		// 打开声音
		mFunVideoView.setMediaSound(true);

		// 设置当前播放的码流类型
		if (FunStreamType.STREAM_SECONDARY == mFunVideoView.getStreamType()) {
			mTextStreamType.setText(R.string.stream_sd);
			ccpButton_maliu.setText(R.string.stream_hd);
		} else {
			mTextStreamType.setText(R.string.stream_hd);
			ccpButton_maliu.setText(R.string.stream_sd);
		}
	}

	private void stopMedia() {
		if (null != mFunVideoView) {
			mFunVideoView.stopPlayback();
			mFunVideoView.stopRecordVideo();
		}
	}

	private void pauseMedia() {
		if (null != mFunVideoView) {
			mFunVideoView.pause();
		}
	}

	private void resumeMedia() {
		if (null != mFunVideoView) {
			mFunVideoView.resume();
		}
	}

	private void switchMediaStream() {
		if (null != mFunVideoView) {
			if (FunStreamType.STREAM_MAIN == mFunVideoView.getStreamType()) {
				mFunVideoView.setStreamType(FunStreamType.STREAM_SECONDARY);
			} else {
				mFunVideoView.setStreamType(FunStreamType.STREAM_MAIN);
			}

			// 重新播放
			mFunVideoView.stopPlayback();
			playRealMedia();
		}
	}

	// 设备登录
	private void requestDeviceStatus(MonitorBean monitorBean) {
		//showWaitDialog();
		mTextVideoStat.setText(R.string.opening);
		mTextVideoStat.setVisibility(View.VISIBLE);
		FunSupport.getInstance().requestDeviceStatus(FunDevType.EE_DEV_NORMAL_MONITOR, monitorBean.getDevid());
	}


	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_PLAY_MEDIA:
				{
						playRealMedia();

			}
				break;
			case MESSAGE_AUTO_HIDE_CONTROL_BAR:
				{
				hideVideoControlBar();
			}
				break;
            case MESSAGE_TOAST_SCREENSHOT_PREVIEW:
                {
				String path = (String) msg.obj;
				toastScreenShotPreview(path);
			}
				break;
            case MESSAGE_OPEN_VOICE:
                {
				mFunVideoView.setMediaSound(true);
			    }
			   break;
			case MESSAGE_AUTO_CAPTURE:
			    autoToCapture();
			    break;
			case MESSAGE_DELAY_FINISH:

					// 启动/打开设备操作界面
				if (null != mFunDevice) {

						// 传入用户名/密码
						mFunDevice.loginName = "admin";
						mFunDevice.loginPsw = "";

						//Save the password to local file
					String devicePasswd = FunDevicePassword.getInstance().getDevicePassword(
							monitorBean.getDevid());
					    if(TextUtils.isEmpty(devicePasswd)){
							FunSDK.DevSetLocalPwd(mFunDevice.getDevSn(), "admin", "");
						}


						// 如果支持云台控制，显示方向键和预置点按钮
						if (mFunDevice.isSupportPTZ()) {
							mSplitView.setVisibility(View.VISIBLE);
						}
					    mTalkManager = new TalkManager(mFunDevice);
						// 如果设备未登录,先登录设备
						if (!mFunDevice.hasLogin() || !mFunDevice.hasConnected()) {
							loginDevice();
						} else {
							requestSystemInfo();
						}

					}
				break;

				case MESSAGE_AUTO_HIDE_DIRECTION:
					hideDirection();
					break;
				case MESSAGE_SECOND_QUERY_STATUS:
					counnt_query_door_bell = 1;
					if(monitorBean!=null)
					requestDeviceStatus(monitorBean);
					break;
			}
		}
	};

	private OnTouchListener mIntercomTouchLs = new OnTouchListener() {

		@Override
		public boolean onTouch(View arg0, MotionEvent arg1) {
			try {
				if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
					startTalk();
				} else if (arg1.getAction() == MotionEvent.ACTION_UP) {
					stopTalk(500);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}
	};

	private void startTalk() {
		if (mTalkManager != null && mHandler != null && mFunVideoView != null) {
			mFunVideoView.setMediaSound(false);			//关闭本地音频

			mTalkManager.onStartTalk();
			mTalkManager.onStartThread();
            mTalkManager.setTalkSound(false);
		}
	}

	private void stopTalk(int delayTime) {
		if (mTalkManager != null && mHandler != null && mFunVideoView != null) {
			mTalkManager.onStopThread();
            mTalkManager.setTalkSound(true);
		}
	}

//    private void OpenVoiceChannel(){
//
//        if (mBtnVoice.getVisibility() == View.VISIBLE) {
//            TranslateAnimation ani = new TranslateAnimation(0, 0, UIFactory.dip2px(this, 100), 0);
//            ani.setDuration(200);
//            mBtnVoiceTalk.setAnimation(ani);
//            mBtnVoiceTalk.setVisibility(View.VISIBLE);
//            mBtnVoice.setVisibility(View.GONE);
//
//            mFunVideoView.setMediaSound(false);			//关闭本地音频
//
//            mTalkManager.onStartTalk();
//			mTalkManager.setTalkSound(true);
//        }
//    }

//    private void CloseVoiceChannel(int delayTime){
//
//        if (mBtnVoiceTalk.getVisibility() == View.VISIBLE) {
//            TranslateAnimation ani = new TranslateAnimation(0, 0, 0, UIFactory.dip2px(this, 100));
//            ani.setDuration(200);
//            mBtnVoiceTalk.setAnimation(ani);
//            mBtnVoiceTalk.setVisibility(View.GONE);
//            mBtnVoice.setVisibility(View.VISIBLE);
//
//            mTalkManager.onStopTalk();
//            mHandler.sendEmptyMessageDelayed(MESSAGE_OPEN_VOICE, delayTime);
//        }
//    }

	/**
	 * 显示输入设备密码对话框
	 */
	private void showInputPasswordDialog() {

		alertDialog = ECAlertDialog.buildAlert(this, getResources().getString(R.string.device_login_input_password),getResources().getString(R.string.cancel),getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		}, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				EditText text = (EditText) alertDialog.getContent().findViewById(R.id.tet);
				String newname = text.getText().toString().trim();

				if (null != mFunDevice) {
					NativeLoginPsw = newname;
					onDeviceSaveNativePws();

					// 重新登录
					loginDevice();
				}

			}
		});
		alertDialog.setContentView(R.layout.edit_alert);
		alertDialog.setTitle(getResources().getString(R.string.device_login_input_password));
		alertDialog.show();


	}

	public void onDeviceSaveNativePws() {
		FunDevicePassword.getInstance().saveDevicePassword(mFunDevice.getDevSn(),
				NativeLoginPsw);
		// 库函数方式本地保存密码
		if (FunSupport.getInstance().getSaveNativePassword()) {
			FunSDK.DevSetLocalPwd(mFunDevice.getDevSn(), "admin", NativeLoginPsw);
			// 如果设置了使用本地保存密码，则将密码保存到本地文件
		}
	}

	@Override
	public void onDeviceLoginSuccess(final FunDevice funDevice) {
		System.out.println("TTT---->>>> loginsuccess");

		if (null != mFunDevice && null != funDevice) {
			if (mFunDevice.getId() == funDevice.getId()) {

				// 登录成功后立刻获取SystemInfo
				// 如果不需要获取SystemInfo,在这里播放视频也可以:playRealMedia();
				requestSystemInfo();
			}
		}
	}

	@Override
	public void onDeviceLoginFailed(final FunDevice funDevice, final Integer errCode) {
		// 设备登录失败
		mTextVideoStat.setVisibility(View.GONE);

		// 如果账号密码不正确,那么需要提示用户,输入密码重新登录
		if (errCode == FunError.EE_DVR_PASSWORD_NOT_VALID) {
			showInputPasswordDialog();
		}
	}

	@Override
	public void onDeviceGetConfigSuccess(final FunDevice funDevice, final String configName, final int nSeq) {
		int channelCount = 0;
		if (SystemInfo.CONFIG_NAME.equals(configName)) {

//			if (!isGetSysFirst) {
//				return;
//			}

			// 更新UI
			//此处为示例如何取通道信息，可能会增加打开视频的时间，可根据需求自行修改代码逻辑
			if (funDevice.channel == null) {
				FunSupport.getInstance().requestGetDevChnName(funDevice);
				requestSystemInfo();
				return;
			}


			mTextVideoStat.setVisibility(View.GONE);

			// 设置允许播放标志
			mCanToPlay = true;

			//isGetSysFirst = false;

			//showToast(getType(funDevice.getNetConnectType()));

			// 获取信息成功后,如果WiFi连接了就自动播放
			// 此处逻辑客户自定义
			int result = NetWorkUtils.getNetWorkType(this);
			Log.i(TAG,"result="+result);
			if(result == 4){
				playRealMedia();
			}else if(result <4 && result > 0){
				wifiDialog = ECAlertDialog.buildAlert(ActivityGuideDeviceCamera.this, getResources().getString(R.string.gprs_hint), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {

					}
				}, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						autoplay = true;
						playRealMedia();
					}
				});
				wifiDialog.show();
			}else{
				showToast(R.string.meida_not_auto_play_because_no_wifi);
			}

		} else if (SimplifyEncode.CONFIG_NAME.equals(configName)) {

			if(mFunDevice!=null){

				try {
					SimplifyEncode simpEnc = (SimplifyEncode) mFunDevice.getConfig(SimplifyEncode.CONFIG_NAME);
					if(FunStreamType.STREAM_SECONDARY ==mFunVideoView.getStreamType()){
						if(simpEnc.extraFormat.AudioEnable){
							ccpButton_mute.setImageResource(R.mipmap.mute);
						}else{
							ccpButton_mute.setImageResource(R.mipmap.not_mute);
						}


					}else if(FunStreamType.STREAM_MAIN ==mFunVideoView.getStreamType()){
						if(simpEnc.mainFormat.AudioEnable){
							ccpButton_mute.setImageResource(R.mipmap.mute);
						}else{
							ccpButton_mute.setImageResource(R.mipmap.not_mute);
						}
					}
				}catch (Exception e){
					e.printStackTrace();
				}

			}

		}
	}

	private String getType(int i){
		switch (i) {
		case 0:
			return "P2P";
		case 1:
			return "DSS";
		case 2:
			return "IP";
		case 5:
			return "RPS";
		default:
			return "";
		}
	}

	@Override
	public void onDeviceGetConfigFailed(final FunDevice funDevice, final Integer errCode) {
		//showToast(FunError.getErrorStr(errCode));
	}


	@Override
	public void onDeviceSetConfigSuccess(final FunDevice funDevice,
			final String configName) {
		if(SimplifyEncode.CONFIG_NAME.equals(configName)){
			if(mFunDevice!=null){
				SimplifyEncode simpEnc = (SimplifyEncode) mFunDevice.getConfig(SimplifyEncode.CONFIG_NAME);
				if(FunStreamType.STREAM_SECONDARY ==mFunVideoView.getStreamType()){
					if(simpEnc.extraFormat.AudioEnable){
						ccpButton_mute.setImageResource(R.mipmap.mute);
					}else{
						ccpButton_mute.setImageResource(R.mipmap.not_mute);
					}


				}else if(FunStreamType.STREAM_MAIN ==mFunVideoView.getStreamType()){
					if(simpEnc.mainFormat.AudioEnable){
						ccpButton_mute.setImageResource(R.mipmap.mute);
					}else{
						ccpButton_mute.setImageResource(R.mipmap.not_mute);
					}
				}
			}
		}
	}


	@Override
	public void onDeviceSetConfigFailed(final FunDevice funDevice,
			final String configName, final Integer errCode) {

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
	public void onPrepared(MediaPlayer arg0) {
		// TODO Auto-generated method stub


	}


	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		// 播放失败
		showToast(getResources().getString(R.string.media_play_error)
				+ " : "
				+ FunError.getErrorStr(extra));

		if ( FunError.EE_TPS_NOT_SUP_MAIN == extra
				|| FunError.EE_DSS_NOT_SUP_MAIN == extra ) {
			// 不支持高清码流,设置为标清码流重新播放
			if (null != mFunVideoView) {
				mFunVideoView.setStreamType(FunStreamType.STREAM_SECONDARY);
				playRealMedia();
			}
		}

		return true;
	}


	@Override
	public boolean onInfo(MediaPlayer arg0, int what, int extra) {

		Log.i(TAG,"onInfo:what="+what);

		if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
			mTextVideoStat.setText(R.string.media_player_buffering);
			mTextVideoStat.setVisibility(View.VISIBLE);
		} else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
			mTextVideoStat.setVisibility(View.GONE);
		}
		return true;
	}

	private OnTouchListener onPtz_up = new OnTouchListener() {

		// @SuppressLint("ClickableViewAccessibility")
		@Override
		public boolean onTouch(View v, MotionEvent arg1) {
			boolean bstop = true;
			int nPTZCommand = -1;
			// return false;
			switch (arg1.getAction()) {
			case KeyEvent.ACTION_DOWN:
				Log.i("test", "onPtz_up -- KeyEvent.ACTION_DOWN");
				bstop = false;
				nPTZCommand = EPTZCMD.TILT_UP;
				break;
			case KeyEvent.ACTION_UP:
				Log.i("test", "onPtz_up -- KeyEvent.ACTION_UP");
				nPTZCommand = EPTZCMD.TILT_UP;
				bstop = true;
				break;
			case KeyEvent.ACTION_MULTIPLE:
				nPTZCommand = EPTZCMD.TILT_UP;
				bstop = Math.abs(arg1.getX()) > v.getWidth()
						|| Math.abs(arg1.getY()) > v.getHeight();
				break;
			default:
				break;
			}
			onContrlPTZ1(nPTZCommand, bstop);
			return false;
		}
	};
	private OnTouchListener onPtz_down = new OnTouchListener() {

		// @SuppressLint("ClickableViewAccessibility")
		@Override
		public boolean onTouch(View v, MotionEvent arg1) {
			boolean bstop = true;
			int nPTZCommand = -1;
			// return false;
			switch (arg1.getAction()) {
			case KeyEvent.ACTION_DOWN:
				bstop = false;
				nPTZCommand = EPTZCMD.TILT_DOWN;
				break;
			case KeyEvent.ACTION_UP:
				bstop = true;
				nPTZCommand = EPTZCMD.TILT_DOWN;
				onContrlPTZ1(nPTZCommand, bstop);
				break;
			case KeyEvent.ACTION_MULTIPLE:
				nPTZCommand = EPTZCMD.TILT_DOWN;
				bstop = Math.abs(arg1.getX()) > v.getWidth()
						|| Math.abs(arg1.getY()) > v.getHeight();
				break;
			default:
				break;
			}
			onContrlPTZ1(nPTZCommand, bstop);
			return false;
		}
	};
	private OnTouchListener onPtz_left = new OnTouchListener() {

		// @SuppressLint("ClickableViewAccessibility")
		@Override
		public boolean onTouch(View v, MotionEvent arg1) {
			boolean bstop = true;
			int nPTZCommand = -1;
			// return false;
			switch (arg1.getAction()) {
			case KeyEvent.ACTION_DOWN:
				bstop = false;
				nPTZCommand = EPTZCMD.PAN_LEFT;
				break;
			case KeyEvent.ACTION_UP:
				bstop = true;
				nPTZCommand = EPTZCMD.PAN_LEFT;
				break;
			case KeyEvent.ACTION_MULTIPLE:
				nPTZCommand = EPTZCMD.PAN_LEFT;
				bstop = Math.abs(arg1.getX()) > v.getWidth()
						|| Math.abs(arg1.getY()) > v.getHeight();
				break;
			default:
				break;
			}
			onContrlPTZ1(nPTZCommand, bstop);
			return false;
		}
	};
	private OnTouchListener onPtz_right = new OnTouchListener() {

		// @SuppressLint("ClickableViewAccessibility")
		@Override
		public boolean onTouch(View v, MotionEvent arg1) {
			boolean bstop = true;
			int nPTZCommand = -1;
			// return false;
			switch (arg1.getAction()) {
			case KeyEvent.ACTION_DOWN:
				bstop = false;
				nPTZCommand = EPTZCMD.PAN_RIGHT;
				break;
			case KeyEvent.ACTION_UP:
				bstop = true;
				nPTZCommand = EPTZCMD.PAN_RIGHT;
				break;
			case KeyEvent.ACTION_MULTIPLE:
				nPTZCommand = EPTZCMD.PAN_RIGHT;
				bstop = Math.abs(arg1.getX()) > v.getWidth()
						|| Math.abs(arg1.getY()) > v.getHeight();
				break;
			default:
				break;
			}
			onContrlPTZ1(nPTZCommand, bstop);
			return false;
		}
	};



	private void onContrlPTZ1(int nPTZCommand, boolean bStop) {
		try{
			FunSupport.getInstance().requestDevicePTZControl(mFunDevice,
					nPTZCommand, bStop, mFunDevice.CurrChannel);
		}catch (NullPointerException e){
			e.printStackTrace();
		}

	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		// TODO Auto-generated method stub
		mFunDevice.CurrChannel = arg1;
		System.out.println("TTTT----"+mFunDevice.CurrChannel);
		if (mCanToPlay) {
			playRealMedia();
		}
	}


	@Override
	public void onDeviceFileListGetFailed(FunDevice funDevice) {
		// TODO Auto-generated method stub

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

    private void initAutoCapture(){

		new Thread(){
			@Override
			public void run() {
				while (autocatureflag){
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if(autocatureflag)
					mHandler.sendEmptyMessage(MESSAGE_AUTO_CAPTURE);

				}
			}
		}.start();

	}

	protected void showWaitDialog(){

		try {
			if(mWaitDialog==null){
				mWaitDialog = new DialogWaitting(this);
				mWaitDialog.show();
			}
		}catch (Exception e){
			e.printStackTrace();
		}

	}

	protected void hideWaitDialog(){
		try {
		if(mWaitDialog!=null&&mWaitDialog.isShowing()){
			mWaitDialog.dismiss();
		}
		}catch (Exception e){
			e.printStackTrace();
		}

	}


}
