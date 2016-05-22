package com.BC.entertainmentgravitation;

import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.BC.entertainmentgravitation.entity.Member;
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
import com.umeng.analytics.MobclickAgent;

/**
 * 个人首页
 * @author zhongwen
 *
 */
public class HomeActivity extends BaseActivity implements OnClickListener{

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
			
			lLayoutFocus.setOnClickListener(this);
			lLayoutLine.setOnClickListener(this);
			lLayoutYupiao.setOnClickListener(this);
			
			Glide.with(this)
			.load(member.getPortrait())
			.centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL)
			.placeholder(R.drawable.avatar_def).into(cImagePortrait);
			
			name.setText(member.getNick() == null ? "未知": member.getNick());
			focus.setText(member.getFollow() == null ? "未知": member.getFollow());
			fans.setText(member.getFans() == null ? "未知": member.getFans());
			career.setText(member.getProfessional() == null ? "未知": member.getProfessional());
			language.setText(member.getLanguage() == null ? "未知": member.getLanguage());
			age.setText(member.getAge() == null ? "未知": member.getAge());
			nationality.setText(member.getNationality() == null ? "未知": member.getNationality());
			constellation.setText(member.getConstellation() == null ? "未知": member.getConstellation());
//			body.setText(member.get == null ? "": member.getName());
			regin.setText(member.getRegion() == null ? "未知": member.getRegion());
//			wx.setText(member.g == null ? "": member.getName());
//			qq.setText(member.getName() == null ? "": member.getName());
			email.setText(member.getEmail() == null ? "未知": member.getEmail());
		}

	}
	
    private void sendMemberRequest(String username)
    {
    	HashMap<String, String> entity = new HashMap<String, String>();
		entity.put("username", username);
    	List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
    	addToThreadPool(Config.member_in, "get start info", params);
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
		switch (v.getId()) {
		
		case R.id.lLayoutFocus:
			ToastUtil.show(this, "此功能正在抓紧开发中，敬请期待...");
			break;
			
		case R.id.lLayoutLine:
			Intent intent = new Intent(this, CurveActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable("member", member);
			intent.putExtras(bundle);
			startActivity(intent);
			break;
			
		case R.id.lLayoutYupiao:
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
	public void RequestSuccessful(String jsonString, int taskType) {
		switch(taskType)
		{
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
