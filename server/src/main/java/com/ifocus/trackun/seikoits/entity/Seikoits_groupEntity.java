package com.ifocus.trackun.seikoits.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@javax.persistence.Table(name="seikoits_group")
public class Seikoits_groupEntity {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer groupid;
	private Integer companyid;
	private Integer divisionid;
	private String groupname;
	private String summary;
	private String manager;
	private String managermail;
	private String managertel;
	@Column(updatable = false)
	private Integer i_uid;
	@Column(updatable = false)
	private Timestamp i_time;
	private Integer u_uid;
	private Timestamp u_time;

}