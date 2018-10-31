package me.hekr.sthome.commonBaseView;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.widget.ImageView;

public class FiterImageView extends ImageView implements OnGestureListener{

	private GestureDetector gestureDetector;
	
	public FiterImageView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		gestureDetector = new GestureDetector(context, this);
	}

	public FiterImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		gestureDetector = new GestureDetector(context, this);
	}

	public FiterImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		gestureDetector = new GestureDetector(context, this);
	}
	
	  @Override
	    public boolean onTouchEvent(MotionEvent event) {
	        //在cancel里将滤镜取消，注意不要捕获cacncel事件,mGestureDetector里有对cancel的捕获操作
	         //在滑动GridView时，AbsListView会拦截掉Move和UP事件，直接给子控件返回Cancel
	        if(event.getActionMasked()== MotionEvent.ACTION_CANCEL || event.getActionMasked()== MotionEvent.ACTION_UP){
	            removeFilter();
	        }
	        return gestureDetector.onTouchEvent(event);
	    }

	@Override
	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
		setFilter();
		return true;
	}

	@Override
	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		performLongClick();
	}

	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		// TODO Auto-generated method stub
		removeFilter();
		performClick();
		return false;
	}

	private void setFilter(){
		//先获取图片资源
		Drawable drawble = getDrawable();
		
		//若没有图片资源，则把背景设为drawble
		if(drawble==null)
		{
			drawble = getBackground();
		}
		
		// 设置灰色滤镜（微信效果）
		if(drawble!=null)
		{
			drawble.setColorFilter(Color.GRAY, Mode.MULTIPLY);
		}
	}
	
	private void removeFilter(){
		//先获取图片资源
		Drawable drawble = getDrawable();
		
		//若没有图片资源，则把背景设为drawble
		if(drawble==null)
		{
			drawble = getBackground();
		}
		
		// 取消灰色滤镜（微信效果）
		if(drawble!=null)
		{
			drawble.clearColorFilter();
		}
		
	}
	
}
