package com.BC.entertainmentgravitation;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;

import com.summer.activity.BaseActivity;
import com.umeng.analytics.MobclickAgent;

public class MessageActivity extends BaseActivity implements OnClickListener{

	private RecyclerView messageList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message);
		findViewById(R.id.imageViewBack).setOnClickListener(this);
		findViewById(R.id.txtViewIgnore).setOnClickListener(this);
	}
	
	private void initView()
	{
		messageList = (RecyclerView) findViewById(R.id.listViewMessage);
		
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
		 * 忽略未读
		 */
		case R.id.txtViewIgnore:
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
