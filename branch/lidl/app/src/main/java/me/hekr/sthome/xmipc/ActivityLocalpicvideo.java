package me.hekr.sthome.xmipc;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.lib.funsdk.support.FunPath;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.hekr.sthome.R;
import me.hekr.sthome.common.TopbarSuperActivity;
import me.hekr.sthome.commonBaseView.CustomViewPager;
import me.hekr.sthome.tools.SystemTintManager;

/**
 * Created by Administrator on 2017/7/17.
 */

public class ActivityLocalpicvideo extends TopbarSuperActivity implements View.OnClickListener{
    private final static String  TAG = "ActivityLocalpicvideo";
    private CustomViewPager customViewPager;
    private LinearLayout root;
    private ImageView btn_back;
    private Button btn_local_pic,btn_local_pic_line,btn_local_video,btn_local_video_line;
    private List<Fragment> fragments;// Tab页面列表
    private int currIndex=0;
    private int changeIndex;
    private ProgressDialog mProgressDialog;
    private List<Localfile> localfiles_pic = new ArrayList<>();
    private List<Localfile> localfiles_video = new ArrayList<>();
    private String path_file; //文件路径
    private int pic_or_video = 0;
    @Override
    protected void onCreateInit() {

        initview();
        InitViewPager();
        init2SystemBar();
        getImages();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_localpicvideo;
    }

    private void initview(){
        path_file = getIntent().getStringExtra("path");
        pic_or_video = getIntent().getIntExtra("pic_or_video",0);
        if(TextUtils.isEmpty(path_file)){
            Toast.makeText(this,getResources().getString(R.string.name_is_null),Toast.LENGTH_LONG).show();
            finish();
        }

        root = (LinearLayout)findViewById(R.id.root);
        getTopBarView().setVisibility(View.GONE);
        customViewPager = (CustomViewPager)findViewById(R.id.vPager);
        btn_local_pic = (Button)findViewById(R.id.show_pic);
        btn_local_pic_line = (Button)findViewById(R.id.show_pic_line);
        btn_local_video = (Button)findViewById(R.id.show_video);
        btn_local_video_line = (Button)findViewById(R.id.show_video_line);
        btn_back = (ImageView)findViewById(R.id.gg);
        btn_back.setOnClickListener(this);
        btn_local_pic.setOnClickListener(this);
        btn_local_video.setOnClickListener(this);

        if(pic_or_video == 0){
            setButton(0);
        }else{
            setButton(1);
        }
    }


    /**
     * 初始化Viewpager页
     */
    private void InitViewPager() {
        fragments = new ArrayList<Fragment>();
        LocalpicFragment localpicFragment = new LocalpicFragment();
        fragments.add(localpicFragment);
        LocalvideFragment localvideFragment = new LocalvideFragment();
        fragments.add(localvideFragment);
        customViewPager.setAdapter(new myPagerAdapter(getSupportFragmentManager(),
                fragments));
        if(pic_or_video == 0){
            customViewPager.setCurrentItem(0);
        }else{
            customViewPager.setCurrentItem(1);
        }

        customViewPager.setOnPageChangeListener(new MyOnPageChangeListener());
    }

    @Override
    public void onClick(View view) {
         switch (view.getId()){
             case R.id.gg:
                 finish();
                 break;
             case R.id.show_pic:
                 customViewPager.setCurrentItem(0,false);
                 break;
             case R.id.show_video:
                 customViewPager.setCurrentItem(1,false);
                 break;
         }
    }


    /**
     * 定义适配器
     */
    class myPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragmentList;

        public myPagerAdapter(FragmentManager fm, List<Fragment> fragmentList) {
            super(fm);
            this.fragmentList = fragmentList;
        }

        /**
         * 得到每个页面
         */
        @Override
        public Fragment getItem(int arg0) {
            return (fragmentList == null || fragmentList.size() == 0) ? null
                    : fragmentList.get(arg0);
        }

