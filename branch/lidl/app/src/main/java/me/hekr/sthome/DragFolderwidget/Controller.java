package me.hekr.sthome.DragFolderwidget;

import java.util.List;

import me.hekr.sthome.model.modelbean.EquipmentBean;

public interface Controller {
    /**
     * 初始化桌面数据
     */
    public void initData(List<ApplicationInfo> list);

    /**
     * 点击应用
     */
    public void onAppClick(EquipmentBean app);

    /**
     * 删除应用
     */
    public void onAppRemove(ApplicationInfo app);


}
