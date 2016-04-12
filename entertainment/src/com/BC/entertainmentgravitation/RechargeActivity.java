package com.BC.entertainmentgravitation;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.BC.entertainment.view.EmotionsView;
import com.BC.entertainmentgravitation.dialog.ChoosePayDialog;
import com.BC.entertainmentgravitation.dialog.ChoosePayDialog.ChoosePayCallback;
import com.BC.entertainmentgravitation.dialog.PayDialog;
import com.BC.entertainmentgravitation.entity.PayResult;
import com.BC.entertainmentgravitation.entity.PayWays;
import com.BC.entertainmentgravitation.entity.WxCheckOrder;
import com.BC.entertainmentgravitation.entity.WxOrder;
import com.BC.entertainmentgravitation.entity.WxPrePayOrder;
import com.BC.entertainmentgravitation.util.SignUtils;
import com.alipay.sdk.app.PayTask;
import com.google.gson.Gson;
import com.summer.activity.BaseActivity;
import com.summer.config.Config;
import com.summer.factory.ThreadPoolFactory;
import com.summer.handler.InfoHandler;
import com.summer.logger.XLog;
import com.summer.task.HttpBaseTask;
import com.summer.treadpool.ThreadPoolConst;
import com.summer.utils.Audio;
import com.summer.utils.JsonUtil;
import com.summer.utils.StringUtil;
import com.summer.utils.ToastUtil;
import com.summer.utils.UrlUtil;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class RechargeActivity extends BaseActivity implements OnClickListener{

	// �̻�PID
	private final String PARTNER = "2088711408262791";
	// �̻��տ��˺�
	private final String SELLER = "13911533774@163.com";
	// �̻�˽Կ��pkcs8��ʽ
	private final String RSA_PRIVATE = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAM083JmW+LUJYG90UVsEBaZH1TvIFZmE8odQBMltupc4kXAZQppBh4DMhoQ8BpJRibZMnR2lqB+OwEP6ZfdKGGRW41XbEF1mXCKFwNZXyDoArlOFb8P/OPw22WbQAbtlZJndM15PGMONReje149FMoOD7SCdfTslTSyvVisI8atnAgMBAAECgYEAxvV1NT9xoq6QWft80qq3f1ark+SHa+fB5QLhYROKiwY/l1glhLx5y5Z54L7/7+AzjBIBTbhFnzIXmM6pAm1F7mMJS1nxELHqytikxpyynHvmUJj976ZL2rrG3RsDaQXF/mrM1+Y6Ajps1hwt+uOtnBDJRnPM9mBD0wGAH9OKBCECQQDoDYEgxePw8LgmmseNhET00VHdE8CL6pcZ46ttivYYpR9mckRy2C9VlQiOYDGbk10H1chXih1erruOEDCd/bWNAkEA4mrxnS4tTk25LJak36pApSYlt1KdOuEtG+wDbIT4a78ffdEgnhP2gKTmBBVXPbHImspSFGO9OZQpYk/7Zv8lwwJAWXC7EJK1pKxjjh2iRJ1yppn3X6q5UDR/QO9Lp9EjwaQDUk1ArLM+q1HiFl5lQH2wIdD4gyUs5M2cZMlAs+SSEQJBAJeeIbmtqG0dIvk2z6VvLuboiq0eR2ecTka6XviWenw8eewY1IzGtXUj91uYptkLalgtT5WTzKz4CFZrVOB9z10CQFD3aoS4/4ya4H6/TvxL254wz+qtL6pxUc7mI5HWpd17lZFfB2aFwQ8UaNjDZfr5v5fzM6oFWF11JcoyBlLgGhM=";
	// ֧������Կ
	private final String RSA_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";
	
	private final int SDK_PAY_FLAG = 1;

	private final int SDK_CHECK_FLAG = 2;
	
	/**
	 * ֧������ҽ��
	 */
	private String price = "";
	/**
	 * �������
	 */
	private String amount = "";
	/**
	 * ����ID
	 */
	private String orderID = "";
	
	private IWXAPI api;
	
	private ChoosePayDialog choosePayDialog;
	
	private WxCheckOrder wxCheckOrder = new WxCheckOrder();
	
	//΢��֧����Ԥ֧��
	private WxPrePayOrder wxPrePayOrder;
	
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SDK_PAY_FLAG: {
				PayResult payResult = new PayResult((String) msg.obj);

				String resultStatus = payResult.getResultStatus();
				// �ж�resultStatus Ϊ��9000�������֧���ɹ�������״̬�������ɲο��ӿ��ĵ�
				if (TextUtils.equals(resultStatus, "9000")) {
					ToastUtil.show(RechargeActivity.this, 
							StringUtil.getXmlResource(RechargeActivity.this, R.string.activity_recharge_pay_success));
					showEmotionsView();
					senAlipayOKRequest();
				} else {
					// �ж�resultStatus Ϊ�ǡ�9000����������֧��ʧ��
					// ��8000������֧�������Ϊ֧������ԭ�����ϵͳԭ���ڵȴ�֧�����ȷ�ϣ����ս����Ƿ�ɹ��Է�����첽֪ͨΪ׼��С����״̬��
					if(TextUtils.equals(resultStatus, "8000")) {
						Toast.makeText(RechargeActivity.this, "֧�����ȷ����",
								Toast.LENGTH_SHORT).show();

					} else {
						// ����ֵ�Ϳ����ж�Ϊ֧��ʧ�ܣ������û�����ȡ��֧��������ϵͳ���صĴ���
						Toast.makeText(RechargeActivity.this, "֧��ʧ��",
								Toast.LENGTH_SHORT).show();

					}
				}
				break;
			}
			case SDK_CHECK_FLAG: {
				Toast.makeText(RechargeActivity.this, "�����Ϊ��" + msg.obj,
						Toast.LENGTH_SHORT).show();
				break;
			}
			case Config.WX_EXCEPTION_ERROR:
				Toast.makeText(RechargeActivity.this, "΢��֧��ʧ�ܣ�֧���쳣",
						Toast.LENGTH_SHORT).show();
				break;
				
			case Config.PAY_WX:
				createWxPrePay();
				break;
			case Config.PAY_ALI:
				pay();
				break;
			default:
				break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recharge);

		findViewById(R.id.up6).setOnClickListener(this);
		findViewById(R.id.up18).setOnClickListener(this);
		findViewById(R.id.up30).setOnClickListener(this);
		findViewById(R.id.up45).setOnClickListener(this);
		findViewById(R.id.up98).setOnClickListener(this);
		findViewById(R.id.up298).setOnClickListener(this);
		// senAlipayOKRequest();
		api = WXAPIFactory.createWXAPI(this, null);
		api.registerApp(Config.APP_ID);
		LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
			      new IntentFilter("wxPayEvent"));
	}
	
	BroadcastReceiver mMessageReceiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
		    int errorCode = intent.getIntExtra("errorCode", 1);
		    XLog.i("Got message: " + errorCode);
		    switch(errorCode)
		    {
		    case -2:
		    	//�û�ȡ��΢��֧��
		    	XLog.i("cancel");
		    	ToastUtil.show(RechargeActivity.this, StringUtil.getXmlResource(context, R.string.activity_recharge_pay_cancel));
		    	break;
		    case -1:
		    	XLog.i("error");
		    	ToastUtil.show(RechargeActivity.this, StringUtil.getXmlResource(context, R.string.activity_recharge_pay_exception));
		    	//�����쳣
		    	break;
		    case 0:
		    	//֧���ɹ�
		    	XLog.i("success");
				checkWxOrder();

		    	break;
		    case 1:
		    	//Ĭ��ֵ
		    	XLog.i("default");
		    	break;
		    }
		}
		
	};
	
	//֧��ѡ��Ի���ص�����
	ChoosePayCallback choosePayCallback = new ChoosePayCallback()
	{

		@Override
		public void pay(Handler handler, PayWays payWays) {
			switch(payWays.getPayId())
			{
			case Config.PAY_WX:
				//΢��֧��
				Message wxMsg = new Message();
				wxMsg.what = Config.PAY_WX;
				mHandler.sendMessage(wxMsg);
				break;
			case Config.PAY_ALI:
				//֧����֧��
				Message aLiMsg = new Message();
				aLiMsg.what = Config.PAY_ALI;
				mHandler.sendMessage(aLiMsg);
				break;
			}
		}
		
	};
	
	/**
	 * ����Ԥ֧������
	 */
	private void createWxPrePay()
	{
		wxPrePayOrder = new WxPrePayOrder();
		wxPrePayOrder.setClientID(Config.User.getClientID());
		wxPrePayOrder.setProductname(Config.BODY);
		wxPrePayOrder.setPrice(Integer.parseInt(price) * 100);
		wxPrePayOrder.setAmount(Integer.parseInt(amount));
		
		String content = getJSONObject(wxPrePayOrder);
		addToThreadPool(Config.wx_pre_pay, "create wx prePay", content);
	}
	
	/**
	 * ��鶩��
	 */
	private void checkWxOrder()
	{
		String content = getJSONObject(wxCheckOrder);
		addToThreadPool(Config.wx_pay, "create check order", content);
	}
	
	private String getJSONObject(Object object) {

		String jsonSting = "";
		try {
			Gson gson = new Gson();
			jsonSting = gson.toJson(object);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonSting;
	}
	
	private void createChoosePayDialog(Context context, Handler handler)
	{
		choosePayDialog = new ChoosePayDialog(context, handler);
		choosePayDialog.SetChoosePayCallback(choosePayCallback);
		choosePayDialog.Show();
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.up6:
			// sendAlipayRequest(18);
			price = "6";
			amount = "600";
			createChoosePayDialog(RechargeActivity.this, mHandler);
//			pay();
//			test();
//			startWxPay();
			break;
		case R.id.up18:
			// sendAlipayRequest(18);
			price = "18";
			amount = "2200";
			createChoosePayDialog(RechargeActivity.this, mHandler);
//			pay();
			break;
		case R.id.up30:
			// sendAlipayRequest(30);
			price = "30";
			amount = "3600";
			createChoosePayDialog(RechargeActivity.this, mHandler);
//			pay();
			break;
		case R.id.up45:
			// sendAlipayRequest(45);
			price = "45";
			amount = "5400";
			createChoosePayDialog(RechargeActivity.this, mHandler);
//			pay();
			break;
		case R.id.up98:
			// sendAlipayRequest(98);
			price = "98";
			amount = "12000";
			createChoosePayDialog(RechargeActivity.this, mHandler);
//			pay();
			break;
		case R.id.up298:
			// sendAlipayRequest(298);
			// pay(v);
			showAlertDialog();
			break;
		}
	}

	private RefreshHandler mRedrawHandler = new RefreshHandler();
	EmotionsView ev;
	AudioTrack audioTrack;

	private void showEmotionsView() {
		// ��ñ�������ͼ,����icon���ڴ�(�ڲ����ļ��������Զ���EmotionsView)
		ev = (EmotionsView) findViewById(R.id.emotion_view); //
		// �˴���ʵ�ֱ���ͼƬ�ĸ��棬�����ж����Է��͵��ı�����
		int intDrawable = R.drawable.money;

		audioTrack = Audio.palyAudio(this, R.raw.g5293);

		ev.LoadEmotionImage(intDrawable);
		ev.setVisibility(View.VISIBLE); // ��ȡ��ǰ��Ļ�ĸߺͿ�
		DisplayMetrics dm = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(dm);
		ev.setView(dm.heightPixels, dm.widthPixels);
		updateEmotions();
	}

	class RefreshHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			if (ev == null || ev.isEnd()) {
				if (audioTrack != null) {
					audioTrack.stop();
					audioTrack.release();
				}
				return;
			}
			ev.addRandomEmotion();
			ev.invalidate();
			sleep(50);
		}

		public void sleep(long delayMillis) {
			this.removeMessages(0);
			sendMessageDelayed(obtainMessage(0), delayMillis);
		}
	};

	public void updateEmotions() {
		ev.setEnd(false);
		ev.clearAllEmotions();
		ev.addRandomEmotion();
		mRedrawHandler.removeMessages(0);
		mRedrawHandler.sleep(100);
	}

	/**
	 * call alipay sdk pay. ����SDK֧��
	 * 
	 */
	public void pay() {
		if (TextUtils.isEmpty(PARTNER) || TextUtils.isEmpty(RSA_PRIVATE)
				|| TextUtils.isEmpty(SELLER)) {
			new AlertDialog.Builder(this)
					.setTitle("����")
					.setMessage("��Ҫ����PARTNER | RSA_PRIVATE| SELLER")
					.setPositiveButton("ȷ��",
							new DialogInterface.OnClickListener() {
								public void onClick(
										DialogInterface dialoginterface, int i) {
									//
									finish();
								}
							}).show();
			return;
		}
		// ����
		String orderInfo = getOrderInfo("����" + amount + "�����",
				Config.User.getNickName() + "�����ֵ" + price + "�����",
				price);

		// �Զ�����RSA ǩ��
		String sign = sign(orderInfo);
		try {
			// �����sign ��URL����
			sign = URLEncoder.encode(sign, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		// �����ķ���֧���������淶�Ķ�����Ϣ
		final String payInfo = orderInfo + "&sign=\"" + sign + "\"&"
				+ getSignType();

		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				// ����PayTask ����
				sendAlipayRequest();
				PayTask alipay = new PayTask(RechargeActivity.this);
				// ����֧���ӿڣ���ȡ֧�����
				String result = alipay.pay(payInfo);

				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};
		// �����첽����
		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}
	
	/**
	 * ��ֵ
	 */
	private void sendAlipayRequest() {
		if (Config.User == null) {
			ToastUtil.show(this, this.getString(R.string.activity_recharge_get_user_fail));
			return;
		}
		HashMap<String, String> entity = new HashMap<String, String>();

		entity.put("clientID", Config.User.getClientID());
		entity.put("amount", "" + amount);
		entity.put("price", price);
		entity.put("order_sn", orderID);
		ShowProgressDialog(this.getString(R.string.activity_recharge_get_charging));
		List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
		addToThreadPool(Config.top_up, "send alipay request", params);
	}
	
	/**
	 * ��ֵ�ɹ�
	 */
	private void senAlipayOKRequest() {
		if (Config.User == null) {
			ToastUtil.show(this, this.getString(R.string.activity_recharge_get_user_fail));
			return;
		}
		HashMap<String, String> entity = new HashMap<String, String>();
		entity.put("order_sn", orderID);
		List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
		addToThreadPool(Config.recharge_success, "send alipay ok request", params);
	}
	
    private void addToThreadPool(int taskType, String Tag, List<NameValuePair> params)
    {
    	HttpBaseTask httpTask = new HttpBaseTask(ThreadPoolConst.THREAD_TYPE_FILE_HTTP, Tag, params, UrlUtil.GetUrl(taskType));
    	httpTask.setTaskType(taskType);
    	InfoHandler handler = new InfoHandler(this);
    	httpTask.setInfoHandler(handler);
    	ThreadPoolFactory.getThreadPoolManager().addTask(httpTask);
    }
    
    private void addToThreadPool(int taskType, String Tag, String constent)
    {
    	HttpBaseTask httpTask = new HttpBaseTask(ThreadPoolConst.THREAD_TYPE_FILE_HTTP, Tag, constent, UrlUtil.GetUrl(taskType));
    	httpTask.setTaskType(taskType);
    	InfoHandler handler = new InfoHandler(this);
    	httpTask.setInfoHandler(handler);
    	ThreadPoolFactory.getThreadPoolManager().addTask(httpTask);
    }

	/**
	 * check whether the device has authentication alipay account.
	 * ��ѯ�ն��豸�Ƿ����֧������֤�˻�
	 */
	public void check(View v) {
		Runnable checkRunnable = new Runnable() {

			@Override
			public void run() {
				// ����PayTask ����
				PayTask payTask = new PayTask(RechargeActivity.this);
				// ���ò�ѯ�ӿڣ���ȡ��ѯ���
				boolean isExist = payTask.checkAccountIfExist();

				Message msg = new Message();
				msg.what = SDK_CHECK_FLAG;
				msg.obj = isExist;
				mHandler.sendMessage(msg);
			}
		};

		Thread checkThread = new Thread(checkRunnable);
		checkThread.start();

	}

	/**
	 * get the sdk version. ��ȡSDK�汾��
	 */
	public void getSDKVersion() {
		PayTask payTask = new PayTask(this);
		String version = payTask.getVersion();
		Toast.makeText(this, version, Toast.LENGTH_SHORT).show();
	}

	/**
	 * create the order info. ����������Ϣ
	 */
	public String getOrderInfo(String subject, String body, String price) {

		// ǩԼ���������ID
		String orderInfo = "partner=" + "\"" + PARTNER + "\"";

		// ǩԼ����֧�����˺�
		orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

		// �̻���վΨһ������

		orderID = getOutTradeNo();
		orderInfo += "&out_trade_no=" + "\"" + orderID + "\"";

		// ��Ʒ����
		orderInfo += "&subject=" + "\"" + subject + "\"";

		// ��Ʒ����
		orderInfo += "&body=" + "\"" + body + "\"";

		// ��Ʒ���
		orderInfo += "&total_fee=" + "\"" + price + "\"";

		// �������첽֪ͨҳ��·��
		orderInfo += "&notify_url=" + "\""
				+ "http://120.25.107.200/php/alipay/notify_url.php" + "\"";

		// ����ӿ����ƣ� �̶�ֵ
		orderInfo += "&service=\"mobile.securitypay.pay\"";

		// ֧�����ͣ� �̶�ֵ
		orderInfo += "&payment_type=\"1\"";

		// �������룬 �̶�ֵ
		orderInfo += "&_input_charset=\"utf-8\"";

		// ����δ����׵ĳ�ʱʱ��
		// Ĭ��30���ӣ�һ����ʱ���ñʽ��׾ͻ��Զ����رա�
		// ȡֵ��Χ��1m��15d��
		// m-���ӣ�h-Сʱ��d-�죬1c-���죨���۽��׺�ʱ����������0��رգ���
		// �ò�����ֵ������С���㣬��1.5h����ת��Ϊ90m��
		orderInfo += "&it_b_pay=\"30m\"";

		// extern_tokenΪ���������Ȩ��ȡ����alipay_open_id,���ϴ˲����û���ʹ����Ȩ���˻�����֧��
		// orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

		// ֧��������������󣬵�ǰҳ����ת���̻�ָ��ҳ���·�����ɿ�
		orderInfo += "&return_url=\"m.alipay.com\"";

		// �������п�֧���������ô˲���������ǩ���� �̶�ֵ ����ҪǩԼ���������п����֧��������ʹ�ã�
		// orderInfo += "&paymethod=\"expressGateway\"";

		return orderInfo;
	}

	/**
	 * get the out_trade_no for an order. �����̻������ţ���ֵ���̻���Ӧ����Ψһ�����Զ����ʽ�淶��
	 * 
	 */
	public String getOutTradeNo() {
		SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss",
				Locale.getDefault());
		Date date = new Date();
		String key = format.format(date);

		Random r = new Random();
		key = key + r.nextInt();
		key = key.substring(0, 15);
		return key;
	}

	/**
	 * sign the order info. �Զ�����Ϣ����ǩ��
	 * 
	 * @param content
	 *            ��ǩ��������Ϣ
	 */
	public String sign(String content) {
		return SignUtils.sign(content, RSA_PRIVATE);
	}

	/**
	 * get the sign type we use. ��ȡǩ����ʽ
	 */
	public String getSignType() {
		return "sign_type=\"RSA\"";
	}

	public void showAlertDialog() {
		final PayDialog.Builder builder = new PayDialog.Builder(this);
		builder.setTitle("�������ֵ����ҽ��");
		builder.setMessage("��������");
		builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				EditText message = (EditText) builder
						.findViewById(R.id.message);
				if (message != null && !message.getText().toString().equals("")) {
					createChoosePayDialog(RechargeActivity.this, mHandler);
//					pay();
				} else {
					ToastUtil.show(RechargeActivity.this, "��Ǹû������ֵ�Ľ��");
				}
				dialog.dismiss();
			}
		});

		builder.setNegativeButton("ȡ��",
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		PayDialog animationDialog = builder.create();
		EditText editText = (EditText) builder.findViewById(R.id.message);
		final TextView textView1 = (TextView) builder
				.findViewById(R.id.textView1);
		final TextView textView2 = (TextView) builder
				.findViewById(R.id.textView2);
		final TextView textView3 = (TextView) builder
				.findViewById(R.id.TextView03);
		final TextView textView4 = (TextView) builder
				.findViewById(R.id.TextView04);
		editText.setText("20");
		textView1.setText("�һ����������");
		textView2.setText("" + 2000);
		textView4.setText("���͵����������");
		textView3.setText(2000 * 0.02 + "");
		price = "20";
		amount = "2040";
		editText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				String ts = s.toString();
				if (!ts.equals("")) {
					int v = Integer.valueOf(ts);
					price = ts;
					textView2.setText("" + v * 100);
					textView4.setText("���͵����������");
					if (v >= 20) {
						amount = "" + (v * 2 + v * 100);
						textView3.setText(v * 2 + "");
					} else {
						amount = "" + (v * 100);
						textView3.setText("0");
					}
				}
			}
		});
		animationDialog.show();
	}

	@Override
	public void RequestSuccessful(String jsonString, int taskType) {
		switch(taskType)
		{
		case Config.wx_pre_pay:
			//���ܵ�΢��Ԥ֧��������Ϣ
			Toast.makeText(RechargeActivity.this, "��������֧��", Toast.LENGTH_SHORT).show();
			startWxPayThread(jsonString);
			break;
		case Config.wx_pay:
			//���ܵ�΢��֧����ɶ�����Ϣ
			showEmotionsView();
			break;
		}
	}
	
    private void startWxPayThread(final String jsonString)
    {
    	
    	Runnable payRunnable = new Runnable(){

			@Override
			public void run() {
				startWxOrder(jsonString);
			}
    	};
		Thread payThread = new Thread(payRunnable);
		payThread.start();
    }

    private void startWxOrder(String jsonString)
    {
    	WxOrder wxOrder = GetWxOrder(jsonString);
		PayReq req = new PayReq();
		req.appId			= wxOrder.getAppid();
		req.partnerId		= wxOrder.getPartnerid();
		req.prepayId		= wxOrder.getPrepayid();
		req.nonceStr		= wxOrder.getNoncestr();
		req.timeStamp		= wxOrder.getTimestamp();
		req.packageValue	= wxOrder.getWxPackage();
		req.sign			= wxOrder.getSign();
		api.sendReq(req);
    }
    
    public WxOrder GetWxOrder(String jsonString)
    {
    	WxOrder wxOrder = new WxOrder();
    	JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(jsonString).getJSONObject("data");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Message message = new Message();
        	message.what = Config.WX_EXCEPTION_ERROR;
        	mHandler.sendMessage(message);
			e.printStackTrace();
		} 
    	if(jsonObject != null)
    	{
			try {
				wxOrder.setPrepayid(jsonObject.getString("prepayid"));
				wxOrder.setNoncestr(jsonObject.getString("noncestr"));
				wxOrder.setTimeStamp(jsonObject.getString("timestamp"));
				wxOrder.setSign(jsonObject.getString("sign"));
				wxCheckOrder.setClientID(Config.User.getClientID());
				wxCheckOrder.setRechargesn(jsonObject.getString("preorder"));
			} catch (JSONException e) {
	        	Message message = new Message();
	        	message.what = Config.WX_EXCEPTION_ERROR;
	        	mHandler.sendMessage(message);
				e.printStackTrace();
			}
    	}
    	return wxOrder;
    }
}
