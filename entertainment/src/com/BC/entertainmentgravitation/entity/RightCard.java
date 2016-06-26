package com.BC.entertainmentgravitation.entity;

import java.io.Serializable;

/**
 * 权益卡
 * @author zhongwen
 *
 */
@SuppressWarnings("serial")
public class RightCard implements Serializable{

	private String card_id;
	
	private String star_id;
	
	private String total;
	
	private String surplus;
	
	private String price;
	
	private String label;
	
	private String describes;
	
	private String region;
	
	private String time;
	
	private String nick_name;
	
	private String head;

	public String getCard_id() {
		return card_id;
	}

	public void setCard_id(String card_id) {
		this.card_id = card_id;
	}

	public String getStar_id() {
		return star_id;
	}

	public void setStar_id(String star_id) {
		this.star_id = star_id;
	}

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	public String getSurplus() {
		return surplus;
	}

	public void setSurplus(String surplus) {
		this.surplus = surplus;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDescribes() {
		return describes;
	}

	public void setDescribes(String describes) {
		this.describes = describes;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getNick_name() {
		return nick_name;
	}

	public void setNick_name(String nick_name) {
		this.nick_name = nick_name;
	}

	public String getHead() {
		return head;
	}

	public void setHead(String head) {
		this.head = head;
	}
}
