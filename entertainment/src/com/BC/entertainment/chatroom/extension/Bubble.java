package com.BC.entertainment.chatroom.extension;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Bubble implements Serializable{
	
	private int category;//emotion kind
	private int customAttachmentType;
	private boolean isFirstSend;
	
	public Bubble(int category, boolean isFirstSend)
	{
		this.category = category;
		this.customAttachmentType = CustomAttachmentType.bubble;
		this.isFirstSend = isFirstSend;
	}

	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}

	public int getCustomAttachmentType() {
		return customAttachmentType;
	}

	public void setCustomAttachmentType(int customAttachmentType) {
		this.customAttachmentType = customAttachmentType;
	}

	public boolean isFirstSend() {
		return isFirstSend;
	}

	public void setFirstSend(boolean isFirstSend) {
		this.isFirstSend = isFirstSend;
	}
	
}
