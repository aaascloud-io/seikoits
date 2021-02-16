package com.ifocus.trackun.seikoits.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

public class PaginationList<T> {

	@Getter
	@Setter
	private long total;

	@Getter
	@Setter
	private int pageNumber;

	@Getter
	@Setter
	private int pageSize;

	@Getter
	@Setter
	private List<T> currentPage = new ArrayList<T>();

	public PaginationList(){
		super();
	}

	public PaginationList(int pageSize, int pageNumber){
		super();
		this.pageSize = pageSize;
		this.pageNumber = pageNumber;
	}

	public Map<String, Object> toMap(){
		Map<String, Object> returnMap = new HashMap<String, Object>();
		returnMap.put("total", total);
		returnMap.put("pageSize", pageSize);
		returnMap.put("pageNumber", pageNumber);
		returnMap.put("list", currentPage);
		return returnMap;

	}

}
