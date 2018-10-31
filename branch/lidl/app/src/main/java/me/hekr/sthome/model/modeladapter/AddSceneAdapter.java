package me.hekr.sthome.model.modeladapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import me.hekr.sthome.R;
import me.hekr.sthome.model.modelbean.SceneBean;

/**
 * Created by jishu0001 on 2016/9/2.
 */
public class AddSceneAdapter extends BaseAdapter {
    private Context mContext;
    private ListView mListView;
    private List<SceneBean> mLists;

    public AddSceneAdapter(Context context, ListView listView, List<SceneBean> lists ){
        this.mContext =context;
        this.mListView = listView;
        this.mLists = lists;
    }

    public int getCount(){
        return mLists.size();
    }

    @Override
    public boolean areAllItemsEnabled(){
        return false;
    }

    @Override
    public boolean isEnabled(int position){
        return !mLists.get(position).getName().startsWith("-");
    }

    public SceneBean getItem(int position){
        return mLists.get(position);
    }

    public long getItemId(int position){
        return position;
    }
    public View getView(int position, View convertView, ViewGroup parent){
        View tv = null;
        ViewHolder holder;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.add_system_model_item, parent,
                    false);
            holder = new ViewHolder();
            holder.checkBox = (ImageView)convertView.findViewById(R.id.checkboxo);
            holder.inName = (TextView)convertView.findViewById(R.id.cellScenceName);
            holder.numb = (TextView)convertView.findViewById(R.id.cellScenceImage);
            convertView.setTag(holder);
        }else{
            tv = convertView;
            holder = (ViewHolder) tv.getTag();
        }
        if("129".equals(mLists.get(position).getMid())){
            holder.inName.setText(mContext.getResources().getString(R.string.pir_default_scene));
        }else if("130".equals(mLists.get(position).getMid())){
            holder.inName.setText(mContext.getResources().getString(R.string.door_default_scene));
        }else if("131".equals(mLists.get(position).getMid())){
            holder.inName.setText(mContext.getResources().getString(R.string.old_man_default_scene));
        }else{
            holder.inName.setText(mLists.get(position).getName());
        }
        holder.inName.setSelected(true);
        holder.numb.setText( position<9?("0"+String.valueOf(position+1)):String.valueOf(position+1));
        updataBackground(position,holder.checkBox);
        return convertView;
    }

    @SuppressLint("NewApi")
    protected void updataBackground(int position, ImageView view){
        if(mListView.isItemChecked(position)){
            view.setBackgroundResource(R.drawable.g_select);
        }else{
            view.setBackgroundResource(R.drawable.g_unselect);
        }

    };

    private class ViewHolder {
        ImageView checkBox;
        //        TextView in;
        TextView inName;
        TextView numb;
//        TextView zzz;
//        TextView inDesc;
    }
}
