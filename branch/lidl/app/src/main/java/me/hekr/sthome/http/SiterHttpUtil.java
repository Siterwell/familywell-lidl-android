package me.hekr.sthome.http;

import java.io.IOException;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.protocol.HTTP;
import cz.msebera.android.httpclient.util.EntityUtils;
import me.hekr.sthome.tools.Config;

/**
 * Created by TracyHenry on 2018/7/25.
 */

public class SiterHttpUtil {


    public static String getResultForHttpGet() throws ClientProtocolException, IOException {
        //服务器  ：服务器项目  ：servlet名称
        String path= Config.ApkVerUrl;
        //name:服务器端的用户名，pwd:服务器端的密码
        //注意字符串连接时不能带空格

        String result="";

        HttpGet httpGet=new HttpGet(path);//编者按：与HttpPost区别所在，这里是将参数在地址中传递
        HttpResponse response=new DefaultHttpClient().execute(httpGet);
        if(response.getStatusLine().getStatusCode()==200){
            HttpEntity entity=response.getEntity();
            result= EntityUtils.toString(entity, HTTP.UTF_8);
        }
        return result;
    }

}
