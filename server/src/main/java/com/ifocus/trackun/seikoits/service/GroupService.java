package com.ifocus.trackun.seikoits.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.ifocus.trackun.seikoits.entity.Seikoits_companyEntity;
import com.ifocus.trackun.seikoits.entity.Seikoits_divisionEntity;
import com.ifocus.trackun.seikoits.entity.Seikoits_groupEntity;
import com.ifocus.trackun.seikoits.entity.Seikoits_userEntity;
import com.ifocus.trackun.seikoits.entity.Seikoits_userEntity.RoleVal;

@Component
public class GroupService {
	
	public boolean isValid(Seikoits_userEntity user, Seikoits_groupEntity groupEntity) {
		boolean isValid = false;
		
		// group所属の部門の管理者であれば、合法
		if (user.getRole() == null) {
			isValid = true;
		}else {
			switch (user.getRole().intValue()) {
			case RoleVal.COMPANY_ADMIN:
				isValid = true;
				break;
			case RoleVal.DEPARTMENT_ADMIN:
				if (user.getDivisionid() == groupEntity.getDivisionid()) {
					isValid = true;
				}
				break;
			default:
				break;
			}
		}
		
		return isValid;
	}

	public Map<? extends String, ? extends String> composeExFieldsForDevice(Seikoits_groupEntity groupEntity) {
		Map<String, String> exFieldsMap = new HashMap<>();
		exFieldsMap.put("cid", groupEntity.getCompanyid().toString());
		exFieldsMap.put("did", groupEntity.getDivisionid().toString());
		exFieldsMap.put("gid", groupEntity.getGroupId().toString());
		return exFieldsMap;
	}
	
	public Map<String, String> composeExFieldsForDevice(Seikoits_userEntity userEntity) {
		Map<String, String> exFieldsMap = new HashMap<>();
		
		// ユーザーのROLEで検索のフィールドが決まる
		if (userEntity.getRole() != null) {
			
			switch (userEntity.getRole().intValue()) {
			case RoleVal.COMPANY_ADMIN:
				if (userEntity.getCompanyid() != null) {
					exFieldsMap.put("cid", userEntity.getCompanyid().toString());
				}
				break;
			case RoleVal.DEPARTMENT_ADMIN:
				if (userEntity.getDivisionid() != null) {
					exFieldsMap.put("did", userEntity.getDivisionid().toString());
				}
				break;
			case RoleVal.NORMAL:
				if (userEntity.getGroupid() != null) {
					exFieldsMap.put("gid", userEntity.getGroupid().toString());
				}
				break;
			default:
				break;
			}
			
		}
		
		return exFieldsMap;
	}

	public boolean isValid(Seikoits_userEntity user, Seikoits_companyEntity companyEntity) {
		boolean isValid = false;
		
		// システム管理者であれば、合法
		if (user.getRole() == null) {
			isValid = true;
		}else if (user.getRole() == RoleVal.COMPANY_ADMIN && user.getCompanyid() == companyEntity.getCompanyid()) {
			isValid = true;
		}
		
		return isValid;
	}

	public Map<String, String> composeExFieldsForDevice(Seikoits_companyEntity companyEntity) {
		Map<String, String> exFieldsMap = new HashMap<>();
		exFieldsMap.put("cid", companyEntity.getCompanyid().toString());
		return exFieldsMap;
	}

	public boolean isValid(Seikoits_userEntity user, Seikoits_divisionEntity divisionEntity) {
		boolean isValid = false;
		
		// 部門所属会社の管理者であれば、合法
		if (user.getRole() == null) {
			isValid = true;
		}else if (user.getRole() == RoleVal.COMPANY_ADMIN && user.getCompanyid() == divisionEntity.getCompanyid()) {
			isValid = true;
		}
		
		return isValid;
	}

	public Map<String, String> composeExFieldsForDevice(Seikoits_divisionEntity divisionEntity) {
		Map<String, String> exFieldsMap = new HashMap<>();
		exFieldsMap.put("cid", divisionEntity.getCompanyid().toString());
		exFieldsMap.put("did", divisionEntity.getDivisionid().toString());
		return exFieldsMap;
	}
	
}
