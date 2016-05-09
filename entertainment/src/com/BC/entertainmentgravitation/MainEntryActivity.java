package com.BC.entertainmentgravitation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.BC.entertainment.cache.ChatCache;
import com.BC.entertainment.cache.InfoCache;
import com.BC.entertainment.chatroom.helper.LogoutHelper;
import com.BC.entertainmentgravitation.R;
import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.BC.entertainmentgravitation.entity.EditPersonal;
import com.BC.entertainmentgravitation.entity.Member;
import com.BC.entertainmentgravitation.entity.Ranking;
import com.BC.entertainmentgravitation.entity.Search;
import com.BC.entertainmentgravitation.entity.StarInformation;
import com.BC.entertainmentgravitation.entity.StarLiveVideoInfo;
import com.BC.entertainmentgravitation.fragment.JiaGeQuXianFragment2;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.ChatRoomServiceObserver;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomInfo;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomKickOutEvent;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMember;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomStatusChangeData;
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomData;
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomResultData;
import com.summer.activity.BaseActivity;
import com.summer.config.Config;
import com.summer.factory.ThreadPoolFactory;
import com.summer.handler.InfoHandler;
import com.summer.handler.InfoHandler.InfoReceiver;
import com.summer.json.Entity;
import com.summer.logger.XLog;
import com.summer.task.HttpBaseTask;
import com.summer.treadpool.ThreadPoolConst;
import com.summer.utils.JsonUtil;
import com.summer.utils.StringUtil;
import com.summer.utils.ToastUtil;
import com.summer.utils.UrlUtil;
import com.summer.view.CircularImage;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainEntryActivity extends BaseActivity implements OnClickListener, UpdataMainActivity{
	

	private View Center, SignIn, activitys, topUp, friends, information, level,
			ToApplyFor, gift, redEnvelope, LuckyDraw, toLevel, searchButton,
			account;
	private EditText searchEdit;
	public static AuthoritativeInformation authoritativeInformation;

	public static final String PACKAGE_NAME = "com.BC.entertainmentgravitation";
	public static final String RAW_PATH = "/data"
			+ Environment.getDataDirectory().getAbsolutePath() + "/"
			+ PACKAGE_NAME;

	private TextView nickname, levelText, Entertainment_dollar, Stage_name,
			star_information, professional, prices, aplayOrLive;
	private int pageIndex = 1;
	private int selectIndex = 0;
	private CircularImage Head_portrait;
	private ImageView details, advertisement, star_Head_portrait;
	private ImageButton previous, next;
	
	ArrayList<Ranking> ranking = new ArrayList<Ranking>();
	ArrayList<Search> searchs;

	private JiaGeQuXianFragment2 jiaGeQuXianFragment;
	
	private float mPosX;
	private float mCurrentPosX;
	private boolean isSlipping = false;
	private View imageIcon;
	
	private InfoReceiver infoRreceiver;
	private StarLiveVideoInfo watchVideo;
	
	private AbortableFuture<EnterChatRoomResultData> enterRequest;//聊天室

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerObservers(true);
		nickname = (TextView) findViewById(R.id.nickname);
		levelText = (TextView) findViewById(R.id.levelText);
		Entertainment_dollar = (TextView) findViewById(R.id.Entertainment_dollar);
		Stage_name = (TextView) findViewById(R.id.Stage_name);
		star_information = (TextView) findViewById(R.id.star_information);
		professional = (TextView) findViewById(R.id.professional);
		prices = (TextView) findViewById(R.id.prices);
		aplayOrLive = (TextView) findViewById(R.id.main_aplay);
		searchEdit = (EditText) findViewById(R.id.searchEdit);
		Head_portrait = (CircularImage) findViewById(R.id.Head_portrait);
		star_Head_portrait = (ImageView) findViewById(R.id.star_Head_portrait);
		details = (ImageView) findViewById(R.id.details);
		advertisement = (ImageView) findViewById(R.id.advertisement);
		// imageView6 = (ImageView) findViewById(R.id.imageView6);
		previous = (ImageButton) findViewById(R.id.previous);
		next = (ImageButton) findViewById(R.id.next);

		jiaGeQuXianFragment = (JiaGeQuXianFragment2) getSupportFragmentManager()
				.findFragmentById(R.id.fragment1);

		Center = findViewById(R.id.Center);
		SignIn = findViewById(R.id.SignIn);
		activitys = findViewById(R.id.activitys);
		topUp = findViewById(R.id.topUp);
		friends = findViewById(R.id.friends);
		information = findViewById(R.id.information);
		level = findViewById(R.id.level);
		ToApplyFor = findViewById(R.id.ToApplyFor);
		gift = findViewById(R.id.gift);
		redEnvelope = findViewById(R.id.redEnvelope);
		LuckyDraw = findViewById(R.id.LuckyDraw);
		toLevel = findViewById(R.id.toLevel);
		searchButton = findViewById(R.id.searchButton);
		account = findViewById(R.id.account);
		imageIcon = findViewById(R.id.imageViewicon);

		Head_portrait.setImageResource(R.drawable.home_image);

		Center.setOnClickListener(this);
		SignIn.setOnClickListener(this);
		activitys.setOnClickListener(this);
		topUp.setOnClickListener(this);
		friends.setOnClickListener(this);
		information.setOnClickListener(this);
		level.setOnClickListener(this);
		ToApplyFor.setOnClickListener(this);
		gift.setOnClickListener(this);
		redEnvelope.setOnClickListener(this);
		LuckyDraw.setOnClickListener(this);
		searchButton.setOnClickListener(this);

		details.setOnClickListener(this);
		
		
		setImageListener();
		toLevel.setOnClickListener(this);
		previous.setOnClickListener(this);
		next.setOnClickListener(this);

		account.setOnClickListener(this);
		jiaGeQuXianFragment.setUpdataMainActivity(this);
		
		if (Config.User != null && Config.User.getPermission().contains("2"))
		{
			aplayOrLive.setText("直播");
			imageIcon.setBackgroundResource(R.drawable.icon_zhibo);
		}
		else
		{
			aplayOrLive.setText("申请");
		}
		
		infoRreceiver = new InfoReceiver() {
			
			@Override
			public void onNotifyText(String notify) {
				
			}
			
			@Override
			public void onInfoReceived(int errorCode, HashMap<String, Object> items) {
				RemoveProgressDialog();
		        if (errorCode == 0)
		        {
		            XLog.i(errorCode);
		            String jsonString = (String) items.get("content");
		            if (jsonString != null)
		            {
		                JSONObject object;
		                try {
		                    object = new JSONObject(jsonString);
		                    String msg = object.optString("msg");
		                    int code = object.optInt("status", -1);
		                    int taskType = (Integer) items.get("taskType");
		                    if (code == 0)
		                    {
		                        RequestSuccessful(jsonString, taskType);
		                    }
		                    else
		                    {
		                    	if (code == 500 && msg.contains("没有记录"))
		                    	{
		    						Intent intent = new Intent(MainEntryActivity.this, DetailsActivity.class);
		    						startActivity(intent);
		                    	}
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
			}
		};
    }
    
    private void queryVideoStatus(StarLiveVideoInfo watchVideo)
    {
    	if (watchVideo == null || watchVideo.getCid() == null)
    	{
			ToastUtil.show(this, "直播间不在直播中，请稍后重试");
			return;
    	}
    	
		HashMap<String, String> entity = new HashMap<String, String>();
		entity.put("cid", watchVideo.getCid());
		ShowProgressDialog("查询直播中...");
		List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
		addToThreadPool(Config.query_video_status, "send search request", params);
    }
    
	@Override
	protected void onResume() {
		super.onResume();
	}



	@Override
	protected void onStart() {
		super.onStart();
		
		getUserInfoRequest();
		getStartRankInfoRequest();
		getAdvertiseInfoRequest();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		registerObservers(false);
        // 清理缓存&注销监听
        LogoutHelper.logout();
	}

	/**
	 * 图片滑动切换用户
	 */
	public void setImageListener() {
		details.setOnTouchListener(new View.OnTouchListener() {
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
//						Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
//						startActivity(intent);
						if (InfoCache.getInstance().getStartInfo() != null && InfoCache.getInstance().getStartInfo().getUser_name() != null)
						{
							watchLiveVideoRequest(InfoCache.getInstance().getStartInfo().getUser_name());	
						}
					}
					break;
				default:
					break;
				}
				return true;
			}
		});
	}
	
	/** 
	 * 首页展示,切换明星
	 * 上一个
	 ***/
	public void lastOne(){
		if (selectIndex > 0) {
			selectIndex--;
			if (searchs != null) {
				getStarInfoRequest(searchs.get(selectIndex).getSearch());
			} else {
				getStarInfoRequest(ranking.get(selectIndex)
						.getStar_ID());
			}
		} else {
			ToastUtil.show(this, "没有更多数据了");
		}
	}
	
	/** 
	 * 首页展示,切换明星
	 * 下一个
	 ***/
	public void nextOne(){
		if (searchs != null) {
			if (selectIndex < searchs.size() - 1) {
				selectIndex++;
				getStarInfoRequest(searchs.get(selectIndex).getSearch());
			} else {
				ToastUtil.show(this, "没有更多数据了");
			}
		} else {
			if (selectIndex < ranking.size() - 1) {
				selectIndex++;
				getStarInfoRequest(ranking.get(selectIndex)
						.getStar_ID());
			} else {
				pageIndex++;
				getStartRankInfoRequest();
			}
		}
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		getUserInfoRequest();
		jiaGeQuXianFragment.sendKLineGraphRequest();
	}

    /**
     * 获取用户信息
     */
    private void getUserInfoRequest()
    {
    	if (Config.User == null)
    	{
			ToastUtil.show(this, StringUtil.getXmlResource(this, R.string.mainactivity_login_invalidate));
			finish();
			return;
    	}
    	HashMap<String, String> entity = new HashMap<String, String>();
    	entity.put("clientID", Config.User.getClientID());
    	List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
    	ShowProgressDialog(this.getString(R.string.mainactivity_get_user_info));
    	addToThreadPool(Config.personal_information, "get user info", params);
    }
    
    /**
     * 获取明星信息
     */
    private void getStarInfoRequest(String starID)
    {
    	HashMap<String, String> entity = new HashMap<String, String>();
    	entity.put("clientID", Config.User.getClientID());
		entity.put("Star_ID", starID);
    	List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
    	ShowProgressDialog(this.getString(R.string.mainactivity_get_start_info));
    	addToThreadPool(Config.star_information, "get start info", params);
    }
    
	/**
	 * 获取广告及公告信息
	 */
    private void getAdvertiseInfoRequest()
    {
    	HashMap<String, String> entity = new HashMap<String, String>();
    	if(Config.User != null && Config.User.getClientID() != null)
    	{
        	entity.put("clientID", Config.User.getClientID());
        	List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
        	addToThreadPool(Config.activities, "get advertisement info", params);
    	}

    }
    
	/**
	 * 获取明星排行信息
	 */
    private void getStartRankInfoRequest()
    {
    	if (Config.User == null)
    	{
			ToastUtil.show(this, StringUtil.getXmlResource(this, R.string.mainactivity_fail_get_sart_info));
			return;
    	}
    	HashMap<String, String> entity = new HashMap<String, String>();
    	entity.put("clientID", Config.User.getClientID());
    	entity.put("The_page_number", "" + pageIndex);
    	entity.put("type", "1");
    	List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
    	ShowProgressDialog(this.getString(R.string.mainactivity_get_sart_info));
    	addToThreadPool(Config.in_comparison_to_listApply_to_be_a_platform_star_, "get start rank info", params);
    }
    
	/**
	 * 搜索
	 */
	private void sendSearchRequest(String search) 
	{
    	if (Config.User == null)
    	{
			ToastUtil.show(this, StringUtil.getXmlResource(this, R.string.mainactivity_fail_get_sart_info));
			return;
    	}
    	
		HashMap<String, String> entity = new HashMap<String, String>();
		entity.put("search", search);
		ShowProgressDialog(this.getString(R.string.mainactivity_get_sart_info));
		List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
		addToThreadPool(Config.in_comparison_to_listApply_to_be_a_platform_star_, "send search request", params);
	}
	
	private void createLiveVideoRequest()
	{
    	if (Config.User == null)
    	{
			ToastUtil.show(this, StringUtil.getXmlResource(this, R.string.mainactivity_fail_get_sart_info));
			return;
    	}
    	HashMap<String, String> entity = new HashMap<String, String>();
    	entity.put("username", Config.User.getUserName());
		List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
		addToThreadPool(Config.create_video, "send create live video request", params);
	}
	
	private void watchLiveVideoRequest(String starName)
	{
		XLog.i("star id : " + starName);
    	if (Config.User == null)
    	{
			ToastUtil.show(this, StringUtil.getXmlResource(this, R.string.mainactivity_fail_get_sart_info));
			return;
    	}
    	HashMap<String, String> entity = new HashMap<String, String>();
    	entity.put("username", starName);
		List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
		addToThreadPool(Config.query_video, "send watch video request", params);
	}
    
    private void addToThreadPool(int taskType, String Tag, List<NameValuePair> params)
    {
    	HttpBaseTask httpTask = new HttpBaseTask(ThreadPoolConst.THREAD_TYPE_FILE_HTTP, Tag, params, UrlUtil.GetUrl(taskType));
    	httpTask.setTaskType(taskType);
    	InfoHandler handler = new InfoHandler(infoRreceiver);
    	httpTask.setInfoHandler(handler);
    	ThreadPoolFactory.getThreadPoolManager().addTask(httpTask);
    }
    
	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) 
		{
		/**
		 * 个人中心
		 */
		case R.id.Center:
			intent = new Intent(this, PersonalActivity.class);
			startActivity(intent);
			break;
		/**
		 * 签到
		 */
		case R.id.SignIn:
			intent = new Intent(this, SignInActivity.class);
			startActivity(intent);
			break;
		/**
		 * 充值
		 */
		case R.id.topUp:
			intent = new Intent(this, ChargeActivity.class);
			startActivity(intent);
			break;
		/**
		 * 活动
		 */
		case R.id.activitys:
			intent = new Intent(this, HuodongActivity.class);
			startActivity(intent);
			break;
		/**
		 * 好友
		 */
		case R.id.friends:
			intent = new Intent(this, FriendsActivity.class);
			startActivity(intent);
			break;
		/**
		 * 消息
		 */
		case R.id.information:
			intent = new Intent(this, InformationActivity.class);
			startActivity(intent);
			break;
		/**
		 * 点赞排行
		 */
		case R.id.level:
			intent = new Intent(this, LevelActivity.class);
			startActivity(intent);
			break;
		/**
		 * 申请
		 */
		case R.id.ToApplyFor:
//			if (!user.getPermission().equals("2")) {
//				intent = new Intent(this, ToApplyForActivity.class);
//				startActivity(intent);
//			} else {
//				ToastUtil.show(this, "您已出道成为明星了");
//			}
//			if (Config.User.getPermission().equals("2")) 
//			{
//				createLiveVideoRequest();
//			}
//			else
//			{
//				intent = new Intent(this, ApplyActivity.class);
//				startActivity(intent);
//			}
			
			intent = new Intent(this, PushActivity.class);
			startActivity(intent);
			break;
		/**
		 * 礼品
		 */
		case R.id.gift:
//			intent = new Intent(this, GiftActivity2.class);
//			startActivity(intent);
			break;
		/**
		 * 红包
		 */
		case R.id.redEnvelope:
			intent = new Intent(this, RedEnvelopeActivity.class);
			startActivity(intent);
			break;
		/**
		 * 抽奖
		 */
		case R.id.LuckyDraw:
//			intent = new Intent(this, LuckyDrawActivity.class);
//			startActivity(intent);
			break;
		/**
		 * 上一个明星
		 */
		case R.id.previous:
			lastOne();
			break;
		/**
		 * 下一个明星
		 */
		case R.id.next:
			nextOne();
			break;
		/**
		 * 排行榜
		 */
		case R.id.toLevel:
			intent = new Intent(this, StarLevelActivity.class);
			startActivity(intent);
			break;
		/**
		 * 明星详情
		 */
		case R.id.details:
			intent = new Intent(this, DetailsActivity.class);
			startActivity(intent);
//			watchLiveVideoRequest();
			break;
		/**
		 * 查询娱币交易信息
		 */
		case R.id.account:
			intent = new Intent(this, AccountActivity.class);
			startActivity(intent);
			break;
		/**
		 * 搜索
		 */
		case R.id.searchButton:
			String search = searchEdit.getText().toString();
			selectIndex = 0;
			if (search.equals("")) {
				searchs = null;
				sendReqConnect();
			} else {
				sendReqSearch(search);
			}
			break;

		default:
			break;
		}
	}
	
	/**
	 * 获取明星排行信息
	 */
	private void sendReqConnect() {
		if (Config.User == null) {
			ToastUtil.show(this, "无法获取信息");
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
	 * 搜索
	 */
	private void sendReqSearch(String search) {
		if (Config.User == null) {
			ToastUtil.show(this, "无法获取信息");
			return;
		}
		HashMap<String, String> entity = new HashMap<String, String>();

		entity.put("search", search);

		ShowProgressDialog("获取信息...");
		
    	List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
    	addToThreadPool(Config.search, "get start rank info", params);
	}
	
	

	@Override
	public void RequestSuccessful(String jsonString, int taskType) {
		Gson gson = new Gson();
		XLog.i("taskType: " + taskType + " json string: " + jsonString);
		switch(taskType)
		{
		case Config.personal_information:
			Entity<EditPersonal> baseEntity = gson.fromJson(jsonString,
					new TypeToken<Entity<EditPersonal>>() {
					}.getType());
			if (baseEntity.getData() != null)
			{
				InfoCache.getInstance().setPersonalInfo(baseEntity.getData());
				if (InfoCache.getInstance().getPersonalInfo() != null) {
					InfoCache.getInstance().getPersonalInfo().getBirthday();
					nickname.setText(InfoCache.getInstance().getPersonalInfo().getNickname());
					levelText.setText("Lv." + InfoCache.getInstance().getPersonalInfo().getLevel());
					
					UpdateDallorTask task = new UpdateDallorTask(this,
							Long.valueOf(InfoCache.getInstance().getPersonalInfo().getEntertainment_dollar()));
					task.execute();
					Glide.with(this).load(InfoCache.getInstance().getPersonalInfo().getHead_portrait())
							.centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL)
							.placeholder(R.drawable.home_image).into(Head_portrait);
				}
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
				if(InfoCache.getInstance().getStartInfo() != null)
				{
					Glide.with(this).load(InfoCache.getInstance().getStartInfo().getFirst_album())
					.centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL)
					.placeholder(R.drawable.home_image).into(details);
			        jiaGeQuXianFragment.initStarInformation();
				}
			}
			break;
		case Config.activities:
			Entity<AuthoritativeInformation> adInfo = gson.fromJson(jsonString,
					new TypeToken<Entity<AuthoritativeInformation>>() {
					}.getType());
			authoritativeInformation = adInfo.getData();
			if (authoritativeInformation != null
					&& authoritativeInformation.getAdvertising() != null
					&& authoritativeInformation.getAdvertising().size() > 0) {
				Glide.with(this)
						.load(authoritativeInformation.getAdvertising().get(0)
								.getPicture_address()).centerCrop()
						.diskCacheStrategy(DiskCacheStrategy.ALL)
						.placeholder(R.drawable.home_ad).into(advertisement);
				advertisement.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(v.getContext(),
								BrowserAcitvity.class);
						intent.putExtra("url", authoritativeInformation
								.getAdvertising().get(0).getThe_link_address());
						startActivity(intent);
					}
				});
			}
			break;
		case Config.in_comparison_to_listApply_to_be_a_platform_star_:
			Entity<List<Ranking>> baseEntity4 = gson.fromJson(jsonString,
					new TypeToken<Entity<List<Ranking>>>() {
					}.getType());
			ranking.addAll(baseEntity4.getData());
			if (ranking != null && baseEntity4.getData().size() > 0) {
				if (selectIndex != 0) {
					selectIndex++;
				}
				getStarInfoRequest(ranking.get(selectIndex).getStar_ID());
			} else {
				ToastUtil.show(this, this.getString(R.string.mainactivity_have_no_data));
			}
			break;
		case Config.search:
			Entity<ArrayList<Search>> baseEntity5 = gson.fromJson(
					jsonString, new TypeToken<Entity<ArrayList<Search>>>() {
					}.getType());
			searchs = baseEntity5.getData();
			if (searchs != null && baseEntity5.getData().size() > 0) {
				if (selectIndex != 0) {
					selectIndex++;
				}
				getStarInfoRequest(searchs.get(selectIndex).getSearch());
			} else {
				searchs = null;
				ToastUtil.show(this, this.getString(R.string.mainactivity_have_no_search_data));
			}
			break;
		case Config.create_video:
			Entity<StarLiveVideoInfo> starLiveInfoEntity = gson.fromJson(jsonString,
					new TypeToken<Entity<StarLiveVideoInfo>>() {
					}.getType());
			StarLiveVideoInfo startLiveVideoInfo = starLiveInfoEntity.getData();
			XLog.i(startLiveVideoInfo.getChatroomid());
			XLog.i(startLiveVideoInfo.getPushUrl());
			XLog.i(startLiveVideoInfo.getCid());
