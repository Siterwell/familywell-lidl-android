package me.hekr.sthome.model.modeladapter;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import me.hekr.sthome.R;
import me.hekr.sthome.model.modelbean.SceneBean;

/**
 * Created by jishu0001 on 2016/9/2.
 */
public class ModifySceneAdapter extends BaseAdapter {
    private static HashMap<Integer, Boolean> isSelected;
    private Context context;
    private List<SceneBean> list;
    private List<Integer> pList;


    public ModifySceneAdapter(Context context, List<SceneBean> lists, List<Integer> beforeValues){
        this.context = context;
        this.list = lists;
        this.pList = beforeValues;
        init();
        showdate();
    }

    private void showdate() {
        for(int i = 0;i<list.size();i++){
            Log.i("isSelected","values "+i+":"+isSelected.containsKey(i));
        }
    }

    private void init() {
        isSelected = new HashMap<Integer, Boolean>();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                isSelected.put(i,false);
                for(int j=0;j<pList.size();j++){
                    if(Integer.parseInt(list.get(i).getMid()) == pList.get(j)){
                        isSelected.put(i,true);
                        break;
                    }
                }
            }
        }
    }

    public Context getContext(){
        return context;
    }

    public void remove(int i){
        list.remove(i);
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
//		Log.d("zhang", "position = " + position);
        View view=null;
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.modify_system_model_item, null);
            holder = new ViewHolder();
            holder.checkBox = (ImageView) convertView.findViewById(R.id.checkboxo);
            holder.inName = (TextView) convertView.findViewById(R.id.cellScenceName);
            holder.nub = (TextView)convertView.findViewById(R.id.cellScenceImage);
            convertView.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        if("129".equals(list.get(position).getMid())){
            holder.inName.setText(context.getResources().getString(R.string.pir_default_scene));
        }else if("130".equals(list.get(position).getMid())){
            holder.inName.setText(context.getResources().getString(R.string.door_default_scene));
        }else if("131".equals(list.get(position).getMid())){
            holder.inName.setText(context.getResources().getString(R.string.old_man_default_scene));
        }else{
            holder.inName.setText(list.get(position).getName());
        }
        holder.inName.setSelected(true);
        holder.nub.setText( position<9?("0"+String.valueOf(position+1)):String.valueOf(position+1));
        if(isSelected.get(position)){  //for selected condition
            holder.checkBox.setBackgroundResource(R.drawable.g_select);
        }else{
            holder.checkBox.setBackgroundResource(R.drawable.g_unselect);
        }


        return convertView;
    }

//    protected abstract void initListCell(int position, View convertView, ViewGroup parent) ;

    public HashMap<Integer,Boolean> getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(HashMap<Integer,Boolean> isSelected) {
        this.isSelected = isSelected;
    }

    private class ViewHolder {
        ImageView checkBox;
        //        TextView in;
        TextView inName;
        TextView nub;
//        TextView inDesc;
    }
}
