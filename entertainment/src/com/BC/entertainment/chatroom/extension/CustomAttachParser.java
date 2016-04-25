package com.BC.entertainment.chatroom.extension;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachmentParser;
import com.summer.logger.XLog;

@SuppressWarnings("serial")
public class CustomAttachParser implements MsgAttachmentParser{
	
    private static final String KEY_TYPE = "type";
    private static final String KEY_DATA = "data";

	@Override
	public MsgAttachment parse(String json) {
		CustomAttachment attachment = null;
		
        try {
			JSONObject object = JSON.parseObject(json);
			int type = object.getInteger(KEY_TYPE);
			JSONObject data = object.getJSONObject(KEY_DATA);
			switch (type)
			{
			case CustomAttachmentType.emotion:
				attachment = new EmotionAttachment(type);
				break;
			case CustomAttachmentType.font:
				attachment = new FontAttachment(type);
				break;
			}
			
            if (attachment != null) {
                attachment.fromJson(data);
            }
            
		} catch (Exception e) {
			e.printStackTrace();
			XLog.e("json string to object error");
		}
		return attachment;
	}

	
    public static String packData(int type, JSONObject data) {
        JSONObject object = new JSONObject();
        object.put(KEY_TYPE, type);
        if (data != null) {
            object.put(KEY_DATA, data);
        }

        return object.toJSONString();
    }
}
