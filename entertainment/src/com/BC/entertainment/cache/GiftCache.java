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
		BaseGift gift1 = new Gift(CustomAttachmentType.font, GiftCategory.font_bxjj, "悲喜交加", R.drawable.emotion_bxjj, 600, 6000);
		gifts.add(gift1);
		BaseGift gift2 = new Gift(CustomAttachmentType.font, GiftCategory.font_cygs, "察言观色", R.drawable.emotion_cygs, 600, 6000);
		gifts.add(gift2);
		BaseGift gift3 = new Gift(CustomAttachmentType.font, GiftCategory.font_djxg, "大惊小怪", R.drawable.emotion_djxg, 600, 6000);
		gifts.add(gift3);
		
		BaseGift gift4 = new Gift(CustomAttachmentType.font, GiftCategory.font_fxlp, "浮想联翩", R.drawable.emotion_fxlp, 600, 6000);
		gifts.add(gift4);
		
		BaseGift gift5 = new Gift(CustomAttachmentType.font, GiftCategory.font_gjtl, "感激涕零", R.drawable.emotion_gjt, 600, 6000);
		gifts.add(gift5);
		
		BaseGift gift6 = new Gift(CustomAttachmentType.font, GiftCategory.font_hqmm, "含情脉脉", R.drawable.emotion_hqmm, 600, 6000);
		gifts.add(gift6);
		
		BaseGift gift7 = new Gift(CustomAttachmentType.font, GiftCategory.font_hsdd, "虎视眈眈", R.drawable.emotion_hsdd, 600, 6000);
		gifts.add(gift7);
		
		BaseGift gift8 = new Gift(CustomAttachmentType.font, GiftCategory.font_jmny, "挤眉弄眼", R.drawable.emotion_jmny, 600, 6000);
		gifts.add(gift8);
		
		BaseGift gift9 = new Gift(CustomAttachmentType.font, GiftCategory.font_kxbd, "哭笑不得", R.drawable.emotion_kxbd, 600, 6000);
		gifts.add(gift9);
		
		BaseGift gift10 = new Gift(CustomAttachmentType.font, GiftCategory.font_lswz, "六神无主", R.drawable.emotion_lswz, 600, 6000);
		gifts.add(gift10);
		BaseGift gift11 = new Gift(CustomAttachmentType.font, GiftCategory.font_mfsw, "眉飞色舞", R.drawable.emotion_mfsw, 600, 6000);
		gifts.add(gift11);
		BaseGift gift12 = new Gift(CustomAttachmentType.font, GiftCategory.font_mgsr, "毛骨悚然", R.drawable.emotion_mgsr, 600, 6000);
		gifts.add(gift12);
		BaseGift gift13 = new Gift(CustomAttachmentType.font, GiftCategory.font_mkyx, "眉开眼笑", R.drawable.emotion_mkyx, 600, 6000);
		gifts.add(gift13);
		BaseGift gift14 = new Gift(CustomAttachmentType.font, GiftCategory.font_nmes, "怒目而视", R.drawable.emotion_nmes, 600, 6000);
		gifts.add(gift14);
		BaseGift gift15 = new Gift(CustomAttachmentType.font, GiftCategory.font_pfdx, "捧腹大笑", R.drawable.emotion_pfdx, 600, 6000);
		gifts.add(gift15);
		BaseGift gift16 = new Gift(CustomAttachmentType.font, GiftCategory.font_swzd, "手舞足蹈", R.drawable.emotion_swzd, 600, 6000);
		gifts.add(gift16);

		BaseGift gift17 = new Gift(CustomAttachmentType.font, GiftCategory.font_xhnf, "心花怒放", R.drawable.emotion_xhnf, 600, 6000);
		gifts.add(gift17);
		BaseGift gift18 = new Gift(CustomAttachmentType.font, GiftCategory.font_xrdj, "心如刀绞", R.drawable.emotion_xrdj, 600, 6000);
		gifts.add(gift18);
		BaseGift gift19 = new Gift(CustomAttachmentType.font, GiftCategory.font_xxrk, "欣喜若狂", R.drawable.emotion_xxrk, 600, 6000);
		gifts.add(gift19);
		
		BaseGift gift20 = new Gift(CustomAttachmentType.emotion, GiftCategory.emotion_rose, "一枝鲜花", R.drawable.emotion_rose, 10, 100);
		gifts.add(gift20);
		
		BaseGift gift21 = new Gift(CustomAttachmentType.emotion, GiftCategory.emotion_flowers, "一捧鲜花", R.drawable.emotion_flowers, 100, 1000);
		gifts.add(gift21);
		
		BaseGift gift22 = new Gift(CustomAttachmentType.emotion, GiftCategory.emotion_touch, "摸摸", R.drawable.emotion_touch, 500, 5000);
		gifts.add(gift22);
		
		BaseGift gift23 = new Gift(CustomAttachmentType.emotion, GiftCategory.emotion_kiss, "亲亲", R.drawable.emotion_kiss, 1000, 10000);
		gifts.add(gift23);
		
		BaseGift gift24 = new Gift(CustomAttachmentType.emotion, GiftCategory.emotion_shoes, "红舞鞋", R.drawable.emotion_shoes, 16000, 160000);
		gifts.add(gift24);
		
		BaseGift gift25 = new Gift(CustomAttachmentType.emotion, GiftCategory.emotion_mic, "金话筒", R.drawable.emotion_mic, 16000, 160000);
		gifts.add(gift25);
		
		BaseGift gift26 = new Gift(CustomAttachmentType.emotion, GiftCategory.emotion_car, "保姆车", R.drawable.emotion_car, 66666, 666666);
		gifts.add(gift26);
		
		BaseGift gift27 = new Gift(CustomAttachmentType.emotion, GiftCategory.emotion_plane, "私人飞机", R.drawable.emotion_plane, 88888, 888888);
		gifts.add(gift27);
		return gifts;
	}

}
