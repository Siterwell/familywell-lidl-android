package me.hekr.sthome.commonBaseView.progress;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import me.hekr.sthome.R;

/**
 * colorful arc progress bar
 * Created by shinelw on 12/4/15.
 */

public class ColorArcProgressBar extends View
{
    private final int DEGREE_PROGRESS_DISTANCE = dipToPx(8);//弧形与外层刻度的距离

    private int mWidth;
    private int mHeight;

    private int diameter = 500;  //直径
    private float centerX;  //圆心X坐标
    private float centerY;  //圆心Y坐标

    private Paint degreePaint;

    private RectF bgRect;

    private ValueAnimator progressAnimator;
    private PaintFlagsDrawFilter mDrawFilter;
    private SweepGradient sweepGradient;//颜色渲染
    private Matrix rotateMatrix;

    private int[] colors = new int[]{Color.GREEN, Color.YELLOW, Color.RED, Color.RED};

    private float mTouchInvalidateRadius;//触摸失效半径,控件外层都可触摸,当触摸区域小于这个值的时候触摸失效

    private float startAngle   = 0;//开始角度(0°与控件X轴平行)
    private float sweepAngle   = 360;//弧形扫过的区域
    private float currentAngle = 0;
    private float lastAngle;

    private float offset = 5;
    private float maxValues     = 60;
    private float currentValues = 0;
    private float subCurrentValues = 0;
    private float progressWidth = dipToPx(7);
    private int   aniSpeed      = 200;//动画时长
    private float longDegree    = dipToPx(13);//长刻度


    private int longDegreeColor  = 0xFFFFFFFF;

    private boolean isNeedDial;

    // sweepAngle / maxValues 的值
    private float k;

    private OnSeekArcChangeListener listener;

    private boolean seekEnable;

    public ColorArcProgressBar(Context context)
    {
        super(context, null);
        initView();
    }

    public ColorArcProgressBar(Context context, AttributeSet attrs)
    {
        super(context, attrs, 0);
        initConfig(context, attrs);
        initView();
    }

    public ColorArcProgressBar(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        initConfig(context, attrs);
        initView();
    }

    /**
     * 初始化布局配置
     *
     * @param context
     * @param attrs
     */
    private void initConfig(Context context, AttributeSet attrs)
    {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ColorArcProgressBar);

        int color1 = a.getColor(R.styleable.ColorArcProgressBar_front_color1, Color.GREEN);
        int color2 = a.getColor(R.styleable.ColorArcProgressBar_front_color2, color1);
        int color3 = a.getColor(R.styleable.ColorArcProgressBar_front_color3, color1);

        colors = new int[]{color1, color2, color3, color3};
        longDegreeColor = context.getResources().getColor(R.color.text_color_selected);
        sweepAngle = a.getInteger(R.styleable.ColorArcProgressBar_sweep_angle, 270);
        progressWidth = a.getDimension(R.styleable.ColorArcProgressBar_front_width, dipToPx(10));

        seekEnable = a.getBoolean(R.styleable.ColorArcProgressBar_is_seek_enable, false);
        isNeedDial = a.getBoolean(R.styleable.ColorArcProgressBar_is_need_dial, false);

