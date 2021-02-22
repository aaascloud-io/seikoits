package com.ifocus.trackun.seikoits.entity;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;


//@Repository
public interface Seikoits_companyRepository extends CrudRepository<Seikoits_companyEntity, Integer> {

	/*
	 * 会社一覧情報取得
	 *
	 *
	 */
	@Query(value = "SELECT c.* FROM seikoits_company c WHERE c.level >= :level ORDER BY level, corporatenumber",nativeQuery = true)
	public List<Seikoits_companyEntity> findCompanyListByLevel(@Param("level") Integer level);

	public Seikoits_companyEntity findByCompanyname(String companyname);

	public Seikoits_companyEntity findByCompanyid(Integer companyid);

}
