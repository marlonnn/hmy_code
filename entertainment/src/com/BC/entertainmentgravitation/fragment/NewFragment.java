package com.BC.entertainmentgravitation.fragment;

import java.util.List;

import org.apache.http.NameValuePair;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.BC.entertainmentgravitation.R;
import com.netease.nim.uikit.common.ui.ptr.PullToRefreshBase;
import com.netease.nim.uikit.common.ui.ptr.PullToRefreshGridView;
import com.netease.nim.uikit.common.ui.ptr.PullToRefreshBase.OnRefreshListener2;
import com.summer.factory.ThreadPoolFactory;
import com.summer.fragment.BaseFragment;
import com.summer.handler.InfoHandler;
import com.summer.task.HttpBaseTask;
import com.summer.treadpool.ThreadPoolConst;
import com.summer.utils.UrlUtil;

/**
 * 明星列表 最新
 * @author wen zhong
 *
 */
public class NewFragment extends BaseFragment{

	private View rootView;
	private PullToRefreshGridView pGridViewNew;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
		rootView = inflater.inflate(R.layout.fragment_star_list, null);
		return rootView;
	}
	
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initView();
	}
	
	private void initView()
	{
		pGridViewNew = (PullToRefreshGridView) rootView.findViewById(R.id.pGridView);
		pGridViewNew.getRefreshableView().setNumColumns(2);
		pGridViewNew.setOnRefreshListener(refreshListener);
	}

	OnRefreshListener2<GridView> refreshListener = new OnRefreshListener2<GridView>() {

		@Override
		public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
			// 下拉刷新
			String time = DateUtils.formatDateTime(getActivity(),
					System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
							| DateUtils.FORMAT_SHOW_DATE
							| DateUtils.FORMAT_ABBREV_ALL);
			pGridViewNew.getLoadingLayoutProxy().setRefreshingLabel(
					"正在刷新");
			pGridViewNew.getLoadingLayoutProxy().setPullLabel("下拉刷新");
			pGridViewNew.getLoadingLayoutProxy().setReleaseLabel(
					"释放开始刷新");
			refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(
					"最后更新时间:" + time);

			sendNewStarListRequest();

		}

		@Override
		public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
			// 上拉翻页
			String time = DateUtils.formatDateTime(getActivity(),
					System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
							| DateUtils.FORMAT_SHOW_DATE
							| DateUtils.FORMAT_ABBREV_ALL);
			pGridViewNew.getLoadingLayoutProxy().setRefreshingLabel(
					"正在加载");
			pGridViewNew.getLoadingLayoutProxy().setPullLabel("上拉翻页");
			pGridViewNew.getLoadingLayoutProxy().setReleaseLabel(
					"释放开始加载");
			refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(
					"最后加载时间:" + time);
			// 调用数据
			sendNewStarListRequest();
		}

	};
	
	private void sendNewStarListRequest()
	{
		
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
		
	}

}
