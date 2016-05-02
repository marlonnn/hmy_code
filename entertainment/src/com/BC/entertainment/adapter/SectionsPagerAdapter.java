package com.BC.entertainment.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;

public class SectionsPagerAdapter extends FragmentPagerAdapter{
	private List<Fragment> fragments;
	private Context context;
	FragmentManager fm;

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public SectionsPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
		super(fm);
		this.fm = fm;
		this.fragments = fragments;
	}

	@Override
	public Fragment getItem(int position) {
		return fragments.get(position);
	}

	@Override
	public int getItemPosition(Object object) {
		// TODO Auto-generated method stub
		return POSITION_NONE;
	}

	@Override
	public int getCount() {
		return fragments.size();
	}

	public void removeFragments(int item) {
		if (this.fragments != null && item < this.fragments.size()) {
			FragmentTransaction ft = fm.beginTransaction();
			ft.detach(this.fragments.get(item));
			ft.remove(this.fragments.get(item));
			ft.commit();
			ft = null;
			fragments.remove(item);
			fm.executePendingTransactions();
		}
		notifyDataSetChanged();
	}

	public void setFragments(ArrayList<Fragment> fragments) {
		this.fragments = fragments;
		notifyDataSetChanged();
	}
}
