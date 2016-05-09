package com.BC.entertainmentgravitation.entity;

/**
 * 我的收益中，可以兑换的娱票和金额
 * @author wen zhong
 *
 */
public class WithDraw {

	/**
	 * 娱票
	 */
	private String user_piao;
	
	/**
	 * 剩余的娱票
	 */
	private String user_piao_left;
	
	/**
	 * 可体现金额
	 */
	private String cash;

	public String getUser_piao() {
		return user_piao;
	}

	public void setUser_piao(String user_piao) {
		this.user_piao = user_piao;
	}

	public String getUser_piao_left() {
		return user_piao_left;
	}

	public void setUser_piao_left(String user_piao_left) {
		this.user_piao_left = user_piao_left;
	}

	public String getCash() {
		return cash;
	}

	public void setCash(String cash) {
		this.cash = cash;
	}
}
