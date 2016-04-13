package com.BC.entertainmentgravitation.entity;

import java.util.List;

public class AllOutConnect {
	private List<OutConnect> Baidu;
	private List<OutConnect> QQspace;
	private List<OutConnect> sina;
	private List<OutConnect> video;
	private List<OutConnect> other;

	public List<OutConnect> getBaidu() {
		return Baidu;
	}

	public void setBaidu(List<OutConnect> baidu) {
		Baidu = baidu;
	}

	public List<OutConnect> getQQspace() {
		return QQspace;
	}

	public void setQQspace(List<OutConnect> qQspace) {
		QQspace = qQspace;
	}

	public List<OutConnect> getSina() {
		return sina;
	}

	public void setSina(List<OutConnect> sina) {
		this.sina = sina;
	}

	public List<OutConnect> getVideo() {
		return video;
	}

	public void setVideo(List<OutConnect> video) {
		this.video = video;
	}

	public List<OutConnect> getOther() {
		return other;
	}

	public void setOther(List<OutConnect> other) {
		this.other = other;
	}

}
