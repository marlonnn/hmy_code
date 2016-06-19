package com.BC.entertainment.cache;

import java.util.ArrayList;
import java.util.List;

import com.BC.entertainmentgravitation.R;

import com.BC.entertainmentgravitation.entity.Personal;

/**
 * 个人信息列表框数据缓存
 * @author zhongwen 2016/4/29
 *
 */
public class PersonalCache {

	private List<Personal> personals;
	
    public static PersonalCache getInstance() {
        return InstanceHolder.instance;
    }
    
    static class InstanceHolder {
        final static PersonalCache instance = new PersonalCache();
    }
    
    public List<Personal> GetPersonalInfos()
    {
		personals = new ArrayList<Personal>();
		
		Personal p1 = new Personal(R.drawable.activity_personal_info, "基本信息");
		personals.add(p1);
		
		Personal p2 = new Personal(R.drawable.activity_personal_album, "相册管理");
		personals.add(p2);
		
		Personal p3 = new Personal(R.drawable.activity_personal_career, "演艺经历");
		personals.add(p3);
		
		Personal p4 = new Personal(R.drawable.activity_personal_broker, "我的经纪");
		personals.add(p4);
		
		Personal p5 = new Personal(R.drawable.activity_modify, "我的签到");
		personals.add(p5);		
		
		Personal p6 = new Personal(R.drawable.activity_personal_income, "我的收益");
		personals.add(p6);
		
		Personal p7 = new Personal(R.drawable.activity_personal_yubi, "我的娛币");
		personals.add(p7);
		
		Personal p8 = new Personal(R.drawable.activity_personal_envelope, "我的红包");
		personals.add(p8);
		
		Personal p9 = new Personal(R.drawable.activity_home_bottom_focus, "我的关注");
		personals.add(p9);
		
		Personal p11 = new Personal(R.drawable.activity_authenticate, "我的认证");
		personals.add(p11);
		
		Personal p12 = new Personal(R.drawable.activity_personal_feedback, "意见反馈");
		personals.add(p12);
		
		Personal p13 = new Personal(R.drawable.activity_personal_about, "关于我们");
		personals.add(p13);
    	return personals;
    }
    
}
