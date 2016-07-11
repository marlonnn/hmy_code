package com.BC.entertainmentgravitation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;

import com.BC.entertainment.adapter.HuodongAdapter;
import com.BC.entertainment.adapter.HuodongAdapter.OnItemClickListener;
import com.BC.entertainmentgravitation.entity.Huodong;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.summer.activity.BaseActivity;
import com.summer.config.Config;
import com.summer.factory.ThreadPoolFactory;
import com.summer.handler.InfoHandler;
import com.summer.json.Entity;
import com.summer.task.HttpBaseTask;
import com.summer.treadpool.ThreadPoolConst;
import com.summer.utils.JsonUtil;
import com.summer.utils.UrlUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * 剧组活动
 * @author wen zhong
 *
 */
public class HuodongActivity extends BaseActivity implements OnClickListener, OnItemClickListener{

	private RecyclerView messageList;
	private List<Huodong> huodongs = new ArrayList<>();
	private HuodongAdapter adapter;
	private Gson gson;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_huodong);
		findViewById(R.id.imageViewBack).setOnClickListener(this);
		initView();
		sendActivityRequest();
	}
	
	private void initView()
	{
		gson = new Gson();
		messageList = (RecyclerView) findViewById(R.id.listViewHuodong);
		adapter = new HuodongAdapter(this, huodongs);
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
		 * 返回键
		 */
		case R.id.imageViewBack:
			finish();
			break;
		}
	}
	
	/**
	 * 获取广告及公告信息
	 */
	private void sendActivityRequest() {
		HashMap<String, String> entity = new HashMap<String, String>();

		ShowProgressDialog("请稍等...");		
		List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
		addToThreadPool(Config.getList, "send delete album request", params);
	}

    private void addToThreadPool(int taskType, String Tag, List<NameValuePair> params)
    {
    	HttpBaseTask httpTask = new HttpBaseTask(ThreadPoolConst.THREAD_TYPE_FILE_HTTP, Tag, params, UrlUtil.GetUrl(taskType));
    	httpTask.setTaskType(taskType);
    	InfoHandler handler = new InfoHandler(this);
    	httpTask.setInfoHandler(handler);
    	ThreadPoolFactory.getThreadPoolManager().addTask(httpTask);
    }

	@Override
	public void RequestSuccessful(String jsonString, int taskType) {
		switch(taskType)
		{
		case Config.getList:
			Entity<List<Huodong>> baseEntity = gson.fromJson(
					jsonString,
					new TypeToken<Entity<List<Huodong>>>() {
					}.getType());
			if (baseEntity.getData() != null)
			{
				huodongs = baseEntity.getData();
				adapter.notifyDataSetChanged();
			}
			break;
		}
	}

	@Override
	public void onItemClick(View view, int position) {
		Huodong huodong = (Huodong) view.getTag();
		if (huodong != null)
		{
			Intent intent = new Intent(HuodongActivity.this, HuodongDetailActivity.class);
			Bundle b = new Bundle();
			b.putSerializable("huodong", huodong);
			intent.putExtras(b);
			startActivity(intent);
		}
	}

}
