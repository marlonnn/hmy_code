package com.BC.entertainmentgravitation;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.BC.entertainment.view.CustomViewPager;
import com.BC.entertainmentgravitation.fragment.LoginFragment;
import com.BC.entertainmentgravitation.fragment.LoginFragment.iLogin;
import com.BC.entertainmentgravitation.fragment.RegisteFragment;
import com.BC.entertainmentgravitation.fragment.RegisteFragment.iRegister;
import com.summer.activity.BaseActivity;
import com.umeng.analytics.MobclickAgent;

public class LoginActivity extends BaseActivity implements iRegister, iLogin{
	
    /**
     * 第三方应用Master Secret，修改为正确的值
     */
    private static final String MASTERSECRET = "xuPUHNX97j5c2RlO4VoBTA";
	
    private Context mContext;
	private CustomViewPager viewPager;
	
	private LoginFragment loginFragment;
	private RegisteFragment registeFragment;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_back);
        mContext = this;
        initView();
        ScreenManager.getScreenManager1().pushActivity(this);
    }
	
	private void initView()
	{
        loginFragment = new LoginFragment();
        registeFragment = new RegisteFragment();
		FragmentManager fragmentManager = this.getSupportFragmentManager();
		viewPager = (CustomViewPager) findViewById(R.id.vPagerLogin);
		viewPager.setPagingEnabled(false);
        viewPager.setAdapter(new FragmentPagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
            	Fragment fragment = null;
            	switch(position)
            	{
				/**
				 * 登陆
				 */
            	case 0:
            		fragment = loginFragment;
            		break;
				/**
				 * 注册
				 */
            	case 1:
            		fragment = registeFragment;
            		break;
            	}
                return fragment;
            }

            @Override
            public int getCount() {
                return 2;
            }
        });
        viewPager.setCurrentItem(0);
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
		ScreenManager.getScreenManager1().popActivity(this);
		super.onDestroy();
	}
	
	@Override
	public void RequestSuccessful(String jsonString, int taskType) {
	}

	@Override
	public void FinishRegister(boolean isSeccuss) {
		viewPager.setCurrentItem(0);
	}

	@Override
	public void isForgetPassword(boolean isForget) {
		registeFragment.IsForgetPassword(isForget);
		viewPager.setCurrentItem(1);
		
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		this.finish();
	}

	
}
