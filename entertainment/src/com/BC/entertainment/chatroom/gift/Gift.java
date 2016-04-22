package com.BC.entertainment.chatroom.gift;

@SuppressWarnings("serial")
public class Gift extends BaseGift{

	/**
	 * 具体的礼物对象
	 * @param name 礼物名称
	 * @param iconResId 礼物图片资源
	 * @param value 价值
	 * @param exPoints 经验值
	 */
	public Gift(String name, int iconResId, int value, int exPoints) {
		super(name, iconResId, value, exPoints);
	}

	@Override
	public void onClick() {
		
	}

}
