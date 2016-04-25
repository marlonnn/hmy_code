package com.BC.entertainment.chatroom.extension;

import com.alibaba.fastjson.JSONObject;
import com.summer.logger.XLog;

/**
 * Custom attachment of emotion
 * @author wen zhong
 *
 */
@SuppressWarnings("serial")
public class EmotionAttachment extends CustomAttachment{
	
	private BaseEmotion emotion;
	
	public EmotionAttachment(int type) {
		super(type);
	}
	
	public EmotionAttachment(int type, BaseEmotion emotion) {
		super(type);
		this.emotion = emotion;
	}

	@Override
	protected void parseData(JSONObject data) {
		emotion = Emotion.enumOfCategory(data.getIntValue("category"));
		try {
			XLog.i("emotion category: " + data.getIntValue("category"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected JSONObject packData() {
        JSONObject data = new JSONObject();
        data.put("category", emotion.getCategory());
        return data;
	}
	
	public BaseEmotion getEmotion()
	{
		return emotion;
	}

}
