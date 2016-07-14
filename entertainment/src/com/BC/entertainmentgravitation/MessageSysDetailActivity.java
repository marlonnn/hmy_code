package com.BC.entertainmentgravitation;

import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;

import com.BC.entertainmentgravitation.entity.Huodong;
import com.BC.entertainmentgravitation.entity.SysMessage;
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
import com.summer.utils.UrlUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * 系统消息 消息详情
 * @author zhongwen
 *
 */
public class MessageSysDetailActivity extends BaseActivity implements OnClickListener{

	private String id;
	private WebView webView;
	private Gson gson;
	private SysMessage  message;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message_sys_detail);
		findViewById(R.id.imageViewBack).setOnClickListener(this);
		gson = new Gson();
        Intent intent = this.getIntent();
        id = (String)intent.getStringExtra("id");
        webView = (WebView) findViewById(R.id.webView1);
        sendDetailMesssageRequest(id);
	}
	
	private void initWebview(String message)
	{
        try {
        	webView.loadDataWithBaseURL(null, Html.fromHtml(message) + "", "text/html", "utf-8", null);
		} catch (Exception e) {
			e.printStackTrace();
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
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		/**
		 * 返回键
		 */
		case R.id.imageViewBack:
			finish();
			break;
		}
	}
	
	/**
	 * 获取消息详情
	 */
	private void sendDetailMesssageRequest(String id) {
		if (id != null)
		{
			HashMap<String, String> entity = new HashMap<String, String>();
			entity.put("id", id);
			ShowProgressDialog("请稍等...");		
			List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
			addToThreadPool(Config.getMessage, "send delete album request", params);
		}

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
	public void RequestSuccessful(String jsonString, int taskType) {
		switch (taskType)
		{
		case Config.getMessage:
			Entity<SysMessage> baseEntity = gson.fromJson(
					jsonString,
					new TypeToken<Entity<SysMessage>>() {
					}.getType());
			if (baseEntity.getData() != null)
			{
				message = baseEntity.getData();
				if (message.getDetail() != null)
				{
					
				}
				initWebview(message.getDetail());
			}
			break;
		}
	}

}
