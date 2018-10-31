package me.hekr.sthome.commonBaseView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * ClassName:SquareLinearLayout
 * 作者：Henry on 2017/3/2 15:01
 * 邮箱：xuejunju_4595@qq.com
 * 描述:自适应正方形背景图片的linearlayout
 */
public class SquareLinearLayout extends LinearLayout {
    public SquareLinearLayout(Context context) {
        super(context);
    }

    public SquareLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //传入参数widthMeasureSpec、heightMeasureSpec 使其形状为正方形
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
