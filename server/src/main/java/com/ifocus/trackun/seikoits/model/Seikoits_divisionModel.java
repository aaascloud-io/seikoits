package com.ifocus.trackun.seikoits.model;

import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Seikoits_divisionModel {

	private Integer divisionid;
	private Integer companyid;
	private String divisionname;
	private String summary;
	private String manager;
	private String managermail;
	private String managertel;
	private Integer i_uid;
	private Timestamp i_time;
	private Integer u_uid;
	private Timestamp u_time;

}