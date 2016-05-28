package com.BC.entertainmentgravitation;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.BC.entertainment.adapter.SectionsPagerAdapter;
import com.BC.entertainmentgravitation.fragment.EnvelopeHoldFragment;
import com.BC.entertainmentgravitation.fragment.EnvelopeRecordFragment;
import com.summer.activity.BaseActivity;
import com.umeng.analytics.MobclickAgent;

public class EnvelopeActivity extends BaseActivity {

	private RadioGroup group;
	private ViewPager pager;
	private ArrayList<Fragment> views = new ArrayList<Fragment>();
	private EnvelopeHoldFragment envelopeHoldFragment;
	private EnvelopeRecordFragment envelopeRecordFragment;
	private SectionsPagerAdapter mAdapter;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal_envelopes);
		group = (RadioGroup) findViewById(R.id.rGroupEnvelope);
		pager = (ViewPager) findViewById(R.id.billsViewPage);
		initView();
		group.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.radioHold:
					pager.setCurrentItem(0);
					break;
				case R.id.radioRevord:
					pager.setCurrentItem(1);
					break;
				}
			}
		});
		pager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				switch (arg0) {
				case 0:
					group.check(R.id.radioHold);
					break;
				case 1:
					group.check(R.id.radioRevord);
					break;

				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}
	
	private void initView() {
		// TODO Auto-generated method stub
		envelopeHoldFragment = new EnvelopeHoldFragment();
		envelopeRecordFragment = new EnvelopeRecordFragment();

		views.add(envelopeHoldFragment);
		views.add(envelopeRecordFragment);
		mAdapter = new SectionsPagerAdapter(this.getSupportFragmentManager(),
				views);
		pager.setAdapter(mAdapter);
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
	public void RequestSuccessful(String jsonString, int taskType) {
		
	}

}
