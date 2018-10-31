package me.hekr.sthome.common;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

/**
 * ClassName:SoftInputUtils
 * 作者：Henry on 2017/4/12 13:12
 * 邮箱：xuejunju_4595@qq.com
 * 描述:软键盘控制
 */
public class SoftInputUtils {

    /**
     * 显示软键盘，Dialog使用
     *
     * @param activity 当前Activity
     */
    public static void showSoftInput(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 隐藏软键盘
     *
     * @param activity 当前Activity
     */
    public static void hideSoftInput(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getWindow().getDecorView().getWindowToken(), 0);
    }

}
