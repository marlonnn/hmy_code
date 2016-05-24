package com.BC.entertainmentgravitation.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.BC.entertainment.view.CustomViewPager;
import com.BC.entertainmentgravitation.R;
import com.summer.fragment.BaseFragment;

/**
 * 直播列表 包括关注 热门 最新
 * @author zhongwen
 *
 */
public class ListFragment extends BaseFragment implements OnClickListener{
	
	private View rootView;
	
	private FocusFragment focusFragment;
	private HotFragment hotFragment;
	private NewFragment newFragment;
	private RadioGroup radio;
	private CustomViewPager viewPager;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		initFragment();
		super.onCreate(savedInstanceState);
	}
	
	private void initFragment()
	{
		focusFragment = new FocusFragment();
		hotFragment = new HotFragment();
		newFragment = new NewFragment();
	}
	
	@SuppressWarnings("deprecation")
	private void initView()
	{
		FragmentManager fragmentManager = getChildFragmentManager();

		radio = (RadioGroup) rootView.findViewById(R.id.rGroup);
		radio.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				/**
				 * 关注
				 */
				case R.id.tbtnFocus:
					viewPager.setCurrentItem(0);
					break;
				/**
				 * 热门
				 */
				case R.id.rbtnHot:
					viewPager.setCurrentItem(1);
					break;
				/**
				 * 最新
				 */
				case R.id.rbtnNew:
					viewPager.setCurrentItem(2);
					break;

				}
			}
		});
		
		viewPager = (CustomViewPager) rootView.findViewById(R.id.customoViewPager);
        viewPager.setAdapter(new FragmentPagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
            	Fragment fragment = null;
            	switch(position)
            	{
				/**
				 * 关注
				 */
            	case 0:
            		fragment = focusFragment;
            		break;
				/**
				 * 热门
				 */
            	case 1:
            		fragment = hotFragment;
            		break;
				/**
				 * 最新
				 */
            	case 2:
            		fragment = newFragment;
            		break;
            	}
                return fragment;
            }

            @Override
            public int getCount() {
                return 3;
            }
        });
        viewPager.setCurrentItem(1);
        radio.check(R.id.rbtnHot);
        
        viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				switch(arg0)
				{
				/**
				 * 关注
				 */
            	case 0:
            		radio.check(R.id.tbtnFocus);
            		break;
				/**
				 * 热门
				 */
            	case 1:
            		radio.check(R.id.rbtnHot);
            		break;
				/**
				 * 最新
				 */
            	case 2:
            		radio.check(R.id.rbtnNew);
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
	public void onStart() {

		super.onStart();
	}

	@Override
	public void onResume() {

		super.onResume();
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	@SuppressLint("InflateParams") @Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_list, null);
		return rootView;
	}
	
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initView();
	}

	@Override
	public void onClick(View v) {
		
	}

	@Override
	public void RequestSuccessful(String jsonString, int taskType) {
	}

}
