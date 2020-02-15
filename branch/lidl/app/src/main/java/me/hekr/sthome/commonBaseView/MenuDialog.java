package me.hekr.sthome.commonBaseView;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.hekr.sthome.R;
import me.hekr.sthome.model.modelbean.SysModelBean;

/**
 * Created by gc-0001 on 2017/5/23.
 */

public class MenuDialog extends Dialog {
    private final String TAG = "MenuDialog";


    public MenuDialog(Context context) {
        super(context);
    }

    protected MenuDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public MenuDialog(Context context, int theme) {
        super(context, theme);
    }

    public static class Builder implements View.OnClickListener{
        private final static  int duratime = 800;
        private static int RADIUS = 250;
        private Context context;
        private View layout;
        private List<SysModelBean> sysModellist;
        public List<LinearLayout> linearlist  = new ArrayList<LinearLayout>();
        private List<PropertyValuesHolder> tranXAnimatinSets = new ArrayList<PropertyValuesHolder>();
        private List<PropertyValuesHolder> tranYAnimatinSets = new ArrayList<PropertyValuesHolder>();
        private List<PropertyValuesHolder> rotateAnimatinSets = new ArrayList<PropertyValuesHolder>();
        private List<PropertyValuesHolder> tranXAnimatinSets2 = new ArrayList<PropertyValuesHolder>();
        private List<PropertyValuesHolder> tranYAnimatinSets2 = new ArrayList<PropertyValuesHolder>();
        private List<PropertyValuesHolder> rotateAnimatinSets2 = new ArrayList<PropertyValuesHolder>();
        public Builder(Context context) {
            this.context = context;
            RADIUS  = (int)context.getResources().getDimension(R.dimen.home_menu_radius);
        }
        private  Dissmins dissmins;


        public void setDissmins(Dissmins dissmins) {
            this.dissmins = dissmins;
        }


        public List<SysModelBean> getSysModellist() {
            return sysModellist;
        }

        public void setSysModellist(List<SysModelBean> sysModellist) {
            this.sysModellist = sysModellist;
        }

        /**
         * Set the positive button resource and it's listener
         *
         * @return
         */


        public MenuDialog create() {

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the dialog with the custom Theme
            final MenuDialog dialog = new MenuDialog(context, R.style.Theme_Home_Menu);
            layout = inflater.inflate(R.layout.menu_mode, null);
            dialog.addContentView(layout, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            // set the dialog title

            dialog.setContentView(layout);

            initView();
            initAnimation();
            startOutAnimation();
            return dialog;
        }


        private void initView(){
               RelativeLayout relativeLayout = (RelativeLayout)layout.findViewById(R.id.mu);
//               LinearLayout  rd = (LinearLayout) layout.findViewById(R.id.boss);
//               int statusheight = UnitTools.getStatusBarHeight(context);
//               RelativeLayout.LayoutParams layoutParams_rd =  (RelativeLayout.LayoutParams)rd.getLayoutParams();
//               layoutParams_rd.topMargin = (int)context.getResources().getDimension(R.dimen.home_half_margin_top) - statusheight;
//               rd.setLayoutParams(layoutParams_rd);

               for(int i=0;i<getSysModellist().size();i++){

                   LinearLayout linearLayout = new LinearLayout(context);
                   RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT ,
                           LinearLayout.LayoutParams.WRAP_CONTENT    );
                   layoutParams.width = (int)context.getResources().getDimension(R.dimen.home_middle_icon_size);
                   layoutParams.height = (int)context.getResources().getDimension(R.dimen.home_middle_icon_size);
                   layoutParams.topMargin = (int)context.getResources().getDimension(R.dimen.home_half_margin_top);
                   linearLayout.setOrientation(LinearLayout.VERTICAL);
                   linearLayout.setGravity(Gravity.CENTER);
                   linearLayout.setLayoutParams(layoutParams);
                   linearLayout.setBackgroundResource(R.drawable.cell_menu_mode_ground);

                   ImageView imageView = new ImageView(context);
                   LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT ,
                           LinearLayout.LayoutParams.WRAP_CONTENT    );
                   layoutParams2.width = (int)context.getResources().getDimension(R.dimen.home_middle_icon_size2);
                   layoutParams2.height = (int)context.getResources().getDimension(R.dimen.home_middle_icon_size2);
                   imageView.setLayoutParams(layoutParams2);
                   if("0".equals(getSysModellist().get(i).getSid())){
                       imageView.setImageResource(R.mipmap.home_mode22);
                   }else if("1".equals(getSysModellist().get(i).getSid())){
                       imageView.setImageResource(R.mipmap.out_mode22);
                   }else if("2".equals(getSysModellist().get(i).getSid())){
                       imageView.setImageResource(R.mipmap.sleep_mode22);
                   }else {
                       imageView.setImageResource(R.mipmap.other_mode22);
                   }

                   linearLayout.addView(imageView);

                   TextView textView = new TextView(context);
                   LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT ,
                           LinearLayout.LayoutParams.WRAP_CONTENT    );
                   layoutParams3.width = (int)context.getResources().getDimension(R.dimen.home_middle_text_width);
                   textView.setLayoutParams(layoutParams3);
                   textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                   textView.setSelected(true);
                   textView.setFocusable(true);
                   textView.setFocusableInTouchMode(true);
                   textView.setTextColor(context.getResources().getColor(R.color.white));
                   if("0".equals(getSysModellist().get(i).getSid())){
                       textView.setText(context.getResources().getString(R.string.home_mode));
                   }else if("1".equals(getSysModellist().get(i).getSid())){
                       textView.setText(context.getResources().getString(R.string.out_mode));
                   }else if("2".equals(getSysModellist().get(i).getSid())){
                       textView.setText(context.getResources().getString(R.string.sleep_mode));
                   }else {
                       textView.setText(getSysModellist().get(i).getModleName());
                   }

