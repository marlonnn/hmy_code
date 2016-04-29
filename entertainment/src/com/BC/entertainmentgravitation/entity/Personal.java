package com.BC.entertainmentgravitation.entity;

public class Personal {

	private int resource;
	
	private String info;
	
	public Personal(int resource, String info)
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
