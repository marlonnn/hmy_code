package com.BC.entertainmentgravitation.entity;

/**
 * 聊天室成员信息
 * @author zhongwen
 *
 */
public class Member{
	
	/**
	 * 用户账号，后台分配的
	 */
	private String id;
	/**
	 * 用户名，即手机号
	 */
	private String name;
	
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
	
}
