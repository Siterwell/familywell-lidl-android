package me.hekr.sthome.commonBaseView;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import me.hekr.sthome.R;


/**
 *  设置栏控件
 */
public class SettingIpcItem extends RelativeLayout {

    /**切换按钮*/
    public static final int ACCESSORY_TYPE_CHECKBOX = 2;
    /**选中图标，但是不能点击*/
    public static final int ACCESSORY_TYPE_SPINNER = 3;
    /**箭头标志*/
    public static final int ACCESSORY_TYPE_TXT = 1;

    /**标题View*/
    private TextView mTitle;
    /**副标题View**/
    private TextView subTitle;
    /**概要View*/
    private TextView mSummary;
    /**分割线*/
    private View mDividerView;
    /**附加类型*/
    private int mAccessoryType;
    /**是否显示分割线*/
    private boolean mShowDivider;
    /**标题*/
    private String mTitleText;
    /**副标题*/
    private String mSubTitleText;
    /**概要文字*/
    private String mSummaryText;
    /**更新标志*/
    private CheckBox switchbox;
    /**spinner控件*/
    private Spinner spinner;
    /**seekbar控件*/
    private SeekBar seekBar;
    /**
     * @param context
     * @param attrs
     */
    public SettingIpcItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.ipc_setting_item, this, true);
        mTitle = (TextView) findViewById(R.id.title);
        subTitle = (TextView)findViewById(R.id.sub_title);
        mTitle.setSelected(true);
        subTitle.setSelected(true);
        mSummary = (TextView) findViewById(R.id.summary);
        mDividerView = findViewById(R.id.item_bottom_divider);
        switchbox =  (CheckBox)findViewById(R.id.checkbox);
        spinner = (Spinner)findViewById(R.id.spinnerCameraDefinition);
        seekBar = (SeekBar)findViewById(R.id.setupRecordPreSeekbar);
        TypedArray localTypedArray = context.obtainStyledAttributes(attrs, R.styleable.ipc_setting_info);
        setTitleText(localTypedArray.getString(R.styleable.ipc_setting_info_titleText));
        setSubTitleText(localTypedArray.getString(R.styleable.ipc_setting_info_subTitleText));
        setDetailText(localTypedArray.getString(R.styleable.ipc_setting_info_detailText));
        setAccessoryType(localTypedArray.getInt(R.styleable.ipc_setting_info_accessoryType , 1));
        setShowDivider(localTypedArray.getBoolean(R.styleable.ipc_setting_info_showDivider , true));
        localTypedArray.recycle();
    }

    /**
     * @param showDivider
     */
    public void setShowDivider(boolean showDivider) {
        mShowDivider = showDivider;
        View dividerView = mDividerView;
        dividerView.setVisibility(mShowDivider ? View.VISIBLE : View.GONE);
    }

    /**
     * 设置标题信息
     * @param text
     */
    public void setTitleText(String text) {
        mTitleText = text;
        if(text == null) {
            mTitle.setText("");
            return ;
        }
        mTitle.setText(mTitleText);
    }

    /**
     * 设置副标题信息
     * @param text
     */
    public void setSubTitleText(String text) {
        mSubTitleText = text;
        if(text == null) {
            subTitle.setText("");
            subTitle.setVisibility(View.GONE);
            return ;
        }
        subTitle.setText(mSubTitleText);
        subTitle.setVisibility(View.VISIBLE);
    }


    /**
     * @param text
     *
     */
    public void setDetailText(String text) {
        mSummaryText = text;
        if(text == null) {
            mSummary.setText("");
            mSummary.setVisibility(View.GONE);
            return ;
        }
        mSummary.setText(mSummaryText);
        mSummary.setVisibility(View.VISIBLE);
    }

    public String getDetailText(){
        return mSummaryText;
    }
    public String getmTitleText() { return mTitleText; }
    public String getmSubTitleText() {return mSubTitleText;}

    public SeekBar getSeekBar(){
        return seekBar;
    }

    public Spinner getSpinner() { return spinner;}
    /**
     *
     * @param accessoryType
     */
    private void setAccessoryType(int accessoryType) {
        if(accessoryType == ACCESSORY_TYPE_TXT) {
            mAccessoryType = ACCESSORY_TYPE_TXT;
            switchbox.setVisibility(View.GONE);
            mSummary.setVisibility(View.VISIBLE);
            seekBar.setVisibility(View.VISIBLE);
            spinner.setVisibility(View.GONE);
            return ;
        }else if(accessoryType == ACCESSORY_TYPE_CHECKBOX){
            mAccessoryType = ACCESSORY_TYPE_CHECKBOX;
            switchbox.setVisibility(View.VISIBLE);
            mSummary.setVisibility(View.GONE);
            spinner.setVisibility(View.GONE);
            seekBar.setVisibility(View.GONE);
            return;
        }else if(accessoryType == ACCESSORY_TYPE_SPINNER){
            mAccessoryType = ACCESSORY_TYPE_SPINNER;
            switchbox.setVisibility(View.GONE);
            mSummary.setVisibility(View.GONE);
            spinner.setVisibility(View.VISIBLE);
            seekBar.setVisibility(View.GONE);
            return;
        }

    }



    /**
     * 是否处于选中状态
     * @return
     */
    public boolean isChecked() {
        if(mAccessoryType == ACCESSORY_TYPE_CHECKBOX) {
            return switchbox.isChecked();
        }
        return true;
    }

    /**
     * 设置状态
     * @param checked
     */
    public void setChecked(boolean checked) {
        if(mAccessoryType != ACCESSORY_TYPE_CHECKBOX) {
            return ;
        }
        switchbox.setChecked(checked);
    }

}
