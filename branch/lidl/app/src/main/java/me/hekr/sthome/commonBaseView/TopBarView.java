package me.hekr.sthome.commonBaseView;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import me.hekr.sthome.R;
import me.hekr.sthome.tools.LableTextWatcher;

/**
 * TODO: document your custom view class.
 */
public class TopBarView extends RelativeLayout implements View.OnClickListener{

    private Context mContext ;

    private RelativeLayout backgroud;
    private ImageView btn_back;
    private TextView txt_title;
    private EditText edit_title;
    private TextView txt_left;
    private TextView img_setting;//右上角图标按钮
    private TextView txt_setting;//右上角文字按钮
    private LinearLayout edit_title_lay;
    private ImageView del_img;  //清除输入内容按钮
    private int left_type = 1;   //1代表返回箭头，2代表文字
    private int title_type=1;    //1代表textview显示,2代表editview显示
    private int setting_type=1;  //1代表图标按钮显示,2代表文字按钮显示,3代表都不显示


    public TopBarView(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public TopBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    public TopBarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.top_bar, this, true);
        del_img =(ImageView)findViewById(R.id.del_name);
        btn_back = (ImageView) findViewById(R.id.goBack);
        txt_title = (TextView) findViewById(R.id.title);
        edit_title = (EditText) findViewById(R.id.title_edit);
        txt_left = (TextView)findViewById(R.id.left_txt);
        img_setting = (TextView) findViewById(R.id.detailEdit_img);
        txt_setting = (TextView) findViewById(R.id.detailEdit);
        backgroud = (RelativeLayout)findViewById(R.id.toolbar);
        edit_title_lay =(LinearLayout)findViewById(R.id.title_edit_lay);
        del_img.setOnClickListener(this);
        edit_title.addTextChangedListener(new LableTextWatcher(del_img));
    }

    public void setTopBarStatus(int title_type ,int setting_type, String title ,String setting_txt, OnClickListener onClickListener1,OnClickListener onClickListener2) {
        setTopBarStatus(1,title_type,setting_type,null,title,setting_txt,onClickListener1,onClickListener2);
    }

    public void setTopBarStatus(int title_type ,int setting_type, String title ,String setting_txt, OnClickListener onClickListener1,OnClickListener onClickListener2,int resid_color){
         if(backgroud!=null){
             backgroud.setBackgroundColor(getResources().getColor(resid_color));
         }
         setTopBarStatus(title_type,setting_type,title,setting_txt,onClickListener1,onClickListener2);
    }

    public void setTopBarStatus(int left_type, int title_type ,int setting_type,String left_txt, String title ,String setting_txt, OnClickListener onClickListener1,OnClickListener onClickListener2){
        this.left_type = left_type;
        if(left_type==1&&TextUtils.isEmpty(left_txt)){
            txt_left.setVisibility(View.GONE);
            btn_back.setVisibility(View.VISIBLE);
        }else{
            txt_left.setVisibility(View.VISIBLE);
            btn_back.setVisibility(View.GONE);
            txt_left.setText(left_txt);
        }

            this.title_type = title_type;
        if(title_type==1){
            txt_title.setVisibility(View.VISIBLE);
            edit_title_lay.setVisibility(View.GONE);
        }
        else{
            txt_title.setVisibility(View.GONE);
            edit_title_lay.setVisibility(View.VISIBLE);
        }

        this.setting_type = setting_type;

        if(setting_type==1){
            img_setting.setVisibility(View.VISIBLE);
            txt_setting.setVisibility(View.GONE);
        }
        else if(setting_type==2){
            img_setting.setVisibility(View.GONE);
            txt_setting.setVisibility(View.VISIBLE);
        }

        if(!TextUtils.isEmpty(title)) {
            txt_title.setText(title);
            edit_title.setText(title);
            del_img.setVisibility(View.VISIBLE);
            edit_title.setSelection(edit_title.getText().length());
        }

        if(!TextUtils.isEmpty(setting_txt)){
            txt_setting.setText(setting_txt);
        }
        if(onClickListener1==null){
            btn_back.setVisibility(View.GONE);
            txt_left.setVisibility(View.GONE);
        }
        else{
            btn_back.setOnClickListener(onClickListener1);
            txt_left.setOnClickListener(onClickListener1);
        }
        if(onClickListener2==null){
            img_setting.setVisibility(View.GONE);
            txt_setting.setVisibility(View.GONE);
        }
        else{
            img_setting.setOnClickListener(onClickListener2);
            txt_setting.setOnClickListener(onClickListener2);
        }
    }

    public void setTextTitle(String title){
            txt_title.setText(title);
    }

    public EditText getEditTitle(){
        return edit_title;
    }

    public ImageView getDelView(){ return del_img;}

    public ImageView getBackView(){ return btn_back;}

    public void setEditTitle(String title){

            edit_title.setText(title);
            edit_title.setSelection(title.length());
    }

    public void setSettingTxt(String title){
        txt_setting.setText(title);
    }


    public void setSettingBg(int res){
        img_setting.setBackgroundResource(res);
    }

    public TextView getSettingTxt(){
        return txt_setting;
    }

    public void setTitleOnclickListener(OnClickListener l){
        txt_title.setClickable(true);
        txt_title.setOnClickListener(l);
    }

    public String getEditTitleText(){
        String str = edit_title.getText().toString().trim();

        return str;

    }

    @Override
    public void onClick(View v) {
         if(v.getId()== R.id.del_name){
             edit_title.setText("");
         }
    }

    public void setTopLeftStatus(int type,String name){

        if(type==1){
            txt_left.setVisibility(View.GONE);
            btn_back.setVisibility(View.VISIBLE);
        }else{
            txt_left.setVisibility(View.VISIBLE);
            btn_back.setVisibility(View.GONE);
            txt_left.setText(name);
        }

    }
}
