package com.BC.entertainmentgravitation;

import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.BC.entertainment.cache.InfoCache;
import com.BC.entertainment.view.CustomViewPager;
import com.BC.entertainmentgravitation.dialog.ApplauseGiveConcern;
import com.BC.entertainmentgravitation.entity.StarInformation;
import com.BC.entertainmentgravitation.entity.StarLiveVideoInfo;
import com.BC.entertainmentgravitation.fragment.CurveFragment;
import com.BC.entertainmentgravitation.fragment.FoundFragment;
import com.BC.entertainmentgravitation.fragment.ListFragment;
import com.BC.entertainmentgravitation.fragment.PersonalFragment;
import com.BC.entertainmentgravitation.fragment.SurfaceEmptyFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
	
	private CustomViewPager viewPager;
	private CurveFragment curveFragment;
	private PersonalFragment personalFragment;
	
	private SurfaceEmptyFragment emptyFragment;
	private FoundFragment foundFragment;
	
//	private HotFragment hotFragment;
	private ListFragment listFragment;
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_home_back);
		gson = new Gson();
		findViewById();
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
			
			imgViewLive.setBackgroundResource(R.drawable.activity_main_home_live_1);
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
	
	@SuppressWarnings("deprecation")
	private void findViewById()
	{
		curveFragment = new CurveFragment();
//		hotFragment = new HotFragment();
		listFragment = new ListFragment();
		personalFragment = new PersonalFragment();
		foundFragment = new FoundFragment();
		emptyFragment = new SurfaceEmptyFragment();
		
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
        viewPager.setAdapter(new FragmentPagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
            	Fragment fragment = null;
            	switch(position)
            	{
				/**
				 * 曲线
				 */
            	case 0:
            		fragment = curveFragment;
            		break;
				/**
				 * 直播列表
				 */
            	case 1:
//            		fragment = hotFragment;
            		fragment = listFragment;
            		break;
				/**
				 * 直播
				 */
            	case 2:
            		fragment = emptyFragment;
            		break;
				/**
				 * 发现
				 */
            	case 3:
            		fragment = foundFragment;
            		break;
				/**
				 * 我的
				 */
            	case 4:
            		fragment = personalFragment;
            		break;            		
            	}
                return fragment;
            }

            @Override
            public int getCount() {
                return 5;
            }
        });
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
				intent = new Intent(this, ApplyActivity.class);
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
    
	@Override
	public void RequestSuccessful(String jsonString, int taskType) {
		switch(taskType)
		{
		case Config.create_video:
			Entity<StarLiveVideoInfo> starLiveInfoEntity = gson.fromJson(jsonString,
					new TypeToken<Entity<StarLiveVideoInfo>>() {
					}.getType());
			StarLiveVideoInfo startLiveVideoInfo = starLiveInfoEntity.getData();
			Intent intent = new Intent(HomeActivity_back.this, PushActivity_back.class);
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
		}
	}

}
