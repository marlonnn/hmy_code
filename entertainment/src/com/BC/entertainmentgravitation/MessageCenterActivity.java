package com.BC.entertainmentgravitation;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.BC.entertainment.adapter.MessageRecycleAdapter.OnItemClickListener;
import com.BC.entertainment.view.CustomViewPager;
import com.BC.entertainmentgravitation.fragment.MessageFragment;
import com.BC.entertainmentgravitation.fragment.PrivateFragment;
import com.summer.activity.BaseActivity;
import com.umeng.analytics.MobclickAgent;

public class MessageCenterActivity extends BaseActivity implements OnClickListener, OnItemClickListener {

	private MessageFragment messageFragment;
	private PrivateFragment privateFragment;
	private CustomViewPager viewPager;
	private RadioGroup radio;
	
	private int currentItem = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message_center);
		findViewById(R.id.imageViewBack).setOnClickListener(this);
		findViewById(R.id.txtViewIgnore).setOnClickListener(this);
		initFragment();
		initView();
	}
	
	private void initFragment()
	{
		messageFragment = new MessageFragment();
		privateFragment = new PrivateFragment();
	}
	
	@SuppressWarnings("deprecation")
	private void initView()
	{
		FragmentManager fragmentManager = this.getSupportFragmentManager();
		radio = (RadioGroup) findViewById(R.id.rGroup);
		radio.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				/**
				 * 系统消息
				 */
				case R.id.tbtnSysMessage:
					viewPager.setCurrentItem(0);
					currentItem = 0;
					break;
				/**
				 * 私信
				 */
				case R.id.rbtnPrivateMessage:
					viewPager.setCurrentItem(1);
					currentItem = 1;
					break;

				}
			}
		});
		
		viewPager = (CustomViewPager) findViewById(R.id.customoViewPager);
        viewPager.setAdapter(new FragmentPagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
            	Fragment fragment = null;
            	switch(position)
            	{
				/**
				 * 系统消息
				 */
            	case 0:
            		fragment = messageFragment;
            		break;
				/**
				 * 私信
				 */
            	case 1:
            		fragment = privateFragment;
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
        radio.check(R.id.tbtnSysMessage);
        
        viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				switch(arg0)
				{
				/**
				 * 系统消息
				 */
            	case 0:
            		radio.check(R.id.tbtnSysMessage);
            		break;
				/**
				 * 私信
				 */
            	case 1:
            		radio.check(R.id.rbtnPrivateMessage);
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
	public void onStop() {
		super.onStop();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	public void onItemClick(View view, int position) {
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		/**
		 * 忽略未读
		 */
		case R.id.txtViewIgnore:
			switch (currentItem)
			{
			/**
			 * 系统消息
			 */
			case 0:
				try {
					if (messageFragment != null)
					{
						messageFragment.ignoreMessage();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			/**
			 * 私信
			 */
			case 1:
				try {
					if (privateFragment != null)
					{
//						privateFragment.ignoreMessage();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			}
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
