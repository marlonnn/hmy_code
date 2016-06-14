package com.BC.entertainmentgravitation.entity;

/**
 * 发现页实体类
 * 
 * @author wen zhong
 *
 */
public class Found {

	private int resource;
	
	private String info;
	
	public Found(int resource, String info)
	{
		this.resource = resource;
		
		this.info = info;
	}

	public int getResource() {
		return resource;
	}

	public void setResource(int resource) {
		this.resource = resource;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}
	
}
