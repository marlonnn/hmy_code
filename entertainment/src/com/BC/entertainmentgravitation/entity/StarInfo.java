package com.BC.entertainmentgravitation.entity;

/**
 * 首页列表信息
 * @author wen zhong
 *
 */
public class StarInfo {
	
    private String Star_ID; //明星ID
    private String Head_portrait;//头像
    private String portrait;//写真照
    private String First_album;//写真首页照
    private String Stage_name;//昵称
    private String professional;//职业
    private String The_constellation;//所在省份
    private String bid;//当前指数
    private String starbonus;//持有红包
    private KLink k_graph_point;//"k线数组",{id,user_id,price,date_time,}(序列号，明星ID,价格，时间)
	private String difference;//昨日涨跌
	private String httpPullUrl;	//http 拉流地址
	private String hlsPullUrl;	//hls 拉流地址
	private String rtmpPullUrl;	//rtm 拉流地址
	private String rtmp;
	private String chatroomid;//聊天室id
	private String vstatus;//直播状态，0代表直播，1代表非直播
	private String peoples;//直播间人数
	
	public String getStar_ID() {
		return Star_ID;
	}
	public void setStar_ID(String star_ID) {
		Star_ID = star_ID;
	}
	public String getHead_portrait() {
		return Head_portrait;
	}
	public void setHead_portrait(String head_portrait) {
		Head_portrait = head_portrait;
	}
	public String getPortrait() {
		return portrait;
	}
	public void setPortrait(String portrait) {
		this.portrait = portrait;
	}
	public String getFirst_album() {
		return First_album;
	}
	public void setFirst_album(String first_album) {
		First_album = first_album;
	}
	public String getStage_name() {
		return Stage_name;
	}
	public void setStage_name(String stage_name) {
		Stage_name = stage_name;
	}
	public String getProfessional() {
		return professional;
	}
	public void setProfessional(String professional) {
		this.professional = professional;
	}
	public String getThe_constellation() {
		return The_constellation;
	}
	public void setThe_constellation(String the_constellation) {
		The_constellation = the_constellation;
	}
	public String getBid() {
		return bid;
	}
	public void setBid(String bid) {
		this.bid = bid;
	}
	public String getStarbonus() {
		return starbonus;
	}
	public void setStarbonus(String starbonus) {
		this.starbonus = starbonus;
	}
	public KLink getK_graph_point() {
		return k_graph_point;
	}
	public void setK_graph_point(KLink k_graph_point) {
		this.k_graph_point = k_graph_point;
	}
	public String getDifference() {
		return difference;
	}
	public void setDifference(String difference) {
		this.difference = difference;
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
	public String getRtmp() {
		return rtmp;
	}
	public void setRtmp(String rtmp) {
		this.rtmp = rtmp;
	}
	public String getChatroomid() {
		return chatroomid;
	}
	public void setChatroomid(String chatroomid) {
		this.chatroomid = chatroomid;
	}
	public String getVstatus() {
		return vstatus;
	}
	public void setVstatus(String vstatus) {
		this.vstatus = vstatus;
	}
	public String getPeoples() {
		return peoples;
	}
	public void setPeoples(String peoples) {
		this.peoples = peoples;
	}
}
