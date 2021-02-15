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
@javax.persistence.Table(name="seikoits_company")
public class Seikoits_companyEntity {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer companyid;
	private String corporatenumber;
	private String companyname;
	private String address;
	private String industry;
	private String mail;
	private String tel;
	private String fax;
	private Integer level;
	@Column(updatable = false)
	private Integer i_uid;
	@Column(updatable = false)
	private Timestamp i_time;
	private Integer u_uid;
	private Timestamp u_time;

}