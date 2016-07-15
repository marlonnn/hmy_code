package com.BC.entertainmentgravitation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.summer.activity.BaseActivity;
import com.summer.factory.ThreadPoolFactory;
import com.umeng.analytics.MobclickAgent;

public class Authenticate1Activity extends BaseActivity implements OnClickListener{

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_authenticate_step1);
		findViewById(R.id.imageViewBack).setOnClickListener(this);
		findViewById(R.id.btnAgree).setOnClickListener(this);
		ScreenManager.getScreenManager1().pushActivity(this);
	}
	
    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
    
	@Override
	protected void onResume() {
		super.onResume();
        MobclickAgent.onResume(this);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		ScreenManager.getScreenManager1().popActivity(this);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnAgree:
			Intent it = new Intent(this, Authenticate2Activity.class);
			startActivity(it);
			break;
		/**
		 * 返回键
		 */
		case R.id.imageViewBack:
			finish();
			break;
		}
	}

	@Override
	public void RequestSuccessful(String jsonString, int taskType) {
		
	}

}
