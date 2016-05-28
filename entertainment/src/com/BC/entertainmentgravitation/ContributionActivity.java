package com.BC.entertainmentgravitation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.BC.entertainmentgravitation.entity.Contribute;
import com.BC.entertainmentgravitation.entity.Member;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.netease.nim.uikit.common.ui.ptr.PullToRefreshBase;
import com.netease.nim.uikit.common.ui.ptr.PullToRefreshGridView;
import com.netease.nim.uikit.common.ui.ptr.PullToRefreshBase.OnRefreshListener2;
import com.summer.activity.BaseActivity;
import com.summer.adapter.CommonAdapter;
import com.summer.config.Config;
import com.summer.factory.ThreadPoolFactory;
import com.summer.handler.InfoHandler;
import com.summer.json.Entity;
import com.summer.logger.XLog;
import com.summer.task.HttpBaseTask;
import com.summer.treadpool.ThreadPoolConst;
import com.summer.utils.JsonUtil;
import com.summer.utils.ToastUtil;
import com.summer.utils.UrlUtil;
import com.summer.view.CircularImage;
import com.umeng.analytics.MobclickAgent;

public class ContributionActivity extends BaseActivity implements OnClickListener {

	private Gson gson;
	private TextView txtViewAmount;
	private PullToRefreshGridView pGridViewContribution;
	
	private int pageIndex = 1;
	private List<Contribute> contributes = new ArrayList<Contribute>();
	private CommonAdapter<Contribute> adapter;
	private Member member;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal_contribution);
		gson = new Gson();
		findViewById(R.id.imageViewBack).setOnClickListener(this);
		initAdapter();
		
		try {
			Intent intent = this.getIntent(); 
			member = (Member) intent.getSerializableExtra("member");
			if (member != null)
			{
				sendContributeRequest(member.getName());
				findViewById(member);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void initAdapter()
	{
//		adapter = new ContributeAdapter(this, contributes);
		adapter = new CommonAdapter<Contribute>(ContributionActivity.this, R.layout.activity_personal_contribution_item, contributes) {
			
			@Override
			public void convert(
					ViewHolder viewHolder,
					final Contribute item, int position) {
				try {
					CircularImage cPortrait = (CircularImage) viewHolder.getView(R.id.cImagePortrait);
					TextView Name = (TextView) viewHolder.getView(R.id.txtViewName);
					TextView contribute = (TextView) viewHolder.getView(R.id.txtViewContribute);
					TextView txtViewRange = (TextView) viewHolder.getView(R.id.txtViewRange);
					if (item != null)
					{
						txtViewRange.setText(String.valueOf(position + 1));
						Name.setText(isNullOrEmpty(item.getNick_name()) ? "未知" : item.getNick_name());
						contribute.setText(isNullOrEmpty(item.getSumcount()) ? "贡献0娱票" : "贡献" + item.getSumcount() + "娱票");
						Glide.with(ContributionActivity.this).load(item.getHead())
						.centerCrop()
						.diskCacheStrategy(DiskCacheStrategy.ALL)
						.placeholder(R.drawable.home_image).into(cPortrait);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
	}
	
	private void findViewById(Member member)
	{
		txtViewAmount = (TextView) findViewById(R.id.txtViewAmount);
		txtViewAmount.setText(isNullOrEmpty(member.getDollar()) ? "0" : member.getDollar());
		pGridViewContribution = (PullToRefreshGridView)findViewById(R.id.pGridViewContribution);
		pGridViewContribution.getRefreshableView().setNumColumns(1);
		pGridViewContribution.getRefreshableView().setVerticalSpacing(10);
		pGridViewContribution.setMode(PullToRefreshBase.Mode.BOTH);
		pGridViewContribution.setOnRefreshListener(refreshListener);
		pGridViewContribution.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
			}
		});
		pGridViewContribution.setAdapter(adapter);
	}
	
	OnRefreshListener2<GridView> refreshListener = new OnRefreshListener2<GridView>() {

		@Override
		public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
			// 下拉刷新
			String time = DateUtils.formatDateTime(ContributionActivity.this,
					System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
							| DateUtils.FORMAT_SHOW_DATE
							| DateUtils.FORMAT_ABBREV_ALL);
			pGridViewContribution.getLoadingLayoutProxy().setRefreshingLabel(
					"正在刷新");
			pGridViewContribution.getLoadingLayoutProxy().setPullLabel("下拉刷新");
			pGridViewContribution.getLoadingLayoutProxy().setReleaseLabel(
					"释放开始刷新");
			refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(
					"最后更新时间:" + time);
			pageIndex = 1;
			if (member != null)
			{
				sendContributeRequest(member.getName());
			}
		}

		@Override
		public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
			// 上拉翻页
			String time = DateUtils.formatDateTime(ContributionActivity.this,
					System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
							| DateUtils.FORMAT_SHOW_DATE
							| DateUtils.FORMAT_ABBREV_ALL);
			pGridViewContribution.getLoadingLayoutProxy().setRefreshingLabel(
					"正在加载");
			pGridViewContribution.getLoadingLayoutProxy().setPullLabel("上拉翻页");
			pGridViewContribution.getLoadingLayoutProxy().setReleaseLabel(
					"释放开始加载");
			refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(
					"最后加载时间:" + time);
			// 调用数据
			if (member != null)
			{
				sendContributeRequest(member.getName());
			}
		}
	};
	
	private boolean isNullOrEmpty(String o)
	{
		if (o != null)
		{
			if (o.length() == 0)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			return true;
		}
	}
	
    private void sendContributeRequest(String username)
    {
    	if (username != null)
    	{
        	HashMap<String, String> entity = new HashMap<String, String>();
    		entity.put("username", username);
    		entity.put("page", String.valueOf(pageIndex));
        	List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
        	addToThreadPool(Config.contribute, "get contribute request", params);	
    	}
    }
    
    private void addToThreadPool(int taskType, String tag, List<NameValuePair> params)
    {
    	XLog.i("add to thread pool: " + tag);
    	HttpBaseTask httpTask = new HttpBaseTask(ThreadPoolConst.THREAD_TYPE_FILE_HTTP, tag, params, UrlUtil.GetUrl(taskType));
    	httpTask.setTaskType(taskType);
    	InfoHandler handler = new InfoHandler(this);
    	httpTask.setInfoHandler(handler);
    	ThreadPoolFactory.getThreadPoolManager().addTask(httpTask);
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
		
	}

	private void addContribute() {
		if (contributes == null) {
			return;
		}
		if (pageIndex == 1) {// 第一页时，先清空数据集
			adapter.clearAll();
		}
		pageIndex++;
		adapter.add(contributes);
	}
	
	@Override
	public void onInfoReceived(int errcode, HashMap<String, Object> items) {
		super.onInfoReceived(errcode, items);
		pGridViewContribution.onRefreshComplete();
	}
	
	@Override
	public void RequestSuccessful(String jsonString, int taskType) {
		switch(taskType)
		{
		case Config.contribute:
				Entity<List<Contribute>> entity = gson.fromJson(jsonString,
 						new TypeToken<Entity<List<Contribute>>>() {
 						}.getType());
 				
 				if (entity != null && entity.getData() != null)
 				{
 					contributes = entity.getData();
 					if (contributes != null && contributes.size() > 0)
 					{
 						addContribute();
 					} else {
 						ToastUtil.show(ContributionActivity.this, "没有更多数据了");
 					}
 				}
			break;
		}
	}

}
