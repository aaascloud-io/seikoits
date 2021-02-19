package com.ifocus.trackun.seikoits.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ifocus.trackun.seikoits.entity.Seikoits_companyEntity;
import com.ifocus.trackun.seikoits.entity.Seikoits_divisionEntity;
import com.ifocus.trackun.seikoits.entity.Seikoits_groupEntity;
import com.ifocus.trackun.seikoits.entity.Seikoits_groupRepository;
import com.ifocus.trackun.seikoits.entity.Seikoits_userEntity;
import com.ifocus.trackun.seikoits.entity.Seikoits_userEntity.RoleVal;
import com.ifocus.trackun.seikoits.model.Seikoits_groupModel;

@Component
public class GroupService {

	@Autowired
	private Seikoits_groupRepository seikoits_groupRepository ;

	public boolean isValid(Seikoits_userEntity user, Seikoits_groupEntity groupEntity) {
		boolean isValid = false;

		// group所属の部門の管理者であれば、合法
		if (user.getRole() == null) {
			isValid = true;
		}else {
			switch (user.getRole().intValue()) {
			case RoleVal.COMPANY_ADMIN:
				isValid = true;
				break;
			case RoleVal.DEPARTMENT_ADMIN:
				if (user.getDivisionid() == groupEntity.getDivisionid()) {
					isValid = true;
				}
				break;
			default:
				break;
			}
		}

		return isValid;
	}

	public Map<? extends String, ? extends String> composeExFieldsForDevice(Seikoits_groupEntity groupEntity) {
		Map<String, String> exFieldsMap = new HashMap<>();
		exFieldsMap.put("cid", groupEntity.getCompanyid().toString());
		exFieldsMap.put("did", groupEntity.getDivisionid().toString());
		exFieldsMap.put("gid", groupEntity.getGroupid().toString());
		return exFieldsMap;
	}

	public Map<String, String> composeExFieldsForDevice(Seikoits_userEntity userEntity) {
		Map<String, String> exFieldsMap = new HashMap<>();

		// ユーザーのROLEで検索のフィールドが決まる
		if (userEntity.getRole() != null) {

			switch (userEntity.getRole().intValue()) {
			case RoleVal.COMPANY_ADMIN:
				if (userEntity.getCompanyid() != null) {
					exFieldsMap.put("cid", userEntity.getCompanyid().toString());
				}
				break;
			case RoleVal.DEPARTMENT_ADMIN:
				if (userEntity.getDivisionid() != null) {
					exFieldsMap.put("cid", userEntity.getCompanyid().toString());
					exFieldsMap.put("did", userEntity.getDivisionid().toString());
				}
				break;
			case RoleVal.NORMAL:
				if (userEntity.getGroupid() != null) {
					exFieldsMap.put("cid", userEntity.getCompanyid().toString());
					exFieldsMap.put("did", userEntity.getDivisionid().toString());
					exFieldsMap.put("gid", userEntity.getGroupid().toString());
				}
				break;
			default:
				break;
			}

		}

		return exFieldsMap;
	}

	public boolean isValid(Seikoits_userEntity user, Seikoits_companyEntity companyEntity) {
		boolean isValid = false;

		// システム管理者であれば、合法
		if (user.getRole() == null) {
			isValid = true;
		}else if (user.getRole() == RoleVal.COMPANY_ADMIN && user.getCompanyid() == companyEntity.getCompanyid()) {
			isValid = true;
		}

		return isValid;
	}

	public Map<String, String> composeExFieldsForDevice(Seikoits_companyEntity companyEntity) {
		Map<String, String> exFieldsMap = new HashMap<>();
		exFieldsMap.put("cid", companyEntity.getCompanyid().toString());
		return exFieldsMap;
	}

	public boolean isValid(Seikoits_userEntity user, Seikoits_divisionEntity divisionEntity) {
		boolean isValid = false;

		// 部門所属会社の管理者であれば、合法
		if (user.getRole() == null) {
			isValid = true;
		}else if (user.getRole() == RoleVal.COMPANY_ADMIN && user.getCompanyid() == divisionEntity.getCompanyid()) {
			isValid = true;
		}

		return isValid;
	}

	public Map<String, String> composeExFieldsForDevice(Seikoits_divisionEntity divisionEntity) {
		Map<String, String> exFieldsMap = new HashMap<>();
		exFieldsMap.put("cid", divisionEntity.getCompanyid().toString());
		exFieldsMap.put("did", divisionEntity.getDivisionid().toString());
		return exFieldsMap;
	}

	/*
	 * グループ一覧情報取得
	 * @param user Seikoits_userEntity ログインユーザー情報
	 * @param divisionid Integer 部門ID
	 * @return List<Seikoits_groupModel> グループ一覧情報
	 */
	public List<Seikoits_groupModel> getUnderGroupInfos(Seikoits_userEntity user, Integer divisionid) throws Exception {

		List<Seikoits_groupEntity> entityList = seikoits_groupRepository.findDivisionGroupList(divisionid);
		return getModelsByEntitys(entityList);

	}

	/*
	 * グループ検索
	 * @param user Seikoits_userEntity ログインユーザー情報
	 * @param divisionid Integer 部門ID
	 * @param model Seikoits_groupModel 検索条件
	 * @return List<Seikoits_groupModel> グループ一覧情報
	 */
	public List<Seikoits_groupModel> searchGroupInfos(Seikoits_userEntity user, Integer divisionid, Seikoits_groupModel model) throws Exception {

		List<Seikoits_groupEntity> entityList = seikoits_groupRepository.searchDivisionGroupListByName(divisionid, model.getGroupnameForSearch());
		return getModelsByEntitys(entityList);

	}

