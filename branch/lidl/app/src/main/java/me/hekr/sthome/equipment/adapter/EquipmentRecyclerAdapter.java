package me.hekr.sthome.equipment.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import me.hekr.sthome.DragFolderwidget.ApplicationInfo;
import me.hekr.sthome.DragFolderwidget.FolderInfo;
import me.hekr.sthome.R;
import me.hekr.sthome.model.modelbean.EquipmentBean;
import me.hekr.sthome.tools.ConnectionPojo;
import me.hekr.sthome.tools.NameSolve;

/**
 * Created by ryanhsueh on 2018/12/25
 */
public class EquipmentRecyclerAdapter extends RecyclerView.Adapter<EquipmentRecyclerAdapter.ViewHolder> {

    private List<ApplicationInfo> list;

    private OnItemClickListener listener;
    public interface OnItemClickListener {
        void onEquipmentClicked(EquipmentBean device);
        void onFolderClicked();
    }

    public EquipmentRecyclerAdapter(Context context, List<ApplicationInfo> list, OnItemClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    public void update(List<ApplicationInfo> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        ImageView imageView;
        TextView textName;

        ViewHolder(@NonNull View view) {
            super(view);
            this.view = view;
            this.imageView = view.findViewById(R.id.cellImageView);
            this.textName = view.findViewById(R.id.cellTextView);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.equipment_image_text_cell, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    int position = viewHolder.getAdapterPosition();

                    ApplicationInfo info = list.get(position);

                    if (info instanceof FolderInfo) {
                        listener.onFolderClicked();
                    } else {
                        EquipmentBean bean = new EquipmentBean();
                        bean.setEqid(info.getEqid());
                        bean.setState(info.getState());
                        bean.setEquipmentName(info.getEquipmentName());
                        bean.setEquipmentDesc(info.getEquipmentDesc());
                        bean.setDeviceid(ConnectionPojo.getInstance().deviceTid);

                        listener.onEquipmentClicked(bean);
                    }
                }
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(EquipmentRecyclerAdapter.ViewHolder holder, int position) {
        try {
            ApplicationInfo equipment = list.get(position);

            if (equipment instanceof FolderInfo) {
                holder.imageView.setImageResource(R.drawable.folder);
                holder.textName.setText(equipment.getText());
            } else {
                holder.imageView.setImageBitmap(equipment.getIcon());
                String name = equipment.getEquipmentName();
                if(TextUtils.isEmpty(name)){
                    name = NameSolve.getDefaultName(holder.view.getContext() ,equipment.getEquipmentDesc(),equipment.getEqid());
                }
                holder.textName.setText(name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return (list.size() > 6 ? 6 : list.size());
    }
}
