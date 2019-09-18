package com.lib.funsdk.support.models;

import me.hekr.sthome.R;

public enum FunDevType {
	// 0, 监控设备
	EE_DEV_NORMAL_MONITOR(0, 
			R.string.dev_type_monitor,
			R.mipmap.qiangji),
	// 1, 智能插座
//	EE_DEV_INTELLIGENTSOCKET(1,
//			R.string.dev_type_intelligentsocket,
//			R.mipmap.xmjp_socket),
	// 2, 情景灯泡
//	EE_DEV_SCENELAMP(2,
//			R.string.dev_type_scenelamp,
//			R.mipmap.xmjp_bulb),
//	// 3, 智能灯座
//	EE_DEV_LAMPHOLDER(3,
//			R.string.dev_type_lampholder,
//			R.drawable.xmjp_bulbsocket),
//	// 4, 汽车伴侣
//	EE_DEV_CARMATE(4,
//			R.string.dev_type_carmate,
//			R.drawable.xmjp_car),
//	// 5, 大眼睛行车记录仪
//	EE_DEV_BIGEYE(5,
//			R.string.dev_type_bigeye,
//			R.drawable.xmjp_beye),
	// 6, 小雨点
	EE_DEV_SMALLEYE(6,
			R.string.dev_type_smalleye,
			R.mipmap.ship_sel),
//	// 7, 雄迈摇头机
	EE_DEV_BOUTIQUEROTOT(7,
			R.string.dev_type_boutiquerotot,
			R.mipmap.yaotouji),
//	// 8, 运动摄像机
//	EE_DEV_SPORTCAMERA(8,
//			R.string.dev_type_sportcamera,
//			R.drawable.xmjp_mov),
//	// 9, 鱼眼小雨点
//	EE_DEV_SMALLRAINDROPS_FISHEYE(9,
//			R.string.dev_type_smallraindrops_fisheye,
//			R.drawable.xmjp_feye),
//	// 10, 鱼眼灯泡/全景智能灯泡
//	EE_DEV_LAMP_FISHEYE(10,
//			R.string.dev_type_lamp_fisheye,
//			R.drawable.xmjp_fbulb),
//	// 11, 小黄人
//	EE_DEV_MINIONS(11,
//			R.string.dev_type_minions,
//			R.drawable.xmjp_bob),
//	// 12, WiFi音乐盒
//	EE_DEV_MUSICBOX(12,
//			R.string.dev_type_musicbox,
//			R.drawable.icon_funsdk),
//	// 13, WiFi音响
//	EE_DEV_SPEAKER(13,
//			R.string.dev_type_speaker,
//			R.drawable.icon_funsdk),
//
// 21, 智能门铃
EE_DEV_IDR(21,
		   R.string.dev_type_idr,
		   R.mipmap.doorbell),
	EE_DEV_GUN_MONITOR(22,
			R.string.dev_type_monitor,
			R.mipmap.qiangji);
//	// 未知设备
//	EE_DEV_UNKNOWN(-1,
//			R.string.dev_type_unknown,
//			R.drawable.icon_funsdk);

	private int devIndex;
	private int devResId;
	private int drawResId;

	FunDevType(int id, int resid, int iconid) {
		this.devIndex = id;
		this.devResId = resid;
		this.drawResId = iconid;
	}

	/**
	 * 获取设备类型的字符串ID
	 * 
	 * @return 设备类型字符串ID
	 */
	public int getTypeStrId() {
		return this.devResId;
	}

	/**
	 * 获取设备图标的资源ID
	 * @return
	 */
	public int getDrawableResId() {
		return this.drawResId;
	}

	
	/**
	 * 获取设备类型的索引号
	 * 
	 * @return
	 */
	public int getDevIndex() {
		return this.devIndex;
	}
	
	public static FunDevType getType(int index) {
		for (FunDevType devType : FunDevType.values()) {
			if (devType.getDevIndex() == index) {
				return devType;
			}
		}
		return EE_DEV_NORMAL_MONITOR;
	}
}
