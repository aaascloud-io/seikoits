package com.ifocus.trackun.seikoits.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.ifocus.trackun.seikoits.entity.Seikoits_userEntity;
import com.ifocus.trackun.seikoits.entity.Seikoits_userRepository;
import com.ifocus.trackun.seikoits.model.PfToken;
import com.ifocus.trackun.seikoits.model.Seikoits_userModel;

@Component
public class UserService {
	
	@Autowired
	private Seikoits_userRepository userRepository;
	
	@Autowired
	private IotPFService iotPFService;
	
	public Seikoits_userModel login(Seikoits_userEntity userEntity){
		Seikoits_userModel loginedUser = null;
		PfToken pfToken = iotPFService.getPfToken();
		if (pfToken != null) {
			userEntity.setToken(pfToken.getAccess_token());
			userEntity = userRepository.save(userEntity);
			if (userEntity != null) {
				Gson gson = new Gson();
				loginedUser = gson.fromJson(gson.toJson(userEntity), Seikoits_userModel.class);
				loginedUser.setPfToken(pfToken);
			}
		}
		return loginedUser;
	}

	public Seikoits_userModel refreshToken(Seikoits_userEntity userEntity, String token) {
		Seikoits_userModel loginedUser = null;
		
		if (userEntity != null) {
			PfToken pfToken = iotPFService.refreshPfToken(userEntity, token);
			userEntity.setToken(pfToken.getAccess_token());
			userEntity = userRepository.save(userEntity);
			
			Gson gson = new Gson();
			loginedUser = gson.fromJson(gson.toJson(userEntity), Seikoits_userModel.class);
			loginedUser.setPfToken(pfToken);
		}
		
		return loginedUser;
	}
	
}
