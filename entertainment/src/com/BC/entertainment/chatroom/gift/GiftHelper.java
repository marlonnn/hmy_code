package com.BC.entertainment.chatroom.gift;

import com.BC.entertainmentgravitation.R;

public class GiftHelper {

    public static int getDrawable(int category)
    {
    	int drawable;
    	switch(category)
    	{
    	case GiftCategory.font_bxjj:
    		drawable = R.drawable.animation_bxjj;
    		break;
    	case GiftCategory.font_cygs:
    		drawable = R.drawable.animation_cygs;
    		break;
    	case GiftCategory.font_djxg:
    		drawable = R.drawable.animation_djxg;
    		break;
    	case GiftCategory.font_fxlp:
    		drawable = R.drawable.animation_fxlp;
    		break;
    	case GiftCategory.font_gjtl:
    		drawable = R.drawable.animation_gjtl;
    		break;
    	case GiftCategory.font_hqmm:
    		drawable = R.drawable.animation_hqmm;
    		break;
    	case GiftCategory.font_hsdd:
    		drawable = R.drawable.animation_hsdd;
    		break;
    	case GiftCategory.font_jmny:
    		drawable = R.drawable.animation_jmny;
    		break;
    	case GiftCategory.font_kxbd:
    		drawable = R.drawable.animation_kxbd;
    		break;
    	case GiftCategory.font_lswz:
    		drawable = R.drawable.animation_lswz;
    		break;
    	case GiftCategory.font_mfsw:
    		drawable = R.drawable.animation_mfsw;
    		break;
    	case GiftCategory.font_mgsr:
    		drawable = R.drawable.animation_mgsr;
    		break;
    	case GiftCategory.font_mkyx:
    		drawable = R.drawable.animation_mkyx;
    		break;
    	case GiftCategory.font_nmes:
    		drawable = R.drawable.animation_nmes;
    		break;
    	case GiftCategory.font_pfdx:
    		drawable = R.drawable.animation_pfdx;
    		break;
    	case GiftCategory.font_swzd:
    		drawable = R.drawable.animation_swzd;
    		break;
    	case GiftCategory.font_xhnf:
    		drawable = R.drawable.animation_xhnf;
    		break;
    	case GiftCategory.font_xrdj:
    		drawable = R.drawable.animation_xrdj;
    		break;
    	case GiftCategory.font_xxrk:
    		drawable = R.drawable.animation_xxrk;
    		break;
    		
    	case GiftCategory.emotion_rose:
    		drawable = R.drawable.animation_rose;
    		break;
    	case GiftCategory.emotion_flowers:
    		drawable = R.drawable.animation_flowers;
    		break;
    	case GiftCategory.emotion_kiss:
    		drawable = R.drawable.animation_kiss;
    		break;
    	case GiftCategory.emotion_touch:
    		drawable = R.drawable.animation_touch;
    		break;
    	case GiftCategory.emotion_shoes:
    		drawable = R.drawable.animation_shoes;
    		break;
    	case GiftCategory.emotion_mic:
    		drawable = R.drawable.animation_mic;
    		break;
    	case GiftCategory.emotion_car:
    		drawable = R.drawable.animation_car;
    		break;
    	case GiftCategory.emotion_plane:
    		drawable = R.drawable.animation_plane;
    		break;
    		default:
    			drawable = R.drawable.animation_bxjj;
    			break;
    	}
    	return drawable;
    }
}
