package com.ifocus.trackun.seikoits.entity.model;

import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Seikoits_groupModel {

	private Integer groupId;
	private Integer companyid;
	private Integer divisionid;
	private String groupname;
	private String summary;
	private String manager;
	private String managermail;
	private String managertel;
	private Integer i_uid;
	private Timestamp i_time;
	private Integer u_uid;
	private Timestamp u_time;

}