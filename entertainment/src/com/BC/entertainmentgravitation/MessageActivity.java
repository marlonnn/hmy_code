package com.BC.entertainmentgravitation;

import java.util.List;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;

import com.BC.entertainment.adapter.MessageRecycleAdapter;
import com.BC.entertainment.adapter.MessageRecycleAdapter.OnItemClickListener;
import com.BC.entertainmentgravitation.entity.GeTui;
import com.BC.entertainmentgravitation.entity.GeTuiDao;
import com.summer.activity.BaseActivity;
import com.umeng.analytics.MobclickAgent;

public class MessageActivity extends BaseActivity implements OnClickListener, OnItemClickListener{

	private RecyclerView messageList;
	
	private List<GeTui> geTuis;
	
	private MessageRecycleAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message);
		findViewById(R.id.imageViewBack).setOnClickListener(this);
		findViewById(R.id.txtViewIgnore).setOnClickListener(this);
		initView();
	}
	
	private void initView()
	{
		messageList = (RecyclerView) findViewById(R.id.listViewMessage);
		geTuis = new GeTuiDao(this).GetAll();
		adapter =  new MessageRecycleAdapter(this, geTuis);
        adapter.notifyDataSetChanged();
        adapter.setmOnItemClickListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        messageList.setVerticalScrollBarEnabled(true);
        messageList.setLayoutManager(linearLayoutManager);
        
        messageList.setItemAnimator(new DefaultItemAnimator());//more的动画效果
		messageList.setAdapter(adapter);
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
		switch (v.getId()) {
		/**
		 * 忽略未读
		 */
		case R.id.txtViewIgnore:
			new GeTuiDao(this).update();
			if (geTuis != null)
			{
				for (int i=0; i<geTuis.size(); i++)
				{
					geTuis.get(i).setHasRead(true);
				}
			}
			adapter.notifyDataSetChanged();
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

	@Override
	public void onItemClick(View view, int position) {
		GeTui g = (GeTui)view.getTag();
		if (g != null)
		{
			g.setHasRead(true);
			new GeTuiDao(this).update(g);
			adapter.notifyDataSetChanged();
		}

	}

}
