package com.BC.entertainment.chatroom.gift;

import java.io.Serializable;

import android.app.Activity;

import com.BC.entertainment.chatroom.extension.BaseEmotion;
import com.BC.entertainment.chatroom.extension.CustomAttachmentType;
import com.BC.entertainment.chatroom.extension.Emotion;
import com.BC.entertainment.chatroom.extension.Font;
import com.BC.entertainment.chatroom.module.Container;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

@SuppressWarnings("serial")
public abstract class BaseGift implements Serializable{
	
	protected BaseEmotion baseEmotion;
	
	protected int customAttachmentType;
	
	protected int category;//emotion kind
	
	protected String name;
	
	protected int iconResId;//图片资源
	
	protected int value;//价值
	
	protected int exPoints;//可以获取到的经验值
	
	private transient Container container;
	
	public BaseEmotion getBaseEmotion() {
		return baseEmotion;
	}

	protected void setBaseEmotion(BaseEmotion baseEmotion) {
		this.baseEmotion = baseEmotion;
	}

	public int getCustomAttachmentType() {
		return customAttachmentType;
	}

	public void setCustomAttachmentType(int customAttachmentType) {
		this.customAttachmentType = customAttachmentType;
	}

	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	protected BaseGift(int customAttachmentType, int category, String name, int iconResId, int value, int exPoints)
	{
		this.customAttachmentType = customAttachmentType;
		this.category = category;
		this.name = name;
		this.iconResId = iconResId;
		this.value = value;
		this.exPoints = exPoints;
		switch(customAttachmentType)
		{
		case CustomAttachmentType.emotion:
			this.baseEmotion = new Emotion(category, name, value, exPoints);
			break;
		case CustomAttachmentType.font:
			this.baseEmotion = new Font(category, name, value, exPoints);
			break;
		}
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
    
    public void setContainer(Container container) {
        this.container = container;
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
	
    protected void sendMessage(IMMessage message) {
    	if(container != null && container.proxy != null)
    	{
            container.proxy.sendMessage(message);
    	}
    }
    
    protected void showAnimation(Gift gift)
    {
    	if(container != null && container.proxy != null)
    	{
            container.proxy.showAnimation(gift);
    	}
    }
}
