package com.ifocus.trackun.seikoits.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.ifocus.trackun.seikoits.constant.LevelConstant;
import com.ifocus.trackun.seikoits.constant.RoleConstant;
import com.ifocus.trackun.seikoits.entity.Seikoits_companyEntity;
import com.ifocus.trackun.seikoits.entity.Seikoits_companyRepository;
import com.ifocus.trackun.seikoits.entity.Seikoits_divisionEntity;
import com.ifocus.trackun.seikoits.entity.Seikoits_divisionRepository;
import com.ifocus.trackun.seikoits.entity.Seikoits_groupEntity;
import com.ifocus.trackun.seikoits.entity.Seikoits_groupRepository;
import com.ifocus.trackun.seikoits.entity.Seikoits_userEntity;
import com.ifocus.trackun.seikoits.entity.Seikoits_userRepository;
import com.ifocus.trackun.seikoits.model.PfToken;
import com.ifocus.trackun.seikoits.model.Seikoits_userModel;

@Component
public class UserService {

	@Autowired
	private Seikoits_userRepository userRepository;
	@Autowired
	private Seikoits_companyRepository companyRepository;
	@Autowired
	private Seikoits_divisionRepository divisionRepository;
	@Autowired
	private Seikoits_groupRepository groupRepository;

	@Autowired
	private IotPFService iotPFService;
	
	@Autowired
	private SystemService systemService;
	
	@PostConstruct
	private void initTopUser() {
		if (systemService.getSystemTopUser() == null) {
			throw new RuntimeException("System service failed to initlize system top user.");
		}
	}

	public Seikoits_userModel login(Seikoits_userEntity userEntity){
		Seikoits_userModel loginedUser = null;
		PfToken pfToken = iotPFService.getPfToken();
		if (pfToken != null) {
			userEntity.setToken(pfToken.getAccess_token());
			userEntity = userRepository.save(userEntity);
			if (userEntity != null) {
				Gson gson = new Gson();
				loginedUser = gson.fromJson(gson.toJson(userEntity), Seikoits_userModel.class);
				loginedUser.setPfToken(pfToken);
			}
		}
		return loginedUser;
	}

	public Seikoits_userModel refreshToken(Seikoits_userEntity userEntity, String refreshToken) {
		Seikoits_userModel loginedUser = null;

		if (userEntity != null) {
			PfToken pfToken = iotPFService.refreshPfToken(userEntity, refreshToken);
			userEntity.setToken(pfToken.getAccess_token());
			userEntity = userRepository.save(userEntity);

			Gson gson = new Gson();
			loginedUser = gson.fromJson(gson.toJson(userEntity), Seikoits_userModel.class);
			loginedUser.setPfToken(pfToken);
		}

		return loginedUser;
	}

	/*
	 * ユーザ一覧情報取得
	 * @param user Seikoits_userEntity ログインユーザー情報
	 * @param divisionid Integer 部門ID
	 * @return List<Seikoits_groupModel> グループ一覧情報
	 */
	public List<Seikoits_userModel> getUnderUserInfos(Seikoits_userEntity user) throws Exception {

		List<Seikoits_userEntity> entityList = new ArrayList<Seikoits_userEntity>();

		// 部門管理者の場合
		if (user.getRole() == RoleConstant.ROLE_DIV_PERSON) {
			entityList = userRepository.findUserListByDivisionid(user.getDivisionid());
		// 会社管理者の場合
		} if (user.getRole() == RoleConstant.ROLE_ADMIN) {
			// 会社情報取得
			Optional<Seikoits_companyEntity> company = companyRepository.findById(user.getCompanyid());
			Seikoits_companyEntity companyEntity = company.get();
			// 2級代理店レベル
			if (LevelConstant.LEVEL_TWO == companyEntity.getLevel()) {
				entityList = userRepository.findUserListByCompanyid(user.getCompanyid());
			} else {
				// ifocus または　正興
				entityList = userRepository.findUserListByLevel(companyEntity.getLevel());
			}
		}

		return getModelsByEntitys(entityList);

	}

