package me.hekr.sthome.commonBaseView;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;

/*
@class CCPButton
@autor Administrator
@time 2017/8/23 8:58
@email xuejunju_4595@qq.com
*/
public class CCPButton extends CCPImageButton {

	public CCPButton(Context context) {
		this(context , null , 0);
	}

	public CCPButton(Context context, AttributeSet attrs) {
		this(context, attrs , 0);
	}

	public CCPButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		init();
		
	}

	private void init() {
		LayoutParams fLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT
				, LayoutParams.WRAP_CONTENT);
		fLayoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
		
		mImageView.setLayoutParams(fLayoutParams);
	}
	

	/**
	 * 
	 * @param resId
	 */
	public final void setCCPButtonBackground(int resId) {
		if(resId < 0) {
			return;
		}
		setBackgroundResource(resId);
	}
	
	/**
	 * 
	 * @param resId
	 */
	public final void setCCPButtonImageResource(int resId) {
		Drawable drawable = getResources().getDrawable(resId);
		setCCPButtonImageDrawable(drawable);
	}
	
	public final void setCCPButtonImageDrawable(Drawable drawable) {
		if(mImageView != null) {
			mImageView.setImageDrawable(drawable);
			mTextView.setVisibility(View.GONE);
		}
	}
	
}
