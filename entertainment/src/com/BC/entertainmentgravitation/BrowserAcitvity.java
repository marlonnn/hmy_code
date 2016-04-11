package com.BC.entertainmentgravitation;

import com.BC.entertainmentgravitation.R;
import org.apache.http.util.EncodingUtils;

import com.summer.activity.BaseActivity;
import com.summer.dialog.CustomProgressDialog;
import com.summer.view.BaseWebView;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

public class BrowserAcitvity extends BaseActivity{
	
	private CustomProgressDialog progressDialog;

	public ValueCallback<Uri> mUploadMessage;
	public final static int FILECHOOSER_RESULTCODE = 1;
	public Uri imageUri;

	public View stop, refresh;
	LinearLayout firstBar, secondBar;
	public BaseWebView mWeb;
	private ProgressBar progressbar;
	String mNodeId;
	int mIsFaverite = 0;
	private CustomProgressDialog mProgressDlg = null;

	boolean isToDownload = false;
	String share = "";
	String nodename = "";
	String url = "";
	String post = "";
	CharSequence[] items = { "移动微博", "短信" };
	String[] sharetype = { "yidongweibo", "duanxin" };
	BrowserAcitvity content;

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			/*
			 * if (msg.what >= 100) { progressbar.setVisibility(View.GONE); }
			 * progressbar.setProgress(msg.what);
			 */
			if (msg.what == 0) {
				loadurl(mWeb, url);
			}
			if (msg.what == 1) {
				loadurl(mWeb, url, post);
			}
			super.handleMessage(msg);
		}
	};

	public void loadurl(final WebView view, final String url) {
		view.loadUrl(url);// 载入网页
	}

	public void loadurl(final WebView view, final String url, final String post) {
		Log.d("shuzhi", "post = " + EncodingUtils.getBytes(post, "BASE64"));
		view.postUrl(url, EncodingUtils.getBytes(post, "BASE64"));// 载入网页
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		this.setContentView(R.layout.activity_page_web);

		content = this;
		Intent intent = getIntent();
		url = intent.getStringExtra("url");
		isToDownload = intent.getBooleanExtra("isToDownload", false);

		post = intent.getStringExtra("post");

		if (post == null) {
			handler.sendEmptyMessage(0);
		} else {
			handler.sendEmptyMessage(1);
		}

		mWeb = (BaseWebView) this.findViewById(R.id.webView1);
		mWeb.requestFocus(View.FOCUS_DOWN);
		mWeb.setScrollBarStyle(0);// 滚动条风格，为0就是不给滚动条留空间，滚动条覆盖在网页上
		mWeb.isHardwareAccelerated();
		Log.d("shuzhi", "mweb = " + mWeb.isHardwareAccelerated());
		if (url != null) {
			mWeb.requestFocus();
		}
		onChangeMyWebViewStatus(mWeb);
	}

	public void onChangeMyWebViewStatus(BaseWebView web) {
	}

	public void progressbar(WebView view, int progress) {
		if (progress != 100) {
			refresh.setVisibility(View.GONE);
			stop.setVisibility(View.VISIBLE);
			progressbar.setVisibility(View.VISIBLE);
			progressbar.setProgress(progress);
		} else {
			refresh.setVisibility(View.VISIBLE);
			stop.setVisibility(View.GONE);
			progressbar.setVisibility(View.GONE);
			progressbar.setProgress(0);
		}
	}

	public void loading() {
		firstBar.setVisibility(View.VISIBLE);
		stop.setVisibility(View.VISIBLE);
		refresh.setVisibility(View.GONE);
		if (progressbar != null) {
			progressbar.setVisibility(View.VISIBLE);
		}
	}

	public void load_is_done() {
		refresh.setVisibility(View.VISIBLE);
		stop.setVisibility(View.GONE);
		firstBar.setVisibility(View.VISIBLE);
		if (progressbar != null) {
			progressbar.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onDestroy() {
		try {

			mWeb.stopLoading();
			mWeb.destroy();

		} catch (Exception e) {

			e.printStackTrace();
		}
		super.onDestroy();
	}

	public void showProgressDialog(int type) {
		try {
			if (mProgressDlg != null) {
				return;
			}
			mProgressDlg = CustomProgressDialog.CreateDialog(this);
			mProgressDlg.setMessage("请等待...");
			mProgressDlg.show();
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this, "请稍后", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onConfigurationChanged(Configuration config) {
		super.onConfigurationChanged(config);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		if (requestCode == FILECHOOSER_RESULTCODE) {
			if (null == mUploadMessage) {
				return;
			}
		}
	}

	public void requestSuccessful(String jsonString, int taskType) {

	}

	@Override
	public void RequestSuccessful(String jsonString, int taskType) {
		// TODO Auto-generated method stub
		
	}
}
