package com.BC.entertainmentgravitation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.BC.entertainment.adapter.BannerAdapter;
import com.BC.entertainment.view.CirclePageIndicator;
import com.BC.entertainmentgravitation.R;
import com.BC.entertainmentgravitation.entity.Activitys;
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
import com.summer.utils.ToastUtil;
import com.summer.utils.UrlUtil;
import com.umeng.analytics.MobclickAgent;

public class HuodongActivity extends BaseActivity {
	private ViewPager banner;
	private CirclePageIndicator indicator;
	private BannerAdapter bannerAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_huo_dong);
		banner = (ViewPager) findViewById(R.id.banner);
		indicator = (CirclePageIndicator) findViewById(R.id.indicator);

		bannerAdapter = new BannerAdapter(getSupportFragmentManager(),
				new ArrayList<Activitys>());
		banner.setAdapter(bannerAdapter);
		if (MainEntryActivity.authoritativeInformation != null
				&& MainEntryActivity.authoritativeInformation.getActivity() != null) {
			List<Activitys> activitys = MainEntryActivity.authoritativeInformation
					.getActivity();
			initBanner(activitys);
		} else {
			sendReqActivities();
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
	 * 获取广告及公告信息
	 */
	private void sendReqActivities() {
		if (Config.User == null) {
			ToastUtil.show(this, "无法获取");
			return;
		}
		HashMap<String, String> entity = new HashMap<String, String>();

		entity.put("clientID", Config.User.getClientID());
		
		ShowProgressDialog("获取信息...");		
		List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
		addToThreadPool(Config.activities, "send delete album request", params);
	}

    private void addToThreadPool(int taskType, String Tag, List<NameValuePair> params)
    {
    	HttpBaseTask httpTask = new HttpBaseTask(ThreadPoolConst.THREAD_TYPE_FILE_HTTP, Tag, params, UrlUtil.GetUrl(taskType));
    	httpTask.setTaskType(taskType);
    	InfoHandler handler = new InfoHandler(this);
    	httpTask.setInfoHandler(handler);
    	ThreadPoolFactory.getThreadPoolManager().addTask(httpTask);
    }
    
	private void initBanner(List<Activitys> result) {
		// TODO Auto-generated method stub

		bannerAdapter.add(result);
		indicator.setPadding((banner.getWidth() - indicator.getWidth()) / 2,
				indicator.getPaddingTop(), indicator.getPaddingRight(),
				indicator.getPaddingBottom());
		indicator.setInterval(5000);
		indicator.setViewPager(banner);
		indicator.startAutoPlay();
	}

	@Override
	public void RequestSuccessful(String jsonString, int taskType) {
		Gson gson = new Gson();
		switch (taskType) {
		case Config.activities:

			Entity<AuthoritativeInformation> baseEntity3 = gson.fromJson(
					jsonString,
					new TypeToken<Entity<AuthoritativeInformation>>() {
					}.getType());
			MainEntryActivity.authoritativeInformation = baseEntity3.getData();
			if (MainEntryActivity.authoritativeInformation != null
					&& MainEntryActivity.authoritativeInformation.getActivity() != null) {
				List<Activitys> activitys = MainEntryActivity.authoritativeInformation
						.getActivity();
				initBanner(activitys);
			}

			break;
		}
		
	}
}
