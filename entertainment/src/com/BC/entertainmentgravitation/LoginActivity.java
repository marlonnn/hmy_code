package com.BC.entertainmentgravitation;


import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.BC.entertainment.config.Preferences;
import com.BC.entertainmentgravitation.dialog.PromptDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.netease.nim.uikit.cache.DataCacheManager;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.summer.activity.BaseActivity;
import com.summer.config.Config;
import com.summer.entity.User;
import com.summer.factory.ThreadPoolFactory;
import com.summer.handler.InfoHandler;
import com.summer.handler.InfoHandler.InfoReceiver;
import com.summer.json.Entity;
import com.summer.logger.XLog;
import com.summer.task.HttpBaseTask;
import com.summer.treadpool.ThreadPoolConst;
import com.summer.utils.JsonUtil;
import com.summer.utils.ToastUtil;
import com.summer.utils.UrlUtil;
import com.summer.utils.ValidateUtil;

import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

/**
 * 登陆界面
 * Created by zhongwen on 2016/4/3.
 */
@SuppressLint("CutPasteId") public class LoginActivity extends BaseActivity implements InfoReceiver{

    private CheckBox chBoxRememberPassword;//记住密码
    private Button btnLogin;//登陆
    private Button btnSignUp;//注册
    private Button btnForgetPassword;//忘记密码
    private EditText loginPhoneNumber;//登陆界面手机号
    private EditText loginPassword;//登陆密码
    private View loginView, signUpView;

    private EditText signUpPhoneNumber;//注册界面手机号
    private EditText signUpValidate;//注册界面验证码
    private EditText signUpPassword;//注册界面密码
    private EditText signUpPasswordAgain;//注册界面再次输入密码
    private EditText signUpShareCode;
    private TextView signUpValidatetext;
    private CheckBox signUpcheckBox;
	private Button verifyButton;
	private Button signUpButton;
	
	private String verifyString;
    
	private boolean isSignUp;
	private View view2;
	private Button cancelButton;
	private TextView readProtocolText;
	
