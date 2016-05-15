package com.BC.entertainment.cache;

import java.util.ArrayList;
import java.util.List;

import com.BC.entertainmentgravitation.entity.Ranking;

/**
 * 明星列表缓存
 * @author zhongwen
 *
 */
public class StarCache {
	
	private List<Ranking> ranking = new ArrayList<Ranking>();

    public static StarCache getInstance() {
        return InstanceHolder.instance;
    }
    
    static class InstanceHolder {
        final static StarCache instance = new StarCache();
    }

	public List<Ranking> getRanking() {
		return ranking;
	}

	public void setRanking(List<Ranking> ranking) {
		if (ranking != null)
		{
			this.ranking .clear();
			this.ranking = ranking;
		}
	}
	
	public void clear()
	{
		this.ranking.clear();
	}
    
}
