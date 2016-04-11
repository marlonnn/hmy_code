package com.BC.entertainmentgravitation.entity;

public class OutConnect {

	/**
	 * 连接ID
	 **/
	private String connectID;
	/**
	 * 明星
	 **/
	private int starID;
	/**
	 * 标题
	 **/
	private String title;
	/**
	 * 链接
	 **/
	private String link;
	/**
	 * 类型1、百度贴吧，2、QQ空间3、新浪微博，4、视屏链接，5、其他
	 **/
	private int type;
	/**
	 * 图片
	 **/
	private String icon;

	public String getConnectID() {
		return connectID;
	}

	public void setConnectID(String connectID) {
		this.connectID = connectID;
	}

	public int getStarID() {
		return starID;
	}

	public void setStarID(int starID) {
		this.starID = starID;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

}
