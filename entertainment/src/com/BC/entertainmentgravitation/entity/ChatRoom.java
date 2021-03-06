package com.BC.entertainmentgravitation.entity;

import com.netease.nimlib.sdk.chatroom.model.ChatRoomInfo;

public class ChatRoom {

	private String cid;
	
	private String pushUrl;
	
	private String httpPullUrl;
	
//	private String hlsPullUrl;
//	
//	private String rtmpPullUrl;
	
	private String chatroomid;
	
	private boolean filter;
	
	private boolean isMaster;//是否是主播
	
	/**
	 * enterChatRoom成功之后回调的聊天室信息，里面包含了聊天室总人数、聊天室创建者等相关信息
	 */
	private ChatRoomInfo chatRoomInfo;

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

	public boolean isMaster() {
		return isMaster;
	}

	public void setMaster(boolean isMaster) {
		this.isMaster = isMaster;
	}

	public ChatRoomInfo getChatRoomInfo() {
		return chatRoomInfo;
	}

	public void setChatRoomInfo(ChatRoomInfo chatRoomInfo) {
		this.chatRoomInfo = chatRoomInfo;
	}
	
}
