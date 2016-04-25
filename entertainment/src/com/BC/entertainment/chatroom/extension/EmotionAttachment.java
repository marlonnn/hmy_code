package com.BC.entertainment.chatroom.extension;

import com.alibaba.fastjson.JSONObject;

/**
 * Custom attachment of emotion
 * @author wen zhong
 *
 */
@SuppressWarnings("serial")
public class EmotionAttachment extends CustomAttachment{
	
	private Emotion emotion;
	
	public EmotionAttachment(int type) {
		super(type);
	}
	
	public EmotionAttachment(int type, Emotion emotion) {
		super(type);
		this.emotion = emotion;
	}

	@Override
	protected void parseData(JSONObject data) {
		emotion = Emotion.enumOfCategory(data.getIntValue("category"));
	}

	@Override
	protected JSONObject packData() {
        JSONObject data = new JSONObject();
        data.put("category", emotion.getCategory());
        return data;
	}
	
	public Emotion getEmotion()
	{
		return emotion;
	}

}
