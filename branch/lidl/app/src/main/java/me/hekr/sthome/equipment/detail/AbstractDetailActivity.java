package me.hekr.sthome.equipment.detail;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import me.hekr.sthome.commonBaseView.ECAlertDialog;
import me.hekr.sthome.model.modelbean.EquipmentBean;
import me.hekr.sthome.model.modeldb.EquipDAO;
import me.hekr.sthome.tools.SendEquipmentData;

/**
 * Created by ryanhsueh on 2019/2/25
 */
public abstract class AbstractDetailActivity extends AppCompatActivity {
    protected ImageView signal,quatity,deviceLogo;
    protected TextView historyInfo, operation, emergencyCall, showStatus, silence;
    protected EquipmentBean device;
    protected EquipDAO ED;
    protected ImageView back_img;
    protected TextView  edt_txt,eq_name,battay_text;
    protected LinearLayout root;
    protected ECAlertDialog alertDialog;
    protected SendEquipmentData sd;

    protected void hideStatus() {
        signal.setVisibility(View.INVISIBLE);
        quatity.setVisibility(View.INVISIBLE);
        battay_text.setVisibility(View.INVISIBLE);
    }

}
