package me.hekr.sthome.commonBaseView.HistoryWidget;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import me.hekr.sthome.R;
import me.hekr.sthome.tools.UnitTools;

/**
 * Created by Administrator on 2017/9/19.
 */

public class TimerHistoryHorizonScrollView extends HorizontalScrollView {

    private final String TAG = TimerHistoryHorizonScrollView.class.getName();
    private LinearLayout linear;
    private int screenWidth;
    public static  final int TOTAL_PROCESS = 4320;
    private HistoryView historyView;
    private Handler mHandler;
    private ScrollViewListener scrollViewListener;

    public enum ScrollType{IDLE,TOUCH_SCROLL,FLING};

    /**
     * 记录当前滚动的距离
     */
    private int currentX = -9999999;
    /**
     * 当前滚动状态
     */
    private ScrollType scrollType = ScrollType.IDLE;
    /**
     * 滚动监听间隔
     */
    private final int scrollDealy = 50;


    public TimerHistoryHorizonScrollView(Context context) {
        super(context);
        intView(context);
    }

    public TimerHistoryHorizonScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        intView(context);
    }

    public TimerHistoryHorizonScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        intView(context);
    }

    private void intView(Context context){
        screenWidth = UnitTools.getScreenWidth(context);
        linear = new LinearLayout(context);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
        linear.setOrientation(LinearLayout.HORIZONTAL);
        layoutParams.width = TOTAL_PROCESS;
        linear.setLayoutParams(layoutParams);
        addView(linear);


        View view2 = new View(context);
        LinearLayout.LayoutParams layoutParams3= new LinearLayout.LayoutParams(screenWidth/2,LinearLayout.LayoutParams.MATCH_PARENT);
        view2.setBackgroundColor(getResources().getColor(R.color.empty_history));
        view2.setLayoutParams(layoutParams3);
        linear.addView(view2);

        LinearLayout.LayoutParams linearLp = new LinearLayout.LayoutParams(
                TOTAL_PROCESS, 140);
        historyView = new HistoryView(context);
        historyView.setLayoutParams(linearLp);
        linear.addView(historyView);


        View view = new View(context);
        LinearLayout.LayoutParams layoutParams2= new LinearLayout.LayoutParams(screenWidth/2,LinearLayout.LayoutParams.MATCH_PARENT);
        view.setBackgroundColor(getResources().getColor(R.color.empty_history));
        view.setLayoutParams(layoutParams2);
        linear.addView(view);


    }

    public HistoryView getHistoryView() {
        return historyView;
    }

    public void setHistoryView(HistoryView historyView) {
        this.historyView = historyView;
    }




    public interface ScrollViewListener {

        void onScrollChanged(ScrollType scrollType);

    }


    /**
     * 滚动监听runnable
     */
    private Runnable scrollRunnable = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            if(currentX == getScrollX()){
                //滚动停止  取消监听线程
                Log.d("", "停止滚动");
                scrollType = ScrollType.IDLE;
                if(scrollViewListener!=null){
                    scrollViewListener.onScrollChanged(scrollType);
                }
                mHandler.removeCallbacks(this);
                return;
            }else{
                //手指离开屏幕    view还在滚动的时候
                Log.d("", "Fling。。。。。");
                scrollType = ScrollType.FLING;
                if(scrollViewListener!=null){
                    scrollViewListener.onScrollChanged(scrollType);
                }
            }
            currentX = getScrollX();
            mHandler.postDelayed(this, scrollDealy);
        }
    };


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
                this.scrollType = ScrollType.TOUCH_SCROLL;
                scrollViewListener.onScrollChanged(scrollType);
                //手指在上面移动的时候   取消滚动监听线程
                mHandler.removeCallbacks(scrollRunnable);
                break;
            case MotionEvent.ACTION_UP:
                //手指移动的时候
                mHandler.post(scrollRunnable);
                break;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 必须先调用这个方法设置Handler  不然会出错
     *  2014-12-7 下午3:55:39
     * @author DZC
     * @return void
     * @param handler
     * @TODO
     */
    public void setHandler(Handler handler){
        this.mHandler = handler;
    }
    /**
     * 设置滚动监听
     *  2014-12-7 下午3:59:51
     * @author DZC
     * @return void
     * @param listener
     * @TODO
     */
    public void setOnScrollStateChangedListener(ScrollViewListener listener){
        this.scrollViewListener = listener;
    }





}
