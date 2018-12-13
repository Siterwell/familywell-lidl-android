package com.lib.funsdk.support.config;

import com.lib.EDEV_JSON_ID;
import com.lib.funsdk.support.FunLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import me.hekr.sthome.tools.LOG;

public class DevCmdOPSCalendarInMonth extends DevCmdGeneral {
	public static final String CONFIG_NAME = "OPSCalendarInMonth";
	public static final int JSON_ID = EDEV_JSON_ID.CALENDAR_MONTH_REQ;
	
	@Override
	public int getJsonID() {
		return JSON_ID;
	}
	
	@Override
	public String getConfigName() {
		return CONFIG_NAME;
	}
	
	private String event = "*";
	private String fileType = "h264";// h264、jpg
	private int month;
	private String rev = "";
	private int year;
	private int ret;
	private ArrayList<Integer> mList = new ArrayList<Integer>();

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public String getRev() {
		return rev;
	}

	public void setRev(String rev) {
		this.rev = rev;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
		this.month = 0;
	}

	public int getRet() {
		return ret;
	}

	public void setRet(int ret) {
		this.ret = ret;
	}

	public ArrayList<Integer> getData() {
		return mList;
	}


	public void setRecordMap(ArrayList<Integer> dataList) {
		this.mList = dataList;
	}

	public String getSendMsg() {
		JSONObject jsonObj = new JSONObject();
		try {
			jsonObj.put("Name", "OPSCalendar");
			jsonObj.put("SessionID", "0x00000001");
			JSONObject c_jsonObj = new JSONObject();
			jsonObj.put("OPSCalendar", c_jsonObj);
			c_jsonObj.put("Event", getEvent());
			c_jsonObj.put("FileType", getFileType());
			c_jsonObj.put("Month", getMonth());
			c_jsonObj.put("Rev", getRev());
			c_jsonObj.put("Year", getYear());
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}
		FunLog.d(CONFIG_NAME, "json:" + jsonObj.toString());
		return jsonObj.toString();
	}

	public boolean onParse(String json) {
		try {
			JSONObject obj = new JSONObject(json);
			int c_obj = new JSONObject(json).getJSONObject("OPSCalendar")
					.getInt("Mask");
				mList.clear();

				onParseMask(c_obj);

				
				// 排序 
				Collections.sort(mList);
			setRet(obj.getInt("Ret"));
			LOG.I("ceshi","有录像的天数:"+mList.toString());
			return ret == 100;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	private void onParseMask(int mask) {
		for (int i = 0; i < 31; ++i) {
			if (0 != (mask & (1 << i))) {
				mList.add(i+1);
			}
		}
	}
}
