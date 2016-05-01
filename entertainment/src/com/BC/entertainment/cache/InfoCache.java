package com.BC.entertainment.cache;

import java.util.ArrayList;
import java.util.List;

import com.BC.entertainmentgravitation.entity.EditPersonal;
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
	
	public List<StarInformation> CreateStarInfoList()
	{
		if (starInfoList == null)
		{
			starInfoList = new ArrayList<StarInformation>();
		}
		return starInfoList;
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
	
	/**
	 * 个人信息
	 */
	private EditPersonal personalInfo;

    public static InfoCache getInstance() {
        return InstanceHolder.instance;
    }
    
    static class InstanceHolder {
        final static InfoCache instance = new InfoCache();
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

	public StarInformation CreateStartInfoInstance()
    {
    	if (startInfo == null)
    	{
    		startInfo = new StarInformation();
    	}
    	return startInfo;
    }
	
	public EditPersonal CreateEditPersonalInstance()
	{
	    if (personalInfo == null)
		{
			personalInfo = new EditPersonal();
		}
		return personalInfo;
	}
	
	public void ClearAllData()
	{
		startInfo = null;
		personalInfo = null;
	}
}
