package com.BC.entertainment.cache;

import java.util.ArrayList;
import java.util.List;

import com.BC.entertainmentgravitation.R;
import com.BC.entertainment.chatroom.extension.BaseEmotion;
import com.BC.entertainment.chatroom.extension.CustomAttachmentType;
import com.BC.entertainment.chatroom.gift.BaseGift;
import com.BC.entertainment.chatroom.gift.Gift;
import com.BC.entertainment.chatroom.gift.GiftCategory;

public class GiftCache {
	
	public List<BaseGift> gifts;
	
    public static GiftCache getInstance() {
        return InstanceHolder.instance;
    }
    
    static class InstanceHolder {
        final static GiftCache instance = new GiftCache();
    }
    
    public BaseEmotion GetEmotion(int type, int category)
    {
    	for(int i=0; i<gifts.size(); i++)
    	{
    		if(gifts.get(i).getCategory() == category)
    		{
    			return gifts.get(i).getBaseEmotion();
    		}
    	}
    	return null;
    }
	
	//创建礼物列表
	public List<BaseGift> getListGifts()
	{
		gifts = new ArrayList<BaseGift>();
		//一下是测试数据
		BaseGift gift1 = new Gift(CustomAttachmentType.font, GiftCategory.font_bxjj, "悲喜交加", R.drawable.emotion_bxjj, 1, 10);
		gifts.add(gift1);
		BaseGift gift2 = new Gift(CustomAttachmentType.font, GiftCategory.font_cygs, "察言观色", R.drawable.emotion_cygs, 2, 20);
		gifts.add(gift2);
		BaseGift gift3 = new Gift(CustomAttachmentType.font, GiftCategory.font_djxg, "大惊小怪", R.drawable.emotion_djxg, 3, 30);
		gifts.add(gift3);
		
		BaseGift gift4 = new Gift(CustomAttachmentType.font, GiftCategory.font_fxlp, "浮想联翩", R.drawable.emotion_fxlp, 4, 40);
		gifts.add(gift4);
		
		BaseGift gift5 = new Gift(CustomAttachmentType.font, GiftCategory.font_gjtl, "感激涕零", R.drawable.emotion_gjt, 5, 50);
		gifts.add(gift5);
		
		BaseGift gift6 = new Gift(CustomAttachmentType.font, GiftCategory.font_hqmm, "含情脉脉", R.drawable.emotion_hqmm, 6, 60);
		gifts.add(gift6);
		
		BaseGift gift7 = new Gift(CustomAttachmentType.font, GiftCategory.font_hsdd, "虎视眈眈", R.drawable.emotion_hsdd, 7, 70);
		gifts.add(gift7);
		
		BaseGift gift8 = new Gift(CustomAttachmentType.font, GiftCategory.font_jmny, "挤眉弄眼", R.drawable.emotion_jmny, 8, 80);
		gifts.add(gift8);
		
		BaseGift gift9 = new Gift(CustomAttachmentType.font, GiftCategory.font_kxbd, "哭笑不得", R.drawable.emotion_kxbd, 9, 90);
		gifts.add(gift9);
		
		BaseGift gift10 = new Gift(CustomAttachmentType.font, GiftCategory.font_lswz, "六神无主", R.drawable.emotion_lswz, 10, 100);
		gifts.add(gift10);
		BaseGift gift11 = new Gift(CustomAttachmentType.font, GiftCategory.font_mfsw, "眉飞色舞", R.drawable.emotion_mfsw, 11, 110);
		gifts.add(gift11);
		BaseGift gift12 = new Gift(CustomAttachmentType.font, GiftCategory.font_mgsr, "毛骨悚然", R.drawable.emotion_mgsr, 12, 120);
		gifts.add(gift12);
		BaseGift gift13 = new Gift(CustomAttachmentType.font, GiftCategory.font_mkyx, "眉开眼笑", R.drawable.emotion_mkyx, 13, 130);
		gifts.add(gift13);
		BaseGift gift14 = new Gift(CustomAttachmentType.font, GiftCategory.font_nmes, "怒目而视", R.drawable.emotion_nmes, 14, 140);
		gifts.add(gift14);
		BaseGift gift15 = new Gift(CustomAttachmentType.font, GiftCategory.font_pfdx, "捧腹大笑", R.drawable.emotion_pfdx, 15, 150);
		gifts.add(gift15);
		BaseGift gift16 = new Gift(CustomAttachmentType.font, GiftCategory.font_swzd, "手舞足蹈", R.drawable.emotion_swzd, 16, 160);
		gifts.add(gift16);

		BaseGift gift17 = new Gift(CustomAttachmentType.font, GiftCategory.font_xhnf, "心花怒放", R.drawable.emotion_xhnf, 17, 170);
		gifts.add(gift17);
		BaseGift gift18 = new Gift(CustomAttachmentType.font, GiftCategory.font_xrdj, "心如刀绞", R.drawable.emotion_xrdj, 18, 180);
		gifts.add(gift18);
		BaseGift gift19 = new Gift(CustomAttachmentType.font, GiftCategory.font_xxrk, "欣喜若狂", R.drawable.emotion_xxrk, 19, 190);
		gifts.add(gift19);
		
		BaseGift gift20 = new Gift(CustomAttachmentType.emotion, GiftCategory.emotion_mic, "金话筒", R.drawable.emotion_mic, 20, 200);
		gifts.add(gift20);
		
		BaseGift gift21 = new Gift(CustomAttachmentType.emotion, GiftCategory.emotion_car, "保姆车", R.drawable.emotion_car, 21, 210);
		gifts.add(gift21);
		return gifts;
	}

}
