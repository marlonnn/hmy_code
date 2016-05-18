package com.BC.entertainment.chatroom.module;

import com.BC.entertainment.cache.BubbleCache;
import com.BC.entertainment.chatroom.extension.Bubble;
import com.BC.entertainment.chatroom.extension.BubbleAttachment;
import com.BC.entertainment.chatroom.extension.CustomAttachmentType;
import com.netease.nimlib.sdk.chatroom.ChatRoomMessageBuilder;
import com.netease.nimlib.sdk.msg.model.IMMessage;

public class BubbingPanel {
	
	private Container container;

	public BubbingPanel(Container container)
	{
		this.container = container;
	}

	public void sendBubbling(boolean isFirst, int index)
	{
		Bubble b= BubbleCache.getInstance().GetBubble(CustomAttachmentType.bubble, index);
		b.setFirstSend(isFirst);
		BubbleAttachment bubbleAttachment = new BubbleAttachment(CustomAttachmentType.bubble, b);
		IMMessage message = ChatRoomMessageBuilder.createChatRoomCustomMessage(container.chatRoom.getChatroomid(), bubbleAttachment);
		if (container != null && container.proxy != null)
		{
			container.proxy.sendMessage(message);
		}
	}

}
