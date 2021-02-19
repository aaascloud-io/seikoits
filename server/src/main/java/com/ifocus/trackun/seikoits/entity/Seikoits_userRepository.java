package com.ifocus.trackun.seikoits.entity;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

//@Repository
public interface Seikoits_userRepository extends CrudRepository<Seikoits_userEntity, Integer> {

	Seikoits_userEntity findByLoginid(String loginid);

	Seikoits_userEntity findByToken(String token);

	/*
	 * 自部門担当者一覧情報取得
	 *
	 *
	 */
	@Query(value = "SELECT u.* "
			+ "FROM seikoits_user u "
			+ "WHERE u.divisionid = :divisionid "
			+ "ORDER BY u.groupid,u.username", nativeQuery = true)
	public List<Seikoits_userEntity> findUserListByDivisionid(@Param("divisionid") Integer divisionid);

	/*
	 * 自社ユーザ一覧情報取得（2級代理店用）
	 *
	 *
	 */
	@Query(value = "SELECT u.* "
			+ "FROM seikoits_user u "
			+ "WHERE u.companyid = :companyid "
			+ "ORDER BY u.groupid,u.username", nativeQuery = true)
	public List<Seikoits_userEntity> findUserListByCompanyid(@Param("companyid") Integer companyid);

	/*
	 * 配下ユーザ一覧情報取得（0、1級代理店用）
	 *
	 *
	 */
	@Query(value = "SELECT u.* "
			+ "FROM seikoits_user u "
			+ "INNER JOIN seikoits_company c ON u.companyid = c.companyid "
			+ "WHERE c.level > :level "
			+ "ORDER BY u.groupid,u.username", nativeQuery = true)
	public List<Seikoits_userEntity> findUserListByLevel(@Param("level") Integer level);

}
