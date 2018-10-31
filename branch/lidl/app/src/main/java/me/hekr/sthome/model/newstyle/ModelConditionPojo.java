package me.hekr.sthome.model.newstyle;

import java.util.List;

import me.hekr.sthome.model.modelbean.EquipmentBean;
import me.hekr.sthome.model.modelbean.SceneBean;

/**
 * Created by gc-0001 on 2016/12/14.
 */
public class ModelConditionPojo {

    //target判断是否是再次进入,the first time,this value is null; but the second time is "second"
    //condition:this is "input" for from input gridView; else is from output gridview;
    public String name,condition;

    public String trigger;

    public EquipmentBean device;

    public List<EquipmentBean> input,output;

    public SceneBean sb;

    public boolean modify;

    public int position;

    private static ModelConditionPojo instance = null;

    private ModelConditionPojo(){
        position = -1;
    }


    public static ModelConditionPojo getInstance(){
        if (instance == null) {
//            synchronized (ConnectionPojo.class) {
//                if (instance == null) {
            return instance = new ModelConditionPojo();
//                }
//            }
        }
        return instance;
    }
    public void cleanModleCondition(){
        name = null;
        trigger = null;
        condition = null;
        input = null;
        output = null;
        device = null;
        modify = false;
        position = -1;
        sb = null;
    }
}
