package com.BC.entertainmentgravitation.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.summer.logger.XLog;
import com.summer.task.HttpBaseTask;
import com.summer.treadpool.ThreadPoolConst;
import com.summer.utils.JsonUtil;
import com.summer.utils.ToastUtil;
import com.summer.utils.UrlUtil;
import com.summer.view.CircularImage;

/**
 * 明星列表 关注
 * @author wen zhong
 *
 */
public class FocusFragment extends BaseFragment{
	
	private Gson gson;
	private View rootView;
	private RelativeLayout rLayout;
	private PullToRefreshGridView pGridViewFocus;
	private CommonAdapter<FHNEntity> adapter;
	private List<FHNEntity> hotList = new ArrayList<>();;
	private int pageIndex = 1;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		gson = new Gson();
		sendFocusStarListRequest();
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
		rootView = inflater.inflate(R.layout.fragment_star, null);
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
		rLayout = (RelativeLayout) rootView.findViewById(R.id.rLayout);
//		rLayout.setVisibility(View.GONE);
		ImageView imgView = (ImageView) rootView.findViewById(R.id.imgViewBtn);
		imgView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
		pGridViewFocus = (PullToRefreshGridView) rootView.findViewById(R.id.pGridViewFocus);
//		pGridViewFocus.setVisibility(View.VISIBLE);
		pGridViewFocus.getRefreshableView().setNumColumns(1);
		pGridViewFocus.getRefreshableView().setVerticalSpacing(10);
		pGridViewFocus.setMode(PullToRefreshBase.Mode.BOTH);
		pGridViewFocus.setOnRefreshListener(refreshListener);
		pGridViewFocus.setAdapter(adapter);
	}
	
	OnRefreshListener2<GridView> refreshListener = new OnRefreshListener2<GridView>() {

		@Override
		public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
			// 下拉刷新
			String time = DateUtils.formatDateTime(getActivity(),
					System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
							| DateUtils.FORMAT_SHOW_DATE
							| DateUtils.FORMAT_ABBREV_ALL);
			pGridViewFocus.getLoadingLayoutProxy().setRefreshingLabel(
					"正在刷新");
			pGridViewFocus.getLoadingLayoutProxy().setPullLabel("下拉刷新");
			pGridViewFocus.getLoadingLayoutProxy().setReleaseLabel(
					"释放开始刷新");
			refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(
					"最后更新时间:" + time);
			pageIndex = 1;
			sendFocusStarListRequest();

		}

		@Override
		public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
			// 上拉翻页
			String time = DateUtils.formatDateTime(getActivity(),
					System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
							| DateUtils.FORMAT_SHOW_DATE
							| DateUtils.FORMAT_ABBREV_ALL);
			pGridViewFocus.getLoadingLayoutProxy().setRefreshingLabel(
					"正在加载");
			pGridViewFocus.getLoadingLayoutProxy().setPullLabel("上拉翻页");
			pGridViewFocus.getLoadingLayoutProxy().setReleaseLabel(
					"释放开始加载");
			refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(
					"最后加载时间:" + time);
			// 调用数据
			sendFocusStarListRequest();
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
	
	private void initAdapter()
	{
		adapter = new CommonAdapter<FHNEntity>(getActivity(), R.layout.fragment_focus_item, hotList) {

			public void setTag(ViewHolder viewHolder, final FHNEntity item)
			{
				viewHolder.getView(R.id.imgViewPortrait).setTag(R.id.tag_focus, item);
				viewHolder.getView(R.id.cImagePortrait).setTag(R.id.tag_cfocus, item);
			}
			
			@Override
			public void convert(
					ViewHolder viewHolder,
					final FHNEntity item, int position) {
				try {
					CircularImage cPortrait = (CircularImage) viewHolder.getView(R.id.cImagePortrait);
					TextView Name = (TextView)viewHolder.getView(R.id.txtViewName);
					TextView Location = (TextView)viewHolder.getView(R.id.txtViewLocation);
					TextView People = (TextView)viewHolder.getView(R.id.txtViewPeople);
					ImageView Status = (ImageView)viewHolder.getView(R.id.imgViewStatus);
					ImageView imgPortrait = (ImageView)viewHolder.getView(R.id.imgViewPortrait);
					if (item != null)
					{
						Name.setText(isNullOrEmpty(item.getStar_names()) ? "未知" : item.getStar_names());
						Location.setText(isNullOrEmpty(item.getRegion()) ? "未知" : item.getRegion());
						People.setText(isNullOrEmpty(item.getPeoples()) ? "0" : item.getPeoples());
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
						cPortrait.setOnTouchListener(new OnTouchListener() {
							
							@Override
							public boolean onTouch(View v, MotionEvent event) {
								switch (event.getAction()) {
								/**
								 * 按下
								 * */
								case MotionEvent.ACTION_DOWN:
								/**
								 * 移动
								 * */
								case MotionEvent.ACTION_MOVE:
								
									break;
								// 拿起
								case MotionEvent.ACTION_UP:
									try {
										FHNEntity entity = (FHNEntity)v.getTag(R.id.tag_cfocus);
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
									break;
								default:
									break;
								}

								return false;
							}
						});
						
						imgPortrait.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								try {
									FHNEntity entity = (FHNEntity)v.getTag(R.id.tag_focus);
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
						Glide.with(getActivity()).load(item.getHead_portrait())
						.centerCrop()
						.diskCacheStrategy(DiskCacheStrategy.ALL)
						.placeholder(R.drawable.home_image).into(cPortrait);
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
	private void sendFocusStarListRequest() {
		if (Config.User == null) {
			ToastUtil.show(getActivity(), "无法获取信息");
			return;
		}
		HashMap<String, String> entity = new HashMap<String, String>();

		entity.put("clientID", Config.User.getClientID());
		entity.put("The_page_number", "" + pageIndex);
		entity.put("type", "1");

		
		ShowProgressDialog("获取热门用户基本信息...");		
		List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
		addToThreadPool(Config.stat_list, "send search request", params);
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
	
	private void watchLiveVideoRequest(String starName)
	{
    	HashMap<String, String> entity = new HashMap<String, String>();
    	entity.put("username", starName);
		List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
		ShowProgressDialog("正在进入直播间，请稍等...");
		addToThreadPool(Config.query_video, "send watch video request", params);
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
	public void onInfoReceived(int errorCode, HashMap<String, Object> items) {
		RemoveProgressDialog();
        if (errorCode == 0)
        {
            String jsonString = (String) items.get("content");
            if (jsonString != null)
            {
                JSONObject object;
                try {
                    object = new JSONObject(jsonString);
                    String msg = object.optString("msg");
                    int code = object.optInt("status", -1);
                    int taskType = (Integer) items.get("taskType");
                    XLog.i("code: " + errorCode);
                    XLog.i("taskType: " + taskType);
                    if (code == 0)
                    {
                        RequestSuccessful(jsonString, taskType);
                    }
                    else
                    {
                        RequestFailed(code, msg, taskType);
                    }
                } catch (JSONException e) {
                    //parse error
                    XLog.e(e);
                    e.printStackTrace();
                    RequestFailed(-1, "Json Parse Error", -1);
                }
            }
        }
		pGridViewFocus.onRefreshComplete();
	}
	
	@Override
	public void RequestFailed(int errcode, String message, int taskType) {
		super.RequestFailed(errcode, message, taskType);
		rLayout.setVisibility(View.VISIBLE);
		pGridViewFocus.setVisibility(View.GONE);
	}

	@Override
	public void RequestSuccessful(String jsonString, int taskType) {
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
			hotList = baseEntity.getData();
			if (hotList != null && hotList.size() > 0) {
				rLayout.setVisibility(View.GONE);
				pGridViewFocus.setVisibility(View.VISIBLE);
				addRanking();
			} else {
				ToastUtil.show(getActivity(), "没有更多数据了");
			}
			break;
		}
	}

}
