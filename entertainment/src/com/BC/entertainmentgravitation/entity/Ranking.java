package com.BC.entertainmentgravitation.entity;

public class Ranking {
	private String Head_portrait;

	private String Star_ID;

	private String Star_names;

	private String My_contribution;

	private int The_applause;

	private String Most_contribution_applause_user_ID;

	private String The_prize_ID;
	
	private int The_current_hooted_thumb_up_prices;
	
	private String vstatus;

	public int getThe_current_hooted_thumb_up_prices() {
		return The_current_hooted_thumb_up_prices;
	}

	public void setThe_current_hooted_thumb_up_prices(
			int the_current_hooted_thumb_up_prices) {
		The_current_hooted_thumb_up_prices = the_current_hooted_thumb_up_prices;
	}

	public void setHead_portrait(String Head_portrait) {
		this.Head_portrait = Head_portrait;
	}

	public String getHead_portrait() {
		return this.Head_portrait;
	}

	public void setStar_ID(String Star_ID) {
		this.Star_ID = Star_ID;
	}

	public String getStar_ID() {
		return this.Star_ID;
	}

	public void setStar_names(String Star_names) {
		this.Star_names = Star_names;
	}

	public String getStar_names() {
		return this.Star_names;
	}

	public void setMy_contribution(String My_contribution) {
		this.My_contribution = My_contribution;
	}

	public String getMy_contribution() {
		return this.My_contribution;
	}

	public void setThe_applause(int The_applause) {
		this.The_applause = The_applause;
	}

	public int getThe_applause() {
		return this.The_applause;
	}

	public void setMost_contribution_Applause_user_ID(
			String Most_contribution_applause_user_ID) {
		this.Most_contribution_applause_user_ID = Most_contribution_applause_user_ID;
	}

	public String getMost_contribution_Applause_user_ID() {
		return this.Most_contribution_applause_user_ID;
	}

	public void setThe_prize_ID(String The_prize_ID) {
		this.The_prize_ID = The_prize_ID;
	}

	public String getThe_prize_ID() {
		return this.The_prize_ID;
	}

	public String getVstatus() {
		return vstatus;
	}

	public void setVstatus(String vstatus) {
		this.vstatus = vstatus;
	}
	
}

