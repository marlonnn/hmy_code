package com.BC.entertainmentgravitation.entity;

import java.io.Serializable;

/**
 * 聊天室成员信息
 * @author zhongwen
 *
 */
@SuppressWarnings("serial")
public class Member implements Serializable{
	
	/**
	 * 用户账号，后台分配的
	 */
	private String id;
	
	/**
	 * 用户名，即手机号
	 */
	private String name;
	
	/**
	 * 昵称
	 */
	private String nick;
	/**
	 * 头像地址，绝对地址
	 */
	private String portrait;	
	/**
	 * 身高
	 */
	private String height;	
	/**
	 * 体重
	 */
	private String weight;	
	/**
	 * 性别
	 */
	private String gender;	
	/**
	 * 语言
	 */
	private String language;
	/**
	 * 国际
	 */
	private String nationality;
	/**
	 * 地区
	 */
	private String region;
	/**
	 * 年龄
	 */
	private String age;
	
	private String birthday;
	/**
	 * 娛币
	 */
	private String dollar;
	/**
	 * 娱票
	 */
	private String piao;
	
	/**
	 * 职业
	 */
	private String professional;
	
	/**
	 * 关注数
	 */
	private String follow;
	
	/**
	 * 粉丝数
	 */
	private String fans;
	
	/**
	 * 心情
	 */
	private String mood;
	
	/**
	 * 审核状态
	 */
	private String is_validated;
	
	/***
	 * emial
	 */
	private String email;
	
	/**
	 * 手机号码
	 */
	private String mobile;
	
	/**
	 * 星座
	 */
	private String constellation;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getPortrait() {
		return portrait;
	}

	public void setPortrait(String portrait) {
		this.portrait = portrait;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getDollar() {
		return dollar;
	}

	public void setDollar(String dollar) {
		this.dollar = dollar;
	}

	public String getPiao() {
		return piao;
	}

	public void setPiao(String piao) {
		this.piao = piao;
	}

	public String getProfessional() {
		return professional;
	}

	public void setProfessional(String professional) {
		this.professional = professional;
	}

	public String getFollow() {
		return follow;
	}

	public void setFollow(String follow) {
		this.follow = follow;
	}

	public String getFans() {
		return fans;
	}

	public void setFans(String fans) {
		this.fans = fans;
	}

	public String getMood() {
		return mood;
	}

	public void setMood(String mood) {
		this.mood = mood;
	}

	public String getIs_validated() {
		return is_validated;
	}

	public void setIs_validated(String is_validated) {
		this.is_validated = is_validated;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getConstellation() {
		return constellation;
	}

	public void setConstellation(String constellation) {
		this.constellation = constellation;
	}
	
}
