package me.hekr.sthome.equipment.adapter;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.hekr.sthome.R;
import me.hekr.sthome.commonBaseView.SlideListView;
import me.hekr.sthome.wheelwidget.helper.Common;

/**
 * Created by jishu0001 on 2016/8/23.
 */
public abstract class EqpAdapter<T> extends BaseAdapter {
    private Context context;
    private int listCellId = 0;
    private SlideListView listView;
    public int donghua_num = 0; //用来监听动画执行完成标志
    private DoneWithItemListener doneWithItemListener;

    public EqpAdapter(Context context, int resId, SlideListView listView, DoneWithItemListener doneWithItemListener){
        this.context = context;
        this.listCellId = resId;
        this.listView = listView;
        this.doneWithItemListener = doneWithItemListener;
    }
    public Context getContext(){
        return context;
    }

    private List<T> list = new ArrayList<T>();
    public void add(T item){
        list.add(item);
        notifyDataSetChanged();
    }
    public void remove(int i){
        list.remove(i);
        notifyDataSetChanged();
    }
    public void removeLast(){
        list.remove(getCount()-1);
    }
    public void clean(){
        list.clear();
    }

    public void refreshLists(List<T> list){
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public T getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if(view == null){
            holder = new ViewHolder();
            view = LayoutInflater.from(getContext()).inflate(listCellId,null);
            holder.text1 = (TextView) view.findViewById(R.id.cellScenceName);
            holder.del = (ImageView)view.findViewById(R.id.dele);
            holder.tv_ico = (LinearLayout)view.findViewById(R.id.ddd);
            holder.arrow = (ImageView)view.findViewById(R.id.arrow);
            holder.btn_done = (Button)view.findViewById(R.id.done);
            holder.btn_del = (Button)view.findViewById(R.id.shanchu);
            view.setTag(holder);
        } else {
            holder = (ViewHolder)view.getTag();
        }

        holder.btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doneWithItemListener.done(i);
            }
        });

        if(listView.getStatus()== SlideListView.MODE.CAN_EDIT){

            int time = donghua_num > 0? 200:0;
            PropertyValuesHolder valuesHolder = PropertyValuesHolder.ofFloat("translationX", 0.0f, context.getResources().getDimension(R.dimen.modle_del_ico_width));
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


            PropertyValuesHolder valuesHolder2 = PropertyValuesHolder.ofFloat("translationX", 0.0f,  context.getResources().getDimension(R.dimen.modle_del_ico_width));
            ObjectAnimator objectAnimator2 = ObjectAnimator.ofPropertyValuesHolder(holder.tv_ico, valuesHolder2);
            objectAnimator2.setDuration(time).start();

            float xl = context.getResources().getDimension(R.dimen.modle_del_ico_width);
            PropertyValuesHolder valuesHolder3 = PropertyValuesHolder.ofFloat("translationX", 0.0f, (xl+20.0f));
            ObjectAnimator objectAnimator3 = ObjectAnimator.ofPropertyValuesHolder(holder.del, valuesHolder3);
            objectAnimator3.setDuration(time).start();

            //
            PropertyValuesHolder valuesHolder4 = PropertyValuesHolder.ofFloat("translationX", 0.0f, context.getResources().getDimension(R.dimen.modle_item_zhixing_width)- Common.toPx(context,5f));
            PropertyValuesHolder valuesHolder4_alph = PropertyValuesHolder.ofFloat("alpha", 0.0f, 1.0f);
            ObjectAnimator objectAnimator4 = ObjectAnimator.ofPropertyValuesHolder(holder.arrow, valuesHolder4,valuesHolder4_alph);
            objectAnimator4.setDuration(time).start();
            PropertyValuesHolder valuesHolder5_alph = PropertyValuesHolder.ofFloat("alpha", 1.0f, 0.0f);
            ObjectAnimator objectAnimator5 = ObjectAnimator.ofPropertyValuesHolder(holder.btn_done, valuesHolder5_alph);
            objectAnimator5.setDuration(time).start();
            holder.btn_done.setClickable(false);

        }
        else if(listView.getStatus()== SlideListView.MODE.SCROLL_INIT ){
            int time = donghua_num > 0? 200:0;

            PropertyValuesHolder valuesHolder = PropertyValuesHolder.ofFloat("translationX", context.getResources().getDimension(R.dimen.modle_del_ico_width),0.0f);

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


            PropertyValuesHolder valuesHolder2 = PropertyValuesHolder.ofFloat("translationX",  context.getResources().getDimension(R.dimen.modle_del_ico_width), 0.0f);
            ObjectAnimator objectAnimator2 = ObjectAnimator.ofPropertyValuesHolder(holder.tv_ico, valuesHolder2);
            objectAnimator2.setDuration(time).start();

            float xl = context.getResources().getDimension(R.dimen.modle_del_ico_width);
            PropertyValuesHolder valuesHolder3 = PropertyValuesHolder.ofFloat("translationX",  (xl+20.0f),0.0f);
            ObjectAnimator objectAnimator3 = ObjectAnimator.ofPropertyValuesHolder(holder.del, valuesHolder3);
            objectAnimator3.setDuration(time).start();

            //
            PropertyValuesHolder valuesHolder4 = PropertyValuesHolder.ofFloat("translationX",  context.getResources().getDimension(R.dimen.modle_item_zhixing_width)- Common.toPx(context,5f),0.0f);
            PropertyValuesHolder valuesHolder4_alph = PropertyValuesHolder.ofFloat("alpha",  1.0f,0.0f);
            ObjectAnimator objectAnimator4 = ObjectAnimator.ofPropertyValuesHolder(holder.arrow, valuesHolder4,valuesHolder4_alph);
            objectAnimator4.setDuration(time).start();
            PropertyValuesHolder valuesHolder5_alph = PropertyValuesHolder.ofFloat("alpha", 0.0f, 1.0f);
            ObjectAnimator objectAnimator5 = ObjectAnimator.ofPropertyValuesHolder(holder.btn_done, valuesHolder5_alph);
            objectAnimator5.setDuration(time).start();
            holder.btn_done.setClickable(true);
        }


        holder.btn_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doneWithItemListener.delete(i);
            }
        });

        holder.tv_ico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doneWithItemListener.editdone(i);
            }
        });
        holder.text1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doneWithItemListener.editdone(i);
            }
        });

        holder.tv_ico.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                doneWithItemListener.delete(i);
                return true;
            }
        });
        holder.text1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                doneWithItemListener.delete(i);
                return true;
            }
        });

        initListCell(i,view,viewGroup);
        return view;
    }

    class ViewHolder{

        ImageView del;
        TextView text1;
        LinearLayout tv_ico;
        ImageView arrow;
        Button btn_del;
        Button btn_done;
    }

    protected abstract void initListCell(int i, View listCell, ViewGroup viewGroup);

    public interface DoneWithItemListener{
        void delete(int position);
        void editdone(int position);
        void done(int position);
    }

}
