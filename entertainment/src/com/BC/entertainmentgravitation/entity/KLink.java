package com.BC.entertainmentgravitation.entity;


import java.util.List;

public class KLink {
	private List<Point> point;

	private String max;
	private String min;
	private String difference;//涨跌
	private String bonus;//红包数
	private String bid;//当前指数

	public String getDifference() {
		return difference;
	}

	public void setDifference(String difference) {
		this.difference = difference;
	}

	public String getMin() {
		return min;
	}

	public void setMin(String min) {
		this.min = min;
	}

	public void setPoint(List<Point> point) {
		this.point = point;
	}

	public List<Point> getPoint() {
		return this.point;
	}

	public void setMax(String max) {
		this.max = max;
	}

	public String getMax() {
		return this.max;
	}

	public String getBonus() {
		return bonus;
	}

	public void setBonus(String bonus) {
		this.bonus = bonus;
	}

	public String getBid() {
		return bid;
	}

	public void setBid(String bid) {
		this.bid = bid;
	}
}
