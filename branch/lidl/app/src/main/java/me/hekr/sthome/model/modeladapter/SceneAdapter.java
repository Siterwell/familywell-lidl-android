package me.hekr.sthome.model.modeladapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import me.hekr.sthome.R;
import me.hekr.sthome.model.modelbean.SysModelBean;

/**
 * Created by jishu0001 on 2016/9/2.
 */
public class SceneAdapter extends BaseAdapter {
    private Context mContext;
    private ListView mListView;
    private List<SysModelBean> mLists;

    public SceneAdapter(Context context,ListView listView,List<SysModelBean> lists ){
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
        return !mLists.get(position).getModleName().startsWith("-");
    }

    public SysModelBean getItem(int position){
        return mLists.get(position);
    }

    public long getItemId(int position){
        return position;
    }
    public View getView(int position, View convertView, ViewGroup parent){
        TextView tv;
        if(convertView == null){
            tv = (TextView) LayoutInflater.from(mContext).inflate(
                    android.R.layout.simple_expandable_list_item_1, parent,
                    false);
        }else{
            tv = (TextView)convertView;
        }
        tv.setText(mLists.get(position).getModleName());
        updataBackground(position,tv);
        return tv;
    }

    @SuppressLint("NewApi")
    protected void updataBackground(int position, View view){
        int backgroundId;
        if(mListView.isItemChecked(position)){
            backgroundId = R.drawable.list_selected_holo_light;
        }else{
            backgroundId = R.drawable.conversation_item_background_read;
        }
        Drawable background = mContext.getResources().getDrawable(backgroundId);

        view.setBackground(background);
    };
}
