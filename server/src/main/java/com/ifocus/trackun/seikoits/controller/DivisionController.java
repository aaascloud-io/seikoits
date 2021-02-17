package com.ifocus.trackun.seikoits.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ifocus.trackun.seikoits.constant.RoleConstant;
import com.ifocus.trackun.seikoits.entity.Seikoits_userEntity;
import com.ifocus.trackun.seikoits.entity.Seikoits_userRepository;
import com.ifocus.trackun.seikoits.model.Seikoits_divisionModel;
import com.ifocus.trackun.seikoits.service.DivisionService;
import com.ifocus.trackun.seikoits.service.IotPFService;

@CrossOrigin
@RestController
@RequestMapping("/division")
public class DivisionController {

	@Autowired
	private Seikoits_userRepository userRepository;

	@Autowired
	private DivisionService divisionService;

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public Map<String, Object> list(@RequestHeader("Authorization") String bearerToken,
			@RequestParam("companyid") int companyid, @RequestParam("pageSize") int pageSize, @RequestParam("pageNumber") int pageNumber) {
		Map<String, Object> returnMap = new HashMap<>();

		try {
			Seikoits_userEntity user = userRepository.findByToken(IotPFService.getRawToken(bearerToken));
			if (user != null) {
				// 会社管理者なら、
				if (user.getRole() == RoleConstant.ROLE_ADMIN) {
					returnMap.put("list", divisionService.getUnderDivisionInfos(user,companyid));
				} else {
					returnMap.put("errorMessage", "No access authority.");
				}
			}else {
				returnMap.put("errorMessage", "No user is found.");
			}
		} catch (Exception e) {
			returnMap.put("errorMessage", "getUnderDivisionInfos() ERROR: " + e.getMessage());
		}

		return returnMap;
	}

	@RequestMapping(value = "/detailInfo", method = RequestMethod.GET)
	public Map<String, Object> detailInfo(@RequestHeader("Authorization") String bearerToken,
			@RequestParam("divisionId") int divisionId) {
		Map<String, Object> returnMap = new HashMap<>();

		try {
			Seikoits_userEntity user = userRepository.findByToken(IotPFService.getRawToken(bearerToken));
			if (user != null) {
				// 会社管理者なら、
				if (user.getRole() == RoleConstant.ROLE_ADMIN) {
					returnMap.put("detailInfo", divisionService.getDivisionInfo(divisionId));
				} else {
					returnMap.put("errorMessage", "No access authority.");
				}
			}else {
				returnMap.put("errorMessage", "No user is found.");
			}
		} catch (Exception e) {
			returnMap.put("errorMessage", "getDivisionInfo() ERROR: " + e.getMessage());
		}

		return returnMap;
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public Map<String, Object> add(@RequestHeader("Authorization") String bearerToken, @RequestBody Seikoits_divisionModel division) {
		Map<String, Object> returnMap = new HashMap<>();

		try {
			Seikoits_userEntity user = userRepository.findByToken(IotPFService.getRawToken(bearerToken));
			if (user != null) {
				// 会社管理者なら、
				if (user.getRole() == RoleConstant.ROLE_ADMIN) {
					returnMap.put("registeredDivision",divisionService.registerDivision(user, division));
				} else {
					returnMap.put("errorMessage", "No access authority.");
				}
			} else {
				returnMap.put("errorMessage", "No user is found.");
			}
		} catch (Exception e) {
			returnMap.put("errorMessage", "registerDivision() ERROR: " + e.getMessage());
		}
		return returnMap;
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public Map<String, Object> update(@RequestHeader("Authorization") String bearerToken, @RequestBody Seikoits_divisionModel division) {
		Map<String, Object> returnMap = new HashMap<>();

		try {
			Seikoits_userEntity user = userRepository.findByToken(IotPFService.getRawToken(bearerToken));
			if (user != null) {
				// 会社管理者なら、
				if (user.getRole() == RoleConstant.ROLE_ADMIN) {
					returnMap.put("updatedDivision",divisionService.updateDivision(user, division));
				} else {
					returnMap.put("errorMessage", "No access authority.");
				}
			} else {
				returnMap.put("errorMessage", "No user is found.");
			}
		} catch (Exception e) {
			returnMap.put("errorMessage", "updateDivision() ERROR: " + e.getMessage());
		}

		return returnMap;
	}

	@RequestMapping(value = "/delete", method = RequestMethod.DELETE)
	public Map<String, Object> delete(@RequestHeader("Authorization") String bearerToken, @RequestBody Seikoits_divisionModel division) {
		Map<String, Object> returnMap = new HashMap<>();

		try {
			Seikoits_userEntity user = userRepository.findByToken(IotPFService.getRawToken(bearerToken));
			if (user != null) {
				// 会社管理者なら、
				if (user.getRole() == RoleConstant.ROLE_ADMIN) {
					divisionService.deleteDivision(division);
					returnMap.put("deletedDivision",division);
				} else {
					returnMap.put("errorMessage", "No access authority.");
				}
			} else {
				returnMap.put("errorMessage", "No user is found.");
			}
		} catch (Exception e) {
			returnMap.put("errorMessage", "deleteDivision() ERROR: " + e.getMessage());
		}

		return returnMap;
	}

}
