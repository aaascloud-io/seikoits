package com.ifocus.trackun.seikoits.model;

import lombok.Getter;
import lombok.Setter;

public class PfToken {
	
	@Getter
	@Setter
	private String access_token;
	
	@Getter
	@Setter
	private String token_type;
	
	@Getter
	@Setter
	private Integer expires_in;
	
	@Getter
	@Setter
	private String refresh_token;
	
	@Getter
	@Setter
	private Integer refresh_expires_in;

}
