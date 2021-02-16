package com.ifocus.trackun.seikoits.model;

import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Seikoits_userModel {

	private Integer userid;
	private Integer companyid;
	private Integer divisionid;
	private Integer groupid;
	private String userName;
	private String loginId;
//	private String password;
	private Integer role;
	private PfToken pfToken;
	private Integer i_uid;
	private Timestamp i_time;
	private Integer u_uid;
	private Timestamp u_time;
	
}