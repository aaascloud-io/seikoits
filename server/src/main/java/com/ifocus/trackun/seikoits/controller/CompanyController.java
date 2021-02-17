package com.ifocus.trackun.seikoits.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ifocus.trackun.seikoits.constant.RoleConstant;
import com.ifocus.trackun.seikoits.entity.Seikoits_userEntity;
import com.ifocus.trackun.seikoits.entity.Seikoits_userRepository;
import com.ifocus.trackun.seikoits.model.Seikoits_companyModel;
import com.ifocus.trackun.seikoits.service.CompanyService;
import com.ifocus.trackun.seikoits.service.IotPFService;

@CrossOrigin
@RestController
@RequestMapping("/company")
public class CompanyController {

	@Autowired
	private Seikoits_userRepository userRepository;

	@Autowired
	private CompanyService companyService;

	@RequestMapping(value = "/list", method = RequestMethod.POST)
	public Map<String, Object> list(@RequestHeader("Authorization") String bearerToken,
			@RequestParam("pageSize") int pageSize, @RequestParam("pageNumber") int pageNumber) {
		Map<String, Object> returnMap = new HashMap<>();

		try {
			Seikoits_userEntity user = userRepository.findByToken(IotPFService.getRawToken(bearerToken));
			if (user != null) {
				// 会社管理者なら、
				if (user.getRole() == RoleConstant.ROLE_ADMIN) {
					returnMap.put("list", companyService.getUnderCompanyInfos(user));
				} else {
					returnMap.put("errorMessage", "No access authority.");
				}
			}else {
				returnMap.put("errorMessage", "No user is found.");
			}
		} catch (Exception e) {
			returnMap.put("errorMessage", "getUnderCompanyInfos() ERROR: " + e.getMessage());
		}

		return returnMap;
	}

	@PostMapping("/detailInfo")
	public Map<String, Object> detailInfo(@RequestHeader("Authorization") String bearerToken,
			@RequestParam("companyId") int companyId) {
		Map<String, Object> returnMap = new HashMap<>();

		try {
			Seikoits_userEntity user = userRepository.findByToken(IotPFService.getRawToken(bearerToken));
			if (user != null) {
				// 会社管理者なら、
				if (user.getRole() == RoleConstant.ROLE_ADMIN) {
					returnMap.put("detailInfo", companyService.getCompanyInfo(companyId));
				} else {
					returnMap.put("errorMessage", "No access authority.");
				}
			}else {
				returnMap.put("errorMessage", "No user is found.");
			}
		} catch (Exception e) {
			returnMap.put("errorMessage", "getCompanyInfo() ERROR: " + e.getMessage());
		}

		return returnMap;
	}

	@PostMapping("/add")
	public Map<String, Object> add(@RequestHeader("Authorization") String bearerToken, @RequestBody Seikoits_companyModel company) {
		Map<String, Object> returnMap = new HashMap<>();

		try {
			Seikoits_userEntity user = userRepository.findByToken(IotPFService.getRawToken(bearerToken));
			if (user != null) {
				// 会社管理者なら、
				if (user.getRole() == RoleConstant.ROLE_ADMIN) {
					returnMap.put("registeredCompany",companyService.registerCompany(user, company));
				} else {
					returnMap.put("errorMessage", "No access authority.");
				}
			} else {
				returnMap.put("errorMessage", "No user is found.");
			}
		} catch (Exception e) {
			returnMap.put("errorMessage", "registerCompany() ERROR: " + e.getMessage());
		}
		return returnMap;
	}

	@PostMapping("/update")
	public Map<String, Object> update(@RequestHeader("Authorization") String bearerToken, @RequestBody Seikoits_companyModel company) {
		Map<String, Object> returnMap = new HashMap<>();

		try {
			Seikoits_userEntity user = userRepository.findByToken(IotPFService.getRawToken(bearerToken));
			if (user != null) {
				// 会社管理者なら、
				if (user.getRole() == RoleConstant.ROLE_ADMIN) {
					returnMap.put("updatedCompany",companyService.updateCompany(user, company));
				} else {
					returnMap.put("errorMessage", "No access authority.");
				}
			} else {
				returnMap.put("errorMessage", "No user is found.");
			}
		} catch (Exception e) {
			returnMap.put("errorMessage", "updateCompany() ERROR: " + e.getMessage());
		}

		return returnMap;
	}

	@PostMapping("/delete")
	public Map<String, Object> delete(@RequestHeader("Authorization") String bearerToken, @RequestBody Seikoits_companyModel company) {
		Map<String, Object> returnMap = new HashMap<>();

		try {
			Seikoits_userEntity user = userRepository.findByToken(IotPFService.getRawToken(bearerToken));
			if (user != null) {
				// 会社管理者なら、
				if (user.getRole() == RoleConstant.ROLE_ADMIN) {
					companyService.deleteCompany(company);
					returnMap.put("deletedCompany",company);
				} else {
					returnMap.put("errorMessage", "No access authority.");
				}
			} else {
				returnMap.put("errorMessage", "No user is found.");
			}
		} catch (Exception e) {
			returnMap.put("errorMessage", "deleteCompany() ERROR: " + e.getMessage());
		}

		return returnMap;
	}

}
