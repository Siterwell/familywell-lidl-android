package me.hekr.sthome.model.modeladapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import me.hekr.sthome.R;
import me.hekr.sthome.model.modelbean.EquipmentBean;
import me.hekr.sthome.tools.NameSolve;

/**
 * Created by jishu0001 on 2016/10/21.
 */
public class GridAdapter extends BaseAdapter {
    private Context context;
    private int listCellId = 0;
    private List<EquipmentBean> lists;
    private ViewHolder holder;
    private Drawable image1,image2,image3,image4,image5,image6;
    //    private Button a,c,d;
//    private TextView s;
    public GridAdapter(Context context, List<EquipmentBean> lists){
        this.context = context;
        this.lists = lists;
//        initSource();
    }
    //    private void initSource() {
//        Resources resource = context.getResources();
//        image1 = resource.getDrawable(mImage[0]);
//        image2 = resource.getDrawable(mImage[1]);
//        image3 = resource.getDrawable(mImage[2]);
//        image4 = resource.getDrawable(mImage[3]);
//        image5 = resource.getDrawable(mImage[4]);
//        image6 = resource.getDrawable(mImage[5]);
//    }
    private class ViewHolder {
        ImageView imageView;
        TextView textView;
    }
    public Context getContext(){
        return context;
    }

    public void add(EquipmentBean eq){
        lists.add(eq);
    }

    public void remove(String eq){
        lists.remove(eq);
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public EquipmentBean getItem(int position) {
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        holder = null;
        final EquipmentBean eq = lists.get(position);
        if( convertView == null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.createscene_image_text_cell,null);
            holder.imageView= (ImageView) convertView.findViewById(R.id.cellImageView);
            holder.textView = (TextView) convertView.findViewById(R.id.cellTextView);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        if("END".equals(eq.getEquipmentDesc())){
            holder.imageView.setImageResource(R.drawable.add);
        }else if(NameSolve.DOOR_CHECK.equals(NameSolve.getEqType(eq.getEquipmentDesc()))){
            holder.imageView.setImageResource(R.drawable.b10);
        }else if(NameSolve.SOCKET.equals(NameSolve.getEqType(eq.getEquipmentDesc()))){
            holder.imageView.setImageResource(R.drawable.b7);
        }else if(NameSolve.PIR_CHECK.equals(NameSolve.getEqType(eq.getEquipmentDesc()))){
            holder.imageView.setImageResource(R.drawable.b1);
        }else if(NameSolve.SOS_KEY.equals(NameSolve.getEqType(eq.getEquipmentDesc()))){
            holder.imageView.setImageResource(R.drawable.b2);
        }else if(NameSolve.SM_ALARM.equals(NameSolve.getEqType(eq.getEquipmentDesc()))){
            holder.imageView.setImageResource(R.drawable.b8);
        }else if(NameSolve.CO_ALARM.equals(NameSolve.getEqType(eq.getEquipmentDesc()))){
            holder.imageView.setImageResource(R.drawable.b9);
        }else if (NameSolve.WT_ALARM.equals(NameSolve.getEqType(eq.getEquipmentDesc()))) {
            holder.imageView.setImageResource(R.drawable.b5);
        }else if(NameSolve.TH_CHECK.equals(NameSolve.getEqType(eq.getEquipmentDesc()))){
            holder.imageView.setImageResource(R.drawable.b11);
        }else if(NameSolve.LAMP.equals(NameSolve.getEqType(eq.getEquipmentDesc()))){
            holder.imageView.setImageResource(R.drawable.b12);
        }else if(NameSolve.GUARD.equals(NameSolve.getEqType(eq.getEquipmentDesc()))) {
            holder.imageView.setImageResource(R.drawable.b14);
        }else if(NameSolve.VALVE.equals(NameSolve.getEqType(eq.getEquipmentDesc()))){
            holder.imageView.setImageResource(R.drawable.b15);
        }else if(NameSolve.CURTAIN.equals(NameSolve.getEqType(eq.getEquipmentDesc()))){
            holder.imageView.setImageResource(R.drawable.b13);
        }else if(NameSolve.BUTTON.equals(NameSolve.getEqType(eq.getEquipmentDesc()))){
            holder.imageView.setImageResource(R.drawable.b18);
        }else if("TIMER".equals(eq.getEquipmentDesc())){
            holder.imageView.setImageResource(R.drawable.timer);
        }else if("DELAY".equals(eq.getEquipmentDesc())){
            holder.imageView.setImageResource(R.drawable.delay);
        }else if("GATEWAY".equals(eq.getEquipmentDesc())){
            holder.imageView.setImageResource(R.drawable.b17);
        }else if("CLICK".equals(eq.getEquipmentDesc())){
            holder.imageView.setImageResource(R.drawable.clicktodo);
        }else if("PHONE".equals(eq.getEquipmentDesc())){
            holder.imageView.setImageResource(R.drawable.phonetonotice);
        }else if(NameSolve.CXSM_ALARM.equals(NameSolve.getEqType(eq.getEquipmentDesc()))){
            holder.imageView.setImageResource(R.drawable.b8);
        }else if(NameSolve.GAS_ALARM.equals(NameSolve.getEqType(eq.getEquipmentDesc()))){
            holder.imageView.setImageResource(R.drawable.b3);
        }else if(NameSolve.THERMAL_ALARM.equals(NameSolve.getEqType(eq.getEquipmentDesc()))){
            holder.imageView.setImageResource(R.drawable.b4);
        }else if(NameSolve.MODE_BUTTON.equals(NameSolve.getEqType(eq.getEquipmentDesc()))){
            holder.imageView.setImageResource(R.drawable.b16);
        }else if(NameSolve.LOCK.equals(NameSolve.getEqType(eq.getEquipmentDesc()))){
            holder.imageView.setImageResource(R.drawable.b19);
        }else if(NameSolve.TWO_SOCKET.equals(NameSolve.getEqType(eq.getEquipmentDesc()))){
            holder.imageView.setImageResource(R.drawable.b20);
        }else if(NameSolve.DIMMING_MODULE.equals(NameSolve.getEqType(eq.getEquipmentDesc()))){
            holder.imageView.setImageResource(R.drawable.b21);
        }else if(NameSolve.TEMP_CONTROL.equals(NameSolve.getEqType(eq.getEquipmentDesc()))){
            holder.imageView.setImageResource(R.drawable.b14);
        }
        if(TextUtils.isEmpty(eq.getEquipmentName())){
            holder.textView.setText(NameSolve.getDefaultName(context,eq.getEquipmentDesc(),eq.getEqid()));
        }else{
            holder.textView.setText(eq.getEquipmentName());
        }

        return convertView;
    }
}