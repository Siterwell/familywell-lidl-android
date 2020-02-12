package me.hekr.sthome.commonBaseView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;

import me.hekr.sthome.R;

/**
 * Created by Administrator on 2017/8/23.
 */

public class CCPButtonGroup extends LinearLayout {
    private CCPButton ccpButton_play,ccpButton_mute,ccpButton_maliu,ccpButton_snap,ccpButton_fullsreen;

    public CCPButtonGroup(Context context) {
        super(context);
        init();
    }

    public CCPButtonGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CCPButtonGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){

        setOrientation(LinearLayout.HORIZONTAL);
        LayoutParams imageViewFLayoutParams = new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        imageViewFLayoutParams.gravity = Gravity.CENTER;
        imageViewFLayoutParams.weight = 1f;
        ccpButton_play=new CCPButton(getContext());
        ccpButton_play.setLayoutParams(imageViewFLayoutParams);
        ccpButton_play.setBackgroundResource(R.drawable.control_bg);
        ccpButton_play.setCCPButtonImageResource(R.mipmap.play_auto);

        addView(ccpButton_play);

        ccpButton_mute=new CCPButton(getContext());
        ccpButton_mute.setLayoutParams(imageViewFLayoutParams);
        ccpButton_mute.setBackgroundResource(R.drawable.control_bg);
        ccpButton_mute.setCCPButtonImageResource(R.mipmap.mute);
        addView(ccpButton_mute);

        ccpButton_maliu=new CCPButton(getContext());
        ccpButton_maliu.setLayoutParams(imageViewFLayoutParams);
        ccpButton_maliu.setBackgroundResource(R.drawable.control_bg);
        ccpButton_maliu.setCCPButtonImageResource(R.mipmap.mute);
        addView(ccpButton_maliu);

        ccpButton_snap=new CCPButton(getContext());
        ccpButton_snap.setLayoutParams(imageViewFLayoutParams);
        ccpButton_snap.setBackgroundResource(R.drawable.control_bg);
        ccpButton_snap.setCCPButtonImageResource(R.mipmap.screenshot);
        addView(ccpButton_snap);

        ccpButton_fullsreen=new CCPButton(getContext());
        ccpButton_fullsreen.setLayoutParams(imageViewFLayoutParams);
        ccpButton_fullsreen.setBackgroundResource(R.drawable.control_bg);
        ccpButton_fullsreen.setCCPButtonImageResource(R.mipmap.fullsreen);
        addView(ccpButton_fullsreen);
    }
}
