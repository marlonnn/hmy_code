package com.BC.entertainmentgravitation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.BC.entertainment.view.CoordinateSystemView;
import com.BC.entertainmentgravitation.dialog.ApplauseGiveConcern;
import com.BC.entertainmentgravitation.dialog.PurchaseDialog;
import com.BC.entertainmentgravitation.entity.Contribution;
import com.BC.entertainmentgravitation.entity.KLink;
import com.BC.entertainmentgravitation.entity.Member;
import com.BC.entertainmentgravitation.entity.Point;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.netease.nim.uikit.common.ui.ptr.PullToRefreshBase;
import com.netease.nim.uikit.common.ui.ptr.PullToRefreshListView;
import com.netease.nim.uikit.common.ui.ptr.PullToRefreshBase.OnRefreshListener2;
import com.summer.activity.BaseActivity;
import com.summer.adapter.CommonAdapter;
import com.summer.config.Config;
import com.summer.factory.ThreadPoolFactory;
import com.summer.handler.InfoHandler;
import com.summer.json.Entity;
import com.summer.logger.XLog;
import com.summer.task.HttpBaseTask;
import com.summer.treadpool.ThreadPoolConst;
import com.summer.utils.JsonUtil;
import com.summer.utils.ToastUtil;
import com.summer.utils.UrlUtil;
import com.summer.view.CircularImage;
import com.summer.view.LineChart;
import com.umeng.analytics.MobclickAgent;

/**
 * 价值曲线
 * @author zhongwen
 *
 */
public class CurveActivity extends BaseActivity implements OnClickListener{
	
	private Gson gson;
	private TextView txtViewChange;
	private TextView txtViewHongBao;
	
