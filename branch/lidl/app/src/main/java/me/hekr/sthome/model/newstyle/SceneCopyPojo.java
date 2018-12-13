package me.hekr.sthome.model.newstyle;

/**
 * Created by gc-0001 on 2016/12/14.
 */
public class SceneCopyPojo {


    public String code;
    public boolean first;

    private static SceneCopyPojo instance = null;

    private SceneCopyPojo(){

    }


    public static SceneCopyPojo getInstance(){
        if (instance == null) {
//            synchronized (ConnectionPojo.class) {
//                if (instance == null) {
            return instance = new SceneCopyPojo();
//                }
//            }
        }
        return instance;
    }
}
