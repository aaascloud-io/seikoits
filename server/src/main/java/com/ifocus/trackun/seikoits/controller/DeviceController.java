package com.ifocus.trackun.seikoits.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.ifocus.trackun.seikoits.entity.Seikoits_companyEntity;
import com.ifocus.trackun.seikoits.entity.Seikoits_companyRepository;
import com.ifocus.trackun.seikoits.entity.Seikoits_divisionEntity;
import com.ifocus.trackun.seikoits.entity.Seikoits_divisionRepository;
import com.ifocus.trackun.seikoits.entity.Seikoits_groupEntity;
import com.ifocus.trackun.seikoits.entity.Seikoits_groupRepository;
import com.ifocus.trackun.seikoits.entity.Seikoits_userEntity;
import com.ifocus.trackun.seikoits.entity.Seikoits_userRepository;
import com.ifocus.trackun.seikoits.model.Device;
import com.ifocus.trackun.seikoits.service.GroupService;
import com.ifocus.trackun.seikoits.service.IotPFService;

@CrossOrigin
@RestController
@RequestMapping("/device")
public class DeviceController {
	
	@Autowired
	private Seikoits_userRepository userRepository;
	
	@Autowired
	private IotPFService iotPfService;
	
	@Autowired
	private GroupService groupService;
	
	@Autowired
	private Seikoits_groupRepository groupRepository;
	
	@Autowired
	private Seikoits_divisionRepository divisionRepository;
	
	@Autowired
	private Seikoits_companyRepository companyRepository;
	
	@PostMapping("/add")
	public Map<String, Object> add(@RequestHeader("Authorization") String bearerToken, @RequestBody Map<String, Object> paramMap) {
		Map<String, Object> returnMap = new HashMap<>();
		
		if (bearerToken != null && !bearerToken.isEmpty()) {
			
			Seikoits_userEntity user = userRepository.findByToken(IotPFService.getRawToken(bearerToken));
			if (user != null) {
				
				List<Device> devices = new ArrayList<>();
				if (paramMap.get("list") != null && paramMap.get("list") instanceof List) {

					Gson gson = new Gson();
					for (Object obj : (List<?>) paramMap.get("list")) {
						Device device = gson.fromJson(gson.toJson(obj), Device.class);
						device.setExFields(groupService.composeExFieldsForDevice(user));
						devices.add(device);
					}
					
					returnMap.putAll(iotPfService.deviceBatchAdd(user, devices).toMap());
					
				}else {
					returnMap.put("errorMessage", "No add list is defined.");
				}
			}else {
				returnMap.put("errorMessage", "Authorization token is unvalid.");
			}
			
		}else {
			returnMap.put("errorMessage", "No authorization token.");
		}

		return returnMap;
	}
	
	// TODO statuscode
	@PostMapping("/update")
	public Map<String, Object> update(@RequestHeader("Authorization") String bearerToken, @RequestBody Map<String, Object> paramMap) {
		Map<String, Object> returnMap = new HashMap<>();
		Device updateTarget = new Device();
		
		boolean paramterValid = true;
		if (paramMap.get("imei") == null) {
			paramterValid = false;
			returnMap.put("errorMessage", "Imei is null");
		}else {
			updateTarget.setImei(paramMap.remove("imei").toString());
		}
		if (bearerToken == null || bearerToken.isEmpty()) {
			paramterValid = false;
			returnMap.put("errorMessage", "No authorization token.");
		}
		if (paramMap.get("devicename") != null) {
			updateTarget.setDevicename(paramMap.remove("devicename").toString());
		}
		if (paramMap != null && !paramMap.isEmpty()) {
			paramterValid = false;
			returnMap.put("errorMessage", "Unsupported properties: " + paramMap.toString());
		}
		
		if (paramterValid) {
			
			Seikoits_userEntity user = userRepository.findByToken(IotPFService.getRawToken(bearerToken));
			if (user != null) {
				
				if (iotPfService.isUserValidForDevice(user, updateTarget.getImei())) {
					
					if (paramMap.get("companyid") != null) { // company変更
						
						Seikoits_companyEntity companyEntity = null;
						Optional<Seikoits_companyEntity> optional = companyRepository.findById(new Integer(paramMap.get("companyid").toString()));
						if (optional.isPresent()) {
							companyEntity = optional.get();
						}
						if (groupService.isValid(user, companyEntity)) {
							updateTarget.getExFields().putAll(groupService.composeExFieldsForDevice(companyEntity));
							returnMap.put("result", iotPfService.deviceModify(user, updateTarget));
						}else {
							returnMap.put("errorMessage", "User is unvalid for this company.");
						}
						
					}else if (paramMap.get("divisionid") != null) {  // division変更
						
						Seikoits_divisionEntity divisionEntity = null;
						Optional<Seikoits_divisionEntity> optional = divisionRepository.findById(new Integer(paramMap.get("divisionid").toString()));
						if (optional.isPresent()) {
							divisionEntity = optional.get();
						}
						if (groupService.isValid(user, divisionEntity)) {
							updateTarget.getExFields().putAll(groupService.composeExFieldsForDevice(divisionEntity));
							returnMap.put("result", iotPfService.deviceModify(user, updateTarget));
						}else {
							returnMap.put("errorMessage", "User is unvalid for this division.");
						}
						
					}else if (paramMap.get("groupid") != null) {  // group変更
						
						Seikoits_groupEntity groupEntity = null;
						Optional<Seikoits_groupEntity> optional = groupRepository.findById(new Integer(paramMap.get("groupid").toString()));
						if (optional.isPresent()) {
							groupEntity = optional.get();
						}
						if (groupService.isValid(user, groupEntity)) {
							updateTarget.getExFields().putAll(groupService.composeExFieldsForDevice(groupEntity));
							returnMap.put("result", iotPfService.deviceModify(user, updateTarget));
						}else {
							returnMap.put("errorMessage", "User is unvalid for this group.");
						}
						
					}else {
						returnMap.put("result", iotPfService.deviceModify(user, updateTarget));
					}
					
				}else {
					returnMap.put("errorMessage", "Unvalid user for this device.");
				}
				
				
			}else {
				returnMap.put("errorMessage", "Authorization token is unvalid.");
			}
			
		}
		
		return returnMap;
	}
	
