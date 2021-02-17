package com.ifocus.trackun.seikoits.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix="iotpf.url")
public class IotPfUrlConfig {
	
	@Getter
	@Setter
	private String tokenFetchUrl;
	
	@Getter
	@Setter
	private String tokenUpdateUrl;

	@Getter
	@Setter
	private String deviceBatchBindUrl;
	
	@Getter
	@Setter
	private String deviceBatchUnbindUrl;
	
	@Getter
	@Setter
	private String deviceBindModifyUrl;
	
	@Getter
	@Setter
	private String deviceBindListUrl;
	
	@Getter
	@Setter
	private String deviceStatusListUrl;
	
}
