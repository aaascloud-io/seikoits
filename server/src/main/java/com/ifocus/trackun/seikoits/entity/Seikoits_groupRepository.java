package com.ifocus.trackun.seikoits.entity;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

//@Repository
public interface Seikoits_groupRepository extends CrudRepository<Seikoits_groupEntity, Integer> {

	List<Seikoits_groupEntity> findByDivisionid(Integer divisionid);

}
