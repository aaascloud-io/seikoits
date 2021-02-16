package com.ifocus.trackun.seikoits.model;

import lombok.Getter;
import lombok.Setter;

public class DeviceStatus {

	/**
	 * ユーザー識別
	 */
	@Deprecated
	@Getter
	@Setter
	private String uid;

	/**
	 * デバイス識別
	 */
	@Getter
	@Setter
	private String imei;

	/**
	 * デバイス名
	 */
	@Deprecated
	@Getter
	@Setter
	private String deviceName;

	/**
	 * 登録状況
	 */
	@Getter
	@Setter
	private int registerStatus;

	public class RegisterStatusType{
		public final static int RESERVED = -1;
		public final static int NO_REGISTRY = 0;
		public final static int REGISTERED = 1;
		public final static int REREGISTERED = 2;
	}

	/**
	 * 登録日時文字列
	 */
	@Getter
	@Setter
	private String registerTime;

	/**
	 * デバイス状態
	 */
	@Getter
	@Setter
	private int status;

	public final static class StatusType{
		public final static int RESERVED = -1;
		public final static int OFFLINE = 0;
		public final static int ONLINE = 1;
		public final static int POWER_OFF = 2;
	}

	/**
	 * バッテリ状況
	 */
	@Getter
	@Setter
	private BatteryInfo batteryInfo;

	/**
	 * 最後の通信が来た時間文字列
	 */
	@Getter
	@Setter
	private String lastUpdateTime;

	/**
	 * デバイス最後の位置
	 */
	@Getter
	@Setter
	private DeviceLocation deviceLocation;
	
	@Getter
	@Setter
	private Device deviceBindInfo;

}
