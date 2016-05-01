package com.BC.entertainmentgravitation.entity;

public class VideoStatus {

	private int needRecord;
	
	private int uid;
	
	private int duration;
	
	private String name;
	
	private String filename;
	
	private int format;
	
	private int type;
	
	private int ctime;
	
	private int status;
	
	private String cid;

	public int getNeedRecord() {
		return needRecord;
	}

	public void setNeedRecord(int needRecord) {
		this.needRecord = needRecord;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public int getFormat() {
		return format;
	}

	public void setFormat(int format) {
		this.format = format;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getCtime() {
		return ctime;
	}

	public void setCtime(int ctime) {
		this.ctime = ctime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}
	
}
