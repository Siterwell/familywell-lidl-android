package me.hekr.sthome.model.modeladapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import me.hekr.sthome.R;
import me.hekr.sthome.model.modelbean.EquipmentBean;
import me.hekr.sthome.model.modelbean.ShortcutBean;
import me.hekr.sthome.model.modelbean.SysModelBean;
import me.hekr.sthome.model.modeldb.ShortcutDAO;
import me.hekr.sthome.model.modeldb.SysmodelDAO;

/**
 * Created by jishu0001 on 2016/10/21.
 */
public class ModeButtonAdapter extends BaseAdapter {
    private Context context;
    private List<SysModelBean> lists;
    private ViewHolder holder;
    private ChangeDesMode changeDesMode;
    private SysmodelDAO sysmodelDAO;
    private ShortcutDAO shortcutDAO;
    private Map<String,String> models = null;
    private EquipmentBean equipmentBean;

    public ModeButtonAdapter(Context context, List<SysModelBean> lists, ChangeDesMode changeDesMode, EquipmentBean equipmentBean){
        this.context = context;
        this.lists = lists;
        this.changeDesMode = changeDesMode;
        this.equipmentBean = equipmentBean;
        sysmodelDAO = new SysmodelDAO(context);
        shortcutDAO = new ShortcutDAO(context);
        models = sysmodelDAO.findAllSysByHash(equipmentBean.getDeviceid());
    }

    private class ViewHolder {
        ImageView src_imageView;
        TextView src_textView;
        ImageView des_imageView;
        TextView des_textView;
    }
    public Context getContext(){
        return context;
    }

    public void add(SysModelBean eq){
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
    public SysModelBean getItem(int position) {
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        holder = null;
        final SysModelBean eq = lists.get(position);
        if( convertView == null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.cell_modebutton_keycut,null);
            holder.src_imageView= (ImageView) convertView.findViewById(R.id.src_img);
            holder.src_textView = (TextView) convertView.findViewById(R.id.src_name);
            holder.des_imageView= (ImageView) convertView.findViewById(R.id.des_img);
            holder.des_textView = (TextView) convertView.findViewById(R.id.des_name);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }


        ShortcutBean shortcutBean = shortcutDAO.findShortcutByeqid(eq.getSid(),equipmentBean.getEqid(),equipmentBean.getDeviceid());

        if(shortcutBean==null){
            holder.des_imageView.setImageResource(R.drawable.no_mode_set);
            holder.des_textView.setText(context.getResources().getString(R.string.please_choose));
        }else {
            if("0".equals(shortcutBean.getDes_sid())){
                holder.des_imageView.setImageResource(R.drawable.home3);
                holder.des_textView.setText(context.getResources().getString(R.string.home_mode));
            }else if("1".equals(shortcutBean.getDes_sid())){
                holder.des_imageView.setImageResource(R.drawable.out3);
                holder.des_textView.setText(context.getResources().getString(R.string.out_mode));
            }else if("2".equals(shortcutBean.getDes_sid())){
                holder.des_imageView.setImageResource(R.drawable.sleep3);
                holder.des_textView.setText(context.getResources().getString(R.string.sleep_mode));
            }else{
                holder.des_imageView.setImageResource(R.drawable.home3);
                holder.des_textView.setText(String.valueOf(models.get(shortcutBean.getDes_sid())));
            }
        }

        if("0".equals(eq.getSid())){
            holder.src_imageView.setImageResource(R.drawable.home3);
            holder.src_textView.setText(context.getResources().getString(R.string.home_mode));
        }else if("1".equals(eq.getSid())){
            holder.src_imageView.setImageResource(R.drawable.out3);
            holder.src_textView.setText(context.getResources().getString(R.string.out_mode));
        }else if("2".equals(eq.getSid())){
            holder.src_imageView.setImageResource(R.drawable.sleep3);
            holder.src_textView.setText(context.getResources().getString(R.string.sleep_mode));
        }else{
            holder.src_imageView.setImageResource(R.drawable.home3);
            holder.src_textView.setText(eq.getModleName());
        }
        holder.des_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeDesMode.showDialogd(position);
            }
        });
        holder.des_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeDesMode.showDialogd(position);
            }
        });
        return convertView;
    }

    public interface ChangeDesMode{
     void showDialogd(final int index);


    }
}