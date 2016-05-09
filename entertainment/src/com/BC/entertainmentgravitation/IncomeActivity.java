package com.BC.entertainmentgravitation;

import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.BC.entertainment.cache.InfoCache;
import com.BC.entertainmentgravitation.entity.EditPersonal;
import com.BC.entertainmentgravitation.entity.WithDraw;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.summer.activity.BaseActivity;
import com.summer.config.Config;
import com.summer.factory.ThreadPoolFactory;
import com.summer.handler.InfoHandler;
import com.summer.json.Entity;
import com.summer.task.HttpBaseTask;
import com.summer.treadpool.ThreadPoolConst;
import com.summer.utils.JsonUtil;
import com.summer.utils.StringUtil;
import com.summer.utils.ToastUtil;
import com.summer.utils.UrlUtil;

/**
 * 基本信息  —— 我的收益
 * @author zhongwen
 *
 */
public class IncomeActivity extends BaseActivity implements OnClickListener{

	private TextView yuPiao;
	private TextView withDraw;
	private ImageButton imageBtnExchange;
	private ImageButton imageBtnWithDraw;
	private Gson gson;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal_income);
		initView();
	}
	
	private void initView()
	{
		gson = new Gson();
		yuPiao = (TextView) findViewById(R.id.txtYupiao);
		withDraw = (TextView) findViewById(R.id.txtWithDraw);
		
		imageBtnExchange = (ImageButton) findViewById(R.id.imageViewExchange);
		imageBtnWithDraw = (ImageButton) findViewById(R.id.imageViewWithDraw);
		
		yuPiao.setOnClickListener(this);
		withDraw.setOnClickListener(this);
		
		imageBtnExchange.setOnClickListener(this);
		imageBtnWithDraw.setOnClickListener(this);
		
		findViewById(R.id.imageViewBack).setOnClickListener(this);
	}
	
	private void initData()
	{
		queryPiaoLeftRequest();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		initData();
	}
	
	/**
	 * 查询可兑换的娱票
	 */
    private void queryPiaoLeftRequest()
    {
    	if (Config.User == null)
    	{
			ToastUtil.show(this, StringUtil.getXmlResource(this, R.string.mainactivity_login_invalidate));
			finish();
			return;
    	}
    	HashMap<String, String> entity = new HashMap<String, String>();
    	entity.put("username", Config.User.getUserName());
    	List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
    	ShowProgressDialog(this.getString(R.string.mainactivity_get_user_info));
    	addToThreadPool(Config.query_piao_left, "get user info", params);
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
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		/**
		 * 娱票兑换娛币
		 */
		case R.id.imageViewExchange:
			intent = new Intent(this, ExchangeActivity.class);
			startActivity(intent);
			break;

		/**
		 * 微信提现
		 */
		case R.id.imageViewWithDraw:
			ToastUtil.show(this, "微信搜索关注 海绵娱 公众号兑换 ");
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
		case Config.query_piao_left:
			try {
				Entity<WithDraw> baseEntity = gson.fromJson(jsonString,
						new TypeToken<Entity<WithDraw>>() {
						}.getType());
				if (baseEntity != null && baseEntity.getData() != null)
				{
					WithDraw w = baseEntity.getData();
					yuPiao.setText(w.getUser_piao());
					InfoCache.getInstance().getPersonalInfo().setPiaoLeft(w.getUser_piao_left());
					withDraw.setText(w.getCash());
				}
			} catch (JsonSyntaxException e1) {
				e1.printStackTrace();
			}
			break;
		}
	}

}
