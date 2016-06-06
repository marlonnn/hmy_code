package com.BC.entertainmentgravitation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.BC.entertainment.adapter.HomeViewPagerAdapter;
import com.BC.entertainment.cache.InfoCache;
import com.BC.entertainment.view.CustomViewPager;
import com.BC.entertainmentgravitation.entity.FHNEntity;
import com.BC.entertainmentgravitation.entity.GeTui;
import com.BC.entertainmentgravitation.entity.StarInformation;
import com.BC.entertainmentgravitation.entity.StarLiveVideoInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;
import com.igexin.sdk.Tag;
import com.summer.activity.BaseActivity;
import com.summer.config.Config;
import com.summer.factory.ThreadPoolFactory;
import com.summer.handler.InfoHandler;
import com.summer.json.Entity;
import com.summer.task.HttpBaseTask;
import com.summer.treadpool.ThreadPoolConst;
import com.summer.utils.JsonUtil;
import com.summer.utils.StringUtil;
import com.summer.utils.ToastUtil;
import com.summer.utils.UrlUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * 
 * @author wen zhong
 *
 */
public class HomeActivity extends BaseActivity implements OnClickListener{
	
	private CustomViewPager viewPager;
	private ImageView imgViewLine;
	private ImageView imgViewVideo;
	private ImageView imgViewLive;
	private ImageView imgViewFound;
	private ImageView imgViewMyself;
	
	private TextView txtViewLine;
	private TextView txtViewVideo;
	private TextView txtViewLive;
	private TextView txtViewFound;
	private TextView txtViewMyself;
	
	private static Context context;
	
	private Gson gson;
	private HomeViewPagerAdapter homePagerAdapter;
	
	private static NotificationManager messageNotificatioManager;
	
