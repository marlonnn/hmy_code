package com.BC.entertainmentgravitation;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.BC.entertainment.cache.InfoCache;
import com.BC.entertainment.view.CoordinateSystemView;
import com.BC.entertainmentgravitation.dialog.ApplauseGiveConcern;
import com.BC.entertainmentgravitation.entity.KLink;
import com.BC.entertainmentgravitation.entity.Point;
import com.BC.entertainmentgravitation.entity.RightCard;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.summer.activity.BaseActivity;
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

public class RightsCardDetailActivity extends BaseActivity implements OnClickListener{

	private RightCard card;
	private TextView txtLocation;
	private TextView txtName;
	private TextView txtValue;
	private TextView txtEnvelopes;
	private TextView txtChange;
	private TextView txtTime;
	private TextView txtCardName;
	private Button btnBuy;
	private CircularImage cImagePortrait;
	private TextView txtViewHongBao;
	private TextView txtViewChange;
	private TextView txtViewIndex;
	private ImageView imgViewGender;
	private Gson gson;
	private CoordinateSystemView coordinateSystemView;
	private LineChart lineChart;
	private ApplauseGiveConcern applauseGiveConcern;
	private boolean hasFollow = false;
	
	private boolean show = false;
	
	private LinearLayout lLayoutContent;
	private ImageView imgViewUp;
	private ImageView imgViewRightsCard;
	private TextView txtContent;
//	private Member member;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rights_card_detail);
		findViewById(R.id.imageViewBack).setOnClickListener(this);
		gson = new Gson();
		lineChart = new LineChart();
		card = (RightCard) getIntent().getSerializableExtra("card");
		initView(card);
		sendKLineGraphRequest(card);
		sendMemberRequest(card);
	}
	
	private void initView(RightCard card)
	{
		lLayoutContent = (LinearLayout) findViewById(R.id.lLayoutIntroductionContent);
		imgViewUp = (ImageView) findViewById(R.id.imgViewUp);
		imgViewRightsCard = (ImageView) findViewById(R.id.imgViewRightsCard);
		txtContent = (TextView) findViewById(R.id.txtViewIntroduction);
		imgViewUp.setOnClickListener(this);
		coordinateSystemView = (CoordinateSystemView) findViewById(R.id.coordinateSystemView);
		cImagePortrait = (CircularImage) findViewById(R.id.cImagePortrait);
		txtLocation = (TextView) findViewById(R.id.txtViewLocation);
		txtName = (TextView) findViewById(R.id.txtViewName);
		txtCardName = (TextView) findViewById(R.id.txtViewCardName);
		txtValue = (TextView) findViewById(R.id.txtViewCurrentValue);
		txtEnvelopes = (TextView) findViewById(R.id.txtViewEnvelopesValue);
		txtChange = (TextView) findViewById(R.id.txtViewChangeValue);
		txtTime = (TextView) findViewById(R.id.txtViewTime);
		imgViewGender = (ImageView) findViewById(R.id.imgViewGender);
		
		btnBuy = (Button) findViewById(R.id.btnBuy);
		
		txtViewHongBao = (TextView) findViewById(R.id.txtViewHongBao);
		txtViewChange = (TextView) findViewById(R.id.txtViewChange);
		txtViewIndex = (TextView) findViewById(R.id.txtViewIndex);
		findViewById(R.id.focus).setOnClickListener(this);
		findViewById(R.id.invest).setOnClickListener(this);
		findViewById(R.id.divest).setOnClickListener(this);
		btnBuy.setOnClickListener(this);
		if (card != null)
		{
			Glide.with(this).load(formatPortrait(card.getHead()))
			.centerCrop()
			.diskCacheStrategy(DiskCacheStrategy.ALL)
			.placeholder(R.drawable.avatar_def).into(cImagePortrait);
			txtLocation.setText(isNullOrEmpty(formatRegion(card.getRegion())) ? "未知" : formatRegion(card.getRegion()));
			txtName.setText(isNullOrEmpty(card.getNick_name()) ? "未知" : card.getNick_name());
			txtCardName.setText(isNullOrEmpty(card.getLabel()) ? "未知" : card.getLabel());
			
			txtValue.setText(isNullOrEmpty(calculateCurrentValue(card.getBid(), card.getPrice())) ? "未知" : calculateCurrentValue(card.getBid(), card.getPrice()));
			txtEnvelopes.setText(isNullOrEmpty(card.getPrice()) ? "未知" : card.getPrice());
			txtTime.setText(isNullOrEmpty(formatTime(card.getTime())) ? "未知" : formatTime(card.getTime()));
			calculateChange(txtChange, card.getBid(), card.getDifference());
			switch(card.getLabel())
			{
			case "戏约卡":
				txtCardName.setTextColor(Color.parseColor(this.getString(R.color.card_blue)));
				btnBuy.setBackgroundColor(Color.parseColor(this.getString(R.color.card_blue)));
				imgViewRightsCard.setBackgroundResource(R.drawable.activity_rights_card_xiyue);
				break;
			case "演出卡":
				txtCardName.setTextColor(Color.parseColor(getString(R.color.card_red)));
				btnBuy.setBackgroundColor(Color.parseColor(this.getString(R.color.card_red)));
				imgViewRightsCard.setBackgroundResource(R.drawable.activity_rights_card_yanchu);
				break;
			case "商务卡":
				txtCardName.setTextColor(Color.parseColor(getString(R.color.card_yellow)));
				btnBuy.setBackgroundColor(Color.parseColor(this.getString(R.color.card_yellow)));
				imgViewRightsCard.setBackgroundResource(R.drawable.activity_rights_card_shangwu);
				break;
				default:
					txtCardName.setTextColor(Color.parseColor(getString(R.color.card_blue)));
					btnBuy.setBackgroundColor(Color.parseColor(this.getString(R.color.card_blue)));
					imgViewRightsCard.setBackgroundResource(R.drawable.activity_rights_card_xiyue);
					break;
			}
			initApplauseConcern(card);
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
	
	private void initApplauseConcern(RightCard rightCard)
	{
		/**
		 * 初始化投资和撤资弹出对话框
		 */
		applauseGiveConcern = new ApplauseGiveConcern( this,
				rightCard.getStar_id(), this, Integer.parseInt(rightCard.getBid()), rightCard.getNick_name());
		if (rightCard.getGender().contains("男"))
		{
			imgViewGender.setBackgroundResource(R.drawable.activity_rights_card_nan);
		}
		else if (rightCard.getGender().contains("女"))
		{
			imgViewGender.setBackgroundResource(R.drawable.activity_rights_card_nv);
		}
	}
	
	private void sendBuyRighCardReuest(RightCard card, int number)
	{
		HashMap<String, String> entity = new HashMap<String, String>();

		entity.put("clientID", Config.User.getClientID());
		entity.put("cardid", card.getCard_id());
		entity.put("star_id", card.getStar_id());
		entity.put("number", String.valueOf(number));
    	List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
    	addToThreadPool(Config.profitOrder, "get start right card", params);
	}
	
    /**
     * 游客进入聊天室，发送获取头像信息请求
     * @param username
     */
    private void sendMemberRequest(RightCard card)
    {
    	if ( card != null)
    	{
        	HashMap<String, String> entity = new HashMap<String, String>();
    		entity.put("clientID", Config.User.getClientID());
    		entity.put("Starer_id", card.getStar_id());
        	List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
        	addToThreadPool(Config.member_in, "get start info", params);
    	}
    }
	
    /**
     * 获取明星信息
     */
    private void sendStarInfoRequest(String starID)
    {
    	HashMap<String, String> entity = new HashMap<String, String>();
    	entity.put("clientID", Config.User.getClientID());
		entity.put("Star_ID", starID);
    	List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
    	ShowProgressDialog(this.getString(R.string.mainactivity_get_start_info));
    	addToThreadPool(Config.star_information, "get start info", params);
    }
	
	/**
	 * 获取价格曲线
	 */
	private void sendKLineGraphRequest(RightCard card) {
		HashMap<String, String> entity = new HashMap<String, String>();
		entity.put("user_id", Config.User.getClientID());
		entity.put("star_id", card.getStar_id());
		entity.put("type", "1");
    	List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
    	addToThreadPool(Config.k_line_graph, "send kLine request", params);
	}
	
	/**
	 * 获取信息
	 */
	private void sendFocusStarListRequest() {
		HashMap<String, String> entity = new HashMap<String, String>();

		entity.put("clientID", Config.User.getClientID());
		entity.put("type", "1");
		
		ShowProgressDialog("获取热门用户基本信息...");		
		List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
		addToThreadPool(Config.stat_list, "send search request", params);
	}
	
    private void addToThreadPool(int taskType, String tag, List<NameValuePair> params)
    {
    	HttpBaseTask httpTask = new HttpBaseTask(ThreadPoolConst.THREAD_TYPE_FILE_HTTP, tag, params, UrlUtil.GetUrl(taskType));
    	httpTask.setTaskType(taskType);
    	InfoHandler handler = new InfoHandler(this);
    	httpTask.setInfoHandler(handler);
    	ThreadPoolFactory.getThreadPoolManager().addTask(httpTask);
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
	
	private String formatTime(String originalTime)
	{
		String[] time = originalTime.split(" ")[0].split("-");
		String formatTime = time[1] + "-" + time[2];
		return formatTime;
	}
	
	private String formatRegion(String region)
	{
		String city = region;
		String[] r = region.split("-");
		if (r.length > 1)
		{
			city = r[1];
		}
		return city;
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
	
	/**
	 * 初始化价值曲线
	 * @param kLink
	 */
	public void setPriceCurve(KLink kLink) {
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
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imgViewUp:
			if (!show)
			{
				imgViewUp.setBackgroundResource(R.drawable.activity_publish_up);
				lLayoutContent.setVisibility(View.VISIBLE);
				switch(card.getLabel())
				{
				case "戏约卡":
					txtContent.setText(getString(R.string.xiyue_desc));
					break;
				case "演出卡":
					txtContent.setText(getString(R.string.yanchu_desc));
					break;
				case "商务卡":
					txtContent.setText(getString(R.string.shangwu_desc));
					break;
				}
				show = true;
			}
			else
			{
				imgViewUp.setBackgroundResource(R.drawable.fragment_right);
				lLayoutContent.setVisibility(View.GONE);
				show = false;
			}
			break;
		case R.id.btnBuy:
			sendBuyRighCardReuest(card, 1);
			break;
			
		case R.id.focus:
			if (applauseGiveConcern != null)
			{
				if (hasFollow)
				{
					applauseGiveConcern.sendUnFocusRequest();
				}
				else
				{
					applauseGiveConcern.sendFocusRequest();
				}
				
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
	public void RequestSuccessful(String jsonString, int taskType) {
		switch (taskType)
		{
		case Config.profitOrder:
			ToastUtil.show(this, "购买成功");
		    break;
// 		case Config.member_in:
// 			try {
// 				Entity<Member> memberEntity = gson.fromJson(jsonString,
// 						new TypeToken<Entity<Member>>() {
// 						}.getType());
// 				
// 				if (memberEntity != null && memberEntity.getData() != null)
// 				{
// 					member = memberEntity.getData();
// 					initApplauseConcern(member);
// 				}
// 			} catch (JsonSyntaxException e) {
// 				e.printStackTrace();
// 				XLog.e("exception: " + e.getMessage());
// 			}
// 			break;
		case Config.give_applause_booed:
			ToastUtil.show(this, "提交成功");
			sendStarInfoRequest(card.getStar_id());
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
			hasFollow = true;
			applauseGiveConcern.showAnimationDialog(R.drawable.circle6,
					R.raw.concern);
			sendStarInfoRequest(InfoCache.getInstance().getStartInfo().getStar_ID());
			break;
		case Config.unfollow_attention:
			//取消关注成功
			hasFollow = false;
			ToastUtil.show(this, "取消关注成功");
			sendFocusStarListRequest();
			break;
		case Config.k_line_graph:
			KLink kLink;
			try {
				Entity<KLink> baseEntity3 = gson.fromJson(jsonString,
						new TypeToken<Entity<KLink>>() {
						}.getType());
				kLink = baseEntity3.getData();
				int diff = Integer.parseInt(kLink.getDifference());
				txtViewChange.setText("昨日涨跌"+diff+"点");
				txtViewHongBao.setText(isNullOrEmpty(kLink.getBonus()) ? "0" : kLink.getBonus());
				txtViewIndex.setText("明星指数：" + kLink.getBid());
				setPriceCurve(kLink);
			} catch (JsonSyntaxException e) {
				e.printStackTrace();
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
			break;
		}
	}

}