//			startLiveVideo(startLiveVideoInfo);
			enterChatRoom(startLiveVideoInfo, true);
			break;
		case Config.query_video:
			Entity<StarLiveVideoInfo> watchVideoEntity = gson.fromJson(jsonString,
					new TypeToken<Entity<StarLiveVideoInfo>>() {
					}.getType());
			watchVideo = watchVideoEntity.getData();
//			queryVideoStatus(watchVideo);
//			startWatchVideo(watchVideo);
			enterChatRoom(watchVideo, false);
			break;
			
			
		case Config.query_video_status:
			
			if (jsonString != null)
			{
				try {
					JSONObject jsonObj = new JSONObject(jsonString);
					String data =  jsonObj.getString("data");
					int status =   jsonObj.getInt("status");
					if (status == 0 && data != null)
					{
						JSONObject ret = jsonObj.getJSONObject("data").getJSONObject("ret"); 
						if (ret != null)
						{
							if( ret.getInt("status") == 1)
							{
								startWatchVideo(watchVideo);
							}
							else
							{
								Toast.makeText(MainEntryActivity.this, "主播不在直播间，请稍后再试", Toast.LENGTH_SHORT).show();
//								finish();
							}
						}
					}

				} catch (JSONException e) {
					e.printStackTrace();
					XLog.e("JSONException");
					finish();
				} 

			}
			break;
		}
		
	}
	
	/**
	 * 明星直播
	 * @param startLiveVideoInfo
	 */
	private void startLiveVideo(StarLiveVideoInfo startLiveVideoInfo)
	{
		Intent intent = new Intent(MainEntryActivity.this, PushActivity.class);
		Bundle bundle=new Bundle();
		bundle.putString("cid", startLiveVideoInfo.getCid());
		bundle.putString("pushUrl", startLiveVideoInfo.getPushUrl());
		bundle.putString("chatroomid", startLiveVideoInfo.getChatroomid());
		bundle.putBoolean("isMaster", true);
		intent.putExtras(bundle);
		startActivity(intent); 
	}
	
	/**
	 * 群众看直播
	 * @param startLiveVideoInfo
	 */
	private void startWatchVideo(StarLiveVideoInfo startLiveVideoInfo)
	{
		if(startLiveVideoInfo != null && startLiveVideoInfo.getHttpPullUrl() != null && !startLiveVideoInfo.getHttpPullUrl().isEmpty())
		{
			Intent intent = new Intent(MainEntryActivity.this, PullActivity.class);
			Bundle bundle=new Bundle();
			bundle.putString("cid", startLiveVideoInfo.getCid());
			bundle.putString("httpPullUrl", startLiveVideoInfo.getHttpPullUrl());
			bundle.putString("chatroomid", startLiveVideoInfo.getChatroomid());
			bundle.putBoolean("isMaster", false);
			intent.putExtras(bundle);
			startActivity(intent); 
		}
		else
		{
			ToastUtil.show(this, "该直播暂未开始！");
		}
	}
	
    @SuppressWarnings("unchecked")
	private void enterChatRoom(final StarLiveVideoInfo startLiveVideoInfo, final boolean isPush)
    {
        EnterChatRoomData data = new EnterChatRoomData(startLiveVideoInfo.getChatroomid());
        enterRequest = NIMClient.getService(ChatRoomService.class).enterChatRoom(data);
        enterRequest.setCallback(new RequestCallback<EnterChatRoomResultData>(){

			@Override
			public void onException(Throwable exception) {
				 onLoginDone();
				 XLog.i("enter chat room exception, e=" + exception.getMessage());
	             Toast.makeText(MainEntryActivity.this, 
	            		 StringUtil.getXmlResource(MainEntryActivity.this, R.string.push_video_nim_login_exception) + exception.getMessage(),
	            		 Toast.LENGTH_SHORT).show();
	             finish();
			}

			@Override
			public void onFailed(int code) {
                onLoginDone();
                if (code == ResponseCode.RES_CHATROOM_BLACKLIST) {
                    Toast.makeText(MainEntryActivity.this, 
                    		StringUtil.getXmlResource(MainEntryActivity.this, R.string.push_video_nim_black_list), 
                    		Toast.LENGTH_SHORT).show();
                } else {
                	XLog.i("enter chat room failed, code=" + code);
                    Toast.makeText(MainEntryActivity.this, "enter chat room failed, code=" + code, Toast.LENGTH_SHORT).show();
                }
                finish();
			}

			@Override
			public void onSuccess(EnterChatRoomResultData result) {
				onLoginDone();
				ChatRoomInfo roomInfo = result.getRoomInfo();
                ChatRoomMember member = result.getMember();
                member.setRoomId(roomInfo.getRoomId());
                ChatCache.getInstance().ClearMember();
                ChatCache.getInstance().AddMember(createMasterMember());
                
                ChatCache.getInstance().getChatRoom().setChatRoomInfo(roomInfo);
                if (isPush)
                {
                    startLiveVideo(startLiveVideoInfo);	
                }
                else
                {
                	startWatchVideo(startLiveVideoInfo);
                }
                XLog.i("enter chat room success" + roomInfo.getRoomId());
			}});
    }
    
    private Member createMasterMember()
    {
    	Member m = new Member();
    	m.setId(Config.User.getClientID());
    	m.setName(Config.User.getUserName());
        m.setPortrait(InfoCache.getInstance().getPersonalInfo().getHead_portrait());
        m.setAge(InfoCache.getInstance().getPersonalInfo().getAge());
        m.setNick(InfoCache.getInstance().getPersonalInfo().getNickname());
        m.setDollar(InfoCache.getInstance().getPersonalInfo().getEntertainment_dollar());
        m.setPiao(InfoCache.getInstance().getPersonalInfo().getPiao());
    	return m;
    }
    
    private void onLoginDone() {
        enterRequest = null;
        DialogMaker.dismissProgressDialog();
    }
    
	/************************** 注册 ***************************/
    private void registerObservers(boolean register) {
        NIMClient.getService(ChatRoomServiceObserver.class).observeOnlineStatus(onlineStatus, register);
        NIMClient.getService(ChatRoomServiceObserver.class).observeKickOutEvent(kickOutObserver, register);
    }
    
    @SuppressWarnings("serial")
	Observer<ChatRoomStatusChangeData> onlineStatus = new Observer<ChatRoomStatusChangeData>() {
        @Override
        public void onEvent(ChatRoomStatusChangeData chatRoomStatusChangeData) {
            if (chatRoomStatusChangeData.status == StatusCode.CONNECTING) {
                DialogMaker.updateLoadingMessage(StringUtil.getXmlResource(MainEntryActivity.this, R.string.push_video_nim_status_connecting));
            } else if (chatRoomStatusChangeData.status == StatusCode.UNLOGIN) {
                Toast.makeText(MainEntryActivity.this, R.string.push_video_nim_status_unlogin, Toast.LENGTH_SHORT).show();
            } else if (chatRoomStatusChangeData.status == StatusCode.LOGINING) {
                DialogMaker.updateLoadingMessage(StringUtil.getXmlResource(MainEntryActivity.this, R.string.push_video_nim_status_logining));
            } else if (chatRoomStatusChangeData.status == StatusCode.LOGINED) {
            } else if (chatRoomStatusChangeData.status.wontAutoLogin()) {
            } else if (chatRoomStatusChangeData.status == StatusCode.NET_BROKEN) {
                Toast.makeText(MainEntryActivity.this, R.string.push_video_net_broken, Toast.LENGTH_SHORT).show();
            }
            XLog.i("Chat Room Online Status:" + chatRoomStatusChangeData.status.name());
        }
    };

    @SuppressWarnings("serial")
	Observer<ChatRoomKickOutEvent> kickOutObserver = new Observer<ChatRoomKickOutEvent>() {
        @Override
        public void onEvent(ChatRoomKickOutEvent chatRoomKickOutEvent) {
            Toast.makeText(MainEntryActivity.this, 
            		StringUtil.getXmlResource(MainEntryActivity.this, R.string.push_video_kick_out) + chatRoomKickOutEvent.getReason(), 
            		Toast.LENGTH_SHORT).show();
        }
    };
    
	
	class UpdateDallorTask extends AsyncTask<Void, Integer, Integer> {
		private Context context;
		private long dollars;

		UpdateDallorTask(Context context, long dollars) {
			this.context = context;
			this.dollars = dollars;
		}

		/**
		 * 运行在UI线程中，在调用doInBackground()之前执行
		 */
		@Override
		protected void onPreExecute() {

		}

		/**
		 * 后台运行的方法，可以运行非UI线程，可以执行耗时的方法
		 */
		@Override
		protected Integer doInBackground(Void... params) {
			int i = 10;
			while (i > 1) {
				publishProgress(i);
				i--;
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
			}
			return i;
		}

		/**
		 * 运行在ui线程中，在doInBackground()执行完毕后执行
		 */
		@Override
		protected void onPostExecute(Integer integer) {
			Entertainment_dollar.setText(dollars + "");
		}

		/**
		 * 在publishProgress()被调用以后执行，publishProgress()用于更新进度
		 */
		@Override
		protected void onProgressUpdate(Integer... values) {
			int v = values[0];
			Entertainment_dollar.setText((dollars / v) + "");
		}
	}

	@Override
	public void updataMainActivity() {
		getUserInfoRequest();
	}
	
}
