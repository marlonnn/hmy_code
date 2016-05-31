package com.BC.entertainmentgravitation.fragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;

import com.BC.entertainment.config.AccessTokenKeeper;
import com.BC.entertainment.config.AuthConstants;
//import com.BC.entertainment.config.Constants;
import com.BC.entertainment.config.Preferences;
import com.BC.entertainmentgravitation.HomeActivity_back;
import com.BC.entertainmentgravitation.R;
import com.BC.entertainmentgravitation.entity.WBUser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.netease.nim.uikit.cache.DataCacheManager;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.summer.config.Config;
import com.summer.entity.User;
import com.summer.factory.ThreadPoolFactory;
import com.summer.fragment.BaseFragment;
import com.summer.handler.InfoHandler;
import com.summer.json.Entity;
import com.summer.logger.XLog;
import com.summer.task.HttpBaseTask;
import com.summer.treadpool.ThreadPoolConst;
import com.summer.utils.JsonUtil;
import com.summer.utils.ToastUtil;
import com.summer.utils.UrlUtil;
import com.summer.utils.ValidateUtil;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

public class LoginFragment extends BaseFragment implements OnClickListener{

	private View rootView;
	private Gson gson;
	private EditText editName;
	private EditText editPassword;
	private Button btnLogin;

	private CheckBox chBoxRememberPassword;
	
	private AbortableFuture<LoginInfo> loginRequest;
	private iLogin iLoginInterface;
	private UMShareAPI mShareAPI = null;
	private String accessToken = "";
	
    /** 注意：SsoHandler 仅当 SDK 支持 SSO 时有效 */
    private SsoHandler mSsoHandler;
    private AuthInfo mAuthInfo;
    /** 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能  */
    private Oauth2AccessToken mAccessToken;
    /** 用户信息接口 */
    private UsersAPI mUsersAPI;
    
    /**
     * 腾讯第三方登录
     */
	private Tencent mTencent;
    private UserInfo mInfo;
	private BaseUiListener listener;
    
	public interface iLogin
	{
		void isForgetPassword(boolean isForget);
	}
	
	@Override
	public void onAttach(Activity activity) {
		try {
			iLoginInterface = (iLogin)activity;
			
		} catch (Exception e) {
			e.printStackTrace();
			XLog.e("get switch camera exception");
		}
		
		super.onAttach(activity);
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		gson = new Gson();
        /** init auth api**/
        mShareAPI = UMShareAPI.get(getActivity());
        
        // 创建微博实例
        //mWeiboAuth = new WeiboAuth(this, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
        // 快速授权时，请不要传入 SCOPE，否则可能会授权不成功
        mAuthInfo = new AuthInfo(getActivity(), AuthConstants.APP_KEY_WB, AuthConstants.REDIRECT_URL_WB, AuthConstants.SCOPE_WB);
        mSsoHandler = new SsoHandler(getActivity(), mAuthInfo);
        registerQQConfig();
		super.onCreate(savedInstanceState);
	}
	
	private void registerQQConfig()
	{
		mTencent = Tencent.createInstance(AuthConstants.APP_ID_QQ, getActivity().getApplicationContext());
		listener = new BaseUiListener();
	}
	
	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	@SuppressLint("InflateParams") @Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_login, null);
		return rootView;
	}
	
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initView();
	}
	
	private void initView()
	{
		editName = (EditText) rootView.findViewById(R.id.eTextName);
		editPassword = (EditText) rootView.findViewById(R.id.eTextPassword);
		btnLogin = (Button) rootView.findViewById(R.id.btnLogin);
		chBoxRememberPassword = (CheckBox) rootView.findViewById(R.id.cBoxRemeber);
		rootView.findViewById(R.id.txtViewNotRegist).setOnClickListener(this);
		rootView.findViewById(R.id.txtViewFindpassword).setOnClickListener(this);
		rootView.findViewById(R.id.btnWx).setOnClickListener(this);
		rootView.findViewById(R.id.btnWb).setOnClickListener(this);
		rootView.findViewById(R.id.btnQq).setOnClickListener(this);
		btnLogin.setOnClickListener(this);
		
		chBoxRememberPassword.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				
			}
		});
		
		if (Config.getPhoneNum() != null) {
			editName.setText(Config.getPhoneNum());
		}
		if (Config.getPassword() != null) {
			editPassword.setText(Config.getPassword());
		}
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
		/**
		 * 登陆
		 */
		case R.id.btnLogin:
			if (isValidate())
			{
				logingToServer();
			}
			
			break;
		/**
		 * 第三方登录 微信
		 */
		case R.id.btnWx:
			onClickAuth(v);
