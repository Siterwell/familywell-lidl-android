package me.hekr.sthome.commonBaseView;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import java.util.List;

import me.hekr.sthome.DragFolderwidget.ApplicationInfo;
import me.hekr.sthome.R;
import me.hekr.sthome.equipment.adapter.EquipmentRecyclerAdapter;
import me.hekr.sthome.main.MainActivity;
import me.hekr.sthome.model.modeldb.EquipDAO;
import me.hekr.sthome.tools.ConnectionPojo;
import me.hekr.sthome.tools.LOG;

/**
 * Created by ryanhsueh on 2018/12/25
 */
public class LayoutDeviceList extends FrameLayout implements EquipmentRecyclerAdapter.OnItemClickListener {

    private MainActivity activity;

    private FrameLayout root;
    private RecyclerView recyclerView;

    public LayoutDeviceList(Context context) {
        super(context);
    }

    public LayoutDeviceList(MainActivity activity) {
        super(activity);

        this.activity = activity;
        init();
    }

    private void init(){
        root = (FrameLayout) LayoutInflater.from(activity).inflate(
                R.layout.view_device_list, null);

        EquipDAO ED = new EquipDAO(activity);
        List<ApplicationInfo> list = ED.findAllEqByNoPack(ConnectionPojo.getInstance().deviceTid);
        LOG.D("FrameLayout", "[RYAN] getDeviceListView > list size = " + list.size());

        recyclerView = root.findViewById(R.id.rv_device_list);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        EquipmentRecyclerAdapter adapter = new EquipmentRecyclerAdapter(getContext(), list, this);
        recyclerView.setAdapter(adapter);
    }

    public FrameLayout getRoot() {
        return root;
    }

    @Override
    public void onItemClicked() {
        activity.jumpToDevice();
    }

}
