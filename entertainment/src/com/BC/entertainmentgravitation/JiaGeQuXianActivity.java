package com.BC.entertainmentgravitation;

import android.os.Bundle;

import com.BC.entertainmentgravitation.R;
import com.BC.entertainmentgravitation.fragment.JiaGeQuXianFragment;

public class JiaGeQuXianActivity extends com.summer.activity.BaseActivity {
	JiaGeQuXianFragment jiaGeQuXianFragment;
	public static String starID;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_jia_ge_qu_xian);
		jiaGeQuXianFragment = (JiaGeQuXianFragment) getSupportFragmentManager()
				.findFragmentById(R.id.fragment1);
		jiaGeQuXianFragment.showStarInformation(starID);
	}

	@Override
	public void RequestSuccessful(String jsonString, int taskType) {
		
	}

}
