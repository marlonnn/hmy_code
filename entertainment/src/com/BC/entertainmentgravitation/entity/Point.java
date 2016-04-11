package com.BC.entertainmentgravitation.entity;
public class Point {
	private String id;
	
	private String user_id;
	
	private String price;
	
	private String date_time;
	
	public void setId(String id){
	this.id = id;
	}
	public String getId(){
	return this.id;
	}
	public void setUser_id(String user_id){
	this.user_id = user_id;
	}
	public String getUser_id(){
	return this.user_id;
	}
	public void setPrice(String price){
	this.price = price;
	}
	public String getPrice(){
	return this.price;
	}
	public void setDate_time(String date_time){
	this.date_time = date_time;
	}
	public String getDate_time(){
	return this.date_time;
	}
}

