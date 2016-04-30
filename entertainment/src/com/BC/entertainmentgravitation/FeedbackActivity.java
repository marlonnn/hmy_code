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

import com.summer.activity.BaseActivity;
import com.summer.config.Config;
import com.summer.factory.ThreadPoolFactory;
import com.summer.handler.InfoHandler;
import com.summer.task.HttpBaseTask;
import com.summer.treadpool.ThreadPoolConst;
import com.summer.utils.JsonUtil;
import com.summer.utils.ToastUtil;
import com.summer.utils.UrlUtil;

public class FeedbackActivity extends BaseActivity implements OnClickListener{

	private String undergo;
	private EditText Describe_the_text;

	private Button editButton, exitEditButton;
	private boolean canEdit = false;
	
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
	
	private void initView()
	{
		findViewById(R.id.imageViewBack).setOnClickListener(this);
		Describe_the_text = (EditText) findViewById(R.id.Describe_the_text);
		editButton = (Button) findViewById(R.id.editButton);
		exitEditButton = (Button) findViewById(R.id.exitEditButton);

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
		sendReqSaveUndergo();
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
		switch (taskType) {
		case Config.feedback:
			ToastUtil.show(this, "提交成功");
			Describe_the_text.setText("");
			break;
		}
	}

}
