package com.ifocus.trackun.seikoits.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import com.ifocus.trackun.seikoits.entity.Seikoits_divisionEntity;
import com.ifocus.trackun.seikoits.entity.Seikoits_divisionRepository;
import com.ifocus.trackun.seikoits.entity.Seikoits_userEntity;
import com.ifocus.trackun.seikoits.model.Seikoits_divisionModel;


@SpringBootApplication
@RestController
@Service
@Transactional
public class DivisionService {

	@Autowired
	private Seikoits_divisionRepository seikoits_divisionRepository ;

	/*
	 * 部門一覧情報取得
	 * @param user Seikoits_userEntity ログインユーザー情報
	 * @param companyid Integer 会社ID
	 * @return List<Seikoits_divisionModel> 部門一覧情報
	 */
	public List<Seikoits_divisionModel> getUnderDivisionInfos(Seikoits_userEntity user, Integer companyid) throws Exception {

		List<Seikoits_divisionEntity> entityList = seikoits_divisionRepository.findCompanyDivisionList(companyid);
		return getModelsByEntitys(entityList);

	}

	/*
	 * 部門詳細情報取得
	 * @param divisionid Integer 部門ID
	 * @return Seikoits_divisionModel 部門詳細情報
	 *
	 */
	public Seikoits_divisionModel getDivisionInfo(Integer divisionid) throws Exception {
		Optional<Seikoits_divisionEntity> entity = seikoits_divisionRepository.findById(divisionid);
		return getModelByEntity(entity.get());

	}

	/*
	 * 部門登録
	 * @param user Seikoits_userEntity ログインユーザー情報
	 * @param model Seikoits_divisionModel 部門情報
	 * @return Seikoits_divisionModel 登録後の部門情報
	 *
	 */
	public Seikoits_divisionModel registerDivision(Seikoits_userEntity user, Seikoits_divisionModel model) throws Exception {
		Seikoits_divisionEntity entity = getEntitByModel(user, model);
		Seikoits_divisionEntity insertedEntity = seikoits_divisionRepository.save(entity);
		return getModelByEntity(insertedEntity);

	}

	/*
	 * 部門更新
	 * @param user Seikoits_userEntity ログインユーザー情報
	 * @param model Seikoits_divisionEntity 部門情報
	 * @return Seikoits_divisionModel 登録後の部門情報
	 *
	 */
	public Seikoits_divisionModel updateDivision(Seikoits_userEntity user, Seikoits_divisionModel model) throws Exception {
		Seikoits_divisionEntity entity = getEntitByModelForUpdate(user,model);
		Seikoits_divisionEntity updatedEntity = seikoits_divisionRepository.save(entity);
		return getModelByEntity(updatedEntity);
	}

	/*
	 * 部門削除
	 * @param model Seikoits_divisionEntity 部門情報
	 *
	 */
	public void deleteDivision(Seikoits_divisionModel model) throws Exception {
		seikoits_divisionRepository.deleteById(model.getDivisionid());
	}

	/*
	 * EntityリストからModeリストl取得
	 * @param entityList List<Seikoits_divisionEntity> Entityリスト
	 * @return List<Seikoits_divisionModel> Modeリスト
	 *
	 */
	private List<Seikoits_divisionModel> getModelsByEntitys(List<Seikoits_divisionEntity> entityList) throws Exception {
		List<Seikoits_divisionModel> modelList = new ArrayList();
		for (Seikoits_divisionEntity entity:entityList) {
			modelList.add(getModelByEntity(entity));
		}

		return modelList;

	}

	/*
	 * モデル取得
	 * @param entity Seikoits_divisionEntity エンティティ
	 * @return Seikoits_divisionModel モデル
	 *
	 */
	private Seikoits_divisionModel getModelByEntity(Seikoits_divisionEntity entity) throws Exception {
		Seikoits_divisionModel model = new Seikoits_divisionModel();
		model.setDivisionid(entity.getDivisionid());
		model.setCompanyid(entity.getCompanyid());
		model.setDivisionname(entity.getDivisionname());
		model.setSummary(entity.getSummary());
		model.setManager(entity.getManager());
		model.setManagermail(entity.getManagermail());
		model.setManagertel(entity.getManagertel());
		return model;

	}

	/*
	 * ModelからEntity取得(登録用)
	 * @param user Seikoits_userEntity ログインユーザー情報
	 * @param model Seikoits_divisionModel
	 * @return Seikoits_divisionEntity
	 *
	 */
	private Seikoits_divisionEntity getEntitByModel(Seikoits_userEntity user, Seikoits_divisionModel model) throws Exception {

		Seikoits_divisionEntity entity = new Seikoits_divisionEntity();

		/* システム日時 */
		Timestamp systemTime = new Timestamp(System.currentTimeMillis());

		// 情報設定
		entity.setDivisionid(model.getDivisionid());
		entity.setCompanyid(model.getCompanyid());
		entity.setDivisionname(model.getDivisionname());
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
	 * @param model Seikoits_divisionModel
	 * @return Seikoits_divisionEntity
	 *
	 */
	private Seikoits_divisionEntity getEntitByModelForUpdate(Seikoits_userEntity user, Seikoits_divisionModel model) throws Exception {

		Optional<Seikoits_divisionEntity> division = seikoits_divisionRepository.findById(model.getDivisionid());
		Seikoits_divisionEntity entity = division.get();

		/* システム日時 */
		Timestamp systemTime = new Timestamp(System.currentTimeMillis());

		// 情報設定
		entity.setCompanyid(model.getCompanyid());
		entity.setDivisionname(model.getDivisionname());
		entity.setSummary(model.getSummary());
		entity.setManager(model.getManager());
		entity.setManagermail(model.getManagermail());
		entity.setManagertel(model.getManagertel());

		entity.setU_uid(user.getUserid());
		entity.setU_time(systemTime);

		return entity;

	}
}
