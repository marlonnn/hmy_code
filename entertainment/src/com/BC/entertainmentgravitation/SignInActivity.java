package com.BC.entertainmentgravitation;

import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.BC.entertainmentgravitation.entity.Continuous;
import com.BC.entertainmentgravitation.fragment.CalendarFragemt;
import com.BC.entertainmentgravitation.util.TimestampTool;
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

public class SignInActivity extends BaseActivity implements OnClickListener{

	private CalendarFragemt calendarFragemt;
	
	private TextView txtContinue;
	private TextView txtWithGet;
	private Gson gson;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sendSignDayRequest();
		setContentView(R.layout.activity_personal_signin);
		findViewById(R.id.imageViewBack).setOnClickListener(this);
		findViewById(R.id.txtViewSign).setOnClickListener(this);
		initView();
	}
	
	private void initView()
	{
		calendarFragemt = (CalendarFragemt) getSupportFragmentManager()
				.findFragmentById(R.id.fragmentCalendar);
		txtContinue = (TextView) findViewById(R.id.txtContinue);
		txtWithGet = (TextView) findViewById(R.id.txtWithGet);
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
		 * 打卡签到
		 */
		case R.id.txtViewSign:
			sendSignRequest();
			break;
		/**
		 * 返回键
		 */
		case R.id.imageViewBack:
			finish();
			break;
		}
	}
	
	/**
	 * 签到
	 */
	private void sendSignRequest()
	{
		HashMap<String, String> entity = new HashMap<String, String>();

		entity.put("clientID", Config.User.getClientID());
		entity.put("The_date_of", TimestampTool.getCurrentDate());
		
		ShowProgressDialog("正在签到中...");	
		List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
		addToThreadPool(Config.sign_in, "send search request", params);
	}
	
	/**
	 * 获取签到天数
	 */
	private void sendSignDayRequest()
	{
		HashMap<String, String> entity = new HashMap<String, String>();
		entity.put("clientID", Config.User.getClientID());
		
		List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
		addToThreadPool(Config.continuous, "send search request", params);
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
		gson = new Gson();
		switch (taskType) {
		/**
		 * 签到
		 */
		case Config.sign_in:
			Entity<Reward> baseEntity3 = gson.fromJson(jsonString,
					new TypeToken<Entity<Reward>>() {
					}.getType());
			Reward type = baseEntity3.getData();
			if (type != null)
			{
				txtWithGet.setText(type.Reward_type);
				Toast.makeText(this, "签到成功,获得：" + type.Reward_type + "个娱币",
						Toast.LENGTH_LONG).show();
			}
			break;
		/**
		 * 	签到天数
		 */
		case Config.continuous:
			Entity<Continuous> entity = gson.fromJson(jsonString,
					new TypeToken<Entity<Continuous>>() {
					}.getType());
			if (entity.getData().getContinuous() != null
					&& !entity.getData().getContinuous().equals("")) {
				calendarFragemt.setSign(entity.getData().getSigns());
				txtContinue.setText(entity.getData().getContinuous());
				int continuous = Integer.parseInt(entity.getData()
						.getContinuous().trim());
				int total = 0;
				if (continuous >= 1)
				{
					int amount = 10 + (continuous -1) * 5;
					total = amount + 5;
					txtWithGet.setText(String.valueOf(amount));
				}
				switch (continuous) {
				case 0:
					setText(R.id.total, "累计签到还有额外奖励");
					break;
				default:
					setText(R.id.total, "明日签到可获得 " + String.valueOf(total) + " 娱币");
					break;
				}

			}
			else 
			{
				ToastUtil.show(this, "获取数据失败");
			}
			break;
		}
	}
	
	class Reward {
		public String Reward_type;
	}
}
