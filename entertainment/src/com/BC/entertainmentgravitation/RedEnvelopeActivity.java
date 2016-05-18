package com.BC.entertainmentgravitation;

import java.util.ArrayList;

import com.BC.entertainmentgravitation.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.BC.entertainment.adapter.SectionsPagerAdapter;
import com.BC.entertainmentgravitation.fragment.RedALiatFragment;
import com.BC.entertainmentgravitation.fragment.RedHoldListFragment;
import com.summer.activity.BaseActivity;
import com.umeng.analytics.MobclickAgent;

/**
 * 红包
 * @author zhongwen
 *
 */
public class RedEnvelopeActivity extends BaseActivity {
	ViewPager pager;
	private SectionsPagerAdapter mAdapter;

	RadioGroup group;

	ArrayList<Fragment> views = new ArrayList<Fragment>();
	RedHoldListFragment holdListFragment;
	RedALiatFragment aLiatFragment;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_redenvelope);
		group = (RadioGroup) findViewById(R.id.radioGroup1);
		pager = (ViewPager) findViewById(R.id.billsViewPage);
		init();
		group.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				switch (checkedId) {
				case R.id.radio0:
					pager.setCurrentItem(0);
					break;
				case R.id.radio1:
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
					group.check(R.id.radio0);
					break;
				case 1:
					group.check(R.id.radio1);
					break;

				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});
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

	private void init() {
		// TODO Auto-generated method stub
		holdListFragment = new RedHoldListFragment();
		aLiatFragment = new RedALiatFragment();

		views.add(holdListFragment);
		views.add(aLiatFragment);
		mAdapter = new SectionsPagerAdapter(this.getSupportFragmentManager(),
				views);
		pager.setAdapter(mAdapter);
	}
	@Override
	public void RequestSuccessful(String jsonString, int taskType) {
		
	}

}
