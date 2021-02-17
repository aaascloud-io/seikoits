package com.ifocus.trackun.seikoits.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix="system.top-user")
public class SystemUserConfig {

	@Getter
	@Setter
	private String username;
	
	@Getter
	@Setter
	private String password;
	
}
