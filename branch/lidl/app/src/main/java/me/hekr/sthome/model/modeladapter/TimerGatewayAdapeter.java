package me.hekr.sthome.model.modeladapter;

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
import me.hekr.sthome.model.ResolveTimer;
import me.hekr.sthome.model.modelbean.TimerGatewayBean;
import me.hekr.sthome.tools.UnitTools;

/**
 * Created by jishu0001 on 2016/9/1.
 */
public class TimerGatewayAdapeter extends BaseAdapter  {
    private Context context;
    private List<TimerGatewayBean> list;
    private switchItemListener doneWithItemListener;
    private UnitTools unitTools;

    public TimerGatewayAdapeter(Context context, List<TimerGatewayBean> lista, switchItemListener doneWithItemListener){
        this.context = context;
        this.list = lista;
        this.doneWithItemListener = doneWithItemListener;
        unitTools = new UnitTools(context);
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
    public TimerGatewayBean getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        TimerGatewayBean sp = list.get(position);
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.cell_time, null);
            holder = new ViewHolder();
            holder.btn_switch = (ImageButton) convertView.findViewById(R.id.enable);
            holder.week = (TextView)convertView.findViewById(R.id.weektime);
            holder.time_hour = (TextView)convertView.findViewById(R.id.time_hour);
            holder.time_min = (TextView)convertView.findViewById(R.id.time_min);
            holder.mode = (TextView)convertView.findViewById(R.id.mode);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        if("0".equals(sp.getModeid())){
            holder.mode.setText(context.getResources().getString(R.string.home_mode));
        }else if("1".equals(sp.getModeid())){
            holder.mode.setText(context.getResources().getString(R.string.out_mode));
        }else if("2".equals(sp.getModeid())){
            holder.mode.setText(context.getResources().getString(R.string.sleep_mode));
        }else{
            if(TextUtils.isEmpty(sp.getModename())){
                holder.mode.setText(sp.getModeid());
            }else{
                holder.mode.setText(sp.getModename());
            }

        }


        holder.week.setText(ResolveTimer.getWeekinfo(sp.getWeek(),context));
        holder.time_hour.setText(sp.getHour());
        holder.time_min.setText(sp.getMin());
        if(sp.getEnable()==1){
            holder.btn_switch.setImageResource(R.drawable.detail_switch_on);
        }else{
            holder.btn_switch.setImageResource(R.drawable.detail_switch_off);
        }
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
        TextView mode;
        ImageButton btn_switch;
    }


    public void refreshList(List<TimerGatewayBean> mlists){
        this.list = mlists;
        notifyDataSetChanged();
    }

    public interface switchItemListener{
        void switchclick(int position);
        void longclick(int position);
        void click(int position);
    }

}
