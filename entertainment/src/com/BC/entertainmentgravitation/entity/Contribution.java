package com.BC.entertainmentgravitation.entity;

public class Contribution {
	/**
	 * 头像
	 **/
	String hand;
	/**
	 * 昵称
	 **/
	String name;
	/**
	 * 用户ID
	 **/
	String userID;
	/**
	 * 明星或平民
	 **/
	String Permission;
	/**
	 * 个人贡献
	 **/
	String contribution;
	/**
	 * 最高掌声
	 **/
	String highest;
	/**
	 * 获得掌声
	 **/
	String acquire;

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getPermission() {
		return Permission;
	}

	public void setPermission(String permission) {
		this.Permission = permission;
	}

	public String getHand() {
		return hand;
	}

	public void setHand(String hand) {
		this.hand = hand;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContribution() {
		return contribution;
	}

	public void setContribution(String contribution) {
		this.contribution = contribution;
	}

	public String getHighest() {
		return highest;
	}

	public void setHighest(String highest) {
		this.highest = highest;
	}

	public String getAcquire() {
		return acquire;
	}

	public void setAcquire(String acquire) {
		this.acquire = acquire;
	}

}

