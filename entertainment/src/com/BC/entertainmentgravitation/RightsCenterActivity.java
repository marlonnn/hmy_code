package com.BC.entertainmentgravitation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.BC.entertainmentgravitation.fragment.CardBuyFragment;
import com.BC.entertainmentgravitation.fragment.CardPublishFragment;
import com.BC.entertainmentgravitation.fragment.CardSellFragment;
import com.summer.activity.BaseActivity;
import com.summer.config.Config;
import com.umeng.analytics.MobclickAgent;

public class RightsCenterActivity extends BaseActivity implements OnClickListener{

	private RadioGroup radio;
	private RadioButton rbtnBuy;
	private RadioButton rbtnSell;
	private TextView txtPublish;
	
	private CardPublishFragment cardPublishFragment;
	private CardBuyFragment cardBuyFragment;
	private CardSellFragment cardSellFragment;
	private FragmentManager fManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal_rights);
		fManager = getSupportFragmentManager();
		findViewById(R.id.imageViewBack).setOnClickListener(this);
		initView();
		setFragmentSelection(R.id.radio0);
	}
	
    private void hideFragments(FragmentTransaction transaction) {  
        if (cardPublishFragment != null) {  
            transaction.hide(cardPublishFragment);  
        }  
        if (cardBuyFragment != null) {  
            transaction.hide(cardBuyFragment);  
        }
        if (cardSellFragment != null) {  
            transaction.hide(cardSellFragment);  
        }
    }
    
	private void setFragmentSelection(int v)
	{
        FragmentTransaction transaction = fManager.beginTransaction();  
        hideFragments(transaction);
		switch(v)
		{

		case R.id.radio0:
            if (cardPublishFragment == null) {  
            	cardPublishFragment = new CardPublishFragment();
                transaction.add(R.id.fLayoutContent, cardPublishFragment);  
            } else {  
                transaction.show(cardPublishFragment);  
            } 
    		transaction.commit();
			break;
		case R.id.radio1:
            if (cardBuyFragment == null) {  
            	cardBuyFragment = new CardBuyFragment();
                transaction.add(R.id.fLayoutContent, cardBuyFragment);  
            } else {  
                transaction.show(cardBuyFragment);  
            } 
    		transaction.commit();
			break;
		case R.id.radio2:
            if (cardSellFragment == null) {  
            	cardSellFragment = new CardSellFragment();
                transaction.add(R.id.fLayoutContent, cardSellFragment);  
            } else {  
                transaction.show(cardSellFragment);  
            } 
    		transaction.commit();
			break;
		}

	}
	
	private void initView()
	{
		radio = (RadioGroup) findViewById(R.id.radioGroup1);
		rbtnBuy = (RadioButton) findViewById(R.id.radio1);
		rbtnSell = (RadioButton) findViewById(R.id.radio2);
		radio.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				setFragmentSelection(checkedId);
				switch (checkedId) {
				case R.id.radio0:
					break;
				case R.id.radio1:
					break;
				case R.id.radio2:
					break;

				}
			}
		});
		
		txtPublish = (TextView) findViewById(R.id.txtViewPublish);
		txtPublish.setOnClickListener(this);
		if (!Config.User.getPermission().contains("2") && Config.User.getCheckType().contains("1"))
		{
			rbtnBuy.setVisibility(View.GONE);
			rbtnSell.setVisibility(View.GONE);
			txtPublish.setVisibility(View.GONE);
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
		 * 发布权益
		 */
		case R.id.txtViewPublish:
			Intent intent = new Intent(RightsCenterActivity.this, PublishActivity.class);
			startActivity(intent);
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
		case Config.member_in:
			
			break;
		}
	}

}