
package me.hekr.sthome.commonBaseView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import me.hekr.sthome.R;
import me.hekr.sthome.wheelwidget.helper.Common;

/*
@class PorterDuffXfermodeView
@autor Administrator
@time 2017/9/4 11:13
@email xuejunju_4595@qq.com
*/
public class PorterDuffXfermodeView extends View {

    private static final int WAVE_TRANS_SPEED = 4;
    private static final int WAVE_PAINT_COLOR = 0xffffffff;
    // y = Asin(wx+b)+h
    private static final float STRETCH_FACTOR_A = 20;
    private static final int OFFSET_Y = 0;
    // dds
    private static final int TRANSLATE_X_SPEED_ONE = 7;
    private static final int TRANSLATE_X_SPEED_TWO = 5;
    private float mCycleFactorW;
    
    private int mTotalWidth, mTotalHeight;

    private PorterDuffXfermode mPorterDuffXfermode;
    private Bitmap mMaskBitmap;
    private Rect mMaskSrcRect, mMaskDestRect;
    private PaintFlagsDrawFilter mDrawFilter;

    private float[] mYPositions;
    private float[] mResetOneYPositions;
    private float[] mResetTwoYPositions; 
    private int mXOffsetSpeedOne;
    private int mXOffsetSpeedTwo;
    private int mXOneOffset;
    private int mXTwoOffset;
    private Paint mWavePaint;
    private float percent = 0l;
    
    public PorterDuffXfermodeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initBitmap(context);
        mPorterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.setDrawFilter(mDrawFilter);
        canvas.drawColor(Color.TRANSPARENT);
        resetPositonY();
        int sc = canvas.saveLayer(0, 0, mTotalWidth, mTotalHeight, null, Canvas.ALL_SAVE_FLAG);
        for (int i = 0; i < mTotalWidth; i++) {
            canvas.drawLine(i, mTotalHeight - mResetOneYPositions[i]-mTotalHeight*percent, i,
                    mTotalHeight,
                    mWavePaint);
            canvas.drawLine(i, mTotalHeight - mResetTwoYPositions[i]-mTotalHeight*percent, i,
                    mTotalHeight,
                    mWavePaint);
        }

        mXOneOffset += mXOffsetSpeedOne;
        mXTwoOffset += mXOffsetSpeedTwo;
        if (mXOneOffset >= mTotalWidth) {
            mXOneOffset = 0;
        }
        if (mXTwoOffset > mTotalWidth) {
            mXTwoOffset = 0;
        }

        




        mWavePaint.setXfermode(mPorterDuffXfermode);

        canvas.drawBitmap(mMaskBitmap, mMaskSrcRect, mMaskDestRect,
        		mWavePaint);
        mWavePaint.setXfermode(null);
        canvas.restoreToCount(sc);
        postInvalidate();
    }


    private void initBitmap(Context context) {

        mXOffsetSpeedOne = Common.toPx(context, TRANSLATE_X_SPEED_ONE);
        mXOffsetSpeedTwo =  Common.toPx(context, TRANSLATE_X_SPEED_TWO);


        mWavePaint = new Paint();

        mWavePaint.setAntiAlias(true);
        mWavePaint.setStyle(Style.FILL);
        mWavePaint.setColor(WAVE_PAINT_COLOR);
        mDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        mMaskBitmap = ((BitmapDrawable) getResources().getDrawable(
                R.mipmap.circle_mask))
                .getBitmap();
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mTotalWidth = w;
        mTotalHeight = h;

        Log.i("ceshi","mTotalHeight:"+mTotalHeight);
        int maskWidth = mMaskBitmap.getWidth();
        int maskHeight = mMaskBitmap.getHeight();
        mMaskSrcRect = new Rect(0, 0, maskWidth, maskHeight);
        mMaskDestRect = new Rect(0, 0, mTotalWidth, mTotalHeight);
        

        mYPositions = new float[mTotalWidth];

        mResetOneYPositions = new float[mTotalWidth];
        mResetTwoYPositions = new float[mTotalWidth];

        mCycleFactorW = (float) (2 * Math.PI / mTotalWidth);

        for (int i = 0; i < mTotalWidth; i++) {
            mYPositions[i] = (float) (STRETCH_FACTOR_A * Math.sin(mCycleFactorW * i) + OFFSET_Y);
        }
        
    }

    private void resetPositonY() {
        int yOneInterval = mYPositions.length - mXOneOffset;
        System.arraycopy(mYPositions, mXOneOffset, mResetOneYPositions, 0, yOneInterval);
        System.arraycopy(mYPositions, 0, mResetOneYPositions, yOneInterval, mXOneOffset);

        int yTwoInterval = mYPositions.length - mXTwoOffset;
        System.arraycopy(mYPositions, mXTwoOffset, mResetTwoYPositions, 0,
                yTwoInterval);
        System.arraycopy(mYPositions, 0, mResetTwoYPositions, yTwoInterval, mXTwoOffset);
    }

    public void setPercent(float percent){
        this.percent = percent;
        invalidate();
    }

    public float getPercent() {
        return percent;
    }
}
