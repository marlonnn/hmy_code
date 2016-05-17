package com.BC.entertainment.chatroom.extension;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Bubble implements Serializable{
	
	private int category;//emotion kind
	private int customAttachmentType;
	
	public Bubble(int category)
	{
		this.category = category;
		this.customAttachmentType = CustomAttachmentType.bubble;
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
}
