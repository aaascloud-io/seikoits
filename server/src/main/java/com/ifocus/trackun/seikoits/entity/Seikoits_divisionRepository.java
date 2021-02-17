package com.ifocus.trackun.seikoits.entity;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

//@Repository
public interface Seikoits_divisionRepository extends CrudRepository<Seikoits_divisionEntity, Integer> {

	/*
	 * 部門一覧情報取得
	 *
	 *
	 */
	@Query(value = "SELECT c.* FROM seikoits_division c WHERE c.companyid = :companyid ORDER BY divisionname",nativeQuery = true)
	public List<Seikoits_divisionEntity> findCompanyDivisionList(@Param("companyid") Integer companyid);

}
