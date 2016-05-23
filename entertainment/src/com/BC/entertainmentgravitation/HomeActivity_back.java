package com.BC.entertainmentgravitation;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;

import com.BC.entertainmentgravitation.fragment.CurveFragment;
import com.summer.activity.BaseActivity;
import com.umeng.analytics.MobclickAgent;

/**
 * 
 * @author wen zhong
 *
 */
public class HomeActivity_back extends BaseActivity implements OnClickListener{
	
	private ViewPager viewPager;
	private CurveFragment curveFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_home_back);
		findViewById();
	}
	
	@SuppressWarnings("deprecation")
	private void findViewById()
	{
		curveFragment = new CurveFragment();
		
		FragmentManager fragmentManager = this.getSupportFragmentManager();
		viewPager = (ViewPager) findViewById(R.id.vPagerContent);
        viewPager.setAdapter(new FragmentPagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
            	Fragment fragment = null;
            	switch(position)
            	{
				/**
				 * 曲线
				 */
            	case 0:
            		fragment = curveFragment;
            		break;
				/**
				 * 直播列表
				 */
            	case 1:
            		break;
				/**
				 * 直播
				 */
            	case 2:
            		break;
				/**
				 * 发现
				 */
            	case 3:
            		break;
				/**
				 * 我的
				 */
            	case 4:
            		break;            		
            	}
                return fragment;
            }

            @Override
            public int getCount() {
                return 5;
            }
        });
        viewPager.setCurrentItem(1);
        
        viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				switch(arg0)
				{
				/**
				 * 曲线
				 */
            	case 0:
            		break;
				/**
				 * 直播列表
				 */
            	case 1:
            		break;
				/**
				 * 直播
				 */
            	case 2:
            		break;
				/**
				 * 发现
				 */
            	case 3:
            		break;
				/**
				 * 我的
				 */
            	case 4:
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
		switch(v.getId())
		{
		case R.id.focus:
			
			break;
		case R.id.invest:
			
			break;
		case R.id.divest:
			
			break;
		}
	}

	@Override
	public void RequestSuccessful(String jsonString, int taskType) {

	}

}
