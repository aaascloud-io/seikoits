package com.ifocus.trackun.seikoits.service;

import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ifocus.trackun.seikoits.constant.CorporateNumberConstant;
import com.ifocus.trackun.seikoits.constant.LevelConstant;
import com.ifocus.trackun.seikoits.constant.RoleConstant;
import com.ifocus.trackun.seikoits.entity.Seikoits_companyEntity;
import com.ifocus.trackun.seikoits.entity.Seikoits_companyRepository;
import com.ifocus.trackun.seikoits.entity.Seikoits_userEntity;
import com.ifocus.trackun.seikoits.entity.Seikoits_userRepository;

@Component
public class SystemService {

	@Autowired
	private Seikoits_companyRepository seikoits_companyRepository ;

	@Autowired
	private Seikoits_userRepository userRepository;

	public Seikoits_companyEntity getSystemTopCompany() {
		Seikoits_companyEntity topCompanyEntity = seikoits_companyRepository.findByCorporatenumber(CorporateNumberConstant.COM_I_FOCUS);
		if (topCompanyEntity == null) {
			topCompanyEntity = new Seikoits_companyEntity();
			topCompanyEntity.setLevel(LevelConstant.LEVEL_TOP);
			topCompanyEntity.setCompanyname("株式会社アイフォーカス");
			topCompanyEntity.setCorporatenumber(CorporateNumberConstant.COM_I_FOCUS);
			seikoits_companyRepository.save(topCompanyEntity);
		}
		return topCompanyEntity;
	}

	public Seikoits_userEntity getSystemTopUser() {
		Seikoits_userEntity systemTopUser = null;
		// top companyの管理者がいない場合、追加
		Seikoits_companyEntity systemTopCompany = getSystemTopCompany();
		List<Seikoits_userEntity> userlist = userRepository.findUserListByCompanyid(systemTopCompany.getCompanyid());
		for (Seikoits_userEntity userEntity : userlist) {
			if (userEntity.getRole() == RoleConstant.ROLE_ADMIN) {
				systemTopUser = userEntity;
				break;
			}
		}
		if (systemTopUser == null) {
			String username = "ifocus";
			String password = "zaq12wsx";
			Seikoits_userEntity topUser = new Seikoits_userEntity();
			topUser.setLoginid(username);
			topUser.setUsername(username);
			topUser.setPassword(getEncodedPassword(password));
			topUser.setCompanyid(systemTopCompany.getCompanyid());
			topUser.setRole(RoleConstant.ROLE_ADMIN);
			systemTopUser = userRepository.save(topUser);
		}
		return systemTopUser;
	}

	public String getEncodedPassword(String rawPwd){
		return DigestUtils.md5Hex(rawPwd);
	}

	public boolean passwordEquals(String rawPwd, String encodedPwd){
		return encodedPwd.equals(DigestUtils.md5Hex(rawPwd));
	}

}
