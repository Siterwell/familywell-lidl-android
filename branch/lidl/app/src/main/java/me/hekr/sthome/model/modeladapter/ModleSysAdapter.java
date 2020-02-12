package me.hekr.sthome.model.modeladapter;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.litesuits.android.log.Log;

import java.util.List;

import me.hekr.sthome.R;
import me.hekr.sthome.commonBaseView.SlideListView;
import me.hekr.sthome.model.modelbean.SysModelBean;
import me.hekr.sthome.model.modeldb.SysmodelDAO;
import me.hekr.sthome.tools.ConnectionPojo;
import me.hekr.sthome.tools.LOG;

/**
 * Created by jishu0001 on 2016/9/7.
 */
public class ModleSysAdapter extends BaseAdapter {
    private Context mContext;
    private List<SysModelBean> mlists;
    private boolean[] check;
    private SlideListView listView;
    public int donghua_num = 0;
    private DoneWithItemLisenter doneWithItemLisenter;

    private int[] image = new int[]{
            R.drawable.g_select,
            R.drawable.home1,
            R.drawable.home2,
            R.drawable.out1,
            R.drawable.out2,
            R.drawable.sleep1,
            R.drawable.sleep2,
            R.drawable.other1
    };

    private int[] color = new int[]{
            R.color.gateway_color_0,
            R.color.gateway_color_1,
            R.color.gateway_color_2,
            R.color.gateway_color_3,
            R.color.gateway_color_4,
            R.color.gateway_color_5,
            R.color.gateway_color_6,
            R.color.gateway_color_7,
            R.color.transparent

    };


    public ModleSysAdapter(Context context, List<SysModelBean> lists, SlideListView listView, DoneWithItemLisenter doneWithItemLisenter) {
        this.mContext = context;
        this.mlists = lists;
        this.listView = listView;
        this.doneWithItemLisenter = doneWithItemLisenter;
        initData();
    }

    private void initData() {
        try {
            SysmodelDAO dao = new SysmodelDAO(mContext);
            String sid = dao.findIdByChoice(ConnectionPojo.getInstance().deviceTid).getSid();
            check = new boolean[mlists.size()];
            if(sid != null){
                for(int i =0; i<mlists.size();i++){
                    if( mlists.get(i).getSid().equals(sid)){
                        check[i] = true;
                    }else{
                        check[i] = false;
                    }
                }
            }
        }catch (Exception e){
           LOG.I("ceshi","无选中的情景组");
        }


    }

    public int getCount() {
        return mlists.size();
    }
    public Object getItem(int position) {
        return mlists.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        Resources r = mContext.getResources();
        SysModelBean ac =mlists.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.cell_scence,null);
            holder.image = convertView.findViewById(R.id.cellScenceImage);
            holder.text1 = convertView.findViewById(R.id.cellScenceName);
            holder.forward = convertView.findViewById(R.id.cellScenceForward);

            holder.del = convertView.findViewById(R.id.dele);
            holder.tv_ico = convertView.findViewById(R.id.ddd);
            holder.btn_del = convertView.findViewById(R.id.shanchu);
            holder.arrow = convertView.findViewById(R.id.arrow);
            holder.touchForward = convertView.findViewById(R.id.touchForward);
            holder.gatewaycolor = convertView.findViewById(R.id.gatewaycolor);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        if(position == 0){  //home
            holder.image.setBackgroundResource(image[1]);
        }else if(position ==1){ //out
            holder.image.setBackgroundResource(image[3]);
        }else if(position ==2){//sleep
            holder.image.setBackgroundResource(image[5]);
        }else{//add
            holder.image.setBackgroundResource(image[7]);
        }
        if(position==0){
            holder.text1.setText(mContext.getResources().getString(R.string.home_mode));
        }else if(position==1){
            holder.text1.setText(mContext.getResources().getString(R.string.out_mode));
        }else if(position==2){
            holder.text1.setText(mContext.getResources().getString(R.string.sleep_mode));
        }else{
            holder.text1.setText(ac.getModleName());
        }

        if(check[position]){
            holder.forward.setImageResource(image[0]);
        }else {
            holder.forward.setImageResource(R.drawable.g_unselect);
        }

        try {
            if(ac.getColor().startsWith("F")){
                try {
                    int ds = Integer.parseInt(ac.getColor().substring(1,2));
                    holder.gatewaycolor.setBackgroundColor(mContext.getResources().getColor(color[ds]));
                }catch (Exception e){
                    holder.gatewaycolor.setBackgroundColor(mContext.getResources().getColor(color[8]));
                }

            }else{
                holder.gatewaycolor.setBackgroundColor(mContext.getResources().getColor(color[8]));
            }
        }catch (NullPointerException e){
            holder.gatewaycolor.setBackgroundColor(mContext.getResources().getColor(color[8]));
        }




