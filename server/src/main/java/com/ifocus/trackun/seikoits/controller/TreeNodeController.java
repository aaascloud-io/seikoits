package com.ifocus.trackun.seikoits.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ifocus.trackun.seikoits.constant.LevelConstant;
import com.ifocus.trackun.seikoits.constant.RoleConstant;
import com.ifocus.trackun.seikoits.entity.Seikoits_companyEntity;
import com.ifocus.trackun.seikoits.entity.Seikoits_companyRepository;
import com.ifocus.trackun.seikoits.entity.Seikoits_divisionEntity;
import com.ifocus.trackun.seikoits.entity.Seikoits_divisionRepository;
import com.ifocus.trackun.seikoits.entity.Seikoits_groupEntity;
import com.ifocus.trackun.seikoits.entity.Seikoits_groupRepository;
import com.ifocus.trackun.seikoits.entity.Seikoits_userEntity;
import com.ifocus.trackun.seikoits.entity.Seikoits_userRepository;
import com.ifocus.trackun.seikoits.service.IotPFService;

@CrossOrigin
@RestController
@RequestMapping("/organization")
public class TreeNodeController {
	
	@Autowired
	private Seikoits_userRepository userRepository;
	
	@Autowired
	private Seikoits_companyRepository companyRepository;;
	
	@Autowired
	private Seikoits_divisionRepository seikoits_divisionRepository;;
	
	@Autowired
	private Seikoits_groupRepository seikoits_groupRepository;
	
	@RequestMapping(value = "/current", method = RequestMethod.GET)
	public Map<String, Object> list(@RequestHeader("Authorization") String bearerToken) {
		Map<String, Object> returnMap = new HashMap<>();

		Seikoits_userEntity user = userRepository.findByToken(IotPFService.getRawToken(bearerToken));
		if (user != null) {
			
			Map<String, Object> treeMap = new HashMap<>();
			
			if (user.getRole() == RoleConstant.ROLE_ADMIN) {
				
				Integer companyid = user.getCompanyid();
				Seikoits_companyEntity userCompanyEntity = companyRepository.findByCompanyid(companyid);
				if (userCompanyEntity.getLevel() == LevelConstant.LEVEL_TOP) {
					// ifocus user TODO bug
					List<Map<String, Object>> companyList = new ArrayList<>();
					
					List<Seikoits_companyEntity> list = companyRepository.findByLevel(LevelConstant.LEVEL_ONE); // level1 companys are children, too
					list.addAll(companyRepository.findByLevel(LevelConstant.LEVEL_TWO));
					
					for (Seikoits_companyEntity seikoits_companyEntity : list) {
						Map<String, Object> companyMap = new HashMap<>();
						companyMap.put("data", seikoits_companyEntity);
						companyMap.put("divisions", divisionTrees(seikoits_companyEntity.getCompanyid()));

						companyList.add(companyMap);
					}
					Map<String, Object> companyMap = new HashMap<>();
					companyMap.put("data", userCompanyEntity);
					companyMap.put("companys", companyList);
					companyMap.put("divisions", divisionTrees(userCompanyEntity.getCompanyid()));
					
					treeMap.put("level1Company", companyMap);
				}else if (userCompanyEntity.getLevel() == LevelConstant.LEVEL_ONE) {
					// its user TODO bug
					// find all companys TODO bug, no method to find companies, just user all level 2 company
					List<Seikoits_companyEntity> list = companyRepository.findByLevel(LevelConstant.LEVEL_TWO);
					List<Map<String, Object>> companyList = new ArrayList<>();
					for (Seikoits_companyEntity seikoits_companyEntity : list) {
						Map<String, Object> companyMap = new HashMap<>();
						companyMap.put("data", seikoits_companyEntity);
						companyMap.put("divisions", divisionTrees(seikoits_companyEntity.getCompanyid()));

						companyList.add(companyMap);
					}
					Map<String, Object> companyMap = new HashMap<>();
					companyMap.put("data", userCompanyEntity);
					companyMap.put("companys", companyList);
					companyMap.put("divisions", divisionTrees(userCompanyEntity.getCompanyid()));
					
					treeMap.put("level1Company", companyMap);
				}else if (userCompanyEntity.getLevel() == LevelConstant.LEVEL_TWO) {
					// normal company admin
					Optional<Seikoits_companyEntity> optionalC = companyRepository.findById(user.getCompanyid());
					if (optionalC.isPresent()) {
						Seikoits_companyEntity companyEntity = optionalC.get();
						Map<String, Object> companyMap = new HashMap<>();
						companyMap.put("data", companyEntity);
						companyMap.put("divisions", divisionTrees(user.getCompanyid()));
						treeMap.put("company", companyMap);
					}
				}
				
			}else if (user.getRole() == RoleConstant.ROLE_DIV_PERSON) {
				Optional<Seikoits_groupEntity> optional = seikoits_groupRepository.findById(user.getGroupid());
				if (optional.isPresent()) {
					treeMap.put("group", optional.get());
				}
			}else if (user.getRole() == RoleConstant.ROLE_DIV_ADMIN) {
				Optional<Seikoits_divisionEntity> optionalD = seikoits_divisionRepository.findById(user.getDivisionid());
				if (optionalD.isPresent()) {
					Seikoits_divisionEntity divisionEntity = optionalD.get();
					treeMap.put("division", divisionTree(divisionEntity));
				}
			}
			
			returnMap.put("data", treeMap);
		}else {
			returnMap.put("errorMessage", "No user is found.");
		}

		return returnMap;
	}

	private List<Map<String, Object>> divisionTrees(Integer companyid) {
		List<Map<String, Object>> divisionTrees = new ArrayList<>();
		List<Seikoits_divisionEntity> divisionEntities = seikoits_divisionRepository.findByCompanyid(companyid);
		for (Seikoits_divisionEntity seikoits_divisionEntity : divisionEntities) {
			divisionTrees.add(divisionTree(seikoits_divisionEntity));
		}
		return divisionTrees;
	}

	private Map<String, Object> divisionTree(Seikoits_divisionEntity divisionEntity) {
		// find groups of division
		List<Seikoits_groupEntity> groupEntities = seikoits_groupRepository.findByDivisionid(divisionEntity.getDivisionid());
		Map<String, Object> divisionMap = new HashMap<>();
		divisionMap.put("data", divisionEntity);
		divisionMap.put("groups", groupEntities);
		return divisionMap;
	}

}
