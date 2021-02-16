package com.ifocus.trackun.seikoits.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.ifocus.trackun.seikoits.entity.Seikoits_groupEntity;
import com.ifocus.trackun.seikoits.entity.Seikoits_userEntity;

@Component
public class GroupService {
	
//	@Autowired
//	private Seikoits_groupRepository groupRepository;

	public boolean isValid(Seikoits_userEntity user, Seikoits_groupEntity groupEntity) {
		return user.getGroupid() == null; // system admin TODO 
	}

	// TODO
	public Map<? extends String, ? extends String> composeExFieldsForDevice(Seikoits_groupEntity groupEntity) {
		Map<String, String> exFieldsMap = new HashMap<>();
		exFieldsMap.put("gid", groupEntity.getGroupId().toString());
		return exFieldsMap;
	}
	
	// TODO
	public Map<String, String> composeExFieldsForDevice(Seikoits_userEntity userEntity) {
		Map<String, String> exFieldsMap = new HashMap<>();
		if (userEntity.getGroupid() != null) {
			exFieldsMap.put("gid", userEntity.getGroupid().toString());
		}
		return exFieldsMap;
	}
	
}
