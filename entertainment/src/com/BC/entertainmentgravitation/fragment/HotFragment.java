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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.BC.entertainment.adapter.HotAdapter;
import com.BC.entertainmentgravitation.DetailsActivity;
import com.BC.entertainmentgravitation.R;
import com.BC.entertainmentgravitation.entity.FHNEntity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.netease.nim.uikit.common.ui.ptr.PullToRefreshBase;
import com.netease.nim.uikit.common.ui.ptr.PullToRefreshGridView;
import com.netease.nim.uikit.common.ui.ptr.PullToRefreshBase.OnRefreshListener2;
import com.summer.adapter.CommonAdapter;
import com.summer.adapter.CommonAdapter.ViewHolder;
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
import com.summer.view.CircularImage;

/**
 * 明星列表 热门
 * @author wen zhong
 *
 */
public class HotFragment extends BaseFragment{
	
	private View rootView;
	private int pageIndex = 1;
	private PullToRefreshGridView pGridViewHot;
	private CommonAdapter<FHNEntity> adapter;
//	private HotAdapter adapter;
	private List<FHNEntity> hotList = new ArrayList<>();;
	private Gson gson;
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		gson = new Gson();
		sendHotStarListRequest();
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
		initAdapter();
		initView();
	}
	
	private void initView()
	{
		pGridViewHot = (PullToRefreshGridView) rootView.findViewById(R.id.pGridView);
		pGridViewHot.getRefreshableView().setNumColumns(1);
		pGridViewHot.getRefreshableView().setVerticalSpacing(10);
		pGridViewHot.setMode(PullToRefreshBase.Mode.BOTH);
		pGridViewHot.setOnRefreshListener(refreshListener);
		pGridViewHot.setOnItemClickListener(new OnItemClickListener() {

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
		pGridViewHot.setAdapter(adapter);
	}

	OnRefreshListener2<GridView> refreshListener = new OnRefreshListener2<GridView>() {

		@Override
		public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
			// 下拉刷新
			String time = DateUtils.formatDateTime(getActivity(),
					System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
							| DateUtils.FORMAT_SHOW_DATE
							| DateUtils.FORMAT_ABBREV_ALL);
			pGridViewHot.getLoadingLayoutProxy().setRefreshingLabel(
					"正在刷新");
			pGridViewHot.getLoadingLayoutProxy().setPullLabel("下拉刷新");
			pGridViewHot.getLoadingLayoutProxy().setReleaseLabel(
					"释放开始刷新");
			refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(
					"最后更新时间:" + time);
			pageIndex = 1;
			sendHotStarListRequest();

		}

		@Override
		public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
			// 上拉翻页
			String time = DateUtils.formatDateTime(getActivity(),
					System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
							| DateUtils.FORMAT_SHOW_DATE
							| DateUtils.FORMAT_ABBREV_ALL);
			pGridViewHot.getLoadingLayoutProxy().setRefreshingLabel(
					"正在加载");
			pGridViewHot.getLoadingLayoutProxy().setPullLabel("上拉翻页");
			pGridViewHot.getLoadingLayoutProxy().setReleaseLabel(
					"释放开始加载");
			refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(
					"最后加载时间:" + time);
			// 调用数据
			sendHotStarListRequest();
		}

	};
	
	
	private void initAdapter()
	{
//		adapter = new HotAdapter(getActivity(), hotList);
		adapter = new CommonAdapter<FHNEntity>(getActivity(), R.layout.fragment_hot_item, hotList) {
			
			public void setTag(ViewHolder viewHolder, final FHNEntity item)
			{
				viewHolder.getView(R.id.imgViewPortrait).setTag(R.id.tag_portrait, item);
			}

			@Override
			public void convert(
					ViewHolder viewHolder,
					final FHNEntity item) {
				try {
					CircularImage cPortrait = (CircularImage) viewHolder.getView(R.id.cImagePortrait);
					TextView Name = (TextView)viewHolder.getView(R.id.txtViewName);
					TextView Location = (TextView)viewHolder.getView(R.id.txtViewLocation);
					TextView People = (TextView)viewHolder.getView(R.id.txtViewPeople);
					ImageView Status = (ImageView)viewHolder.getView(R.id.imgViewStatus);
					ImageView imgPortrait = (ImageView)viewHolder.getView(R.id.imgViewPortrait);
					if (item != null)
					{
						Name.setText(item.getStar_names());
						Location.setText(item.getRegion());
						if (item.getPeoples() != null && !item.getPeoples().isEmpty())
						{
							People.setText(item.getPeoples());
						}
						if (item.getVstatus() != null && !item.getVstatus().isEmpty() && item.getVstatus().contains("0"))
						{
							Status.setVisibility(View.VISIBLE);
						}
						else
						{
							Status.setVisibility(View.GONE);
						}
						Glide.with(getActivity()).load(item.getPortrait())
						.centerCrop()
						.diskCacheStrategy(DiskCacheStrategy.ALL)
						.placeholder(R.drawable.home_image).into(imgPortrait);
						imgPortrait.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								try {
									FHNEntity entity = (FHNEntity)v.getTag(R.id.tag_portrait);
									if (entity != null)
									{
										Intent i = new Intent(getActivity(), DetailsActivity.class);
										i.putExtra("userID", entity.getStar_ID());
										startActivity(i);
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
//								Intent i = new Intent(getActivity(), DetailsActivity.class);
//								i.putExtra("userID", item.getStar_ID());
//								startActivity(i);
							}
						});
						Glide.with(getActivity()).load(item.getHead_portrait())
						.centerCrop()
						.diskCacheStrategy(DiskCacheStrategy.ALL)
						.placeholder(R.drawable.avatar_def).into(cPortrait);
						cPortrait.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								
							}
						});
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
	}
	
	/**
	 * 获取信息
	 */
	private void sendHotStarListRequest() {
		if (Config.User == null) {
			ToastUtil.show(getActivity(), "无法获取信息");
			return;
		}
		HashMap<String, String> entity = new HashMap<String, String>();

		entity.put("clientID", Config.User.getClientID());
		entity.put("The_page_number", "" + pageIndex);
		entity.put("type", "2");

		
		ShowProgressDialog("获取热门用户基本信息...");		
		List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
		addToThreadPool(Config.stat_list, "send search request", params);
	}
	
    private void addToThreadPool(int taskType, String Tag, List<NameValuePair> params)
    {
    	HttpBaseTask httpTask = new HttpBaseTask(ThreadPoolConst.THREAD_TYPE_FILE_HTTP, Tag, params, UrlUtil.GetUrl(taskType));
    	httpTask.setTaskType(taskType);
    	InfoHandler handler = new InfoHandler(this);
    	httpTask.setInfoHandler(handler);
    	ThreadPoolFactory.getThreadPoolManager().addTask(httpTask);
    }
    
	private void addRanking() {
		if (hotList == null) {
			return;
		}
		if (pageIndex == 1) {// 第一页时，先清空数据集
			adapter.clearAll();
		}
		pageIndex++;
		adapter.add(hotList);
	}
    
	@Override
	public void onInfoReceived(int errcode, HashMap<String, Object> items) {
		// TODO Auto-generated method stub
		super.onInfoReceived(errcode, items);
		pGridViewHot.onRefreshComplete();
	}
	
	@Override
	public void RequestSuccessful(String jsonString, int taskType) {
		switch(taskType)
		{
		case Config.stat_list:
			Entity<List<FHNEntity>> baseEntity = gson.fromJson(jsonString,
					new TypeToken<Entity<List<FHNEntity>>>() {
					}.getType());
			hotList = baseEntity.getData();
			if (hotList != null && hotList.size() > 0) {
				addRanking();
			} else {
				ToastUtil.show(getActivity(), "没有更多数据了");
			}
			break;
		}
	}

}

