package com.BC.entertainmentgravitation.entity;

import com.summer.config.Config;

public class WxOrder {
	
	private String appid = Config.APP_ID;
	
	private String partnerid = Config.MCH_ID;
	
	private String prepayid;
	
	private String wxPackage = Config.PACKAGE;
	
	private String noncestr;
	
	private String sign;
	
	private String timestamp;
	
	public String getAppid() {
		return appid;
	}

	public String getPartnerid() {
		return partnerid;
	}

	public String getPrepayid() {
		return prepayid;
	}

	public void setPrepayid(String prepayid) {
		this.prepayid = prepayid;
	}

	public String getWxPackage() {
		return wxPackage;
	}
	
	public String getNoncestr() {
		return noncestr;
	}

	public void setNoncestr(String noncestr) {
		this.noncestr = noncestr;
	}

	public String getTimestamp() {
		return timestamp;
	}
	
	public void setTimeStamp(String timestamp)
	{
		this.timestamp = timestamp;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}
	
}
