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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.BC.entertainment.adapter.ChargeRecycleAdapter;
import com.BC.entertainment.adapter.ChargeRecycleAdapter.OnItemClickListener;
import com.BC.entertainment.cache.InfoCache;
import com.BC.entertainment.cache.YubiCache;
import com.BC.entertainmentgravitation.entity.EditPersonal;
import com.BC.entertainmentgravitation.entity.PayResult;
import com.BC.entertainmentgravitation.entity.WxCheckOrder;
import com.BC.entertainmentgravitation.entity.WxOrder;
import com.BC.entertainmentgravitation.entity.WxPrePayOrder;
import com.BC.entertainmentgravitation.entity.Yubi;
import com.BC.entertainmentgravitation.util.SignUtils;
import com.alipay.sdk.app.PayTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.summer.activity.BaseActivity;
import com.summer.config.Config;
import com.summer.factory.ThreadPoolFactory;
import com.summer.handler.InfoHandler;
import com.summer.json.Entity;
import com.summer.logger.XLog;
import com.summer.task.HttpBaseTask;
import com.summer.treadpool.ThreadPoolConst;
import com.summer.utils.JsonUtil;
import com.summer.utils.StringUtil;
import com.summer.utils.ToastUtil;
import com.summer.utils.UrlUtil;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;

public class ChargeActivity extends BaseActivity implements OnClickListener, OnItemClickListener{
	
	// 商户PID
	private final String PARTNER = "2088711408262791";
	// 商户收款账号
	private final String SELLER = "13911533774@163.com";
	// 商户私钥，pkcs8格式
	private final String RSA_PRIVATE = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAM083JmW+LUJYG90UVsEBaZH1TvIFZmE8odQBMltupc4kXAZQppBh4DMhoQ8BpJRibZMnR2lqB+OwEP6ZfdKGGRW41XbEF1mXCKFwNZXyDoArlOFb8P/OPw22WbQAbtlZJndM15PGMONReje149FMoOD7SCdfTslTSyvVisI8atnAgMBAAECgYEAxvV1NT9xoq6QWft80qq3f1ark+SHa+fB5QLhYROKiwY/l1glhLx5y5Z54L7/7+AzjBIBTbhFnzIXmM6pAm1F7mMJS1nxELHqytikxpyynHvmUJj976ZL2rrG3RsDaQXF/mrM1+Y6Ajps1hwt+uOtnBDJRnPM9mBD0wGAH9OKBCECQQDoDYEgxePw8LgmmseNhET00VHdE8CL6pcZ46ttivYYpR9mckRy2C9VlQiOYDGbk10H1chXih1erruOEDCd/bWNAkEA4mrxnS4tTk25LJak36pApSYlt1KdOuEtG+wDbIT4a78ffdEgnhP2gKTmBBVXPbHImspSFGO9OZQpYk/7Zv8lwwJAWXC7EJK1pKxjjh2iRJ1yppn3X6q5UDR/QO9Lp9EjwaQDUk1ArLM+q1HiFl5lQH2wIdD4gyUs5M2cZMlAs+SSEQJBAJeeIbmtqG0dIvk2z6VvLuboiq0eR2ecTka6XviWenw8eewY1IzGtXUj91uYptkLalgtT5WTzKz4CFZrVOB9z10CQFD3aoS4/4ya4H6/TvxL254wz+qtL6pxUc7mI5HWpd17lZFfB2aFwQ8UaNjDZfr5v5fzM6oFWF11JcoyBlLgGhM=";
	
	private final int SDK_PAY_FLAG = 1;

	private final int SDK_CHECK_FLAG = 2;
	
	/**
	 * 订单ID
	 */
	private String orderID = "";
	
	private IWXAPI api;
	
	//微信支付，预支付
	private WxPrePayOrder wxPrePayOrder;
	
	private WxCheckOrder wxCheckOrder = new WxCheckOrder();
	
	private TextView textViewAccount;
	
	private RecyclerView yubiRecycleList;
	
	private ChargeRecycleAdapter adapter;
	
	private List<Yubi> mYubi;
	
