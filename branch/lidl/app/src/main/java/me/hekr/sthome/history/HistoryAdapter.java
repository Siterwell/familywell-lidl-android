package me.hekr.sthome.history;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import me.hekr.sthome.R;
import me.hekr.sthome.push.NoticeBean;

/**
 * Created by ST-020111 on 2017/4/1.
 */

public class HistoryAdapter  extends BaseAdapter {
    private Context mContext;
    private List<NoticeBean> mLists;

    public HistoryAdapter(Context context, List<NoticeBean> lists ){
        this.mContext =context;
        this.mLists = lists;
    }

    public int getCount(){
        return mLists.size();
    }



    public NoticeBean getItem(int position){
        return mLists.get(position);
    }

    public long getItemId(int position){
        return position;
    }
    public View getView(int position, View convertView, ViewGroup parent){
        View tv = null;
        HistoryAdapter.ViewHolder holder;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.cell_alarm_message, parent,
                    false);
            holder = new HistoryAdapter.ViewHolder();
            holder.inName = (TextView)convertView.findViewById(R.id.name);
            holder.activitytime = (TextView)convertView.findViewById(R.id.time);
            holder.type   = (TextView)convertView.findViewById(R.id.type);
            holder.status = (TextView)convertView.findViewById(R.id.status);
            convertView.setTag(holder);
        }else{
            tv = convertView;
            holder = (HistoryAdapter.ViewHolder) tv.getTag();
        }

        holder.activitytime.setText(mLists.get(position).getActivitytime());
        if("AD".equals(mLists.get(position).getType())){
            if("0".equals(mLists.get(position).getEqid())){
                holder.type.setText(mContext.getResources().getString(R.string.gateway_alert));
                holder.inName.setText("");
            }else{
                holder.type.setText(mContext.getResources().getString(R.string.eq_alert));
                holder.inName.setText(mLists.get(position).getName());
            }
                holder.status.setText(HistoryDataHandler.getAlert(mContext,mLists.get(position).getEquipmenttype(),mLists.get(position).getEqstatus()));

        }else if("AC".equals(mLists.get(position).getType())){
            holder.inName.setText(mLists.get(position).getName());
            holder.type.setText(mContext.getResources().getString(R.string.scene_alert));
            holder.status.setText("");
        }

        return convertView;
    }


    private class ViewHolder {

        TextView inName;
        TextView activitytime;
        TextView type;
        TextView status;
    }

    public void refreshList(List<NoticeBean> mlists){
        this.mLists = mlists;
        notifyDataSetChanged();
    }
}
