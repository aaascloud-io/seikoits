package com.ifocus.trackun.seikoits;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ifocus.trackun.seikoits.entity.Seikoits_userEntity;
import com.ifocus.trackun.seikoits.entity.Seikoits_userRepository;
import com.ifocus.trackun.seikoits.model.Seikoits_companyModel;
import com.ifocus.trackun.seikoits.model.Seikoits_divisionModel;
import com.ifocus.trackun.seikoits.service.CompanyService;
import com.ifocus.trackun.seikoits.service.DivisionService;

@SpringBootTest
class SeikoitsApplicationTests {

	@Autowired
	private Seikoits_userRepository seikoits_userRepository;

	@Autowired
	private CompanyService companyService;
	@Autowired
	private DivisionService divisionService;

	@Test
	void contextLoads() {
	}

	/*
	 * CompanyService
	 * 一覧取得テストgetUnderCompanyInfos
	 * 正常系
	 *
	 */
	@Test
	public void testGetUnderCompanyInfos() throws Exception {
		// ifocus
		Optional<Seikoits_userEntity> loginUserEntity = seikoits_userRepository.findById(1);

		List<Seikoits_companyModel> returnList = companyService.getUnderCompanyInfos(loginUserEntity.get());

		assertEquals(4, returnList.size());

	}

	/*
	 * CompanyService
	 * 会社詳細情報取得テストgetCompanyInfo
	 * 正常系
	 *
	 */
	@Test
	public void testGetCompanyInfo() throws Exception {

		Seikoits_companyModel model = companyService.getCompanyInfo(1);

		assertEquals("2011501020673", model.getCorporatenumber());

	}

	/*
	 * CompanyService
	 * 会社更新テストupdateCompany
	 * 正常系
	 *
	 */
	@Test
	public void testUpdateCompany() throws Exception {

		// ifocus
		Optional<Seikoits_userEntity> loginUserEntity = seikoits_userRepository.findById(1);

		Seikoits_companyModel model = new Seikoits_companyModel();
		model.setCompanyid(1);
		model.setCorporatenumber("2011501029999");
		model.setCompanyname("アイフォーカス株式会社");
		model.setAddress("東京都中央区入船");
		model.setIndustry("サービス業");
		model.setMail("XXX@i-focus.co.jp");
		model.setTel("03-1234-XXXX");
		model.setFax("03-1234-YYYY");

		Seikoits_companyModel updated = companyService.updateCompany(loginUserEntity.get(), model);

		assertEquals("2011501029999", updated.getCorporatenumber());
		assertEquals("アイフォーカス株式会社", updated.getCompanyname());
		assertEquals("東京都中央区入船", updated.getAddress());
		assertEquals("サービス業", updated.getIndustry());
		assertEquals("XXX@i-focus.co.jp", updated.getMail());
		assertEquals("03-1234-XXXX", updated.getTel());
		assertEquals("03-1234-YYYY", updated.getFax());
		assertEquals(0, updated.getLevel());

	}

	/*
	 * CompanyService
	 * 会社登録テストregisterCompany
	 * 正常系
	 *
	 */
	@Test
	public void testRegisterCompany() throws Exception {

		// ifocus
		Optional<Seikoits_userEntity> loginUserEntity = seikoits_userRepository.findById(1);

		Seikoits_companyModel model = new Seikoits_companyModel();
		model.setCorporatenumber("201150102XXXX");
		model.setCompanyname("アイフォーカス支社");
		model.setAddress("大阪中央区");
		model.setIndustry("サービス業");
		model.setMail("YYY@i-focus.co.jp");
		model.setTel("03-1234-ZZZZ");
		model.setFax("03-1234-AAAA");

		Seikoits_companyModel inserted = companyService.registerCompany(loginUserEntity.get(), model);

		assertEquals("201150102XXXX", inserted.getCorporatenumber());
		assertEquals("アイフォーカス支社", inserted.getCompanyname());
		assertEquals("大阪中央区", inserted.getAddress());
		assertEquals("サービス業", inserted.getIndustry());
		assertEquals("YYY@i-focus.co.jp", inserted.getMail());
		assertEquals("03-1234-ZZZZ", inserted.getTel());
		assertEquals("03-1234-AAAA", inserted.getFax());
		assertEquals(1, inserted.getLevel());

	}

	/*
	 * DivisionService
	 * 部門更新テストupdateDivision
	 * 正常系
	 *
	 */
	@Test
	public void testUpdateDivision() throws Exception {

		// ifocus
		Optional<Seikoits_userEntity> loginUserEntity = seikoits_userRepository.findById(1);

		Seikoits_divisionModel model = new Seikoits_divisionModel();
		model.setDivisionid(6);
		model.setCompanyid(2);
		model.setDivisionname("開発部");
		model.setSummary("東京都");
		model.setManager("鈴木");
		model.setManagermail("XXX@i-focus.co.jp");
		model.setManagertel("03-1234-XXXX");

		Seikoits_divisionModel updated = divisionService.updateDivision(loginUserEntity.get(), model);

		assertEquals("開発部", updated.getDivisionname());
		assertEquals("東京都", updated.getSummary());
		assertEquals("鈴木", updated.getManager());
		assertEquals("XXX@i-focus.co.jp", updated.getManagermail());
		assertEquals("03-1234-XXXX", updated.getManagertel());

	}
}
