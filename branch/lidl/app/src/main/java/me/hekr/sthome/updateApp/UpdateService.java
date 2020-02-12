package me.hekr.sthome.updateApp;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import me.hekr.sthome.R;

/**
 * 更新service
 *
 */
public class UpdateService extends Service {
	public static final String TAG = "UpdateService";
	public static final String Install_Apk = "Install_Apk";
	/********download progress step*********/
	private static final int down_step_custom = 3;
	
	private static final int TIMEOUT = 10 * 1000;//设置反应时间
	private static String down_url;
	private static final int DOWN_OK = 1;
	private static final int DOWN_ERROR = 0;
	private static final int DOWN_ING = 2;
	
	private String app_name;
    public static  boolean flag_updating = false;
		
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		app_name = intent.getStringExtra("Key_App_Name");
		down_url = intent.getStringExtra("Key_Down_Url");
		

		FileUtil.createFile(app_name);
		
		if(FileUtil.isCreateFileSucess == true){
			createThread();
		}else{
			Toast.makeText(this, R.string.insert_card, Toast.LENGTH_SHORT).show();
			stopSelf();
			
		}
		
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		handler.removeCallbacksAndMessages(null);
	}

	private final Handler handler = new Handler() {


		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DOWN_OK:
				flag_updating =  false;
				stopSelf();
				installApk();
				ProgressEvent progressEvent2 = new ProgressEvent();
				progressEvent2.setUpdateprogress(100);
				EventBus.getDefault().post(progressEvent2);
				break;

			case DOWN_ERROR:
				stopSelf();
				flag_updating =  false;
				break;

				case DOWN_ING:
                 int progres  = (int)msg.obj;
					Log.i(TAG,"DOWN_ING"+progres);
					ProgressEvent progressEvent = new ProgressEvent();
					progressEvent.setUpdateprogress(progres);
					EventBus.getDefault().post(progressEvent);
					break;
			default:

				break;
			}
		}
	};

	/**
	 * 自动安装的方法，用户点击安装时执行
	 */
	private void installApk() {

		Uri uri = Uri.fromFile(FileUtil.updateFile);
		Intent intent = new Intent(Intent.ACTION_VIEW);

		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
		
		intent.setDataAndType(uri,"application/vnd.android.package-archive");			        
        UpdateService.this.startActivity(intent);
	}
	

	public void createThread() {
		flag_updating =  true;
		new DownLoadThread().start();
	}

	
	private class DownLoadThread extends Thread{
		@Override
		public void run() {

			Message message = new Message();
			try {								
				long downloadSize = downloadUpdateFile(down_url,FileUtil.updateFile.toString());
				if (downloadSize > 0) {					
					// down success										
					message.what = DOWN_OK;
					handler.sendMessage(message);																		
				}
			} catch (Exception e) {
				e.printStackTrace();
				message.what = DOWN_ERROR;
				handler.sendMessage(message);
			}						
		}		
	}




	public long downloadUpdateFile(String down_url, String file)throws Exception {
		
		int down_step = down_step_custom;
		int totalSize;
		int downloadCount = 0;
		int updateCount = 0;
		
		InputStream inputStream;
		OutputStream outputStream;

		URL url = new URL(down_url);
		HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
		httpURLConnection.setConnectTimeout(TIMEOUT);
		httpURLConnection.setReadTimeout(TIMEOUT);
		totalSize = httpURLConnection.getContentLength();
		
		if (httpURLConnection.getResponseCode() == 404) {
			throw new Exception("fail!");

		}
		
		inputStream = httpURLConnection.getInputStream();
		outputStream = new FileOutputStream(file, false);
		
		byte buffer[] = new byte[1024];
		int readsize = 0;
		
		while ((readsize = inputStream.read(buffer)) != -1) {
			

						
			outputStream.write(buffer, 0, readsize);
			downloadCount += readsize;
			if (updateCount == 0 || (downloadCount * 100 / totalSize - down_step) >= updateCount) {
				updateCount += down_step;
				Log.i(TAG,"updateCount"+updateCount);
				Message message = new Message();
				message.what = DOWN_ING;
				message.obj = updateCount;
				handler.sendMessage(message);
			}
		}
		if (httpURLConnection != null) {
			httpURLConnection.disconnect();
		}
		inputStream.close();
		outputStream.close();
		
		return downloadCount;
	}

}