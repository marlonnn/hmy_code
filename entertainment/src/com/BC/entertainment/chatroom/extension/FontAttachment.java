package com.BC.entertainment.chatroom.extension;

import com.BC.entertainment.cache.GiftCache;
import com.alibaba.fastjson.JSONObject;

/**
 * custom attachment of font 
 * @author wen zhong
 *
 */
@SuppressWarnings("serial")
public class FontAttachment extends CustomAttachment{

	private BaseEmotion emotion;
	
	public FontAttachment(int type) {
		super(type);
	}
	
	public FontAttachment(int type, BaseEmotion emotion) {
		super(type);
		this.emotion = emotion;
	}

	@Override
	protected void parseData(JSONObject data) {
		emotion = GiftCache.getInstance().GetEmotion(type, data.getIntValue("category"));
	}

	@Override
	protected JSONObject packData() {
        JSONObject data = new JSONObject();
        data.put("category", emotion.getCategory());
        data.put("name", emotion.getName());
        data.put("value", emotion.getValue());
        data.put("exPoints", emotion.getExPoints());
//        data.put("frequency", emotion.getFrequency());
        return data;
	}
	
	public BaseEmotion getEmotion()
	{
		return emotion;
	}

}
