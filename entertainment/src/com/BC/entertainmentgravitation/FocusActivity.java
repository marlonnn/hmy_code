package com.BC.entertainmentgravitation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.BC.entertainment.adapter.SlideAdapter;
import com.BC.entertainment.inter.SlideCallback;
import com.BC.entertainmentgravitation.entity.FHNEntity;
import com.BC.entertainmentgravitation.entity.Member;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.summer.activity.BaseActivity;
import com.summer.config.Config;
import com.summer.factory.ThreadPoolFactory;
import com.summer.handler.InfoHandler;
import com.summer.json.Entity;
import com.summer.slidelistview.SlideListView;
import com.summer.slidelistview.SlideListView.SlideMode;
import com.summer.task.HttpBaseTask;
import com.summer.treadpool.ThreadPoolConst;
import com.summer.utils.JsonUtil;
import com.summer.utils.ToastUtil;
import com.summer.utils.UrlUtil;
import com.umeng.analytics.MobclickAgent;

public class FocusActivity extends BaseActivity implements OnClickListener, SlideCallback {
	
	private Gson gson;
	private List<FHNEntity> hotList = new ArrayList<>();
	private SlideAdapter adapter;
	private int pageIndex = 1;
	private SlideListView mSlideListView;
	private String clientId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		gson = new Gson();
		setContentView(R.layout.activity_focus);
		findViewById(R.id.imageViewBack).setOnClickListener(this);
		mSlideListView = ((SlideListView) findViewById(R.id.slistview));
		
		adapter = new SlideAdapter(this, hotList, this);
//		mSlideListView.setSlideMode(SlideMode.RIGHT);
		
		mSlideListView.setAdapter(adapter);
		
		try {
			Intent intent = this.getIntent();
			clientId = (String)intent.getSerializableExtra("clientId");
			if (clientId != null && clientId.contains(Config.User.getClientID()))
			{
				mSlideListView.setSlideMode(SlideMode.RIGHT);
			}
			else
			{
				mSlideListView.setSlideMode(SlideMode.NONE);
			}
			sendFocusStarListRequest(clientId);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
	
	/**
	 * 获取信息
	 */
	private void sendFocusStarListRequest(String clientID) {
		if (clientID == null ) {
			ToastUtil.show(this, "无法获取信息");
			return;
		}
		HashMap<String, String> entity = new HashMap<String, String>();

		entity.put("clientID", clientID);
//		entity.put("The_page_number", "" + pageIndex);
		entity.put("type", "1");

		
		ShowProgressDialog("获取热门用户基本信息...");		
		List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
		addToThreadPool(Config.stat_list, "send search request", params);
	}
	
	/**
	 * 取消关注
	 */
	private void sendUnFocusRequest(String starID)
	{
		if (Config.User == null || starID == null) {
			ToastUtil.show(this, "抱歉，提交失败");
			return;
		}
		HashMap<String, String> entity = new HashMap<String, String>();

		entity.put("clientID", Config.User.getClientID());
		entity.put("Star_ID", starID);
		List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
		addToThreadPool(Config.unfollow_attention, "send un focus request", params);
	}
	
	/**
	 * 获取热门用户基本信息
	 * @param fhnEntity
	 */
	private void sendBaseInfoRequest(FHNEntity fhnEntity)
	{
		if (fhnEntity != null && fhnEntity.getUsername() != null)
		{
			HashMap<String, String> entity = new HashMap<String, String>();
			entity.put("username", fhnEntity.getUsername());
			ShowProgressDialog("获取热门用户基本信息...");		
			List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
			addToThreadPool(Config.member_in, "send search request", params);
		}
	}
	
    private void addToThreadPool(int taskType, String Tag, List<NameValuePair> params)
    {
    	HttpBaseTask httpTask = new HttpBaseTask(ThreadPoolConst.THREAD_TYPE_FILE_HTTP, Tag, params, UrlUtil.GetUrl(taskType));
    	httpTask.setTaskType(taskType);
    	InfoHandler handler = new InfoHandler(this);
    	httpTask.setInfoHandler(handler);
    	ThreadPoolFactory.getThreadPoolManager().addTask(httpTask);
    }
    
	private void addRanking() {
		if (hotList == null) {
			return;
		}
		if (pageIndex == 1) {// 第一页时，先清空数据集
			adapter.clearAll();
		}
		pageIndex++;
		adapter.add(hotList);
	}
    
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
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
		switch(taskType)
		{
		case Config.stat_list:
			Entity<List<FHNEntity>> baseEntity = gson.fromJson(jsonString,
					new TypeToken<Entity<List<FHNEntity>>>() {
					}.getType());
			hotList = baseEntity.getData();
			if (hotList != null && hotList.size() > 0) {
				addRanking();
			} else {
				ToastUtil.show(this, "没有更多数据了");
			}
			break;
		case Config.unfollow_attention:
			//取消关注成功
			ToastUtil.show(this, "取消关注成功");
			break;
		case Config.member_in:
			Entity<Member> entity = gson.fromJson(jsonString,
					new TypeToken<Entity<Member>>() {
					}.getType());
			if (entity != null && entity.getData() != null)
			{
				Intent intent = new Intent();
				intent.setClass(FocusActivity.this, PersonalHomeActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("member", entity.getData());
				intent.putExtras(bundle);
				startActivity(intent);
			}
			break;
		}
	}

	@Override
	public void unFocus(String starId) {
		if (starId != null)
		{
			sendUnFocusRequest(starId);
		}
	}

	@Override
	public void unListen(String starId) {
		
	}

	@Override
	public void itemClick(FHNEntity item) {
		if (item != null)
		{
			sendBaseInfoRequest(item);
		}
		
	}

}
