package com.BC.entertainmentgravitation.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "tb_getui")
public class GeTui {

	@DatabaseField(generatedId = true)
	private int id;
	
	@DatabaseField(columnName = "messagetype")
	private String messagetype;
	
	@DatabaseField(columnName = "messageid")
	private String messageid;
	
	@DatabaseField(columnName = "messagetitle")
	private String messagetitle;
	
	@DatabaseField(columnName = "messagecontent")
	private String messagecontent;
	
	@DatabaseField(columnName = "time")
	private String time;
	
	@DatabaseField(columnName = "hasRead")
	private boolean hasRead = false;

	public String getMessagetype() {
		return messagetype;
	}

	public void setMessagetype(String messagetype) {
		this.messagetype = messagetype;
	}

	public String getMessageid() {
		return messageid;
	}

	public void setMessageid(String messageid) {
		this.messageid = messageid;
	}

	public String getMessagetitle() {
		return messagetitle;
	}

	public void setMessagetitle(String messagetitle) {
		this.messagetitle = messagetitle;
	}

	public String getMessagecontent() {
		return messagecontent;
	}

	public void setMessagecontent(String messagecontent) {
		this.messagecontent = messagecontent;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public boolean isHasRead() {
		return hasRead;
	}

	public void setHasRead(boolean hasRead) {
		this.hasRead = hasRead;
	}
	
}
