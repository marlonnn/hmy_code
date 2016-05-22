package com.BC.entertainmentgravitation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.BC.entertainment.view.CoordinateSystemView;
import com.BC.entertainmentgravitation.entity.KLink;
import com.BC.entertainmentgravitation.entity.Member;
import com.BC.entertainmentgravitation.entity.Point;
import com.BC.entertainmentgravitation.entity.RedAList;
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
	private TextView currentIndex;
	
	private CoordinateSystemView coordinateSystemView;
	private LineChart lineChart;
	private Member member;
	private PullToRefreshListView pullToRefreshListView;
	private CommonAdapter<RedAList> adapter;
	private int pageIndex = 1;
	
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
		currentIndex = (TextView) findViewById(R.id.txtViewIndex);
		txtViewChange = (TextView) findViewById(R.id.txtViewChange);
		txtViewHongBao = (TextView) findViewById(R.id.txtViewHongBao);
		coordinateSystemView = (CoordinateSystemView) findViewById(R.id.coordinateSystemView);
	}
	
	private void initializeView()
	{
		pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.pullToRefreshListView);
		pullToRefreshListView.setOnRefreshListener(refreshListener);
		adapter = new CommonAdapter<RedAList>(this.getBaseContext(),
				R.layout.activity_curve_item, new ArrayList<RedAList>()) {

			@Override
			public void convert(ViewHolder helper, final RedAList item) {

				TextView textView = (TextView) helper.getView(R.id.type);

				switch (item.getType()) {
				case 1:
					textView.setText(getString(R.string.fragment_account_item_publish_income));
					textView.setTextColor(Color.parseColor("#dd0000"));
					break;
				case 2:
					textView.setText(getString(R.string.fragment_account_item_publish_payout));
					textView.setTextColor(Color.parseColor("#2fab21"));
					break;
				case 3:
					textView.setText(getString(R.string.fragment_account_item_publish_payout));
					textView.setTextColor(Color.parseColor("#2fab21"));
					break;

				}
				helper.setText(R.id.The_publishers_name,
						item.getThe_publishers_name() + "");
				helper.setText(R.id.Grants_of_number,
						item.getGrants_of_number() + "");
				helper.setText(R.id.time, item.getTime() + "");
			}
		};
		pullToRefreshListView.setAdapter(adapter);
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
		HashMap<String, String> entity = new HashMap<String, String>();
		entity.put("star_id", clientId);
		entity.put("type", "1");
    	List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
    	ShowProgressDialog("获取折线图...");
    	addToThreadPool(Config.k_line_graph, "send kLine request", params);
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

		entity.put("clientID", "" + member.getId());
		entity.put("The_page_number", "" + pageIndex);
		
		List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
		addToThreadPool(Config.account, "send account request", params);
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
			ToastUtil.show(this, "此功能正在抓紧开发中，敬请期待...");
			break;
			
		case R.id.invest:
			ToastUtil.show(this, "此功能正在抓紧开发中，敬请期待...");
			break;
			
		case R.id.divest:
			ToastUtil.show(this, "此功能正在抓紧开发中，敬请期待...");
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
	public void onInfoReceived(int errcode, HashMap<String, Object> items) {
		super.onInfoReceived(errcode, items);
		pullToRefreshListView.onRefreshComplete();
	}

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
		case Config.k_line_graph:
			XLog.i("get k line success");
			Entity<KLink> baseEntity3 = gson.fromJson(jsonString,
					new TypeToken<Entity<KLink>>() {
					}.getType());
			KLink kLink = baseEntity3.getData();
			int diff = Integer.parseInt(kLink.getDifference());
			txtViewChange.setText("昨日涨跌"+diff+"点");
			txtViewHongBao.setText(kLink.getBonus() == null ? "" : kLink.getBonus());
			initPriceCurve(kLink);
			XLog.i(kLink.toString());
			break;
		case Config.account:
			Entity<List<RedAList>> baseEntity = gson.fromJson(jsonString,
					new TypeToken<Entity<List<RedAList>>>() {
					}.getType());
			List<RedAList> messageItems = baseEntity.getData();
			if (messageItems != null) {
				if (pageIndex == 1) {// 第一页时，先清空数据集
					adapter.clearAll();
				}
				pageIndex++;
				adapter.add(messageItems);
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
