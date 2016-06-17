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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.BC.entertainment.cache.InfoCache;
import com.BC.entertainmentgravitation.PersonalHomeActivity;
import com.BC.entertainmentgravitation.PullActivity;
import com.BC.entertainmentgravitation.R;
import com.BC.entertainmentgravitation.entity.FHNEntity;
import com.BC.entertainmentgravitation.entity.Member;
import com.BC.entertainmentgravitation.entity.StarLiveVideoInfo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.summer.ptr.PullToRefreshBase;
import com.summer.ptr.PullToRefreshGridView;
import com.summer.ptr.PullToRefreshBase.OnRefreshListener2;
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
 * 明星列表 最新
 * @author wen zhong
 *
 */
public class NewFragment extends BaseFragment{

	private View rootView;
	private int pageIndex = 1;
	private PullToRefreshGridView pGridViewNew;
	
	private CommonAdapter<FHNEntity> adapter;
	private List<FHNEntity> newList = new ArrayList<>();;
	private Gson gson;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		gson = new Gson();
		sendNewStarListRequest();
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
		pGridViewNew = (PullToRefreshGridView) rootView.findViewById(R.id.pGridView);
		pGridViewNew.getRefreshableView().setNumColumns(2);
		pGridViewNew.getRefreshableView().setHorizontalSpacing(10);
		pGridViewNew.getRefreshableView().setVerticalSpacing(10);
		pGridViewNew.setMode(PullToRefreshBase.Mode.BOTH);
		pGridViewNew.setOnRefreshListener(refreshListener);

		pGridViewNew.setAdapter(adapter);
	}
	
	private void initAdapter()
	{
		adapter = new CommonAdapter<FHNEntity>(getActivity(), R.layout.fragment_new_item, newList) {

			public void setTag(ViewHolder viewHolder, final FHNEntity item)
			{
				viewHolder.getView(R.id.imgViewPortrait).setTag(R.id.tag_new, item);
			}
			
			@Override
			public void convert(
					ViewHolder viewHolder,
					final FHNEntity item, int position) {
				try {
					ImageView imgPortrait = (ImageView)viewHolder.getView(R.id.imgViewPortrait);
					TextView Name = (TextView)viewHolder.getView(R.id.txtViewName);
					TextView Location = (TextView)viewHolder.getView(R.id.txtViewLocation);
					TextView job = (TextView)viewHolder.getView(R.id.txtViewJob);
					TextView index = (TextView)viewHolder.getView(R.id.txtViewIndex);
					if (item != null)
					{
						Name.setText(isNullOrEmpty(item.getStar_names()) ? "未知" : item.getStar_names());
						job.setText(isNullOrEmpty(item.getCareer()) ? "未知" : item.getCareer());
						index.setText(isNullOrEmpty(item.getBid()) ? "未知" : item.getBid());
						Location.setText(isNullOrEmpty(item.getRegion()) ? "未知" : item.getRegion());
						
						Glide.with(getActivity()).load(item.getHead_portrait())
						.centerCrop()
						.diskCacheStrategy(DiskCacheStrategy.ALL)
						.placeholder(R.drawable.home_image).into(imgPortrait);
						imgPortrait.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								try {
									FHNEntity entity = (FHNEntity)v.getTag(R.id.tag_new);
									if (entity != null)
									{
										if (entity.getVstatus() != null)
										{
											if (entity.getVstatus().contains("0"))
											{
												try {
													if (entity.getUsername() != null)
													{
														InfoCache.getInstance().setLiveStar(entity);
														watchLiveVideoRequest(entity.getUsername());
													}

												} catch (Exception e) {
													e.printStackTrace();
													ToastUtil.show(getActivity(), "服务器异常，请稍后再试");
												}
											}
											else
											{
												ToastUtil.show(getActivity(), "主播不在直播间，请稍后再试");
												sendBaseInfoRequest(entity);
											}
										}
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
			pageIndex = 1;
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
	
	/**
	 * 获取信息
	 */
	private void sendNewStarListRequest() {
		if (Config.User == null) {
			ToastUtil.show(getActivity(), "无法获取信息");
			return;
		}
		HashMap<String, String> entity = new HashMap<String, String>();

		entity.put("clientID", Config.User.getClientID());
		entity.put("The_page_number", "" + pageIndex);
		entity.put("type", "3");

		
		ShowProgressDialog("获取热门用户基本信息...");		
		List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
		addToThreadPool(Config.stat_list, "send search request", params);
	}
	
	private void watchLiveVideoRequest(String starName)
	{
    	HashMap<String, String> entity = new HashMap<String, String>();
    	entity.put("username", starName);
		List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
		ShowProgressDialog("正在进入直播间，请稍等...");
		addToThreadPool(Config.query_video, "send watch video request", params);
	}
	
	private void sendBaseInfoRequest(FHNEntity fhnEntity)
	{
		if (fhnEntity != null && fhnEntity.getUsername() != null)
		{
			HashMap<String, String> entity = new HashMap<String, String>();
			entity.put("username", fhnEntity.getUsername());
			ShowProgressDialog("获取热门用户基本信息...");		
			List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
			addToThreadPool(Config.member_in, "send search request", params);
		}
	}
	
    private void addToThreadPool(int taskType, String Tag, List<NameValuePair> params)
    {
    	HttpBaseTask httpTask = new HttpBaseTask(ThreadPoolConst.THREAD_TYPE_FILE_HTTP, Tag, params, UrlUtil.GetUrl(taskType));
    	httpTask.setTaskType(taskType);
    	InfoHandler handler = new InfoHandler(this);
    	httpTask.setInfoHandler(handler);
    	ThreadPoolFactory.getThreadPoolManager().addTask(httpTask);
    }
    
	private void addItem() {
		if (newList == null) {
			return;
		}
		if (pageIndex == 1) {// 第一页时，先清空数据集
			adapter.clearAll();
		}
		pageIndex++;
		adapter.add(newList);
	}
	
	@Override
	public void onInfoReceived(int errcode, HashMap<String, Object> items) {
		// TODO Auto-generated method stub
		super.onInfoReceived(errcode, items);
		pGridViewNew.onRefreshComplete();
	}
	
	@Override
	public void RequestSuccessful(int status, String jsonString, int taskType) {
		switch(taskType)
		{
		case Config.member_in:
			Entity<Member> entity = gson.fromJson(jsonString,
					new TypeToken<Entity<Member>>() {
					}.getType());
			if (entity != null && entity.getData() != null)
			{
				Intent intent = new Intent();
				intent.setClass(getActivity(), PersonalHomeActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("member", entity.getData());
				intent.putExtras(bundle);
				startActivity(intent);
			}
			break;
		
		case Config.query_video:
			Entity<StarLiveVideoInfo> watchVideoEntity = gson.fromJson(jsonString,
					new TypeToken<Entity<StarLiveVideoInfo>>() {
					}.getType());
			StarLiveVideoInfo watchVideo = watchVideoEntity.getData();
			if (watchVideo != null)
			{
				Intent intent = new Intent(getActivity(), PullActivity.class);
				Bundle b = new Bundle();
				b.putSerializable("liveInfo", watchVideo);
				intent.putExtras(b);
				startActivity(intent); 
			}
			break;
		case Config.stat_list:
			Entity<List<FHNEntity>> baseEntity = gson.fromJson(jsonString,
					new TypeToken<Entity<List<FHNEntity>>>() {
					}.getType());
			newList = baseEntity.getData();
			if (newList != null && newList.size() > 0) {
				addItem();
			} else {
				ToastUtil.show(getActivity(), "没有更多数据了");
			}
			break;
		}
	}

}
