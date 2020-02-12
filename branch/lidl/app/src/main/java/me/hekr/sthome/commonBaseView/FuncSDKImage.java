package me.hekr.sthome.commonBaseView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import me.hekr.sthome.R;

/**
 * Created by gc-0001 on 2017/4/21.
 */

public class FuncSDKImage extends FrameLayout {

    private static final String TAG = "FuncSDKImage";
    private FiterImageView imageView_src;
            private ImageView imageView_play;

    public FuncSDKImage(Context context) {
        super(context);
        init(context);
    }

    public FuncSDKImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FuncSDKImage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }
    private void init(Context context){
        imageView_src = new FiterImageView(context);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        imageView_src.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView_src.setLayoutParams(layoutParams);
        addView(imageView_src);

        imageView_play = new ImageView(context);
        LayoutParams layoutParams2 = new LayoutParams(getResources().getDimensionPixelSize(R.dimen.home_play_size),getResources().getDimensionPixelSize(R.dimen.home_play_size));
        layoutParams2.gravity = Gravity.CENTER;
        imageView_play.setLayoutParams(layoutParams2);
        imageView_play.setImageResource(R.drawable.play);
        addView(imageView_play);

    }


    public void setImageResource(int resource){
        imageView_src.setImageResource(resource);
        imageView_play.setVisibility(View.VISIBLE);
    }

    public void setImageResourceNoMark(int resource){
        imageView_src.setImageResource(resource);
        imageView_play.setVisibility(View.GONE);
    }


    public  void setImageBitmap(Bitmap imageBitmap){
        imageView_src.setImageBitmap(imageBitmap);
        imageView_play.setVisibility(View.VISIBLE);
    }
//    private Bitmap createBitmap(Bitmap src, Bitmap watermark )
//    {
//
//        String tag = "createBitmap";
//
//        Log.i(tag, "create a new bitmap");
//
//        if (src == null)
//        {
//            return null;
//
//        }
//
//        int w = src.getWidth();
//
//        int h = src.getHeight();
//
//        int ww = watermark.getWidth();
//
//
//        int wh = watermark.getHeight();
//
//        float suofang = (float)  w/(ww * 4);
//
//        Matrix matrix = new Matrix();
//        matrix.postScale(suofang,suofang);//第一个参数是x方向缩放比例，第二个参数是Y方向的缩放比例
//        Bitmap bitmap = Bitmap.createBitmap(watermark, 0, 0, watermark.getWidth(), watermark.getHeight(), matrix, true);//得到最终的bitmap结果
//
////create the new blank bitmap
//
//        Bitmap newb = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);//创建一个新的和SRC长度宽度一样的位图
//
//        Canvas cv = new Canvas(newb);
//
////draw src into
//
//        cv.drawBitmap(src, 0, 0, null);//在 0，0坐标开始画入src
//
////draw watermark into
//
//        cv.drawBitmap(bitmap, w/2-bitmap.getWidth()/2, h/2-bitmap.getHeight()/2, null);//在src的右下角画入水印
//
////save all clip
//
//        cv.save(Canvas.ALL_SAVE_FLAG);//保存
//
////store
//
//        cv.restore();//存储
//        bitmap.recycle();
//        return newb;
//         }
//	/** 图片宽和高的比例 */
	/** 图片宽和高的比例 */
private float ratio = 1.4f;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub

        // 父容器传过来的宽度方向上的模式
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        // 父容器传过来的高度方向上的模式
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        // 父容器传过来的宽度的值
        int width = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft()
                - getPaddingRight();
        // 父容器传过来的高度的值
        int height = MeasureSpec.getSize(heightMeasureSpec) - getPaddingLeft()
                - getPaddingRight();

        if (widthMode == MeasureSpec.EXACTLY
                && heightMode != MeasureSpec.EXACTLY && ratio != 0.0f) {
            // 判断条件为，宽度模式为Exactly，也就是填充父窗体或者是指定宽度；
            // 且高度模式不是Exaclty，代表设置的既不是fill_parent也不是具体的值，于是需要具体测量
            // 且图片的宽高比已经赋值完毕，不再是0.0f
            // 表示宽度确定，要测量高度
            height = (int) (width / ratio);
             heightMeasureSpec = MeasureSpec.makeMeasureSpec(height,
                      MeasureSpec.EXACTLY);

            //Log.i(TAG,"heightMeasureSpec:"+heightMeasureSpec);
        } else if (widthMode != MeasureSpec.EXACTLY
                && heightMode == MeasureSpec.EXACTLY && ratio != 0.0f) {
            // 判断条件跟上面的相反，宽度方向和高度方向的条件互换
            // 表示高度确定，要测量宽度
            width = (int) (height * ratio + 0.5f);

             widthMeasureSpec = MeasureSpec.makeMeasureSpec(width,
                     MeasureSpec.EXACTLY);
        }else{
            height = (int) (width / ratio);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height,
                              MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }



}
