package com.lib.funsdk.support.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import me.hekr.sthome.R;

/**
 * Created by TracyHenry on 2018/5/25.
 */

public class FishEyeSettingPannel extends LinearLayout{

    private ImageView imageView_qiu;
    private ImageView imageView_fangkuai;
    private ImageView imageView_banyuan1;
    private ImageView imageView_banyuan2;
    private ImageView imageView_radius_fangkuai;
    private ImageView imageView_duoping;
    private ImageView imageView_shangxia;

    public FishEyeSettingPannel(Context context) {
        super(context);
        initView(context);
    }

    public FishEyeSettingPannel(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public FishEyeSettingPannel(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }


    private void initView(Context context){
        this.setOrientation(LinearLayout.HORIZONTAL);
        imageView_qiu = new ImageView(context);
        imageView_qiu.setImageDrawable(getResources().getDrawable(R.mipmap.qiu));
        addView(imageView_qiu, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        imageView_fangkuai = new ImageView(context);
        imageView_fangkuai.setImageDrawable(getResources().getDrawable(R.mipmap.fangkuai));
        addView(imageView_fangkuai, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        imageView_banyuan1 = new ImageView(context);
        imageView_banyuan1.setImageDrawable(getResources().getDrawable(R.mipmap.bantuoyuan));
        addView(imageView_banyuan1, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        imageView_banyuan2 = new ImageView(context);
        imageView_banyuan2.setImageDrawable(getResources().getDrawable(R.mipmap.bantuoyuan2));
        addView(imageView_banyuan2, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        imageView_radius_fangkuai = new ImageView(context);
        imageView_radius_fangkuai.setImageDrawable(getResources().getDrawable(R.mipmap.radius_fangkuai));
        addView(imageView_radius_fangkuai, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        imageView_duoping = new ImageView(context);
        imageView_duoping.setImageDrawable(getResources().getDrawable(R.mipmap.duoping));
        addView(imageView_duoping, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        imageView_shangxia = new ImageView(context);
        imageView_shangxia.setImageDrawable(getResources().getDrawable(R.mipmap.shangxia));
        addView(imageView_shangxia, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    }


    public ImageView getImageView_qiu() {
        return imageView_qiu;
    }

    public ImageView getImageView_fangkuai() {
        return imageView_fangkuai;
    }

    public ImageView getImageView_banyuan1() {
        return imageView_banyuan1;
    }

    public ImageView getImageView_duoping() {
        return imageView_duoping;
    }

    public ImageView getImageView_shangxia() {
        return imageView_shangxia;
    }

    public ImageView getImageView_banyuan2() {
        return imageView_banyuan2;
    }

    public ImageView getImageView_radius_fangkuai() {
        return imageView_radius_fangkuai;
    }
}
