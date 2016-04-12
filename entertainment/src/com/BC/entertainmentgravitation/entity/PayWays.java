package com.BC.entertainmentgravitation.entity;

/**
 *支付方式  微信支付/支付宝支付
 * 
 * @author zhongwen
 *
 */
public class PayWays {
	
	private String name;
	
	private int imageResource;
	
	private int payId;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getImageResource() {
		return imageResource;
	}

	public void setImageResource(int imageResource) {
		this.imageResource = imageResource;
	}

	public int getPayId() {
		return payId;
	}

	public void setPayId(int payId) {
		this.payId = payId;
	}
}
