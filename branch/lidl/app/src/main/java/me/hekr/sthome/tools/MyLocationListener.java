package me.hekr.sthome.tools;

import android.content.Context;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;

public abstract class MyLocationListener implements BDLocationListener {
	private BDLocation loc;
	private Context context;
	
	
	public MyLocationListener(Context context) {
		this.context = context;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onReceiveLocation(BDLocation location) {
		loc=location;
		if(location.getLocType()==161){
			logMsg(location.getLatitude(),location.getLongitude());
			
		}else{
			Log.i("ceshi","失败");
		}
	}
	
	public abstract void logMsg(double lat,double lon);
	
}
