package me.hekr.sthome.commonBaseView;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import java.util.List;

import me.hekr.sthome.DragFolderwidget.ApplicationInfo;
import me.hekr.sthome.R;
import me.hekr.sthome.equipment.adapter.EquipmentRecyclerAdapter;
import me.hekr.sthome.equipment.detail.ButtonDetailActivity;
import me.hekr.sthome.equipment.detail.Channel2SocketDetailActivity;
import me.hekr.sthome.equipment.detail.CoDetailActivity;
import me.hekr.sthome.equipment.detail.CurtainDetailActivity;
import me.hekr.sthome.equipment.detail.CxSmDetailActivity;
import me.hekr.sthome.equipment.detail.DimmingModuleDetailActivity;
import me.hekr.sthome.equipment.detail.DoorDetailActivity;
import me.hekr.sthome.equipment.detail.GasDetailActivity;
import me.hekr.sthome.equipment.detail.GuardDetailActivity;
import me.hekr.sthome.equipment.detail.LampDetailActivity;
import me.hekr.sthome.equipment.detail.LockDetailActivity;
import me.hekr.sthome.equipment.detail.ModeButtonDetailActivity;
import me.hekr.sthome.equipment.detail.PirDetailActivity;
import me.hekr.sthome.equipment.detail.SmDetailActivity;
import me.hekr.sthome.equipment.detail.SocketDetailActivity;
import me.hekr.sthome.equipment.detail.SosDetailActivity;
import me.hekr.sthome.equipment.detail.THCheckDetailActivity;
import me.hekr.sthome.equipment.detail.TempControlDetailActivity;
import me.hekr.sthome.equipment.detail.ThermalDetailActivity;
import me.hekr.sthome.equipment.detail.ValveDetailActivity;
import me.hekr.sthome.equipment.detail.WaterDetailActivity;
import me.hekr.sthome.main.MainActivity;
import me.hekr.sthome.model.modelbean.EquipmentBean;
import me.hekr.sthome.model.modeldb.EquipDAO;
import me.hekr.sthome.tools.ConnectionPojo;
import me.hekr.sthome.tools.LOG;
import me.hekr.sthome.tools.NameSolve;

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
        list = updateDeviceList(list);
        LOG.D("FrameLayout", "[RYAN] getDeviceListView > list size = " + list.size());

        recyclerView = root.findViewById(R.id.rv_device_list);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        EquipmentRecyclerAdapter adapter = new EquipmentRecyclerAdapter(getContext(), list, this);
        recyclerView.setAdapter(adapter);
    }

    private List<ApplicationInfo> updateDeviceList(List<ApplicationInfo> list) {
        for (int i = 0; i < list.size(); i++) {
            if(NameSolve.DOOR_CHECK.equals(NameSolve.getEqType(list.get(i).getEquipmentDesc()))) {      //menci
                list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.d10));
                if(list.get(i).getState()!=null&& list.get(i).getState().length() == 8){
                    int quantity = Integer.parseInt(list.get(i).getState().substring(2, 4),16);

                    if ("AA".equals(list.get(i).getState().substring(4, 6))) {
//                    holder.s.setText("关门");
                        if(quantity <= 15){
                            list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.y10));
                        }else{
                            list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.g10));
                        }
                    } else if ("55".equals(list.get(i).getState().substring(4, 6))) {
//                    holder.s.setText("开门");
                        list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e10));
                    }else if("66".equals(list.get(i).getState().substring(4,6))){
//                    holder.s.setText("门已打开");
                        list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e10));
                    }

                }
            }else if(NameSolve.SOCKET.equals(NameSolve.getEqType(list.get(i).getEquipmentDesc()))){   //socket
                list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.d7));
                if (list.get(i).getState() != null && list.get(i).getState().length() == 8) {
                    String ddd = list.get(i).getState().substring(6, 8);
                    if ("01".equals(ddd)) {
//                    holder.s.setText("闭合");
                        list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e7));
                    } else if ("00".equals(ddd)) {
//                    holder.s.setText("断开");
                        list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.g7));
                    }
                }
            }else if(NameSolve.TWO_SOCKET.equals(NameSolve.getEqType(list.get(i).getEquipmentDesc()))){   //socket
                list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.d20));
                if (list.get(i).getState() != null && list.get(i).getState().length() == 8) {
                    String ddd = list.get(i).getState().substring(4, 8);
                    if ("0301".equals(ddd) || "0302".equals(ddd) || "0303".equals(ddd)) {
//                    holder.s.setText("闭合");
                        list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e20));
                    } else if ("0300".equals(ddd)) {
//                    holder.s.setText("断开");
                        list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.g20));
                    }
                }
            }else if(NameSolve.PIR_CHECK.equals(NameSolve.getEqType(list.get(i).getEquipmentDesc()))) {  //pir
                list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.d1));
                if (list.get(i).getState() != null && list.get(i).getState().length() == 8) {
                    int quantity = Integer.parseInt(list.get(i).getState().substring(2, 4),16);

                    String ddd = list.get(i).getState().substring(4, 6);
                    if ("AA".equals(list.get(i).getState().substring(4, 6))) {
//                    holder.s.setText("正常");
                        if(quantity <= 15){
                            list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.y1));
                        }else{
                            list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.g1));
                        }

                    } else if ("55".equals(list.get(i).getState().substring(4, 6))) {
//                    holder.s.setText("有人");
                        list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e1));
                    }else if("11".equals(list.get(i).getState().substring(4,6))){
//                    holder.s.setText("故障");
                        list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e1));
                    }else if("A0".equals(list.get(i).getState().substring(4,6))) {
//                    holder.s.setText("拆除");
                        list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e1));
                    }
                }
            }else if(NameSolve.SOS_KEY.equals(NameSolve.getEqType(list.get(i).getEquipmentDesc()))) {  //sod
                list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.d2));
                if(list.get(i).getState() != null && list.get(i).getState().length() == 8){
                    int quantity = Integer.parseInt(list.get(i).getState().substring(2, 4),16);

                    String ddd = list.get(i).getState().substring(4, 6);
                    if ("AA".equals(list.get(i).getState().substring(4, 6))) {
//                    holder.s.setText("关门");
                        if(quantity <= 15){
                            list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.y2));
                        }else {
                            list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.g2));
                        }

                    } else if ("55".equals(list.get(i).getState().substring(4, 6))) {
//                    holder.s.setText("开门");
                        list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e2));
                    }else if("66".equals(list.get(i).getState().substring(4,6))){
//                    holder.s.setText("门已打开");
                        list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e2));
                    }
                }
            }else if(NameSolve.SM_ALARM.equals(NameSolve.getEqType(list.get(i).getEquipmentDesc()))) {  //sm
                list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.d8));
                if (list.get(i).getState() != null && list.get(i).getState().length() == 8) {
                    int quantity = Integer.parseInt(list.get(i).getState().substring(2, 4),16);

                    if ("11".equals(list.get(i).getState().substring(4, 6))) {
                        list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e8));
                    } else if ("55".equals(list.get(i).getState().substring(4, 6))) {
//                    holder.s.setText("有人");
                        list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e8));
                    }else if("AA".equals(list.get(i).getState().substring(4,6))){
//                    holder.s.setText("故障");
                        if(quantity <= 15){
                            list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.y8));
                        }else{
                            list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.g8));
                        }

                    }else if("BB".equals(list.get(i).getState().substring(4,6))) {
//                    holder.s.setText("拆除");
//                    holder.imageView.setImageResource(R.drawable.d1);
                        list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e8));
                    }else if("50".equals(list.get(i).getState().substring(4,6))){
                        list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e8));
                    }
                }
            }else if(NameSolve.CO_ALARM.equals(NameSolve.getEqType(list.get(i).getEquipmentDesc()))){  //end
                list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.d9));
                if (list.get(i).getState() != null && list.get(i).getState().length() == 8) {
                    int quantity = Integer.parseInt(list.get(i).getState().substring(2, 4),16);
                    if ("11".equals(list.get(i).getState().substring(4, 6))) {
                        list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e9));
                    } else if ("55".equals(list.get(i).getState().substring(4, 6))) {
                        //                    holder.s.setText("有人");
                        list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e9));
                    } else if ("AA".equals(list.get(i).getState().substring(4, 6))) {
                        //                    holder.s.setText("故障");
                        if(quantity <= 15){
                            list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.y9));
                        }else{
                            list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.g9));
                        }
                    } else if ("BB".equals(list.get(i).getState().substring(4, 6))) {
                        //                    holder.s.setText("拆除");
                        //                    holder.imageView.setImageResource(R.drawable.d1);
                        list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e9));
                    } else if ("50".equals(list.get(i).getState().substring(4, 6))) {
                        list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e9));
                    }
                }
            }else if(NameSolve.WT_ALARM.equals(NameSolve.getEqType(list.get(i).getEquipmentDesc()))){  //end
                list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.d5));
                if (list.get(i).getState() != null && list.get(i).getState().length() == 8) {
                    int quantity = Integer.parseInt(list.get(i).getState().substring(2, 4),16);
                    if ("11".equals(list.get(i).getState().substring(4, 6))) {
                        list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e5));
                    } else if ("55".equals(list.get(i).getState().substring(4, 6))) {
                        //                    holder.s.setText("有人");
                        list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e5));
                    } else if ("AA".equals(list.get(i).getState().substring(4, 6))) {
                        //                    holder.s.setText("故障");
                        if(quantity <= 15){
                            list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.y5));
                        }else{
                            list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.g5));
                        }
                    } else if ("BB".equals(list.get(i).getState().substring(4, 6))) {
                        //                    holder.s.setText("拆除");
                        //                    holder.imageView.setImageResource(R.drawable.d1);
                        list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e5));
                    } else if ("50".equals(list.get(i).getState().substring(4, 6))) {
                        list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e5));
                    }
                }
            }else if(NameSolve.TH_CHECK.equals(NameSolve.getEqType(list.get(i).getEquipmentDesc()))){  //end
                list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.d11));
                if (list.get(i).getState() != null && list.get(i).getState().length() == 8) {
                    String ddd = list.get(i).getState().substring(2, 4);
                    String temp = list.get(i).getState().substring(4,6);
                    String temp2 = Integer.toBinaryString(Integer.parseInt(temp,16));
                    String humidity = list.get(i).getState().substring(6,8);
                    int realH = Integer.parseInt(humidity,16);
                    String realT;
                    if (temp2.length()==8){
                        realT = "-"+ (128 - Integer.parseInt(temp2.substring(1,temp2.length()),2));
                    }else{
                        realT = "" + Integer.parseInt(temp2,2);
                    }


                    if(Integer.parseInt(realT)>100 || Integer.parseInt(realT) < -40 || realH > 100 || realH < 0){
                        list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.d11));
                    }else{

                        int qqqq = Integer.parseInt(ddd,16);
                        if( qqqq <= 15 ){
                            list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.y11));
                        }else{
                            list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.g11));
                        }
                    }


                }
            }else if(NameSolve.LAMP.equals(NameSolve.getEqType(list.get(i).getEquipmentDesc()))){   //socket
                list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.d12));
                if (list.get(i).getState() != null && list.get(i).getState().length() == 8) {
                    String ddd = list.get(i).getState().substring(6, 8);
                    if("38".equals(ddd)){
                        list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e12));
                    }else{
                        list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.g12));
                    }
                }
            }else if(NameSolve.GUARD.equals(NameSolve.getEqType(list.get(i).getEquipmentDesc()))){   //socket
                list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.d14));
                if (list.get(i).getState() != null && list.get(i).getState().length() == 8) {
                    String ddd = list.get(i).getState().substring(6, 8);
                    if ("55".equals(ddd)) {
//                    holder.s.setText("闭合");
                        list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e14));
                    } else if ("AA".equals(ddd)) {
//                    holder.s.setText("断开");
                        list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.g14));
                    }
                }
            }else if(NameSolve.VALVE.equals(NameSolve.getEqType(list.get(i).getEquipmentDesc()))){   //socket
                list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.d15));
                if (list.get(i).getState() != null && list.get(i).getState().length() == 8) {
                    String ddd = list.get(i).getState().substring(6, 8);
                    if ("01".equals(ddd)) {
//                    holder.s.setText("闭合");
                        list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e15));
                    } else if ("00".equals(ddd)) {
//                    holder.s.setText("断开");
                        list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.g15));
                    }
                }
            }else if(NameSolve.CURTAIN.equals(NameSolve.getEqType(list.get(i).getEquipmentDesc()))){   //socket
                list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.d13));
                if (list.get(i).getState() != null && list.get(i).getState().length() == 8) {
                    String ddd = list.get(i).getState().substring(6, 8);
                    if ( ddd!=null && !"".equals(ddd)) {
                        list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.g13));
                    }
                }
            }else if(NameSolve.BUTTON.equals(NameSolve.getEqType(list.get(i).getEquipmentDesc()))){   //socket
//            holder.imageView.setImageResource(mImage[16]);
                list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.d18));
                if (list.get(i).getState() != null && list.get(i).getState().length() == 8) {
                    int quantity = Integer.parseInt(list.get(i).getState().substring(2, 4),16);
                    if ("01".equals(list.get(i).getState().substring(4, 6)) || "AA".equals(list.get(i).getState().substring(4, 6))) {
                        if(quantity <= 15){
                            list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.y18));
                        }else{
                            list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.g18));
                        }
                    }
                }
            }else if(NameSolve.CXSM_ALARM.equals(NameSolve.getEqType(list.get(i).getEquipmentDesc()))) {  //sm
                if (list.get(i).getState() != null && list.get(i).getState().length() == 8) {

                    String ddd = list.get(i).getState().substring(4, 6);
                    if ("12".equals(ddd)) {
                        list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.y8));
                    } else if ("17".equals(ddd)) {
                        list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e8));
                    } else if ("19".equals(ddd)) {
                        list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e8));
                    }else if ("15".equals(ddd)) {
                        list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.y8));
                    }else if ("1B".equals(ddd)) {
                        list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.g8));
                    }else if("AA".equals(ddd)){
                        int quantity = Integer.parseInt(list.get(i).getState().substring(2, 4), 16);
                        if (quantity <= 15) {
                            list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.y8));
                        }else{
                            list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.g8));
                        }
                    }else{
                        list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.d8));
                    }
                }else {
                    list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.d8));
                }
            }else if(NameSolve.GAS_ALARM.equals(NameSolve.getEqType(list.get(i).getEquipmentDesc()))){  //end
                list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.d3));
                if (list.get(i).getState() != null && list.get(i).getState().length() == 8) {
                    if ("11".equals(list.get(i).getState().substring(4, 6))) {
                        list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e3));
                    } else if ("55".equals(list.get(i).getState().substring(4, 6))) {
                        //                    holder.s.setText("有人");
                        list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e3));
                    } else if ("AA".equals(list.get(i).getState().substring(4, 6))) {
                        //                    holder.s.setText("故障");
                        list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.g3));
                    } else if ("BB".equals(list.get(i).getState().substring(4, 6))) {
                        //                    holder.s.setText("拆除");
                        //                    holder.imageView.setImageResource(R.drawable.d1);
                        list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e3));
                    } else if ("50".equals(list.get(i).getState().substring(4, 6))) {
                        list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e3));
                    }
                }
            }else if(NameSolve.THERMAL_ALARM.equals(NameSolve.getEqType(list.get(i).getEquipmentDesc()))){  //end
                list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.d4));
                if (list.get(i).getState() != null && list.get(i).getState().length() == 8) {
                    int quantity = Integer.parseInt(list.get(i).getState().substring(2, 4),16);
                    if ("11".equals(list.get(i).getState().substring(4, 6))) {
                        list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e4));
                    } else if ("55".equals(list.get(i).getState().substring(4, 6))) {
                        //                    holder.s.setText("有人");
                        list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e4));
                    } else if ("AA".equals(list.get(i).getState().substring(4, 6))) {
                        //                    holder.s.setText("故障");
                        if(quantity <= 15){
                            list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.y4));
                        }else{
                            list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.g4));
                        }
                    } else if ("BB".equals(list.get(i).getState().substring(4, 6))) {
                        //                    holder.s.setText("拆除");
                        //                    holder.imageView.setImageResource(R.drawable.d1);
                        list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e4));
                    } else if ("50".equals(list.get(i).getState().substring(4, 6))) {
                        list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e4));
                    }
                }
            }else if(NameSolve.MODE_BUTTON.equals(NameSolve.getEqType(list.get(i).getEquipmentDesc()))){  //end
                list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.d16));
                if (list.get(i).getState() != null && list.get(i).getState().length() == 8) {
                    int quantity = Integer.parseInt(list.get(i).getState().substring(2, 4),16);
                    if ("11".equals(list.get(i).getState().substring(4, 6))) {
                        list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e16));
                    } else if ("55".equals(list.get(i).getState().substring(4, 6))) {
                        //                    holder.s.setText("有人");
                        list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e16));
                    } else if ("AA".equals(list.get(i).getState().substring(4, 6))
                            || "01".equals(list.get(i).getState().substring(4, 6))
                            || "02".equals(list.get(i).getState().substring(4, 6))
                            || "04".equals(list.get(i).getState().substring(4, 6))
                            || "08".equals(list.get(i).getState().substring(4, 6))
                            ) {
                        //                    holder.s.setText("故障");
                        if(quantity <= 15){
                            list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.y16));
                        }else{
                            list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.g16));
                        }
                    } else if ("BB".equals(list.get(i).getState().substring(4, 6))) {
                        //                    holder.s.setText("拆除");
                        //                    holder.imageView.setImageResource(R.drawable.d1);
                        list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e16));
                    } else if ("50".equals(list.get(i).getState().substring(4, 6))) {
                        list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e16));
                    }
                }
            }else if(NameSolve.LOCK.equals(NameSolve.getEqType(list.get(i).getEquipmentDesc()))){  //end
                list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.d19));
                if (list.get(i).getState() != null && list.get(i).getState().length() == 8) {
                    String ds = list.get(i).getState().substring(4, 6);
                    int quantity = Integer.parseInt(list.get(i).getState().substring(2, 4),16);
                    if ("AA".equals(ds) || "60".equals(ds) || "AB".equals(ds) || "55".equals(ds) || "56".equals(ds)) {
                        if(quantity <= 15){
                            list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.y19));
                        }else{
                            list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.g19));
                        }
                    } else if ("51".equals(ds) || "52".equals(ds) || "53".equals(ds)  || "10".equals(ds) || "20".equals(ds) || "30".equals(ds)) {
                        list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.e19));
                    } else if ("40".equals(ds)) {
                        list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.y19));
                    }
                }
            }else if(NameSolve.TEMP_CONTROL.equals(NameSolve.getEqType(list.get(i).getEquipmentDesc()))){  //end
                list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.g14));
                if (list.get(i).getState() != null && list.get(i).getState().length() == 8) {
                    int quantity = Integer.parseInt(list.get(i).getState().substring(2, 4),16);
                    if(quantity <= 15){
                        list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.y14));
                    }else {
                        list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.g14));
                    }
                }
            }else if(NameSolve.DIMMING_MODULE.equals(NameSolve.getEqType(list.get(i).getEquipmentDesc()))){  //end
                list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.d21));
                if (list.get(i).getState() != null && list.get(i).getState().length() == 8) {
                    int quantity = Integer.parseInt(list.get(i).getState().substring(2, 4),16);
                    String draw = list.get(i).getState().substring(4,6);
                    int liangdu = Integer.parseInt(list.get(i).getState().substring(6,8),16);
                    if(("00".equals(draw) || "01".equals(draw)) && (liangdu>=0&&liangdu<=100)){
                        if(quantity <= 15){
                            list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.y21));
                        }else {
                            list.get(i).setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.g21));
                        }
                    }


                }
            }
        }

        return list;
    }

    private void onAppClicked(EquipmentBean device) {
        Intent detail = null;
        
        String type = NameSolve.getEqType(device.getEquipmentDesc());
        if(NameSolve.DOOR_CHECK.equals(type)) {      //menci
            detail = new Intent(activity, DoorDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("device", device);
            detail.putExtras(bundle);

        }else if(NameSolve.SOCKET.equals(type)){   //chazuo
            detail = new Intent(activity, SocketDetailActivity.class);
        }else if(NameSolve.PIR_CHECK.equals(type)) {  //pir
            detail = new Intent(activity, PirDetailActivity.class);
        }else if(NameSolve.SOS_KEY.equals(type)) {  //sos
            detail = new Intent(activity, SosDetailActivity.class);
            //Bundle bundle = new Bundle();
            //bundle.putSerializable("device", device);
            detail.putExtra("device",device);
        }else if(NameSolve.SM_ALARM.equals(type)) {  //sm
            detail = new Intent(activity, SmDetailActivity.class);
        }else if(NameSolve.CO_ALARM.equals(type)) {  //co
            detail = new Intent(activity, CoDetailActivity.class);
        }else if(NameSolve.GAS_ALARM.equals(type)) {  //co
            detail = new Intent(activity, GasDetailActivity.class);
        }else if(NameSolve.WT_ALARM.equals(type)) {  //water
            detail = new Intent(activity, WaterDetailActivity.class);
        }else if(NameSolve.TH_CHECK.equals(type)) {  //temprature and hib
            detail = new Intent(activity, THCheckDetailActivity.class);
        }else if(NameSolve.LAMP.equals(type)) {  //lamp
            detail = new Intent(activity, LampDetailActivity.class);
        }else if(NameSolve.GUARD.equals(type)) {  //door guard
            detail = new Intent(activity, GuardDetailActivity.class);
        }else if(NameSolve.VALVE.equals(type)) {  //door guard
            detail = new Intent(activity, ValveDetailActivity.class);
        }else if(NameSolve.BUTTON.equals(type)) {  //door guard
            detail = new Intent(activity, ButtonDetailActivity.class);
        }else if(NameSolve.CURTAIN.equals(type)) {  //door guard
            detail = new Intent(activity, CurtainDetailActivity.class);
        }else if(NameSolve.CXSM_ALARM.equals(type)) {  //door guard
            detail = new Intent(activity, CxSmDetailActivity.class);
        }else if(NameSolve.THERMAL_ALARM.equals(type)) {  //door guard
            detail = new Intent(activity, ThermalDetailActivity.class);
        }else if(NameSolve.MODE_BUTTON.equals(type)) {  //door guard
            detail = new Intent(activity, ModeButtonDetailActivity.class);
        }else if(NameSolve.LOCK.equals(type)) {  //door guard
            detail = new Intent(activity, LockDetailActivity.class);
        }else if(NameSolve.TWO_SOCKET.equals(type)) {  //door guard
            detail = new Intent(activity, Channel2SocketDetailActivity.class);
        }else if(NameSolve.TEMP_CONTROL.equals(type)) {  //door guard
            detail = new Intent(activity, TempControlDetailActivity.class);
        }else if(NameSolve.DIMMING_MODULE.equals(type)) {  //door guard
            detail = new Intent(activity, DimmingModuleDetailActivity.class);
        }

        if (detail != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("device", device);
            detail.putExtras(bundle);
            activity.startActivity(detail);
        }
    }

    public FrameLayout getRoot() {
        return root;
    }

    @Override
    public void onItemClicked(EquipmentBean device) {
//        activity.jumpToDevice();
        onAppClicked(device);
    }

}
