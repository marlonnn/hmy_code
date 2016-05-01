package com.BC.entertainmentgravitation.entity;

public class Comment_on_the_list {
	private String userID;
	
	private String permission;
	
	private String Evaluation_of_the_pictures;

	private String nickname;

	private String Comment_on_the_content;

	private String Comment_on_time;

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public void setEvaluation_of_the_pictures(String Evaluation_of_the_pictures) {
		this.Evaluation_of_the_pictures = Evaluation_of_the_pictures;
	}

	public String getEvaluation_of_the_pictures() {
		return this.Evaluation_of_the_pictures;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getNickname() {
		return this.nickname;
	}

	public void setComment_on_the_content(String Comment_on_the_content) {
		this.Comment_on_the_content = Comment_on_the_content;
	}

	public String getComment_on_the_content() {
		return this.Comment_on_the_content;
	}

	public void setComment_on_time(String Comment_on_time) {
		this.Comment_on_time = Comment_on_time;
	}

	public String getComment_on_time() {
		return this.Comment_on_time;
	}
}
