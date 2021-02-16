package com.ifocus.trackun.seikoits.model;

import lombok.Getter;
import lombok.Setter;

public class BatteryInfo {

	@Getter
	@Setter
	private String imei;

	@Getter
	@Setter
	private int batteryPercentage;

	@Getter
	@Setter
	private int charging;

	@Getter
	@Setter
	private Long timestamp;

}
