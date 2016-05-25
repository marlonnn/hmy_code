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
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;

import com.BC.entertainmentgravitation.AuthoritativeInformation;
import com.BC.entertainmentgravitation.BrowserAcitvity;
import com.BC.entertainmentgravitation.DetailsActivity;
import com.BC.entertainmentgravitation.R;
import com.BC.entertainmentgravitation.entity.Activitys;
import com.BC.entertainmentgravitation.entity.FHNEntity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.netease.nim.uikit.common.ui.ptr.PullToRefreshBase;
import com.netease.nim.uikit.common.ui.ptr.PullToRefreshGridView;
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
import com.summer.utils.UrlUtil;

/**
 * 发现页
 * @author wen zhong
 *
 */
public class FoundFragment extends BaseFragment implements OnClickListener{
	
	private View rootView;
	private PullToRefreshGridView pGridViewFound;
	private List<Activitys> activityList = new ArrayList<>();
	private CommonAdapter<Activitys> adapter;
	private Gson gson;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		gson = new Gson();
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
		rootView = inflater.inflate(R.layout.fragment_found, null);
		return rootView;
	}
	
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initAdapter();
		initView();
	}
	
	private void initAdapter()
	{
//		adapter = new HotAdapter(getActivity(), hotList);
		adapter = new CommonAdapter<Activitys>(getActivity(), R.layout.fragment_found_item, activityList) {
			
			public void setTag(ViewHolder viewHolder, final Activity item)
			{
				viewHolder.getView(R.id.imgViewFound).setTag(R.id.tag_found, item);
			}

			@Override
			public void convert(
					ViewHolder viewHolder,
					final Activitys item) {
				try {
					ImageView imgViewActvity = (ImageView)viewHolder.getView(R.id.imgViewFound);
					if (item != null)
					{
						Glide.with(getActivity()).load(item.getPicture_address())
						.centerCrop()
						.diskCacheStrategy(DiskCacheStrategy.ALL)
						.placeholder(R.drawable.home_image).into(imgViewActvity);
						imgViewActvity.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								try {
									Activitys entity = (Activitys)v.getTag(R.id.tag_found);
									if (entity != null)
									{
										Intent intent = new Intent(v.getContext(),
												BrowserAcitvity.class);
										intent.putExtra("url", entity.getThe_link_address());
										startActivity(intent);
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						});
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
	}
	
	private void initView()
	{
		pGridViewFound = (PullToRefreshGridView) rootView.findViewById(R.id.pGridViewFound);
		pGridViewFound.getRefreshableView().setNumColumns(1);
		pGridViewFound.getRefreshableView().setVerticalSpacing(10);
		pGridViewFound.setMode(PullToRefreshBase.Mode.BOTH);
		pGridViewFound.setOnRefreshListener(refreshListener);
		pGridViewFound.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				try {
					FHNEntity entity = (FHNEntity)view.getTag();
					if (entity != null)
					{
						Intent i = new Intent(getActivity(), DetailsActivity.class);
						i.putExtra("userID", entity.getStar_ID());
						startActivity(i);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		pGridViewFound.setAdapter(adapter);
	}

	OnRefreshListener2<GridView> refreshListener = new OnRefreshListener2<GridView>() {

		@Override
		public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
			// 下拉刷新
			String time = DateUtils.formatDateTime(getActivity(),
					System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
							| DateUtils.FORMAT_SHOW_DATE
							| DateUtils.FORMAT_ABBREV_ALL);
			pGridViewFound.getLoadingLayoutProxy().setRefreshingLabel(
					"正在刷新");
			pGridViewFound.getLoadingLayoutProxy().setPullLabel("下拉刷新");
			pGridViewFound.getLoadingLayoutProxy().setReleaseLabel(
					"释放开始刷新");
			refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(
					"最后更新时间:" + time);
			sendActivityRequest();

		}

		@Override
		public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
			// 上拉翻页
			String time = DateUtils.formatDateTime(getActivity(),
					System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
							| DateUtils.FORMAT_SHOW_DATE
							| DateUtils.FORMAT_ABBREV_ALL);
			pGridViewFound.getLoadingLayoutProxy().setRefreshingLabel(
					"正在加载");
			pGridViewFound.getLoadingLayoutProxy().setPullLabel("上拉翻页");
			pGridViewFound.getLoadingLayoutProxy().setReleaseLabel(
					"释放开始加载");
			refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(
					"最后加载时间:" + time);
			// 调用数据
			sendActivityRequest();
		}

	};
	
	/**
	 * 获取广告及公告信息
	 */
	private void sendActivityRequest() {
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
    
	private void addActivity() {
		if (activityList == null) {
			return;
		}
		adapter.clearAll();
		adapter.add(activityList);
	}

	@Override
	public void onClick(View v) {
		
	}

	@Override
	public void RequestSuccessful(String jsonString, int taskType) {
		gson = new Gson();
		switch (taskType) {
		case Config.activities:

			Entity<AuthoritativeInformation> baseEntity = gson.fromJson(
					jsonString,
					new TypeToken<Entity<AuthoritativeInformation>>() {
					}.getType());
			if (baseEntity.getData() != null && baseEntity.getData().getActivity() != null)
			{
				activityList = baseEntity.getData().getActivity();
				addActivity();
			}

			break;
		}
	}

}