	private static Notification notification;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_home_back);
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		gson = new Gson();
		context = this;
		sendFocusStarListRequest();
		findViewById();
		initNotification();
	}
	
	private void initNotification()
	{
		int icon = R.drawable.app_logo; //通知图标  
		notification = new Notification();
		notification.icon = icon;
		messageNotificatioManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
	}
	
	public static void showNotification(GeTui getTui)
	{
		Intent notificationIntent = new Intent(context, HomeActivity.class); 
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);  
		notification.setLatestEventInfo(context, getTui.getMessagetitle(), getTui.getMessagecontent(), contentIntent); 
		messageNotificatioManager.notify(1, notification);
	}

	private void touchButton(int id)
	{
		switch(id)
		{
		case R.id.rLayoutLine:
			imgViewLine.setBackgroundResource(R.drawable.activity_main_home_line_1);
			txtViewLine.setTextColor(getResources().getColor(R.color.hmy_red));
			
			imgViewVideo.setBackgroundResource(R.drawable.activity_main_home_video);
			txtViewVideo.setTextColor(getResources().getColor(R.color.white));
			
			imgViewLive.setBackgroundResource(R.drawable.activity_main_home_live);
			txtViewLive.setTextColor(getResources().getColor(R.color.white));
			
			imgViewFound.setBackgroundResource(R.drawable.activity_main_home_found);
			txtViewFound.setTextColor(getResources().getColor(R.color.white));
			
			imgViewMyself.setBackgroundResource(R.drawable.activity_main_home_myself);
			txtViewMyself.setTextColor(getResources().getColor(R.color.white));
			break;
		case R.id.rLayoutVideo:
			imgViewLine.setBackgroundResource(R.drawable.activity_main_home_line);
			txtViewLine.setTextColor(getResources().getColor(R.color.white));
			
			imgViewVideo.setBackgroundResource(R.drawable.activity_main_home_video_1);
			txtViewVideo.setTextColor(getResources().getColor(R.color.hmy_red));
			
			imgViewLive.setBackgroundResource(R.drawable.activity_main_home_live);
			txtViewLive.setTextColor(getResources().getColor(R.color.white));
			
			imgViewFound.setBackgroundResource(R.drawable.activity_main_home_found);
			txtViewFound.setTextColor(getResources().getColor(R.color.white));
			
			imgViewMyself.setBackgroundResource(R.drawable.activity_main_home_myself);
			txtViewMyself.setTextColor(getResources().getColor(R.color.white));
			break;
		case R.id.rLayoutLive:
			imgViewLine.setBackgroundResource(R.drawable.activity_main_home_line);
			txtViewLine.setTextColor(getResources().getColor(R.color.white));
			
			imgViewVideo.setBackgroundResource(R.drawable.activity_main_home_video);
			txtViewVideo.setTextColor(getResources().getColor(R.color.white));
			
			imgViewLive.setBackgroundResource(R.drawable.activity_main_home_live);
//			imgViewLive.setBackgroundResource(R.drawable.activity_main_home_live_1);
			txtViewLive.setTextColor(getResources().getColor(R.color.hmy_red));
			
			imgViewFound.setBackgroundResource(R.drawable.activity_main_home_found);
			txtViewFound.setTextColor(getResources().getColor(R.color.white));
			
			imgViewMyself.setBackgroundResource(R.drawable.activity_main_home_myself);
			txtViewMyself.setTextColor(getResources().getColor(R.color.white));
			break;
		case R.id.rLayoutFound:
			imgViewLine.setBackgroundResource(R.drawable.activity_main_home_line);
			txtViewLine.setTextColor(getResources().getColor(R.color.white));
			
			imgViewVideo.setBackgroundResource(R.drawable.activity_main_home_video);
			txtViewVideo.setTextColor(getResources().getColor(R.color.white));
			
			imgViewLive.setBackgroundResource(R.drawable.activity_main_home_live);
			txtViewLive.setTextColor(getResources().getColor(R.color.white));
			
			imgViewFound.setBackgroundResource(R.drawable.activity_main_home_found_1);
			txtViewFound.setTextColor(getResources().getColor(R.color.hmy_red));
			
			imgViewMyself.setBackgroundResource(R.drawable.activity_main_home_myself);
			txtViewMyself.setTextColor(getResources().getColor(R.color.white));
			break;
		case R.id.rLayoutMyself:
			imgViewLine.setBackgroundResource(R.drawable.activity_main_home_line);
			txtViewLine.setTextColor(getResources().getColor(R.color.white));
			
			imgViewVideo.setBackgroundResource(R.drawable.activity_main_home_video);
			txtViewVideo.setTextColor(getResources().getColor(R.color.white));
			
			imgViewLive.setBackgroundResource(R.drawable.activity_main_home_live);
			txtViewLive.setTextColor(getResources().getColor(R.color.white));
			
			imgViewFound.setBackgroundResource(R.drawable.activity_main_home_found);
			txtViewFound.setTextColor(getResources().getColor(R.color.white));
			
			imgViewMyself.setBackgroundResource(R.drawable.activity_main_home_myself_1);
			txtViewMyself.setTextColor(getResources().getColor(R.color.hmy_red));
			break;
		}
	}
	
	private void findViewById()
	{
		imgViewLine = (ImageView) findViewById(R.id.imgViewLine);
		imgViewVideo = (ImageView) findViewById(R.id.imgViewVideo);
		imgViewLive = (ImageView) findViewById(R.id.imgViewLive);
		imgViewFound = (ImageView) findViewById(R.id.imgViewFound);
		imgViewMyself = (ImageView) findViewById(R.id.imgViewMyself);
		
		txtViewLine = (TextView) findViewById(R.id.txtViewLine);
		txtViewVideo = (TextView) findViewById(R.id.txtViewVideo);
		txtViewLive = (TextView) findViewById(R.id.txtViewLive);
		txtViewFound = (TextView) findViewById(R.id.txtViewFound);
		txtViewMyself = (TextView) findViewById(R.id.txtViewMyself);
		
		imgViewLine.setBackgroundResource(R.drawable.activity_main_home_line_1);
		txtViewLine.setTextColor(getResources().getColor(R.color.hmy_red));
		
		imgViewVideo.setBackgroundResource(R.drawable.activity_main_home_video);
		txtViewVideo.setTextColor(getResources().getColor(R.color.white));
		
		imgViewLive.setBackgroundResource(R.drawable.activity_main_home_live);
		txtViewLive.setTextColor(getResources().getColor(R.color.white));
		
		imgViewFound.setBackgroundResource(R.drawable.activity_main_home_found);
		txtViewFound.setTextColor(getResources().getColor(R.color.white));
		
		imgViewMyself.setBackgroundResource(R.drawable.activity_main_home_myself);
		txtViewMyself.setTextColor(getResources().getColor(R.color.white));
		
		findViewById(R.id.rLayoutLine).setOnClickListener(this);
		findViewById(R.id.rLayoutVideo).setOnClickListener(this);
		findViewById(R.id.rLayoutLive).setOnClickListener(this);
		findViewById(R.id.rLayoutFound).setOnClickListener(this);
		findViewById(R.id.rLayoutMyself).setOnClickListener(this);
		
		FragmentManager fragmentManager = this.getSupportFragmentManager();
		viewPager = (CustomViewPager) findViewById(R.id.vPagerContent);
		viewPager.setPagingEnabled(false);
		homePagerAdapter = new HomeViewPagerAdapter(fragmentManager);
		viewPager.setAdapter(homePagerAdapter);
        viewPager.setCurrentItem(0);
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
		touchButton(v.getId());
		Intent intent;
		switch(v.getId())
		{

		case R.id.rLayoutLine:
			viewPager.setCurrentItem(0);
			break;
		case R.id.rLayoutVideo:
			viewPager.setCurrentItem(1);
			break;
		case R.id.rLayoutLive:
			if (Config.User.getPermission().equals("2")) 
			{
				createLiveVideoRequest();
			}
			else
			{
//				intent = new Intent(this, ApplyActivity.class);
//				startActivity(intent);
				
				intent = new Intent(v.getContext(),
						BrowserAcitvity.class);
				intent.putExtra("url", Config.AthuAddress + Config.User.getClientID());
				startActivity(intent);
			}
			break;
		case R.id.rLayoutFound:
			viewPager.setCurrentItem(3);
			break;
		case R.id.rLayoutMyself:
			viewPager.setCurrentItem(4);
			break;
		}
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
		ShowProgressDialog("正在进入直播间，请稍等...");
		addToThreadPool(Config.create_video, "send create live video request", params);
	}
	
    private void addToThreadPool(int taskType, String tag, List<NameValuePair> params)
    {
    	HttpBaseTask httpTask = new HttpBaseTask(ThreadPoolConst.THREAD_TYPE_FILE_HTTP, tag, params, UrlUtil.GetUrl(taskType));
    	httpTask.setTaskType(taskType);
    	InfoHandler handler = new InfoHandler(this);
    	httpTask.setInfoHandler(handler);
    	ThreadPoolFactory.getThreadPoolManager().addTask(httpTask);
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
    
	@Override
	public void RequestSuccessful(String jsonString, int taskType) {
		switch(taskType)
		{
		case Config.create_video:
			Entity<StarLiveVideoInfo> starLiveInfoEntity = gson.fromJson(jsonString,
					new TypeToken<Entity<StarLiveVideoInfo>>() {
					}.getType());
			StarLiveVideoInfo startLiveVideoInfo = starLiveInfoEntity.getData();
			Intent intent = new Intent(HomeActivity.this, PushActivity.class);
			Bundle b = new Bundle();
			b.putSerializable("liveInfo", startLiveVideoInfo);
			intent.putExtras(b);
			startActivity(intent); 
//			enterChatRoom(startLiveVideoInfo, true);
			break;
		case Config.star_information:
			Entity<StarInformation> startInfo = gson.fromJson(jsonString,
					new TypeToken<Entity<StarInformation>>() {
					}.getType());
			if (startInfo != null)
			{
				InfoCache.getInstance().setStartInfo(startInfo.getData());
				InfoCache.getInstance().AddToStarInfoList(startInfo.getData());
			}
			break;
		case Config.stat_list:
			Entity<List<FHNEntity>> baseEntity = gson.fromJson(jsonString,
					new TypeToken<Entity<List<FHNEntity>>>() {
					}.getType());
			List<FHNEntity> hotList = baseEntity.getData();
			if (hotList != null && hotList.size() > 0) {
				List<String> list = new ArrayList<>();
				
				if (Config.User != null)
				{
					if (Config.User.getPermission().contains("2") && Config.User.getCheckType().contains("0"))
					{
						list.add("Group1");
					}
					else
					{
						list.add("Group" + Config.User.getPermission());
					}
				}
				for (int i=0; i<hotList.size(); i++)
				{
					if (i == 99)
					{
						break;
					}
					list.add("starer" + hotList.get(i).getStar_ID());
				}
				Tag[] tags = new Tag[list.size()];
				for (int i=0; i<list.size(); i++)
				{
					Tag t = new Tag();
					t.setName(list.get(i));
					tags[i] = t;
				}
				int result = PushManager.getInstance().setTag(this, tags);
				String text = "设置标签失败,未知异常";

				switch (result) {
				    case PushConsts.SETTAG_SUCCESS:
				        text = "设置标签成功";
				        break;

				    case PushConsts.SETTAG_ERROR_COUNT:
				        text = "设置标签失败, tag数量过大, 最大不能超过200个";
				        break;

				    case PushConsts.SETTAG_ERROR_FREQUENCY:
				        text = "设置标签失败, 频率过快, 两次间隔应大于1s";
				        break;

				    case PushConsts.SETTAG_ERROR_REPEAT:
				        text = "设置标签失败, 标签重复";
				        break;

				    case PushConsts.SETTAG_ERROR_UNBIND:
				        text = "设置标签失败, 服务未初始化成功";
				        break;

				    case PushConsts.SETTAG_ERROR_EXCEPTION:
				        text = "设置标签失败, 未知异常";
				        break;

				    case PushConsts.SETTAG_ERROR_NULL:
				        text = "设置标签失败, tag 为空";
				        break;

				    default:
				        break;
				}
			} 
			break;
		}
	}

}
