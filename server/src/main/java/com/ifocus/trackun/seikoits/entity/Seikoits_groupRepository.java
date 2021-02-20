package com.ifocus.trackun.seikoits.entity;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

//@Repository
public interface Seikoits_groupRepository extends CrudRepository<Seikoits_groupEntity, Integer> {

	/*
	 * グループ一覧情報取得
	 *
	 *
	 */
	@Query(value = "SELECT c.* FROM seikoits_group c WHERE c.divisionid = :divisionid ORDER BY groupname",nativeQuery = true)
	public List<Seikoits_groupEntity> findDivisionGroupList(@Param("divisionid") Integer divisionid);

	/*
	 * グループ検索
	 *
	 *
	 */
	@Query(value = "SELECT c.* FROM seikoits_group c WHERE c.divisionid = :divisionid AND c.groupname LIKE :groupname ORDER BY groupname",nativeQuery = true)
	public List<Seikoits_groupEntity> searchDivisionGroupListByName(@Param("divisionid") Integer divisionid, @Param("groupname") String groupname);

	List<Seikoits_groupEntity> findByDivisionid(Integer divisionid);
	
}
