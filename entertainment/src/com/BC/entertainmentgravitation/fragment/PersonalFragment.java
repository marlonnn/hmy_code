package com.BC.entertainmentgravitation.fragment;

import java.text.SimpleDateFormat;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
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
import com.BC.entertainmentgravitation.LoginActivity_back;
import com.BC.entertainmentgravitation.R;
import com.BC.entertainmentgravitation.entity.EditPersonal;
import com.BC.entertainmentgravitation.entity.Personal;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.summer.fragment.BaseFragment;
import com.summer.view.CircularImage;

public class PersonalFragment extends BaseFragment implements OnClickListener, OnItemClickListener{
	
	private Gson gson;
	private View rootView;
	private SimpleDateFormat format;
	
	private CircularImage portrait;
	
	private Bitmap portraitBmp;
	
	private List<Personal> personals;
	
	private PersonalRecycleAdapter adapter;

	private RecyclerView infoList;
	
	private TextView txtName;
	
	/**
	 * 从后台获取的个人信息
	 */
	private EditPersonal info;
	
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
		rootView = inflater.inflate(R.layout.fragment_personal, null);
		return rootView;
	}
	
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		format = new SimpleDateFormat("yyyyMMddHHmmsssss");
		initView();
	}
	
	private void initView()
	{
		personals = PersonalCache.getInstance().GetPersonalInfos();
		info = InfoCache.getInstance().getPersonalInfo();
		
		portrait = (CircularImage) rootView.findViewById(R.id.cirImagePortrait);
		txtName = (TextView) rootView.findViewById(R.id.txtName);
		rootView.findViewById(R.id.rLayoutExit).setOnClickListener(this);
		
		if (info != null && info.getNickname() != null)
		{
			txtName.setText(info.getNickname());
		}
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

	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
		/**
		 * 修改头像
		 */
		case R.id.cirImagePortrait:
//			showAlertDialog(R.layout.dialog_alert3, R.id.button3, R.id.button1, R.id.button2);
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
			/**
			 * 退出登录
			 */
			case R.id.rLayoutExit:
				intent = new Intent(getActivity(), LoginActivity_back.class);
				startActivity(intent);
				break;
			}
		}
	}

	@Override
	public void RequestSuccessful(String jsonString, int taskType) {
		
	}

}
