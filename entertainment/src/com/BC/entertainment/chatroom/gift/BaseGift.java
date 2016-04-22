package com.BC.entertainment.chatroom.gift;

import java.io.Serializable;

import android.app.Activity;

import com.netease.nim.uikit.session.module.Container;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;

@SuppressWarnings("serial")
public abstract class BaseGift implements Serializable{
	
	private String name;
	
	private int iconResId;//图片资源
	
	private int value;//价值
	
	private int exPoints;//可以获取到的经验值
	
	private transient Container container;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	protected BaseGift(String name, int iconResId, int value, int exPoints)
	{
		this.name = name;
		this.iconResId = iconResId;
		this.value = value;
		this.exPoints = exPoints;
	}
	
    public Activity getActivity() {
        return container.activity;
    }

    public String getAccount() {
        return container.account;
    }

    public SessionTypeEnum getSessionType() {
        return container.sessionType;
    }
    
    public Container getContainer() {
        return container;
    }
    
    public abstract void onClick();

	public int getIconResId() {
		return iconResId;
	}

	public void setIconResId(int iconResId) {
		this.iconResId = iconResId;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public int getExPoints() {
		return exPoints;
	}

	public void setExPoints(int exPoints) {
		this.exPoints = exPoints;
	}
	
}
