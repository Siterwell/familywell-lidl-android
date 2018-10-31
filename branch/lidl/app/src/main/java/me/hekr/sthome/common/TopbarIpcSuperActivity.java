package me.hekr.sthome.common;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import me.hekr.sthome.R;
import me.hekr.sthome.commonBaseView.ProgressDialog;
import me.hekr.sthome.tools.SystemTintManager;

/**
 * Created by Administrator on 2017/8/31.
 */

public abstract class TopbarIpcSuperActivity extends AppCompatActivity {

    /**
     * 标题
     */
    private RelativeLayout mTopBarView;
    private LayoutInflater mLayoutInflater;
    private View mContentView;
    private ProgressDialog mProgressDialog;
   private  ViewGroup mRootView;
    private DialogWaitting mWaitDialog = null;
    private Toast mToast = null;
    protected TextView textView_back,textView_title;
    protected ImageButton imageButton_setting,imageButton_back;
    protected TextView textView_setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_ipc);
        init();
        onCreateInit();
    }


    protected RelativeLayout getTopBarView() {
        if(mTopBarView instanceof RelativeLayout) {
            return (RelativeLayout) mTopBarView;
        }
        return null;
    }


    private void init()  {

        int layoutId = getLayoutId();
        mRootView = (ViewGroup)findViewById(R.id.root);
        mLayoutInflater = LayoutInflater.from(this);
        mTopBarView = (RelativeLayout)findViewById(R.id.layoutTop);

        if (layoutId != -1) {
            mContentView = mLayoutInflater.inflate(getLayoutId(), null);
            mRootView.addView(mContentView, LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
        }
        textView_back = (TextView)findViewById(R.id.backBtnInTopLayout);
        imageButton_back = (ImageButton)findViewById(R.id.backBtnInTopLayout2);
        textView_title = (TextView)findViewById(R.id.textViewInTopLayout);
        imageButton_setting = (ImageButton)findViewById(R.id.SettingBtnInTopLayout);
        textView_setting = (TextView)findViewById(R.id.SettingBtnInTopLayoutText);
        imageButton_setting.setVisibility(View.GONE);
        imageButton_back.setVisibility(View.GONE);
        initSystemBar();
    }

    protected abstract void onCreateInit();

    protected abstract int getLayoutId();

    /**
     * hide inputMethod
     */
    public void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(inputMethodManager != null ) {
            View localView = this.getCurrentFocus();
            if(localView != null && localView.getWindowToken() != null ) {
                IBinder windowToken = localView.getWindowToken();
                inputMethodManager.hideSoftInputFromWindow(windowToken, 0);
            }
        }
    }


    protected void initSystemBar(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            mRootView.setFitsSystemWindows(true);//需要把根布局设置为这个属性 子布局则不会占用状态栏位置
            mRootView.setClipToPadding(true);//需要把根布局设置为这个属性 子布局则不会占用状态栏位置
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE);
        }
        SystemTintManager tintManager = new SystemTintManager(this);// 创建状态栏的管理实例
        tintManager.setStatusBarDarkMode(true, this);//false 状态栏字体颜色是白色 true 颜色是黑色
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initSystemBar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSoftKeyboard();
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


    public void showWaitDialog() {
        if ( null == mWaitDialog ) {
            mWaitDialog = new DialogWaitting(this);
        }
        mWaitDialog.show();
    }

    public void showWaitDialog(int resid) {
        if ( null == mWaitDialog ) {
            mWaitDialog = new DialogWaitting(this);
        }
        mWaitDialog.show(resid);
    }

    public void showWaitDialog(String text) {
        if ( null == mWaitDialog ) {
            mWaitDialog = new DialogWaitting(this);
        }
        mWaitDialog.show(text);
    }

    public void hideWaitDialog() {
        if ( null != mWaitDialog ) {
            mWaitDialog.dismiss();
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

    public void showToast(int resid){
        if ( resid > 0 ) {
            if ( null != mToast ) {
                mToast.cancel();
            }
            mToast = Toast.makeText(this, resid, Toast.LENGTH_SHORT);
            mToast.show();
        }
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

}
