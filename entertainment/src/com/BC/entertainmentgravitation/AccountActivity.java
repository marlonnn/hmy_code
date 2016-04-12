package com.BC.entertainmentgravitation;

import android.os.Bundle;

import com.summer.activity.BaseActivity;
import com.BC.entertainmentgravitation.R;


public class AccountActivity extends BaseActivity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account_detail);
	}
	
	@Override
	public void RequestSuccessful(String jsonString, int taskType) {
	}

}