	private AbortableFuture<LoginInfo> loginRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initializeLoginView();
        initializeSignUpView();
//        initialView(true);
        signUpView.setVisibility(View.GONE);
        
    }

    /**
     * 初始化登录界面控件
     */
    private void initializeLoginView()
    {
        chBoxRememberPassword = (CheckBox) findViewById(R.id.checkBox_remember_password);
        btnLogin = (Button) findViewById(R.id.button_login);
        btnSignUp = (Button) findViewById(R.id.button_signUp);
        btnForgetPassword = (Button) findViewById(R.id.button_forget_password);
        loginPhoneNumber = (EditText) findViewById(R.id.login_editText_phone);
        loginPassword = (EditText) findViewById(R.id.login_editText_passWord);
        loginView = findViewById(R.id.view_login);
        signUpView = findViewById(R.id.view_signUp);
        
        view2 = findViewById(R.id.view2);
        
		if (Config.getPhoneNum() != null) {
			loginPhoneNumber.setText(Config.getPhoneNum());
		}
		if (Config.getPassword() != null) {
			loginPassword.setText(Config.getPassword());
		}
		
		loginPhoneNumber.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				XLog.i("CharSequence: " + s);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		//login
        btnLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (checkData()) {
					ShowProgressDialog(getResources().getString(R.string.loginIsLogining));
					logingToServer();
				}
			}
		});
        //sign up
        btnSignUp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				isSignUp = true;
				signUpShareCode.setVisibility(View.VISIBLE);
				clearAll();
				initialView(false);
			}
		});
        btnForgetPassword.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				forgetPassword();
			}
		});
    }
    
	private void clearAll() {
		signUpPhoneNumber.setText("");
		signUpValidate.setText("");
		signUpPassword.setText("");
		signUpPasswordAgain.setText("");
	}

	public void forgetPassword()
	{
		isSignUp = false;
		signUpShareCode.setVisibility(View.GONE);
		clearAll();
		initialView(false);
		view2.setVisibility(View.GONE);
	}
    /**
     * 初始化注册界面空间
     */
    private void initializeSignUpView() {
        signUpPhoneNumber = (EditText) findViewById(R.id.signUp_editText_phone);
        signUpValidate = (EditText) findViewById(R.id.signUp_editText_validate);
        signUpPassword = (EditText) findViewById(R.id.signUp_editText_phone);

        signUpPasswordAgain = (EditText) findViewById(R.id.signUp_editText_passWord_againt);
        signUpShareCode = (EditText) findViewById(R.id.signUp_editText_shareCode);
        signUpValidatetext = (TextView) findViewById(R.id.vtext);
        signUpValidatetext.setVisibility(View.GONE);
        signUpcheckBox = (CheckBox) findViewById(R.id.checkBox1);
        verifyButton = (Button) findViewById(R.id.verifyButton);
        signUpButton = (Button) findViewById(R.id.button_confirm);
        cancelButton = (Button) findViewById(R.id.no);
        readProtocolText = (TextView) findViewById(R.id.textView3);
        
        readProtocolText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showUserProtocol();
			}
		});
        
        verifyButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				sendValidateRequest();
			}
		});
        
        signUpButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (isSignUp) {
					sendSignUpRequest();
				} else {
					sendForgetPasswordRequest();
				}
			}
		});
        
        cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				initialView(true);
			}
		});
    }
    
    
    /**
     * 获取验证码
     */
    private void sendValidateRequest()
    {
		if (signUpPhoneNumber.getText().toString().equals("")
				|| !ValidateUtil.isMobileNumber(signUpPhoneNumber)) {
			ToastUtil.show(this, this.getString(R.string.signUpPhone));
			return;
		}
		List<NameValuePair> params = getValidateParams();
		ShowProgressDialog(this.getString(R.string.signUpValidate));
		addToThreadPool(Config.VERIFY, "singUp validate", params);
    }
    
    /**
     * 注册
     */
    private void sendSignUpRequest()
    {
		if (!signUpcheckBox.isChecked()) {
			ToastUtil.show(this, this.getString(R.string.signUpUserProtocol));
			return;
		}
		if (ValidateUtil.isEmpty(signUpPhoneNumber, this.getString(R.string.signUpPhoneNumber))
				|| ValidateUtil.isEmpty(signUpPassword, this.getString(R.string.loginPassword))
				|| ValidateUtil.isEmpty(signUpPasswordAgain, this.getString(R.string.loginPassword))
				|| ValidateUtil.isEmpty(signUpValidate, this.getString(R.string.signUpValidateCode))) {
			return;
		}
		if (!signUpPassword.getText().toString()
				.contains(signUpPasswordAgain.getText().toString())) {
			ToastUtil.show(this, this.getString(R.string.signUpPasswordDifferent));
			return;
		}
		if (!signUpValidate.getText().toString().contains(verifyString)) {
			ToastUtil.show(this, this.getString(R.string.signUpValidateError));
			return;
		}
		List<NameValuePair> params = getSignUpParams();
		ShowProgressDialog(this.getString(R.string.signUping));
		addToThreadPool(Config.REGISTER, "singUp", params);
    }
    
    private void sendForgetPasswordRequest()
    {
		if (ValidateUtil.isEmpty(signUpPhoneNumber, this.getString(R.string.signUpPhoneNumber))
				|| ValidateUtil.isEmpty(signUpPassword, this.getString(R.string.loginPassword))
				|| ValidateUtil.isEmpty(signUpPasswordAgain, this.getString(R.string.loginPassword))
				|| ValidateUtil.isEmpty(signUpValidate, this.getString(R.string.signUpValidateCode))) {
			return;
		}
		if (!signUpPassword.getText().toString()
				.equals(signUpPasswordAgain.getText().toString())) {
			ToastUtil.show(this, this.getString(R.string.signUpPasswordDifferent));
			return;
		}
		if (!signUpValidate.getText().toString().equals(verifyString)) {
			ToastUtil.show(this, this.getString(R.string.signUpValidateError));
			return;
		}
		List<NameValuePair> params = getForgetPasswordParams();
		ShowProgressDialog(this.getString(R.string.signUpForgetPassword));
		addToThreadPool(Config.forget_password, "forget password", params);
    }
    /**
     * check user name and password
     * @return
     */
    private boolean checkData()
    {
		boolean checked = false;
		
		checked = (!ValidateUtil.isEmpty(loginPhoneNumber, this.getString(R.string.loginName)) && !ValidateUtil
				.isEmpty(loginPassword, this.getString(R.string.loginPassword)));
		return checked;
    }
    
    /**
     * Login to server
     */
    private void logingToServer()
    {
		final String name = loginPhoneNumber.getText().toString();
//		Config.setPhoneNum(name);
//		Config.saveConfig();
		final String psw = loginPassword.getText().toString();
		List<NameValuePair> params = getLogingParams(name, psw, Config.POS + "");
		addToThreadPool(Config.LOGIN_TYPE, "loginTask", params);
    }
    
    @SuppressWarnings("unchecked")
	private void logingNimServer()
    {
    	final String account = Preferences.getUserAccount();
    	final String token = Preferences.getUserToken();
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
	                    Toast.makeText(LoginActivity.this, "登陆失败", Toast.LENGTH_SHORT).show();
	                } else {
	                    Toast.makeText(LoginActivity.this, "登录失败: " + arg0, Toast.LENGTH_SHORT).show();
	                }
				}

				@Override
				public void onSuccess(LoginInfo loginInfo) {
					XLog.i("login to Nim server seccuss: ");
					XLog.i("loginInfo: " + loginInfo.getAccount());
					saveLoginInfo(account, token);
	                DataCacheManager.buildDataCacheAsync();
	        		saveNimAccount(Config.User);
	    			Intent intent = new Intent(LoginActivity.this, MainActivity.class);
	    			startActivity(intent);
				}
			});
    	}
    }
    
    private void saveLoginInfo(final String account, final String token) {
        Preferences.saveUserAccount(account);
        Preferences.saveUserToken(token);
    }
    
    private List<NameValuePair> getLogingParams(String name, String passWord, String pos)
    {
    	HashMap<String, String> entity = new HashMap<String, String>();
		entity.put("pos", pos);
		entity.put("name", name);
		entity.put("passWord", passWord);
		return JsonUtil.requestForNameValuePair(entity);
    }
    
    private List<NameValuePair> getValidateParams()
    {
    	HashMap<String, String> entity = new HashMap<String, String>();
		entity.put("phone", signUpPhoneNumber.getText().toString());
		return JsonUtil.requestForNameValuePair(entity);
    }
    
    private List<NameValuePair> getSignUpParams()
    {
    	HashMap<String, String> entity = new HashMap<String, String>();
		entity.put("phone", signUpPhoneNumber.getText().toString());
		entity.put("passWord", signUpPassword.getText().toString());
		entity.put("verify", signUpValidate.getText().toString());
		entity.put("shareCode", signUpShareCode.getText().toString());
		return JsonUtil.requestForNameValuePair(entity);
    }
    
    private List<NameValuePair> getForgetPasswordParams()
    {
    	HashMap<String, String> entity = new HashMap<String, String>();
		entity.put("phone", signUpPhoneNumber.getText().toString());
		entity.put("passWord", signUpPassword.getText().toString());
		entity.put("verify", signUpValidate.getText().toString());
		return JsonUtil.requestForNameValuePair(entity);
    }
    
    private void addToThreadPool(int taskType, String Tag, List<NameValuePair> params)
    {
    	HttpBaseTask httpTask = new HttpBaseTask(ThreadPoolConst.THREAD_TYPE_FILE_HTTP, Tag, params, UrlUtil.GetUrl(taskType));
    	httpTask.setTaskType(taskType);
    	InfoHandler handler = new InfoHandler(this);
    	httpTask.setInfoHandler(handler);
    	ThreadPoolFactory.getThreadPoolManager().addTask(httpTask);
    }
    
    
    
    @Override
	protected void onDestroy() {
		super.onDestroy();
		stopAllThreadPool();
	}

	private void stopAllThreadPool()
    {
    	ThreadPoolFactory.getThreadPoolManager().stopAllTask();
    }
    
    @Override
    public void onInfoReceived(int errorCode, HashMap<String, Object> items) {
        super.onInfoReceived(errorCode, items);
    }

    @Override
    public void onNotifyText(String notify) {
        super.onNotifyText(notify);
    }

    @Override
    public void RequestFailed(int errcode, String message, int taskType) {
        super.RequestFailed(errcode, message, taskType);
    }

    @Override
    public void RequestSuccessful(String jsonString, int taskType) {
    	Gson gson = new Gson();
    	switch (taskType)
    	{
    	case Config.LOGIN_TYPE:
    		if (chBoxRememberPassword.isChecked()) {
    			Config.setPhoneNum(loginPhoneNumber.getText().toString());
    			Config.setPassword(loginPassword.getText().toString());
    			Config.saveConfig();
    		}
    		ToastUtil.show(this, this.getString(R.string.loginSuccess));
    		Entity<User> entity = gson.fromJson(jsonString, 
    				new TypeToken<Entity<User>>() {}.getType());
    		Config.User = entity.getData();
			logingNimServer();

    		break;
    		
    	case Config.REGISTER:
    		ToastUtil.show(this, this.getString(R.string.signUpSuccess));
    		initialView(true);
    		break;
    	case Config.forget_password:
    		ToastUtil.show(this, this.getString(R.string.signUpgetPassword));
    		initialView(true);
    		break;
    		
    	case Config.VERIFY:
    		ToastUtil.show(this, this.getString(R.string.ValidateSuccess));
			Entity<String> validateEntity = gson.fromJson(jsonString,
					new TypeToken<Entity<String>>() {}.getType());
			verifyString = validateEntity.getData();
			signUpValidate.setText(verifyString);
    		break;
    	}
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
    
    /**
     * Exchange login and sign up view
     * @param isLoginIn
     */
    private void initialView(boolean isLoginIn)
    {
    	if (isLoginIn)
    	{
    		exChangeView(loginView, signUpView);
    	}
    	else
    	{
    		exChangeView(signUpView, loginView);
    	}
    }
    
    private void showUserProtocol()
    {
    	final PromptDialog.Builder builder = new PromptDialog.Builder(this);
		builder.setTitle(this.getString(R.string.readUserProtocol));
		View v = getLayoutInflater().inflate(R.layout.dialog_protocol,
				null);
		builder.setContentView(v);
		builder.setPositiveButton(this.getString(R.string.login_ok), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				signUpcheckBox.setChecked(true);
				dialog.dismiss();
				// 设置你的操作事项
			}
		});

		builder.setNegativeButton(this.getString(R.string.login_cancel),
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		builder.create().show();
    }

    //region 切换登陆和注册view，显示动画效果
    private void exChangeView(final View view1, final View view2) {
        view2.setVisibility(View.VISIBLE);
        Animation in = new AlphaAnimation(0f, 1f);
        in.setDuration(500);
        Animation out = new AlphaAnimation(1f, 0f);
        out.setDuration(500);
        in.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view1.setVisibility(View.VISIBLE);
            }
        });
        out.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view2.setVisibility(View.GONE);
            }
        });

        view1.startAnimation(in);
        view2.startAnimation(out);
    }
    //endregion
}

