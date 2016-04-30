package com.BC.entertainmentgravitation;

import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.BC.entertainmentgravitation.entity.Broker;
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
 * 基本信息 ---我的经纪
 * @author zhongwen
 *
 */
public class BrokerActivity extends BaseActivity implements OnClickListener{
	
	private Broker broker;
	private EditText Agent_name, The_phone, QQ, WeChat, email, address;
	private Button editButton, exitEditButton;
	private boolean canEdit = false;
	
	protected Broker getBroker() {
		return broker;
	}

	protected void setBroker(Broker broker) {
		this.broker = broker;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal_brokers);
		initView();
		sendBrokerRequest();
	}
	
	private void initView()
	{
		findViewById(R.id.imageViewBack).setOnClickListener(this);
		Agent_name = (EditText) findViewById(R.id.Agent_name);
		The_phone = (EditText) findViewById(R.id.The_phone);
		QQ = (EditText) findViewById(R.id.QQ);
		WeChat = (EditText) findViewById(R.id.WeChat);
		email = (EditText) findViewById(R.id.email);
		address = (EditText) findViewById(R.id.address);

		editButton = (Button) findViewById(R.id.editButton);
		exitEditButton = (Button) findViewById(R.id.exitEditButton);

		editButton.setOnClickListener(this);
		exitEditButton.setOnClickListener(this);
		exitEditButton.setVisibility(View.GONE);
		canEdit(canEdit);
	}
	
	public void initPersonalBroker() {
		// TODO Auto-generated method stub
		if (broker == null) {
			// ToastUtil.show(activity, "获取数据失败");
			return;
		}
		Agent_name.setText(broker.getAgent_name());
		The_phone.setText(broker.getThe_phone());
		QQ.setText(broker.getQQ());
		WeChat.setText(broker.getWeChat());
		email.setText(broker.getEmail());
		address.setText(broker.getAddress());
	}
	
	public void save() {
		// TODO Auto-generated method stub
		if (broker == null) {
			broker = new Broker();
		}
		broker.setAgent_name(Agent_name.getText().toString());
		broker.setThe_phone(The_phone.getText().toString());
		broker.setQQ(QQ.getText().toString());
		broker.setWeChat(WeChat.getText().toString());
		broker.setEmail(email.getText().toString());
		broker.setAddress(address.getText().toString());
		sendReqSaveBroker();
	}
	
	public void canEdit(boolean b) {
		Agent_name.setEnabled(b);
		The_phone.setEnabled(b);
		QQ.setEnabled(b);
		WeChat.setEnabled(b);
		email.setEnabled(b);
		address.setEnabled(b);
	}
	
	/**
	 * 获取商务信息
	 */
	private void sendBrokerRequest() {
		if (Config.User == null) {
			ToastUtil.show(this, "无法获取信息");
			return;
		}
		HashMap<String, String> entity = new HashMap<String, String>();

		entity.put("clientID", Config.User.getClientID());
		
		ShowProgressDialog("获取经纪人信息...");		
		List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
		addToThreadPool(Config.business_information, "send album request", params);
	}

	/**
	 * 获取商务信息
	 */
	private void sendReqSaveBroker() {
		if (Config.User == null) {
			ToastUtil.show(this, "无法获取信息");
			return;
		}
		HashMap<String, String> entity = JsonUtil
				.object2HashMap(broker);

		entity.put("clientID", Config.User.getClientID());

		ShowProgressDialog("保存经纪人信息...");		
		List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
		addToThreadPool(Config.edit_business_information, "send album request", params);
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
		case Config.business_information:
			Entity<Broker> baseEntity2 = gson.fromJson(
					jsonString,
					new TypeToken<Entity<Broker>>() {
					}.getType());
			Broker broker = baseEntity2.getData();
			if (broker != null) {
				setBroker(broker);
				initPersonalBroker();
			} else {
				ToastUtil.show(this, "获取数据失败");
			}
			break;
		}
	}
    
}