//			onClickInfo(v);
//			ToastUtil.show(getActivity(), "此功能正在完善中，尽情期待...");
			break;
		/**
		 * 第三方登录 QQ
		 */
		case R.id.btnQq:
			tencentLogin();
			break;
		/**
		 * 第三方登录 微博
		 */
		case R.id.btnWb:
	        // 从 SharedPreferences 中读取上次已保存好 AccessToken 等信息，
	        // 第一次启动本应用，AccessToken 不可用
	        mAccessToken = AccessTokenKeeper.readAccessToken(getActivity());
	        if (mAccessToken.isSessionValid()) {
	        	//已授权
	            mUsersAPI = new UsersAPI(getActivity(), AuthConstants.APP_KEY_WB, mAccessToken);
	            long uid = Long.parseLong(mAccessToken.getUid());
	            mUsersAPI.show(uid, mListener);
	        }
	        else
	        {
	        	mSsoHandler.authorize(new AuthListener());
	        }
			break;
		/**
		 * 没有账号，请注册
		 */
		case R.id.txtViewNotRegist:
			if (iLoginInterface != null)
			{
				iLoginInterface.isForgetPassword(false);
			}
			break;
		/**
		 * 找回密码
		 */
		case R.id.txtViewFindpassword:
			if (iLoginInterface != null)
			{
				iLoginInterface.isForgetPassword(true);
			}
			break;
		}
	}
	
	private boolean isValidate()
	{
		try {
			if (ValidateUtil.isEmpty(editName, "用户名") || ValidateUtil.isEmpty(editPassword, "密码"))
			{
				return false;
			}
			else
			{
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
    private void onClickAuth(View view) {
        SHARE_MEDIA platform = null;
        if (view.getId() == R.id.btnWb){
            platform = SHARE_MEDIA.SINA;
        }else if (view.getId() == R.id.btnQq){
            platform = SHARE_MEDIA.QQ;
        }else if (view.getId() == R.id.btnWx){
            platform = SHARE_MEDIA.WEIXIN;
        }
        /**begin invoke umeng api**/

        mShareAPI.doOauthVerify(getActivity(), platform, umAuthListener);
    }
    
    /** auth callback interface**/
    private UMAuthListener umAuthListener = new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            Toast.makeText(getActivity(), "Authorize succeed", Toast.LENGTH_SHORT).show();
            if (mShareAPI.isAuthorize(getActivity(), platform))
            {
                mShareAPI.getPlatformInfo(getActivity(), platform, umInfoListener);
            }

            if (data!=null){
                XLog.i(data.toString());
                Toast.makeText(getActivity(), data.toString(), Toast.LENGTH_SHORT).show();
                try {
					accessToken = data.get("access_token");
				} catch (Exception e) {
					e.printStackTrace();
				}
                for(String key : data.keySet())
                {
                	XLog.i("key: " + key);
                	XLog.i("value: " + data.get(key));
                }
            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            Toast.makeText( getActivity(), "Authorize fail", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            Toast.makeText( getActivity(), "Authorize cancel", Toast.LENGTH_SHORT).show();
        }
    };
    
    private UMAuthListener umInfoListener = new UMAuthListener(){

		@Override
		public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            Toast.makeText(getActivity(), "get info succeed", Toast.LENGTH_SHORT).show();
            if (data!=null){
                XLog.i("getting data");
                String d = data.toString();
                XLog.i("user info: " + d);
                if (data!=null){
                    XLog.i("getting data");
                    XLog.i(data.toString());
                    Toast.makeText(getActivity(), data.toString(), Toast.LENGTH_SHORT).show();
                    for(String key : data.keySet())
                    {
                    	XLog.i("11key: " + key);
                    	XLog.i("11value: " + data.get(key));
                    }
                }
                sendThirdRequest(platform, data);
                Toast.makeText(getActivity(), data.toString(), Toast.LENGTH_SHORT).show();
            }
		}

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            Toast.makeText( getActivity(), "Authorize fail", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            Toast.makeText( getActivity(), "Authorize cancel", Toast.LENGTH_SHORT).show();
        }
    	
    };
	
    /**
     * Login to server
     */
    private void logingToServer()
    {
		final String name = editName.getText().toString();
		final String psw = editPassword.getText().toString();
		List<NameValuePair> params = getLogingParams(name, psw, Config.POS + "");
		ShowProgressDialog(getResources().getString(R.string.loginIsLogining));
		addToThreadPool(Config.LOGIN_TYPE, "loginTask", params);
    }
    
    /**
     * 第三方登录
     */
    private void sendThirdRequest(SHARE_MEDIA platform, Map<String, String> data)
    {
    	if (data != null)
    	{
        	HashMap<String, String> entity = new HashMap<String, String>();
        	if (platform == SHARE_MEDIA.WEIXIN)
        	{
        		entity.put("uid", data.get("unionid"));
        		entity.put("userName", data.get("nickname"));
        		entity.put("accessToken", accessToken);
        		entity.put("iconURL", data.get("headimgurl"));
        		entity.put("type", "0");
        		
        	}
        	else if (platform == SHARE_MEDIA.QQ)
        	{
        		entity.put("uid", data.get("openid"));
        		entity.put("userName", data.get("screen_name"));
        		entity.put("accessToken", accessToken);
        		entity.put("iconURL", data.get("profile_image_url"));
        		entity.put("type", "1");
        	}
        	else if (platform == SHARE_MEDIA.SINA)
        	{
        		entity.put("type", "2");
        	}
    		List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
    		ShowProgressDialog("正在注册，请稍等...");
    		addToThreadPool(Config.third_regist, "thid regist Task", params);
    	}
    }
    
    /**
     * 微博第三方登录
     */
    private void sendWeiboRequest(WBUser user)
    {
    	if (user != null)
    	{
        	HashMap<String, String> entity = new HashMap<String, String>();
    		entity.put("uid", user.id);
    		entity.put("userName", user.screen_name);
    		entity.put("accessToken", mAccessToken.getToken());
    		entity.put("iconURL", user.profile_image_url);
        	entity.put("type", "2");
    		List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
    		ShowProgressDialog("正在注册，请稍等...");
    		addToThreadPool(Config.third_regist, "thid regist Task", params);
    	}
    }
    
    /**
     * 腾讯第三方登录
     */
    private void sendQQRequest(JSONObject json)
    {
    	if (json != null)
    	{
        	try {
				HashMap<String, String> entity = new HashMap<String, String>();
				if (json != null)
				{
					json.getString("nickname");
					entity.put("uid", json.getString("openid"));
					entity.put("userName", json.getString("screen_name"));
					entity.put("accessToken", mTencent.getAccessToken());
					entity.put("iconURL", json.getString("profile_image_url"));
					entity.put("type", "1");
					XLog.i("nickname: " + json.getString("nickname"));
				}
				entity.put("type", "2");
				List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
				ShowProgressDialog("正在注册，请稍等...");
				addToThreadPool(Config.third_regist, "thid regist Task", params);
			} catch (JSONException e) {
				e.printStackTrace();
			}
    	}
    }
    
    private void addToThreadPool(int taskType, String Tag, List<NameValuePair> params)
    {
    	HttpBaseTask httpTask = new HttpBaseTask(ThreadPoolConst.THREAD_TYPE_FILE_HTTP, Tag, params, UrlUtil.GetUrl(taskType));
    	httpTask.setTaskType(taskType);
    	InfoHandler handler = new InfoHandler(this);
    	httpTask.setInfoHandler(handler);
    	ThreadPoolFactory.getThreadPoolManager().addTask(httpTask);
    }
    
    private List<NameValuePair> getLogingParams(String name, String passWord, String pos)
    {
    	HashMap<String, String> entity = new HashMap<String, String>();
		entity.put("pos", pos);
		entity.put("name", name);
		entity.put("passWord", passWord);
		return JsonUtil.requestForNameValuePair(entity);
    }
    
    @SuppressWarnings("unchecked")
	private void logingNimServer(User user)
    {
    	final String account = user.getUserName();
    	final String token = user.getToken();
    	if (account != null && token != null)
    	{
        	loginRequest = NIMClient.getService(AuthService.class).login(new LoginInfo(account, token));
        	loginRequest.setCallback(new RequestCallback<LoginInfo>() {
				@Override
				public void onException(Throwable arg0) {
					XLog.i("login to Nim server exception: " + arg0);
				}

				@Override
				public void onFailed(int arg0) {
					XLog.i("login to Nim server failed: " + arg0);
	                if (arg0 == 302 || arg0 == 404) {
	                    Toast.makeText(getActivity(), "登陆失败", Toast.LENGTH_SHORT).show();
	                } else {
	                    Toast.makeText(getActivity(), "登录失败: " + arg0, Toast.LENGTH_SHORT).show();
	                }
				}

				@Override
				public void onSuccess(LoginInfo loginInfo) {
					XLog.i("login to Nim server seccuss: ");
		    		if (chBoxRememberPassword.isChecked()) {
		    			Config.setPhoneNum(editName.getText().toString());
		    			Config.setPassword(editPassword.getText().toString());
		    			Config.saveConfig();
		    		}
		    		
					XLog.i("loginInfo: " + loginInfo.getAccount());
					saveLoginInfo(account, token);
	                DataCacheManager.buildDataCacheAsync();
	        		saveNimAccount(Config.User);
	    			Intent intent = new Intent(getActivity(), HomeActivity_back.class);
	    			startActivity(intent);
	        		ToastUtil.show(getActivity(), getActivity().getString(R.string.loginSuccess));
				}
			});
    	}
    	else
    	{
    		Toast.makeText(getActivity(), "登陆失败", Toast.LENGTH_SHORT).show();
    	}
    }
    
    private void saveLoginInfo(final String account, final String token) {
        Preferences.saveUserAccount(account);
        Preferences.saveUserToken(token);
    }
    
	private void saveNimAccount(User user)
	{
		if (user != null)
		{
			if (user.getUserName() != null)
			{
				Preferences.saveUserAccount(user.getUserName());
			}
			if (user.getToken() != null)
			{
				Preferences.saveUserToken(user.getToken());
			}
		}
	}

	@Override
	public void RequestSuccessful(String jsonString, int taskType) {
		switch(taskType)
		{
    	case Config.LOGIN_TYPE:
    		Entity<User> entity = gson.fromJson(jsonString, 
    				new TypeToken<Entity<User>>() {}.getType());
    		Config.User = entity.getData();
			logingNimServer(Config.User);

    		break;
    	case Config.third_regist:
    		Entity<User> entity1 = gson.fromJson(jsonString, 
    				new TypeToken<Entity<User>>() {}.getType());
    		Config.User = entity1.getData();
			logingNimServer(Config.User);
    		break;
		}
	}
	
    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        XLog.d("on activity re 2");
        mShareAPI.onActivityResult(requestCode, resultCode, data);
        XLog.d("on activity re 3");
        
        // SSO 授权回调
        // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResults
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
        
	    if (requestCode == Constants.REQUEST_LOGIN ||
		    	requestCode == Constants.REQUEST_APPBAR) {
		    	Tencent.onActivityResultData(requestCode,resultCode,data,loginListener);
		}
    }

    /**
     * 微博认证授权回调类。
     * 1. SSO 授权时，需要在 {@link #onActivityResult} 中调用 {@link SsoHandler#authorizeCallBack} 后，
     *    该回调才会被执行。
     * 2. 非 SSO 授权时，当授权结束后，该回调就会被执行。
     * 当授权成功后，请保存该 access_token、expires_in、uid 等信息到 SharedPreferences 中。
     */
    class AuthListener implements WeiboAuthListener {
        
        @Override
        public void onComplete(Bundle values) {
            // 从 Bundle 中解析 Token
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            if (mAccessToken.isSessionValid()) {
                // 保存 Token 到 SharedPreferences
                AccessTokenKeeper.writeAccessToken(getActivity(), mAccessToken);
                mUsersAPI = new UsersAPI(getActivity(), AuthConstants.APP_KEY_WB, mAccessToken);
                long uid = Long.parseLong(mAccessToken.getUid());
                mUsersAPI.show(uid, mListener);
                Toast.makeText(getActivity(), "授权成功", Toast.LENGTH_SHORT).show();
            } else {
                // 以下几种情况，您会收到 Code：
                // 1. 当您未在平台上注册的应用程序的包名与签名时；
                // 2. 当您注册的应用程序包名与签名不正确时；
                // 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
                String code = values.getString("code");
                String message = "授权失败";
                if (!TextUtils.isEmpty(code)) {
                    message = message + "\nObtained the code: " + code;
                }
                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onCancel() {
            Toast.makeText(getActivity(), "取消授权", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(getActivity(), 
                    "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    /**
     * 微博 OpenAPI 回调接口。
     */
    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                // 调用 User#parse 将JSON串解析成User对象
                WBUser user = WBUser.parse(response);
                if (user != null) {
                	sendWeiboRequest(user);
                    Toast.makeText(getActivity(), 
                            "获取User信息成功，用户昵称：" + user.screen_name, 
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            XLog.e(e.getMessage());
//            ErrorInfo info = ErrorInfo.parse(e.getMessage());
//            Toast.makeText(WBUserAPIActivity.this, info.toString(), Toast.LENGTH_LONG).show();
        }
    };
    
    private void tencentLogin()
    {
	    mTencent = Tencent.createInstance(AuthConstants.APP_ID_QQ, getActivity().getApplicationContext());
	    
	    if (!mTencent.isSessionValid())
	    {
	        mTencent.login(getActivity(), AuthConstants.SCOPE_QQ, listener);
	    }
	    else
	    {
	    	mTencent.logout(getActivity());
	    	mTencent.login(getActivity(), AuthConstants.SCOPE_QQ, listener);
			updateUserInfo();
	    }
    }
    
	private void updateUserInfo() {
		if (mTencent != null && mTencent.isSessionValid()) {
			IUiListener listener = new IUiListener() {

				@Override
				public void onError(UiError e) {

				}

				@Override
				public void onComplete(final Object response) {
					JSONObject json = (JSONObject)response;
					sendQQRequest(json);
				}

				@Override
				public void onCancel() {

				}
			};
			mInfo = new UserInfo(getActivity(), mTencent.getQQToken());
			mInfo.getUserInfo(listener);

		} 
	}
	
	public void initOpenidAndToken(JSONObject jsonObject) {
        try {
            String token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
            String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
            String openId = jsonObject.getString(Constants.PARAM_OPEN_ID);
            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires)
                    && !TextUtils.isEmpty(openId)) {
                mTencent.setAccessToken(token, expires);
                mTencent.setOpenId(openId);
            }
        } catch(Exception e) {
        }
    }

	IUiListener loginListener = new BaseUiListener() {
        @Override
        protected void doComplete(JSONObject values) {
        	Log.d("SDKQQAgentPref", "AuthorSwitch_SDK:" + SystemClock.elapsedRealtime());
            initOpenidAndToken(values);
            updateUserInfo();
        }
    };
    
    private class BaseUiListener implements IUiListener {
    	@Override
    	public void onComplete(Object response) {
    	     //V2.0版本，参数类型由JSONObject 改成了Object,具体类型参考api文档
//    		doComplete(response);
/*    		{
    			"ret":0,
    			"pay_token":"xxxxxxxxxxxxxxxx",
    			"pf":"openmobile_android",
    			"expires_in":"7776000",
    			"openid":"xxxxxxxxxxxxxxxxxxx",
    			"pfkey":"xxxxxxxxxxxxxxxxxxx",
    			"msg":"sucess",
    			"access_token":"xxxxxxxxxxxxxxxxxxxxx"
    		}*/
    		
            if (null == response) {
                ToastUtil.show(getActivity(), "返回为空登录失败");
                return;
            }
            JSONObject jsonResponse = (JSONObject) response;
            if (null != jsonResponse && jsonResponse.length() == 0) {
            	ToastUtil.show(getActivity(), "返回为空, 登录失败");
                return;
            }
			ToastUtil.show(getActivity(), response.toString() + "登录成功");
			doComplete((JSONObject)response);
    	}
    	
    	protected void doComplete(JSONObject values) {

    	}
    	
    	@Override
    	public void onError(UiError e) {
	    	XLog.i("code:" + e.errorCode + ", msg:"
	    	+ e.errorMessage + ", detail:" + e.errorDetail);
    	}
    	@Override
    	public void onCancel() {
    		ToastUtil.show(getActivity(), "Login cancel");
    	}
    }
}
