package me.hekr.sthome.common;

import android.content.Context;
import android.content.Intent;

import com.lib.funsdk.support.models.FunDevType;
import com.lib.funsdk.support.models.FunDevice;

import java.util.HashMap;
import java.util.Map;

import me.hekr.sthome.xmipc.ActivityGuideDeviceCamera;


public class DeviceActivitys {

	private static Map<FunDevType, Class<?>> sDeviceActivityMap = new HashMap<FunDevType, Class<?>>();
	static {
		// 监控设备
		sDeviceActivityMap.put(FunDevType.EE_DEV_NORMAL_MONITOR,
				ActivityGuideDeviceCamera.class);

	}
	
	public static void startDeviceActivity(Context context, FunDevice funDevice,String Monitorname) {
		Class<?> a = sDeviceActivityMap.get(funDevice.devType);
		if ( null != a ) {
			Intent intent = new Intent();
			intent.setClass(context, a);
			intent.putExtra("FUN_DEVICE_ID", funDevice.getId());
			intent.putExtra("FUN_DEVICE_NAME",Monitorname);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		}
	}

	public static void startDeviceActivity(Context context, String devSn,String Monitorname) {
		Class<?> a = sDeviceActivityMap.get(FunDevType.EE_DEV_NORMAL_MONITOR);
			Intent intent = new Intent();
			intent.setClass(context, a);
			intent.putExtra("FUN_DEVICE_ID", devSn);
			intent.putExtra("FUN_DEVICE_NAME",Monitorname);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);

	}
	
}
