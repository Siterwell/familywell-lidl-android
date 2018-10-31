package me.hekr.sthome.model;

import android.content.Context;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import me.hekr.sthome.model.modelbean.EquipmentBean;

/**
 * Created by jishu0001 on 2016/12/9.
 */
public class ResolveScene {
    private String code ;
    private Context context;
    private SceneModelPojo sp;
    private List<EquipmentBean> input;
    private List<EquipmentBean> output;
    private boolean target;

    public ResolveScene(Context context, String code){
        this.context = context;
        this.code = code;
        sp = new SceneModelPojo();
        sp.setLength(code.substring(0,4));
    }
    public boolean isTarget(){
        int sLength = Integer.parseInt(sp.getLength(),16)-16;
        if (sLength >= 11){
            sp.setSceneNum(String.valueOf(Integer.parseInt(code.substring(4,6),16)));
            sp.setCondition(code.substring(38,40));
            sp.setTimer(timeHextoO(code.substring(40,46),6));
            sp.setClickAction(code.substring(46,48));
            sp.setInform(code.substring(48,50));
            sp.setInputNum(code.substring(50,52));
            sp.setOutputNum(code.substring(52,54));
            target = true;
        }else {
            target = false;
        }
        return target;
    }
    public SceneModelPojo getSp(){
        return sp;
    }
    public List<EquipmentBean> getInput(){
        int iLength = Integer.parseInt(sp.getInputNum(),16);
        String inputCode = code.substring(54,54+iLength*12);
        input = new ArrayList<>();
        if(!"000000".equals(sp.getTimer())){
            EquipmentBean equipmentBean = new EquipmentBean();
            equipmentBean.setEqid("IN");
            equipmentBean.setEquipmentDesc("TIMER");
            equipmentBean.setEquipmentName("timer");
            equipmentBean.setState(sp.getTimer());
            input.add(equipmentBean);
        }

        if("ab".equals(sp.getClickAction()) || "AB".equals(sp.getClickAction()) ){
            EquipmentBean equipmentBean = new EquipmentBean();
            equipmentBean.setEqid("CLICK");
            equipmentBean.setEquipmentDesc("CLICK");
            equipmentBean.setEquipmentName("click");
            equipmentBean.setState(sp.getClickAction());
            input.add(equipmentBean);
        }

        if(iLength>0){
            for(int i = 1 ; i<= iLength;i++){
                EquipmentBean equipmentBean = new EquipmentBean();
                equipmentBean.setEqid(String.valueOf(Integer.parseInt(inputCode.substring(0,4),16)));
                equipmentBean.setState(inputCode.substring(4,12));
                input.add(equipmentBean);
                if(i < iLength){
                    inputCode = inputCode.substring(12,inputCode.length());
                }
            }
        }
        return input;
    }
    public List<EquipmentBean> getOutput(){
        output = new ArrayList<>();

        int oLength = Integer.parseInt(sp.getOutputNum(),16);
        int iLength = Integer.parseInt(sp.getInputNum(),16);
        String outputCode = code.substring(54+iLength*12,code.length());

        if("AC".equals(sp.getInform().toUpperCase()) ){
            EquipmentBean equipmentBean = new EquipmentBean();
            equipmentBean.setEqid("PHONE");
            equipmentBean.setEquipmentDesc("PHONE");
            equipmentBean.setEquipmentName("phone");
            equipmentBean.setState(sp.getInform());
            output.add(equipmentBean);
        }

        int outNum = outputCode.length()/16;
        for(int i = 1 ; i<=outNum;i++){
            String delay = outputCode.substring(0,4);
            String eqid = outputCode.substring(4,8);
            String status = outputCode.substring(8,16);
            if(!"0000".equals(delay)){
                EquipmentBean equipmentBean = new EquipmentBean();
                equipmentBean.setState(timeHextoO(delay,4));
                equipmentBean.setEquipmentDesc("DELAY");
                equipmentBean.setEqid("OUT");
                equipmentBean.setEquipmentName("Delay");
                output.add(equipmentBean);
                EquipmentBean equipmentBean1 = new EquipmentBean();
                equipmentBean1.setEqid(String.valueOf(Integer.parseInt(eqid,16)));
                equipmentBean1.setState(status);
                output.add(equipmentBean1);

            }else {
                EquipmentBean equipmentBean1 = new EquipmentBean();
                equipmentBean1.setEqid(String.valueOf(Integer.parseInt(eqid,16)));
                equipmentBean1.setState(status);
                output.add(equipmentBean1);
            }
            if(i < outNum){
                outputCode = outputCode.substring(16,outputCode.length());
            }
        }
        return output;
    }

    /**
     * @param time
     * @return
     */
    private String timeHextoO(String time,int num){
        String newTime = null;
        switch (num){
            case 6:
                if(!TextUtils.isEmpty(time)&& time.length() == 6){
                    String day = time.substring(0,2);
                    String hour = time.substring(2,4);
                    String min = time.substring(4,6);
                    String newHour =
                            String.valueOf(Integer.parseInt(hour,16)).length()==2 ?
                                    String.valueOf(Integer.parseInt(hour,16)): "0"+String.valueOf(Integer.parseInt(hour,16));
                    String newMin = String.valueOf(Integer.parseInt(min,16)).length()==2 ?
                            String.valueOf(Integer.parseInt(min,16)): "0"+String.valueOf(Integer.parseInt(min,16));
                    newTime = day+newHour+newMin;
                }
                break;
            case 4:
                if(!TextUtils.isEmpty(time)&& time.length() == 4){
                    String min = time.substring(0,2);
                    String sec = time.substring(2,4);
                    String newMin =
                            String.valueOf(Integer.parseInt(min,16)).length()==2 ?
                                    String.valueOf(Integer.parseInt(min,16)): "0"+String.valueOf(Integer.parseInt(min,16));
                    String newSec = String.valueOf(Integer.parseInt(sec,16)).length()==2 ?
                            String.valueOf(Integer.parseInt(sec,16)): "0"+String.valueOf(Integer.parseInt(sec,16));
                    newTime = newMin+newSec;
                }
                break;
            default:
                break;
        }

        return newTime;
    }


}
