package com.BC.entertainmentgravitation;

import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONException;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.BC.entertainment.adapter.ExchangeRecycleAdapter;
import com.BC.entertainment.adapter.ExchangeRecycleAdapter.OnItemClickListener;
import com.BC.entertainment.cache.InfoCache;
import com.BC.entertainment.cache.YubiCache;
import com.BC.entertainmentgravitation.entity.EditPersonal;
import com.BC.entertainmentgravitation.entity.Exchange;
import com.BC.entertainmentgravitation.entity.Yubi;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.summer.activity.BaseActivity;
import com.summer.config.Config;
import com.summer.factory.ThreadPoolFactory;
import com.summer.handler.InfoHandler;
import com.summer.json.Entity;
import com.summer.logger.XLog;
import com.summer.task.HttpBaseTask;
import com.summer.treadpool.ThreadPoolConst;
import com.summer.utils.JsonUtil;
import com.summer.utils.StringUtil;
import com.summer.utils.ToastUtil;
import com.summer.utils.UrlUtil;

public class ExchangeActivity extends BaseActivity implements OnClickListener, OnItemClickListener{
	
	private TextView textViewAccount;
	
	private RecyclerView yubiRecycleList;
	
	private ExchangeRecycleAdapter adapter;
	
	private List<Yubi> mYubi;
	
	/**
	 * 需要充值的娛币对象
	 */
	private Yubi chargeYubi;
	
	private Gson gson;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_exchange);
		initView();
	}
	
	private void initView()
	{
		gson = new Gson();
		
		textViewAccount = (TextView) findViewById(R.id.textViewAccount);
		
		if (InfoCache.getInstance().getPersonalInfo() != null && InfoCache.getInstance().getPersonalInfo().getEntertainment_dollar() != null)
		{
			textViewAccount.setText(InfoCache.getInstance().getPersonalInfo().getPiaoLeft() + " 娱票 ");
		}
		
		findViewById(R.id.imageViewBack).setOnClickListener(this);
		
		yubiRecycleList = (RecyclerView)findViewById(R.id.listViewCharge);
		
		mYubi = YubiCache.getInstance().GetYubiLists();
		
		adapter = new ExchangeRecycleAdapter(this, mYubi);
        adapter.notifyDataSetChanged();
        adapter.setmOnItemClickListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        yubiRecycleList.setLayoutManager(linearLayoutManager);
        
        yubiRecycleList.setItemAnimator(new DefaultItemAnimator());//more的动画效果
        
        yubiRecycleList.setAdapter(adapter);
        
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	private void sendExchangeYubiRequest(Yubi yubi)
	{
		EditPersonal personal = InfoCache.getInstance().getPersonalInfo();
    	if (personal != null)
    	{ 
    		if (yubi.getAmount() > Integer.parseInt(personal.getEntertainment_dollar()))
    		{
    			//娱票不足,无法兑换
    			
    		}
    		else
    		{
    	    	HashMap<String, String> entity = new HashMap<String, String>();
    	    	entity.put("username", Config.User.getUserName());
    	    	entity.put("piao", String.valueOf(yubi.getAmount()));
    	    	List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
    	    	ShowProgressDialog("正在兑换中...");
    	    	addToThreadPool(Config.exchange_piao, "exchange piao request", params);
    		}
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
	public void onClick(View v) {
		switch(v.getId())
		{
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
		case Config.exchange_piao:
			try {
				
				Entity<Exchange> baseEntity = gson.fromJson(jsonString,
						new TypeToken<Entity<Exchange>>() {
						}.getType());
				if (baseEntity != null && baseEntity.getData() != null)
				{
					Exchange exchange = baseEntity.getData();
					InfoCache.getInstance().getPersonalInfo().setEntertainment_dollar(exchange.getUser_dollar());
					InfoCache.getInstance().getPersonalInfo().setPiaoLeft(exchange.getUser_piao_left());
					textViewAccount.setText(exchange.getUser_piao_left() + " 娱票");
					ToastUtil.show(ExchangeActivity.this, "兑换成功， 您兑换了： " + chargeYubi.getAmount() + " 个娛币。");
				}
			} catch (Exception e) {
				e.printStackTrace();
				XLog.e("json exception");
			}
			
			break;
		}
	}

	@Override
	public void onItemClick(View view, int position) {
		try {
			chargeYubi = (Yubi) view.getTag();
			sendExchangeYubiRequest(chargeYubi);
//			ToastUtil.show(ExchangeActivity.this, "您选择了充值： " + chargeYubi.getAmount() + " 个娛币，请选择支付方式。");
		} catch (Exception e) {
			e.printStackTrace();
			XLog.e("get tag exception on item click: ");
		}
	}

	@Override
	public void onItemLongClick(View view, int position) {
		
	}

}
