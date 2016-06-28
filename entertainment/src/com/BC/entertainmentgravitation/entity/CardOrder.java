package com.BC.entertainmentgravitation.entity;

/**
 * 权益卡订单
 * 订单类型 state 0：我发布 1：我购买  2：被购买
 * @author wen zhong
 *
 */
public class CardOrder {
	
	private String order_id;
	
	private String order_sn;
	
	private String card_id;
	
	private String userID;
	
	private String star_id;
	
	private String quantity;
	
	private String price;
	
	//状态： 0、退回  1、预购、  2、预约  3、同意  4、完成
	private String state;
	
	private String price_index;
	
	private String bid;
	
	private String order_time;
	
	private String reserve_time;
	
	private String cancel_time;
	
	private String agree_time;
	
	private String over_time;
	
	private String nick_name;
	
	private String head;
	
	private String region;
	
	private String label;

	public String getOrder_id() {
		return order_id;
	}

	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}

	public String getOrder_sn() {
		return order_sn;
	}

	public void setOrder_sn(String order_sn) {
		this.order_sn = order_sn;
	}

	public String getCard_id() {
		return card_id;
	}

	public void setCard_id(String card_id) {
		this.card_id = card_id;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getStar_id() {
		return star_id;
	}

	public void setStar_id(String star_id) {
		this.star_id = star_id;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getPrice_index() {
		return price_index;
	}

	public void setPrice_index(String price_index) {
		this.price_index = price_index;
	}

	public String getOrder_time() {
		return order_time;
	}

	public void setOrder_time(String order_time) {
		this.order_time = order_time;
	}

	public String getReserve_time() {
		return reserve_time;
	}

	public void setReserve_time(String reserve_time) {
		this.reserve_time = reserve_time;
	}

	public String getCancel_time() {
		return cancel_time;
	}

	public void setCancel_time(String cancel_time) {
		this.cancel_time = cancel_time;
	}

	public String getAgree_time() {
		return agree_time;
	}

	public void setAgree_time(String agree_time) {
		this.agree_time = agree_time;
	}

	public String getOver_time() {
		return over_time;
	}

	public void setOver_time(String over_time) {
		this.over_time = over_time;
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

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getBid() {
		return bid;
	}

	public void setBid(String bid) {
		this.bid = bid;
	}
	
}
