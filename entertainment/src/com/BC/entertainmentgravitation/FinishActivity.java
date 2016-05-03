package com.BC.entertainmentgravitation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.BC.entertainment.cache.InfoCache;
import com.BC.entertainmentgravitation.dialog.ApplauseGiveConcern;
import com.summer.activity.BaseActivity;
import com.summer.logger.XLog;
import com.summer.utils.ToastUtil;

public class FinishActivity extends BaseActivity implements OnClickListener{

	private TextView txtTotalPeople;
	
    private ApplauseGiveConcern applauseGiveConcern;//投资或者撤资、关注
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_finish);
		initView();
	}
	
	private void initView()
	{
		try {
			Bundle bundle = getIntent().getExtras();
			txtTotalPeople = (TextView) findViewById(R.id.txtTotalPeople);
			txtTotalPeople.setText(String.valueOf(bundle.getLong("totalPeople")));
			
			findViewById(R.id.imageViewWeibo).setOnClickListener(this);
			findViewById(R.id.imageViewWeixin).setOnClickListener(this);
			findViewById(R.id.imageViewQq).setOnClickListener(this);
			findViewById(R.id.imageViewZone).setOnClickListener(this);
			findViewById(R.id.imageViewPengyou).setOnClickListener(this);
			
			
			findViewById(R.id.imageViewFocus).setOnClickListener(this);
			findViewById(R.id.imageViewBack).setOnClickListener(this);
			
			/**
			 * 初始化投资和撤资弹出对话框
			 */
			applauseGiveConcern = new ApplauseGiveConcern(this,
					InfoCache.getInstance().getStartInfo().getStar_ID(), this,
					InfoCache.getInstance().getStartInfo()
							.getThe_current_hooted_thumb_up_prices(),
					InfoCache.getInstance().getStartInfo().getStage_name());
			
		} catch (Exception e) {
			e.printStackTrace();
			XLog.e("exception");
		}
	}
	
	@Override
	public void onClick(View v) {
		Intent intent;
		switch(v.getId())
		{
		case R.id.imageViewWeibo:
		case R.id.imageViewWeixin:
		case R.id.imageViewQq:
		case R.id.imageViewZone:
		case R.id.imageViewPengyou:
			ToastUtil.show(this, "此功能正在完善中，敬请期待...");
			break;
		case R.id.imageViewFocus:
			if (applauseGiveConcern != null)
			{
				applauseGiveConcern.sendFocusRequest();
			}
			break;
			
		case R.id.imageViewBack:
			intent = new Intent(this, MainActivity.class);
			startActivity(intent);
			this.finish();
			break;
		}
	}

	@Override
	public void RequestSuccessful(String jsonString, int taskType) {
		
	}

}
