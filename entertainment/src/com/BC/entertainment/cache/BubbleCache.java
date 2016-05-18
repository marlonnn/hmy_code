package com.BC.entertainment.cache;

import java.util.ArrayList;
import java.util.List;

import com.BC.entertainment.chatroom.extension.Bubble;
import com.BC.entertainment.chatroom.gift.BubbleCategory;

public class BubbleCache {
	
	private List<Bubble> bubbles;
	
    public static BubbleCache getInstance() {
        return InstanceHolder.instance;
    }
    
    static class InstanceHolder {
        final static BubbleCache instance = new BubbleCache();
    }
    
    public Bubble GetBubble(int type, int category)
    {
    	if(bubbles == null)
    	{
    		bubbles = Bubbles();
    	}
    	for(int i=0; i<bubbles.size(); i++)
    	{
    		if(bubbles.get(i).getCategory() == category)
    		{
    			return bubbles.get(i);
    		}
    	}
    	return null;
    }
    
    public List<Bubble> Bubbles()
    {
    	bubbles = new ArrayList<>();
    	Bubble b1 = new Bubble(BubbleCategory.bubble_1, true);
    	bubbles.add(b1);
    	
    	Bubble b2 = new Bubble(BubbleCategory.bubble_2, true);
    	bubbles.add(b2);
    	
    	Bubble b3 = new Bubble(BubbleCategory.bubble_3, true);
    	bubbles.add(b3);
    	
    	Bubble b4 = new Bubble(BubbleCategory.bubble_4, true);
    	bubbles.add(b4);
    	
    	Bubble b5 = new Bubble(BubbleCategory.bubble_5, true);
    	bubbles.add(b5);
    	
    	Bubble b6 = new Bubble(BubbleCategory.bubble_6, true);
    	bubbles.add(b6);
    	
    	Bubble b7 = new Bubble(BubbleCategory.bubble_7, true);
    	bubbles.add(b7);
    	
    	Bubble b8 = new Bubble(BubbleCategory.bubble_8, true);
    	bubbles.add(b8);
    	
    	Bubble b9 = new Bubble(BubbleCategory.bubble_9, true);
    	bubbles.add(b9);
    	return bubbles;
    }
}
