package me.hekr.sthome.xmipc;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.basic.G;
import com.lib.funsdk.support.FunPath;
import com.lib.funsdk.support.FunSupport;
import com.lib.funsdk.support.OnFunDeviceFileListener;
import com.lib.funsdk.support.config.DevCmdOPFileQueryJP;
import com.lib.funsdk.support.config.DevCmdOPSCalendar;
import com.lib.funsdk.support.config.SameDayPicInfo;
import com.lib.funsdk.support.models.FunDevice;
import com.lib.funsdk.support.models.FunFileData;

import java.io.File;

import me.hekr.sthome.R;
import me.hekr.sthome.common.TopbarSuperActivity;


public class ActivityGuideDeviceNormalPic extends TopbarSuperActivity implements View.OnClickListener, OnFunDeviceFileListener {

    private FunDevice mFunDevice = null;
    private int mPosition = 0;
    private FunFileData mImageInfo = null;
    private boolean isPreview = false;
    private boolean isFromSportCamera = false;
    private String path = FunPath.getTempPicPath();

    private ImageView mImage = null;
    private Button mBtnDownload = null;



    @Override
    protected void onCreateInit() {
        getTopBarView().setTopBarStatus(1, 1, getResources().getString(R.string.device_sport_camera_normal_pic), getResources().getString(R.string.save), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 返回/退出
                finish();
            }
        }, null);
        int devId = getIntent().getIntExtra("FUN_DEVICE_ID", 0);
        mFunDevice = FunSupport.getInstance().findDeviceById(devId);

        mPosition = getIntent().getIntExtra("position", 0);
        isPreview = getIntent().getBooleanExtra("preview", false);
        isFromSportCamera = getIntent().getBooleanExtra("fromSportCamera", false);

        mImage = (ImageView) findViewById(R.id.pic_normal_img);

        mBtnDownload = (Button) findViewById(R.id.btn_download);
        mBtnDownload.setOnClickListener(this);


        FunSupport.getInstance().registerOnFunDeviceFileListener(this);
        tryToLoadPic();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_device_normal_pic;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FunSupport.getInstance().removeOnFunDeviceFileListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_download:
                downloadToPhone();
                break;
            default:
                break;
        }
    }


    private void tryToLoadPic(){
        showWaitDialog();
        if (isPreview) {
            DevCmdOPFileQueryJP cmdOPFileQueryJP = (DevCmdOPFileQueryJP)
                    mFunDevice.getConfig(DevCmdOPFileQueryJP.CONFIG_NAME);
            mImageInfo = cmdOPFileQueryJP.getFileData().get(mPosition);
        } else if (isFromSportCamera) {
            DevCmdOPSCalendar opsCalendar = (DevCmdOPSCalendar) mFunDevice.checkConfig(DevCmdOPSCalendar.CONFIG_NAME);
            int pos = mPosition;
            for (SameDayPicInfo picInfo : opsCalendar.getData()) {
                if (pos >= 0 && pos < picInfo.getPicNum()) {
                    mImageInfo = picInfo.getPicData(pos);
                    break;
                } else if (pos >= picInfo.getPicNum()) {
                    pos -= picInfo.getPicNum();
                }
            }
        } else {
            mImageInfo = mFunDevice.mDatas.get(mPosition);
        }

        byte[] data = null;
        if (mImageInfo != null) {
            data = G.ObjToBytes(mImageInfo.getFileData());
        } else {
            showToast("Error");
            finish();
            return;
        }

        if (FunPath.isFileExists(path) >= 0) {
            FunPath.deleteFile(path);
        }

        FunSupport.getInstance().requestDeviceDownloadByFile(mFunDevice, data, path, mPosition);
    }

    private void downloadToPhone() {
        String fileName = mImageInfo.getBeginDateStr() + "_" + mImageInfo.getBeginTimeStr() + ".jpg";
        String newPath = FunPath.PATH_PHOTO + File.separator + fileName;
        if (mImageInfo != null && FunPath.isFileExists(path) > 0) {
            File oldFile = new File(path);
            File newFile = new File(newPath);
            if (oldFile.renameTo(newFile)) {
                String str = getString(R.string.device_sport_camera_download_success);
                showToast(str + FunPath.PATH_PHOTO);
            }
        } else {
            if (FunPath.isFileExists(newPath) > 0) {
                showToast(R.string.device_sport_camera_pic_existed);
            } else{
                showToast(R.string.device_sport_camera_load_first);
            }
        }
    }

    @Override
    public void onDeviceFileDownCompleted(FunDevice funDevice, String path, int nSeq) {
        hideWaitDialog();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inDither = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        mImage.setImageBitmap(bitmap);
    }

    @Override
    public void onDeviceFileDownProgress(int totalSize, int progress, int nSeq) {

    }

    @Override
    public void onDeviceFileDownStart(boolean isStartSuccess, int nSeq) {

    }
}