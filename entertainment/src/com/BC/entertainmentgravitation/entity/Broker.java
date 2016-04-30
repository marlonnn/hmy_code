package com.BC.entertainmentgravitation.entity;

/**
 * 基本信息中 经纪人 实体类
 * @author zhongwen
 *
 */
public class Broker {
	private String Agent_name;

	private String The_phone;

	private String QQ;

	private String WeChat;

	private String email;

	private String address;

	public void setAgent_name(String Agent_name) {
		this.Agent_name = Agent_name;
	}

	public String getAgent_name() {
		return this.Agent_name;
	}

	public void setThe_phone(String The_phone) {
		this.The_phone = The_phone;
	}

	public String getThe_phone() {
		return this.The_phone;
	}

	public void setQQ(String QQ) {
		this.QQ = QQ;
	}

	public String getQQ() {
		return this.QQ;
	}

	public void setWeChat(String WeChat) {
		this.WeChat = WeChat;
	}

	public String getWeChat() {
		return this.WeChat;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmail() {
		return this.email;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAddress() {
		return this.address;
	}
}
