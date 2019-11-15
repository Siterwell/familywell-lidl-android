package me.hekr.sthome.equipment.detail;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import me.hekr.sthome.commonBaseView.ECAlertDialog;
import me.hekr.sthome.commonBaseView.ECListDialog;
import me.hekr.sthome.model.modeldb.EquipDAO;
import me.hekr.sthome.tools.SendEquipmentData;

/**
 * Created by ryanhsueh on 2019/2/25
 */
public abstract class AbstractDetailActivity extends LogDetailActivity {
    protected ImageView signal,quatity,deviceLogo;
    protected TextView historyInfo, operation, emergencyCall, showStatus, silence;
    protected EquipDAO ED;
    protected ImageView back_img;
    protected TextView  edt_txt,eq_name,battay_text;
    protected ECAlertDialog alertDialog;
    protected SendEquipmentData sd;
    protected ECListDialog ecListDialog;
    protected ArrayList<String> itemslist = new ArrayList<>();

    protected void hideStatus() {
        signal.setVisibility(View.INVISIBLE);
        quatity.setVisibility(View.INVISIBLE);
        battay_text.setVisibility(View.INVISIBLE);
    }

}
