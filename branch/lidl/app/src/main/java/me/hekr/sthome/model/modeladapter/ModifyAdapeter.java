package me.hekr.sthome.model.modeladapter;

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

import java.util.List;

import me.hekr.sthome.R;
import me.hekr.sthome.commonBaseView.SlideListView;
import me.hekr.sthome.http.bean.DeviceBean;
import me.hekr.sthome.model.modelbean.MyDeviceBean;

/**
 * Created by jishu0001 on 2016/9/1.
 */
public class ModifyAdapeter extends BaseAdapter  {
    private Context context;
    private List<MyDeviceBean> list;
    private SlideListView listView;
    public int donghua_num = 0; //用来监听动画执行完成标志
    private DoneWithItemListener doneWithItemListener;

    public ModifyAdapeter(Context context, List<MyDeviceBean> lista, SlideListView listView, DoneWithItemListener doneWithItemListener){
        this.context = context;
        this.list = lista;
        this.listView =listView;
        this.doneWithItemListener = doneWithItemListener;
    }
    public Context getContext(){
        return context;
    }

    public void remove(int i){
        list.remove(i);
        notifyDataSetChanged();
    }

    public void removeLast(){
        list.remove(getCount()-1);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public DeviceBean getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        MyDeviceBean sp = list.get(position);
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.modify_modle_cell, null);
            holder = new ViewHolder();
            holder.del = (ImageView) convertView.findViewById(R.id.dele);
            holder.btn_del = (Button)convertView.findViewById(R.id.shanchu);
            holder.title = (TextView)convertView.findViewById(R.id.title);
            holder.sub_title = (TextView)convertView.findViewById(R.id.sub_title);
            holder.status  = (TextView)convertView.findViewById(R.id.summary);
            holder.content = (LinearLayout)convertView.findViewById(R.id.content);
            holder.arrow  = (ImageView)convertView.findViewById(R.id.arrow);
            holder.gouzi = (ImageView)convertView.findViewById(R.id.gouzi);
            convertView.setTag(holder);
//            initListCell(position,convertView,parent);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        if(sp.isOnline()){
            holder.status.setText(R.string.on_line);
        }else{
            holder.status.setText(R.string.off_line);
        }
        if(sp.getChoice()==1){
            holder.gouzi.setVisibility(View.VISIBLE);
        }else{
            holder.gouzi.setVisibility(View.INVISIBLE);
        }

        if("报警器".equals(sp.getDeviceName())){
            holder.title.setText(context.getResources().getString(R.string.my_home));
        }else{
            holder.title.setText(sp.getDeviceName());
        }

        holder.sub_title.setText(sp.getDevTid());

        if(listView.getStatus()== SlideListView.MODE.CAN_EDIT){

            int time = donghua_num > 0? 200:0;
            PropertyValuesHolder valuesHolder = PropertyValuesHolder.ofFloat("translationX", 0.0f, context.getResources().getDimension(R.dimen.modle_del_ico_width));
            ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(holder.content, valuesHolder);
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


            float xl = context.getResources().getDimension(R.dimen.modle_del_ico_width);
            PropertyValuesHolder valuesHolder2 = PropertyValuesHolder.ofFloat("translationX", 0.0f, (xl+20.0f));
            ObjectAnimator objectAnimator2 = ObjectAnimator.ofPropertyValuesHolder(holder.del, valuesHolder2);
            objectAnimator2.setDuration(time).start();

            PropertyValuesHolder valuesHolder4 = PropertyValuesHolder.ofFloat("translationX", 0.0f, context.getResources().getDimension(R.dimen.modle_del_ico_width));
            PropertyValuesHolder valuesHolder4_alph = PropertyValuesHolder.ofFloat("alpha", 0.0f, 1.0f);
            ObjectAnimator objectAnimator4 = ObjectAnimator.ofPropertyValuesHolder(holder.arrow, valuesHolder4,valuesHolder4_alph);
            objectAnimator4.setDuration(time).start();

            PropertyValuesHolder valuesHolder5 = PropertyValuesHolder.ofFloat("translationX", 0.0f, context.getResources().getDimension(R.dimen.modle_del_ico_width));
            PropertyValuesHolder valuesHolder5_alph = PropertyValuesHolder.ofFloat("alpha", 1.0f, 0.0f);
            ObjectAnimator objectAnimator5 = ObjectAnimator.ofPropertyValuesHolder(holder.status, valuesHolder5,valuesHolder5_alph);
            objectAnimator5.setDuration(time).start();

        }
        else if(listView.getStatus()== SlideListView.MODE.SCROLL_INIT ){
            int time = donghua_num > 0? 200:0;

            PropertyValuesHolder valuesHolder = PropertyValuesHolder.ofFloat("translationX", context.getResources().getDimension(R.dimen.modle_del_ico_width),0.0f);

            ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(holder.content, valuesHolder);

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


            float xl = context.getResources().getDimension(R.dimen.modle_del_ico_width);
            PropertyValuesHolder valuesHolder2 = PropertyValuesHolder.ofFloat("translationX",  (xl+20.0f),0.0f);
            ObjectAnimator objectAnimator2 = ObjectAnimator.ofPropertyValuesHolder(holder.del, valuesHolder2);
            objectAnimator2.setDuration(time).start();

            //
            PropertyValuesHolder valuesHolder4 = PropertyValuesHolder.ofFloat("translationX", context.getResources().getDimension(R.dimen.modle_del_ico_width), 0.0f);
            PropertyValuesHolder valuesHolder4_alph = PropertyValuesHolder.ofFloat("alpha",1.0f ,0.0f );
            ObjectAnimator objectAnimator4 = ObjectAnimator.ofPropertyValuesHolder(holder.arrow, valuesHolder4,valuesHolder4_alph);
            objectAnimator4.setDuration(time).start();

            PropertyValuesHolder valuesHolder5 = PropertyValuesHolder.ofFloat("translationX", context.getResources().getDimension(R.dimen.modle_del_ico_width), 0.0f);
            PropertyValuesHolder valuesHolder5_alph = PropertyValuesHolder.ofFloat("alpha", 0.0f, 1.0f);
            ObjectAnimator objectAnimator5 = ObjectAnimator.ofPropertyValuesHolder(holder.status, valuesHolder5,valuesHolder5_alph);
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

        return convertView;
    }

//    protected abstract void initListCell(int position, View convertView, ViewGroup parent) ;


    public class ViewHolder {
        ImageView del;
        TextView title;
        TextView sub_title;
        TextView status;
        Button btn_del;
        LinearLayout content;
        ImageView arrow;
        ImageView gouzi;
    }


    public void refreshList(List<MyDeviceBean> mlists){
        this.list = mlists;
        notifyDataSetChanged();
    }

    static public interface DoneWithItemListener{
        void delete(int position);
    }

    @Override
    public void notifyDataSetChanged() {

        //donghua_num = listView.getLastVisiblePosition()-listView.getFirstVisiblePosition()+1;
        super.notifyDataSetChanged();
    }
}
