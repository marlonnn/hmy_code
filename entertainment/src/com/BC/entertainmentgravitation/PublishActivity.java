package com.BC.entertainmentgravitation;

import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.BC.entertainment.cache.AuthenCache;
import com.BC.entertainmentgravitation.dialog.RegionDialog;
import com.BC.entertainmentgravitation.dialog.RightsNameDialog;
import com.BC.entertainmentgravitation.dialog.RightsNameDialog.Builder;
import com.summer.activity.BaseActivity;
import com.summer.config.Config;
import com.summer.factory.ThreadPoolFactory;
import com.summer.handler.InfoHandler;
import com.summer.task.HttpBaseTask;
import com.summer.treadpool.ThreadPoolConst;
import com.summer.utils.JsonUtil;
import com.summer.utils.ToastUtil;
import com.summer.utils.UrlUtil;
import com.summer.utils.ValidateUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * 发布权益卡
 * @author zhongwen
 *
 */
public class PublishActivity extends BaseActivity implements OnClickListener{

	private RegionDialog.Builder builder;
	private AuthenCache authenCache;
	private TextView txtRregion;
	private Builder rbuilder;
	private TextView txtName;
	private EditText eTxtAmount;
	private EditText eTxtPrice;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_publish);
		initView();
		authenCache = new AuthenCache(this);
	}
	
	private void initView()
	{
		findViewById(R.id.imageViewBack).setOnClickListener(this);
		
		findViewById(R.id.imgViewUp).setOnClickListener(this);
		findViewById(R.id.rLayoutAmount).setOnClickListener(this);
		findViewById(R.id.rLayoutPrice).setOnClickListener(this);
		findViewById(R.id.rLayoutRegion).setOnClickListener(this);
		findViewById(R.id.btnPublish).setOnClickListener(this);
		
		txtName = (TextView) findViewById(R.id.txtViewName);
		txtRregion = (TextView) findViewById(R.id.txtViewRegion);
		
		eTxtAmount = (EditText) findViewById(R.id.editTextAmount);
		eTxtPrice = (EditText) findViewById(R.id.editTextPrice);
		
		txtRregion.setOnClickListener(this);
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
	
	/**
	 * 选择地区
	 */
	private void showSelectRegion()
	{
		final String[] privinces = authenCache.mProvinceDatas;
		if (privinces != null && privinces.length > 0)
		{
			builder = new RegionDialog.Builder(this, authenCache);
			builder.setTitle("选择地区");
			builder.setmProvinceDatas(authenCache.mProvinceDatas);
			builder.setPositiveButton(new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					String privince = builder.getmCurrentProviceName();
					String city = builder.getmCurrentCityName();
					String area = builder.getmCurrentAreaName();
					txtRregion.setText(privince + "-" + city + "-" + area);
					if (dialog != null)
					{
						dialog.dismiss();
					}
				}});
			
			builder.setNegativeButton(new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (dialog != null)
					{
						dialog.dismiss();
					}
				}
			});
			RegionDialog dialog = builder.Create();
			dialog.show();
		}

	}
	
	/**
	 * 选择权益卡
	 */
	private void showSelectRights()
	{
		rbuilder = new RightsNameDialog.Builder(this);
		rbuilder.setTitle("选择权益卡");
		rbuilder.setPositiveButton(new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				String data = rbuilder.getmCurrentName();
				if (data != null)
				{
					txtName.setText(data);
				}
				if (dialog != null)
				{
					dialog.dismiss();
				}
			}});
		
		rbuilder.setNegativeButton(new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (dialog != null)
				{
					dialog.dismiss();
				}
			}
		});
		RightsNameDialog dialog = rbuilder.Create();
		dialog.show();

	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imgViewUp:
			showSelectRights();
			break;
			
		case R.id.rLayoutAmount:
			break;
			
		case R.id.rLayoutPrice:
			break;
			
		case R.id.txtViewRegion:
			showSelectRegion();
			break;
		case R.id.btnPublish:
			sendPublishRequest();
			break;
		/**
		 * 返回键
		 */
		case R.id.imageViewBack:
			finish();
			break;
		}
	}
	
	private void sendPublishRequest()
	{
		if (ValidateUtil.isEmpty(txtName, "权益卡不能为空") || 
				ValidateUtil.isEmpty(eTxtAmount, "总量不能为空") ||
				ValidateUtil.isEmpty(eTxtPrice, "价格不能为空") ||
				ValidateUtil.isEmpty(txtRregion, "地区不能为空"))
		{
			return;
		}
		else
		{
			HashMap<String, String> entity = new HashMap<String, String>();
			entity.put("clientID", Config.User.getClientID());
			entity.put("number", eTxtAmount.getText().toString());
			entity.put("label", txtName.getText().toString());
			entity.put("region", txtRregion.getText().toString());
			
			entity.put("price", eTxtPrice.getText().toString());
			entity.put("describes", "0");
			
			ShowProgressDialog("正在发布...");		
			List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
			addToThreadPool(Config.newProfit, "send search request", params);
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
		switch(taskType)
		{
		case Config.newProfit:
			ToastUtil.show(this, "发布成功");
			break;
		}
	}

}
