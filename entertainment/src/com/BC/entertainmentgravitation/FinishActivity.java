package com.BC.entertainmentgravitation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.BC.entertainment.cache.InfoCache;
import com.BC.entertainmentgravitation.dialog.ApplauseGiveConcern;
import com.summer.activity.BaseActivity;
import com.summer.logger.XLog;
import com.summer.utils.ToastUtil;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.utils.Log;

public class FinishActivity extends BaseActivity implements OnClickListener{

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
		UMImage image = new UMImage(FinishActivity.this, "http://app.haimianyu.cn/DOWNLOAD/app_logo.png");
		switch(v.getId())
		{
		case R.id.imageViewWeibo:
            new ShareAction(this).setPlatform(SHARE_MEDIA.SINA).setCallback(umShareListener)
            .withMedia(image)
            .withTitle("演员正在直播！导演你快来......")
            .withText("看演员，去海绵娱直播APP!")
            .withTargetUrl("http://a.app.qq.com/o/simple.jsp?pkgname=com.BC.entertainmentgravitation")
           .share();
            break;
		case R.id.imageViewWeixin:
            new ShareAction(this).setPlatform(SHARE_MEDIA.WEIXIN).setCallback(umShareListener)
            .withMedia(image)
            .withTitle("演员正在直播！导演你快来......")
            .withText("看演员，去海绵娱直播APP!")
            .withTargetUrl("http://a.app.qq.com/o/simple.jsp?pkgname=com.BC.entertainmentgravitation")
           .share();
            break;
		case R.id.imageViewQq:
            new ShareAction(this).setPlatform(SHARE_MEDIA.QQ).setCallback(umShareListener)
            .withMedia(image)
            .withTitle("演员正在直播！导演你快来......")
            .withText("看演员，去海绵娱直播APP!")
            .withTargetUrl("http://a.app.qq.com/o/simple.jsp?pkgname=com.BC.entertainmentgravitation")
           .share();
            break;
		case R.id.imageViewZone:
            new ShareAction(this).setPlatform(SHARE_MEDIA.QZONE).setCallback(umShareListener)
            .withMedia(image)
            .withTitle("演员正在直播！导演你快来......")
            .withText("看演员，去海绵娱直播APP!")
            .withTargetUrl("http://a.app.qq.com/o/simple.jsp?pkgname=com.BC.entertainmentgravitation")
           .share();
            break;
		case R.id.imageViewPengyou:
//			ToastUtil.show(this, "此功能正在完善中，敬请期待...");
            new ShareAction(this).setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE).setCallback(umShareListener)
            .withMedia(image)
            .withTitle("演员正在直播！导演你快来......")
            .withText("看演员，去海绵娱直播APP!")
            .withTargetUrl("http://a.app.qq.com/o/simple.jsp?pkgname=com.BC.entertainmentgravitation")
           .share();
			break;
		case R.id.imageViewFocus:
			if (applauseGiveConcern != null)
			{
				applauseGiveConcern.sendFocusRequest();
			}
			break;
			
		case R.id.imageViewBack:
			intent = new Intent(this, MainEntryActivity.class);
			startActivity(intent);
			this.finish();
			break;
		}
	}
	
    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            Log.d("plat","platform"+platform);
            Toast.makeText(FinishActivity.this, platform + " 分享成功啦", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(FinishActivity.this,platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(FinishActivity.this,platform + " 分享取消了", Toast.LENGTH_SHORT).show();
        }
    };

	@Override
	public void RequestSuccessful(String jsonString, int taskType) {
		
	}

}
