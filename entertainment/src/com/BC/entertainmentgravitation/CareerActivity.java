package com.BC.entertainmentgravitation;

import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.BC.entertainmentgravitation.entity.Career;
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
import com.summer.utils.ToastUtil;
import com.summer.utils.UrlUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * 基本信息  —— 演艺经历
 * @author zhongwen
 *
 */
public class CareerActivity extends BaseActivity implements OnClickListener{

	private String career;
	private EditText Describe_the_text;

	public String getCareer() {
		return career;
	}

	public void setCareer(String career) {
		this.career = career;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal_career);
		initView();
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
	
	private void initView()
	{
		findViewById(R.id.imageViewBack).setOnClickListener(this);
		findViewById(R.id.imgViewModify).setOnClickListener(this);
		Describe_the_text = (EditText) findViewById(R.id.Describe_the_text);
		canEdit(true);
	}
	
	public void canEdit(boolean b) {
		Describe_the_text.setEnabled(b);
	}
	
	public void save() {
		if (career == null) {
			career = "暂无描述";
		}
		career = Describe_the_text.getText().toString();
		if (!isNullOrEmpty(Describe_the_text.getText().toString()))
		{
			sendReqSaveUndergo();
		}
		else
		{
			ToastUtil.show(this, "请输入您的演艺经历");
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
	public void initPersonalInformation() {
		if (career == null) {
			ToastUtil.show(this, "获取数据失败");
			return;
		}
		Describe_the_text.setText(career);
	}
	

	/**
	 * 获取演艺经历信息
	 */
	private void sendReqUndergo() {
		if (Config.User == null) {
			ToastUtil.show(this, "无法获取信息");
			return;
		}
		HashMap<String, String> entity = new HashMap<String, String>();

		entity.put("clientID", Config.User.getClientID());

		
		ShowProgressDialog("获取信息...");		
		List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
		addToThreadPool(Config.her_career, "send delete album request", params);
	}

	/**
	 * 保存演艺经历信息
	 */
	private void sendReqSaveUndergo() {
		if (Config.User == null) {
			ToastUtil.show(this, "无法获取信息");
			return;
		}
		HashMap<String, String> entity = new HashMap<String, String>();

		entity.put("clientID", Config.User.getClientID());
		entity.put("Describe_the_text",
				getTextViewContent(R.id.Describe_the_text));

		ShowProgressDialog("获取信息...");		
		List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
		addToThreadPool(Config.editor_of_her_career, "send delete album request", params);

	}
	
	private String getTextViewContent(int id) {
		String s = new String();
		TextView t = (TextView) findViewById(id);
		s = t.getText().toString();
		return s;
	}
	
    private void addToThreadPool(int taskType, String Tag, List<NameValuePair> params)
    {
    	HttpBaseTask httpTask = new HttpBaseTask(ThreadPoolConst.THREAD_TYPE_FILE_HTTP, Tag, params, UrlUtil.GetUrl(taskType));
    	httpTask.setTaskType(taskType);
    	InfoHandler handler = new InfoHandler(this);
    	httpTask.setInfoHandler(handler);
    	ThreadPoolFactory.getThreadPoolManager().addTask(httpTask);
    }
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imgViewModify:
			save();
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
		Gson gson = new Gson();
		switch (taskType) {
		case Config.her_career:
			Entity<Career> baseEntity3 = gson.fromJson(jsonString,
					new TypeToken<Entity<Career>>() {
					}.getType());
			Career career = baseEntity3.getData();
			if (career != null && career.getDescribe_the_text() != null) {
				setCareer(career.getDescribe_the_text());
				initPersonalInformation();
			} else {
				ToastUtil.show(this, "获取数据失败");
			}
			break;
		case Config.editor_of_her_career:
			ToastUtil.show(this, "保存成功");
			sendReqUndergo();
			break;
		}
	}

}
