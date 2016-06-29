package com.BC.entertainment.chatroom.module;

import android.app.Activity;

import com.BC.entertainment.inter.ActivityCallback;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;

public class Container_new {

	private ActivityCallback activityCallback;
	private SessionTypeEnum sessionType;
	
	public Container_new (ActivityCallback activityCallback, SessionTypeEnum sessionType)
	{
		this.activityCallback = activityCallback;
		this.sessionType = sessionType;
	}
	public ActivityCallback getActivityCallback() {
		return activityCallback;
	}

	public void setActivityCallback(ActivityCallback activityCallback) {
		this.activityCallback = activityCallback;
	}

	public SessionTypeEnum getSessionType() {
		return sessionType;
	}

	public void setSessionType(SessionTypeEnum sessionType) {
		this.sessionType = sessionType;
	}
	
    public Activity getActivity() {
        return activityCallback.getActivity();
    }
}
