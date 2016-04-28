package com.BC.entertainmentgravitation.entity;

/**
 * 娱币充值实体类
 * @author wen zhong
 *
 */
public class Yubi {
	
	/**
	 * 充值数量
	 */
	private int amount;
	
	/**
	 * 价格
	 */
	private String price;
	
	/**
	 * 额外奖励
	 */
	private int bonus;
	
	public Yubi()
	{
		
	}
	
	public Yubi(int amount, String price, int bonus)
	{
	    this.amount = amount;
	    this.price = price;
	    this.bonus = bonus;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public int getBonus() {
		return bonus;
	}

	public void setBonus(int bonus) {
		this.bonus = bonus;
	}
	
}
