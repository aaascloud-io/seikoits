package com.ifocus.trackun.seikoits.model;

import lombok.Getter;
import lombok.Setter;

public class DeviceLocation {

	/**
	 * 位置確定方式
	 */
	@Getter
	@Setter
	private int type;

	public static final class LocationType{
		public final static int UNKNOWN = -1;
		public final static int GPS = 0;
		public final static int BS_LOCATION = 1;
		public final static int WLAN_LOCATION = 2;
	}

	/**
	 * 位置確定トリガー
	 */
	@Getter
	@Setter
	private int trigger;

	public static final class Trigger{
		public final static int START_TO_MOVE = 1;
		public final static int STOP_TO_MOTION = 2;
		public final static int SMS = 3;
		public final static int SOS = 4;
		public final static int BINDING = 5;
		public final static int MOVING_PERIOD = 6;
		public final static int MOTIONLESS_PERIOD = 7;
		public final static int SHELL = 8;
		public final static int POWER_ON = 9;
	}

	/**
	 * デバイス識別
	 */
	@Getter
	@Setter
	private String imei;

	/**
	 * データタイムスタンプ
	 */
	@Getter
	@Setter
	private long timestamp;

	/**
	 * データ日付
	 */
	@Getter
	@Setter
	private String date;

	/**
	 * データ時間
	 */
	@Getter
	@Setter
	private String time;

	/**
	 * 経度
	 */
	@Getter
	@Setter
	private double lat;

	/**
	 * 緯度
	 */
	@Getter
	@Setter
	private double lng;

	/**
	 * 高度
	 */
	@Getter
	@Setter
	private double ati;

	/**
	 * 精度
	 */
	@Getter
	@Setter
	private double accuracy;

	/**
	 * バッテリレベル
	 */
	@Getter
	@Setter
	private int batterypercent;

	/**
	 * 信号強度
	 */
	@Getter
	@Setter
	private int netSignal;

	public static final class NetSignalType{
		public final static int UNKNOWN = -1;
	}

}
