package com.BC.entertainmentgravitation;

import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
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

public class ApplyActivity extends BaseActivity{

	
	
	private Button btnApply;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_apply);
		btnApply = (Button) findViewById(R.id.btnApply);
		btnApply.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
		initView();
	}
	
	private void initView()
	{
		if (MainActivity.personalInformation == null) {
			ToastUtil.show(this, "获取数据失败");
			return;
		}
		setText(R.id.Stage_name, MainActivity.personalInformation.getNickname());
		setText(R.id.professional,
				MainActivity.personalInformation.getProfessional());
		setText(R.id.Starting_price,
				MainActivity.personalInformation.getStarting_price());
		setText(R.id.The_constellation,
				MainActivity.personalInformation.getThe_constellation());
		setText(R.id.height, MainActivity.personalInformation.getHeight());
		setText(R.id.weight, MainActivity.personalInformation.getWeight());
		setText(R.id.gender, MainActivity.personalInformation.getGender());
		setText(R.id.language, MainActivity.personalInformation.getLanguage());
		setText(R.id.nationality,
				MainActivity.personalInformation.getNationality());
		setText(R.id.region, MainActivity.personalInformation.getRegion());
		setText(R.id.age, MainActivity.personalInformation.getAge());
	}
	
	private void sendApplyRequest()
	{
		if (Config.User == null) {
			ToastUtil.show(this, "无法获取信息");
			return;
		}
		
		HashMap<String, String> entity = new HashMap<String, String>();
		entity.put("clientID", "" + Config.User.getClientID());
		entity.put("Stage_name", "" + getTextViewContent(R.id.Stage_name));
		entity.put("professional", "" + getTextViewContent(R.id.professional));
		entity.put("Starting_price", "" + 100);
		entity.put("The_constellation", ""
				+ getTextViewContent(R.id.The_constellation));
		entity.put("height", "" + getTextViewContent(R.id.height));
		entity.put("weight", "" + getTextViewContent(R.id.weight));
		entity.put("gender", "" + getTextViewContent(R.id.gender));
		entity.put("language", "" + getTextViewContent(R.id.language));
		entity.put("nationality", "" + getTextViewContent(R.id.nationality));
		entity.put("region", "" + getTextViewContent(R.id.region));
		entity.put("age", "" + getTextViewContent(R.id.age));
		entity.put("Whether_the_application_for_the_star", "1");
		
		List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
		addToThreadPool(Config.edit_personal_information, "send apply request", params);
		
	}
	
    private void addToThreadPool(int taskType, String Tag, List<NameValuePair> params)
    {
    	HttpBaseTask httpTask = new HttpBaseTask(ThreadPoolConst.THREAD_TYPE_FILE_HTTP, Tag, params, UrlUtil.GetUrl(taskType));
    	httpTask.setTaskType(taskType);
    	InfoHandler handler = new InfoHandler(this);
    	httpTask.setInfoHandler(handler);
    	ThreadPoolFactory.getThreadPoolManager().addTask(httpTask);
    }
	
	private String getTextViewContent(int id) {
		String s = new String();
		TextView t = (TextView) findViewById(id);
		s = t.getText().toString();
		return s;
	}

	@Override
	public void RequestSuccessful(String jsonString, int taskType) {
		switch(taskType)
		{
		case Config.edit_personal_information:
			ToastUtil.show(this, "提交申请成功");
			finish();
			break;
		default:
			break;
		}
	}

}
