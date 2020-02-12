package me.hekr.sthome.equipment;

import me.hekr.sthome.DragFolderwidget.ApplicationInfo;

/**
 * Created by ryanhsueh on 2019/2/23
 */
public class EquipmentState {

    public static String BROKEN = "11";
    public static String TRIGGERED = "55";
    public static String NORMAL = "AA";
    public static String TEST = "BB";
    public static String MUTE = "50";
    public static String PIR_TEARED = "A0";
    public static String DOOR_NOT_CLOSED = "66";

    public static boolean isEquipmentStateAvalible(ApplicationInfo equipment) {
        return (equipment.getState() != null && equipment.getState().length() == 8);
    }

    public static boolean isBroken(ApplicationInfo equipment) {
        return isBroken(getDevFirstState(equipment));
    }
    public static boolean isBroken(String state) {
        return BROKEN.equals(state);
    }

    public static boolean isTriggered(ApplicationInfo equipment) {
        return TRIGGERED.equals(getDevFirstState(equipment));
    }
    public static boolean isTriggered(String state) {
        return TRIGGERED.equals(state);
    }

    public static boolean isNormal(ApplicationInfo equipment) {
        return NORMAL.equals(getDevFirstState(equipment));
    }
    public static boolean isNormal(String state) {
        return NORMAL.equals(state);
    }

    public static boolean isTesting(ApplicationInfo equipment) {
        return TEST.equals(getDevFirstState(equipment));
    }
    public static boolean isTesting(String state) {
        return TEST.equals(state);
    }

    public static boolean isMute(ApplicationInfo equipment) {
        return MUTE.equals(getDevFirstState(equipment));
    }
    public static boolean isMute(String state) {
        return MUTE.equals(state);
    }

    public static boolean isPirTeared(ApplicationInfo equipment) {
        return PIR_TEARED.equals(getDevFirstState(equipment));
    }

    public static boolean isDoorNotClosed(ApplicationInfo equipment) {
        return DOOR_NOT_CLOSED.equals(getDevFirstState(equipment));
    }

    public static boolean isLowBattery(int battery) {
        return (battery <= 15);
    }
    public static boolean isLowBattery(String state) {
        return isLowBattery(getDevBatteryLevel(state));
    }

    public static int getDevBatteryLevel(ApplicationInfo equipment) {
        return Integer.parseInt(equipment.getState().substring(2, 4), 16);
    }
    public static int getDevBatteryLevel(String state) {
        return Integer.parseInt(state, 16);
    }

    public static String getDevFirstState(ApplicationInfo equipment) {
        return equipment.getState().substring(4, 6);
    }

    public static String getDevSecondState(ApplicationInfo equipment) {
        return equipment.getState().substring(6, 8);
    }
}
