package me.hekr.sthome.commonBaseView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by Administrator on 2017/9/28.
 */

public class MyRelativeLayout extends RelativeLayout {
    public MyRelativeLayout(Context context) {
        super(context);
    }

    public MyRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /** 图片宽和高的比例 */
    private float ratio = 1.4f;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub

        // 父容器传过来的宽度方向上的模式
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        // 父容器传过来的宽度的值
        int width = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft()
                - getPaddingRight();
        // 父容器传过来的高度的值
        int height = MeasureSpec.getSize(heightMeasureSpec) - getPaddingLeft()
                - getPaddingRight();

        if (widthMode == MeasureSpec.EXACTLY || widthMode == MeasureSpec.AT_MOST) {
            // 判断条件为，宽度模式为Exactly，也就是填充父窗体或者是指定宽度；
            // 且高度模式不是Exaclty，代表设置的既不是fill_parent也不是具体的值，于是需要具体测量
            // 且图片的宽高比已经赋值完毕，不再是0.0f
            // 表示宽度确定，要测量高度
            height = (int) (width / ratio);
             heightMeasureSpec = MeasureSpec.makeMeasureSpec(height,
                      MeasureSpec.EXACTLY);

            //Log.i(TAG,"heightMeasureSpec:"+heightMeasureSpec);
        }
        //setMeasuredDimension(width, height);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
