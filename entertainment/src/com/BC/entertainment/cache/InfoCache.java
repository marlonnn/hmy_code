package com.BC.entertainment.cache;

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
