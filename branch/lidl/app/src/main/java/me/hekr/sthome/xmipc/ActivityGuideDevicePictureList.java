package me.hekr.sthome.xmipc;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.lib.FunSDK;
import com.lib.IFunSDKResult;
import com.lib.MsgContent;
import com.lib.SDKCONST;
import com.lib.funsdk.support.FunLog;
import com.lib.funsdk.support.FunPath;
import com.lib.funsdk.support.FunSupport;
import com.lib.funsdk.support.OnFunDeviceOptListener;
import com.lib.funsdk.support.config.DevCmdOPSCalendar;
import com.lib.funsdk.support.config.OPCompressPic;
import com.lib.funsdk.support.models.FunDevice;
import com.lib.funsdk.support.models.FunFileData;
import com.lib.sdk.struct.H264_DVR_FILE_DATA;
import com.lib.sdk.struct.H264_DVR_FINDINFO;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import me.hekr.sthome.R;
import me.hekr.sthome.common.TopbarIpcSuperActivity;
import me.hekr.sthome.commonBaseView.DateSelectActivity;

/*
@class ActivityGuideDevicePictureList
@autor Administrator
@time 2017/6/29 10:01
@email xuejunju_4595@qq.com
设备图片列表,点击图片后打开原图参考->ActivityGuideDevicePicBrowser
*/
public class ActivityGuideDevicePictureList extends TopbarIpcSuperActivity implements
        AdapterView.OnItemClickListener, OnFunDeviceOptListener,IFunSDKResult,View.OnClickListener{
    private final String TAG = "PictureList";
    private FunDevice mFunDevice = null;
    private String mFileType = null;
    private H264_DVR_FINDINFO findInfo = null;
    private int pictureType = SDKCONST.SDK_File_Type.SDK_PIC_ALL;
    private List<FunFileData> mDatas = new ArrayList<FunFileData>();
    private DeviceCameraPicAdapter mDeviceCameraPicAdapter;
    private Calendar calendar;
    private boolean flag = true;        //this sign shows it is the first time get pic of this type or get more pic

    private ListView mImageList = null;
    private final int REQUEST_SELECT_DATE = 1;
    private int mHandler = -1;

    @Override
    protected void onCreateInit() {
        int devId = getIntent().getIntExtra("FUN_DEVICE_ID", 0);
        FunDevice funDevice = FunSupport.getInstance().findDeviceById(devId);
        if (null == funDevice) {
            finish();
            return;
        } else {
            mFunDevice = funDevice;
        }

        mHandler = FunSDK.RegUser(this);

        FunPath.onCreateSptTempPath(mFunDevice.getSerialNo());

        mFileType = getIntent().getStringExtra("FILE_TYPE");
        if (null == mFileType) {
            mFileType = "jpg";
        }

        calendar = Calendar.getInstance();
        textView_back.setOnClickListener(this);
        textView_title.setText(getResources().getString(R.string.device_opt_picture_list));
        imageButton_setting.setVisibility(View.VISIBLE);
        imageButton_setting.setBackgroundResource(R.mipmap.icon_date);
        imageButton_setting.setOnClickListener(this);
        mImageList = (ListView) findViewById(R.id.lv_images);
        mDeviceCameraPicAdapter = new DeviceCameraPicAdapter(this, mImageList, mFunDevice, mDatas);
        mImageList.setAdapter(mDeviceCameraPicAdapter);
        mImageList.setOnItemClickListener(this);
        mImageList.setOnScrollListener(scrolllistener);
        FunSupport.getInstance().registerOnFunDeviceOptListener(this);
        onSearchPicture(calendar.getTime(), 0);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_device_picture_list;
    }

    OnScrollListener scrolllistener = new OnScrollListener() {
		
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			// TODO Auto-generated method stub
			
		}
		
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			// TODO Auto-generated method stub
            FunLog.i("ActivityGuideDevicePictureList", "TTTT---flag = " + flag);
			if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount > 0 && flag) {
				flag = false;
				System.out.println("TTTTTT---->> Get More Picture");
				onSearchMorePicture(calendar.getTime(), 1);
                showProgressDialog(getResources().getString(R.string.wait));
			}
		}
	};


    @Override
    protected void onDestroy() {

        FunSupport.getInstance().removeOnFunDeviceOptListener(this);

        // 是否Adapter中使用的资源
        if (null != mDeviceCameraPicAdapter) {
            mDeviceCameraPicAdapter.release();
        }
        
        super.onDestroy();
    }




    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int type;
        FunFileData imageInfo = mDatas.get(position);

        if (imageInfo != null) {
            type = imageInfo.getFileType();
        } else {
            type = 0;
        }

        Intent it = null;
        if (type == SDKCONST.PicFileType.PIC_BURST_SHOOT
            || type == SDKCONST.PicFileType.PIC_TIME_LAPSE) {
            //it = new Intent(this, ActivityGuideDevicePicBrowser.class);
        } else {
            it = new Intent(this, ActivityGuideDeviceNormalPic.class);
        }
        it.putExtra("position", position);
        it.putExtra("FUN_DEVICE_ID", mFunDevice.getId());
        startActivity(it);
    }

    private void onSearchPicture(Date date, int flag) {
        showProgressDialog(getResources().getString(R.string.wait));
        findInfo = new H264_DVR_FINDINFO();
        findInfo.st_1_nFileType = pictureType;
        initSearchInfo(findInfo, date, 0, flag);
        FunSupport.getInstance().requestDeviceFileList(mFunDevice, findInfo);
    }
    
    private void onSearchMorePicture(Date date, int flag){
    	findInfo = new H264_DVR_FINDINFO();
    	findInfo.st_1_nFileType = pictureType;
    	initSearchInfo(findInfo, date, 0, flag);
    	FunSupport.getInstance().requestDeviceFileList(mFunDevice, findInfo);
    }

    private void initSearchInfo(H264_DVR_FINDINFO info, Date date, int channel,
                                int flag) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        info.st_2_startTime.st_0_dwYear = c.get(Calendar.YEAR);
        info.st_2_startTime.st_1_dwMonth = c.get(Calendar.MONTH) + 1;
        info.st_2_startTime.st_2_dwDay = c.get(Calendar.DATE);
        if (flag == 0) {
        	info.st_2_startTime.st_3_dwHour = 0;
        	info.st_2_startTime.st_4_dwMinute = 0;
        	info.st_2_startTime.st_5_dwSecond = 0;
		}else {
			info.st_2_startTime.st_3_dwHour = c.get(Calendar.HOUR_OF_DAY);
			info.st_2_startTime.st_4_dwMinute = c.get(Calendar.MINUTE);
			info.st_2_startTime.st_5_dwSecond = c.get(Calendar.SECOND);
		}
        info.st_3_endTime.st_0_dwYear = c.get(Calendar.YEAR);
        info.st_3_endTime.st_1_dwMonth = c.get(Calendar.MONTH) + 1;
        info.st_3_endTime.st_2_dwDay = c.get(Calendar.DATE);
        info.st_3_endTime.st_3_dwHour = 23;
        info.st_3_endTime.st_4_dwMinute = 59;
        info.st_3_endTime.st_5_dwSecond = 59;
        info.st_0_nChannelN0 = channel;
    }


    @Override
    public void onDeviceLoginSuccess(FunDevice funDevice) {

    }

    @Override
    public void onDeviceLoginFailed(FunDevice funDevice, Integer errCode) {

    }

    @Override
    public void onDeviceGetConfigSuccess(FunDevice funDevice, String configName, int nSeq) {
    	
    	if (configName.equals(DevCmdOPSCalendar.CONFIG_NAME)) {
			DevCmdOPSCalendar calendar = (DevCmdOPSCalendar) funDevice.getConfig(DevCmdOPSCalendar.CONFIG_NAME);
			List<String> dates = new ArrayList<>();
			for (int i = 0; i < calendar.getData().size(); i++) {
				dates.add(calendar.getData().get(i).getDispDate());
			}
			new AlertDialog.Builder(this)
			.setTitle("Dates")
			.setItems(dates.toArray(new String[dates.size()]), null)
			.setNegativeButton("OK", null)
			.show();
		}
    	mFunDevice.invalidConfig(DevCmdOPSCalendar.CONFIG_NAME);
    }

    @Override
    public void onDeviceGetConfigFailed(FunDevice funDevice, Integer errCode) {

    }

    @Override
    public void onDeviceSetConfigSuccess(final FunDevice funDevice,
    		final String configName) {

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
        Log.i(TAG,"onDeviceFileListChanged1");
    }

    private void notifyDataSetChanged() {
        if (null != mDeviceCameraPicAdapter) {
            mDeviceCameraPicAdapter.notifyDataSetChanged();
            flag = true;
        }
    }
    
    private void notifyDataSetInvalidated() {
    	if (null != mDeviceCameraPicAdapter) {
    		mDeviceCameraPicAdapter.notifyDataSetInvalidated();
            flag = true;
    	}
    }

    @Override
    public void onDeviceFileListChanged(FunDevice funDevice, H264_DVR_FILE_DATA[] datas) {
        Log.i(TAG,"onDeviceFileListChanged2");
        hideProgressDialog();

        if (null != funDevice
                && null != mFunDevice
                && funDevice.getId() == mFunDevice.getId()) {
        	if (flag) {
        		mDatas.clear();
	            for (H264_DVR_FILE_DATA data : datas) {
	                FunFileData funFileData = new FunFileData(data, new OPCompressPic());
	                mDatas.add(funFileData);
	            }
	
	            mFunDevice.mDatas = mDatas;
	            	notifyDataSetInvalidated();
        	}else {
        		 for (H264_DVR_FILE_DATA data : datas) {
 	                FunFileData funFileData = new FunFileData(data, new OPCompressPic());
 	                mDatas.add(funFileData);
 	            }
 	
 	            mFunDevice.mDatas = mDatas;
 	            	notifyDataSetChanged();
			}
        	FunFileData data = mDatas.get(mDatas.size()-1);
        	calendar.setTime(data.getBeginDate());

            if ( mDatas.size() == 0 ) {
            	Toast.makeText(this,getResources().getString(R.string.device_pic_list_empty),Toast.LENGTH_LONG).show();
            }
        }


    }

	@Override
	public int OnFunSDKResult(Message arg0, MsgContent arg1) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void onDeviceFileListGetFailed(FunDevice funDevice) {
		// TODO Auto-generated method stub
        Log.i(TAG,"onDeviceFileListGetFailed22");

        Toast.makeText(this,"No Files!!",Toast.LENGTH_LONG).show();
        hideProgressDialog();
	}


    @Override
    public void onClick(View view) {
     switch (view.getId()){
         case R.id.backBtnInTopLayout:
             finish();
             break;
         case R.id.SettingBtnInTopLayout:
             Intent intent = new Intent(this, DateSelectActivity.class);
             intent.putExtra("FUN_DEVICE_ID", mFunDevice.getId());
             intent.putExtra("Date",calendar);
             startActivityForResult(intent,REQUEST_SELECT_DATE);
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
            mDatas.clear();
            mFunDevice.mDatas = mDatas;
            notifyDataSetChanged();
            calendar.set(result_year, result_month, result_day);
            onSearchPicture(calendar.getTime(), 0);

        }

    }
}
