package me.hekr.sthome.xmipc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import me.hekr.sthome.R;
import me.hekr.sthome.common.ImageLoader;

/**
 * Created by jishu0001 on 2016/10/21.
 */
public class LocalPicAdapter extends BaseAdapter {
    private Context context;
    private List<Localfile> lists;
    private ViewHolder holder;
    public  final SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public LocalPicAdapter(Context context, List<Localfile> lists){
        this.context = context;
        this.lists = lists;

    }

    private class ViewHolder {
        ImageView imageView;
        TextView textView_name;
        TextView textView_time;
    }
    public Context getContext(){
        return context;
    }


    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Localfile getItem(int position) {
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        holder = null;
        final Localfile eq = lists.get(position);
        if( convertView == null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_device_camera_pic,null);
            holder.imageView= (ImageView) convertView.findViewById(R.id.iv_push_result_checked);
            holder.textView_name = (TextView) convertView.findViewById(R.id.tv_push_result_event);
            holder.textView_time = (TextView) convertView.findViewById(R.id.tv_push_result_time);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        ImageLoader.getInstance(3, ImageLoader.Type.LIFO).loadImage(eq.getFilepath(), holder.imageView);
       // holder.imageView.setImageResource(R.drawable.u2);
        holder.textView_name.setText(eq.getFilename());
        holder.textView_time.setText(yearFormat.format(new Date(Long.parseLong(eq.getModifytime())*1000)));
        return convertView;
    }

    public void refreshList(List<Localfile> mlists){
        this.lists = mlists;
        notifyDataSetChanged();
    }

}