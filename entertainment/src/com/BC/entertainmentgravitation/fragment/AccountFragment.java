package com.BC.entertainmentgravitation.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import com.BC.entertainmentgravitation.R;
import com.BC.entertainmentgravitation.entity.RedAList;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.netease.nim.uikit.common.ui.ptr.PullToRefreshBase;
import com.netease.nim.uikit.common.ui.ptr.PullToRefreshBase.OnRefreshListener2;
import com.netease.nim.uikit.common.ui.ptr.PullToRefreshListView;
//import com.handmark.pulltorefresh.library.PullToRefreshBase;
//import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
//import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.summer.adapter.CommonAdapter;
import com.summer.config.Config;
import com.summer.factory.ThreadPoolFactory;
import com.summer.fragment.BaseFragment;
import com.summer.handler.InfoHandler;
import com.summer.json.Entity;
import com.summer.task.HttpBaseTask;
import com.summer.treadpool.ThreadPoolConst;
import com.summer.utils.JsonUtil;
import com.summer.utils.ToastUtil;
import com.summer.utils.UrlUtil;

public class AccountFragment extends BaseFragment{

	private PullToRefreshListView pullToRefreshListView;
	
	private CommonAdapter<RedAList> adapter;
	
	private View contentView;
	
	private int pageIndex = 1;
	
	@SuppressLint("InflateParams") @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		contentView = inflater.inflate(R.layout.fragment_account_list, null);
		return contentView;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initializeView();
		sendAccountRequest(1);
	}
	
	private void initializeView()
	{
		pullToRefreshListView = (PullToRefreshListView) contentView
				.findViewById(R.id.pullToRefreshListView1);
		pullToRefreshListView.setOnRefreshListener(refreshListener);
		adapter = new CommonAdapter<RedAList>(getActivity(),
				R.layout.fragment_account_item, new ArrayList<RedAList>()) {

			@Override
			public void convert(ViewHolder helper, final RedAList item) {
				helper.setText(R.id.The_publishers_name,
						item.getThe_publishers_name() + "");
				helper.setText(R.id.Grants_of_number,
						item.getGrants_of_number() + "");

				TextView textView = (TextView) helper.getView(R.id.type);

				switch (item.getType()) {
				case 1:
					textView.setText(getActivity().getString(R.string.fragment_account_item_publish_income));
					textView.setTextColor(Color.parseColor("#dd0000"));
					break;
				case 2:
					textView.setText(getActivity().getString(R.string.fragment_account_item_publish_payout));
					textView.setTextColor(Color.parseColor("#2fab21"));
					break;
				case 3:
					textView.setText(getActivity().getString(R.string.fragment_account_item_publish_payout));
					textView.setTextColor(Color.parseColor("#2fab21"));
					break;

				}

				helper.setText(R.id.The_donor, item.getThe_donor() + "");
				helper.setText(R.id.time, item.getTime() + "");
			}
		};
		pullToRefreshListView.setAdapter(adapter);
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
	public void RequestSuccessful(String jsonString, int taskType) {
		Gson gson = new Gson();
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
				ToastUtil.show(getActivity(), getResourceString(R.string.fragment_account_fail_data));
			}
			break;
		}
	}

	@Override
	public void RequestFailed(int errcode, String message, int taskType) {
		super.RequestFailed(errcode, message, taskType);
		ToastUtil.show(getActivity(), getResourceString(R.string.fragment_account_fail_data));
	}



	OnRefreshListener2<ListView> refreshListener = new OnRefreshListener2<ListView>(){

		@Override
		public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
			// 下拉刷新
			String time = DateUtils.formatDateTime(getActivity(),
					System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
							| DateUtils.FORMAT_SHOW_DATE
							| DateUtils.FORMAT_ABBREV_ALL);
			pullToRefreshListView.getLoadingLayoutProxy().setRefreshingLabel(
					getResourceString(R.string.fragment_account_refresh_now));
			pullToRefreshListView.getLoadingLayoutProxy().setPullLabel(getResourceString(R.string.fragment_account_pull_down));
			pullToRefreshListView.getLoadingLayoutProxy().setReleaseLabel(
					getResourceString(R.string.fragment_account_release_refresh));
			refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(
					getResourceString(R.string.fragment_account_last_update_time) + time);
			pageIndex = 1;
			// send request
			sendAccountRequest(pageIndex);
		}

		@Override
		public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
			// 上拉翻页
			String time = DateUtils.formatDateTime(getActivity(),
					System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
							| DateUtils.FORMAT_SHOW_DATE
							| DateUtils.FORMAT_ABBREV_ALL);
			pullToRefreshListView.getLoadingLayoutProxy().setRefreshingLabel(
					getResourceString(R.string.fragment_account_loading));
			pullToRefreshListView.getLoadingLayoutProxy().setPullLabel(getResourceString(R.string.fragment_account_pull_up));
			pullToRefreshListView.getLoadingLayoutProxy().setReleaseLabel(
					getResourceString(R.string.fragment_account_release_loading));
			refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(
					getResourceString(R.string.fragment_account_last_loading_time) + time);
			// send request
			sendAccountRequest(pageIndex);
		}
		
	};
	
	private void sendAccountRequest(int pageIndex)
	{
    	if (Config.User == null)
    	{
			ToastUtil.show(getActivity(), getResourceString(R.string.mainactivity_fail_get_sart_info));
			return;
    	}
    	
		HashMap<String, String> entity = new HashMap<String, String>();

		entity.put("clientID", "" + Config.User.getClientID());
		entity.put("The_page_number", "" + pageIndex);
		
		List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
		addToThreadPool(Config.account, "send account request", params);
	}
	
	private String getResourceString(int id)
	{
		return getActivity().getString(id);
	}
	
    private void addToThreadPool(int taskType, String Tag, List<NameValuePair> params)
    {
    	HttpBaseTask httpTask = new HttpBaseTask(ThreadPoolConst.THREAD_TYPE_FILE_HTTP, Tag, params, UrlUtil.GetUrl(taskType));
    	httpTask.setTaskType(taskType);
    	InfoHandler handler = new InfoHandler(this);
    	httpTask.setInfoHandler(handler);
    	ThreadPoolFactory.getThreadPoolManager().addTask(httpTask);
    }
	
}
