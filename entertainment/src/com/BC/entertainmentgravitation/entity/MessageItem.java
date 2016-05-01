package com.BC.entertainmentgravitation.entity;

public class MessageItem {
	private String star_name;

	private String Message_ID;

	private String Head_portrait;

	private String describe;

	private String If_there_is_a_picture;
	private String External_links;

	public String getExternal_links() {
		return External_links;
	}

	public String getStar_nmae() {
		// TODO Auto-generated method stub
		return star_name;
	}

	public void setStar_nmae(String starNmae) {
		this.star_name = starNmae;
	}

	public void setExternal_links(String external_links) {
		External_links = external_links;
	}

	public void setMessage_ID(String Message_ID) {
		this.Message_ID = Message_ID;
	}

	public String getMessage_ID() {
		return this.Message_ID;
	}

	public void setHead_portrait(String Head_portrait) {
		this.Head_portrait = Head_portrait;
	}

	public String getHead_portrait() {
		return this.Head_portrait;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}

	public String getDescribe() {
		return this.describe;
	}

	public void setIf_there_is_a_picture(String If_there_is_a_picture) {
		this.If_there_is_a_picture = If_there_is_a_picture;
	}

	public String getIf_there_is_a_picture() {
		return this.If_there_is_a_picture;
	}
}

