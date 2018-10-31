package me.hekr.sthome.commonBaseView;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import me.hekr.sthome.R;

public class WeekDayView extends View {
	//上横线颜色
	private int mTopLineColor = Color.parseColor("#ffffff");
	//下横线颜色
	private int mBottomLineColor = Color.parseColor("#ffffff");
	//线的宽度
	private int mStrokeWidth = 1;
	private int mWeekSize = 14;
	private Paint paint;
	private DisplayMetrics mDisplayMetrics;
	private String[] weekString = getResources().getStringArray(R.array.week3);
	public WeekDayView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mDisplayMetrics = getResources().getDisplayMetrics();
		paint = new Paint();
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		
		if(heightMode == MeasureSpec.AT_MOST){
			heightSize = mDisplayMetrics.densityDpi * 30;
		}
		if(widthMode == MeasureSpec.AT_MOST){
			widthSize = mDisplayMetrics.densityDpi * 300;
		}
		setMeasuredDimension(widthSize, heightSize);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		int width = getWidth();
		int height = getHeight();
		//进行画上下线
		paint.setStyle(Style.STROKE);
		paint.setColor(mTopLineColor);
		paint.setStrokeWidth(mStrokeWidth);
		canvas.drawLine(0, 0, width, 0, paint);
		
		//画下横线
		paint.setColor(mBottomLineColor);
		canvas.drawLine(0, height, width, height, paint);
		
		paint.setStyle(Style.FILL);
		paint.setTextSize(mWeekSize * mDisplayMetrics.scaledDensity);
		int columnWidth = width / 7;
		for(int i=0;i < weekString.length;i++){
			String text = weekString[i];
			int fontWidth = (int) paint.measureText(text);
			int startX = columnWidth * i + (columnWidth - fontWidth)/2;
			int startY = (int) (height/2 - (paint.ascent() + paint.descent())/2);
			if(i == 0|| i == 6){
				paint.setColor(getResources().getColor(R.color.red));
			}else{
				paint.setColor(getResources().getColor(R.color.delist_bk));
			}
			canvas.drawText(text, startX, startY, paint);
		}
	}

	/**
	 * 设置顶线的颜色
	 * @param mTopLineColor
	 */
	public void setmTopLineColor(int mTopLineColor) {
		this.mTopLineColor = mTopLineColor;
	}

	/**
	 * 设置底线的颜色
	 * @param mBottomLineColor
	 */
	public void setmBottomLineColor(int mBottomLineColor) {
		this.mBottomLineColor = mBottomLineColor;
	}


	/**
	 * 设置边线的宽度
	 * @param mStrokeWidth
	 */
	public void setmStrokeWidth(int mStrokeWidth) {
		this.mStrokeWidth = mStrokeWidth;
	}


	/**
	 * 设置字体的大小
	 * @param mWeekSize
	 */
	public void setmWeekSize(int mWeekSize) {
		this.mWeekSize = mWeekSize;
	}


	/**
	 * 设置星期的形式
	 * @param weekString
	 * 默认值	"日","一","二","三","四","五","六"
	 */
	public void setWeekString(String[] weekString) {
		this.weekString = weekString;
	}
}