                   textView.setSingleLine();
                   textView.setMarqueeRepeatLimit(-1);
                   textView.setGravity(Gravity.CENTER_HORIZONTAL);
                   linearLayout.addView(textView);
                   linearLayout.setTag(i);
                   linearLayout.setOnClickListener(this);
                   relativeLayout.addView(linearLayout);
                   linearlist.add(linearLayout);
               }
            ((Button)layout.findViewById(R.id.main)).setOnClickListener(this);
            layout.setOnClickListener(this);
        }

        private void initAnimation() {
            int size = linearlist.size();
            for (int i = 0; i < size; i++) {
                int x = 0;
                int y = 0;
                double angle = 0;

//                if (i >= 0 && i <= 1) {
//				angle = (90 / (size / 2 - 1)) * i;
                    if(size==3){
                        angle = (360 * (i) / (size))+90;
                    }
                    else if(size == 5){
                        angle = (360 * (i) / (size))+18;
                    }
                    else {
                        angle = (360 * (i) / (size));
                    }

                    x = -(int) (RADIUS * Math.cos(Math.toRadians(angle)));
                    y = -(int) (RADIUS * Math.sin(Math.toRadians(angle)));
//                } else if (i >= 2 && i < size) {
////				angle = (90 / (size / 2 - 1)) * (i - 3);
//                    angle = (360 * (i+1)) / (size +1 );
//                    x = -(int) (RADIUS * Math.cos(Math.toRadians(angle)));
//                    y = -(int) (RADIUS * Math.sin(Math.toRadians(angle)));
//                }
                // long time = ANIMATION_TIME - i * TIME_INTERVAL;
                // final long time = 800;
                System.out.println("===============================");
                System.out.println(i + "====>" + x);
                System.out.println(i + "====>" + y);
                System.out.println(i + "====>" + (angle));

                PropertyValuesHolder  animator = PropertyValuesHolder.ofFloat("translationX", 0f, x);
                PropertyValuesHolder animator2 = PropertyValuesHolder.ofFloat("translationY", 0f, y);
                PropertyValuesHolder animator3 = PropertyValuesHolder.ofFloat("rotation", 0f, 360f);
                PropertyValuesHolder  animator4 = PropertyValuesHolder.ofFloat("translationX",  x,0f);
                PropertyValuesHolder animator5 = PropertyValuesHolder.ofFloat("translationY", y,0f);
                PropertyValuesHolder animator6 = PropertyValuesHolder.ofFloat("rotation",  360f,0f);


                tranXAnimatinSets.add(animator);
                tranYAnimatinSets.add(animator2);
                rotateAnimatinSets.add(animator3);
                tranXAnimatinSets2.add(animator4);
                tranYAnimatinSets2.add(animator5);
                rotateAnimatinSets2.add(animator6);

            }
        }

        private void startOutAnimation() {
            for (int i = 0; i < linearlist.size(); i++) {
                LinearLayout layout = linearlist.get(i);
                ObjectAnimator.ofPropertyValuesHolder(layout, tranXAnimatinSets.get(i), tranYAnimatinSets.get(i),rotateAnimatinSets.get(i)).setDuration(duratime).start();
            }

            PropertyValuesHolder animator3 = PropertyValuesHolder.ofFloat("rotation", 0f, 360f);
            LinearLayout l = (LinearLayout) layout.findViewById(R.id.boss);
            ObjectAnimator.ofPropertyValuesHolder(l, animator3).setDuration(duratime).start();
        }

        public void startInAnimation() {
            for (int i = 0; i < linearlist.size(); i++) {
                LinearLayout layout = linearlist.get(i);
                ObjectAnimator.ofPropertyValuesHolder(layout, tranXAnimatinSets2.get(i), tranYAnimatinSets2.get(i),rotateAnimatinSets2.get(i)).setDuration(duratime).start();
            }

            PropertyValuesHolder animator3 = PropertyValuesHolder.ofFloat("rotation",  360f,0f);
            LinearLayout l = (LinearLayout) layout.findViewById(R.id.boss);
            ObjectAnimator objectAnimator =  ObjectAnimator.ofPropertyValuesHolder(l, animator3);
            objectAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    dissmins.dmissListener();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            objectAnimator.setDuration(duratime).start();

        }

        @Override
        public void onClick(View v) {
             if(v.getId()== R.id.main || v == layout){
                 dissmins.dmissListener();
             }else {

                 int index = (int) v.getTag();
                 dissmins.sendListener(index);
                 startInAnimation();
             }
        }
    }



    public interface  Dissmins {
        void dmissListener();
        void sendListener(int position);
    }
}
