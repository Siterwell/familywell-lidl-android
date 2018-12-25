package me.hekr.sthome.equipment.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import me.hekr.sthome.DragFolderwidget.ApplicationInfo;
import me.hekr.sthome.R;

/**
 * Created by ryanhsueh on 2018/12/25
 */
public class EquipmentRecyclerAdapter extends RecyclerView.Adapter<EquipmentRecyclerAdapter.ViewHolder> {

    private List<ApplicationInfo> list;

    private OnItemClickListener listener;
    public interface OnItemClickListener {
        void onItemClicked();
    }

    public EquipmentRecyclerAdapter(Context context, List<ApplicationInfo> list, OnItemClickListener listener) {
        this.list = list;
        this.listener = listener;
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
                listener.onItemClicked();
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(EquipmentRecyclerAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
