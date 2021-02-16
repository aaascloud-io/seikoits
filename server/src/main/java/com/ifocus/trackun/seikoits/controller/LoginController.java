package com.ifocus.trackun.seikoits.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.tomcat.util.security.MD5Encoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ifocus.trackun.seikoits.entity.Seikoits_userEntity;
import com.ifocus.trackun.seikoits.entity.Seikoits_userRepository;
import com.ifocus.trackun.seikoits.model.Seikoits_userModel;
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
	public Map<String, Object> login(@RequestParam("loginid") String loginid, @RequestParam("password") String password) {
		Map<String, Object> returnMap = new HashMap<>();
		
		Seikoits_userEntity user = userRepository.findByLoginId(loginid);
		if (user != null) {
			String encodedPwd = MD5Encoder.encode(password.getBytes());
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
	public Map<String, Object> refresh(@RequestParam("token") String token) {
		Map<String, Object> returnMap = new HashMap<>();
		
		Seikoits_userEntity userEntity = userRepository.findByToken(token);
		Seikoits_userModel usermodal = userService.refreshToken(userEntity, token);
		
		if (usermodal != null) {
			returnMap.put("user", usermodal);
		}else {
			returnMap.put("errorMessage", "No user is found.");
		}
		
		return returnMap;
	}

}
