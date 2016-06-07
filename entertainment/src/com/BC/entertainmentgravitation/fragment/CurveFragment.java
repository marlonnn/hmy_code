package com.BC.entertainmentgravitation.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.BC.entertainment.cache.InfoCache;
import com.BC.entertainment.view.CoordinateSystemView;
import com.BC.entertainmentgravitation.ChargeActivity;
import com.BC.entertainmentgravitation.PersonalHomeActivity;
import com.BC.entertainmentgravitation.R;
import com.BC.entertainmentgravitation.dialog.ApplaudDialog;
import com.BC.entertainmentgravitation.dialog.ApplauseGiveConcern;
import com.BC.entertainmentgravitation.dialog.PurchaseDialog;
import com.BC.entertainmentgravitation.entity.EditPersonal;
import com.BC.entertainmentgravitation.entity.KLink;
import com.BC.entertainmentgravitation.entity.Member;
import com.BC.entertainmentgravitation.entity.Point;
import com.BC.entertainmentgravitation.entity.Ranking;
import com.BC.entertainmentgravitation.entity.StarInformation;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.summer.config.Config;
import com.summer.factory.ThreadPoolFactory;
import com.summer.fragment.BaseFragment;
import com.summer.handler.InfoHandler;
import com.summer.json.Entity;
import com.summer.logger.XLog;
import com.summer.task.HttpBaseTask;
import com.summer.treadpool.ThreadPoolConst;
import com.summer.utils.JsonUtil;
import com.summer.utils.StringUtil;
import com.summer.utils.ToastUtil;
import com.summer.utils.UrlUtil;
import com.summer.view.CircularImage;
import com.summer.view.LineChart;

/**
 * 价值曲线
 * @author wen zhong
 *
 */
public class CurveFragment extends BaseFragment implements OnClickListener{
	
	private View rootView;
	private Gson gson;
	
	private CoordinateSystemView coordinateSystemView;
	private CircularImage cImagePortrait;
	private ImageView imgViewPortrait;
	private TextView txtViewName;
	private TextView txtViewCareer;
	private TextView txtViewHongBao;
	private TextView txtViewChange;
	private TextView txtViewIndex;
	private TextView txtViewLocation;
	
	private LineChart lineChart;
	
	ArrayList<Ranking> ranking = new ArrayList<Ranking>();
	
	private float mPosX;
	private float mCurrentPosX;
	private boolean isSlipping = false;
	private int pageIndex = 1;
	private int selectIndex = 0;
	