	@PostMapping("/delete")
	public Map<String, Object> delete(@RequestHeader("Authorization") String bearerToken, @RequestBody Map<String, Object> paramMap) {
		Map<String, Object> returnMap = new HashMap<>();
		
		List<String> deleteTargets = new ArrayList<>();
		if (paramMap.get("deleteTargets") != null && paramMap.get("deleteTargets") instanceof List) {
			
			for (Object obj : (List<?>) paramMap.get("deleteTargets")) {
				deleteTargets.add(obj.toString());
			}
			
			if (bearerToken != null && !bearerToken.isEmpty()) {
				
				Seikoits_userEntity user = userRepository.findByToken(IotPFService.getRawToken(bearerToken));
				if (user != null) {
					returnMap.putAll(iotPfService.deviceBatchDelete(user, deleteTargets).toMap());
				}else {
					returnMap.put("errorMessage", "Authorization token is unvalid.");
				}
				
			}else {
				returnMap.put("errorMessage", "No authorization token.");
			}
			
		}else {
			returnMap.put("errorMessage", "No delete list is defined.");
		}
		
		return returnMap;
	}
	
	@GetMapping("/list")
	public Map<String, Object> list(@RequestHeader("Authorization") String bearerToken, 
			@RequestParam(value = "devicename", required = false) String devicename, 
			@RequestParam(value = "imei", required = false) String imei, 
			@RequestParam(value = "iccid", required = false) String iccid, 
			@RequestParam(value = "imsi", required = false) String imsi, 
			@RequestParam("pageSize") int pageSize, @RequestParam("pageNumber") int pageNumber) {
		Device device = new Device();
		device.setDevicename(devicename);
		device.setImei(imei);
		device.setIccid(iccid);
		device.setImsi(imsi);
		
		Map<String, Object> returnMap = new HashMap<>();
		
		if (bearerToken != null && !bearerToken.isEmpty()) {
			
			Seikoits_userEntity user = userRepository.findByToken(IotPFService.getRawToken(bearerToken));
			if (user != null) {
				returnMap.putAll(iotPfService.deviceList(user, device, pageSize, pageNumber).toMap());
			}else {
				returnMap.put("errorMessage", "Authorization token is unvalid.");
			}
			
		}else {
			returnMap.put("errorMessage", "No authorization token.");
		}
		
		return returnMap;
	}
	
	@GetMapping("/statusList")
	public Map<String, Object> statusList(@RequestHeader("Authorization") String bearerToken, 
			@RequestParam(value = "devicename", required = false) String devicename, 
			@RequestParam(value = "imei", required = false) String imei, 
			@RequestParam(value = "iccid", required = false) String iccid, 
			@RequestParam(value = "imsi", required = false) String imsi, 
			@RequestParam("pageSize") int pageSize, @RequestParam("pageNumber") int pageNumber) {
		Device device = new Device();
		device.setDevicename(devicename);
		device.setImei(imei);
		device.setIccid(iccid);
		device.setImsi(imsi);
		
		Map<String, Object> returnMap = new HashMap<>();
		
		if (bearerToken != null && !bearerToken.isEmpty()) {
			
			Seikoits_userEntity user = userRepository.findByToken(IotPFService.getRawToken(bearerToken));
			if (user != null) {
				returnMap.putAll(iotPfService.deviceStatusList(user, device, pageSize, pageNumber).toMap());
			}else {
				returnMap.put("errorMessage", "Authorization token is unvalid.");
			}
			
		}else {
			returnMap.put("errorMessage", "No authorization token.");
		}
		
		return returnMap;
	}

}
