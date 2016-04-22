package com.BC.entertainment.chatroom.module;

import java.util.ArrayList;
import java.util.List;

import com.BC.entertainmentgravitation.R;
import com.BC.entertainment.chatroom.gift.Gift;

public class GiftCache {
	
    public static GiftCache getInstance() {
        return InstanceHolder.instance;
    }
    
    static class InstanceHolder {
        final static GiftCache instance = new GiftCache();
    }
	
	//���������б�
	public List<Gift> getListGifts()
	{
		List<Gift> gifts = new ArrayList<Gift>();
		//һ���ǲ�������
		Gift gift1 = new Gift("һ֧õ��", R.drawable.avatar_def, 10, 100);
		gifts.add(gift1);
		Gift gift2 = new Gift("һ���ʻ�", R.drawable.avatar_def, 20, 200);
		gifts.add(gift2);
		Gift gift3 = new Gift("һ���ʻ�", R.drawable.avatar_def, 30, 300);
		gifts.add(gift3);
		Gift gift4 = new Gift("��������", R.drawable.avatar_def, 40, 400);
		gifts.add(gift4);
		Gift gift5 = new Gift("��Ц����", R.drawable.avatar_def, 50, 500);
		gifts.add(gift5);
		Gift gift6 = new Gift("��С��", R.drawable.avatar_def, 60, 600);
		gifts.add(gift6);
		Gift gift7 = new Gift("���Թ�ɫ", R.drawable.avatar_def, 70, 700);
		gifts.add(gift7);
		Gift gift8 = new Gift("���絶��", R.drawable.avatar_def, 80, 800);
		gifts.add(gift8);
		return gifts;
	}

}
