package com.BC.entertainmentgravitation.fragment;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

import com.BC.entertainment.adapter.MessageSysRecycleAdapter;
import com.BC.entertainment.adapter.MessageSysRecycleAdapter.OnItemClickListener;
import com.BC.entertainmentgravitation.HuodongActivity;
import com.BC.entertainmentgravitation.HuodongDetailActivity;
import com.BC.entertainmentgravitation.MessageSysDetailActivity;
import com.BC.entertainmentgravitation.R;
import com.BC.entertainmentgravitation.entity.GeTui;
import com.BC.entertainmentgravitation.entity.GeTuiDao;
import com.summer.fragment.BaseFragment;

public class MessageFragment extends BaseFragment implements OnClickListener, OnItemClickListener {

	private View rootView;
	
	private RecyclerView messageList;
	
	private List<GeTui> geTuis = new ArrayList<>();;
	
	private MessageSysRecycleAdapter adapter;
	
	@SuppressLint("InflateParams") @Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_message_system, null);
		return rootView;
	}
	
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initView();
	}
	
	private void initView()
	{
		messageList = (RecyclerView) rootView.findViewById(R.id.listViewMessage);
		geTuis = new GeTuiDao(getActivity()).Query("messagetype", "3");
		adapter =  new MessageSysRecycleAdapter(getActivity(), geTuis);
        adapter.notifyDataSetChanged();
        adapter.setmOnItemClickListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        messageList.setVerticalScrollBarEnabled(true);
        messageList.setLayoutManager(linearLayoutManager);
        
        messageList.setItemAnimator(new DefaultItemAnimator());//more的动画效果
		messageList.setAdapter(adapter);
	}
	
	@Override
	public void RequestSuccessful(int status, String jsonString, int taskType) {
		
	}

	@Override
	public void onItemClick(View view, int position) {
		GeTui g = (GeTui)view.getTag();
		if (g != null)
		{
			g.setHasRead(true);
			new GeTuiDao(getActivity()).update(g);
			adapter.notifyDataSetChanged();
			if (g.getMessagetype().contains("3") && g.getMessageid() != null)
			{
				Intent intent = new Intent(getActivity(), MessageSysDetailActivity.class);
				Bundle b = new Bundle();
				b.putString("id", g.getMessageid());
				intent.putExtras(b);
				startActivity(intent);
			}

		}
	}
	
	/**
	 * 忽略未读
	 */	
	public void ignoreMessage()
	{
		new GeTuiDao(getActivity()).update("messagetype", "3");
		if (geTuis != null)
		{
			for (int i=0; i<geTuis.size(); i++)
			{
				geTuis.get(i).setHasRead(true);
			}
		}
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		}
	}

}
