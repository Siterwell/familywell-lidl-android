package me.hekr.sthome.commonBaseView;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import me.hekr.sthome.R;

/**
 * Created by xjj on 2016/12/5.
 */
public class ToastTools {

    /**
     *
     * @param text
     *            ：文本
     * @param context
     *            :上下文

     */
    public static void ShowSuccussToast(String text, Context context) {


                LayoutInflater inflater = LayoutInflater.from(context);
                View v = inflater.inflate(R.layout.common_toast, null);// 得到加载view
                ImageView image = (ImageView) v
                        .findViewById(R.id.imageview);
                image.setImageResource(R.drawable.success);
                TextView title = (TextView) v.findViewById(R.id.textview);
                title.setText(text);
                Toast toast = new Toast(context.getApplicationContext());
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(v);
                toast.show();


    }

    /**
     *
     * @param text
     *            ;文本
     * @param context
     *            :上下文

     */
    public static void ShowErrorToast(String text, Context context) {


        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.common_toast, null);// 得到加载view
        ImageView image = (ImageView) v
                .findViewById(R.id.imageview);
        image.setImageResource(R.drawable.error);
        TextView title = (TextView) v.findViewById(R.id.textview);
        title.setText(text);
        Toast toast = new Toast(context.getApplicationContext());
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(v);
        toast.show();


    }

}