	/*
	 * ユーザ詳細情報取得
	 * @param userid Integer ユーザID
	 * @return Seikoits_userModel ユーザ詳細情報
	 *
	 */
	public Seikoits_userModel getUserInfo(Integer userid) throws Exception {
		Optional<Seikoits_userEntity> entity = userRepository.findById(userid);
		return getModelByEntity(entity.get());

	}

	/*
	 * ユーザ情報チェック（For登録＆更新）
	 * @param user Seikoits_userEntity ログインユーザー情報
	 * @param model Seikoits_userModel ユーザ情報
	 * @return String チェック結果
	 *      OK = チェックOK
	 *      OK以外 = エラーメッセージ
	 *
	 */
	public String checkUserInfo(Seikoits_userEntity user, Seikoits_userModel model) throws Exception {

		String returnStr = "OK";
		// 部門管理者の場合
		if (model.getRole() == RoleConstant.ROLE_DIV_PERSON) {
			if (model.getDivisionid() == null) {
				returnStr = "部門管理者には、divisionidの指定が必須です。";
			} else {
				// 部門情報取得
				Optional<Seikoits_divisionEntity> division = divisionRepository.findById(model.getDivisionid());
				// 会社IDを設定する
				model.setCompanyid(division.get().getCompanyid());
			}
		// 会社管理者の場合
		} if (model.getRole() == RoleConstant.ROLE_ADMIN) {
			if (model.getCompanyid() == null) {
				returnStr = "会社管理者には、companyidの指定が必須です。";
			}
		// 担当者の場合
		} if (model.getRole() == RoleConstant.ROLE_DIV_PERSON) {
			if (model.getGroupid() == null) {
				returnStr = "担当者には、groupidの指定が必須です。";
			} else {
				// グループ情報取得
				Optional<Seikoits_groupEntity> group = groupRepository.findById(user.getGroupid());
				// 会社IDを設定する
				model.setCompanyid(group.get().getCompanyid());
				// 部門IDを設定する
				model.setDivisionid(group.get().getDivisionid());
			}
		}
		return returnStr;

	}

	/*
	 * ユーザ登録
	 * @param user Seikoits_userEntity ログインユーザー情報
	 * @param model Seikoits_userModel ユーザ情報
	 * @return Seikoits_userModel 登録後のユーザ情報
	 *
	 */
	public Seikoits_userModel registerUser(Seikoits_userEntity user, Seikoits_userModel model) throws Exception {
		Seikoits_userEntity entity = getEntitByModel(user, model);
		Seikoits_userEntity insertedEntity = userRepository.save(entity);
		return getModelByEntity(insertedEntity);

	}

	/*
	 * ユーザ更新
	 * @param user Seikoits_userEntity ログインユーザー情報
	 * @param model Seikoits_userEntity ユーザ情報
	 * @return Seikoits_userModel 登録後のユーザ情報
	 *
	 */
	public Seikoits_userModel updateUser(Seikoits_userEntity user, Seikoits_userModel model) throws Exception {
		Seikoits_userEntity entity = getEntitByModelForUpdate(user,model);
		Seikoits_userEntity updatedEntity = userRepository.save(entity);
		return getModelByEntity(updatedEntity);
	}

	/*
	 * ユーザプロファイル更新
	 * @param user Seikoits_userEntity ログインユーザー情報
	 * @param model Seikoits_userEntity ユーザ情報
	 * @return Seikoits_userModel 登録後のユーザ情報
	 *
	 */
	public Seikoits_userModel updateUserProfile(Seikoits_userEntity user, Seikoits_userModel model) throws Exception {
		Seikoits_userEntity entity = getEntitByModelForUpdateProfile(user,model);
		Seikoits_userEntity updatedEntity = userRepository.save(entity);
		return getModelByEntity(updatedEntity);
	}

	/*
	 * ユーザ削除
	 * @param model Seikoits_userEntity ユーザ情報
	 *
	 */
	public void deleteUser(Seikoits_userModel model) throws Exception {
		userRepository.deleteById(model.getUserid());
	}

	/*
	 * EntityリストからModeリストl取得
	 * @param entityList List<Seikoits_userEntity> Entityリスト
	 * @return List<Seikoits_userModel> Modeリスト
	 *
	 */
	private List<Seikoits_userModel> getModelsByEntitys(List<Seikoits_userEntity> entityList) throws Exception {
		List<Seikoits_userModel> modelList = new ArrayList<Seikoits_userModel>();
		for (Seikoits_userEntity entity:entityList) {
			modelList.add(getModelByEntity(entity));
		}

		return modelList;

	}

