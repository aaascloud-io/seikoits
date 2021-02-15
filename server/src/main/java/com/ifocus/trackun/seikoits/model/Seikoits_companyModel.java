package com.ifocus.trackun.seikoits.model;

import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Seikoits_companyModel {

	private Integer companyid;
	private String corporatenumber;
	private String companyname;
	private String address;
	private String industry;
	private String mail;
	private String tel;
	private String fax;
	private Integer level;
	private Integer i_uid;
	private Timestamp i_time;
	private Integer u_uid;
	private Timestamp u_time;

}