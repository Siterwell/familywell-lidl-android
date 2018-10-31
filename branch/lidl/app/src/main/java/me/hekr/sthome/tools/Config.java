package me.hekr.sthome.tools;


import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;


import com.loopj.android.http.AsyncHttpResponseHandler;

import cz.msebera.android.httpclient.Header;
import me.hekr.sthome.R;
import me.hekr.sthome.http.BaseHttpUtil;
import me.hekr.sthome.http.HekrCodeUtil;
import me.hekr.sthome.http.HekrUser;

public class Config {
	private static final String TAG = "Config";

	public final static String RootPath = "61.164.94.198:1415/app";
	public final static String ApkVerUrl = "http://" + RootPath
			+ "/SilverCrest.ver";
	public final static String ApkUrl = "http://" + RootPath
			+ "/SilverCrest.apk";

	public static final String UPDATE_APKNAME="SilverCrest.apk";


	public static int getVerCode(Context context,String packageName) {
		int verCode = -1;
		try {
			verCode = context.getPackageManager().getPackageInfo(
					packageName, 0).versionCode;
		} catch (NameNotFoundException e) {
			Log.e(TAG, e.getMessage());
		}
		return verCode;
	}
	
	public static String getVerName(Context context,String packageName) {
		String verName = "";
		try {
			verName = context.getPackageManager().getPackageInfo(
					packageName, 0).versionName;
		} catch (NameNotFoundException e) {
			Log.e(TAG, e.getMessage());
		}
		return verName;	

	}
	
	public static String getAppName(Context context) {
		String verName = context.getResources()
		.getText(R.string.app_name).toString();
		return verName;
	}

	/**
	 * 3.5 获取APP更新信息
	 *
	 * @param loginListener 回调接口
	 */

	public static void getUpdateInfo(Context mContext, final HekrUser.LoginListener loginListener) {

		BaseHttpUtil.getData(mContext, ApkVerUrl, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int i, Header[] headers, byte[] bytes) {
				loginListener.loginSuccess(new String(bytes));
			}

			@Override
			public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
				loginListener.loginFail(HekrCodeUtil.getErrorCode(i, bytes));
			}
		});
	}


	public static void getWeatherInfo(Context mContext, final HekrUser.LoginListener loginListener,String url) {

		BaseHttpUtil.getData(mContext, url, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int i, Header[] headers, byte[] bytes) {
				loginListener.loginSuccess(new String(bytes));
			}

			@Override
			public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
				loginListener.loginFail(HekrCodeUtil.getErrorCode(i, bytes));
			}
		});
	}

	public static void getPlaceInfo(Context mContext, final HekrUser.LoginListener loginListener,String url) {

		BaseHttpUtil.getData(mContext, url, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int i, Header[] headers, byte[] bytes) {
				loginListener.loginSuccess(new String(bytes));
			}

			@Override
			public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
				loginListener.loginFail(HekrCodeUtil.getErrorCode(i, bytes));
			}
		});
	}


	public static class UpdateInfo {
		public int code;
		public String name;

		public int getCode() {
			return code;
		}

		public void setCode(int code) {
			this.code = code;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	};
}

