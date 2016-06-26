package com.BC.entertainmentgravitation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.BC.entertainment.cache.InfoCache;
import com.BC.entertainmentgravitation.entity.EditPersonal;
import com.BC.entertainmentgravitation.entity.FHNEntity;
import com.BC.entertainmentgravitation.entity.StarInformation;
import com.BC.entertainmentgravitation.entity.StarLiveVideoInfo;
import com.BC.entertainmentgravitation.fragment.FoundFragment_back;
import com.BC.entertainmentgravitation.fragment.ListFragment;
import com.BC.entertainmentgravitation.fragment.RightsCenterFragment;
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
public class HomeActivity_back extends BaseActivity implements OnClickListener{
	
	private static final int TIME_INTERVAL = 2000;
	private long mBackPressed;
	
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
	
	private Gson gson;
	
//	private CurveFragment curveFragment;
	private RightsCenterFragment rightCardFragment;
	private ListFragment listFragment;
	private FoundFragment_back foundFragment;
	private FragmentManager fManager;
	
	private int lastFragment = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_back);
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		fManager = getSupportFragmentManager();
		gson = new Gson();
		sendFocusStarListRequest();
		sendPersonalInfoRequest();
		findViewById();
		setFragmentSelection(R.id.rLayoutLine);
	}
	
    private void hideFragments(FragmentTransaction transaction) {  
        if (rightCardFragment != null) {  
            transaction.hide(rightCardFragment);  
        }  
//        if (curveFragment != null) {  
//            transaction.hide(curveFragment);  
//        }  
        if (listFragment != null) {  
            transaction.hide(listFragment);  
        }  
        if (foundFragment != null) {  
            transaction.hide(foundFragment);  
        }
    } 
	
	private void touchButton(int id)
	{
		switch(id)
		{
		case R.id.rLayoutLine:
			imgViewLine.setBackgroundResource(R.drawable.activity_rights_center);
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
			imgViewLine.setBackgroundResource(R.drawable.activity_rights_center_0);
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
			imgViewLine.setBackgroundResource(R.drawable.activity_rights_center_0);
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
			imgViewLine.setBackgroundResource(R.drawable.activity_rights_center_0);
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
			imgViewLine.setBackgroundResource(R.drawable.activity_rights_center_0);
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
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		switch(lastFragment)
		{
		case 1:
			touchButton(R.id.rLayoutLine);
			break;
		case 2:
			touchButton(R.id.rLayoutVideo);
			break;
		case 3:
			touchButton(R.id.rLayoutFound);
			break;
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
	
	private void setFragmentSelection(int v)
	{
        FragmentTransaction transaction = fManager.beginTransaction();  
        hideFragments(transaction);
		touchButton(v );
		Intent intent;
		switch(v)
		{

		case R.id.rLayoutLine:
            if (rightCardFragment == null) {  
            	rightCardFragment = new RightsCenterFragment();
                transaction.add(R.id.content, rightCardFragment);  
            } else {  
                transaction.show(rightCardFragment);  
            } 
            lastFragment = 1;
    		transaction.commit();
			break;
		case R.id.rLayoutVideo:
            if (listFragment == null) {  
            	listFragment = new ListFragment();
                transaction.add(R.id.content, listFragment);  
            } else {  
                transaction.show(listFragment);  
            } 
            lastFragment = 2;
    		transaction.commit();
			break;
		case R.id.rLayoutLive:
			if (Config.User.getPermission().equals("2")) 
			{
				createLiveVideoRequest();
			}
			else
			{
				intent = new Intent(this, Authenticate1Activity.class);
				startActivity(intent);
			}
			break;
		case R.id.rLayoutFound:
            if (foundFragment == null) {  
            	foundFragment = new FoundFragment_back();
                transaction.add(R.id.content, foundFragment);  
            } else {  
                transaction.show(foundFragment);  
            } 
            lastFragment = 3;
    		transaction.commit();  
			break;
		case R.id.rLayoutMyself:
			intent = new Intent(this, PersonalCenterActivity.class);
			intent.putExtra("lastFragment", lastFragment);
			startActivity(intent);
//            if (personalFragment == null) {  
//            	personalFragment = new PersonalFragment();
//                transaction.add(R.id.content, personalFragment);  
//            } else {  
//                transaction.show(personalFragment);  
//            } 
			break;
		}

	}

	@Override
	public void onClick(View v) {
		setFragmentSelection(v.getId());
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
     * 获取用户信息
     */
    private void sendPersonalInfoRequest()
    {
    	HashMap<String, String> entity = new HashMap<String, String>();
    	entity.put("clientID", Config.User.getClientID());
    	List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
    	addToThreadPool(Config.personal_information, "get user info", params);
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
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        FragmentTransaction transaction = fManager.beginTransaction();  
        hideFragments(transaction);
		switch (resultCode)
		{
		case RESULT_OK:
			 Bundle b = data.getExtras();
			 int lastFragment = b.getInt("lastFragment");
			 switch (lastFragment)
			 {
				/**
				 * 
				 */
				case 1:
					touchButton(R.id.rLayoutLine);
		            if (rightCardFragment == null) {  
		            	rightCardFragment = new RightsCenterFragment();
		                transaction.add(R.id.content, rightCardFragment);  
		            } else {  
		                transaction.show(rightCardFragment);  
		            } 
					break;
				/**
				 * 
				 */
				case 2:
					touchButton(R.id.rLayoutVideo);
		            if (listFragment == null) {  
		            	listFragment = new ListFragment();
		                transaction.add(R.id.content, listFragment);  
		            } else {  
		                transaction.show(listFragment);  
		            } 
					break;
				/**
				 * 
				 */
				case 3:
					touchButton(R.id.rLayoutFound);
		            if (foundFragment == null) {  
		            	foundFragment = new FoundFragment_back();
		                transaction.add(R.id.content, foundFragment);  
		            } else {  
		                transaction.show(foundFragment);  
		            } 
					break;
			 }
			 
			break;

		}
	}
    
	@Override
	public void RequestSuccessful(String jsonString, int taskType) {
		switch(taskType)
		{
		case Config.personal_information:
			Entity<EditPersonal> baseEntity1 = gson.fromJson(jsonString,
					new TypeToken<Entity<EditPersonal>>() {
					}.getType());
			if (baseEntity1.getData() != null)
			{
				InfoCache.getInstance().setPersonalInfo(baseEntity1.getData());
			}

			break;
		case Config.create_video:
			Entity<StarLiveVideoInfo> starLiveInfoEntity = gson.fromJson(jsonString,
					new TypeToken<Entity<StarLiveVideoInfo>>() {
					}.getType());
			StarLiveVideoInfo startLiveVideoInfo = starLiveInfoEntity.getData();
			Intent intent = new Intent(HomeActivity_back.this, PushActivity.class);
			Bundle b = new Bundle();
			b.putSerializable("liveInfo", startLiveVideoInfo);
			intent.putExtras(b);
			intent.putExtra("lastFragment", lastFragment);
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
	
	@Override
	public void onBackPressed()
	{
	    if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) 
	    { 
	        super.onBackPressed(); 
	        return;
	    }
	    else 
	    { 
	    	ToastUtil.show(this, "再按一次退出");
	    }

	    mBackPressed = System.currentTimeMillis();
	}
}
