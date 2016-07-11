package com.BC.entertainmentgravitation;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;

import com.BC.entertainmentgravitation.entity.Huodong;
import com.summer.activity.BaseActivity;
import com.umeng.analytics.MobclickAgent;

public class HuodongDetailActivity extends BaseActivity implements OnClickListener{

	private Huodong huodong;
	private WebView webView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_huodong_detail);
		findViewById(R.id.imageViewBack).setOnClickListener(this);
        Intent intent = this.getIntent();
        huodong = (Huodong)intent.getSerializableExtra("huodong");
        webView = (WebView) findViewById(R.id.webView1);
        try {
        	webView.loadDataWithBaseURL(null, Html.fromHtml(huodong.getContent()) + "", "text/html", "utf-8", null);
		} catch (Exception e) {
			e.printStackTrace();
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
