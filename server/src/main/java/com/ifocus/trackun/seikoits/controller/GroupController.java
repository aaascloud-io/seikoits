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
import com.ifocus.trackun.seikoits.model.Seikoits_groupModel;
import com.ifocus.trackun.seikoits.service.GroupService;
import com.ifocus.trackun.seikoits.service.IotPFService;

@CrossOrigin
@RestController
@RequestMapping("/group")
public class GroupController {

	@Autowired
	private Seikoits_userRepository userRepository;

	@Autowired
	private GroupService groupService;

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public Map<String, Object> list(@RequestHeader("Authorization") String bearerToken,
			@RequestParam("divisionid") int divisionid, @RequestParam("pageSize") int pageSize, @RequestParam("pageNumber") int pageNumber) {
		Map<String, Object> returnMap = new HashMap<>();

		try {
			Seikoits_userEntity user = userRepository.findByToken(IotPFService.getRawToken(bearerToken));
			if (user != null) {
				// 会社管理者、または、部門管理者なら、
				if (user.getRole() <= RoleConstant.ROLE_DIV_ADMIN) {
					returnMap.put("list", groupService.getUnderGroupInfos(user,divisionid));
				} else {
					returnMap.put("errorMessage", "No access authority.");
				}
			}else {
				returnMap.put("errorMessage", "No user is found.");
			}
		} catch (Exception e) {
			returnMap.put("errorMessage", "getUnderGroupInfos() ERROR: " + e.getMessage());
		}

		return returnMap;
	}

	@RequestMapping(value = "/detailInfo", method = RequestMethod.GET)
	public Map<String, Object> detailInfo(@RequestHeader("Authorization") String bearerToken,
			@RequestParam("groupId") int groupId) {
		Map<String, Object> returnMap = new HashMap<>();

		try {
			Seikoits_userEntity user = userRepository.findByToken(IotPFService.getRawToken(bearerToken));
			if (user != null) {
				// 会社管理者、または、部門管理者なら、
				if (user.getRole() <= RoleConstant.ROLE_DIV_ADMIN) {
					returnMap.put("detailInfo", groupService.getGroupInfo(groupId));
				} else {
					returnMap.put("errorMessage", "No access authority.");
				}
			}else {
				returnMap.put("errorMessage", "No user is found.");
			}
		} catch (Exception e) {
			returnMap.put("errorMessage", "getGroupInfo() ERROR: " + e.getMessage());
		}

		return returnMap;
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public Map<String, Object> add(@RequestHeader("Authorization") String bearerToken, @RequestBody Seikoits_groupModel group) {
		Map<String, Object> returnMap = new HashMap<>();

		try {
			Seikoits_userEntity user = userRepository.findByToken(IotPFService.getRawToken(bearerToken));
			if (user != null) {
				// 会社管理者、または、部門管理者なら、
				if (user.getRole() <= RoleConstant.ROLE_DIV_ADMIN) {
					returnMap.put("registeredGroup",groupService.registerGroup(user, group));
				} else {
					returnMap.put("errorMessage", "No access authority.");
				}
			} else {
				returnMap.put("errorMessage", "No user is found.");
			}
		} catch (Exception e) {
			returnMap.put("errorMessage", "registerGroup() ERROR: " + e.getMessage());
		}
		return returnMap;
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public Map<String, Object> update(@RequestHeader("Authorization") String bearerToken, @RequestBody Seikoits_groupModel group) {
		Map<String, Object> returnMap = new HashMap<>();

		try {
			Seikoits_userEntity user = userRepository.findByToken(IotPFService.getRawToken(bearerToken));
			if (user != null) {
				// 会社管理者、または、部門管理者なら、
				if (user.getRole() <= RoleConstant.ROLE_DIV_ADMIN) {
					returnMap.put("updatedGroup",groupService.updateGroup(user, group));
				} else {
					returnMap.put("errorMessage", "No access authority.");
				}
			} else {
				returnMap.put("errorMessage", "No user is found.");
			}
		} catch (Exception e) {
			returnMap.put("errorMessage", "updateGroup() ERROR: " + e.getMessage());
		}

		return returnMap;
	}

	@RequestMapping(value = "/delete", method = RequestMethod.DELETE)
	public Map<String, Object> delete(@RequestHeader("Authorization") String bearerToken, @RequestBody Seikoits_groupModel group) {
		Map<String, Object> returnMap = new HashMap<>();

		try {
			Seikoits_userEntity user = userRepository.findByToken(IotPFService.getRawToken(bearerToken));
			if (user != null) {
				// 会社管理者、または、部門管理者なら、
				if (user.getRole() <= RoleConstant.ROLE_DIV_ADMIN) {
					groupService.deleteGroup(group);
					returnMap.put("deletedGroup",group);
				} else {
					returnMap.put("errorMessage", "No access authority.");
				}
			} else {
				returnMap.put("errorMessage", "No user is found.");
			}
		} catch (Exception e) {
			returnMap.put("errorMessage", "deleteGroup() ERROR: " + e.getMessage());
		}

		return returnMap;
	}

}
