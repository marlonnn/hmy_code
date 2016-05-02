package com.BC.entertainmentgravitation.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.BC.entertainmentgravitation.R;
import com.BC.entertainmentgravitation.entity.RedAList;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.netease.nim.uikit.common.ui.ptr.PullToRefreshBase;
import com.netease.nim.uikit.common.ui.ptr.PullToRefreshBase.OnRefreshListener2;
import com.netease.nim.uikit.common.ui.ptr.PullToRefreshListView;
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

public class RedALiatFragment extends BaseFragment {
	View contentView;
	PullToRefreshListView pullToRefreshListView1;

	List<RedAList> messageItems;
	private CommonAdapter<RedAList> adapter;
	private int pageIndex = 1;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		contentView = inflater.inflate(R.layout.fragment_red_a_list, null);
		return contentView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		init();
		sendReqMessage(1);
		super.onViewCreated(view, savedInstanceState);
	}

	private void init() {
		pullToRefreshListView1 = (PullToRefreshListView) contentView
				.findViewById(R.id.pullToRefreshListView1);
		pullToRefreshListView1.setOnRefreshListener(refreshListener);
		adapter = new CommonAdapter<RedAList>(getActivity(),
				R.layout.item_list_red_a_list, new ArrayList<RedAList>()) {

			@Override
			public void convert(ViewHolder helper, final RedAList item) {
				helper.setText(R.id.The_publishers_name,
						item.getThe_publishers_name() + "");
				helper.setText(R.id.Grants_of_number,
						item.getGrants_of_number() + "");

				TextView textView = (TextView) helper.getView(R.id.type);

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

				helper.setText(R.id.The_donor, item.getThe_donor() + "");
				helper.setText(R.id.time, item.getTime() + "");
				// 服务好客户，担保
				// helper.setText(R.id.describe, item.getDescribe());
				// helper.getView(R.id.If_there_is_a_picture).setVisibility(
				// "".equals(item.getIf_there_is_a_picture()) ? View.GONE
				// : View.VISIBLE);
				//
				// ImageView imageView = helper.getView(R.id.Head_portrait);
				// Glide.with(getActivity()).load(item.getHead_portrait())
				// .centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL)
				// .placeholder(R.drawable.home_image)
				// .into(imageView);
			}
		};
		pullToRefreshListView1.setAdapter(adapter);
		// pullToRefreshListView1.setOnItemClickListener(this);
	}

	private void sendReqMessage(int pageIndex) {
		// TODO Auto-generated method stub
		if (Config.User == null) {
			ToastUtil.show(getActivity(), "无法获取信息");
			return;
		}
		HashMap<String, String> entity = new HashMap<String, String>();

		entity.put("clientID", "" + Config.User.getClientID());
		entity.put("The_page_number", "" + pageIndex);
		ShowProgressDialog("获取信息...");
		
		List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
		addToThreadPool(Config.a_list, "send watch video request", params);
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
		pullToRefreshListView1.onRefreshComplete();
	}

	@Override
	public void RequestSuccessful(String jsonString, int taskType) {
		// TODO Auto-generated method stub
		Gson gson = new Gson();
		switch (taskType) {
		case Config.a_list:
			Entity<List<RedAList>> baseEntity5 = gson.fromJson(jsonString,
					new TypeToken<Entity<List<RedAList>>>() {
					}.getType());
			messageItems = baseEntity5.getData();
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

	OnRefreshListener2<ListView> refreshListener = new OnRefreshListener2<ListView>() {

		@Override
		public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
			// 下拉刷新
			String time = DateUtils.formatDateTime(getActivity(),
					System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
							| DateUtils.FORMAT_SHOW_DATE
							| DateUtils.FORMAT_ABBREV_ALL);
			pullToRefreshListView1.getLoadingLayoutProxy().setRefreshingLabel(
					"正在刷新");
			pullToRefreshListView1.getLoadingLayoutProxy().setPullLabel("下拉刷新");
			pullToRefreshListView1.getLoadingLayoutProxy().setReleaseLabel(
					"释放开始刷新");
			refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(
					"最后更新时间:" + time);
			pageIndex = 1;
			// 调用数据

			// sendReq(pageIndex);
			sendReqMessage(pageIndex);

		}

		@Override
		public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
			// 上拉翻页
			String time = DateUtils.formatDateTime(getActivity(),
					System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
							| DateUtils.FORMAT_SHOW_DATE
							| DateUtils.FORMAT_ABBREV_ALL);
			pullToRefreshListView1.getLoadingLayoutProxy().setRefreshingLabel(
					"正在加载");
			pullToRefreshListView1.getLoadingLayoutProxy().setPullLabel("上拉翻页");
			pullToRefreshListView1.getLoadingLayoutProxy().setReleaseLabel(
					"释放开始加载");
			refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(
					"最后加载时间:" + time);
			// 调用数据
			sendReqMessage(pageIndex);
		}
	};

}

