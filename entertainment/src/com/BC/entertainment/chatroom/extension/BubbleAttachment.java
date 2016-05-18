package com.BC.entertainment.chatroom.extension;

import com.BC.entertainment.cache.BubbleCache;
import com.alibaba.fastjson.JSONObject;
import com.summer.logger.XLog;

@SuppressWarnings("serial")
public class BubbleAttachment extends CustomAttachment {

	private Bubble bubble;
	
	public BubbleAttachment(int type) {
		super(type);
	}
	
	public BubbleAttachment(int type, Bubble bubble) {
		super(type);
		this.bubble = bubble;
	}
	
	@Override
	protected void parseData(JSONObject data) {
		bubble = BubbleCache.getInstance().GetBubble(type, data.getIntValue("category"));
		bubble.setFirstSend(data.getBooleanValue("isFirstSend"));
		try {
			XLog.i("bubble category: " + data.getIntValue("category"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected JSONObject packData() {
        JSONObject data = new JSONObject();
        data.put("category", bubble.getCategory());
        data.put("isFirstSend", bubble.isFirstSend());
        return data;
	}
	
	public Bubble getBubble()
	{
		return bubble;
	}
}
