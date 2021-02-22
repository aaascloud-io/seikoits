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
import com.ifocus.trackun.seikoits.model.Seikoits_userModel;
import com.ifocus.trackun.seikoits.service.IotPFService;
import com.ifocus.trackun.seikoits.service.UserService;

@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	private Seikoits_userRepository userRepository;

	@Autowired
	private UserService userService;

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public Map<String, Object> list(@RequestHeader("Authorization") String bearerToken,
			@RequestParam("pageSize") int pageSize, @RequestParam("pageNumber") int pageNumber) {
		Map<String, Object> returnMap = new HashMap<>();

		try {
			Seikoits_userEntity user = userRepository.findByToken(IotPFService.getRawToken(bearerToken));
			if (user != null) {
				// 会社管理者、または、部門管理者なら、
				if (user.getRole() <= RoleConstant.ROLE_DIV_ADMIN) {
					returnMap.put("list", userService.getUnderUserInfos(user));
				} else {
					returnMap.put("errorMessage", "No access authority.");
				}
			}else {
				returnMap.put("errorMessage", "No user is found.");
			}
		} catch (Exception e) {
			returnMap.put("errorMessage", "getUnderUserInfos() ERROR: " + e.getMessage());
		}

		return returnMap;
	}

	@RequestMapping(value = "/detailInfo", method = RequestMethod.GET)
	public Map<String, Object> detailInfo(@RequestHeader("Authorization") String bearerToken,
			@RequestParam("userId") int userId) {
		Map<String, Object> returnMap = new HashMap<>();

		try {
			Seikoits_userEntity user = userRepository.findByToken(IotPFService.getRawToken(bearerToken));
			if (user != null) {
				// 会社管理者、または、部門管理者なら、
				if (user.getRole() <= RoleConstant.ROLE_DIV_ADMIN) {
					returnMap.put("detailInfo", userService.getUserInfo(userId));
				} else {
					returnMap.put("errorMessage", "No access authority.");
				}
			}else {
				returnMap.put("errorMessage", "No user is found.");
			}
		} catch (Exception e) {
			returnMap.put("errorMessage", "getUserInfo() ERROR: " + e.getMessage());
		}

		return returnMap;
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public Map<String, Object> add(@RequestHeader("Authorization") String bearerToken, @RequestBody Seikoits_userModel userModel) {
		Map<String, Object> returnMap = new HashMap<>();

		try {
			Seikoits_userEntity user = userRepository.findByToken(IotPFService.getRawToken(bearerToken));
			if (user != null) {
				// 会社管理者、または、部門管理者なら、
				if (user.getRole() <= RoleConstant.ROLE_DIV_ADMIN) {
					String returnMessage = userService.checkUserInfo(user, userModel);
					if (returnMessage == "OK") {
						returnMap.put("registeredUser",userService.registerUser(user, userModel));
					} else {
						returnMap.put("errorMessage", returnMessage);
					}
				} else {
					returnMap.put("errorMessage", "No access authority.");
				}
			} else {
				returnMap.put("errorMessage", "No user is found.");
			}
		} catch (Exception e) {
			returnMap.put("errorMessage", "registerUser() ERROR: " + e.getMessage());
		}
		return returnMap;
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public Map<String, Object> update(@RequestHeader("Authorization") String bearerToken, @RequestBody Seikoits_userModel userModel) {
		Map<String, Object> returnMap = new HashMap<>();

		try {
			Seikoits_userEntity user = userRepository.findByToken(IotPFService.getRawToken(bearerToken));
			if (user != null) {
				// 会社管理者、または、部門管理者なら、
				if (user.getRole() <= RoleConstant.ROLE_DIV_ADMIN) {
					String returnMessage = userService.checkUserInfo(user, userModel);
					if (returnMessage == "OK") {
						returnMap.put("updatedUser",userService.updateUser(user, userModel));
					} else {
						returnMap.put("errorMessage", returnMessage);
					}
				} else {
					returnMap.put("errorMessage", "No access authority.");
				}
			} else {
				returnMap.put("errorMessage", "No user is found.");
			}
		} catch (Exception e) {
			returnMap.put("errorMessage", "updateUser() ERROR: " + e.getMessage());
		}

		return returnMap;
	}

	@RequestMapping(value = "/delete", method = RequestMethod.DELETE)
	public Map<String, Object> delete(@RequestHeader("Authorization") String bearerToken, @RequestBody Seikoits_userModel userModel) {
		Map<String, Object> returnMap = new HashMap<>();

		try {
			Seikoits_userEntity user = userRepository.findByToken(IotPFService.getRawToken(bearerToken));
			if (user != null) {
				// 会社管理者、または、部門管理者なら、
				if (user.getRole() <= RoleConstant.ROLE_DIV_ADMIN) {
					userService.deleteUser(userModel);
					returnMap.put("deletedUser",userModel);
				} else {
					returnMap.put("errorMessage", "No access authority.");
				}
			} else {
				returnMap.put("errorMessage", "No user is found.");
			}
		} catch (Exception e) {
			returnMap.put("errorMessage", "deleteUser() ERROR: " + e.getMessage());
		}

		return returnMap;
	}

}
