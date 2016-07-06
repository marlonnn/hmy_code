package com.BC.entertainmentgravitation.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

import com.BC.entertainment.adapter.MessageRecycleAdapter.OnItemClickListener;
import com.BC.entertainmentgravitation.R;
import com.summer.fragment.BaseFragment;

/**
 * 私信
 * @author zhongwen
 *
 */
public class PrivateFragment extends BaseFragment implements OnClickListener, OnItemClickListener {

	private View rootView;
	
	@SuppressLint("InflateParams") @Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_message, null);
		return rootView;
	}
	
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initView();
	}
	
	private void initView()
	{
		
	}
	
	@Override
	public void onItemClick(View view, int position) {
		
	}

	@Override
	public void onClick(View v) {
		
	}

	@Override
	public void RequestSuccessful(int status, String jsonString, int taskType) {
		
	}

}
