package com.BC.entertainmentgravitation.entity;


public class Friend {
	private String Head_portrait;

	private String The_name_of_the;

	private String level;

	private String A_friend_ID;
	private String permission;

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public void setHead_portrait(String Head_portrait) {
		this.Head_portrait = Head_portrait;
	}

	public String getHead_portrait() {
		return this.Head_portrait;
	}

	public void setThe_name_of_the(String The_name_of_the) {
		this.The_name_of_the = The_name_of_the;
	}

	public String getThe_name_of_the() {
		return this.The_name_of_the;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getLevel() {
		return this.level;
	}

	public void setA_friend_ID(String A_friend_ID) {
		this.A_friend_ID = A_friend_ID;
	}

	public String getA_friend_ID() {
		return this.A_friend_ID;
	}
}
