package com.BC.entertainmentgravitation;

import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.sina.weibo.SinaWeibo.ShareParams;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;

import com.BC.entertainment.cache.InfoCache;
import com.BC.entertainmentgravitation.dialog.ApplauseGiveConcern;
import com.summer.activity.BaseActivity;
import com.summer.logger.XLog;
import com.summer.utils.ToastUtil;
import com.umeng.analytics.MobclickAgent;

public class FinishActivity extends BaseActivity implements OnClickListener, PlatformActionListener{

	private TextView txtTotalPeople;
	
    private ApplauseGiveConcern applauseGiveConcern;//投资或者撤资、关注
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_finish);
		initView();
		
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
		ShareParams sp = new ShareParams();
//		UMImage image = new UMImage(FinishActivity.this, "http://app.haimianyu.cn/DOWNLOAD/app_logo.png");
		switch(v.getId())
		{
		case R.id.imageViewWeibo:
			sp.setTitle("演员正在直播！导演你快来......");
			sp.setText("看演员，去海绵娱直播APP!");
			sp.setImagePath("http://app.haimianyu.cn/DOWNLOAD/app_logo.png");

			Platform weibo = ShareSDK.getPlatform(SinaWeibo.NAME);
			weibo.setPlatformActionListener(this); // 设置分享事件回调
			// 执行图文分享
			weibo.share(sp);
            break;
		case R.id.imageViewWeixin:
			sp.setTitle("演员正在直播！导演你快来......");
			sp.setText("看演员，去海绵娱直播APP!");
			sp.setImageUrl("http://app.haimianyu.cn/DOWNLOAD/app_logo.png");
			sp.setSite("发布分享的网站名称");
			sp.setSiteUrl("发布分享网站的地址");

			Platform wx = ShareSDK.getPlatform (Wechat.NAME);
			wx.setPlatformActionListener (this); // 设置分享事件回调
			// 执行图文分享
			wx.share(sp);
            break;
		case R.id.imageViewQq:
			sp.setTitle("演员正在直播！导演你快来......");
			sp.setText("看演员，去海绵娱直播APP!");
			sp.setImageUrl("http://app.haimianyu.cn/DOWNLOAD/app_logo.png");
			sp.setSite("发布分享的网站名称");
			sp.setSiteUrl("发布分享网站的地址");

			Platform qzone = ShareSDK.getPlatform (QZone.NAME);
			qzone.setPlatformActionListener (this); // 设置分享事件回调
            break;
		case R.id.imageViewZone:
			sp.setTitle("演员正在直播！导演你快来......");
			sp.setText("看演员，去海绵娱直播APP!");
			sp.setImageUrl("http://app.haimianyu.cn/DOWNLOAD/app_logo.png");
			sp.setSite("发布分享的网站名称");
			sp.setSiteUrl("发布分享网站的地址");

			Platform zone = ShareSDK.getPlatform (QZone.NAME);
			zone.setPlatformActionListener (this); // 设置分享事件回调
            break;
		case R.id.imageViewPengyou:
			sp.setTitle("演员正在直播！导演你快来......");
			sp.setText("看演员，去海绵娱直播APP!");
			sp.setImageUrl("http://app.haimianyu.cn/DOWNLOAD/app_logo.png");
			sp.setSite("发布分享的网站名称");
			sp.setSiteUrl("发布分享网站的地址");

			Platform p = ShareSDK.getPlatform (Wechat.NAME);
			p.setPlatformActionListener (this); // 设置分享事件回调
			break;
		case R.id.imageViewFocus:
			if (applauseGiveConcern != null)
			{
				applauseGiveConcern.sendFocusRequest();
			}
			break;
			
		case R.id.imageViewBack:
			intent = new Intent(this, HomeActivity.class);
			startActivity(intent);
			this.finish();
			break;
		}
	}

	@Override
	public void RequestSuccessful(String jsonString, int taskType) {
		
	}

	@Override
	public void onCancel(Platform arg0, int arg1) {
		ToastUtil.show(this, "分享取消");
	}

	@Override
	public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
		ToastUtil.show(this, "分享成功");
		
	}

	@Override
	public void onError(Platform arg0, int arg1, Throwable arg2) {
		ToastUtil.show(this, "分享异常");
	}

}
