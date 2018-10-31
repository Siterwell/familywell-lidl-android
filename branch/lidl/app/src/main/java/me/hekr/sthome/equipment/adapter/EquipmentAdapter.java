package me.hekr.sthome.equipment.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
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
 * Created by jishu0001 on 2016/9/9.
 */
public class EquipmentAdapter extends BaseAdapter {
    private Context context;
    private int listCellId = 0;
    private List<EquipmentBean> lists;
    private ViewHolder holder;
    private Drawable image1,image2,image3,image4,image5,image6;
    private int[] mImage= {
            -1,
            R.drawable.d1,
            R.drawable.d2,
            R.drawable.d3,
            R.drawable.d4,
            R.drawable.d5,
            R.drawable.d6,
            R.drawable.d7,
            R.drawable.d8,
            R.drawable.d9,
            R.drawable.d10,
            R.drawable.d11,
            R.drawable.d12,
            R.drawable.d13,
            R.drawable.d14,
            R.drawable.d15,
            R.drawable.d16

    };
//    private Button a,c,d;
//    private TextView s;
    public EquipmentAdapter(Context context, List<EquipmentBean> lists){
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

    public void remove(EquipmentBean eq){
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
            convertView = LayoutInflater.from(context).inflate(R.layout.equipment_image_text_cell,null);
            holder.imageView= (ImageView) convertView.findViewById(R.id.cellImageView);
            holder.textView = (TextView) convertView.findViewById(R.id.cellTextView);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
//        holder.textView.setText(eq.getEquipmentName());

            if (NameSolve.DOOR_CHECK.equals(NameSolve.getEqType(eq.getEquipmentDesc()))) {      //menci
                holder.imageView.setImageResource(mImage[10]);
                holder.textView.setText(eq.getEquipmentName());
                if (eq.getState() != null && eq.getState().length() == 8) {
                    int quantity = Integer.parseInt(eq.getState().substring(4, 6), 16);
                    if (quantity <= 15) {
                        holder.imageView.setImageResource(R.drawable.y10);
                    }
                    String ddd = eq.getState().substring(4, 6);
                    if ("AA".equals(eq.getState().substring(4, 6))) {
//                    holder.s.setText("关门");
                        holder.imageView.setImageResource(R.drawable.g10);
                    } else if ("55".equals(eq.getState().substring(4, 6))) {
//                    holder.s.setText("开门");
                        holder.imageView.setImageResource(R.drawable.e10);
                    } else if ("66".equals(eq.getState().substring(4, 6))) {
//                    holder.s.setText("门已打开");
                        holder.imageView.setImageResource(R.drawable.g10);
                    }

                }
            } else if (NameSolve.SOCKET.equals(NameSolve.getEqType(eq.getEquipmentDesc()))) {   //socket
                holder.imageView.setImageResource(mImage[7]);
                holder.textView.setText(eq.getEquipmentName());
                if (eq.getState() != null && eq.getState().length() == 8) {
                    String ddd = eq.getState().substring(6, 8);
                    if ("01".equals(ddd)) {
//                    holder.s.setText("闭合");
                        holder.imageView.setImageResource(R.drawable.e7);
                    } else if ("00".equals(ddd)) {
//                    holder.s.setText("断开");
                        holder.imageView.setImageResource(R.drawable.g7);
                    }
                }
            } else if (NameSolve.PIR_CHECK.equals(NameSolve.getEqType(eq.getEquipmentDesc()))) {  //pir
                holder.imageView.setImageResource(mImage[1]);
                holder.textView.setText(eq.getEquipmentName());
                if (eq.getState() != null && eq.getState().length() == 8) {
                    int quantity = Integer.parseInt(eq.getState().substring(4, 6), 16);
                    if (quantity <= 15) {
                        holder.imageView.setImageResource(R.drawable.y1);
                    }
                    String ddd = eq.getState().substring(4, 6);
                    if ("AA".equals(eq.getState().substring(4, 6))) {
//                    holder.s.setText("正常");
                        holder.imageView.setImageResource(R.drawable.g1);
                    } else if ("55".equals(eq.getState().substring(4, 6))) {
//                    holder.s.setText("有人");
                        holder.imageView.setImageResource(R.drawable.e1);
                    } else if ("11".equals(eq.getState().substring(4, 6))) {
//                    holder.s.setText("故障");
                        holder.imageView.setImageResource(R.drawable.y1);
                    } else if ("A0".equals(eq.getState().substring(4, 6))) {
//                    holder.s.setText("拆除");
                        holder.imageView.setImageResource(R.drawable.e1);
                    }
                }
            } else if (NameSolve.SOS_KEY.equals(NameSolve.getEqType(eq.getEquipmentDesc()))) {  //sod
                holder.imageView.setImageResource(mImage[2]);
                holder.textView.setText(eq.getEquipmentName());
                if (eq.getState() != null && eq.getState().length() == 8) {
                    int quantity = Integer.parseInt(eq.getState().substring(4, 6), 16);
                    if (quantity <= 15) {
                        holder.imageView.setImageResource(R.drawable.y2);
                    }
                    String ddd = eq.getState().substring(4, 6);
                    if ("AA".equals(eq.getState().substring(4, 6))) {
//                    holder.s.setText("关门");
                        holder.imageView.setImageResource(R.drawable.g2);
                    } else if ("55".equals(eq.getState().substring(4, 6))) {
//                    holder.s.setText("开门");
                        holder.imageView.setImageResource(R.drawable.e2);
                    } else if ("66".equals(eq.getState().substring(4, 6))) {
//                    holder.s.setText("门已打开");
                        holder.imageView.setImageResource(R.drawable.g2);
                    }
                }
            } else if (NameSolve.SM_ALARM.equals(NameSolve.getEqType(eq.getEquipmentDesc()))) {  //sm
                holder.imageView.setImageResource(mImage[8]);
                holder.textView.setText(eq.getEquipmentName());
                if (eq.getState() != null && eq.getState().length() == 8) {
                    int quantity = Integer.parseInt(eq.getState().substring(4, 6), 16);
                    if (quantity <= 15) {
                        holder.imageView.setImageResource(R.drawable.y8);
                    }
                    String ddd = eq.getState().substring(4, 6);
                    if ("11".equals(eq.getState().substring(4, 6))) {
                        holder.imageView.setImageResource(R.drawable.y8);
                    } else if ("55".equals(eq.getState().substring(4, 6))) {
//                    holder.s.setText("有人");
                        holder.imageView.setImageResource(R.drawable.e8);
                    } else if ("AA".equals(eq.getState().substring(4, 6))) {
//                    holder.s.setText("故障");
                        holder.imageView.setImageResource(R.drawable.g8);
                    } else if ("BB".equals(eq.getState().substring(4, 6))) {
//                    holder.s.setText("拆除");
//                    holder.imageView.setImageResource(R.drawable.d1);
                        holder.imageView.setImageResource(R.drawable.g8);
                    } else if ("50".equals(eq.getState().substring(4, 6))) {
                        holder.imageView.setImageResource(R.drawable.g8);
                    }
                }
            } else if ("END".equals(eq.getEquipmentDesc())) {  //end
                holder.imageView.setImageResource(R.drawable.add);
                holder.textView.setText(eq.getEquipmentName());
            } else if (NameSolve.CO_ALARM.equals(NameSolve.getEqType(eq.getEquipmentDesc()))) {  //end
                holder.imageView.setImageResource(mImage[9]);
                holder.textView.setText(eq.getEquipmentName());
                if (eq.getState() != null && eq.getState().length() == 8) {
                    String ddd = eq.getState().substring(4, 6);
                    if ("11".equals(eq.getState().substring(4, 6))) {
                        holder.imageView.setImageResource(R.drawable.y9);
                    } else if ("55".equals(eq.getState().substring(4, 6))) {
                        //                    holder.s.setText("有人");
                        holder.imageView.setImageResource(R.drawable.e9);
                    } else if ("AA".equals(eq.getState().substring(4, 6))) {
                        //                    holder.s.setText("故障");
                        holder.imageView.setImageResource(R.drawable.g9);
                    } else if ("BB".equals(eq.getState().substring(4, 6))) {
                        //                    holder.s.setText("拆除");
                        //                    holder.imageView.setImageResource(R.drawable.d1);
                        holder.imageView.setImageResource(R.drawable.g9);
                    } else if ("50".equals(eq.getState().substring(4, 6))) {
                        holder.imageView.setImageResource(R.drawable.g9);
                    }
                }
            } else if (NameSolve.WT_ALARM.equals(NameSolve.getEqType(eq.getEquipmentDesc()))) {  //end
                holder.imageView.setImageResource(mImage[5]);
                holder.textView.setText(eq.getEquipmentName());
                if (eq.getState() != null && eq.getState().length() == 8) {
                    String ddd = eq.getState().substring(4, 6);
                    if ("11".equals(eq.getState().substring(4, 6))) {
                        holder.imageView.setImageResource(R.drawable.y5);
                    } else if ("55".equals(eq.getState().substring(4, 6))) {
                        //                    holder.s.setText("有人");
                        holder.imageView.setImageResource(R.drawable.e5);
                    } else if ("AA".equals(eq.getState().substring(4, 6))) {
                        //                    holder.s.setText("故障");
                        holder.imageView.setImageResource(R.drawable.g5);
                    } else if ("BB".equals(eq.getState().substring(4, 6))) {
                        //                    holder.s.setText("拆除");
                        //                    holder.imageView.setImageResource(R.drawable.d1);
                        holder.imageView.setImageResource(R.drawable.g5);
                    } else if ("50".equals(eq.getState().substring(4, 6))) {
                        holder.imageView.setImageResource(R.drawable.g5);
                    }
                }
            } else if (NameSolve.TH_CHECK.equals(NameSolve.getEqType(eq.getEquipmentDesc()))) {  //end
                holder.imageView.setImageResource(mImage[11]);
                holder.textView.setText(eq.getEquipmentName());
                if (eq.getState() != null && eq.getState().length() == 8) {
                    String ddd = eq.getState().substring(2, 4);
                    String shiwei = ddd.substring(0, 1);
                    holder.imageView.setImageResource(R.drawable.g11);
                    if (!"8".equals(shiwei)) {
                        int qqqq = Integer.parseInt(ddd, 16);
                        if (qqqq <= 15) {
                            holder.imageView.setImageResource(R.drawable.y11);
                        }
                    }
                }
            } else if (NameSolve.LAMP.equals(NameSolve.getEqType(eq.getEquipmentDesc()))) {   //socket
                holder.imageView.setImageResource(mImage[12]);
                holder.textView.setText(eq.getEquipmentName());
                if (eq.getState() != null && eq.getState().length() == 8) {
                    String ddd = eq.getState().substring(6, 8);
                    if ("38".equals(ddd)) {
                        holder.imageView.setImageResource(R.drawable.e12);
                    } else {
                        holder.imageView.setImageResource(R.drawable.g12);
                    }
                }
            } else if (NameSolve.GUARD.equals(NameSolve.getEqType(eq.getEquipmentDesc()))) {   //socket
                holder.imageView.setImageResource(mImage[14]);
                holder.textView.setText(eq.getEquipmentName());
                if (eq.getState() != null && eq.getState().length() == 8) {
                    String ddd = eq.getState().substring(6, 8);
                    if ("55".equals(ddd)) {
//                    holder.s.setText("闭合");
                        holder.imageView.setImageResource(R.drawable.e14);
                    } else if ("AA".equals(ddd)) {
//                    holder.s.setText("断开");
                        holder.imageView.setImageResource(R.drawable.g14);
                    }
                }
            } else if (NameSolve.VALVE.equals(NameSolve.getEqType(eq.getEquipmentDesc()))) {   //socket
                holder.imageView.setImageResource(mImage[15]);
                holder.textView.setText(eq.getEquipmentName());
                if (eq.getState() != null && eq.getState().length() == 8) {
                    String ddd = eq.getState().substring(6, 8);
                    if ("01".equals(ddd)) {
//                    holder.s.setText("闭合");
                        holder.imageView.setImageResource(R.drawable.e15);
                    } else if ("00".equals(ddd)) {
//                    holder.s.setText("断开");
                        holder.imageView.setImageResource(R.drawable.g15);
                    }
                }
            } else if (NameSolve.CURTAIN.equals(NameSolve.getEqType(eq.getEquipmentDesc()))) {   //socket
                holder.imageView.setImageResource(mImage[13]);
                holder.textView.setText(eq.getEquipmentName());
                if (eq.getState() != null && eq.getState().length() == 8) {
                    String ddd = eq.getState().substring(6, 8);
                    if (ddd != null && !"".equals(ddd)) {
                        holder.imageView.setImageResource(R.drawable.g13);
                    }
                }
            } else if (NameSolve.BUTTON.equals(NameSolve.getEqType(eq.getEquipmentDesc()))) {   //socket
//            holder.imageView.setImageResource(mImage[16]);
                holder.textView.setText(eq.getEquipmentName());
                holder.imageView.setImageResource(R.drawable.g16);
            }else if(NameSolve.CXSM_ALARM.equals(NameSolve.getEqType(eq.getEquipmentDesc()))) {  //sm
                holder.imageView.setImageResource(mImage[8]);
                holder.textView.setText(eq.getEquipmentName());
                if (eq.getState() != null && eq.getState().length() == 8) {
                    int quantity = Integer.parseInt(eq.getState().substring(4, 6), 16);
                    if (quantity <= 15) {
                        holder.imageView.setImageResource(R.drawable.y8);
                    }
                    String ddd = eq.getState().substring(4, 6);
                    if ("AA".equals(eq.getState().substring(4, 6))) {
                        holder.imageView.setImageResource(R.drawable.g8);
                    } else if ("17".equals(eq.getState().substring(4, 6)) ||"18".equals(eq.getState().substring(4, 6)) ||"19".equals(eq.getState().substring(4, 6)) ){
                        holder.imageView.setImageResource(R.drawable.e8);
                    }else{
                        holder.imageView.setImageResource(R.drawable.g8);
                    }
                }
            } else if(NameSolve.GAS_ALARM.equals(NameSolve.getEqType(eq.getEquipmentDesc()))) {  //end
                holder.imageView.setImageResource(mImage[4]);
                holder.textView.setText(eq.getEquipmentName());
                if (eq.getState() != null && eq.getState().length() == 8) {
                    String ddd = eq.getState().substring(4, 6);
                    if ("11".equals(eq.getState().substring(4, 6))) {
                        holder.imageView.setImageResource(R.drawable.y3);
                    } else if ("55".equals(eq.getState().substring(4, 6))) {
                        //                    holder.s.setText("有人");
                        holder.imageView.setImageResource(R.drawable.e3);
                    } else if ("AA".equals(eq.getState().substring(4, 6))) {
                        //                    holder.s.setText("故障");
                        holder.imageView.setImageResource(R.drawable.g3);
                    } else if ("BB".equals(eq.getState().substring(4, 6))) {
                        //                    holder.s.setText("拆除");
                        //                    holder.imageView.setImageResource(R.drawable.d1);
                        holder.imageView.setImageResource(R.drawable.g3);
                    } else if ("50".equals(eq.getState().substring(4, 6))) {
                        holder.imageView.setImageResource(R.drawable.g3);
                    }
                }
            }else if (NameSolve.THERMAL_ALARM.equals(NameSolve.getEqType(eq.getEquipmentDesc()))) {  //end
                holder.imageView.setImageResource(mImage[4]);
                holder.textView.setText(eq.getEquipmentName());
                if (eq.getState() != null && eq.getState().length() == 8) {
                    String ddd = eq.getState().substring(4, 6);
                    if ("11".equals(eq.getState().substring(4, 6))) {
                        holder.imageView.setImageResource(R.drawable.y4);
                    } else if ("55".equals(eq.getState().substring(4, 6))) {
                        //                    holder.s.setText("有人");
                        holder.imageView.setImageResource(R.drawable.e4);
                    } else if ("AA".equals(eq.getState().substring(4, 6))) {
                        //                    holder.s.setText("故障");
                        holder.imageView.setImageResource(R.drawable.g4);
                    } else if ("BB".equals(eq.getState().substring(4, 6))) {
                        //                    holder.s.setText("拆除");
                        //                    holder.imageView.setImageResource(R.drawable.d1);
                        holder.imageView.setImageResource(R.drawable.e4);
                    } else if ("50".equals(eq.getState().substring(4, 6))) {
                        holder.imageView.setImageResource(R.drawable.e4);
                    }
                }
            }
        return convertView;
    }

    public void RefreshLists(List<EquipmentBean> lists) {
        this.lists = lists;
        notifyDataSetChanged();
    }
}

