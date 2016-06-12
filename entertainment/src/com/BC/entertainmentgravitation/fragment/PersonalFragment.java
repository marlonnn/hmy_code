package com.BC.entertainmentgravitation.fragment;

import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.BC.entertainment.adapter.PersonalRecycleAdapter;
import com.BC.entertainment.adapter.PersonalRecycleAdapter.OnItemClickListener;
import com.BC.entertainment.cache.InfoCache;
import com.BC.entertainment.cache.PersonalCache;
import com.BC.entertainmentgravitation.AboutActivity;
import com.BC.entertainmentgravitation.AlbumActivity;
import com.BC.entertainmentgravitation.BaseInfoActivity;
import com.BC.entertainmentgravitation.BrokerActivity;
import com.BC.entertainmentgravitation.CareerActivity;
import com.BC.entertainmentgravitation.ChargeActivity;
import com.BC.entertainmentgravitation.EnvelopeActivity;
import com.BC.entertainmentgravitation.FeedbackActivity;
import com.BC.entertainmentgravitation.IncomeActivity;
import com.BC.entertainmentgravitation.LoginActivity;
import com.BC.entertainmentgravitation.PersonalHomeActivity;
import com.BC.entertainmentgravitation.R;
import com.BC.entertainmentgravitation.entity.EditPersonal;
import com.BC.entertainmentgravitation.entity.Member;
import com.BC.entertainmentgravitation.entity.Personal;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.summer.config.Config;
import com.summer.factory.ThreadPoolFactory;
import com.summer.fragment.BaseFragment;
import com.summer.handler.InfoHandler;
import com.summer.json.Entity;
import com.summer.task.HttpBaseTask;
import com.summer.treadpool.ThreadPoolConst;
import com.summer.utils.JsonUtil;
import com.summer.utils.UrlUtil;
import com.summer.view.CircularImage;

/**
 * 首页 个人中心
 * @author zhongwen
 *
 */
public class PersonalFragment extends BaseFragment implements OnClickListener, OnItemClickListener{
	
	private View rootView;
	
	private CircularImage portrait;
	
	private List<Personal> personals;
	
	private PersonalRecycleAdapter adapter;

	private RecyclerView infoList;
	
	private TextView txtName;
	
	private Gson gson;
	
	/**
	 * 从后台获取的个人信息
	 */
	private EditPersonal info;
	private Activity ativity;

//	private TextView txtViewTopFocus;
//
//	private TextView txtViewTopFans;
	
	@Override
	public void onAttach(Activity activity) {
		this.ativity = activity;
		super.onAttach(activity);
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		gson = new Gson();
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
		rootView = inflater.inflate(R.layout.fragment_personal, null);
		return rootView;
	}
	
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initView();
	}
	
	private void sendBaseInfoRequest()
	{
		HashMap<String, String> entity = new HashMap<String, String>();
		entity.put("username", Config.User.getUserName());
//		ShowProgressDialog("获取热门用户基本信息...");		
		List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
		addToThreadPool(Config.member_in, "send search request", params);
	}
	
    private void addToThreadPool(int taskType, String Tag, List<NameValuePair> params)
    {
    	HttpBaseTask httpTask = new HttpBaseTask(ThreadPoolConst.THREAD_TYPE_FILE_HTTP, Tag, params, UrlUtil.GetUrl(taskType));
    	httpTask.setTaskType(taskType);
    	InfoHandler handler = new InfoHandler(this);
    	httpTask.setInfoHandler(handler);
    	ThreadPoolFactory.getThreadPoolManager().addTask(httpTask);
    }
    
	private void initView()
	{
		personals = PersonalCache.getInstance().GetPersonalInfos();
		info = InfoCache.getInstance().getPersonalInfo();
		
		portrait = (CircularImage) rootView.findViewById(R.id.cirImagePortrait);
		portrait.setOnClickListener(this);
		txtName = (TextView) rootView.findViewById(R.id.txtName);
//		txtViewTopFocus = (TextView) rootView.findViewById(R.id.txtViewTopFocus);
//		txtViewTopFans = (TextView) rootView.findViewById(R.id.txtViewTopFans);
		RelativeLayout r = (RelativeLayout) rootView.findViewById(R.id.rLayoutExit);
		r.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Config.manualExit = true;
				Intent intent = new Intent(getActivity(), LoginActivity.class);
				startActivity(intent);
				if (ativity != null)
				{
					ativity.finish();
				}
			}
		});
		
		if (info != null && info.getNickname() != null)
		{
			txtName.setText(info.getNickname());
		}
//		txtViewTopFocus.setText(isNullOrEmpty(info.get) ? "" : )
		Glide.with(this)
		.load(InfoCache.getInstance().getPersonalInfo().getHead_portrait())
		.centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL)
		.placeholder(R.drawable.avatar_def).into(portrait);
		portrait.setOnClickListener(this);
		
		infoList = (RecyclerView) rootView.findViewById(R.id.listViewInfo);
		
		adapter = new PersonalRecycleAdapter(getActivity(), personals);
		
        adapter.notifyDataSetChanged();
        adapter.setmOnItemClickListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        infoList.setVerticalScrollBarEnabled(true);
        infoList.setLayoutManager(linearLayoutManager);
        
        infoList.setItemAnimator(new DefaultItemAnimator());//more的动画效果
        
        infoList.setAdapter(adapter);
		
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

	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
		/**
		 * 修改头像
		 */
		case R.id.cirImagePortrait:
			sendBaseInfoRequest();
			break;
		}
	}
	
	@Override
	public void onItemClick(View view, int position) {
		Personal personal = (Personal)view.getTag();
		Intent intent = null;
		if(personal != null)
		{
			switch(personal.getResource())
			{
			/**
			 * 基本信息
			 */
			case R.drawable.activity_personal_info:
				intent = new Intent(getActivity(), BaseInfoActivity.class);
				startActivity(intent);
				break;
			/**
			 * 相册管理
			 */
			case R.drawable.activity_personal_album:
				intent = new Intent(getActivity(), AlbumActivity.class);
				startActivity(intent);
				break;
			/**
			 * 演艺经历
			 */
			case R.drawable.activity_personal_career:
				intent = new Intent(getActivity(), CareerActivity.class);
				startActivity(intent);
				break;
			/**
			 * 我的经纪
			 */
			case R.drawable.activity_personal_broker:
				intent = new Intent(getActivity(), BrokerActivity.class);
				startActivity(intent);
				break;
			/**
			 * 我的收益
			 */
			case R.drawable.activity_personal_income:
				intent = new Intent(getActivity(), IncomeActivity.class);
				startActivity(intent);
				break;
			/**
			 * 我的娛币
			 */
			case R.drawable.activity_personal_yubi:
				intent = new Intent(getActivity(), ChargeActivity.class);
				startActivity(intent);
				break;
			/**
			 * 我的红包
			 */
			case R.drawable.activity_personal_envelope:
				intent = new Intent(getActivity(), EnvelopeActivity.class);
				startActivity(intent);
				break;
			/**
			 * 我的关注
			 */
			case R.drawable.activity_home_bottom_focus:
				sendBaseInfoRequest();
				break;
			/**
			 * 意见反馈
			 */
			case R.drawable.activity_personal_feedback:
				intent = new Intent(getActivity(), FeedbackActivity.class);
				startActivity(intent);
				break;
			/**
			 * 关于我们
			 */
			case R.drawable.activity_personal_about:
				intent = new Intent(getActivity(), AboutActivity.class);
				startActivity(intent);
				break;
			}
		}
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
		}
	}

}
