package com.BC.entertainmentgravitation.entity;

public class WxPrePayOrder {

	//�ͻ���
	private String clientID;
	
	//��Ʒ����
	private String productname;
	
	//�۸�
	private int price;
	
	//�������
	private int amount;

	public String getClientID() {
		return clientID;
	}

	public void setClientID(String clientID) {
		this.clientID = clientID;
	}

	public String getProductname() {
		return productname;
	}

	public void setProductname(String productname) {
		this.productname = productname;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}	
	
}
