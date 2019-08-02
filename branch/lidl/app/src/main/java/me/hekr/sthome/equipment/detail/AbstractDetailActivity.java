package me.hekr.sthome.equipment.detail;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.hekr.sthome.commonBaseView.ECAlertDialog;
import me.hekr.sthome.commonBaseView.ECListDialog;
import me.hekr.sthome.commonBaseView.MultiDirectionSlidingDrawer;
import me.hekr.sthome.commonBaseView.ProgressDialog;
import me.hekr.sthome.commonBaseView.PullListView;
import me.hekr.sthome.history.HistoryAdapter;
import me.hekr.sthome.model.modelbean.EquipmentBean;
import me.hekr.sthome.model.modelbean.NoticeBean;
import me.hekr.sthome.model.modeldb.EquipDAO;
import me.hekr.sthome.tools.SendEquipmentData;
import me.hekr.sthome.tools.SystemTintManager;

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
    protected MultiDirectionSlidingDrawer drawer;
    protected TextView textView_log;
    protected PullListView listView;
    protected ImageView imageView_cancel;
    protected Button btn_clear;
    protected List<NoticeBean> noticeBeanList;
    protected List<NoticeBean> noticeBeanFilterList;
    protected ProgressDialog progressDialog;
    protected HistoryAdapter historyAdapter;
    protected int page;
    protected View empty;
    protected SystemTintManager tintManager;
    protected ECListDialog ecListDialog;
    protected ArrayList<String> itemslist = new ArrayList<String>();

    protected void hideStatus() {
        signal.setVisibility(View.INVISIBLE);
        quatity.setVisibility(View.INVISIBLE);
        battay_text.setVisibility(View.INVISIBLE);
    }

}
