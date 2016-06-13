package com.BC.entertainment.cache;

import java.util.ArrayList;
import java.util.List;

import com.BC.entertainmentgravitation.entity.EditPersonal;
import com.BC.entertainmentgravitation.entity.FHNEntity;
import com.BC.entertainmentgravitation.entity.StarInformation;

/**
 * 存放获取的明星、个人信息
 * @author wen zhong
 *
 */
public class InfoCache {
	
	/**
	 * 明星信息
	 */
	private StarInformation startInfo;
	
	private List<StarInformation> starInfoList;
	
	/**
	 * 个人信息
	 */
	private EditPersonal personalInfo;
	
	/**
	 * 当前正在直播的用户
	 */
	private StarInformation liveStar;

    public static InfoCache getInstance() {
        return InstanceHolder.instance;
    }
    
    static class InstanceHolder {
        final static InfoCache instance = new InfoCache();
    }

	public StarInformation CreateStartInfoInstance()
    {
    	if (startInfo == null)
    	{
    		startInfo = new StarInformation();
    	}
    	return startInfo;
    }
	
	public List<StarInformation> CreateStarInfoList()
	{
		if (starInfoList == null)
		{
			starInfoList = new ArrayList<StarInformation>();
		}
		return starInfoList;
	}
	
	public List<StarInformation> getStarInfoList() {
		return starInfoList;
	}


	public void setStarInfoList(List<StarInformation> starInfoList) {
		this.starInfoList = starInfoList;
	}

	public EditPersonal CreateEditPersonalInstance()
	{
	    if (personalInfo == null)
		{
			personalInfo = new EditPersonal();
		}
		return personalInfo;
	}
	
	public void AddToStarInfoList(StarInformation starInfo)
	{
		CreateStarInfoList();
		for (int i=0; i<starInfoList.size(); i++)
		{
			if(!starInfoList.get(i).getStar_ID().contains(starInfo.getStar_ID()))
			{
				starInfoList.add(starInfo);
			}
		}
	}
	
	public StarInformation GetStarInfo(StarInformation starInfo)
	{
		CreateStarInfoList();
		for (int i=0; i<starInfoList.size(); i++)
		{
			if(starInfoList.get(i).getUser_name().contains(starInfo.getUser_name()))
			{
				return starInfoList.get(i);
			}
		}
		return null;
	}
	
	public StarInformation GetStarInfo(String userName)
	{
		CreateStarInfoList();
		for (int i=0; i<starInfoList.size(); i++)
		{
			if(starInfoList.get(i).getUser_name().contains(userName))
			{
				return starInfoList.get(i);
			}
		}
		return null;
	}
	
	public StarInformation GetStarInfoById(String clientId)
	{
		CreateStarInfoList();
		for (int i=0; i<starInfoList.size(); i++)
		{
			if(starInfoList.get(i).getUser_name().contains(clientId))
			{
				return starInfoList.get(i);
			}
		}
		return null;
	}
	
    public StarInformation getStartInfo() {
		return startInfo;
	}

	public void setStartInfo(StarInformation startInfo) {
		this.startInfo = startInfo;
	}

	public EditPersonal getPersonalInfo() {
		return personalInfo;
	}

	public void setPersonalInfo(EditPersonal personalInfo) {
		this.personalInfo = personalInfo;
	}
	
	public StarInformation getLiveStar() {
		return liveStar;
	}

	public void setLiveStar(StarInformation liveStar) {
		this.liveStar = liveStar;
	}
	
	public void setLiveStar(FHNEntity entity)
	{
		if (liveStar == null)
		{
			liveStar = new StarInformation();
		}
		if (entity != null)
		{
			liveStar.setStar_ID(entity.getStar_ID());
			liveStar.setUser_name(entity.getUsername());
			liveStar.setProfessional(entity.getCareer());
			liveStar.setHead_portrait(entity.getHead_portrait());
			liveStar.setStage_name(entity.getStar_names());
			liveStar.setThe_current_hooted_thumb_up_prices(Integer.parseInt(entity.getBid()));
		}
	}
	
	public void ClearAllData()
	{
		startInfo = null;
		personalInfo = null;
		liveStar = null;
	}
}
