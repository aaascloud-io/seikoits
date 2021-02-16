package com.ifocus.trackun.seikoits.entity;

import org.springframework.data.repository.CrudRepository;

//@Repository
public interface Seikoits_userRepository extends CrudRepository<Seikoits_userEntity, Integer> {
	
	Seikoits_userEntity findByLoginId(String loginId);

	Seikoits_userEntity findByToken(String token);

}