	private CoordinateSystemView coordinateSystemView;
	private LineChart lineChart;
	private Member member;
	private PullToRefreshListView pullToRefreshListView;
	private CommonAdapter<Contribution> adapter;
	private int pageIndex = 1;
	private List<Contribution> ranking = new ArrayList<>();
	private ApplauseGiveConcern applauseGiveConcern;
	private TextView txtViewIndex;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal_line);
		gson = new Gson();
		lineChart = new LineChart();
		findViewViewById();
		initializeView();
		
		try {
			Intent intent = this.getIntent(); 
			member = (Member) intent.getSerializableExtra("member");
			sendKLineGraphRequest(member.getId());
			sendAccountRequest(1);
			
			applauseGiveConcern = new ApplauseGiveConcern(this, member.getId(),
					this, Integer.parseInt(member.getBid()),
					member.getNick());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
    
	@Override
	protected void onResume() {
		super.onResume();
        MobclickAgent.onResume(this);
	}
	
	private void findViewViewById()
	{
		findViewById(R.id.imageViewBack).setOnClickListener(this);
		findViewById(R.id.focus).setOnClickListener(this);
		findViewById(R.id.invest).setOnClickListener(this);
		findViewById(R.id.divest).setOnClickListener(this);
		txtViewIndex = (TextView) findViewById(R.id.txtViewIndex);
		txtViewChange = (TextView) findViewById(R.id.txtViewChange);
		txtViewHongBao = (TextView) findViewById(R.id.txtViewHongBao);
		coordinateSystemView = (CoordinateSystemView) findViewById(R.id.coordinateSystemView);
	}
	
	private void initializeView()
	{
		pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.pullToRefreshListView);
		pullToRefreshListView.setOnRefreshListener(refreshListener);
		adapter = new CommonAdapter<Contribution>(this.getBaseContext(),
				R.layout.activity_curve_item, new ArrayList<Contribution>()) {

			@Override
			public void convert(ViewHolder helper, final Contribution item, int position) {

				TextView txtViewRank = (TextView) helper.getView(R.id.txtViewRank);
				ImageView imgViewRank = (ImageView) helper.getView(R.id.imgViewRank);
				CircularImage cImagePortrait = (CircularImage) helper.getView(R.id.cImagePortrait);
				TextView txtViewName = (TextView) helper.getView(R.id.txtViewName);
				TextView txtViewContribute = (TextView) helper.getView(R.id.txtViewContribute);
				int rank = position + 1;
				switch(rank)
				{
				case 1:
					imgViewRank.setImageResource(R.drawable.icon_1);
					txtViewRank.setVisibility(View.GONE);
					imgViewRank.setVisibility(View.VISIBLE);
					Glide.with(CurveActivity.this).load(item.getHand())
					.centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL)
					.placeholder(R.drawable.avatar_def)
					.into(cImagePortrait);
					txtViewName.setText(isNullOrEmpty(item.getName()) ? "" : item.getName());
					txtViewContribute.setText(isNullOrEmpty(item.getContribution()) ? "0" : item.getContribution());
					break;
				case 2:
					imgViewRank.setImageResource(R.drawable.icon_2);
					txtViewRank.setVisibility(View.GONE);
					imgViewRank.setVisibility(View.VISIBLE);
					Glide.with(CurveActivity.this).load(item.getHand())
					.centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL)
					.placeholder(R.drawable.avatar_def)
					.into(cImagePortrait);					
					txtViewName.setText(isNullOrEmpty(item.getName()) ? "" : item.getName());
					txtViewContribute.setText(isNullOrEmpty(item.getContribution()) ? "0" : item.getContribution());
					
					break;
				case 3:
					imgViewRank.setImageResource(R.drawable.icon_3);
					txtViewRank.setVisibility(View.GONE);
					imgViewRank.setVisibility(View.VISIBLE);
					Glide.with(CurveActivity.this).load(item.getHand())
					.centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL)
					.placeholder(R.drawable.avatar_def)
					.into(cImagePortrait);
					txtViewName.setText(isNullOrEmpty(item.getName()) ? "" : item.getName());
					txtViewContribute.setText(isNullOrEmpty(item.getContribution()) ? "0" : item.getContribution());
					break;
				default:
					Glide.with(CurveActivity.this).load(item.getHand())
					.centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL)
					.placeholder(R.drawable.avatar_def)
					.into(cImagePortrait);
					txtViewRank.setVisibility(View.VISIBLE);
					imgViewRank.setVisibility(View.GONE);

					txtViewRank.setText("NO." + rank);
					txtViewName.setText(isNullOrEmpty(item.getName()) ? "" : item.getName());
					txtViewContribute.setText(isNullOrEmpty(item.getContribution()) ? "0" : item.getContribution());
					break;
				}

			}
		};
		pullToRefreshListView.setAdapter(adapter);
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
	
	OnRefreshListener2<ListView> refreshListener = new OnRefreshListener2<ListView>(){

		@Override
		public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
			// 下拉刷新
			String time = DateUtils.formatDateTime(CurveActivity.this,
					System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
							| DateUtils.FORMAT_SHOW_DATE
							| DateUtils.FORMAT_ABBREV_ALL);
			pullToRefreshListView.getLoadingLayoutProxy().setRefreshingLabel(
					getResourceString(R.string.fragment_account_refresh_now));
			pullToRefreshListView.getLoadingLayoutProxy().setPullLabel(getResourceString(R.string.fragment_account_pull_down));
			pullToRefreshListView.getLoadingLayoutProxy().setReleaseLabel(
					getResourceString(R.string.fragment_account_release_refresh));
			refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(
					getResourceString(R.string.fragment_account_last_update_time) + time);
			pageIndex = 1;
			// send request
			sendAccountRequest(pageIndex);
		}

		@Override
		public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
			// 上拉翻页
			String time = DateUtils.formatDateTime(CurveActivity.this,
					System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
							| DateUtils.FORMAT_SHOW_DATE
							| DateUtils.FORMAT_ABBREV_ALL);
			pullToRefreshListView.getLoadingLayoutProxy().setRefreshingLabel(
					getResourceString(R.string.fragment_account_loading));
			pullToRefreshListView.getLoadingLayoutProxy().setPullLabel(getResourceString(R.string.fragment_account_pull_up));
			pullToRefreshListView.getLoadingLayoutProxy().setReleaseLabel(
					getResourceString(R.string.fragment_account_release_loading));
			refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(
					getResourceString(R.string.fragment_account_last_loading_time) + time);
			// send request
			sendAccountRequest(pageIndex);
		}
		
	};
	
	private String getResourceString(int id)
	{
		return getString(id);
	}
	
	/**
	 * 获取价格曲线
	 */
	public void sendKLineGraphRequest(String clientId) {
		try {
			HashMap<String, String> entity = new HashMap<String, String>();
			entity.put("star_id", clientId);
			entity.put("user_id", Config.User.getClientID());
			entity.put("type", "1");
			List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
			ShowProgressDialog("获取折线图...");
			addToThreadPool(Config.k_line_graph, "send kLine request", params);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取用户交易详情
	 * @param pageIndex
	 */
	private void sendAccountRequest(int pageIndex)
	{
    	if (Config.User == null)
    	{
			ToastUtil.show(this,"无法获取交易详情，请稍后再试...");
			return;
    	}
    	
		HashMap<String, String> entity = new HashMap<String, String>();

		entity.put("star_id", member.getId());
		entity.put("pageIndex", "" + pageIndex);

		ShowProgressDialog("获取信息...");
		List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
		addToThreadPool(Config.contribution, "send save user request", params);
	}
	
    private void addToThreadPool(int taskType, String tag, List<NameValuePair> params)
    {
    	XLog.i("add to thread pool: " + tag);
    	HttpBaseTask httpTask = new HttpBaseTask(ThreadPoolConst.THREAD_TYPE_FILE_HTTP, tag, params, UrlUtil.GetUrl(taskType));
    	httpTask.setTaskType(taskType);
    	InfoHandler handler = new InfoHandler(this);
    	httpTask.setInfoHandler(handler);
    	ThreadPoolFactory.getThreadPoolManager().addTask(httpTask);
    }

	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
		case R.id.focus:
			if (applauseGiveConcern != null)
			{
				applauseGiveConcern.sendFocusRequest();
			}
			break;
		case R.id.invest:
			if (applauseGiveConcern != null)
			{
				applauseGiveConcern.showApplaudDialog(1);
			}
			break;
		case R.id.divest:
			if (applauseGiveConcern != null)
			{
				applauseGiveConcern.showApplaudDialog(2);
			}
			break;
		/**
		 * 返回键
		 */
		case R.id.imageViewBack:
			finish();
			break;
		}
	}
	
	@Override
	public void onInfoReceived(int errorCode, HashMap<String, Object> items) {
		pullToRefreshListView.onRefreshComplete();
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
                    	if (taskType == Config.give_applause_booed && errorCode == 205)
                    	{
                    		showPurchaseDialog();
                    	}
                    	else
                    	{
                    		RequestFailed(code, msg, taskType);
                    	}
                    }
                } catch (JSONException e) {
                    XLog.e(e);
                    e.printStackTrace();
                    RequestFailed(-1, "Json Parse Error", -1);
                }
            }
        }
	}
	
	private void showPurchaseDialog()
	{
		PurchaseDialog.Builder builder = new PurchaseDialog.Builder(this);
		builder.setTitle("购买娛币");
		builder.setMessage("娛币不足，是否购买娛币？");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(CurveActivity.this, ChargeActivity.class);
				startActivity(intent);				
			}
			
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		PurchaseDialog purchaseDialog = builder.create();
		purchaseDialog.show();
	}
	
