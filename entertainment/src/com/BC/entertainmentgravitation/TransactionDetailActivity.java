package com.BC.entertainmentgravitation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.BC.entertainmentgravitation.entity.RedAList;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.netease.nim.uikit.common.ui.ptr.PullToRefreshBase;
import com.netease.nim.uikit.common.ui.ptr.PullToRefreshBase.OnRefreshListener2;
import com.netease.nim.uikit.common.ui.ptr.PullToRefreshListView;
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
import com.umeng.analytics.MobclickAgent;

public class TransactionDetailActivity extends BaseActivity implements OnClickListener{

	private Gson gson;
	private PullToRefreshListView pullToRefreshListView;
	private CommonAdapter<RedAList> adapter;
	private int pageIndex = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_transaction);
		gson = new Gson();
		findViewViewById();
		initializeView();
		sendAccountRequest(1);
	}
	
	private void findViewViewById()
	{
		findViewById(R.id.imageViewBack).setOnClickListener(this);
	}
	
	private void initializeView()
	{
		pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.pullToRefreshListView);
		pullToRefreshListView.setOnRefreshListener(refreshListener);
		adapter = new CommonAdapter<RedAList>(this.getBaseContext(),
				R.layout.activity_transaction_item, new ArrayList<RedAList>()) {

			@Override
			public void convert(ViewHolder helper, final RedAList item, int position) {
				helper.setText(R.id.txtViewHold,
						item.getThe_donor() + "");
				helper.setText(R.id.txtViewNumber,
						item.getGrants_of_number() + "");

				TextView textView = (TextView) helper.getView(R.id.txtViewType);

				switch (item.getType()) {
				case 1:
					textView.setText("收入");
					textView.setTextColor(Color.parseColor("#dd0000"));
					break;
				case 2:
					textView.setText("支出");
					textView.setTextColor(Color.parseColor("#2fab21"));
					break;
				case 3:
					textView.setText("支出");
					textView.setTextColor(Color.parseColor("#2fab21"));
					break;

				}

				helper.setText(R.id.txtViewDes, item.getThe_publishers_name() + "");
				helper.setText(R.id.txtViewTime, item.getTime() + "");


			}
		};
		pullToRefreshListView.setAdapter(adapter);
	}
	
	OnRefreshListener2<ListView> refreshListener = new OnRefreshListener2<ListView>(){

		@Override
		public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
			// 下拉刷新
			String time = DateUtils.formatDateTime(TransactionDetailActivity.this,
					System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
							| DateUtils.FORMAT_SHOW_DATE
							| DateUtils.FORMAT_ABBREV_ALL);
			pullToRefreshListView.getLoadingLayoutProxy().setRefreshingLabel(
					getString(R.string.fragment_account_refresh_now));
			pullToRefreshListView.getLoadingLayoutProxy().setPullLabel(getString(R.string.fragment_account_pull_down));
			pullToRefreshListView.getLoadingLayoutProxy().setReleaseLabel(
					getString(R.string.fragment_account_release_refresh));
			refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(
					getString(R.string.fragment_account_last_update_time) + time);
			pageIndex = 1;
			// send request
			sendAccountRequest(pageIndex);
		}

		@Override
		public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
			// 上拉翻页
			String time = DateUtils.formatDateTime(TransactionDetailActivity.this,
					System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
							| DateUtils.FORMAT_SHOW_DATE
							| DateUtils.FORMAT_ABBREV_ALL);
			pullToRefreshListView.getLoadingLayoutProxy().setRefreshingLabel(
					getString(R.string.fragment_account_loading));
			pullToRefreshListView.getLoadingLayoutProxy().setPullLabel(getString(R.string.fragment_account_pull_up));
			pullToRefreshListView.getLoadingLayoutProxy().setReleaseLabel(
					getString(R.string.fragment_account_release_loading));
			refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(
					getString(R.string.fragment_account_last_loading_time) + time);
			// send request
			sendAccountRequest(pageIndex);
		}
	};
	
	private void sendAccountRequest(int pageIndex)
	{
    	if (Config.User == null)
    	{
			ToastUtil.show(this, getString(R.string.mainactivity_fail_get_sart_info));
			return;
    	}
    	
		HashMap<String, String> entity = new HashMap<String, String>();

		entity.put("clientID", "" + Config.User.getClientID());
		entity.put("The_page_number", "" + pageIndex);
		
		List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
		addToThreadPool(Config.account, "send account request", params);
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
	public void onInfoReceived(int errcode, HashMap<String, Object> items) {
		super.onInfoReceived(errcode, items);
		pullToRefreshListView.onRefreshComplete();
	}

	@Override
	public void onNotifyText(String notify) {
		super.onNotifyText(notify);
	}
	
	@Override
	public void RequestFailed(int errcode, String message, int taskType) {
		super.RequestFailed(errcode, message, taskType);
		ToastUtil.show(this, getString(R.string.fragment_account_fail_data));
	}

	@Override
	public void RequestSuccessful(String jsonString, int taskType) {
		switch (taskType) {
		case Config.account:
			Entity<List<RedAList>> baseEntity = gson.fromJson(jsonString,
					new TypeToken<Entity<List<RedAList>>>() {
					}.getType());
			List<RedAList> messageItems = baseEntity.getData();
			if (messageItems != null) {
				if (pageIndex == 1) {// 第一页时，先清空数据集
					adapter.clearAll();
				}
				pageIndex++;
				adapter.add(messageItems);
			} else {
				ToastUtil.show(this, this.getString(R.string.fragment_account_fail_data));
			}
			break;
		}
	}
	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
		/**
		 * 返回键
		 */
		case R.id.imageViewBack:
			finish();
			break;
		}
		
	}
}