        /**
         * 每个页面的title
         */
        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }

        /**
         * 页面的总个数
         */
        @Override
        public int getCount() {
            return fragmentList == null ? 0 : fragmentList.size();
        }
    }


    protected void init2SystemBar(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            root.setFitsSystemWindows(true);//需要把根布局设置为这个属性 子布局则不会占用状态栏位置
            root.setClipToPadding(true);//需要把根布局设置为这个属性 子布局则不会占用状态栏位置
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE);
        }
        SystemTintManager tintManager = new SystemTintManager(this);// 创建状态栏的管理实例
        tintManager.setStatusBarDarkMode(true, this);//false 状态栏字体颜色是白色 true 颜色是黑色
    }


    private void setButton(int index){
        btn_local_video.setBackgroundColor(getResources().getColor(R.color.mainGround));
        btn_local_pic.setBackgroundColor(getResources().getColor(R.color.mainGround));
        btn_local_pic_line.setBackgroundColor(getResources().getColor(R.color.mainGround));
        btn_local_video_line.setBackgroundColor(getResources().getColor(R.color.mainGround));

        switch (index){
            case 0:
                btn_local_pic.setBackgroundColor(getResources().getColor(R.color.text_color_selected));
                btn_local_pic_line.setBackgroundColor(getResources().getColor(R.color.text_color_selected));
                break;
            case 1:
                btn_local_video.setBackgroundColor(getResources().getColor(R.color.text_color_selected));
                btn_local_video_line.setBackgroundColor(getResources().getColor(R.color.text_color_selected));
                break;
        }
    }


    /**
     * 为选项卡绑定监听器
     */
    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {




        public void onPageScrollStateChanged(int index) {

        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        public void onPageSelected(int index) {
            currIndex = index;
            setButton(currIndex);
        }
    }


    /**
     * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中 完成图片的扫描，最终获得jpg最多的那个文件夹
     */
    private void getImages()
    {
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED))
        {
            Toast.makeText(this, getResources().getString(R.string.no_extern_storage), Toast.LENGTH_SHORT).show();
            return;
        }
        // 显示进度条
        mProgressDialog = ProgressDialog.show(this, null, getResources().getString(R.string.loading_press));

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver mContentResolver = ActivityLocalpicvideo.this
                        .getContentResolver();
                // 只查询jpeg和png的图片
                Cursor  mCursor = mContentResolver.query(mImageUri, null,
                        "("+MediaStore.Images.Media.MIME_TYPE + "=? or "
                                + MediaStore.Images.Media.MIME_TYPE + "=?"+ ") and "
                                + MediaStore.Images.Media.DATA +" like ?",
                        new String[] { "image/jpeg", "image/png" ,"%" + FunPath.PATH_CAPTURE_TEMP+File.separator+path_file+"%"},
                        MediaStore.Images.Media.DATE_MODIFIED  + " DESC");

                Log.i(TAG, mCursor.getCount() + "");
                while (mCursor.moveToNext())
                {
                    // 获取图片的路径
                    String path = mCursor.getString(mCursor
                            .getColumnIndex(MediaStore.Images.Media.DATA));
                    String modify = mCursor.getString(mCursor
                            .getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED));
                    File flie = new File(path);
                    Localfile localfile = new Localfile();
                    localfile.setFilename(flie.getName());
                    localfile.setFilepath(path);
                    localfile.setModifytime(modify);
                    localfiles_pic.add(localfile);
                    // 拿到第一张图片的路径
                }
                mCursor.close();


                Log.i(TAG,"localfiles============"+localfiles_pic.toString());
                // 通知Handler扫描图片完成
                mHandler.sendEmptyMessage(0x110);

            }
        }).start();

    }

    /**
     * 利用ContentProvider扫描手机中的的录像
     */
    private void getVideos()
    {
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED))
        {
            Toast.makeText(this, getResources().getString(R.string.no_extern_storage), Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                Uri mImageUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                ContentResolver mContentResolver = ActivityLocalpicvideo.this
                        .getContentResolver();
                // 只查询jpeg和png的图片
                Cursor  mCursor = mContentResolver.query(mImageUri, null,
                         MediaStore.Video.Media.DATA +" like ?",
                        new String[] {  "%" + FunPath.PATH_VIDEO+File.separator+path_file+"%"},
                        MediaStore.Video.Media.DATE_MODIFIED  + " DESC");

                Log.i(TAG, mCursor.getCount() + "");
                while (mCursor.moveToNext())
                {
                    // 获取图片的路径
                    String path = mCursor.getString(mCursor
                            .getColumnIndex(MediaStore.Video.Media.DATA));
                    String modify = mCursor.getString(mCursor
                            .getColumnIndex(MediaStore.Video.Media.DATE_MODIFIED));
                    File flie = new File(path);
                    Localfile localfile = new Localfile();
                    localfile.setFilename(flie.getName());
                    localfile.setFilepath(path);
                    localfile.setModifytime(modify);
                    localfiles_video.add(localfile);
                    // 拿到第一张图片的路径
                }
                mCursor.close();


                Log.i(TAG,"localfiles============"+localfiles_video.toString());
                // 通知Handler扫描图片完成
                mHandler.sendEmptyMessage(0x111);

            }
        }).start();

    }

    private Handler mHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {

            switch (msg.what) {
                case 0x110:
                    getVideos();
                    break;
                case 0x111:
                    mProgressDialog.dismiss();
                    // 为View绑定数据
                    ((LocalpicFragment)fragments.get(0)).refresh(localfiles_pic);
                    // 为View绑定数据
                    ((LocalvideFragment)fragments.get(1)).refresh(localfiles_video);
                    break;
                default:
                    break;
            }

        }
    };

}
