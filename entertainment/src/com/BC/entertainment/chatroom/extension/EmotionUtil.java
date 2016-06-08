package com.BC.entertainment.chatroom.extension;

import com.BC.entertainment.cache.InfoCache;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

public class EmotionUtil {
	
	/**
	 * 判断用户是否可以发送礼物 娱币余额
	 * @param message
	 * @return
	 */
    public static boolean CanSendCustomMessage(IMMessage message)
    {
    	boolean can = false;
    	
    	if (message != null)
    	{
    		try {
				
				if (message.getMsgType() == MsgTypeEnum.text)
				{
					can = true;
				}
				else if (message.getMsgType() == MsgTypeEnum.custom)
				{
					CustomAttachment customAttachment = (CustomAttachment)message.getAttachment();
					switch(customAttachment.getType())
					{
					/**
					 * 表情
					 */
					case CustomAttachmentType.emotion:
						EmotionAttachment emotionAttachment = (EmotionAttachment)customAttachment;
						if (emotionAttachment != null)
						{
							if (emotionAttachment.getEmotion().getValue() < Integer.parseInt(InfoCache.getInstance().getPersonalInfo().getEntertainment_dollar()))
							{
								can = true;
							}
						}
						break;
					/**
					 * 字体
					 */
					case CustomAttachmentType.font:
						FontAttachment fontAttachment = (FontAttachment)customAttachment;
						if (fontAttachment != null)
						{
							if (fontAttachment.getEmotion().getValue() < Integer.parseInt(InfoCache.getInstance().getPersonalInfo().getEntertainment_dollar()))
							{
								can = true;
							}
						}
						break;
					/**
					 * 气泡
					 */
					case CustomAttachmentType.bubble:
						//气泡暂时不需要娱币
						can = true;
						break;
					}
				}

			} catch (Exception e) {
				can = false;
				e.printStackTrace();
			}
    	}
    	return can;
    }

}
