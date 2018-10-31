package me.hekr.sthome.commonBaseView;


import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import me.hekr.sthome.R;

public class CCPImageButton extends FrameLayout {
	
	public ImageView mImageView;
	public TextView mTextView;

	public CCPImageButton(Context context) {
		this(context, null, 0);
	}

	public CCPImageButton(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CCPImageButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		LayoutParams imageViewFLayoutParams = new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		imageViewFLayoutParams.gravity = Gravity.CENTER;
		mImageView = new ImageView(context);
		mImageView.setLayoutParams(imageViewFLayoutParams);

		addView(mImageView);
		LayoutParams textViewFLayoutParams = new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		textViewFLayoutParams.gravity = Gravity.CENTER;
		mTextView = new TextView(context);
		mTextView.setLayoutParams(textViewFLayoutParams);
		mTextView.setClickable(false);
		mTextView.setFocusable(false);
		mTextView.setFocusableInTouchMode(false);
		mTextView.setBackgroundResource(R.drawable.control_sub_txt_bg);
		mTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,10f);
		ColorStateList colorStateList = context.getResources().getColorStateList(R.color.monitor_control_panel_bg);
		mTextView.setTextColor(colorStateList);
		addView(mTextView);
	}
	
	
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		mImageView.setEnabled(enabled);
		mTextView.setEnabled(enabled);
	}

	public final void setImageDrawable(Drawable drawable) {
		mImageView.setImageDrawable(drawable);
		mImageView.setVisibility(View.VISIBLE);
		mTextView.setVisibility(View.GONE);
	}

	public final void setText(int resid) {
		mTextView.setText(resid);
		mTextView.setVisibility(View.VISIBLE);
		mImageView.setVisibility(View.GONE);
	}

	public final void setText(String text) {
		mTextView.setText(text);
		mTextView.setVisibility(View.VISIBLE);
		mImageView.setVisibility(View.GONE);
	}
	
	/**
	 * 
	 * @Title: setTextColor 
	 * @Description: TODO 
	 * @param @param color 
	 * @return void 
	 * @throws
	 */
	public final void setTextColor(int color) {
		mTextView.setTextColor(color);
	}
	
	public final void setTextColor(ColorStateList colors) {
		mTextView.setTextColor(colors);
	}

	public final void setImageResource(int resid) {
		Drawable drawable = getResources().getDrawable(resid);
		setImageDrawable(drawable);
	}

	/**
	 * 
	 * @Title: setTextSize 
	 * @Description: TODO 
	 * @param @param size 
	 * @return void 
	 * @throws
	 */
	public final void setTextSize(float size) {
		setTextSize(TypedValue.COMPLEX_UNIT_PX,size);
	}
	
	/**
	 * 
	 * @Title: setTextSize 
	 * @Description: TODO 
	 * @param @param unit
	 * @param @param size 
	 * @return void 
	 * @throws
	 */
	public final void setTextSize(int unit , float size) {
		mTextView.setTextSize(unit,size);
	}



}
