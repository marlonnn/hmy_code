package com.BC.entertainmentgravitation.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ListView;

import com.BC.entertainmentgravitation.R;
import com.BC.entertainmentgravitation.entity.RedList;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.netease.nim.uikit.common.ui.ptr.PullToRefreshBase;
import com.netease.nim.uikit.common.ui.ptr.PullToRefreshListView;
import com.netease.nim.uikit.common.ui.ptr.PullToRefreshBase.OnRefreshListener2;
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

/**
 * 持有红包
 * @author zhongwen
 *
 */
public class EnvelopeHoldFragment extends BaseFragment {
	
	private Gson gson;
	private View rootView;
	private PullToRefreshListView pullToRefreshListView;
	private CommonAdapter<RedList> adapter;
	private List<RedList> messageItems;
	private int pageIndex = 1;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		gson = new Gson();
		sendReqMessage(1);
		super.onCreate(savedInstanceState);
	}
	
	private void initView()
	{
		pullToRefreshListView = (PullToRefreshListView) rootView
				.findViewById(R.id.pullToRefreshListView1);
		pullToRefreshListView.setOnRefreshListener(refreshListener);
		adapter = new CommonAdapter<RedList>(getActivity(),
				R.layout.fragment_envelope_hold_item, new ArrayList<RedList>()) {

			@Override
			public void convert(ViewHolder helper, final RedList item, int position) {
				helper.setText(R.id.txtViewHoldName,
						item.getThe_publishers_name());

				helper.setText(R.id.txtViewNumber,
						item.getThe_user_holds_a_number_of() + "");
//				helper.setText(R.id.txtViewValue,
//						item.getThe_current_value() + "");
				int price = item.getThe_current_value()-1;
				int count = item.getThe_user_holds_a_number_of();
				int allPrice = 0;
				for(int i = 0; i < count; i++){
					allPrice += price;
					price --;
				}
				helper.setText(R.id.txtViewValue, 
						allPrice  + "");
			}
		};
		pullToRefreshListView.setAdapter(adapter);
	}
	
	OnRefreshListener2<ListView> refreshListener = new OnRefreshListener2<ListView>() {

		@Override
		public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
			// 下拉刷新
			String time = DateUtils.formatDateTime(getActivity(),
					System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
							| DateUtils.FORMAT_SHOW_DATE
							| DateUtils.FORMAT_ABBREV_ALL);
			pullToRefreshListView.getLoadingLayoutProxy().setRefreshingLabel(
					"正在刷新");
			pullToRefreshListView.getLoadingLayoutProxy().setPullLabel("下拉刷新");
			pullToRefreshListView.getLoadingLayoutProxy().setReleaseLabel(
					"释放开始刷新");
			refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(
					"最后更新时间:" + time);
			pageIndex = 1;
			// 调用数据

			sendReqMessage(pageIndex);

		}

		@Override
		public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
			// 上拉翻页
			String time = DateUtils.formatDateTime(getActivity(),
					System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
							| DateUtils.FORMAT_SHOW_DATE
							| DateUtils.FORMAT_ABBREV_ALL);
			pullToRefreshListView.getLoadingLayoutProxy().setRefreshingLabel(
					"正在加载");
			pullToRefreshListView.getLoadingLayoutProxy().setPullLabel("上拉翻页");
			pullToRefreshListView.getLoadingLayoutProxy().setReleaseLabel(
					"释放开始加载");
			refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(
					"最后加载时间:" + time);
			// 调用数据
			sendReqMessage(pageIndex);
		}
	};

	private void sendReqMessage(int pageIndex) {
		if (Config.User == null) {
			ToastUtil.show(getActivity(), "无法获取信息");
			return;
		}
		HashMap<String, String> entity = new HashMap<String, String>();

		entity.put("clientID", "" + Config.User.getClientID());
		entity.put("The_page_number", "" + pageIndex);
		ShowProgressDialog("获取信息...");
    	List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
    	addToThreadPool(Config.hold_list, "get start info", params);
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
	public void onInfoReceived(int errcode, HashMap<String, Object> items) {
		super.onInfoReceived(errcode, items);
		pullToRefreshListView.onRefreshComplete();
	}
	
	@Override
	public void onStart() {
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
	
	@SuppressLint("InflateParams") @Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_envelope_hold, null);
		return rootView;
	}
	
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initView();
	}
	

	@Override
	public void RequestSuccessful(String jsonString, int taskType) {
		switch (taskType) {
		case Config.hold_list:
			Entity<List<RedList>> baseEntity = gson.fromJson(jsonString,
					new TypeToken<Entity<List<RedList>>>() {
					}.getType());
			messageItems = baseEntity.getData();
			if (messageItems != null) {
				if (pageIndex == 1) {// 第一页时，先清空数据集
					adapter.clearAll();
				}
				pageIndex++;
				adapter.add(messageItems);
			} else {
				ToastUtil.show(getActivity(), "获取数据失败");
			}
			break;
		}
	}

}
