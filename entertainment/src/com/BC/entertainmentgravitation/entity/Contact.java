package com.BC.entertainmentgravitation.entity;

public class Contact {
	
	private String id;
	private String content;
	private String account;
	private String nick;
	private String avator;
	long time;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getNick() {
		return nick;
	}
	public void setNick(String nick) {
		this.nick = nick;
	}
	public String getAvator() {
		return avator;
	}
	public void setAvator(String avator) {
		this.avator = avator;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
}
