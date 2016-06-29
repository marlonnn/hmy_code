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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.BC.entertainmentgravitation.AppealActivity;
import com.BC.entertainmentgravitation.R;
import com.BC.entertainmentgravitation.entity.CardOrder;
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

public class CardSellFragment extends BaseFragment implements OnClickListener {

	private List<CardOrder> sellCards = new ArrayList<>();
	private int pageIndex = 1;
	private Gson gson;
	private View rootView;
	private CommonAdapter<CardOrder> adapter;
	private PullToRefreshGridView pGridViewSell;
	
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
		rootView = inflater.inflate(R.layout.fragment_card_sell, null);
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
		adapter = new CommonAdapter<CardOrder>(getActivity(), R.layout.fragment_card_sell_item, sellCards){

			private LinearLayout lLayoutBook;
			private LinearLayout lLayoutCommunicate;
			private LinearLayout lLayoutAppeal;
			private LinearLayout lLayoutAgree;
			private LinearLayout lLayoutComplete;
			private LinearLayout lLayoutWancheng;
			private LinearLayout lLayoutCancel;
			
			public void setTag(ViewHolder viewHolder, final CardOrder item)
			{
				viewHolder.getView(R.id.rLayoutAppeal).setTag(R.id.tag_card_sell_appeal, item);
				viewHolder.getView(R.id.rLayoutCommunicate).setTag(R.id.tag_card_sell_communicate, item);
				viewHolder.getView(R.id.rLayoutAgree).setTag(R.id.tag_card_sell_agree, item);
				viewHolder.getView(R.id.rLayoutYuyue).setTag(R.id.tag_card_sell_yuyue, item);
				viewHolder.getView(R.id.lLayoutWancheng).setTag(R.id.tag_card_sell_wancheng, item);
			}
			
			private void hide(String state)
			{
				//状态： 0、退回  1、预购、  2、预约  3、同意  4、完成
				switch(state)
				{
				case "0":
					lLayoutBook.setVisibility(View.GONE);
					lLayoutCommunicate.setVisibility(View.GONE);
					lLayoutAppeal.setVisibility(View.GONE);
					lLayoutAgree.setVisibility(View.GONE);
					lLayoutComplete.setVisibility(View.GONE);
					lLayoutWancheng.setVisibility(View.GONE);
					lLayoutCancel.setVisibility(View.VISIBLE);
					break;
				case "1":
					lLayoutCancel.setVisibility(View.GONE);
					lLayoutBook.setVisibility(View.VISIBLE);
					lLayoutCommunicate.setVisibility(View.VISIBLE);
					lLayoutAppeal.setVisibility(View.GONE);
					lLayoutAgree.setVisibility(View.VISIBLE);
					lLayoutComplete.setVisibility(View.GONE);
					lLayoutWancheng.setVisibility(View.GONE);
					break;
				case "2":
					lLayoutCancel.setVisibility(View.GONE);
					lLayoutBook.setVisibility(View.GONE);
					lLayoutCommunicate.setVisibility(View.VISIBLE);
					lLayoutAppeal.setVisibility(View.VISIBLE);
					lLayoutAgree.setVisibility(View.VISIBLE);
					lLayoutComplete.setVisibility(View.GONE);
					lLayoutWancheng.setVisibility(View.GONE);
					break;
				case "3":
					lLayoutCancel.setVisibility(View.GONE);
					lLayoutBook.setVisibility(View.GONE);
					lLayoutCommunicate.setVisibility(View.VISIBLE);
					lLayoutAppeal.setVisibility(View.VISIBLE);
					lLayoutAgree.setVisibility(View.GONE);
					lLayoutComplete.setVisibility(View.GONE);
					lLayoutWancheng.setVisibility(View.GONE);
					break;
				case "4":
					lLayoutCancel.setVisibility(View.GONE);
					lLayoutBook.setVisibility(View.GONE);
					lLayoutCommunicate.setVisibility(View.GONE);
					lLayoutAppeal.setVisibility(View.GONE);
					lLayoutAgree.setVisibility(View.GONE);
					lLayoutComplete.setVisibility(View.VISIBLE);
					lLayoutWancheng.setVisibility(View.GONE);
					break;
				}
			}
			
			@Override
			public void convert(
					ViewHolder viewHolder,
					CardOrder item, int position) {
				LinearLayout root = (LinearLayout) viewHolder.getView(R.id.lLayoutContent);
				lLayoutBook = (LinearLayout) viewHolder.getView(R.id.lLayoutBook);
				lLayoutCommunicate = (LinearLayout) viewHolder.getView(R.id.lLayoutCommunicate);
				lLayoutAppeal = (LinearLayout) viewHolder.getView(R.id.lLayoutAppeal);
				lLayoutAgree = (LinearLayout) viewHolder.getView(R.id.lLayoutAgree);
				lLayoutComplete = (LinearLayout) viewHolder.getView(R.id.lLayoutComplete);
				lLayoutWancheng = (LinearLayout) viewHolder.getView(R.id.lLayoutWancheng);
				lLayoutCancel = (LinearLayout) viewHolder.getView(R.id.lLayoutCancel);
				CircularImage cPortrait = (CircularImage) viewHolder.getView(R.id.cImagePortrait);
				TextView txtName = (TextView) viewHolder.getView(R.id.txtViewName);
				TextView txtCardName = (TextView) viewHolder.getView(R.id.txtViewCardName);
				ImageView imagName = (ImageView) viewHolder.getView(R.id.imgViewName);
				TextView txtValue = (TextView) viewHolder.getView(R.id.txtViewCurrentValue);
				TextView txtEnvelopes = (TextView) viewHolder.getView(R.id.txtViewEnvelopesValue);
				TextView txtChange = (TextView) viewHolder.getView(R.id.txtViewChangeValue);
				TextView txtTime = (TextView) viewHolder.getView(R.id.txtViewTime);
				ImageView imagBack = (ImageView) viewHolder.getView(R.id.imgViewBack);
				
				RelativeLayout rLayoutAppeal = (RelativeLayout) viewHolder.getView(R.id.rLayoutAppeal);
				RelativeLayout rLayoutCommunicate = (RelativeLayout) viewHolder.getView(R.id.rLayoutCommunicate);
				RelativeLayout rLayoutAgree = (RelativeLayout) viewHolder.getView(R.id.rLayoutAgree);
				RelativeLayout rLayoutYuyue = (RelativeLayout) viewHolder.getView(R.id.rLayoutYuyue);
				RelativeLayout rLayoutWancheng = (RelativeLayout) viewHolder.getView(R.id.rLayoutWancheng);
				ImageView imgYuyueBg = (ImageView) viewHolder.getView(R.id.imgViewYuyueBg);
				ImageView imgViewWanchengBg = (ImageView) viewHolder.getView(R.id.imgViewWanchengBg);
				ImageView imgAppealBg = (ImageView) viewHolder.getView(R.id.imgViewAppealBg);
				ImageView imgCommunicateBg = (ImageView) viewHolder.getView(R.id.imgViewCommunicateBg); 
				ImageView imgViewAgreeBg = (ImageView) viewHolder.getView(R.id.imgViewAgreeBg); 
				if (item != null)
				{
					Glide.with(getActivity()).load(formatPortrait(item.getHead()))
					.centerCrop()
					.diskCacheStrategy(DiskCacheStrategy.ALL)
					.placeholder(R.drawable.avatar_def).into(cPortrait);
					txtName.setText(isNullOrEmpty(item.getNick_name()) ? "未知" : item.getNick_name());
					txtCardName.setText(isNullOrEmpty(item.getLabel()) ? "未知" : item.getLabel());
					calculateChange(txtChange, item.getPrice_index(), item.getBid());
					hide(item.getState());
					if (item.getLabel() != null)
					{
						switch(item.getLabel())
						{
						case "戏约卡":
							txtCardName.setTextColor(Color.parseColor(getActivity().getString(R.color.card_blue)));
							imagName.setImageResource(R.drawable.activity_xiyue_name_bg);
							imagBack.setImageResource(R.drawable.activity_card_xiyue_bg);
							imgAppealBg.setImageResource(R.drawable.item_xiyue_bg);
							imgViewWanchengBg.setImageResource(R.drawable.item_xiyue_bg);
							imgYuyueBg.setImageResource(R.drawable.item_xiyue_bg);
							imgCommunicateBg.setImageResource(R.drawable.item_xiyue_bg);
							imgViewAgreeBg.setImageResource(R.drawable.item_xiyue_bg);
							break;
						case "演出卡":
							txtCardName.setTextColor(Color.parseColor(getActivity().getString(R.color.card_red)));
							imagName.setImageResource(R.drawable.activity_yanchu_bg);
							imagBack.setImageResource(R.drawable.activity_card_yanchu_bg);
							imgAppealBg.setImageResource(R.drawable.item_yanchu_bg);
							imgViewWanchengBg.setImageResource(R.drawable.item_yanchu_bg);
							imgYuyueBg.setImageResource(R.drawable.item_yanchu_bg);
							imgCommunicateBg.setImageResource(R.drawable.item_yanchu_bg);
							imgViewAgreeBg.setImageResource(R.drawable.item_yanchu_bg);
							break;
						case "商务卡":
							txtCardName.setTextColor(Color.parseColor(getActivity().getString(R.color.card_yellow)));
							imagName.setImageResource(R.drawable.activity_shangwu_bg);
							imagBack.setImageResource(R.drawable.activity_card_shangwu_bg);
							imgAppealBg.setImageResource(R.drawable.item_shangwu_bg);
							imgViewWanchengBg.setImageResource(R.drawable.item_shangwu_bg);
							imgYuyueBg.setImageResource(R.drawable.item_shangwu_bg);
							imgCommunicateBg.setImageResource(R.drawable.item_shangwu_bg);
							imgViewAgreeBg.setImageResource(R.drawable.item_shangwu_bg);
							break;
							default:
								txtCardName.setTextColor(Color.parseColor(getActivity().getString(R.color.card_blue)));
								imagName.setImageResource(R.drawable.activity_xiyue_name_bg);
								imagBack.setImageResource(R.drawable.activity_card_xiyue_bg);
								imgAppealBg.setImageResource(R.drawable.item_xiyue_bg);
								imgViewWanchengBg.setImageResource(R.drawable.item_xiyue_bg);
								imgYuyueBg.setImageResource(R.drawable.item_xiyue_bg);
								imgCommunicateBg.setImageResource(R.drawable.item_xiyue_bg);
								imgViewAgreeBg.setImageResource(R.drawable.item_xiyue_bg);
								break;
						}
					}
					txtValue.setText(isNullOrEmpty(calculateCurrentValue(item.getPrice_index(), item.getPrice())) ? "未知" : calculateCurrentValue(item.getPrice_index(), item.getPrice()));
					txtEnvelopes.setText(isNullOrEmpty(item.getPrice()) ? "未知" : item.getPrice());
					txtTime.setText(isNullOrEmpty(formatTime(item.getOrder_time())) ? "未知" : formatTime(item.getOrder_time()));
					
					rLayoutAppeal.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							try {
								CardOrder cardOrder = (CardOrder) v.getTag(R.id.tag_card_sell_appeal);
								XLog.i("----name--------" + cardOrder.getNick_name());
								Intent i = new Intent(getActivity(), AppealActivity.class);
								Bundle b = new Bundle();
								b.putSerializable("cardOrder", cardOrder);
								i.putExtras(b);
								startActivity(i);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
					
					rLayoutCommunicate.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							
						}
					});
					rLayoutAgree.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							CardOrder cardOrder = (CardOrder) v.getTag(R.id.tag_card_sell_agree);
							sendChangeOrderStatusRequest(cardOrder, "3");
						}
					});
					rLayoutYuyue.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							CardOrder cardOrder = (CardOrder) v.getTag(R.id.tag_card_sell_yuyue);
							sendChangeOrderStatusRequest(cardOrder, "2");
						}
					});
					rLayoutWancheng.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							CardOrder cardOrder = (CardOrder) v.getTag(R.id.tag_card_sell_wancheng);
							sendChangeOrderStatusRequest(cardOrder, "4");
						}
					});
				}
			}
			
		};
	}
	
	private void sendChangeOrderStatusRequest(CardOrder order, String status)
	{
		if (order != null)
		{
	    	HashMap<String, String> entity = new HashMap<String, String>();
			entity.put("clientID", Config.User.getClientID());
			entity.put("orderid", order.getOrder_id());
			entity.put("status", status);
	    	List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
	    	ShowProgressDialog("提交中...");
	    	addToThreadPool(Config.orderStatus, "get start info", params);	
		}

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
	
	private void calculateChange(TextView txtView, String price_index, String bid)
	{
		String p = "0.00%";
		try {
			int iBid = Integer.parseInt(bid);
			int iPriceIndex = Integer.parseInt(price_index);
			float last = (float) (iBid - iPriceIndex);
			float diff = (last / iPriceIndex ) * 100;
			DecimalFormat decimalFormat=new DecimalFormat("0.00");
			p= decimalFormat.format(diff);
			if (last > 0)
			{
				p = "+" + p + "%";
				txtView.setBackgroundColor(getResources().getColor(R.color.card_change_red));
			}
			else if (last < 0)
			{
				p = "-" + p + "%";
				txtView.setBackgroundColor(getResources().getColor(R.color.card_change_green));
			}
			else if (last == 0)
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
		pGridViewSell = (PullToRefreshGridView) rootView.findViewById(R.id.pGridViewSell);
		pGridViewSell.getRefreshableView().setNumColumns(1);
		pGridViewSell.getRefreshableView().setVerticalSpacing(10);
		pGridViewSell.setMode(PullToRefreshBase.Mode.BOTH);
		pGridViewSell.setOnRefreshListener(refreshListener);
		pGridViewSell.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				try {
					CardOrder entity = (CardOrder)view.getTag();
					if (entity != null)
					{
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		pGridViewSell.setAdapter(adapter);
	}
	
	OnRefreshListener2<GridView> refreshListener = new OnRefreshListener2<GridView>() {

		@Override
		public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
			// 下拉刷新
			String time = DateUtils.formatDateTime(getActivity(),
					System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
							| DateUtils.FORMAT_SHOW_DATE
							| DateUtils.FORMAT_ABBREV_ALL);
			pGridViewSell.getLoadingLayoutProxy().setRefreshingLabel(
					"正在刷新");
			pGridViewSell.getLoadingLayoutProxy().setPullLabel("下拉刷新");
			pGridViewSell.getLoadingLayoutProxy().setReleaseLabel(
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
			pGridViewSell.getLoadingLayoutProxy().setRefreshingLabel(
					"正在加载");
			pGridViewSell.getLoadingLayoutProxy().setPullLabel("上拉翻页");
			pGridViewSell.getLoadingLayoutProxy().setReleaseLabel(
					"释放开始加载");
			refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(
					"最后加载时间:" + time);
			// 调用数据
			sendCardOrderListRequest();
		}

	};
	
	@Override
	public void onStart() {
		sendCardOrderListRequest();
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
		entity.put("ordertype", "1");
		entity.put("page", String.valueOf(pageIndex));
    	List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
    	addToThreadPool(Config.orderList, "get start info", params);	
	}
	
    private void addToThreadPool(int taskType, String tag, List<NameValuePair> params)
    {
    	HttpBaseTask httpTask = new HttpBaseTask(ThreadPoolConst.THREAD_TYPE_FILE_HTTP, tag, params, UrlUtil.GetUrl(taskType));
    	httpTask.setTaskType(taskType);
    	InfoHandler handler = new InfoHandler(this);
    	httpTask.setInfoHandler(handler);
    	ThreadPoolFactory.getThreadPoolManager().addTask(httpTask);
    }
    
	private String formatTime(String originalTime)
	{
		String formatTime = "";
		try {
			if (originalTime != null)
			{
				String[] time = originalTime.split(" ")[0].split("-");
				formatTime = time[1] + "-" + time[2];
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

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
		pGridViewSell.onRefreshComplete();
	}
	
	@Override
	public void onClick(View v) {
		
	}

	@Override
	public void RequestSuccessful(int status, String jsonString, int taskType) {
		switch (taskType)
		{
		case Config.orderStatus:
			ToastUtil.show(getActivity(), "提交成功");
			break;
		case Config.orderList:
			Entity<List<CardOrder>> entity = gson.fromJson(jsonString,
					new TypeToken<Entity<List<CardOrder>>>() {
					}.getType());
			sellCards = entity.getData();
			if (entity.getData() != null && entity.getData().size() > 0) {
				addSellCards();
			} else {
				ToastUtil.show(getActivity(), "没有更多数据了");
			}
			break;
		}
	}
	
	private void addSellCards()
	{
		if (sellCards == null) {
			return;
		}
		if (pageIndex == 1) {// 第一页时，先清空数据集
			adapter.clearAll();
		}
		pageIndex++;
		adapter.add(sellCards);
	}
	
//	private void filter(List<CardOrder> listCard)
//	{
//		if (pageIndex == 1) {// 第一页时，先清空数据集
//			adapter.clearAll();
//		}
//		for (CardOrder c : listCard)
//		{
//			if (c != null && c.getState() != null && c.getState().contains("2"))
//			{
//				sellCards.add(c);
//			}
//		}
//		if (sellCards == null) {
//			return;
//		}
//		pageIndex++;
//		adapter.add(sellCards);
//	}

}
