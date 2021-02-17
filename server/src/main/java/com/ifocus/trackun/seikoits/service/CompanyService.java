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

import com.ifocus.trackun.seikoits.entity.Seikoits_companyEntity;
import com.ifocus.trackun.seikoits.entity.Seikoits_companyRepository;
import com.ifocus.trackun.seikoits.entity.Seikoits_userEntity;
import com.ifocus.trackun.seikoits.model.Seikoits_companyModel;


@SpringBootApplication
@RestController
@Service
@Transactional
public class CompanyService {

	@Autowired
	private Seikoits_companyRepository seikoits_companyRepository ;

	/*
	 * 会社一覧情報取得
	 * @param user Seikoits_userEntity ログインユーザー情報
	 * @return List<Seikoits_companyModel> 会社一覧情報
	 */
	public List<Seikoits_companyModel> getUnderCompanyInfos(Seikoits_userEntity user) throws Exception {

		// 自社情報取得
		Seikoits_companyModel model = getCompanyInfo(user.getCompanyid());
		List<Seikoits_companyEntity> entityList = seikoits_companyRepository.findCompanyListByLevel(model.getLevel());
		return getModelsByEntitys(entityList);

	}

	/*
	 * 会社詳細情報取得
	 * @param companyid Integer 会社ID
	 * @return Seikoits_companyModel 会社詳細情報
	 *
	 */
	public Seikoits_companyModel getCompanyInfo(Integer companyid) throws Exception {
		Optional<Seikoits_companyEntity> entity = seikoits_companyRepository.findById(companyid);
		return getModelByEntity(entity.get());

	}

	/*
	 * 会社登録
	 * @param user Seikoits_userEntity ログインユーザー情報
	 * @param model Seikoits_companyEntity 会社情報
	 * @return Seikoits_companyModel 登録後の会社情報
	 *
	 */
	public Seikoits_companyModel registerCompany(Seikoits_userEntity user, Seikoits_companyModel model) throws Exception {
		Seikoits_companyEntity entity = getEntitByModel(user, model);
		Seikoits_companyEntity insertedEntity = seikoits_companyRepository.save(entity);
		return getModelByEntity(insertedEntity);

	}

	/*
	 * 会社更新
	 * @param user Seikoits_userEntity ログインユーザー情報
	 * @param model Seikoits_companyEntity 会社情報
	 * @return Seikoits_companyModel 登録後の会社情報
	 *
	 */
	public Seikoits_companyModel updateCompany(Seikoits_userEntity user, Seikoits_companyModel model) throws Exception {
		Seikoits_companyEntity entity = getEntitByModelForUpdate(user,model);
		Seikoits_companyEntity updatedEntity = seikoits_companyRepository.save(entity);
		return getModelByEntity(updatedEntity);
	}

	/*
	 * 会社削除
	 * @param model Seikoits_companyEntity 会社情報
	 *
	 */
	public void deleteCompany(Seikoits_companyModel model) throws Exception {
		seikoits_companyRepository.deleteById(model.getCompanyid());
	}

	/*
	 * EntityリストからModeリストl取得
	 * @param entityList List<Seikoits_companyEntity> Entityリスト
	 * @return List<Seikoits_companyModel> Modeリスト
	 *
	 */
	private List<Seikoits_companyModel> getModelsByEntitys(List<Seikoits_companyEntity> entityList) throws Exception {
		List<Seikoits_companyModel> modelList = new ArrayList();
		for (Seikoits_companyEntity entity:entityList) {
			modelList.add(getModelByEntity(entity));
		}

		return modelList;

	}

	/*
	 * モデル取得
	 * @param entity Seikoits_companyEntity エンティティ
	 * @return Seikoits_companyModel モデル
	 *
	 */
	private Seikoits_companyModel getModelByEntity(Seikoits_companyEntity entity) throws Exception {
		Seikoits_companyModel model = new Seikoits_companyModel();
		model.setCompanyid(entity.getCompanyid());
		model.setCorporatenumber(entity.getCorporatenumber());
		model.setCompanyname(entity.getCompanyname());
		model.setAddress(entity.getAddress());
		model.setIndustry(entity.getIndustry());
		model.setMail(entity.getMail());
		model.setTel(entity.getTel());
		model.setFax(entity.getFax());
		model.setLevel(entity.getLevel());
		return model;

	}

	/*
	 * ModelからEntity取得(登録用)
	 * @param user Seikoits_userEntity ログインユーザー情報
	 * @param model Seikoits_companyModel
	 * @return Seikoits_companyEntity
	 *
	 */
	private Seikoits_companyEntity getEntitByModel(Seikoits_userEntity user, Seikoits_companyModel model) throws Exception {

		Seikoits_companyEntity entity = new Seikoits_companyEntity();

		/* システム日時 */
		Timestamp systemTime = new Timestamp(System.currentTimeMillis());

		// 自社情報取得
		Seikoits_companyModel myModel = getCompanyInfo(user.getCompanyid());

		// 情報設定
		entity.setCompanyname(model.getCompanyname());
		entity.setCorporatenumber(model.getCorporatenumber());
		entity.setAddress(model.getAddress());
		entity.setIndustry(model.getIndustry());
		entity.setMail(model.getMail());
		entity.setTel(model.getTel());
		entity.setFax(model.getFax());
		entity.setLevel(myModel.getLevel() + 1 );		// 代理店レベルアップ（＋１）
		entity.setI_uid(user.getUserid());
		entity.setI_time(systemTime);
		entity.setU_uid(user.getUserid());
		entity.setU_time(systemTime);

		return entity;

	}

	/*
	 * ModelからEntity取得(更新用)
	 * @param user Seikoits_userEntity ログインユーザー情報
	 * @param model Seikoits_companyModel
	 * @return Seikoits_companyEntity
	 *
	 */
	private Seikoits_companyEntity getEntitByModelForUpdate(Seikoits_userEntity user, Seikoits_companyModel model) throws Exception {

		Optional<Seikoits_companyEntity> company = seikoits_companyRepository.findById(model.getCompanyid());
		Seikoits_companyEntity entity = company.get();

		/* システム日時 */
		Timestamp systemTime = new Timestamp(System.currentTimeMillis());

		// 自社情報取得
		Seikoits_companyModel myModel = getCompanyInfo(user.getCompanyid());

		// 情報設定
		entity.setCompanyname(model.getCompanyname());
		entity.setCorporatenumber(model.getCorporatenumber());
		entity.setAddress(model.getAddress());
		entity.setIndustry(model.getIndustry());
		entity.setMail(model.getMail());
		entity.setTel(model.getTel());
		entity.setFax(model.getFax());
		entity.setU_uid(user.getUserid());
		entity.setU_time(systemTime);

		return entity;

	}
}
