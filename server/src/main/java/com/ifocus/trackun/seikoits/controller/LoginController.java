package com.ifocus.trackun.seikoits.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ifocus.trackun.seikoits.entity.Seikoits_userEntity;
import com.ifocus.trackun.seikoits.entity.Seikoits_userRepository;
import com.ifocus.trackun.seikoits.model.Seikoits_userModel;
import com.ifocus.trackun.seikoits.service.IotPFService;
import com.ifocus.trackun.seikoits.service.UserService;

@CrossOrigin
@RestController
@RequestMapping("/login")
public class LoginController {

	@Autowired
	private Seikoits_userRepository userRepository;

	@Autowired
	private UserService userService;

	@PostMapping
	public Map<String, Object> login(@RequestBody Map<String, String> paramMap) {
		String loginId = paramMap.get("loginId");
		String password = paramMap.get("password");

		Map<String, Object> returnMap = new HashMap<>();

		Seikoits_userEntity user = userRepository.findByLoginid(loginId);

		if (user != null) {
			String encodedPwd = DigestUtils.md5Hex(password.getBytes());
			if (user.getPassword().equals(encodedPwd)) {
				returnMap.put("user", userService.login(user));
			}else {
				returnMap.put("errorMessage", "Password unvalid.");
			}
		}else {
			returnMap.put("errorMessage", "No user is found.");
		}

		return returnMap;
	}

	@PostMapping("/refresh")
	public Map<String, Object> refresh(@RequestHeader("Authorization") String bearerToken, @RequestBody Map<String, String> tokenMap) {
		String refreshToken = tokenMap.get("refresh_token");

		Map<String, Object> returnMap = new HashMap<>();

		Seikoits_userEntity userEntity = userRepository.findByToken(IotPFService.getRawToken(bearerToken));
		Seikoits_userModel usermodal = userService.refreshToken(userEntity, refreshToken);

		if (usermodal != null) {
			returnMap.put("user", usermodal);
		}else {
			returnMap.put("errorMessage", "No user is found.");
		}

		return returnMap;
	}

}
