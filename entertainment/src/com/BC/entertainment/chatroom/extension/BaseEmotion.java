package com.BC.entertainment.chatroom.extension;

public class BaseEmotion {
	
	private int category;//emotion kind
	
	private String name;
	
	private int value;//value
	
	private int exPoints;//extra points
	
	public BaseEmotion(int category, String name, int value, int exPoints)
	{
		this.category = category;
		this.name = name;
		this.value = value;
		this.exPoints = exPoints;
	}

	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public int getExPoints() {
		return exPoints;
	}

	public void setExPoints(int exPoints) {
		this.exPoints = exPoints;
	}
	
}
