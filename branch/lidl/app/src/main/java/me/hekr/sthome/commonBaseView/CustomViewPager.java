package me.hekr.sthome.commonBaseView;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by gc-0001 on 2017/2/4.
 */
public class CustomViewPager extends ViewPager {

    private boolean isCanScroll = true;

    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScanScroll(boolean isCanScroll) {
        this.isCanScroll = isCanScroll;
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        if(isCanScroll){

            try{
                return super.onInterceptTouchEvent(arg0);
        } catch (IllegalArgumentException ex) {
               ex.printStackTrace();
                return false;
        }


        }else{
            //false  不能左右滑动
            return false;
        }
    }
}
