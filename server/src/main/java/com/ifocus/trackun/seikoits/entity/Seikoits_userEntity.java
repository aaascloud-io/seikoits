package com.ifocus.trackun.seikoits.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@javax.persistence.Table(name="seikoits_user")
public class Seikoits_userEntity {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer userid;
	private Integer companyid;
	private Integer divisionid;
	private Integer groupid;
	private String username;
	private String loginid;
	private String password;
	private Integer role;

	public class RoleVal{
		public final static int COMPANY_ADMIN = 1;
		public final static int DEPARTMENT_ADMIN = 2;
		public final static int NORMAL = 3;
	}

	@Lob
	private String token;

	@Column(updatable = false)
	private Integer i_uid;
	@Column(updatable = false)
	private Timestamp i_time;
	private Integer u_uid;
	private Timestamp u_time;

}