package com.BC.entertainmentgravitation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.BC.entertainmentgravitation.entity.Huodong;
import com.BC.entertainmentgravitation.entity.StarLiveVideoInfo;
import com.summer.activity.BaseActivity;
import com.umeng.analytics.MobclickAgent;

public class HuodongDetailActivity extends BaseActivity implements OnClickListener{

	private Huodong huodong;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_huodong_detail);
		findViewById(R.id.imageViewBack).setOnClickListener(this);
        Intent intent = this.getIntent();
        huodong = (Huodong)intent.getSerializableExtra("huodong");
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