	/*
	 * モデル取得
	 * @param entity Seikoits_userEntity エンティティ
	 * @return Seikoits_userModel モデル
	 *
	 */
	private Seikoits_userModel getModelByEntity(Seikoits_userEntity entity) throws Exception {
		Seikoits_userModel model = new Seikoits_userModel();
		model.setUserid(entity.getUserid());
		model.setCompanyid(entity.getCompanyid());
		model.setDivisionid(entity.getDivisionid());
		model.setGroupid(entity.getGroupid());
		model.setUsername(entity.getUsername());
		model.setLoginid(entity.getLoginid());
//		model.setPassword(entity.getPassword());
		model.setRole(entity.getRole());
		return model;

	}

	/*
	 * ModelからEntity取得(登録用)
	 * @param user Seikoits_userEntity ログインユーザー情報
	 * @param model Seikoits_userModel
	 * @return Seikoits_userEntity
	 *
	 */
	private Seikoits_userEntity getEntitByModel(Seikoits_userEntity user, Seikoits_userModel model) throws Exception {

		Seikoits_userEntity entity = new Seikoits_userEntity();

		/* システム日時 */
		Timestamp systemTime = new Timestamp(System.currentTimeMillis());

		// 情報設定
		entity.setUserid(model.getUserid());
		entity.setCompanyid(model.getCompanyid());
		entity.setDivisionid(model.getDivisionid());
		entity.setGroupid(model.getGroupid());
		entity.setUsername(model.getUsername());
		entity.setLoginid(model.getLoginid());
		entity.setPassword(DigestUtils.md5Hex(model.getPassword().getBytes()));
		entity.setRole(model.getRole());

		entity.setI_uid(user.getUserid());
		entity.setI_time(systemTime);
		entity.setU_uid(user.getUserid());
		entity.setU_time(systemTime);

		return entity;

	}

	/*
	 * ModelからEntity取得(更新用)
	 * @param user Seikoits_userEntity ログインユーザー情報
	 * @param model Seikoits_userModel
	 * @return Seikoits_userEntity
	 *
	 */
	private Seikoits_userEntity getEntitByModelForUpdate(Seikoits_userEntity user, Seikoits_userModel model) throws Exception {

		Optional<Seikoits_userEntity> updateUser = userRepository.findById(model.getUserid());
		Seikoits_userEntity entity = updateUser.get();

		/* システム日時 */
		Timestamp systemTime = new Timestamp(System.currentTimeMillis());

		// 情報設定
		entity.setUserid(model.getUserid());
		entity.setCompanyid(model.getCompanyid());
		entity.setDivisionid(model.getDivisionid());
		entity.setGroupid(model.getGroupid());
		entity.setUsername(model.getUsername());
		entity.setLoginid(model.getLoginid());
		if (model.getPassword() != null ) {
			entity.setPassword(DigestUtils.md5Hex(model.getPassword().getBytes()));
		}
		entity.setRole(model.getRole());

		entity.setU_uid(user.getUserid());
		entity.setU_time(systemTime);

		return entity;

	}

	/*
	 * ModelからEntity取得(更新用)
	 * @param user Seikoits_userEntity ログインユーザー情報
	 * @param model Seikoits_userModel
	 * @return Seikoits_userEntity
	 *
	 */
	private Seikoits_userEntity getEntitByModelForUpdateProfile(Seikoits_userEntity user, Seikoits_userModel model) throws Exception {

		Optional<Seikoits_userEntity> updateUser = userRepository.findById(model.getUserid());
		Seikoits_userEntity entity = updateUser.get();

		/* システム日時 */
		Timestamp systemTime = new Timestamp(System.currentTimeMillis());

		// 情報設定
		entity.setUsername(model.getUsername());
		entity.setPassword(DigestUtils.md5Hex(model.getPassword().getBytes()));

		entity.setU_uid(user.getUserid());
		entity.setU_time(systemTime);

		return entity;

	}
}
