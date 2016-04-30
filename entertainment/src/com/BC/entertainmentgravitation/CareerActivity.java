package com.BC.entertainmentgravitation;

import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
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

/**
 * 基本信息  —— 演艺经历
 * @author zhongwen
 *
 */
public class CareerActivity extends BaseActivity implements OnClickListener{

	private String career;
	private EditText Describe_the_text;

	private Button editButton, exitEditButton;
	boolean canEdit = false;

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
	
	private void initView()
	{
		findViewById(R.id.imageViewBack).setOnClickListener(this);
		
		Describe_the_text = (EditText) findViewById(R.id.Describe_the_text);
		editButton = (Button) findViewById(R.id.editButton);
		exitEditButton = (Button)findViewById(R.id.exitEditButton);

		editButton.setOnClickListener(this);
		exitEditButton.setOnClickListener(this);
		exitEditButton.setVisibility(View.GONE);
		canEdit(canEdit);
	}
	
	public void canEdit(boolean b) {
		Describe_the_text.setEnabled(b);
	}
	
	public void save() {
		// TODO Auto-generated method stub
		if (career == null) {
			career = "暂无描述";
		}
		career = Describe_the_text.getText().toString();
		sendReqSaveUndergo();
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
		
		case R.id.editButton:
			canEdit = !canEdit;
			canEdit(canEdit);
			if (!canEdit) {
				editButton.setText("更改");
				save();
				exitEditButton.setVisibility(View.GONE);
			} else {
				editButton.setText("确定");
				exitEditButton.setVisibility(View.VISIBLE);
			}
			break;

		case R.id.exitEditButton:
			canEdit = false;
			canEdit(canEdit);
			editButton.setText("更改");
			exitEditButton.setVisibility(View.GONE);
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
