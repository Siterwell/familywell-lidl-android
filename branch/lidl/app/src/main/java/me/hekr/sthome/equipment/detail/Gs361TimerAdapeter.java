package me.hekr.sthome.equipment.detail;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import me.hekr.sthome.R;
import me.hekr.sthome.model.ResolveScene;
import me.hekr.sthome.model.ResolveTimer;
import me.hekr.sthome.model.modelbean.EquipmentBean;
import me.hekr.sthome.model.modelbean.SceneBean;
import me.hekr.sthome.model.modelbean.TimerGatewayBean;
import me.hekr.sthome.tools.ByteUtil;
import me.hekr.sthome.tools.NameSolve;
import me.hekr.sthome.tools.UnitTools;

/**
 * Created by jishu0001 on 2016/9/1.
 */
public class Gs361TimerAdapeter extends BaseAdapter  {
    private Context context;
    private List<SceneBean> list;
    private switchItemListener doneWithItemListener;

    public Gs361TimerAdapeter(Context context, List<SceneBean> lista, switchItemListener doneWithItemListener){
        this.context = context;
        this.list = lista;
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
    public SceneBean getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        SceneBean sp = list.get(position);
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.cell_gs361_timer, null);
            holder = new ViewHolder();
            holder.btn_switch = (ImageButton) convertView.findViewById(R.id.enable);
            holder.week = (TextView)convertView.findViewById(R.id.weektime);
            holder.time_hour = (TextView)convertView.findViewById(R.id.time_hour);
            holder.time_min = (TextView)convertView.findViewById(R.id.time_min);
            holder.target_timer = (TextView)convertView.findViewById(R.id.target_temp);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        ResolveScene resolveScene = new ResolveScene(context,sp.getCode());
        if(resolveScene.isTarget()){
           String timer = resolveScene.getSp().getTimer();
            String initwek = timer.substring(0,timer.length()-4);
            holder.week.setText(ResolveTimer.getWeekinfo(initwek,context));
            String hour = timer.substring(timer.length()-4,timer.length()-2);
            String min = timer.substring(timer.length()-2);
            holder.time_hour.setText(hour);
             holder.time_min.setText(min);
            String status1 = resolveScene.getOutput().get(0).getState().substring(0,2);
            byte ds = (byte)Integer.parseInt(status1,16);
            byte xiaoshu = (byte)((0x20) & ds);
            int sta =  ((0x1F) & ds);
            holder.target_timer.setText(sta + (xiaoshu==0?".0":".5")+"℃");
        }
//        if("0".equals(sp.getModeid())){
//            holder.mode.setText(context.getResources().getString(R.string.home_mode));
//        }else if("1".equals(sp.getModeid())){
//            holder.mode.setText(context.getResources().getString(R.string.out_mode));
//        }else if("2".equals(sp.getModeid())){
//            holder.mode.setText(context.getResources().getString(R.string.sleep_mode));
//        }else{
//            if(TextUtils.isEmpty(sp.getModename())){
//                holder.mode.setText(sp.getModeid());
//            }else{
//                holder.mode.setText(sp.getModename());
//            }
//
//        }
//
//
//        holder.week.setText(ResolveTimer.getWeekinfo(sp.getWeek(),context,unitTools));
//        holder.time_hour.setText(sp.getHour());
//        holder.time_min.setText(sp.getMin());
        holder.btn_switch.setVisibility(View.GONE);
        holder.btn_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doneWithItemListener.switchclick(position);
            }
        });
        convertView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                doneWithItemListener.click(position);
            }
        });
        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                doneWithItemListener.longclick(position);
                return true;
            }
        });

        return convertView;
    }



    public class ViewHolder {
        TextView week;
        TextView time_hour;
        TextView time_min;
        TextView target_timer;
        ImageButton btn_switch;
    }


    public void refreshList(List<SceneBean> mlists){
        this.list = mlists;
        notifyDataSetChanged();
    }

    public interface switchItemListener{
        void switchclick(int position);
        void longclick(int position);
        void click(int position);
    }

}
