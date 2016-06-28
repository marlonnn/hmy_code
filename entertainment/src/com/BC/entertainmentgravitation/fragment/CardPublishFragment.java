package com.BC.entertainmentgravitation.fragment;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.BC.entertainmentgravitation.R;
import com.BC.entertainmentgravitation.RightsCardDetailActivity;
import com.BC.entertainmentgravitation.entity.RightCard;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.summer.adapter.CommonAdapter;
import com.summer.adapter.CommonAdapter.ViewHolder;
import com.summer.config.Config;
import com.summer.factory.ThreadPoolFactory;
import com.summer.fragment.BaseFragment;
import com.summer.handler.InfoHandler;
import com.summer.json.Entity;
import com.summer.logger.XLog;
import com.summer.ptr.PullToRefreshBase;
import com.summer.ptr.PullToRefreshGridView;
import com.summer.ptr.PullToRefreshBase.OnRefreshListener2;
import com.summer.task.HttpBaseTask;
import com.summer.treadpool.ThreadPoolConst;
import com.summer.utils.JsonUtil;
import com.summer.utils.ToastUtil;
import com.summer.utils.UrlUtil;
import com.summer.view.CircularImage;

public class CardPublishFragment extends BaseFragment implements OnClickListener {

	private List<RightCard> publishCards = new ArrayList<>();
	private CommonAdapter<RightCard> adapter;
	private int pageIndex = 1;
	private Gson gson;
	private View rootView;
	private PullToRefreshGridView pGridViewPublish;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		gson = new Gson();
		sendCardOrderListRequest();
		super.onCreate(savedInstanceState);
	}
	
	@SuppressLint("InflateParams") @Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_card_publish, null);
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
		adapter = new CommonAdapter<RightCard>(getActivity(), R.layout.fragment_card_publish_item, publishCards){

			public void setTag(ViewHolder viewHolder, final RightCard item)
			{
				viewHolder.getView(R.id.lLayoutContent).setTag(R.id.tag_card_root, item);
				viewHolder.getView(R.id.cImagePortrait).setTag(R.id.tag_card_portrait, item);
				viewHolder.getView(R.id.imgViewBuy).setTag(R.id.tag_card_buy, item);
			}
			
			@Override
			public void convert(
					ViewHolder viewHolder,
					RightCard item, int position) {
				LinearLayout root = (LinearLayout) viewHolder.getView(R.id.lLayoutContent);
				CircularImage cPortrait = (CircularImage) viewHolder.getView(R.id.cImagePortrait);
				TextView txtName = (TextView) viewHolder.getView(R.id.txtViewName);
				TextView txtCardName = (TextView) viewHolder.getView(R.id.txtViewCardName);
				ImageView imagName = (ImageView) viewHolder.getView(R.id.imgViewName);
				TextView txtValue = (TextView) viewHolder.getView(R.id.txtViewCurrentValue);
				TextView txtEnvelopes = (TextView) viewHolder.getView(R.id.txtViewEnvelopesValue);
				TextView txtChange = (TextView) viewHolder.getView(R.id.txtViewChangeValue);
				TextView txtTime = (TextView) viewHolder.getView(R.id.txtViewTime);
				ImageView imagBuy = (ImageView) viewHolder.getView(R.id.imgViewBuy);
				ImageView imagBack = (ImageView) viewHolder.getView(R.id.imgViewBack);
				if (item != null)
				{
					Glide.with(getActivity()).load(formatPortrait(item.getHead()))
					.centerCrop()
					.diskCacheStrategy(DiskCacheStrategy.ALL)
					.placeholder(R.drawable.avatar_def).into(cPortrait);
					txtName.setText(isNullOrEmpty(item.getNick_name()) ? "未知" : item.getNick_name());
					txtCardName.setText(isNullOrEmpty(item.getLabel()) ? "未知" : item.getLabel());
					calculateChange(txtChange, item.getBid(), item.getDifference());
//					if (!isNullOrEmpty(item.getLabel()))
					if (item.getLabel() != null)
					{
						switch(item.getLabel())
						{
						case "戏约卡":
							txtCardName.setTextColor(Color.parseColor(getActivity().getString(R.color.card_blue)));
							imagName.setImageResource(R.drawable.activity_xiyue_name_bg);
							imagBuy.setImageResource(R.drawable.activity_card_xiyue_buy);
							imagBack.setImageResource(R.drawable.activity_card_xiyue_bg);
							break;
						case "演出卡":
							txtCardName.setTextColor(Color.parseColor(getActivity().getString(R.color.card_red)));
							imagName.setImageResource(R.drawable.activity_yanchu_bg);
							imagBuy.setImageResource(R.drawable.activity_card_yanchu_buy);
							imagBack.setImageResource(R.drawable.activity_card_yanchu_bg);
							break;
						case "商务卡":
							txtCardName.setTextColor(Color.parseColor(getActivity().getString(R.color.card_yellow)));
							imagName.setImageResource(R.drawable.activity_shangwu_bg);
							imagBuy.setImageResource(R.drawable.activity_card_shangwu_buy);
							imagBack.setImageResource(R.drawable.activity_card_shangwu_bg);
							break;
							default:
								txtCardName.setTextColor(Color.parseColor(getActivity().getString(R.color.card_blue)));
								imagName.setImageResource(R.drawable.activity_xiyue_name_bg);
								imagBuy.setImageResource(R.drawable.activity_card_xiyue_buy);
								imagBack.setImageResource(R.drawable.activity_card_xiyue_bg);
								break;
						}
					}
					txtValue.setText(isNullOrEmpty(calculateCurrentValue(item.getBid(), item.getPrice())) ? "未知" : calculateCurrentValue(item.getBid(), item.getPrice()));
					txtEnvelopes.setText(isNullOrEmpty(item.getPrice()) ? "未知" : item.getPrice());
					txtTime.setText(isNullOrEmpty(formatTime(item.getTime())) ? "未知" : formatTime(item.getTime()));
					root.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							RightCard card = (RightCard) v.getTag(R.id.tag_card_root);
							XLog.i("----name--------" + card.getNick_name());
							Intent i = new Intent(getActivity(), RightsCardDetailActivity.class);
							Bundle b = new Bundle();
							b.putSerializable("card", card);
							i.putExtras(b);
							startActivity(i);
						}
					});
				}
			}
			
		};
	}
	
	private String calculateCurrentValue(String bid, String price)
	{
		int sum = 0;
		try {
			int iBid = Integer.parseInt(bid);
			int iPrice = Integer.parseInt(price);
			int sPrice = iBid - iPrice + 1;
			for ( int start = sPrice; start <= iBid; start ++ )
			{
				sum += start;
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return String.valueOf(sum);
	}
	
	private void calculateChange(TextView txtView, String bid, float difference)
	{
		String p = "0.00%";
		try {
			int iBid = Integer.parseInt(bid);
			int last = (int) (iBid - difference);
			float diff = (difference / last ) * 100;
			DecimalFormat decimalFormat=new DecimalFormat("0.00");
			p= decimalFormat.format(diff);
			if (difference > 0)
			{
				p = "+" + p + "%";
				txtView.setBackgroundColor(getResources().getColor(R.color.card_change_red));
			}
			else if (difference < 0)
			{
				p = "-" + p + "%";
				txtView.setBackgroundColor(getResources().getColor(R.color.card_change_green));
			}
			else if (difference == 0)
			{
				p = "+0.00%";
				txtView.setBackgroundColor(getResources().getColor(R.color.card_change_red));
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		txtView.setText(p);
	}
	private void initView()
	{
		pGridViewPublish = (PullToRefreshGridView) rootView.findViewById(R.id.pGridViewPublish);
		pGridViewPublish.getRefreshableView().setNumColumns(1);
		pGridViewPublish.getRefreshableView().setVerticalSpacing(10);
		pGridViewPublish.setMode(PullToRefreshBase.Mode.BOTH);
		pGridViewPublish.setOnRefreshListener(refreshListener);
		pGridViewPublish.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				try {
					RightCard entity = (RightCard)view.getTag();
					if (entity != null)
					{
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		pGridViewPublish.setAdapter(adapter);
	}
	
	OnRefreshListener2<GridView> refreshListener = new OnRefreshListener2<GridView>() {

		@Override
		public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
			// 下拉刷新
			String time = DateUtils.formatDateTime(getActivity(),
					System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
							| DateUtils.FORMAT_SHOW_DATE
							| DateUtils.FORMAT_ABBREV_ALL);
			pGridViewPublish.getLoadingLayoutProxy().setRefreshingLabel(
					"正在刷新");
			pGridViewPublish.getLoadingLayoutProxy().setPullLabel("下拉刷新");
			pGridViewPublish.getLoadingLayoutProxy().setReleaseLabel(
					"释放开始刷新");
			refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(
					"最后更新时间:" + time);
			pageIndex = 1;
			sendCardOrderListRequest();

		}

		@Override
		public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
			// 上拉翻页
			String time = DateUtils.formatDateTime(getActivity(),
					System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
							| DateUtils.FORMAT_SHOW_DATE
							| DateUtils.FORMAT_ABBREV_ALL);
			pGridViewPublish.getLoadingLayoutProxy().setRefreshingLabel(
					"正在加载");
			pGridViewPublish.getLoadingLayoutProxy().setPullLabel("上拉翻页");
			pGridViewPublish.getLoadingLayoutProxy().setReleaseLabel(
					"释放开始加载");
			refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(
					"最后加载时间:" + time);
			// 调用数据
			sendCardOrderListRequest();
		}

	};
	
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
	
	private void sendCardOrderListRequest()
	{
    	HashMap<String, String> entity = new HashMap<String, String>();
		entity.put("clientID", Config.User.getClientID());
		entity.put("is_mine", "1");
		entity.put("page", String.valueOf(pageIndex));
    	List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
    	addToThreadPool(Config.getProfit, "get start info", params);	
	}
	
    private void addToThreadPool(int taskType, String tag, List<NameValuePair> params)
    {
    	HttpBaseTask httpTask = new HttpBaseTask(ThreadPoolConst.THREAD_TYPE_FILE_HTTP, tag, params, UrlUtil.GetUrl(taskType));
    	httpTask.setTaskType(taskType);
    	InfoHandler handler = new InfoHandler(this);
    	httpTask.setInfoHandler(handler);
    	ThreadPoolFactory.getThreadPoolManager().addTask(httpTask);
    }
    
	@Override
	public void onClick(View v) {
		
	}
	
	private void addPublishCards()
	{
		if (publishCards == null) {
			return;
		}
		if (pageIndex == 1) {// 第一页时，先清空数据集
			adapter.clearAll();
		}
		pageIndex++;
		adapter.add(publishCards);
	}
	
	private String formatTime(String originalTime)
	{
		String[] time = originalTime.split(" ")[0].split("-");
		String formatTime = time[1] + "-" + time[2];
		return formatTime;
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
	
	private String formatPortrait(String portrait)
	{
		String image = null;
		try {
			String s[] = portrait.split("/");
			
			if (s[2] != null && !s[2].contains("app.haimianyu.cn"))
			{
				image = "http://app.haimianyu.cn/" + portrait;
			}
			else
			{
				image = portrait;
			}
		} catch (Exception e) {
			XLog.e("portrait: " + portrait);
			e.printStackTrace();
		}
		return image;
	}
	
	@Override
	public void onInfoReceived(int errcode, HashMap<String, Object> items) {
		// TODO Auto-generated method stub
		super.onInfoReceived(errcode, items);
		pGridViewPublish.onRefreshComplete();
	}

	@Override
	public void RequestSuccessful(int status, String jsonString, int taskType) {
		switch (taskType)
		{
		case Config.getProfit:
			Entity<List<RightCard>> entity = gson.fromJson(jsonString,
					new TypeToken<Entity<List<RightCard>>>() {
					}.getType());
			publishCards = entity.getData();
			if (publishCards != null && publishCards.size() > 0) {
				addPublishCards();
			} else {
				ToastUtil.show(getActivity(), "没有更多数据了");
			}
			break;
		}
	}
}