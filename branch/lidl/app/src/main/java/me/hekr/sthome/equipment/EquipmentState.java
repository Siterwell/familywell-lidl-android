package me.hekr.sthome.equipment;

import me.hekr.sthome.DragFolderwidget.ApplicationInfo;

/**
 * Created by ryanhsueh on 2019/2/23
 */
public class EquipmentState {

    public static boolean isEquipmentStateAvalible(ApplicationInfo equipment) {
        return (equipment.getState() != null && equipment.getState().length() == 8);
    }

    public static int getDevBatteryLevel(ApplicationInfo equipment) {
        return Integer.parseInt(equipment.getState().substring(2, 4), 16);
    }

    public static String getDevFirstState(ApplicationInfo equipment) {
        return equipment.getState().substring(4, 6);
    }

    public static String getDevSecondState(ApplicationInfo equipment) {
        return equipment.getState().substring(6, 8);
    }

    public static boolean isLowBattery(int battery) {
        return (battery <= 15);
    }

}
