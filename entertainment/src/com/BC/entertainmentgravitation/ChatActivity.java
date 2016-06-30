package com.BC.entertainmentgravitation;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.summer.activity.BaseActivity;
import com.umeng.analytics.MobclickAgent;

public class ChatActivity extends BaseActivity implements OnClickListener{

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		findViewById(R.id.imageViewBack).setOnClickListener(this);
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
	public void onClick(View v) {
		switch (v.getId()) {
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
