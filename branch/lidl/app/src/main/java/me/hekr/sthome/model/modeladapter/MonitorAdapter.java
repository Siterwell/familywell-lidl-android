package me.hekr.sthome.model.modeladapter;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import me.hekr.sthome.R;
import me.hekr.sthome.commonBaseView.SlideListView;
import me.hekr.sthome.model.modelbean.MonitorBean;

/**
 * Created by jishu0001 on 2016/9/7.
 */
public class MonitorAdapter extends BaseAdapter {
    private Context mContext;
    private List<MonitorBean> mlists;
    private SlideListView listView;
    public int donghua_num = 0;
    private DoneWithItemListener doneWithItemListener;

    public MonitorAdapter(Context context, List<MonitorBean> lists, SlideListView listView, DoneWithItemListener doneWithItemListener) {
        this.mContext = context;
        this.mlists = lists;
        this.listView = listView;
        this.doneWithItemListener = doneWithItemListener;
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
        MonitorBean ac =mlists.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.cell_monitor,null);
            holder.image = (TextView) convertView.findViewById(R.id.cellScenceImage);
            holder.text1 = (TextView) convertView.findViewById(R.id.cellScenceName);
            holder.forward = (ImageView) convertView.findViewById(R.id.cellScenceForward);
            holder.btn_edit = ( Button) convertView.findViewById(R.id.edit);
            holder.del = (ImageView)convertView.findViewById(R.id.dele);
            holder.tv_ico = (LinearLayout)convertView.findViewById(R.id.ddd);
            holder.btn_del = (Button)convertView.findViewById(R.id.shanchu);
            holder.arrow = (ImageView)convertView.findViewById(R.id.arrow);
            holder.touchForward = (LinearLayout)convertView.findViewById(R.id.touchForward);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }


        holder.text1.setTextColor(r.getColor(R.color.black));
        holder.text1.setText(ac.getDevid()+" "+ac.getName());
        holder.forward.setVisibility(View.INVISIBLE);

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

        holder.del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listView.scrollLeftEx(position);
            }
        });

        holder.btn_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doneWithItemListener.delete(position);
            }
        });

        holder.btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doneWithItemListener.done(position);
            }
        });

        return convertView;
    }



    class ViewHolder{
        TextView image;
        ImageView forward;
        ImageView del;
        TextView text1;
        LinearLayout tv_ico;
        Button btn_del;
        Button btn_edit;
        ImageView arrow;
        LinearLayout touchForward;
    }

    public void refreshLists(List<MonitorBean> mlists){
        this.mlists = mlists;
        notifyDataSetChanged();
    }

    static public interface DoneWithItemListener{
        void delete(int position);
        void done(int position);
    }

}