	/**
	 * 需要充值的娛币对象
	 */
	private Yubi chargeYubi;
	
	private Gson gson;
	
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SDK_PAY_FLAG: {
				PayResult payResult = new PayResult((String) msg.obj);

				String resultStatus = payResult.getResultStatus();
				// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
				if (TextUtils.equals(resultStatus, "9000")) {
					ToastUtil.show(ChargeActivity.this, 
							StringUtil.getXmlResource(ChargeActivity.this, R.string.activity_recharge_pay_success));
//					showEmotionsView();
					getUserInfoRequest();
					senAlipayOKRequest();
				} else {
					// 判断resultStatus 为非“9000”则代表可能支付失败
					// “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
					if(TextUtils.equals(resultStatus, "8000")) {
						Toast.makeText(ChargeActivity.this, "支付结果确认中",
								Toast.LENGTH_SHORT).show();

					} else {
						// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
						Toast.makeText(ChargeActivity.this, "支付失败",
								Toast.LENGTH_SHORT).show();

					}
				}
				break;
			}
			case SDK_CHECK_FLAG: {
				Toast.makeText(ChargeActivity.this, "检查结果为：" + msg.obj,
						Toast.LENGTH_SHORT).show();
				break;
			}
			case Config.WX_EXCEPTION_ERROR:
				Toast.makeText(ChargeActivity.this, "微信支付失败，支付异常",
						Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		};
	};
	
	BroadcastReceiver mMessageReceiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
		    int errorCode = intent.getIntExtra("errorCode", 1);
		    XLog.i("Got message: " + errorCode);
		    switch(errorCode)
		    {
		    case -2:
		    	//用户取消微信支付
		    	XLog.i("cancel");
		    	ToastUtil.show(ChargeActivity.this, StringUtil.getXmlResource(context, R.string.activity_recharge_pay_cancel));
		    	break;
		    case -1:
		    	XLog.i("error");
		    	ToastUtil.show(ChargeActivity.this, StringUtil.getXmlResource(context, R.string.activity_recharge_pay_exception));
		    	//错误，异常
		    	break;
		    case 0:
		    	//支付成功
		    	XLog.i("success");
				checkWxOrder();
		    	break;
		    case 1:
		    	//默认值
		    	XLog.i("default");
		    	break;
		    }
		}
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_charge);
		initView();
		// senAlipayOKRequest();
		api = WXAPIFactory.createWXAPI(this, null);
		api.registerApp(Config.APP_ID);
		LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
			      new IntentFilter("wxPayEvent"));
	}
	
	private void initView()
	{
		textViewAccount = (TextView) findViewById(R.id.textViewAccount);
		
		if (InfoCache.getInstance().getPersonalInfo() != null && InfoCache.getInstance().getPersonalInfo().getEntertainment_dollar() != null)
		{
			textViewAccount.setText(InfoCache.getInstance().getPersonalInfo().getEntertainment_dollar() + " 娱币");
		}
		
		findViewById(R.id.imageViewBack).setOnClickListener(this);
		
		findViewById(R.id.imageViewWx).setOnClickListener(this);
		findViewById(R.id.imageViewAli).setOnClickListener(this);
		findViewById(R.id.txtViewDetail).setOnClickListener(this);
		yubiRecycleList = (RecyclerView)findViewById(R.id.listViewCharge);
		
		mYubi = YubiCache.getInstance().GetYubiLists();
		
		adapter = new ChargeRecycleAdapter(this, mYubi);
        adapter.notifyDataSetChanged();
        adapter.setmOnItemClickListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        yubiRecycleList.setLayoutManager(linearLayoutManager);
        
        yubiRecycleList.setItemAnimator(new DefaultItemAnimator());//more的动画效果
        
        yubiRecycleList.setAdapter(adapter);
        
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
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
	}

	/**
	 * 创建预支付订单
	 */
	private void createWxPrePay(Yubi yubi)
	{
		wxPrePayOrder = new WxPrePayOrder();
		wxPrePayOrder.setClientID(Config.User.getClientID());
		wxPrePayOrder.setProductname(Config.BODY);
		wxPrePayOrder.setPrice(Integer.parseInt(yubi.getPrice()) * 100);
//		wxPrePayOrder.setPrice(1);
		wxPrePayOrder.setAmount(yubi.getAmount());
		
		String content = JsonUtil.toString(wxPrePayOrder);
		addToThreadPool(Config.wx_pre_pay, "create wx prePay", content);
	}
	
	/**
	 * 检查订单
	 */
	private void checkWxOrder()
	{
		String content = JsonUtil.toString(wxCheckOrder);
		addToThreadPool(Config.wx_pay, "create check order", content);
	}
	
	/**
	 * 充值成功
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
	 * 查询终端设备是否存在支付宝认证账户
	 */
	public void check(View v) {
		Runnable checkRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask payTask = new PayTask(ChargeActivity.this);
				// 调用查询接口，获取查询结果
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
	 * get the sdk version. 获取SDK版本号
	 */
	public void getSDKVersion() {
		PayTask payTask = new PayTask(this);
		String version = payTask.getVersion();
		Toast.makeText(this, version, Toast.LENGTH_SHORT).show();
	}

	/**
	 * create the order info. 创建订单信息
	 */
	public String getOrderInfo(String subject, String body, String price) {

		// 签约合作者身份ID
		String orderInfo = "partner=" + "\"" + PARTNER + "\"";

		// 签约卖家支付宝账号
		orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

		// 商户网站唯一订单号

		orderID = getOutTradeNo();
		orderInfo += "&out_trade_no=" + "\"" + orderID + "\"";

		// 商品名称
		orderInfo += "&subject=" + "\"" + subject + "\"";

		// 商品详情
		orderInfo += "&body=" + "\"" + body + "\"";

		// 商品金额
		orderInfo += "&total_fee=" + "\"" + price + "\"";

		// 服务器异步通知页面路径
		orderInfo += "&notify_url=" + "\""
				+ "http://120.25.107.200/php/alipay/notify_url.php" + "\"";

		// 服务接口名称， 固定值
		orderInfo += "&service=\"mobile.securitypay.pay\"";

		// 支付类型， 固定值
		orderInfo += "&payment_type=\"1\"";

		// 参数编码， 固定值
		orderInfo += "&_input_charset=\"utf-8\"";

		// 设置未付款交易的超时时间
		// 默认30分钟，一旦超时，该笔交易就会自动被关闭。
		// 取值范围：1m～15d。
		// m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
		// 该参数数值不接受小数点，如1.5h，可转换为90m。
		orderInfo += "&it_b_pay=\"30m\"";

		// extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
		// orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

		// 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
		orderInfo += "&return_url=\"m.alipay.com\"";

		// 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
		// orderInfo += "&paymethod=\"expressGateway\"";

		return orderInfo;
	}

	/**
	 * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
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
	 * sign the order info. 对订单信息进行签名
	 * 
	 * @param content
	 *            待签名订单信息
	 */
	public String sign(String content) {
		return SignUtils.sign(content, RSA_PRIVATE);
	}

	/**
	 * get the sign type we use. 获取签名方式
	 */
	public String getSignType() {
		return "sign_type=\"RSA\"";
	}

	/**
	 * call alipay sdk pay. 调用SDK支付
	 * 
	 */
	public void createAliPay(final Yubi yubi) {
		if (TextUtils.isEmpty(PARTNER) || TextUtils.isEmpty(RSA_PRIVATE)
				|| TextUtils.isEmpty(SELLER)) {
			new AlertDialog.Builder(this)
					.setTitle("警告")
					.setMessage("需要配置PARTNER | RSA_PRIVATE| SELLER")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(
										DialogInterface dialoginterface, int i) {
									//
									finish();
								}
							}).show();
			return;
		}
		// 订单
		String orderInfo = getOrderInfo("购买" + yubi.getAmount() + "个娱币",
				Config.User.getNickName() + "购买价值" + yubi.getPrice() + "的娱币",
				yubi.getPrice());

		// 对订单做RSA 签名
		String sign = sign(orderInfo);
		try {
			// 仅需对sign 做URL编码
			sign = URLEncoder.encode(sign, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		// 完整的符合支付宝参数规范的订单信息
		final String payInfo = orderInfo + "&sign=\"" + sign + "\"&"
				+ getSignType();

		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造PayTask 对象
				sendAlipayRequest(yubi);
				PayTask alipay = new PayTask(ChargeActivity.this);
				// 调用支付接口，获取支付结果
				String result = alipay.pay(payInfo);

				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};
		// 必须异步调用
		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}
	
	/**
	 * 充值
	 */
	private void sendAlipayRequest(Yubi yubi) {
		if (Config.User == null) {
			ToastUtil.show(this, this.getString(R.string.activity_recharge_get_user_fail));
			return;
		}
		HashMap<String, String> entity = new HashMap<String, String>();

		entity.put("clientID", Config.User.getClientID());
		entity.put("amount", "" + yubi.getAmount());
		entity.put("price", yubi.getPrice());
		entity.put("order_sn", orderID);
		ShowProgressDialog(this.getString(R.string.activity_recharge_get_charging));
		List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
		addToThreadPool(Config.top_up, "send alipay request", params);
	}
	
	/**
	 * 正式开始微信支付
	 * @param jsonString
	 */
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
    
    /**
     * 支付完毕，获取用户信息
     */
    private void getUserInfoRequest()
    {
    	if (Config.User == null)
    	{
			ToastUtil.show(this, StringUtil.getXmlResource(this, R.string.mainactivity_login_invalidate));
			finish();
			return;
    	}
    	HashMap<String, String> entity = new HashMap<String, String>();
    	entity.put("clientID", Config.User.getClientID());
    	List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
    	ShowProgressDialog(this.getString(R.string.mainactivity_get_user_info));
    	addToThreadPool(Config.personal_information, "get user info", params);
    }
    
	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
		/**
		 * 微信支付
		 */
		case R.id.imageViewWx:
			if (chargeYubi != null)
			{
				createWxPrePay(chargeYubi);
			}
			else
			{
				ToastUtil.show(ChargeActivity.this, "请先点击您需要充值的数量");
			}
			break;
		/**
		 * 支付宝支付
		 */
		case R.id.imageViewAli:
			if (chargeYubi != null)
			{
				createAliPay(chargeYubi);
			}
			else
			{
				ToastUtil.show(ChargeActivity.this, "请先点击您需要充值的数量");
			}
			break;
			
		case R.id.txtViewDetail:
			Intent intent = new Intent(this, TransactionDetailActivity.class);
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
		gson = new Gson();
		switch(taskType)
		{
		case Config.wx_pre_pay:
			//接受到微信预支付订单信息
			ToastUtil.show(ChargeActivity.this, "正常调起支付");
			startWxPayThread(jsonString);
			break;
		case Config.wx_pay:
			//接受到微信支付完成订单信息
			ToastUtil.show(ChargeActivity.this, "支付完成，充值成功");
			getUserInfoRequest();
			break;
			
		case Config.personal_information:
			Entity<EditPersonal> baseEntity = gson.fromJson(jsonString,
					new TypeToken<Entity<EditPersonal>>() {
					}.getType());
			if (baseEntity.getData() != null)
			{
				InfoCache.getInstance().setPersonalInfo(baseEntity.getData());
				if (InfoCache.getInstance().getPersonalInfo() != null) {
					textViewAccount.setText(InfoCache.getInstance().getPersonalInfo().getEntertainment_dollar() + " 娱币");
				}
			}
		}
	}

	@Override
	public void onItemClick(View view, int position) {
		try {
			chargeYubi = (Yubi) view.getTag();
			ToastUtil.show(ChargeActivity.this, "您选择了充值： " + chargeYubi.getAmount() + " 个娛币，请选择支付方式。");
		} catch (Exception e) {
			e.printStackTrace();
			XLog.e("get tag exception on item click: ");
		}
	}

	@Override
	public void onItemLongClick(View view, int position) {
		
	}

}
