package com.BC.entertainmentgravitation.entity;

import java.io.Serializable;

/**
 * 明星创建的直播频道相关信息
 * @author zhongwen
 *
 */
@SuppressWarnings("serial")
public class StarLiveVideoInfo implements Serializable{

	private String username;
	
	/**
	 * 直播频道ID
	 */
	private String cid;
	
	/**
	 * 推流地址
	 */
	private String pushUrl;
	
	/**
	 * 拉流三个地址
	 */
	private String httpPullUrl;
	
	private String hlsPullUrl;
	
	private String rtmpPullUrl;
	
	/**
	 * 聊天室ID
	 */
	private String chatroomid;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getPushUrl() {
		return pushUrl;
	}

	public void setPushUrl(String pushUrl) {
		this.pushUrl = pushUrl;
	}

	public String getHttpPullUrl() {
		return httpPullUrl;
	}

	public void setHttpPullUrl(String httpPullUrl) {
		this.httpPullUrl = httpPullUrl;
	}

	public String getHlsPullUrl() {
		return hlsPullUrl;
	}

	public void setHlsPullUrl(String hlsPullUrl) {
		this.hlsPullUrl = hlsPullUrl;
	}

	public String getRtmpPullUrl() {
		return rtmpPullUrl;
	}

	public void setRtmpPullUrl(String rtmpPullUrl) {
		this.rtmpPullUrl = rtmpPullUrl;
	}

	public String getChatroomid() {
		return chatroomid;
	}

	public void setChatroomid(String chatroomid) {
		this.chatroomid = chatroomid;
	}
	
	
}
