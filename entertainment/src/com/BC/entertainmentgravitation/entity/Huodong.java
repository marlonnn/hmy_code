package com.BC.entertainmentgravitation.entity;

import java.io.Serializable;


/**
 * 剧组活动
 * @author wen zhong
 *
 */
@SuppressWarnings("serial")
public class Huodong  implements Serializable {

	private String id;//活动的ID号
	
	private String type;//活动类型
	
	private String name;//活动名称
	
	private String add_time;//
	
	private String image;//活动Logo
	
	private String link;//活动链接
	
	private String status;//活动状态
	
	private String content;//活动内容

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAdd_time() {
		return add_time;
	}

	public void setAdd_time(String add_time) {
		this.add_time = add_time;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
