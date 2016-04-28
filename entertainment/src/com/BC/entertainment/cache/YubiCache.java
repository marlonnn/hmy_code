package com.BC.entertainment.cache;

import java.util.ArrayList;
import java.util.List;

import com.BC.entertainmentgravitation.entity.Yubi;

public class YubiCache {

    public static YubiCache getInstance() {
        return InstanceHolder.instance;
    }
    
    static class InstanceHolder {
        final static YubiCache instance = new YubiCache();
    }
    
	public List<Yubi> GetYubiLists()
	{
		List<Yubi> yubis = new ArrayList<Yubi>();
		Yubi y1 = new Yubi(60, "6", 0);
		yubis.add(y1);
		
		Yubi y2 = new Yubi(220, "18", 40);
		yubis.add(y2);
		
		Yubi y3 = new Yubi(360, "30", 60);
		yubis.add(y3);
		
		Yubi y4 = new Yubi(540, "45", 90);
		yubis.add(y4);
		
		Yubi y5 = new Yubi(1200, "98", 400);
		yubis.add(y5);
		
		Yubi y6 = new Yubi(3800, "298", 820);
		yubis.add(y6);
		
		return yubis;
	}
}
