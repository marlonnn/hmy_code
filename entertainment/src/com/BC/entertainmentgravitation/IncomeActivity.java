package com.BC.entertainmentgravitation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.BC.entertainment.cache.InfoCache;
import com.BC.entertainmentgravitation.entity.EditPersonal;
import com.summer.activity.BaseActivity;
import com.summer.logger.XLog;

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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal_income);
		initView();
	}
	
	private void initView()
	{
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
		try {
			EditPersonal personal = InfoCache.getInstance().getPersonalInfo();
			if( personal != null && personal.getEntertainment_dollar() != null)
			{
				yuPiao.setText(personal.getPiao());
				withDraw.setText(String.valueOf(getValue(personal.getPiao())));
			}
		} catch (Exception e) {
			e.printStackTrace();
			XLog.e("init data exception");
		}
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		initData();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}

	private int getValue(String yupiao)
	{
		int value = 0;
		try {
			double piao = Double.parseDouble(yupiao);
			
			if (piao <= 10000)
			{
				value = (int) (piao * 0.4);
			}
			else if( piao <= 1000000 && piao >10000)
			{
				value = (int) (piao * 0.45);
			}
			else if( piao <= 2000000 && piao >1000000)
			{
				value = (int) (piao * 0.5);
			}
			else if( piao <= 3000000 && piao >1000000)
			{
				value = (int) (piao * 0.55);
			}
			else if( piao <= 4000000 && piao >3000000)
			{
				value = (int) (piao * 0.6);
			}
			else if(piao >4000000)
			{
				value = (int) (piao * 0.7);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return value;
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
		}
	}

}