        currentValues = a.getFloat(R.styleable.ColorArcProgressBar_current_value, 5);
        subCurrentValues = a.getFloat(R.styleable.ColorArcProgressBar_sub_current_value,10);
        maxValues = a.getFloat(R.styleable.ColorArcProgressBar_max_value, 60);
        offset = a.getFloat(R.styleable.ColorArcProgressBar_min_value,5);
        setCurrentValues(currentValues);
        setMaxValues(maxValues);
        setSubCurrentValues(subCurrentValues);
        a.recycle();

    }

    //传入参数widthMeasureSpec、heightMeasureSpec 使其形状为正方形
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }



    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        Log.v("ColorArcProgressBar", "onSizeChanged: mWidth:" + mWidth + " mHeight:" + mHeight);

        diameter = (int) (Math.min(mWidth, mHeight) - 2 * (longDegree + DEGREE_PROGRESS_DISTANCE + progressWidth / 2));

        Log.v("ColorArcProgressBar", "onSizeChanged: diameter:" + diameter);

        //弧形的矩阵区域
        bgRect = new RectF();
        bgRect.top = longDegree + DEGREE_PROGRESS_DISTANCE + progressWidth / 2;
        bgRect.left = longDegree + DEGREE_PROGRESS_DISTANCE + progressWidth / 2;
        bgRect.right = diameter + (longDegree + progressWidth / 2 + DEGREE_PROGRESS_DISTANCE);
        bgRect.bottom = diameter + (longDegree + progressWidth / 2 + DEGREE_PROGRESS_DISTANCE);

        Log.v("ColorArcProgressBar", "initView: " + diameter);

        //圆心
        centerX = (2 * (longDegree + DEGREE_PROGRESS_DISTANCE + progressWidth / 2) + diameter) / 2;
        centerY = (2 * (longDegree + DEGREE_PROGRESS_DISTANCE + progressWidth / 2) + diameter) / 2;

        sweepGradient = new SweepGradient(centerX, centerY, colors, null);

        mTouchInvalidateRadius = Math.max(mWidth, mHeight) / 2 - longDegree - DEGREE_PROGRESS_DISTANCE - progressWidth * 2;


    }

    private void initView()
    {

        //外部刻度线画笔
        degreePaint = new Paint();
        degreePaint.setColor(longDegreeColor);




        mDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

        rotateMatrix = new Matrix();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        //抗锯齿
        canvas.setDrawFilter(mDrawFilter);

        if(isNeedDial)
        {
            //画刻度线
            for(int i = 0; i < 72; i++)//把整个圆划分成8大份40小份
            {
                    degreePaint.setStrokeWidth(dipToPx(2));
                    degreePaint.setColor(longDegreeColor);
                    degreePaint.setAlpha(255*i/72);
                    canvas.drawLine(centerX, centerY - diameter / 2 - progressWidth / 2 - DEGREE_PROGRESS_DISTANCE, centerX, centerY - diameter / 2 - progressWidth / 2 - DEGREE_PROGRESS_DISTANCE - longDegree, degreePaint);
                //每绘制一个小刻度,旋转1/40
                canvas.rotate(5, centerX, centerY);
            }
        }



        invalidate();

    }

    /**
     * 设置最大值
     *
     * @param maxValues
     */
    public void setMaxValues(float maxValues)
    {
        this.maxValues = maxValues;
        k = sweepAngle / (maxValues-offset);

    }

    public void setOffset(float offset){
        this.offset = offset;
    }

    /**
     * 设置当前值
     *
     * @param currentValues
     */
    public void setCurrentValues(float currentValues)
    {
        if(currentValues > maxValues)
        {
            currentValues = maxValues;
        }
        if(currentValues < offset)
        {
            currentValues = offset;
        }
        this.currentValues = currentValues;
        lastAngle = currentAngle;
        setAnimation(lastAngle, (currentValues-offset) * k, aniSpeed);
    }

    public void setSubCurrentValues(float subCurrentValues){

        this.subCurrentValues = subCurrentValues;
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if(seekEnable)
        {
            this.getParent().requestDisallowInterceptTouchEvent(true);//一旦底层View收到touch的action后调用这个方法那么父层View就不会再调用onInterceptTouchEvent了，也无法截获以后的action

            switch(event.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    onStartTrackingTouch();
                    updateOnTouch(event);
                    break;
                case MotionEvent.ACTION_MOVE:
                    updateOnTouch(event);
                    break;
                case MotionEvent.ACTION_UP:
                    onStopTrackingTouch();
                    setPressed(false);
                    this.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
                case MotionEvent.ACTION_CANCEL:
                    onStopTrackingTouch();
                    setPressed(false);
                    this.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
            }
            return true;
        }
        return false;
    }


    private void onStartTrackingTouch()
    {
        if(listener != null)
        {
            listener.onStartTrackingTouch(this);
        }
    }

    private void onStopTrackingTouch()
    {
        if(listener != null)
        {
            listener.onStopTrackingTouch(this);
        }
    }




    private void updateOnTouch(MotionEvent event)
    {
        boolean validateTouch = validateTouch(event.getX(), event.getY());
        if(!validateTouch)
        {
            return;
        }
        setPressed(true);
        double mTouchAngle = getTouchDegrees(event.getX(), event.getY());

        float progress = angleToProgress(mTouchAngle);
        Log.v("ColorArcProgressBar", "updateOnTouch: " + progress);
        onProgressRefresh(progress, true);
    }

    /**
     * 判断触摸是否有效
     *
     * @param xPos x
     * @param yPos y
     * @return is validate touch
     */
    private boolean validateTouch(float xPos, float yPos)
    {
        boolean validate = false;

        float x = xPos - centerX;
        float y = yPos - centerY;

        float touchRadius = (float) Math.sqrt(((x * x) + (y * y)));//触摸半径

        double angle = Math.toDegrees(Math.atan2(y, x) + (Math.PI / 2) - Math.toRadians(225));

        if(angle < 0)
        {
            angle = 360 + angle;
        }
//

        if(touchRadius > mTouchInvalidateRadius && (angle >= 0 && angle <= 280))//其实角度小于270就够了,但是弧度换成角度是不精确的,所以需要适当放大范围,不然有时候滑动不到最大值
        {
            validate = true;
        }

        Log.v("ColorArcProgressBar", "validateTouch: " + angle);
        return validate;
    }

    private double getTouchDegrees(float xPos, float yPos)
    {
        float x = xPos - centerX;//触摸点X坐标与圆心X坐标的距离
        float y = yPos - centerY;//触摸点Y坐标与圆心Y坐标的距离
        // Math.toDegrees convert to arc Angle

        //Math.atan2(y, x)以弧度为单位计算并返回点 y /x 的夹角，该角度从圆的 x 轴（0 点在其上，0 表示圆心）沿逆时针方向测量。返回值介于正 pi 和负 pi 之间。
        //触摸点与圆心的夹角- Math.toRadians(225)是因为我们希望0°从圆弧的起点开始,默认角度从穿过圆心的X轴开始
        double angle = Math.toDegrees(Math.atan2(y, x) + (Math.PI / 2) - Math.toRadians(225));

        if(angle < 0)
        {
            angle = 360 + angle;
        }
        Log.v("ColorArcProgressBar", "getTouchDegrees: " + angle);
//        angle -= mStartAngle;
        return angle;
    }

    private float angleToProgress(double angle)
    {
        float progress = 0f;
        float progress_init = (float) (valuePerDegree() * angle);
        String str = String.valueOf(valuePerDegree() * angle);//浮点变量a转换为字符串str
        int idx = str.lastIndexOf(".");//查找小数点的位置
        String strNum = str.substring(0,idx);//截取从字符串开始到小数点位置的字符串，就是整数部分
        int num = Integer.valueOf(strNum);//把整数部分通过Integer.valueof方法转换为数字

        float xiaoshu =  progress_init-(float) num;
        if(xiaoshu<0.5&&xiaoshu>=0){
            progress = num;
        }else {
            progress = num+0.5f;
        }



        progress = (progress < 0) ? 0 : progress;
        progress = (progress > (maxValues-offset)) ? (int) (maxValues - offset) : progress;
        return progress;
    }

    private float valuePerDegree()
    {
        return (maxValues-offset) / sweepAngle;
    }

    private void onProgressRefresh(float progress, boolean fromUser)
    {
        updateProgress(progress, fromUser);
    }

    private void updateProgress(float progress, boolean fromUser)
    {

        currentValues = progress+offset;

        if(listener != null)
        {
            listener.onProgressChanged(this, progress, fromUser);
        }

        currentAngle = (float) progress / (maxValues-offset) * sweepAngle;//计算划过当前的角度

        lastAngle = currentAngle;

        invalidate();
    }

    /**
     * 设置进度宽度
     *
     * @param progressWidth
     */
    public void setProgressWidth(int progressWidth)
    {
        this.progressWidth = progressWidth;
    }


    /**
     * 设置直径大小
     *
     * @param diameter
     */
    public void setDiameter(int diameter)
    {
        this.diameter = dipToPx(diameter);
    }

    /**
     * 设置是否显示外部刻度盘
     *
     * @param isNeedDial
     */
    private void setIsNeedDial(boolean isNeedDial)
    {
        this.isNeedDial = isNeedDial;
    }

    /**
     * 为进度设置动画
     *
     * @param last
     * @param current
     */
    private void setAnimation(float last, float current, int length)
    {
        progressAnimator = ValueAnimator.ofFloat(last, current);
        progressAnimator.setDuration(length);
        progressAnimator.setTarget(currentAngle);
        progressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {

            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                currentAngle = (float) animation.getAnimatedValue();
                currentValues = currentAngle / k+offset;
            }
        });
        progressAnimator.start();
    }

    public void setSeekEnable(boolean seekEnable)
    {
        this.seekEnable = seekEnable;
    }

    public void setOnSeekArcChangeListener(OnSeekArcChangeListener listener)
    {
        this.listener = listener;
    }

    /**
     * dip 转换成px
     *
     * @param dip
     * @return
     */
    private int dipToPx(float dip)
    {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dip * density + 0.5f * (dip >= 0 ? 1 : -1));
    }

    /**
     * 得到屏幕宽度
     *
     * @return
     */
    private int getScreenWidth()
    {
        WindowManager windowManager  = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    public float getCurrentValues(){
        return currentValues;
    }

    public float getSubCurrentValues(){
        return subCurrentValues;
    }

    public interface OnSeekArcChangeListener
    {

        /**
         * Notification that the progress level has changed. Clients can use the
         * fromUser parameter to distinguish user-initiated changes from those
         * that occurred programmatically.
         *
         * @param seekArc  The SeekArc whose progress has changed
         * @param progress The current progress level. This will be in the range
         *                 0..max where max was set by
         *                 {@link ColorArcProgressBar#setMaxValues(float)} . (The default value for
         *                 max is 100.)
         * @param fromUser True if the progress change was initiated by the user.
         */
        void onProgressChanged(ColorArcProgressBar seekArc, float progress, boolean fromUser);

        /**
         * Notification that the user has started a touch gesture. Clients may
         * want to use this to disable advancing the SeekBar.
         *
         * @param seekArc The SeekArc in which the touch gesture began
         */
        void onStartTrackingTouch(ColorArcProgressBar seekArc);

        /**
         * Notification that the user has finished a touch gesture. Clients may
         * want to use this to re-enable advancing the SeekBar.
         *
         * @param seekArc The SeekArc in which the touch gesture began
         */
        void onStopTrackingTouch(ColorArcProgressBar seekArc);
    }
}
