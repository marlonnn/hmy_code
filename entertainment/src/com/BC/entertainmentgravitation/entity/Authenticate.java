package com.BC.entertainmentgravitation.entity;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Authenticate implements Serializable {

	/**
	 * 真实姓名
	 */
	private String name;
	
	/**
	 * 手机号码
	 */
	private String mobile;
	
	/**
	 * 银行卡号
	 */
	private String bankCard;
	
	/**
	 * 开户银行
	 */
	private String bank;
	
	/**
	 * 开户省份
	 */
	private String bankProvince;
	
	/**
	 * 开户城市
	 */
	private String bankCity;
	
	/**
	 * 支行名称
	 */
	private String bankBranch;
	
	/**
	 * 证件类型
	 */
	private String authType;
	
	/**
	 * 证件号码
	 */
	private String id;
	
	/**
	 * 专业名称
	 */
	private String proName;
	
	/**
	 * 专业证照片
	 */
	private String proPhoto;
	
	/**
	 * 身份证正面
	 */
	private String idCardFontPhoto;
	
	/**
	 * 身份证反面
	 */
	private String idCardBackPhoto;
	
	/**
	 * 手持身份证正面
	 */
	private String idCardPhoto;

	public Authenticate(String name, String mobile)
	{
		this.name = name;
		this.mobile = mobile;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getBankCard() {
		return bankCard;
	}

	public void setBankCard(String bankCard) {
		this.bankCard = bankCard;
	}

	public String getBank() {
		return bank;
	}

	public void setBank(String bank) {
		this.bank = bank;
	}

	public String getBankProvince() {
		return bankProvince;
	}

	public void setBankProvince(String bankProvince) {
		this.bankProvince = bankProvince;
	}

	public String getBankCity() {
		return bankCity;
	}

	public void setBankCity(String bankCity) {
		this.bankCity = bankCity;
	}

	public String getBankBranch() {
		return bankBranch;
	}

	public void setBankBranch(String bankBranch) {
		this.bankBranch = bankBranch;
	}

	public String getAuthType() {
		return authType;
	}

	public void setAuthType(String authType) {
		this.authType = authType;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProName() {
		return proName;
	}

	public void setProName(String proName) {
		this.proName = proName;
	}

	public String getProPhoto() {
		return proPhoto;
	}

	public void setProPhoto(String proPhoto) {
		this.proPhoto = proPhoto;
	}

	public String getIdCardFontPhoto() {
		return idCardFontPhoto;
	}

	public void setIdCardFontPhoto(String idCardFontPhoto) {
		this.idCardFontPhoto = idCardFontPhoto;
	}

	public String getIdCardBackPhoto() {
		return idCardBackPhoto;
	}

	public void setIdCardBackPhoto(String idCardBackPhoto) {
		this.idCardBackPhoto = idCardBackPhoto;
	}

	public String getIdCardPhoto() {
		return idCardPhoto;
	}

	public void setIdCardPhoto(String idCardPhoto) {
		this.idCardPhoto = idCardPhoto;
	}
	
}
