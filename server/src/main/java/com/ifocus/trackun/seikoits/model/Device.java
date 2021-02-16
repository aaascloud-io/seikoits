package com.ifocus.trackun.seikoits.model;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

public class Device {

	@Getter
	@Setter
	private String imei;

	@Getter
	@Setter
	private String imsi;

	@Getter
	@Setter
	private String iccid;

	@Getter
	@Setter
	private String devicename;

	@Getter
	@Setter
	private Map<String, String> exFields = new HashMap<>();
}
