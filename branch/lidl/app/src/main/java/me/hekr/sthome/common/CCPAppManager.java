
package me.hekr.sthome.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;

import me.hekr.sthome.MyApplication;
import me.hekr.sthome.model.modelbean.ClientUser;
import me.hekr.sthome.tools.LOG;

public class CCPAppManager {
    public static Md5FileNameGenerator md5FileNameGenerator = new Md5FileNameGenerator();
    private static ClientUser mClientUser;


    /**
     * 缓存账号注册信息
     * @param user
     */
    public static void setClientUser(ClientUser user){
        mClientUser = user;

        if(user==null){
            SharedPreferences users = MyApplication.getAppContext().getSharedPreferences("user_info",0);
            SharedPreferences.Editor mydata = users.edit();
            mydata.putString("userinfo" ,"");
            mydata.commit();
        }else{
            SharedPreferences users = MyApplication.getAppContext().getSharedPreferences("user_info",0);
            SharedPreferences.Editor mydata = users.edit();
            mydata.putString("userinfo" ,user.toString());
            mydata.commit();

//            LOG.D("CCPAppManager", "[RYAN] setClientUser > " + user.toString());
        }
    }

    /**
     * 保存注册账号信息
     * @return
     */
    public static ClientUser getClientUser() {
    
        if(mClientUser != null) {
            return mClientUser;
        }
        String registAccount = getAutoRegistAccount(MyApplication.getAppContext());
        if(!TextUtils.isEmpty(registAccount)) {
            mClientUser = new ClientUser();

            return mClientUser.from(registAccount);
        }
        return null;
    }

    public static String getUserId() {
        return getClientUser().getId();
    }

    private static String getAutoRegistAccount(Context context) {
        SharedPreferences wode = context.getSharedPreferences("user_info",0);
        String user = wode.getString("userinfo","");
        return user;

    }



}
