package com.BC.entertainmentgravitation.entity;

/**
 * 关注、 热门、 最新
 * @author zhongwen
 *
 */
public class FHNEntity {
	
	private String Username;

	private String Head_portrait;
	
	private String Star_ID;

	private String Star_names;
	
	private int The_current_hooted_thumb_up_prices;
	
	private String vstatus;
	
	private String region;
	
	private String peoples;
	
	private String portrait;//写真照片
	
	private int The_applause;

	public String getUsername() {
		return Username;
	}

	public void setUsername(String username) {
		this.Username = username;
	}

	public String getHead_portrait() {
		return Head_portrait;
	}

	public void setHead_portrait(String head_portrait) {
		Head_portrait = head_portrait;
	}

	public String getStar_ID() {
		return Star_ID;
	}

	public void setStar_ID(String star_ID) {
		Star_ID = star_ID;
	}

	public String getStar_names() {
		return Star_names;
	}

	public void setStar_names(String star_names) {
		Star_names = star_names;
	}

	public int getThe_current_hooted_thumb_up_prices() {
		return The_current_hooted_thumb_up_prices;
	}

	public void setThe_current_hooted_thumb_up_prices(
			int the_current_hooted_thumb_up_prices) {
		The_current_hooted_thumb_up_prices = the_current_hooted_thumb_up_prices;
	}

	public String getVstatus() {
		return vstatus;
	}

	public void setVstatus(String vstatus) {
		this.vstatus = vstatus;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getPeoples() {
		return peoples;
	}

	public void setPeoples(String peoples) {
		this.peoples = peoples;
	}

	public String getPortrait() {
		return portrait;
	}

	public void setPortrait(String portrait) {
		this.portrait = portrait;
	}

	public int getThe_applause() {
		return The_applause;
	}

	public void setThe_applause(int the_applause) {
		The_applause = the_applause;
	}
	
}
