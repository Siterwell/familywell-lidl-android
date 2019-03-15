package me.hekr.sthome.commonBaseView;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import me.hekr.sthome.R;


/**
 * Created by xjj on 2016/12/5.
 */
public class LoadingProceedDialog extends Dialog{

    private  final  String TAG = this.getClass().getName();
    private TextView mTextView;
    private CompletedView mLoadingView;
    AsyncTask mAsyncTask;
    private int count;
    private  proceedTask myTask;
    private Timer timer;
    public boolean flag_success;
    private ResultListener resultListener;

    private final OnCancelListener mCancelListener
            = new OnCancelListener() {

        @Override
        public void onCancel(DialogInterface dialog) {
            if(mAsyncTask != null) {
                mAsyncTask.cancel(true);
            }

        }
    };

    /**
     * @param context
     */
    public LoadingProceedDialog(Context context) {
        super(context , R.style.Theme_Light_CustomDialog_Blue);
        mAsyncTask = null;
        setCancelable(true);
        setContentView(R.layout.common_toast_proceeding);
        mTextView = (TextView)findViewById(R.id.textview);
        mTextView.setText(R.string.wait);
        mLoadingView = (CompletedView)findViewById(R.id.tasks_view);
        mLoadingView.setProgress(50);
        setOnCancelListener(mCancelListener);
        timer = new Timer();
        myTask = new proceedTask();
        timer.schedule(myTask,0,1000);
        flag_success = false;
    }

    /**
     * @param context
     * @param resid
     */
    public LoadingProceedDialog(Context context , int resid) {
        this(context);
        mTextView.setText(resid);
    }

    public LoadingProceedDialog(Context context , CharSequence text) {
        this(context);
        mTextView.setText(text);
    }

    public LoadingProceedDialog(Context context , AsyncTask asyncTask) {
        this(context);
        mAsyncTask = asyncTask;
    }

    public LoadingProceedDialog(Context context , CharSequence text , AsyncTask asyncTask) {
        this(context , text);
        mAsyncTask = asyncTask;
    }

    /**
     * 设置对话框显示文本
     * @param text
     */
    public final void setPressText(CharSequence text) {
        mTextView.setText(text);
    }

    class proceedTask extends TimerTask {
        @Override
        public void run() {

            handler.sendEmptyMessage(2);
            if(count>=100){
                handler.sendEmptyMessage(3);
            }
            if(flag_success){
                count= count + 10;
            }else {
                count ++;
            }

        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 2:
                    if(count>=100){
                        mLoadingView.setProgress(100);
                    }else {
                        mLoadingView.setProgress(count);
                    }
                    if(resultListener!=null && (count %10==0)){
                        resultListener.proceed();
                    }
                    break;
                case 3:
                    count = 0;
                    timer.cancel();
                    timer = null;
                    myTask.cancel();
                    myTask = null;
                    if(resultListener!=null){
                        resultListener.result(flag_success);
                    }
                    LoadingProceedDialog.this.dismiss();
                    break;
            }
        }
    };

    public void setResultListener(ResultListener resultListener) {
        this.resultListener = resultListener;
    }


    public void setFlag_success(boolean flag_success) {
        this.flag_success = flag_success;
    }

    public interface ResultListener {

        void result(boolean success);
        void proceed();
    }


}
