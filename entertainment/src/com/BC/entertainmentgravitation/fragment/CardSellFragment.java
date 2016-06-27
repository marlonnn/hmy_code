package com.BC.entertainmentgravitation.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

import com.BC.entertainmentgravitation.R;
import com.BC.entertainmentgravitation.entity.CardOrder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.summer.config.Config;
import com.summer.factory.ThreadPoolFactory;
import com.summer.fragment.BaseFragment;
import com.summer.handler.InfoHandler;
import com.summer.json.Entity;
import com.summer.task.HttpBaseTask;
import com.summer.treadpool.ThreadPoolConst;
import com.summer.utils.JsonUtil;
import com.summer.utils.UrlUtil;

public class CardSellFragment extends BaseFragment implements OnClickListener {

	private List<CardOrder> sellCards = new ArrayList<>();
	private int pageIndex = 1;
	private Gson gson;
	private View rootView;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		gson = new Gson();
		super.onCreate(savedInstanceState);
	}
	
	@SuppressLint("InflateParams") @Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_card_sell, null);
		return rootView;
	}
	
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}
	
	private void initView()
	{
		
	}
	
	@Override
	public void onStart() {
		sendCardOrderListRequest();
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
	
	private void sendCardOrderListRequest()
	{
    	HashMap<String, String> entity = new HashMap<String, String>();
		entity.put("clientID", Config.User.getClientID());
		entity.put("page", String.valueOf(pageIndex));
    	List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
    	addToThreadPool(Config.member_in, "get start info", params);	
	}
	
    private void addToThreadPool(int taskType, String tag, List<NameValuePair> params)
    {
    	HttpBaseTask httpTask = new HttpBaseTask(ThreadPoolConst.THREAD_TYPE_FILE_HTTP, tag, params, UrlUtil.GetUrl(taskType));
    	httpTask.setTaskType(taskType);
    	InfoHandler handler = new InfoHandler(this);
    	httpTask.setInfoHandler(handler);
    	ThreadPoolFactory.getThreadPoolManager().addTask(httpTask);
    }
    
	@Override
	public void onClick(View v) {
		
	}

	@Override
	public void RequestSuccessful(int status, String jsonString, int taskType) {
		switch (taskType)
		{
		case Config.member_in:
			Entity<List<CardOrder>> entity = gson.fromJson(jsonString,
					new TypeToken<Entity<List<CardOrder>>>() {
					}.getType());
			if (entity.getData() != null)
			{
				sellCards = entity.getData();
			}
			break;
		}
	}

}
