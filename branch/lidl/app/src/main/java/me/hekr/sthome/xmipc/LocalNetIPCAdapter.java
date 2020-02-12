package me.hekr.sthome.xmipc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lib.funsdk.support.models.FunDevice;

import java.util.List;

import me.hekr.sthome.R;

/**
 * Created by st on 2017/12/25.
 */

public class LocalNetIPCAdapter extends BaseAdapter {

    private Context context;
    private List<FunDevice> lists;
    private ViewHolder holder;
    public LocalNetIPCAdapter(Context context, List<FunDevice> lists){
        this.context = context;
        this.lists = lists;

    }
    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public FunDevice getItem(int i) {
        return lists.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        holder = null;
        final FunDevice eq = lists.get(i);
        if( convertView == null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.cell_localnet_ipc,null);
            holder.textView_name= (TextView) convertView.findViewById(R.id.monitor_name);
            holder.textView_sn = (TextView) convertView.findViewById(R.id.monitor_sn);
            holder.textView_ip = (TextView) convertView.findViewById(R.id.monitor_ip);
            holder.imageView = (ImageView)convertView.findViewById(R.id.sub_monitor);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.imageView.setImageResource(eq.getDevType().getDrawableResId());
        holder.textView_name.setText(eq.getDevName());
        holder.textView_sn.setText(eq.getDevSn());
        holder.textView_ip.setText(eq.getDevIP());
        return convertView;
    }

    private class ViewHolder {
        TextView textView_name;
        TextView textView_sn;
        TextView textView_ip;
        ImageView imageView;
    }
}
