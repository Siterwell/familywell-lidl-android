package me.hekr.sthome.DragFolderwidget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * 文件滚动视图
 */
public class MyScrollView extends ScrollView {
    public MyScrollView(Context context) {
        super(context);
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            super.onTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            super.onTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
        }
        return false;
    }
}
