package com.BC.entertainment.adapter;

import com.BC.entertainmentgravitation.fragment.CurveFragment;
import com.BC.entertainmentgravitation.fragment.FoundFragment;
import com.BC.entertainmentgravitation.fragment.FoundFragment_back;
import com.BC.entertainmentgravitation.fragment.ListFragment;
import com.BC.entertainmentgravitation.fragment.PersonalFragment;
import com.BC.entertainmentgravitation.fragment.SurfaceEmptyFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

public class HomeViewPagerAdapter extends FragmentPagerAdapter {

	private final int PAGER_COUNT = 5;
	
	private CurveFragment curveFragment;
	private ListFragment listFragment;
	private PersonalFragment personalFragment;
	private FoundFragment_back foundFragment;
	private SurfaceEmptyFragment emptyFragment;

	public HomeViewPagerAdapter(FragmentManager fm) {
		super(fm);
		curveFragment = new CurveFragment();
		listFragment = new ListFragment();
		emptyFragment = new SurfaceEmptyFragment();
		personalFragment = new PersonalFragment();
		foundFragment = new FoundFragment_back();
	}

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
    		fragment = listFragment;
    		break;
		/**
		 * 直播
		 */
    	case 2:
    		fragment = emptyFragment;
    		break;
		/**
		 * 发现
		 */
    	case 3:
    		fragment = foundFragment;
    		break;
		/**
		 * 我的
		 */
    	case 4:
    		fragment = personalFragment;
    		break;            		
    	}
        return fragment;
	}

	@Override
	public int getCount() {
		return PAGER_COUNT;
	}

    @Override
    public Object instantiateItem(ViewGroup vg, int position) {
        return super.instantiateItem(vg, position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }
}
