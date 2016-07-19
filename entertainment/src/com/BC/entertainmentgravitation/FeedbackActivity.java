package com.BC.entertainmentgravitation;

import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.summer.activity.BaseActivity;
import com.summer.config.Config;
import com.summer.factory.ThreadPoolFactory;
import com.summer.handler.InfoHandler;
import com.summer.task.HttpBaseTask;
import com.summer.treadpool.ThreadPoolConst;
import com.summer.utils.JsonUtil;
import com.summer.utils.ToastUtil;
import com.summer.utils.UrlUtil;
import com.umeng.analytics.MobclickAgent;

public class FeedbackActivity extends BaseActivity implements OnClickListener{

	private String undergo;
	private EditText Describe_the_text;
	
	public String getUndergo() {
		return undergo;
	}

	public void setUndergo(String undergo) {
		this.undergo = undergo;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal_feedback);
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
		if (!isNullOrEmpty(Describe_the_text.getText().toString()))
		{
			sendReqSaveUndergo();
		}
		else
		{
			ToastUtil.show(this, "请输入您的宝贵意见");
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
	
	/**
	 * 保存演艺经历信息
	 */
	private void sendReqSaveUndergo() {
		if (Config.User == null) {
			ToastUtil.show(this, "无法获取信息");
			return;
		}
		if (Describe_the_text.getText().equals("")) {
			ToastUtil.show(this, "反馈信息还是填一下吧。");
			return;
		}
		HashMap<String, String> entity = new HashMap<String, String>();

		entity.put("clientID", Config.User.getClientID());
		entity.put("feed_balk", getTextViewContent(R.id.Describe_the_text));

		ShowProgressDialog("获取信息...");		
		List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
		addToThreadPool(Config.feedback, "send save album request", params);

	}
	
    private void addToThreadPool(int taskType, String Tag, List<NameValuePair> params)
    {
    	HttpBaseTask httpTask = new HttpBaseTask(ThreadPoolConst.THREAD_TYPE_FILE_HTTP, Tag, params, UrlUtil.GetUrl(taskType));
    	httpTask.setTaskType(taskType);
    	InfoHandler handler = new InfoHandler(this);
    	httpTask.setInfoHandler(handler);
    	ThreadPoolFactory.getThreadPoolManager().addTask(httpTask);
    }
	
	public String getTextViewContent(int id) {
		String s = new String();
		TextView t = (TextView) findViewById(id);
		s = t.getText().toString();
		return s;
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
		switch (taskType) {
		case Config.feedback:
			ToastUtil.show(this, "提交成功");
			Describe_the_text.setText("");
			break;
		}
	}

}
