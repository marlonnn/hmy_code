package com.BC.entertainmentgravitation;

import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.BC.entertainmentgravitation.entity.CardOrder;
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

/**
 * 申诉
 * @author zhongwen
 *
 */
/**
 * @author zhongwen
 *
 */
public class AppealActivity extends BaseActivity implements OnClickListener{

	
	private CardOrder cardOrder;
	private TextView txtNumber;
	private EditText eTxtPhone;
	private EditText eTxtContent;
	private TextView txtSubmit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_appeal);
		findViewById(R.id.imageViewBack).setOnClickListener(this);
		cardOrder = (CardOrder) getIntent().getSerializableExtra("cardOrder");
		initView();
	}
	
	private void initView()
	{
		txtNumber = (TextView) findViewById(R.id.txtNumber);
		txtSubmit = (TextView) findViewById(R.id.imgViewModify);
		eTxtPhone = (EditText) findViewById(R.id.txtPhone);
		eTxtContent = (EditText) findViewById(R.id.editText1);
		txtNumber.setText(isNullOrEmpty(cardOrder.getOrder_sn()) ? "unknown" : cardOrder.getOrder_sn());
		txtSubmit.setOnClickListener(this);
	}
	
	private void sendAppealReuest()
	{
		HashMap<String, String> entity = new HashMap<String, String>();

		entity.put("clientID", Config.User.getClientID());
		entity.put("orderid", isNullOrEmpty(cardOrder.getOrder_sn()) ? "" : cardOrder.getOrder_sn());
		entity.put("phone", isNullOrEmpty(eTxtPhone.getText().toString()) ? "" : eTxtPhone.getText().toString());
		entity.put("content", isNullOrEmpty(eTxtContent.getText().toString()) ? "" : eTxtContent.getText().toString());
    	List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
    	ShowProgressDialog("正在提交...");
    	addToThreadPool(Config.appeal, "get start right card", params);
	}
	
    private void addToThreadPool(int taskType, String tag, List<NameValuePair> params)
    {
    	HttpBaseTask httpTask = new HttpBaseTask(ThreadPoolConst.THREAD_TYPE_FILE_HTTP, tag, params, UrlUtil.GetUrl(taskType));
    	httpTask.setTaskType(taskType);
    	InfoHandler handler = new InfoHandler(this);
    	httpTask.setInfoHandler(handler);
    	ThreadPoolFactory.getThreadPoolManager().addTask(httpTask);
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
		case R.id.imgViewModify:
			sendAppealReuest();
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
		switch (taskType)
		{
		case Config.appeal:
			ToastUtil.show(this, "提交成功");
			break;
		}
	}

}
