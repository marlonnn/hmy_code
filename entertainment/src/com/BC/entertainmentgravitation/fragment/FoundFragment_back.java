package com.BC.entertainmentgravitation.fragment;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.BC.entertainment.adapter.FoundRecycleAdapter;
import com.BC.entertainment.adapter.FoundRecycleAdapter.OnItemClickListener;
import com.BC.entertainment.cache.FoundCache;
import com.BC.entertainmentgravitation.CrewActivity;
import com.BC.entertainmentgravitation.MessageActivity;
import com.BC.entertainmentgravitation.R;
import com.BC.entertainmentgravitation.entity.Found;
import com.summer.fragment.BaseFragment;

/**
 * 发现页
 * @author wen zhong
 *
 */
public class FoundFragment_back extends BaseFragment implements OnClickListener, OnItemClickListener{
	
	private View rootView;
	
	private FoundRecycleAdapter adapter;
	private List<Found> founds;

	private RecyclerView foundList;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
		rootView = inflater.inflate(R.layout.fragment_founds, null);
		return rootView;
	}
	
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initView();
	}
	
	private void initView()
	{
		founds = FoundCache.getInstance().GetFounds();
		foundList = (RecyclerView) rootView.findViewById(R.id.listViewFound);
		adapter = new FoundRecycleAdapter(getActivity(), founds);
        adapter.notifyDataSetChanged();
        adapter.setmOnItemClickListener(this);
        foundList.setAdapter(adapter);
	}



	@Override
	public void onClick(View v) {
		
	}

	@Override
	public void RequestSuccessful(String jsonString, int taskType) {
		switch (taskType) {
		}
	}

	@Override
	public void onItemClick(View view, int position) {
		Found found = (Found)view.getTag();
		Intent intent = null;
		if(found != null)
		{
			switch(found.getResource())
			{
			/**
			 * 剧组信息
			 */
			case R.drawable.activity_found_live:
				intent = new Intent(getActivity(), CrewActivity.class);
				startActivity(intent);
				break;
			/**
			 * 消息中心
			 */
			case R.drawable.activity_found_message:
				intent = new Intent(getActivity(), MessageActivity.class);
				startActivity(intent);
				break;
//			/**
//			 * 权益中心
//			 */
//			case R.drawable.activity_personal_career:
//				intent = new Intent(getActivity(), CareerActivity.class);
//				startActivity(intent);
//				break;

			}
		}
	}

}
