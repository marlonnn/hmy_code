package com.BC.entertainment.chatroom.extension;

import com.alibaba.fastjson.JSONObject;

/**
 * custom attachment of font 
 * @author wen zhong
 *
 */
@SuppressWarnings("serial")
public class FontAttachment extends CustomAttachment{

	private Font font;
	
	public FontAttachment(int type) {
		super(type);
	}
	
	public FontAttachment(int type, Font font) {
		super(type);
		this.font = font;
	}

	@Override
	protected void parseData(JSONObject data) {
		font = Font.enumOfCategory(data.getIntValue("category"));
	}

	@Override
	protected JSONObject packData() {
        JSONObject data = new JSONObject();
        data.put("category", font.getCategory());
        return data;
	}
	
	public Font getFont()
	{
		return font;
	}

}
