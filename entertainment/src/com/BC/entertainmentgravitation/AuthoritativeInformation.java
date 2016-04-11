package com.BC.entertainmentgravitation;

import java.util.List;

import com.BC.entertainmentgravitation.entity.Activitys;
import com.BC.entertainmentgravitation.entity.Advertising;

public class AuthoritativeInformation {
	private List<Activitys> activity;

	private List<Advertising> advertising;

	public void setActivity(List<Activitys> activity) {
		this.activity = activity;
	}

	public List<Activitys> getActivity() {
		return this.activity;
	}

	public void setAdvertising(List<Advertising> advertising) {
		this.advertising = advertising;
	}

	public List<Advertising> getAdvertising() {
		return this.advertising;
	}
}
