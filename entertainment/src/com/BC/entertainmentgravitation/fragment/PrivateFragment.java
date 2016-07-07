package com.BC.entertainmentgravitation.fragment;

import java.util.List;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

import com.BC.entertainment.adapter.MessageRecycleAdapter;
import com.BC.entertainment.adapter.MessageRecycleAdapter.OnItemClickListener;
import com.BC.entertainmentgravitation.R;
import com.BC.entertainmentgravitation.entity.GeTui;
import com.BC.entertainmentgravitation.entity.GeTuiDao;
import com.summer.fragment.BaseFragment;

/**
 * 私信
 * @author zhongwen
 *
 */
public class PrivateFragment extends BaseFragment implements OnClickListener, OnItemClickListener {

	private View rootView;
	
	private RecyclerView messageList;
	
	private List<GeTui> geTuis;
	
	private MessageRecycleAdapter adapter;
	
	@SuppressLint("InflateParams") @Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_message_private, null);
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
		geTuis = new GeTuiDao(getActivity()).GetAll();
		adapter =  new MessageRecycleAdapter(getActivity(), geTuis);
        adapter.notifyDataSetChanged();
        adapter.setmOnItemClickListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        messageList.setVerticalScrollBarEnabled(true);
        messageList.setLayoutManager(linearLayoutManager);
        
        messageList.setItemAnimator(new DefaultItemAnimator());//more的动画效果
		messageList.setAdapter(adapter);
	}
	
	@Override
	public void onItemClick(View view, int position) {
		GeTui g = (GeTui)view.getTag();
		if (g != null)
		{
			g.setHasRead(true);
			new GeTuiDao(getActivity()).update(g);
			adapter.notifyDataSetChanged();
		}
	}
	
	/**
	 * 忽略未读
	 */	
	public void ignoreMessage()
	{
		new GeTuiDao(getActivity()).update();
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
		
	}

	@Override
	public void RequestSuccessful(int status, String jsonString, int taskType) {
		
	}

}
