package com.BC.entertainment.chatroom.gift;

import com.BC.entertainment.chatroom.extension.CustomAttachmentType;
import com.BC.entertainment.chatroom.extension.Emotion;
import com.BC.entertainment.chatroom.extension.EmotionAttachment;
import com.BC.entertainment.chatroom.extension.Font;
import com.BC.entertainment.chatroom.extension.FontAttachment;
import com.netease.nimlib.sdk.chatroom.ChatRoomMessageBuilder;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

@SuppressWarnings("serial")
public class Gift extends BaseGift{

	private IMMessage message;
	/**
	 * gift to be send to 
	 * 
	 * @param customAttachmentType Custom attachment type
	 * @param category category
	 * @param name gift name
	 * @param iconResId gift image resource id
	 * @param value gift value
	 * @param exPoints gift extra points
	 */
	public Gift(int customAttachmentType, int category, String name, int iconResId, int value, int exPoints) {
		super(customAttachmentType, category, name, iconResId, value, exPoints);
	}

	@Override
	public void onClick() {
		//1.send message to chat room
		//2.show animation at local
		switch(customAttachmentType)
		{
		case CustomAttachmentType.emotion:
			Emotion emotion = Emotion.enumOfCategory(category);
			EmotionAttachment emotionAttachment = new EmotionAttachment(customAttachmentType, emotion);
			if (getContainer() != null && getContainer().sessionType == SessionTypeEnum.ChatRoom)
			{
				message = ChatRoomMessageBuilder.createChatRoomCustomMessage(getAccount(), emotionAttachment);
			}
			else
			{
				 message = MessageBuilder.createCustomMessage(getAccount(), getSessionType(), emotionAttachment.getEmotion().getName(), emotionAttachment);
			}
			sendMessage(message);
//			showAnimation();
			break;
		case CustomAttachmentType.font:
			Font font = Font.enumOfCategory(category);
			FontAttachment fontAttachment = new FontAttachment(customAttachmentType, font);
			if (getContainer() != null && getContainer().sessionType == SessionTypeEnum.ChatRoom)
			{
				message = ChatRoomMessageBuilder.createChatRoomCustomMessage(getAccount(), fontAttachment);
			}
			else
			{
				 message = MessageBuilder.createCustomMessage(getAccount(), getSessionType(), fontAttachment.getFont().getName(), fontAttachment);
			}
			sendMessage(message);
			break;
		}
	}

}
