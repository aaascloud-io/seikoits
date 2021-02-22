package com.ifocus.trackun.seikoits.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix="iotpf.user")
public class IotPfUserConfig {

	@Getter
	@Setter
	private String username;
	
	@Getter
	@Setter
	private String password;
	
}
