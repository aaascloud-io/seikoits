package com.ifocus.trackun.seikoits.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ifocus.trackun.seikoits.entity.Seikoits_userEntity;
import com.ifocus.trackun.seikoits.entity.Seikoits_userRepository;
import com.ifocus.trackun.seikoits.model.Device;
import com.ifocus.trackun.seikoits.service.IotPFService;

@CrossOrigin
@RestController
@RequestMapping("/device")
public class DeviceController {
	
	@Autowired
	private Seikoits_userRepository userRepository;
	
	@Autowired
	private IotPFService iotPfService;
	
	@PostMapping("/add")
	public Map<String, Object> add(@RequestHeader("Authorization") String bearerToken, @RequestParam("list") List<Device> devices) {
		Map<String, Object> returnMap = new HashMap<>();
		
		Seikoits_userEntity user = userRepository.findByToken(IotPFService.getRawToken(bearerToken));
		if (user != null) {
			returnMap.putAll(iotPfService.deviceBatchAdd(user, devices).toMap());
		}else {
			returnMap.put("errorMessage", "No user is found.");
		}
		
		return returnMap;
	}
	
	@PostMapping("/update")
	public Map<String, Object> update(@RequestHeader("Authorization") String bearerToken, @RequestBody Device device) {
		Map<String, Object> returnMap = new HashMap<>();
		
		Seikoits_userEntity user = userRepository.findByToken(IotPFService.getRawToken(bearerToken));
		if (user != null) {
			returnMap.put("updated", iotPfService.deviceModify(user, device));
		}else {
			returnMap.put("errorMessage", "No user is found.");
		}
		
		return returnMap;
	}
	
	@PostMapping("/delete")
	public Map<String, Object> delete(@RequestHeader("Authorization") String bearerToken, @RequestParam("deleteTargets") List<String> deleteTargets) {
		Map<String, Object> returnMap = new HashMap<>();
		
		Seikoits_userEntity user = userRepository.findByToken(IotPFService.getRawToken(bearerToken));
		if (user != null) {
			returnMap.putAll(iotPfService.deviceBatchDelete(user, deleteTargets).toMap());
		}else {
			returnMap.put("errorMessage", "No user is found.");
		}
		
		return returnMap;
	}
	
	@PostMapping("/list")
	public Map<String, Object> list(@RequestHeader("Authorization") String bearerToken, @RequestBody Device device, 
			@RequestParam("pageSize") int pageSize, @RequestParam("pageNumber") int pageNumber) {
		Map<String, Object> returnMap = new HashMap<>();
		
		Seikoits_userEntity user = userRepository.findByToken(IotPFService.getRawToken(bearerToken));
		if (user != null) {
			returnMap.putAll(iotPfService.deviceList(user, device, pageSize, pageNumber).toMap());
		}else {
			returnMap.put("errorMessage", "No user is found.");
		}
		
		return returnMap;
	}
	
	@PostMapping("/statusList")
	public Map<String, Object> statusList(@RequestHeader("Authorization") String bearerToken, @RequestBody Device device, 
			@RequestParam("pageSize") int pageSize, @RequestParam("pageNumber") int pageNumber) {
		Map<String, Object> returnMap = new HashMap<>();
		
		Seikoits_userEntity user = userRepository.findByToken(IotPFService.getRawToken(bearerToken));
		if (user != null) {
			returnMap.putAll(iotPfService.deviceStatusList(user, device, pageSize, pageNumber).toMap());
		}else {
			returnMap.put("errorMessage", "No user is found.");
		}
		
		return returnMap;
	}

}
