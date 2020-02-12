package me.hekr.sthome.commonBaseView.HistoryWidget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.hekr.sthome.R;

/**
 * Created by Administrator on 2017/9/20.
 */

public class HistoryView extends View {

    private int normalvideo_color;
    private int sensevideo_color;
    private int emptyvideo_color;
    private  final float TOTAL_PROCESS = 4320f;
    private Context context;

    private int viewheight;
    //线的宽度
    private int mStrokeWidth = 1;
    private final int mWeekSize = 14;
    private Paint paint;
    private final int NORMAL = 1;
    private final int ALARM =  0;
    private final String TAG = HistoryView.class.getName();

    private List<Map<String,Object>> list;

    public HistoryView(Context context) {
        super(context);
        initView(context);
    }

    public HistoryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public HistoryView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public void setEmptyvideo_color(int emptyvideo_color) {
        this.emptyvideo_color = emptyvideo_color;
    }

    public void setNormalvideo_color(int normalvideo_color) {
        this.normalvideo_color = normalvideo_color;
    }

    public void setSensevideo_color(int sensevideo_color) {
        this.sensevideo_color = sensevideo_color;
    }

    private void initView(Context context){
        paint = new Paint();
        this.context = context;
        list = new ArrayList<Map<String,Object>>();
        setEmptyvideo_color(context.getResources().getColor(R.color.empty_history));
        setNormalvideo_color(context.getResources().getColor(R.color.common_history));
        setSensevideo_color(context.getResources().getColor(R.color.test_history));
    }

    @Override
    protected void onDraw(Canvas canvas) {



        paint.setColor(emptyvideo_color);
        canvas.drawRect(0, 0, TOTAL_PROCESS, viewheight, paint);

        for(int i = 0;i<list.size();i++){
            //绘制矩形
            //设置画笔颜色
            float start = (float)list.get(i).get("start");

            float end   = (float)list.get(i).get("end");
            switch ((int)list.get(i).get("type")){

                case NORMAL:
                    paint.setColor(normalvideo_color);
                    canvas.drawRect(start, 0, end, viewheight, paint);
                    Log.i(TAG,"end:"+end);
                    break;

                case ALARM:
                    paint.setColor(sensevideo_color);
                    canvas.drawRect(start, 0, end, viewheight, paint);
                    break;
                default:
                    break;
            }


        }



        paint.setColor(context.getResources().getColor(R.color.black));
        paint.setStrokeWidth(2l);

        canvas.drawLine(0,viewheight/2,TOTAL_PROCESS,viewheight/2,paint);

        paint.setStrokeWidth(2l);
        for(int i = 0;i<25;i++){

            canvas.drawLine(i*TOTAL_PROCESS/24,viewheight/4,i*TOTAL_PROCESS/24,3*viewheight/4,paint);

        }

        for(int i=0;i<72;i++){
            if(i%3!=0){
                canvas.drawLine(i*TOTAL_PROCESS/72,viewheight/3,i*TOTAL_PROCESS/72,2*viewheight/3,paint);
            }
        }

        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(mWeekSize * getResources().getDisplayMetrics().scaledDensity);
        for(int i=0;i < 24;i++){
            String text = i<10?("0"+i+":00"):(i+":00");
            int startX = (int)TOTAL_PROCESS/24 * i + 5;
            int startY = viewheight - 10;
            canvas.drawText(text, startX, startY, paint);
        }



        canvas.save();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if(heightMode == MeasureSpec.EXACTLY){
            viewheight = heightSize;
        }

        setMeasuredDimension(widthSize, heightSize);
    }


    public void setPlayList(List<Map<String,Object>> list){
        this.list = list;
        invalidate();
    }

}
