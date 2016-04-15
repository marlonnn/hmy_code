package com.BC.entertainmentgravitation.entity;

public class ChatRoom {

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
	
//	private String hlsPullUrl;
//	
//	private String rtmpPullUrl;
	
	/**
	 * 聊天室ID
	 */
	private String chatroomid;
	
	private boolean filter;

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

//	public String getHlsPullUrl() {
//		return hlsPullUrl;
//	}
//
//	public void setHlsPullUrl(String hlsPullUrl) {
//		this.hlsPullUrl = hlsPullUrl;
//	}
//
//	public String getRtmpPullUrl() {
//		return rtmpPullUrl;
//	}
//
//	public void setRtmpPullUrl(String rtmpPullUrl) {
//		this.rtmpPullUrl = rtmpPullUrl;
//	}

	public String getChatroomid() {
		return chatroomid;
	}

	public void setChatroomid(String chatroomid) {
		this.chatroomid = chatroomid;
	}

	public boolean isFilter() {
		return filter;
	}

	public void setFilter(boolean filter) {
		this.filter = filter;
	}
	
}