//	@Override
//	public void onInfoReceived(int errcode, HashMap<String, Object> items) {
//		super.onInfoReceived(errcode, items);
//		pullToRefreshListView.onRefreshComplete();
//	}

	@Override
	public void onNotifyText(String notify) {
		super.onNotifyText(notify);
	}
	
	@Override
	public void RequestFailed(int errcode, String message, int taskType) {
		super.RequestFailed(errcode, message, taskType);
		ToastUtil.show(this, getResourceString(R.string.fragment_account_fail_data));
	}

	@Override
	public void RequestSuccessful(String jsonString, int taskType) {
		switch(taskType)
		{
		case Config.give_applause_booed:
			ToastUtil.show(this, "提交成功");
			switch (applauseGiveConcern.getType()) {
			case 1:
				applauseGiveConcern.showAnimationDialog(R.drawable.circle4,
						R.raw.applaud);
				break;
			case 2:
				applauseGiveConcern.showAnimationDialog(R.drawable.circle5,
						R.raw.give_back);
				break;
			default:
				applauseGiveConcern.showAnimationDialog(R.drawable.circle4,
						R.raw.applaud);
				break;
			}
			break;
		case Config.and_attention:
			ToastUtil.show(this, "提交成功");
			applauseGiveConcern.showAnimationDialog(R.drawable.circle6,
					R.raw.concern);
			break;
		case Config.k_line_graph:
			XLog.i("get k line success");
			Entity<KLink> baseEntity3 = gson.fromJson(jsonString,
					new TypeToken<Entity<KLink>>() {
					}.getType());
			KLink kLink = baseEntity3.getData();
			txtViewChange.setText(isNullOrEmpty(kLink.getDifference()) ? "昨日涨跌0点" : "昨日涨跌"+ kLink.getDifference()+ "点");
			txtViewIndex.setText(isNullOrEmpty(kLink.getBid())? "明星储备指数：0点" : "明星储备指数：" + kLink.getBid()+  "点");
			txtViewHongBao.setText(isNullOrEmpty(kLink.getBonus())? "0" : kLink.getBonus());
			initPriceCurve(kLink);
			XLog.i(kLink.toString());
			break;
		case Config.contribution:
			Entity<List<Contribution>> baseEntity = gson.fromJson(
					jsonString,
					new TypeToken<Entity<List<Contribution>>>() {
					}.getType());
			ranking = baseEntity.getData();
			if (ranking != null)
			{
				if (pageIndex == 1) {// 第一页时，先清空数据集
					adapter.clearAll();
				}
				pageIndex++;
				adapter.add(ranking);
			} else {
				ToastUtil.show(this, getResourceString(R.string.fragment_account_fail_data));
			}
			break;
		}
	}
	
	/**
	 * 初始化价值曲线
	 * @param kLink
	 */
	public void initPriceCurve(KLink kLink) {
		if (kLink == null || kLink.getPoint() == null
				|| kLink.getPoint().size() == 0) {
			ToastUtil.show(this, "暂无数据");
			return;
		}
		List<Point> price_movements = kLink.getPoint();
		int l = kLink.getMax().length();
		String o = "21";
		for (int i = 1; i < l - 1; i++) {
			o += "0";
		}
		long over = Long.valueOf(o);
		long max = Long.valueOf(kLink.getMax());
		max = over + max;
		max = max / over;
		max = max * over;
		coordinateSystemView.setyShowMax(max);
		long yLin1 = (max / 7);
		long yLin2 = yLin1 * 2;
		long yLin3 = yLin1 * 3;
		long yLin4 = yLin1 * 4;
		long yLin5 = yLin1 * 5;
		long yLin6 = yLin1 * 6;
		long yLin7 = yLin1 * 7;
		coordinateSystemView.setyPartingName(new String[] { yLin1 + "",
				yLin2 + "", yLin3 + "", yLin4 + "", yLin5 + "", yLin6 + "",
				yLin7 + "" });
		lineChart.getCoordinates().clear();
		coordinateSystemView.setxParting(48);
		String[] s = new String[48];
		for (int i = 0; i < s.length; i++) {
			if (i % 2 == 0) {
				s[i] = i / 2 + ":00";
			} else {
				s[i] = i / 2 + ":30";
			}
		}
		coordinateSystemView.setxPartingName(s);
		lineChart.setNum(48);
		for (int i = 0; i < price_movements.size(); i++) {
			lineChart.addPoint(i,
					Float.valueOf(price_movements.get(i).getPrice()), null);
		}
		coordinateSystemView.setLineChart(lineChart);

	}

}
