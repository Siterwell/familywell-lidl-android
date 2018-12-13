package me.hekr.sthome.xmipc;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lib.SDKCONST;
import com.lib.funsdk.support.FunError;
import com.lib.funsdk.support.FunPath;
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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import me.hekr.sthome.R;
import me.hekr.sthome.common.CCPAppManager;
import me.hekr.sthome.common.DateUtil;
import me.hekr.sthome.commonBaseView.CCPButton;
import me.hekr.sthome.commonBaseView.DateSelectActivity;
import me.hekr.sthome.commonBaseView.ECAlertDialog;
import me.hekr.sthome.commonBaseView.ECAlertDialog_ipc;
import me.hekr.sthome.commonBaseView.FiterImageView;
import me.hekr.sthome.commonBaseView.HistoryWidget.TimerHistoryHorizonScrollView;
import me.hekr.sthome.commonBaseView.ProgressDialog;
import me.hekr.sthome.model.modelbean.MonitorBean;
import me.hekr.sthome.tools.LOG;
import me.hekr.sthome.tools.SystemTintManager;
import me.hekr.sthome.tools.UnitTools;

/**
 * Created by Administrator on 2017/9/19.
 */

public class ActivityGuideDeviceRecordListNew extends AppCompatActivity implements View.OnClickListener,OnFunDeviceRecordListener
        ,OnFunDeviceOptListener,MediaPlayer.OnPreparedListener,MediaPlayer.OnCompletionListener
        ,MediaPlayer.OnErrorListener,TimerHistoryHorizonScrollView.ScrollViewListener {


    private static String TAG = ActivityGuideDeviceRecordListNew.class.getName();
    private TimerHistoryHorizonScrollView scrollView;
    private List<Map<String,Object>> playlist;
    private List<FunFileData> files;
    private FunFileData currfiles;
    private SystemTintManager systemTintManager;
    private final int REQUEST_SELECT_DATE  = 1;
    private final int MESSAGE_AUTO_HIDE_CONTROL_BAR = 0x103;
    private final int MESSAGE_TOAST_SCREENSHOT_PREVIEW = 0x104;
    private final int MESSAGE_SETVIDEOAT = 0x105;
    private final int MESSAGE_SETVIDEOIN = 0x106;
    private final int MESSAGE_REFRESH_PROGRESS = 0x107;
    private FunDevice mFunDevice = null;
    private Calendar calendar;
    private TextView textView_back;
    private TextView textView_title;
    private TextView textView_loading;
    private TextView textView_time;
    private TextView textView_devname;
    private ImageButton settingBtn;
    private RelativeLayout root;
    private RelativeLayout mLayoutVideoWnd = null;
    private RelativeLayout mLayoutTop = null;
    private RelativeLayout mLayoutPlayer = null;
    private FunVideoView mVideoView = null;
    private ProgressDialog mProgressDialog;
    private LinearLayout mLayoutControls = null;
    private LinearLayout mLayoutControls2 = null;
    private RelativeLayout mLayoutBottom = null;
    private CCPButton ccpButton_play,ccpButton_mute,ccpButton_snap,ccpButton_fullsreen;
    private CCPButton ccpButton_play2,ccpButton_mute2,ccpButton_snap2,ccpButton_fullsreen2;
    private Toast mToast = null;
    private boolean srcoll_status = false; //若为true表示进度条在被手指滑动
    private float curr = -1f;  //若为true表示进度条在中间；并且不是同一段录像
    private LinearLayout linearLayout_video;
    private HorizontalScrollView horizontalScrollView_video;
    private AtomicBoolean isSounded = new AtomicBoolean(true);
    private final SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
    private int PER_ITEM_SCROLL_WIDTH = 0;
    private ECAlertDialog ecAlertDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_record_list_horizon);
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

        calendar = Calendar.getInstance();
        mLayoutTop = (RelativeLayout)findViewById(R.id.layoutTop);
        root = (RelativeLayout)findViewById(R.id.root);
        mLayoutVideoWnd = (RelativeLayout)findViewById(R.id.layoutPlayWnd);
        mLayoutPlayer = (RelativeLayout)findViewById(R.id.player);
        playlist = new ArrayList<>();
        files = new ArrayList<FunFileData>();
        systemTintManager = new SystemTintManager(this);
        mVideoView = (FunVideoView)findViewById(R.id.funRecVideoView);
        mVideoView.setOnPreparedListener(this);
        mVideoView.setOnErrorListener(this);
        mVideoView.setOnCompletionListener(this);
        mVideoView.setOnTouchListener(new VideoToucher());
        scrollView = (TimerHistoryHorizonScrollView)findViewById(R.id.scroll_view);
        findViewById(R.id.SettingBtnInTopLayout).setVisibility(View.GONE);
        textView_back = (TextView)findViewById(R.id.backBtnInTopLayout);
        textView_title = (TextView)findViewById(R.id.textViewInTopLayout);
        textView_loading = (TextView)findViewById(R.id.loading);
        textView_time   = (TextView)findViewById(R.id.luxiangtime);
        textView_devname = (TextView)findViewById(R.id.devname);
        textView_time.setVisibility(View.GONE);
        textView_loading.setVisibility(View.GONE);
        textView_back.setOnClickListener(this);
        textView_title.setText(getResources().getString(R.string.device_opt_record_list));
        settingBtn = (ImageButton)findViewById(R.id.SettingBtnInTopLayout);
        settingBtn.setVisibility(View.VISIBLE);
        settingBtn.setBackgroundResource(R.mipmap.icon_date);
        settingBtn.setOnClickListener(this);
        scrollView.setHandler(handler);
        scrollView.setOnScrollStateChangedListener(this);

        mLayoutControls = (LinearLayout) findViewById(R.id.layoutVideoControl);
        mLayoutControls2 = (LinearLayout) findViewById(R.id.layoutVideoControl2);
        mLayoutBottom = (RelativeLayout)findViewById(R.id.bottomthing);
        horizontalScrollView_video = (HorizontalScrollView)findViewById(R.id.lsit);
        linearLayout_video  = (LinearLayout)findViewById(R.id.lv);
        ccpButton_play = (CCPButton) findViewById(R.id.btnPlay);
        ccpButton_mute= (CCPButton) findViewById(R.id.btnvoice);
        ccpButton_snap= (CCPButton) findViewById(R.id.btnCapture);
        ccpButton_fullsreen= (CCPButton) findViewById(R.id.btnScreenRatio);
        ccpButton_play2 = (CCPButton) findViewById(R.id.btnPlay2);
        ccpButton_mute2= (CCPButton) findViewById(R.id.btnvoice2);
        ccpButton_snap2= (CCPButton) findViewById(R.id.btnCapture2);
        ccpButton_fullsreen2= (CCPButton) findViewById(R.id.btnScreenRatio2);
        ccpButton_play.setCCPButtonImageResource(R.mipmap.pause);
        ccpButton_mute.setCCPButtonImageResource(R.mipmap.mute);
        ccpButton_snap.setCCPButtonImageResource(R.mipmap.screenshot);
        ccpButton_fullsreen.setCCPButtonImageResource(R.mipmap.smallsreen);
        ccpButton_play2.setCCPButtonImageResource(R.mipmap.pause);
        ccpButton_mute2.setCCPButtonImageResource(R.mipmap.mute);
        ccpButton_snap2.setCCPButtonImageResource(R.mipmap.screenshot);
        ccpButton_fullsreen2.setCCPButtonImageResource(R.mipmap.fullsreen);


        ccpButton_play.setOnClickListener(this);
        ccpButton_mute.setOnClickListener(this);
        ccpButton_snap.setOnClickListener(this);
        ccpButton_fullsreen.setOnClickListener(this);
        ccpButton_play2.setOnClickListener(this);
        ccpButton_mute2.setOnClickListener(this);
        ccpButton_snap2.setOnClickListener(this);
        ccpButton_fullsreen2.setOnClickListener(this);
        PER_ITEM_SCROLL_WIDTH = 20 + UnitTools.getScreenWidth(this)/3;
        // 1. 注册录像文件搜索结果监听 - 在搜索完成后以回调的方式返回
        FunSupport.getInstance().registerOnFunDeviceRecordListener(this);
        FunSupport.getInstance().registerOnFunDeviceOptListener(this);
        showAsPortrait();
        onSearchFile();
        initnamesubmonitor();
    }

    private void initnamesubmonitor(){

        try {
            List<MonitorBean> list = CCPAppManager.getClientUser().getMonitorList();
            for(int i =0;i<list.size();i++){
                if(mFunDevice.getDevSn().equals(list.get(i).getDevid())){
                    textView_devname.setText(list.get(i).getName());
                    break;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }



    }

      Handler handler = new Handler(){
          @Override
          public void handleMessage(Message msg) {
              switch (msg.what){
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
                  case MESSAGE_SETVIDEOAT:
                  {
                      currfiles = (FunFileData)msg.obj;
                      playRecordVideoByFile(currfiles);
                  }
                      break;
                  case MESSAGE_REFRESH_PROGRESS:
                  {
                      refreshProgress();
                      resetProgressInterval();
                  }
                  break;
                  case MESSAGE_SETVIDEOIN:
                  {
                    Map<String,Object> data = (Map<String,Object>)msg.obj;
                     FunFileData currfiles2= (FunFileData)data.get("video");
                     float  curr1 = (float)data.get("current");
                      if(currfiles!=null && currfiles2.getBeginTimeStr().equals(currfiles.getBeginTimeStr())){

                              seekRecordVideo(currfiles2,curr1);
                      }else{
                          currfiles = currfiles2;
                          curr = curr1;
                          playRecordVideoByFile(currfiles2);
                      }


                  }
                  break;
              }
          }
      };




    private void intVideoHorizon(){
        linearLayout_video.removeAllViews();
        for (int i=0;i<files.size();i++){
            RelativeLayout view2 = new RelativeLayout(this);
            LinearLayout.LayoutParams layoutParams3= new LinearLayout.LayoutParams(UnitTools.getScreenWidth(this)/3, UnitTools.getScreenWidth(this)*2/9);
            layoutParams3.leftMargin = 10;
            layoutParams3.rightMargin = 10;
            view2.setLayoutParams(layoutParams3);

            linearLayout_video.addView(view2);

            FiterImageView imageView2 = new FiterImageView(this);
            RelativeLayout.LayoutParams layoutParams4= new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
            imageView2.setLayoutParams(layoutParams4);

            if(currfiles.getBeginTimeStr().equals(files.get(i).getBeginTimeStr())){
                imageView2.setImageResource(R.drawable.u3);
            }else{
                imageView2.setImageResource(R.drawable.u2);
            }


            imageView2.setTag(i);
            view2.addView(imageView2);

            TextView textView2 = new TextView(this);
            RelativeLayout.LayoutParams layoutParams5= new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
            layoutParams5.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            layoutParams5.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
            textView2.setLayoutParams(layoutParams5);
            textView2.setText(files.get(i).getBeginTimeStr());
            view2.addView(textView2);
            imageView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int d = (int)view.getTag();
                    for(int i=0;i<files.size();i++){
                        ((FiterImageView)((RelativeLayout)linearLayout_video.getChildAt(i)).getChildAt(0)).setImageResource(R.drawable.u2);
                    }

                    ((FiterImageView)view).setImageResource(R.drawable.u3);
                    Message message = new Message();
                    message.what = MESSAGE_SETVIDEOAT;
                    message.obj = files.get(d);
                    handler.sendMessageDelayed(message, 10);
                }
            });

        }


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
                ActivityGuideDeviceRecordListNew.this.sendBroadcast(intent);
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

    private void hideVideoControlBar() {
        if (mLayoutControls.getVisibility() != View.GONE) {
            TranslateAnimation ani = new TranslateAnimation(0, 0, 0, UIFactory.dip2px(this, 42));
            ani.setDuration(200);
            mLayoutControls.startAnimation(ani);
            mLayoutControls.setVisibility(View.GONE);
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
        handler.removeMessages(MESSAGE_AUTO_HIDE_CONTROL_BAR);
    }

    /**
     * 视频截图,并延时一会提示截图对话框
     */
    private void tryToCapture() {
        if (!mVideoView.isPlaying()) {
            showToast(R.string.media_capture_failure_need_playing);
            return;
        }

        final String path = mVideoView.captureImage(mFunDevice.getDevSn());	//图片异步保存
        if (!TextUtils.isEmpty(path)) {
            Message message = new Message();
            message.what = MESSAGE_TOAST_SCREENSHOT_PREVIEW;
            message.obj = path;
            handler.sendMessageDelayed(message, 400);			//此处延时一定时间等待图片保存完成后显示，也可以在回调成功后显示
        }
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
        info.st_2_startTime.st_4_dwMinute =0;
        info.st_2_startTime.st_5_dwSecond = 0;
        info.st_3_endTime.st_0_dwYear = time[0];
        info.st_3_endTime.st_1_dwMonth = time[1];
        info.st_3_endTime.st_2_dwDay = time[2];
        info.st_3_endTime.st_3_dwHour = 23;
        info.st_3_endTime.st_4_dwMinute = 59;
        info.st_3_endTime.st_5_dwSecond = 59;
        info.st_0_nChannelN0 = mFunDevice.CurrChannel;
        FunSupport.getInstance().requestDeviceFileList(mFunDevice, info);

        textView_title.setText(df.format(calendar.getTime()));

    }


    private void searchOnTimeAt(int hour,int min,int sec){

        int time[] = { calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DATE) };
        H264_DVR_FINDINFO info = new H264_DVR_FINDINFO();
        info.st_1_nFileType = SDKCONST.FileType.SDK_RECORD_ALL;
        info.st_2_startTime.st_0_dwYear = time[0];
        info.st_2_startTime.st_1_dwMonth = time[1];
        info.st_2_startTime.st_2_dwDay = time[2];
        info.st_2_startTime.st_3_dwHour = hour;
        info.st_2_startTime.st_4_dwMinute =min;
        info.st_2_startTime.st_5_dwSecond = sec;
        info.st_3_endTime.st_0_dwYear = time[0];
        info.st_3_endTime.st_1_dwMonth = time[1];
        info.st_3_endTime.st_2_dwDay = time[2];
        info.st_3_endTime.st_3_dwHour = 23;
        info.st_3_endTime.st_4_dwMinute = 59;
        info.st_3_endTime.st_5_dwSecond = 59;
        info.st_0_nChannelN0 = mFunDevice.CurrChannel;
        FunSupport.getInstance().requestDeviceFileList(mFunDevice, info);

    }


    private void seekRecordVideo(FunFileData funFileData, float curr) {
        if ( null != mVideoView && null != funFileData) {

            float startt = (float) DateUtil.getSecondInDay(funFileData.getBeginTimeStr()) * TimerHistoryHorizonScrollView.TOTAL_PROCESS /86400f;
            float endt   = (float) DateUtil.getSecondInDay(funFileData.getEndTimeStr()) * TimerHistoryHorizonScrollView.TOTAL_PROCESS /86400f;

            int seekposbyfile = (int)((curr-startt)*100f/(endt-startt));
            LOG.I(TAG,"seekRecordVideo+++++++++seekposbyfile:"+seekposbyfile);
            mVideoView.seekbyfile(seekposbyfile);
        }
    }

    private void playRecordVideoByFile(FunFileData recordFile) {
        try {
            if(recordFile!=null && recordFile.getFileData()!=null){
                mVideoView.stopPlayback();
                textView_loading.setVisibility(View.VISIBLE);
                ccpButton_play.setCCPButtonImageResource(R.mipmap.pause);
                ccpButton_play2.setCCPButtonImageResource(R.mipmap.pause);
                mVideoView.playRecordByFile(mFunDevice.getDevSn(), recordFile.getFileData(), mFunDevice.CurrChannel);
                mVideoView.setMediaSound(isSounded.get());
                int start= DateUtil.getSecondInDay(recordFile.getBeginTimeStr()) * TimerHistoryHorizonScrollView.TOTAL_PROCESS / 86400;
                scrollView.smoothScrollTo(start,0);

                for(int i=0;i<files.size();i++){
                    if(recordFile.getBeginTimeStr().equals(files.get(i).getBeginTimeStr())){
                        horizontalScrollView_video.smoothScrollTo(PER_ITEM_SCROLL_WIDTH * i,0);

                        for(int j=0;j<files.size();j++){
                            ((FiterImageView)((RelativeLayout)linearLayout_video.getChildAt(j)).getChildAt(0)).setImageResource(R.drawable.u2);
                        }

                        ((FiterImageView)((RelativeLayout)linearLayout_video.getChildAt(i)).getChildAt(0)).setImageResource(R.drawable.u3);

                        break;
                    }

                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }


    }

    @Override
    public void onClick(View view) {
         switch (view.getId()){
             case R.id.backBtnInTopLayout:
                 onBackPressed();
                 break;
             case R.id.btnScreenRatio2:
             case R.id.btnScreenRatio:
                 switchOrientation();
                 break;
             case R.id.SettingBtnInTopLayout:
                 Intent intent = new Intent(this, DateSelectActivity.class);
                 intent.putExtra("FUN_DEVICE_ID", mFunDevice.getId());
                 intent.putExtra("Date",calendar);
                 startActivityForResult(intent,REQUEST_SELECT_DATE);
                 break;
             case R.id.btnCapture:
             case R.id.btnCapture2:
                 tryToCapture();
                 break;
             case R.id.btnvoice:
             case R.id.btnvoice2:
                 if(isSounded.get()){
                     isSounded.set(false);
                     ccpButton_mute.setCCPButtonImageResource(R.mipmap.not_mute);
                     ccpButton_mute2.setCCPButtonImageResource(R.mipmap.not_mute);
                     mVideoView.setMediaSound(false);
                 }
                 else{
                     isSounded.set(true);
                     ccpButton_mute.setCCPButtonImageResource(R.mipmap.mute);
                     ccpButton_mute2.setCCPButtonImageResource(R.mipmap.mute);
                     mVideoView.setMediaSound(true);
                 }
                 break;
             case R.id.btnPlay:
             case R.id.btnPlay2:
                 if(mVideoView.isPlaying()){

                     ccpButton_play.setCCPButtonImageResource(R.mipmap.play_auto);
                     ccpButton_play2.setCCPButtonImageResource(R.mipmap.play_auto);
                     if ( null != mVideoView ) {
                         mVideoView.stopPlayback();
                     }
                 }
                 else{
                     ccpButton_play.setCCPButtonImageResource(R.mipmap.pause);
                     ccpButton_play2.setCCPButtonImageResource(R.mipmap.pause);
                     Message message = new Message();
                     message.what = MESSAGE_SETVIDEOAT;
                     message.obj = currfiles;
                     handler.sendMessageDelayed(message, 10);
                 }
                 break;

         }
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
    protected void onResume() {
        systemTintManager.setStatusBarDarkMode(true,this);
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode!=RESULT_OK) return;

        if(requestCode==REQUEST_SELECT_DATE){
            playlist.clear();
            files.clear();
            int result_year = data.getExtras().getInt("year");
            int result_month = data.getExtras().getInt("month");
            int result_day   = data.getExtras().getInt("day");

            calendar.set(result_year, result_month, result_day);
            onSearchFile();

        }

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
        mLayoutControls.setVisibility(View.VISIBLE);
        mLayoutControls2.setVisibility(View.GONE);
        mLayoutBottom.setVisibility(View.GONE);
        showVideoControlBar();
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

        // 视频显示为小窗口
        RelativeLayout.LayoutParams lpWnd = (RelativeLayout.LayoutParams) mLayoutVideoWnd.getLayoutParams();
        lpWnd.height = (int)getResources().getDimension(R.dimen.funcview_height);
        lpWnd.topMargin = UIFactory.dip2px(this, 48);
        // lpWnd.addRule(RelativeLayout.BELOW, mLayoutTop.getId());
        mLayoutVideoWnd.setLayoutParams(lpWnd);
        mLayoutControls.setVisibility(View.GONE);
        mLayoutControls2.setVisibility(View.VISIBLE);
        mLayoutBottom.setVisibility(View.VISIBLE);
    }


    private void refreshPlayInfo() {

        int startTm = mVideoView.getStartTime();
        int endTm = mVideoView.getEndTime();
        LOG.I("startTm","TTTT----" + startTm);
        LOG.I("endTm","TTTT----" + endTm);
        if (startTm > 0 && endTm > startTm) {
            resetProgressInterval();
        } else {

            cleanProgressInterval();
        }
    }

    private void resetProgressInterval() {
        if ( null != handler ) {
            handler.removeMessages(MESSAGE_REFRESH_PROGRESS);
            handler.sendEmptyMessageDelayed(MESSAGE_REFRESH_PROGRESS, 500);
        }
    }

    private void cleanProgressInterval() {
        if ( null != handler ) {
            handler.removeMessages(MESSAGE_REFRESH_PROGRESS);
        }
    }

    private void refreshProgress() {
        int posTm = mVideoView.getPosition();
        if ( posTm > 0 ) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
            LOG.I(TAG,"当前时间----" + sdf.format(new Date((long)posTm*1000)));
            if(null != textView_time){
                textView_time.setText(sdf.format(new Date((long)posTm*1000)));
            }

            if(scrollView!=null && !srcoll_status){
                scrollView.smoothScrollTo(DateUtil.getSecondInDay(sdf.format(new Date((long)posTm*1000))) * TimerHistoryHorizonScrollView.TOTAL_PROCESS / 86400,0);
            }

        }
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
        if ( null != handler ) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
        super.onDestroy();
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int extra) {
        Toast.makeText(this,getResources().getString(R.string.media_play_error)
                + " : "
                + FunError.getErrorStr(extra),Toast.LENGTH_LONG).show();
        return true;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        textView_loading.setVisibility(View.GONE);
        refreshPlayInfo();
        if(curr!=-1f){
            seekRecordVideo(currfiles,curr);
            curr = -1f;
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if(null != currfiles){
            for(int i=0;i<files.size();i++){
                if(currfiles.getBeginTimeStr().equals(files.get(i).getBeginTimeStr())){

                    if(i<(files.size()-1)){
                        currfiles = files.get(i+1);
                        playRecordVideoByFile(currfiles);
                    }

                    break;
                }
            }

        }

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
    public void onDeviceFileListChanged(FunDevice funDevice, H264_DVR_FILE_DATA[] datas) {


        if (null != funDevice
                && null != mFunDevice
                && funDevice.getId() == mFunDevice.getId()) {

            if(datas.length==0){
                hideProgressDialog();
                 if(files.size()>0){
                     horizontalScrollView_video.setVisibility(View.VISIBLE);
                     currfiles = files.get(0);
                     intVideoHorizon();
                     mLayoutPlayer.setVisibility(View.VISIBLE);
                     textView_time.setVisibility(View.VISIBLE);
                     scrollView.getHistoryView().setPlayList(playlist);
                     playRecordVideoByFile(currfiles);
                 }else{
                     Toast.makeText(this,getResources().getString(R.string.device_camera_video_list_empty),Toast.LENGTH_LONG).show();
                     mLayoutPlayer.setVisibility(View.GONE);
                     textView_time.setVisibility(View.GONE);
                     horizontalScrollView_video.setVisibility(View.GONE);
                 }

            }else{

                for (H264_DVR_FILE_DATA data : datas) {
                    FunFileData funFileData = new FunFileData(data, new OPCompressPic());
                    LOG.I(TAG,"funFileData++++++"+funFileData.toString());


                        float startt = (float)(DateUtil.getSecondInDay(funFileData.getBeginTimeStr())* TimerHistoryHorizonScrollView.TOTAL_PROCESS) /86400f;
                        float endt   = (float)(DateUtil.getSecondInDay(funFileData.getEndTimeStr()) * TimerHistoryHorizonScrollView.TOTAL_PROCESS) /86400f;


                        if("00:00:00".equals(funFileData.getEndTimeStr())){
                            endt = (float) TimerHistoryHorizonScrollView.TOTAL_PROCESS;
                        }
                    if((endt-startt)<=1 || funFileData.getFileType()>1 )  {
                        continue;
                    }

                             int type = funFileData.getFileType();
                             Map<String,Object> map = new HashMap<>();
                             map.put("start",startt);
                             map.put("end",endt);
                             map.put("type",type);
                             playlist.add(map);
                             files.add(funFileData);

                }


                    hideProgressDialog();

                    if(files.size()>0){
                    horizontalScrollView_video.setVisibility(View.VISIBLE);
                    currfiles = files.get(0);
                    intVideoHorizon();
                    mLayoutPlayer.setVisibility(View.VISIBLE);
                    textView_time.setVisibility(View.VISIBLE);
                    scrollView.getHistoryView().setPlayList(playlist);
                    playRecordVideoByFile(currfiles);
                    }else{
                        Toast.makeText(this,getResources().getString(R.string.device_camera_video_list_empty),Toast.LENGTH_LONG).show();
                        mLayoutPlayer.setVisibility(View.GONE);
                        textView_time.setVisibility(View.GONE);
                        horizontalScrollView_video.setVisibility(View.GONE);
                    }



            }



        }
    }

    @Override
    public void onDeviceFileListGetFailed(FunDevice funDevice) {

    }

    @Override
    public void onRequestRecordListSuccess(List<FunDevRecordFile> files) {

    }

    @Override
    public void onRequestRecordListFailed(Integer errCode) {
        playlist.clear();
        files.clear();
        hideProgressDialog();
        mLayoutPlayer.setVisibility(View.GONE);
        textView_time.setVisibility(View.GONE);
        horizontalScrollView_video.setVisibility(View.GONE);
        if(null!=mVideoView){
            mVideoView.stopPlayback();
        }
        if(ecAlertDialog == null || (ecAlertDialog!=null && !ecAlertDialog.isShowing())){
            ecAlertDialog = ECAlertDialog.buildPositiveAlert(this, FunError.getErrorStr(errCode), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            ecAlertDialog.show();
        }

    }

    @Override
    public void onScrollChanged(TimerHistoryHorizonScrollView.ScrollType scrollType) {
        float scrollx;

        if(scrollType != TimerHistoryHorizonScrollView.ScrollType.IDLE){
            srcoll_status = true;
            return;
        }
        srcoll_status = false;
        if(scrollView.getScrollX()<0){
            scrollx = 0f;
        }else if(scrollView.getScrollX()> TimerHistoryHorizonScrollView.TOTAL_PROCESS){
            scrollx = (float) TimerHistoryHorizonScrollView.TOTAL_PROCESS;
        }else{
            scrollx = (float)scrollView.getScrollX();
        }


        for(int i=0;i<files.size();i++){

                if(( (DateUtil.getSecondInDay(files.get(i).getBeginTimeStr())* TimerHistoryHorizonScrollView.TOTAL_PROCESS) /86400f)>scrollx){
                    Message message = new Message();
                    message.what = MESSAGE_SETVIDEOAT;
                    message.obj = files.get(i);
                    handler.sendMessageDelayed(message, 10);
                    break;
                }else if((( (float)(DateUtil.getSecondInDay(files.get(i).getBeginTimeStr())* TimerHistoryHorizonScrollView.TOTAL_PROCESS) /86400f)<=scrollx && (( (float)(DateUtil.getSecondInDay(files.get(i).getEndTimeStr())* TimerHistoryHorizonScrollView.TOTAL_PROCESS) /86400f))>=scrollx)){
                    Message message = new Message();
                    message.what = MESSAGE_SETVIDEOIN;

                    Map<String,Object> ds = new HashMap<>();
                    ds.put("video", files.get(i));
                    ds.put("current",scrollx);

                    message.obj = ds;
                    handler.sendMessageDelayed(message, 10);
                    break;
                }

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

    private void showVideoControlBar() {
        if (mLayoutControls.getVisibility() != View.VISIBLE) {
            TranslateAnimation ani = new TranslateAnimation(0, 0, UIFactory.dip2px(this, 42), 0);
            ani.setDuration(200);
            mLayoutControls.startAnimation(ani);
            mLayoutControls.setVisibility(View.VISIBLE);
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
        handler.removeMessages(MESSAGE_AUTO_HIDE_CONTROL_BAR);
        handler.sendEmptyMessageDelayed(MESSAGE_AUTO_HIDE_CONTROL_BAR, 10000);
    }




    private class VideoToucher implements View.OnTouchListener {



        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int iAction = event.getAction();
            if (iAction == MotionEvent.ACTION_UP || iAction == MotionEvent.ACTION_CANCEL) { // 弹起

                        if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                                || getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE) {
                            // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                            if (mLayoutControls.getVisibility() == View.VISIBLE) {
                                hideVideoControlBar();
                            } else {
                                showVideoControlBar();
                            }
                        }

            }
            return false;
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

}
