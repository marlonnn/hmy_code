package com.BC.entertainmentgravitation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.BC.entertainmentgravitation.dialog.ApplauseGiveConcern;
import com.BC.entertainmentgravitation.entity.FHNEntity;
import com.BC.entertainmentgravitation.entity.Member;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.igexin.sdk.PushManager;
import com.igexin.sdk.Tag;
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
import com.umeng.analytics.MobclickAgent;

/**
 * 个人首页
 * @author zhongwen
 *
 */
public class PersonalHomeActivity extends BaseActivity implements OnClickListener{

	private CircularImage cImagePortrait;
	private TextView name;
	private TextView focus;
	private TextView fans;
	private TextView career;
	private TextView language;
	private TextView age;
	private TextView nationality;
	private TextView constellation;
	private TextView body;
	private TextView regin;
	private TextView wx;
	private TextView qq;
	private TextView email;
	private LinearLayout lLayoutFocus;
	private LinearLayout lLayoutLine;
	private LinearLayout lLayoutYupiao;
	private Member member;
	
	private Gson gson;
	
	private ApplauseGiveConcern applauseGiveConcern;
	private boolean hasFollow = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal_home);
		gson = new Gson();
		findViewById(R.id.imageViewBack).setOnClickListener(this);
		
		try {
			Intent intent = this.getIntent(); 
			member = (Member) intent.getSerializableExtra("member");
			sendMemberRequest(member.getName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void findViewById(Member member)
	{
		if (member != null)
		{
			cImagePortrait = (CircularImage) findViewById(R.id.cirImagePortrait);
			cImagePortrait.setOnClickListener(this);
			name = (TextView) findViewById(R.id.txtName);
			focus = (TextView) findViewById(R.id.txtViewTopFocus);
			fans = (TextView) findViewById(R.id.txtViewTopFans);
			career = (TextView) findViewById(R.id.txtViewCareer);
			language = (TextView) findViewById(R.id.txtViewLanguage);
			age = (TextView) findViewById(R.id.txtViewAge);
			nationality = (TextView) findViewById(R.id.txtViewNationality);
			constellation = (TextView) findViewById(R.id.txtViewConstellation);
			body = (TextView) findViewById(R.id.txtViewBody);
			regin = (TextView) findViewById(R.id.txtViewRegin);
			wx = (TextView) findViewById(R.id.txtViewWx);
			qq = (TextView) findViewById(R.id.txtViewQq);
			email = (TextView) findViewById(R.id.txtViewEmail);
			
			lLayoutFocus = (LinearLayout) findViewById(R.id.lLayoutFocus);
			lLayoutLine = (LinearLayout) findViewById(R.id.lLayoutLine);
			lLayoutYupiao = (LinearLayout) findViewById(R.id.lLayoutYupiao);
			
			findViewById(R.id.imageViewAlbum).setOnClickListener(this);
			focus.setOnClickListener(this);
			findViewById(R.id.txtViewFocusContent).setOnClickListener(this);
			lLayoutFocus.setOnClickListener(this);
			lLayoutLine.setOnClickListener(this);
			lLayoutYupiao.setOnClickListener(this);
			
			Glide.with(this)
			.load(member.getPortrait())
			.centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL)
			.placeholder(R.drawable.avatar_def).into(cImagePortrait);
			
			name.setText(isNullOrEmpty(member.getNick()) ? ":未知": member.getNick());
			focus.setText(isNullOrEmpty(member.getFocus()) ? "未知": member.getFocus());
			fans.setText(isNullOrEmpty(member.getFans()) ? ":未知": member.getFans());
			career.setText(isNullOrEmpty(member.getProfessional()) ? "未知": member.getProfessional());
			language.setText(isNullOrEmpty(member.getLanguage()) ? "普通话": member.getLanguage());
			age.setText(isNullOrEmpty(member.getAge()) ? "未知": member.getAge());
			nationality.setText(isNullOrEmpty(member.getNationality()) ? "中国": member.getNationality());
			constellation.setText(isNullOrEmpty(member.getConstellation()) ? "未知": member.getConstellation());
			body.setText("未知");
			regin.setText(isNullOrEmpty(member.getRegion()) ? "未知": member.getRegion());
			wx.setText("保密");
			qq.setText("保密");
			email.setText(isNullOrEmpty(member.getEmail()) ? "未知": member.getEmail());
			
			/**
			 * 初始化投资和撤资弹出对话框
			 */
			try {
				applauseGiveConcern = new ApplauseGiveConcern( PersonalHomeActivity.this,
						member.getId(), this, Integer.parseInt(member.getBid()),
								member.getNick());
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
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
	
    private void sendMemberRequest(String username)
    {
    	HashMap<String, String> entity = new HashMap<String, String>();
		entity.put("username", username);
    	List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
    	addToThreadPool(Config.member_in, "get start info", params);
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
    	XLog.i("add to thread pool: " + tag);
    	HttpBaseTask httpTask = new HttpBaseTask(ThreadPoolConst.THREAD_TYPE_FILE_HTTP, tag, params, UrlUtil.GetUrl(taskType));
    	httpTask.setTaskType(taskType);
    	InfoHandler handler = new InfoHandler(this);
    	httpTask.setInfoHandler(handler);
    	ThreadPoolFactory.getThreadPoolManager().addTask(httpTask);
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
		Intent intent;
		switch (v.getId()) {
		/**
		 * 用户头像 
		 */
		case R.id.cirImagePortrait:
			intent = new Intent(this, PesonalPortraitActivity.class);
			startActivity(intent);
			break;
		/**
		 * 关注和取消关注
		 */
		case R.id.lLayoutFocus:
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
		/**
		 * 价值曲线	
		 */
		case R.id.lLayoutLine:
			intent = new Intent(this, CurveActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable("member", member);
			intent.putExtras(bundle);
			startActivity(intent);
			break;
		/**
		 * 娱票排行榜
		 */
		case R.id.lLayoutYupiao:
			intent = new Intent(this, ContributionActivity.class);
			Bundle b = new Bundle();
			b.putSerializable("member", member);
			intent.putExtras(b);
			startActivity(intent);
			break;
		/**
		 * 用户相册
		 */
		case R.id.imageViewAlbum:
			intent = new Intent(this, PersonalAlbumActivity.class);
			Bundle bd = new Bundle();
			bd.putSerializable("clientId", member.getId());
			intent.putExtras(bd);
			startActivity(intent);
			break;
		/**
		 * 关注列表	
		 */
		case R.id.txtViewFocusContent:
			intent = new Intent(this, FocusActivity.class);
			Bundle b1 = new Bundle();
			b1.putSerializable("clientId", member.getId());
			intent.putExtras(b1);
			startActivity(intent);
			break;
		case R.id.txtViewTopFocus:
			intent = new Intent(this, FocusActivity.class);
			Bundle b2 = new Bundle();
			b2.putSerializable("clientId", member.getId());
			intent.putExtras(b2);
			startActivity(intent);
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
                    	if (taskType == Config.and_attention)
                    	{
                			//关注成功
                			hasFollow = true;
                    		RequestSuccessful(jsonString, taskType);
                    	}
                    	else if (taskType == Config.unfollow_attention)
                    	{
                    		if (code == 0)
                    		{
                    			//取消关注成功
                    			hasFollow = false;
                    			RequestSuccessful(jsonString, taskType);
                    		}
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

	@Override
	public void RequestSuccessful(String jsonString, int taskType) {
		switch(taskType)
		{
		case Config.and_attention:
			hasFollow = true;
			ToastUtil.show(PersonalHomeActivity.this, "提交成功");
			applauseGiveConcern.showAnimationDialog(R.drawable.circle6,
					R.raw.concern);
			break;
		case Config.unfollow_attention:
			//取消关注成功
			hasFollow = false;
			ToastUtil.show(this, "取消关注成功");
			sendFocusStarListRequest();
			break;
		case Config.stat_list:
			Entity<List<FHNEntity>> entity = gson.fromJson(jsonString,
					new TypeToken<Entity<List<FHNEntity>>>() {
					}.getType());
			List<FHNEntity> hotList = entity.getData();
			if (hotList != null && hotList.size() > 0) {
				List<String> list = new ArrayList<>();
				
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
				PushManager.getInstance().setTag(this, tags);
			}
			break;
		case Config.give_applause_booed:
			ToastUtil.show(PersonalHomeActivity.this, "提交成功");
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
 		case Config.member_in:
 			try {
 				Entity<Member> memberEntity = gson.fromJson(jsonString,
 						new TypeToken<Entity<Member>>() {
 						}.getType());
 				
 				if (memberEntity != null && memberEntity.getData() != null)
 				{
 					Member member = memberEntity.getData();
 					findViewById(member);
 				}
 			} catch (JsonSyntaxException e) {
 				e.printStackTrace();
 				XLog.e("exception: " + e.getMessage());
 			}
 			break;
		}
	}

}
