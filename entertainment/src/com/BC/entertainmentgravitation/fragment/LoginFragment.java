package com.BC.entertainmentgravitation.fragment;

import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import com.BC.entertainment.config.Preferences;
import com.BC.entertainmentgravitation.HomeActivity_back;
import com.BC.entertainmentgravitation.R;
import com.BC.entertainmentgravitation.fragment.RegisteFragment.iRegister;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.netease.nim.uikit.cache.DataCacheManager;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;
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

public class LoginFragment extends BaseFragment implements OnClickListener{

	private View rootView;
	private Gson gson;
	private EditText editName;
	private EditText editPassword;
	private Button btnLogin;

	private CheckBox chBoxRememberPassword;
	
	private AbortableFuture<LoginInfo> loginRequest;
	private iLogin iLoginInterface;
	
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
		super.onCreate(savedInstanceState);
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
			ToastUtil.show(getActivity(), "此功能正在完善中，尽情期待...");
			break;
		/**
		 * 第三方登录 QQ
		 */
		case R.id.btnQq:
			ToastUtil.show(getActivity(), "此功能正在完善中，尽情期待...");
			break;
		/**
		 * 第三方登录 微博
		 */
		case R.id.btnWb:
			ToastUtil.show(getActivity(), "此功能正在完善中，尽情期待...");
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
		}
	}

}