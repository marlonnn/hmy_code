package com.BC.entertainment.cache;

import java.util.ArrayList;
import java.util.List;

import com.BC.entertainmentgravitation.R;
import com.BC.entertainmentgravitation.entity.Found;

public class FoundCache {

	private List<Found> founds;
	
    public static FoundCache getInstance() {
        return InstanceHolder.instance;
    }
    
    static class InstanceHolder {
        final static FoundCache instance = new FoundCache();
    }
    
    public List<Found> GetFounds()
    {
    	founds = new ArrayList<Found>();
		
    	Found p1 = new Found(R.drawable.activity_found_live, "剧组信息");
		founds.add(p1);
		
		Found p2 = new Found(R.drawable.activity_found_message, "消息中心");
		founds.add(p2);
		
		Found p3 = new Found(R.drawable.activity_rights_center, "权益中心");
		founds.add(p3);
		
    	return founds;
    }
}
