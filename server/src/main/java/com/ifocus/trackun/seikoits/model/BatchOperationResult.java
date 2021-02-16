package com.ifocus.trackun.seikoits.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

public class BatchOperationResult {

	@Getter
	@Setter
	private int successCount;

	@Getter
	@Setter
	private int failCount;

	@Getter
	@Setter
	private List<String> successImeis = new ArrayList<String>();

	@Getter
	@Setter
	private List<String> failImeis = new ArrayList<String>();
	
	public Map<String, Object> toMap(){
		Map<String, Object> returnMap = new HashMap<String, Object>();
		returnMap.put("successCount", successCount);
		returnMap.put("failCount", failCount);
		returnMap.put("successImeis", successImeis);
		returnMap.put("failImeis", failImeis);
		return returnMap;
	}
}