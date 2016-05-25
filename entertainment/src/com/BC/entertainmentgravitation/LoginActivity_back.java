package com.BC.entertainmentgravitation;

import android.content.Context;
import android.os.Bundle;

import com.summer.activity.BaseActivity;
import com.summer.factory.ThreadPoolFactory;
import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.MobclickAgent.EScenarioType;

public class LoginActivity_back extends BaseActivity{
	
    private Context mContext;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = this;
        MobclickAgent.setDebugMode(true);
        // SDK在统计Fragment时，需要关闭Activity自带的页面统计，
        // 然后在每个页面中重新集成页面统计的代码(包括调用了 onResume 和 onPause 的Activity)。
        MobclickAgent.openActivityDurationTrack(false);
        MobclickAgent.setScenarioType(mContext, EScenarioType.E_UM_NORMAL);
    }

    @Override
	protected void onPause() {
		super.onPause();
        MobclickAgent.onPageEnd("Login Activity");
        MobclickAgent.onPause(mContext);
	}

	@Override
	protected void onResume() {
		super.onResume();
        MobclickAgent.onPageStart("Login Activity");
        MobclickAgent.onResume(mContext);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			ThreadPoolFactory.getThreadPoolManager().stopAllTask();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void RequestSuccessful(String jsonString, int taskType) {
		
	}

}
