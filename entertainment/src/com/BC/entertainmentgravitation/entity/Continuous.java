package com.BC.entertainmentgravitation.entity;

import java.util.List;

public class Continuous {
	String continuous;

	List<SignTime> signs;

	public List<SignTime> getSigns() {
		return signs;
	}

	public void setSigns(List<SignTime> signs) {
		this.signs = signs;
	}

	public String getContinuous() {
		return continuous;
	}

	public void setContinuous(String continuous) {
		this.continuous = continuous;
	}
}