	private ApplauseGiveConcern applauseGiveConcern;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		gson = new Gson();
		lineChart = new LineChart();
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onStart() {
		sendPersonalInfoRequest();
		sendStarRankRequest();
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
		rootView = inflater.inflate(R.layout.fragment_curve, null);
		return rootView;
	}
	
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		findViewById();
	}
	
	private void findViewById()
	{
		coordinateSystemView = (CoordinateSystemView) rootView.findViewById(R.id.coordinateSystemView);
		cImagePortrait = (CircularImage) rootView.findViewById(R.id.cImageportrait);
		imgViewPortrait = (ImageView) rootView.findViewById(R.id.imgViewPortrait);
		imgViewPortrait.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				/**
				 * 按下
				 * */
				case MotionEvent.ACTION_DOWN:
					mPosX = event.getX();
					isSlipping = true;
					break;
				/**
				 * 移动
				 * */
				case MotionEvent.ACTION_MOVE:
					mCurrentPosX = event.getX();
					if(isSlipping){
						if (mCurrentPosX - mPosX > 10){
							isSlipping = false;
							nextOne();
						}
						else if (mPosX - mCurrentPosX > 10){
							isSlipping = false;
							lastOne();
						}
					}
				
					break;
				// 拿起
				case MotionEvent.ACTION_UP:
					if(isSlipping){
						Intent intent = new Intent();
						intent.setClass(getActivity(), PersonalHomeActivity.class);
						Bundle bundle = new Bundle();
						bundle.putSerializable("member", CreateMember());
						intent.putExtras(bundle);
						startActivity(intent);
					}
					break;
				default:
					break;
				}
				return true;
			}
		});
		txtViewName = (TextView) rootView.findViewById(R.id.txtViewName);
		txtViewCareer = (TextView) rootView.findViewById(R.id.txtViewCareer);
		txtViewHongBao = (TextView) rootView.findViewById(R.id.txtViewHongBao);
		txtViewChange = (TextView) rootView.findViewById(R.id.txtViewChange);
		txtViewIndex = (TextView) rootView.findViewById(R.id.txtViewIndex);
		txtViewLocation = (TextView) rootView.findViewById(R.id.txtViewLocation);
		cImagePortrait.setOnClickListener(this);
		rootView.findViewById(R.id.focus).setOnClickListener(this);
		rootView.findViewById(R.id.invest).setOnClickListener(this);
		rootView.findViewById(R.id.divest).setOnClickListener(this);
		
	}
	
	/** 
	 * 首页展示,切换明星
	 * 上一个
	 ***/
	public void lastOne(){
		if (selectIndex > 0) {
			selectIndex--;
			sendStarInfoRequest(ranking.get(selectIndex)
					.getStar_ID());
		} else {
			ToastUtil.show(getActivity(), "没有更多数据了");
		}
	}
	
	/** 
	 * 首页展示,切换明星
	 * 下一个
	 ***/
	public void nextOne(){
		if (selectIndex < ranking.size() - 1) {
			selectIndex++;
			sendStarInfoRequest(ranking.get(selectIndex)
					.getStar_ID());
		} else {
			pageIndex++;
			sendStarRankRequest();
		}
	}
	
	/**
	 * 获取明星排行信息
	 */
	private void sendStarRankRequest() {
		if (Config.User == null) {
			ToastUtil.show(getActivity(), "无法获取信息");
			return;
		}
		HashMap<String, String> entity = new HashMap<String, String>();

		entity.put("clientID", Config.User.getClientID());
		entity.put("The_page_number", "" + pageIndex);
		entity.put("type", "1");

		ShowProgressDialog("获取信息...");
    	List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
    	addToThreadPool(Config.in_comparison_to_listApply_to_be_a_platform_star_, "get start rank info", params);
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
	 * 初始化价值曲线
	 * @param kLink
	 */
	public void setPriceCurve(KLink kLink) {
		if (kLink == null || kLink.getPoint() == null
				|| kLink.getPoint().size() == 0) {
			ToastUtil.show(getActivity(), "暂无数据");
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

    /**
     * 获取用户信息
     */
    private void sendPersonalInfoRequest()
    {
    	if (Config.User == null)
    	{
			ToastUtil.show(getActivity(), StringUtil.getXmlResource(getActivity(), R.string.mainactivity_login_invalidate));
			return;
    	}
    	HashMap<String, String> entity = new HashMap<String, String>();
    	entity.put("clientID", Config.User.getClientID());
    	List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
    	ShowProgressDialog(this.getString(R.string.mainactivity_get_user_info));
    	addToThreadPool(Config.personal_information, "get user info", params);
    }
	
	/**
	 * 获取价格曲线
	 */
	private void sendKLineGraphRequest() {
		HashMap<String, String> entity = new HashMap<String, String>();
		entity.put("user_id", Config.User.getClientID());
		entity.put("star_id", ranking.get(selectIndex).getStar_ID());
		entity.put("type", "1");
    	List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
//    	ShowProgressDialog("获取折线图...");
    	addToThreadPool(Config.k_line_graph, "send kLine request", params);
	}
	
    private void addToThreadPool(int taskType, String tag, List<NameValuePair> params)
    {
    	HttpBaseTask httpTask = new HttpBaseTask(ThreadPoolConst.THREAD_TYPE_FILE_HTTP, tag, params, UrlUtil.GetUrl(taskType));
    	httpTask.setTaskType(taskType);
    	InfoHandler handler = new InfoHandler(this);
    	httpTask.setInfoHandler(handler);
    	ThreadPoolFactory.getThreadPoolManager().addTask(httpTask);
    }
    
	private Member CreateMember()
	{
		Member m = new Member();
		m.setId(InfoCache.getInstance().getStartInfo().getStar_ID());
		m.setNick(InfoCache.getInstance().getStartInfo().getStage_name());
		m.setName(InfoCache.getInstance().getStartInfo().getUser_name());
		m.setPortrait(InfoCache.getInstance().getStartInfo().getHead_portrait());
		m.setGender(InfoCache.getInstance().getStartInfo().getGender());
		m.setRegion(InfoCache.getInstance().getStartInfo().getRegion());
		m.setConstellation(InfoCache.getInstance().getStartInfo().getThe_constellation());
		m.setNationality(InfoCache.getInstance().getStartInfo().getNationality());
		m.setLanguage(InfoCache.getInstance().getStartInfo().getLanguage());
		m.setDollar(InfoCache.getInstance().getStartInfo().getEntertainment_dollar());
		m.setBid(String.valueOf(InfoCache.getInstance().getStartInfo().getThe_current_hooted_thumb_up_prices()));
		return m;
	}
	
	@Override
	public void onClick(View v) {
		Intent intent;
		switch(v.getId())
		{
		case R.id.cImageportrait:
			intent = new Intent();
			intent.setClass(getActivity(), PersonalHomeActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable("member", CreateMember());
			intent.putExtras(bundle);
			startActivity(intent);
			break;
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
		}
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
	
//	@Override
//	public void RequestFailed(int errcode, String message, int taskType) {
//		switch(taskType)
//		{
//		case Config.give_applause_booed:
//			showPurchaseDialog();
//			break;
//		}
//		super.RequestFailed(errcode, message, taskType);
//	}
	
	private void showPurchaseDialog()
	{
		PurchaseDialog.Builder builder = new PurchaseDialog.Builder(getActivity());
		builder.setTitle("购买娛币");
		builder.setMessage("娛币不足，是否购买娛币？");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(getActivity(), ChargeActivity.class);
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

	@Override
	public void RequestSuccessful(String jsonString, int taskType) {
		switch(taskType)
		{
		case Config.give_applause_booed:
			ToastUtil.show(getActivity(), "提交成功");
			sendStarInfoRequest(InfoCache.getInstance().getStartInfo().getStar_ID());
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
			ToastUtil.show(getActivity(), "提交成功");
			applauseGiveConcern.showAnimationDialog(R.drawable.circle6,
					R.raw.concern);
			sendStarInfoRequest(InfoCache.getInstance().getStartInfo().getStar_ID());
			break;
		case Config.personal_information:
			Entity<EditPersonal> baseEntity = gson.fromJson(jsonString,
					new TypeToken<Entity<EditPersonal>>() {
					}.getType());
			if (baseEntity.getData() != null)
			{
				InfoCache.getInstance().setPersonalInfo(baseEntity.getData());
			}

			break;
		case Config.star_information:
			Entity<StarInformation> startInfo = gson.fromJson(jsonString,
					new TypeToken<Entity<StarInformation>>() {
					}.getType());
			if (startInfo != null)
			{
				InfoCache.getInstance().setStartInfo(startInfo.getData());
				InfoCache.getInstance().AddToStarInfoList(startInfo.getData());
				
				Glide.with(this).load(InfoCache.getInstance().getStartInfo().getHead_portrait())
				.centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL)
				.placeholder(R.drawable.home_image).into(imgViewPortrait);
				
				txtViewName.setText(InfoCache.getInstance().getStartInfo().getStage_name());
				txtViewLocation.setText(InfoCache.getInstance().getStartInfo().getRegion());
				txtViewCareer.setText(InfoCache.getInstance().getStartInfo().getProfessional());
				
				Glide.with(this).load(InfoCache.getInstance().getStartInfo().getHead_portrait())
				.centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL)
				.placeholder(R.drawable.home_image).into(cImagePortrait);
				
				applauseGiveConcern = new ApplauseGiveConcern(getActivity(),
						InfoCache.getInstance().getStartInfo().getStar_ID(), this,
						InfoCache.getInstance().getStartInfo()
								.getThe_current_hooted_thumb_up_prices(),
						InfoCache.getInstance().getStartInfo().getStage_name());
				
				sendKLineGraphRequest();
			}
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
				txtViewIndex.setText("明星储备指数：" + kLink.getBid());
				setPriceCurve(kLink);
			} catch (JsonSyntaxException e) {
				e.printStackTrace();
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
			break;
		case Config.in_comparison_to_listApply_to_be_a_platform_star_:
			try {
				Entity<List<Ranking>> baseEntity4 = gson.fromJson(jsonString,
						new TypeToken<Entity<List<Ranking>>>() {
						}.getType());
				ranking.addAll(baseEntity4.getData());
				if (ranking != null && baseEntity4.getData().size() > 0) {
//					if (selectIndex != 0) {
//						selectIndex++;
//					}
					sendStarInfoRequest(ranking.get(selectIndex).getStar_ID());
				} else {
					ToastUtil.show(getActivity(), this.getString(R.string.mainactivity_have_no_data));
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			break;
		}
	}

}
