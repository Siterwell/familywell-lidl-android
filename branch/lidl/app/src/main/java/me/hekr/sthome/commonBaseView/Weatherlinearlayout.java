package me.hekr.sthome.commonBaseView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import me.hekr.sthome.R;

/**
 * Created by gc-0001 on 2017/4/21.
 */

public class Weatherlinearlayout extends LinearLayout {

    private static final String TAG = "Weatherlinearlayout";
    private TextView txt_gps,txt_weather,txt_humidy,txt_temp;
    private ImageView img_weather;

    public Weatherlinearlayout(Context context) {
        super(context);
        init(context);
    }

    public Weatherlinearlayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Weatherlinearlayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }
    private void init(Context context){
        txt_gps    = (TextView)findViewById(R.id.gps);
        txt_humidy = (TextView)findViewById(R.id.HShow);
        txt_temp   = (TextView)findViewById(R.id.TShow);
        txt_weather= (TextView)findViewById(R.id.weater_txt);
        img_weather =(ImageView)findViewById(R.id.weather);

    }


    public void setImgeWeather(boolean flag){
        img_weather.setVisibility(flag?View.VISIBLE:View.INVISIBLE);
    }

    public void setTxt_gps(String flag){
        txt_gps.setText(flag);
    }




}