        if(listView.getStatus()== SlideListView.MODE.CAN_EDIT){

            int time = donghua_num > 0? 200:0;
            PropertyValuesHolder valuesHolder = PropertyValuesHolder.ofFloat("translationX", 0.0f, mContext.getResources().getDimension(R.dimen.modle_del_ico_width));
            ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(holder.text1, valuesHolder);
            objectAnimator.setDuration(time).start();
            objectAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    donghua_num--;
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });


            PropertyValuesHolder valuesHolder2 = PropertyValuesHolder.ofFloat("translationX", 0.0f,  mContext.getResources().getDimension(R.dimen.modle_del_ico_width));
            ObjectAnimator objectAnimator2 = ObjectAnimator.ofPropertyValuesHolder(holder.tv_ico, valuesHolder2);
            objectAnimator2.setDuration(time).start();

            float xl = mContext.getResources().getDimension(R.dimen.modle_del_ico_width);
            PropertyValuesHolder valuesHolder3 = PropertyValuesHolder.ofFloat("translationX", 0.0f, (xl+20.0f));
            ObjectAnimator objectAnimator3 = ObjectAnimator.ofPropertyValuesHolder(holder.del, valuesHolder3);
            objectAnimator3.setDuration(time).start();

            //
            PropertyValuesHolder valuesHolder4 = PropertyValuesHolder.ofFloat("translationX", 0.0f, mContext.getResources().getDimension(R.dimen.modle_del_ico_width));
            PropertyValuesHolder valuesHolder4_alph = PropertyValuesHolder.ofFloat("alpha", 0.0f, 1.0f);
            ObjectAnimator objectAnimator4 = ObjectAnimator.ofPropertyValuesHolder(holder.arrow, valuesHolder4,valuesHolder4_alph);
            objectAnimator4.setDuration(time).start();



            PropertyValuesHolder valuesHolder5 = PropertyValuesHolder.ofFloat("alpha", 1.0f, 0.0f);
            ObjectAnimator objectAnimator5 = ObjectAnimator.ofPropertyValuesHolder(holder.touchForward, valuesHolder5);
            objectAnimator5.setDuration(time).start();
        }
        else if(listView.getStatus()== SlideListView.MODE.SCROLL_INIT ){
            int time = donghua_num > 0? 200:0;

            PropertyValuesHolder valuesHolder = PropertyValuesHolder.ofFloat("translationX", mContext.getResources().getDimension(R.dimen.modle_del_ico_width),0.0f);

            ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(holder.text1, valuesHolder);

            objectAnimator.setDuration(time).start();
            objectAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    donghua_num--;
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });


            PropertyValuesHolder valuesHolder2 = PropertyValuesHolder.ofFloat("translationX",  mContext.getResources().getDimension(R.dimen.modle_del_ico_width), 0.0f);
            ObjectAnimator objectAnimator2 = ObjectAnimator.ofPropertyValuesHolder(holder.tv_ico, valuesHolder2);
            objectAnimator2.setDuration(time).start();

            float xl = mContext.getResources().getDimension(R.dimen.modle_del_ico_width);
            PropertyValuesHolder valuesHolder3 = PropertyValuesHolder.ofFloat("translationX",  (xl+20.0f),0.0f);
            ObjectAnimator objectAnimator3 = ObjectAnimator.ofPropertyValuesHolder(holder.del, valuesHolder3);
            objectAnimator3.setDuration(time).start();

            //
            PropertyValuesHolder valuesHolder4 = PropertyValuesHolder.ofFloat("translationX",  mContext.getResources().getDimension(R.dimen.modle_del_ico_width),0.0f);
            PropertyValuesHolder valuesHolder4_alph = PropertyValuesHolder.ofFloat("alpha",  1.0f,0.0f);
            ObjectAnimator objectAnimator4 = ObjectAnimator.ofPropertyValuesHolder(holder.arrow, valuesHolder4,valuesHolder4_alph);
            objectAnimator4.setDuration(time).start();



            PropertyValuesHolder valuesHolder5 = PropertyValuesHolder.ofFloat("alpha",  0.0f,1.0f);

            ObjectAnimator objectAnimator5 = ObjectAnimator.ofPropertyValuesHolder(holder.touchForward, valuesHolder5);

            objectAnimator5.setDuration(time).start();
        }


        holder.forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doneWithItemLisenter.switchmode(position);
            }
        });

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doneWithItemLisenter.edit(position);
            }
        });
        holder.gatewaycolor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doneWithItemLisenter.edit(position);
            }
        });
        holder.image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                doneWithItemLisenter.longclick(position);
                return true;
            }
        });


        holder.text1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doneWithItemLisenter.edit(position);
            }
        });

        holder.text1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                doneWithItemLisenter.longclick(position);
                return true;
            }
        });

        return convertView;
    }



    class ViewHolder{
        TextView image;
        ImageView forward;
        ImageView gatewaycolor;
        ImageView del;
        TextView text1;
        LinearLayout tv_ico;
        Button btn_del;
        ImageView arrow;
        LinearLayout touchForward;
    }
    class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String msg = intent.getStringExtra("message");
            Log.i("system adapter",msg);
            Log.i("system adapter","resolveData complete" );

        }
    }

    public void refreshLists(List<SysModelBean> mlists){
        this.mlists = mlists;
        initData();
        notifyDataSetChanged();
    }

    public interface DoneWithItemLisenter {
        void switchmode(int position);
        void edit(int position);
        void longclick(int position);
    }
}
