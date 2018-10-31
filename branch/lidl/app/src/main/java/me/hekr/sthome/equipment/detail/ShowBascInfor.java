package me.hekr.sthome.equipment.detail;

import me.hekr.sthome.R;

/**
 * Created by ST-020111 on 2017/4/7.
 */

public class ShowBascInfor {
    private static int[] imageQ = new int[]{
            R.drawable.q0,
            R.drawable.q1,
            R.drawable.q2,
            R.drawable.q3,
            R.drawable.q4,
    };
    private static int[] imageS = new int[]{
            R.drawable.s0,
            R.drawable.s1,
            R.drawable.s2,
            R.drawable.s3
    };

    /**
     * show quantity
     * @param q
     * @return
     */
    public static int choseQPic(int q){
        int qResouce;
        if(q<=15){
            qResouce = imageQ[0];
        }else if(q>15 && q <=40){
            qResouce = imageQ[1];
        }else if(q > 40 && q <= 60){
            qResouce = imageQ[2];
        }else if(q > 60 && q <= 80){
            qResouce = imageQ[3];
        }else{
            qResouce = imageQ[4];
        }
        return qResouce;
    }

    /**
     * show signal
     * @param s
     * @return
     */
    public static int choseSPic(String s){
        int sResouce;
        if("00".equals(s)){
            sResouce = imageS[0];
        }else if("01".equals(s)){
            sResouce = imageS[1];
        }else if("02".equals(s) || "03".equals(s)){
            sResouce = imageS[2];
        }else if("04".equals(s)){
            sResouce = imageS[3];
        }else {
            sResouce = imageS[0];
        }
        return sResouce;
    }

    public static String choseLNum(int n){
        String num;
        if(n>100){
            num = "100%";
        }else{
            num = String.valueOf(n)+"%";
        }
        return num;
    }
}
