package com.BC.entertainmentgravitation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.summer.activity.BaseActivity;
import com.umeng.analytics.MobclickAgent;

public class RightsCenterActivity extends BaseActivity implements OnClickListener{

	
	private RadioGroup radio;
	private TextView txtPublish;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal_rights);
		findViewById(R.id.imageViewBack).setOnClickListener(this);
		initView();
	}
	
	private void initView()
	{
		radio = (RadioGroup) findViewById(R.id.radioGroup1);
		radio.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
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
		
	}

}