	/*
	 * グループ詳細情報取得
	 * @param groupid Integer グループID
	 * @return Seikoits_groupModel グループ詳細情報
	 *
	 */
	public Seikoits_groupModel getGroupInfo(Integer groupid) throws Exception {
		Optional<Seikoits_groupEntity> entity = seikoits_groupRepository.findById(groupid);
		return getModelByEntity(entity.get());

	}

	/*
	 * グループ登録
	 * @param user Seikoits_userEntity ログインユーザー情報
	 * @param model Seikoits_groupModel グループ情報
	 * @return Seikoits_groupModel 登録後のグループ情報
	 *
	 */
	public Seikoits_groupModel registerGroup(Seikoits_userEntity user, Seikoits_groupModel model) throws Exception {
		Seikoits_groupEntity entity = getEntitByModel(user, model);
		Seikoits_groupEntity insertedEntity = seikoits_groupRepository.save(entity);
		return getModelByEntity(insertedEntity);

	}

	/*
	 * グループ更新
	 * @param user Seikoits_userEntity ログインユーザー情報
	 * @param model Seikoits_groupEntity グループ情報
	 * @return Seikoits_groupModel 登録後のグループ情報
	 *
	 */
	public Seikoits_groupModel updateGroup(Seikoits_userEntity user, Seikoits_groupModel model) throws Exception {
		Seikoits_groupEntity entity = getEntitByModelForUpdate(user,model);
		Seikoits_groupEntity updatedEntity = seikoits_groupRepository.save(entity);
		return getModelByEntity(updatedEntity);
	}

	/*
	 * グループ削除
	 * @param model Seikoits_groupEntity グループ情報
	 *
	 */
	public void deleteGroup(Seikoits_groupModel model) throws Exception {
		seikoits_groupRepository.deleteById(model.getGroupid());
	}

	/*
	 * EntityリストからModeリストl取得
	 * @param entityList List<Seikoits_groupEntity> Entityリスト
	 * @return List<Seikoits_groupModel> Modeリスト
	 *
	 */
	private List<Seikoits_groupModel> getModelsByEntitys(List<Seikoits_groupEntity> entityList) throws Exception {
		List<Seikoits_groupModel> modelList = new ArrayList();
		for (Seikoits_groupEntity entity:entityList) {
			modelList.add(getModelByEntity(entity));
		}

		return modelList;

	}

	/*
	 * モデル取得
	 * @param entity Seikoits_groupEntity エンティティ
	 * @return Seikoits_groupModel モデル
	 *
	 */
	private Seikoits_groupModel getModelByEntity(Seikoits_groupEntity entity) throws Exception {
		Seikoits_groupModel model = new Seikoits_groupModel();
		model.setGroupid(entity.getGroupid());
		model.setCompanyid(entity.getCompanyid());
		model.setDivisionid(entity.getDivisionid());
		model.setGroupname(entity.getGroupname());
		model.setSummary(entity.getSummary());
		model.setManager(entity.getManager());
		model.setManagermail(entity.getManagermail());
		model.setManagertel(entity.getManagertel());
		return model;

	}

	/*
	 * ModelからEntity取得(登録用)
	 * @param user Seikoits_userEntity ログインユーザー情報
	 * @param model Seikoits_groupModel
	 * @return Seikoits_groupEntity
	 *
	 */
	private Seikoits_groupEntity getEntitByModel(Seikoits_userEntity user, Seikoits_groupModel model) throws Exception {

		Seikoits_groupEntity entity = new Seikoits_groupEntity();

		/* システム日時 */
		Timestamp systemTime = new Timestamp(System.currentTimeMillis());

		// 情報設定
		entity.setGroupid(model.getGroupid());
		entity.setCompanyid(model.getCompanyid());
		entity.setDivisionid(model.getDivisionid());
		entity.setGroupname(model.getGroupname());
		entity.setSummary(model.getSummary());
		entity.setManager(model.getManager());
		entity.setManagermail(model.getManagermail());
		entity.setManagertel(model.getManagertel());

		entity.setI_uid(user.getUserid());
		entity.setI_time(systemTime);
		entity.setU_uid(user.getUserid());
		entity.setU_time(systemTime);

		return entity;

	}

	/*
	 * ModelからEntity取得(更新用)
	 * @param user Seikoits_userEntity ログインユーザー情報
	 * @param model Seikoits_groupModel
	 * @return Seikoits_groupEntity
	 *
	 */
	private Seikoits_groupEntity getEntitByModelForUpdate(Seikoits_userEntity user, Seikoits_groupModel model) throws Exception {

		Optional<Seikoits_groupEntity> group = seikoits_groupRepository.findById(model.getGroupid());
		Seikoits_groupEntity entity = group.get();

		/* システム日時 */
		Timestamp systemTime = new Timestamp(System.currentTimeMillis());

		// 情報設定
		entity.setCompanyid(model.getCompanyid());
		entity.setDivisionid(model.getDivisionid());
		entity.setGroupname(model.getGroupname());
		entity.setSummary(model.getSummary());
		entity.setManager(model.getManager());
		entity.setManagermail(model.getManagermail());
		entity.setManagertel(model.getManagertel());

		entity.setU_uid(user.getUserid());
		entity.setU_time(systemTime);

		return entity;

	}
}
