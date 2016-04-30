package com.BC.entertainmentgravitation;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.summer.activity.BaseActivity;

public class AboutActivity extends BaseActivity implements OnClickListener{

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal_about);
		findViewById(R.id.imageViewBack).setOnClickListener(this);
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
