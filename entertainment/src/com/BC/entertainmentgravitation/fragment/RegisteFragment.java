package com.BC.entertainmentgravitation.fragment;

import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.BC.entertainmentgravitation.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.summer.config.Config;
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

public class RegisteFragment extends BaseFragment implements OnClickListener{

	private View rootView;
	private EditText editName;
	private EditText editPhone;
	private EditText editYanzhengma;
	private EditText editPassword;
	private EditText editPasswordAgain;
	private EditText editShareCode;
	private CheckBox chBoxRead;

	private Gson gson;
	private String verifyString;
	private iRegister iRegiste;
	private boolean isForgetPassword;
	
	
	public interface iRegister
	{
		void FinishRegister(boolean isSeccuss);
	}

	public void IsForgetPassword(boolean isForgetPassword)
	{
		this.isForgetPassword = isForgetPassword;
	}
	
	@Override
	public void onAttach(Activity activity) {
		try {
			iRegiste = (iRegister)activity;
			
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
		rootView = inflater.inflate(R.layout.fragment_signup, null);
		return rootView;
	}
	
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initView();
	}
	private void initView()
	{
		editName = (EditText) rootView.findViewById(R.id.eTextNameRegister);
		editPhone = (EditText) rootView.findViewById(R.id.eTextPhoneRegister);
		editYanzhengma = (EditText) rootView.findViewById(R.id.eTextYanzhengmaRegister);
		editPassword = (EditText) rootView.findViewById(R.id.eTextPasswordRegister);
		editPasswordAgain = (EditText) rootView.findViewById(R.id.eTextPasswordAgainRegister);
		editShareCode = (EditText) rootView.findViewById(R.id.eTextShareCodeRegister);
		clearAll();
		chBoxRead = (CheckBox) rootView.findViewById(R.id.cBoxRead);
		if (isForgetPassword)
		{
			rootView.findViewById(R.id.rLayoutShareCode).setVisibility(View.GONE);
		}
		
		rootView.findViewById(R.id.imgViewYanzhengma).setOnClickListener(this);
		rootView.findViewById(R.id.btnRegister).setOnClickListener(this);
		rootView.findViewById(R.id.btnCancel).setOnClickListener(this);
		
		chBoxRead.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
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
	
	private void clearAll()
	{
		editName.setText("");
		editPhone.setText("");
		editYanzhengma.setText("");
		editPassword.setText("");
		editPasswordAgain.setText("");
		editShareCode.setText("");
	}
	
	private boolean isValidate()
	{
		try {
			if (ValidateUtil.isEmpty(editName, "用户名") || ValidateUtil.isEmpty(editPhone, "手机")
					|| ValidateUtil.isEmpty(editYanzhengma, "验证码") || ValidateUtil.isEmpty(editPassword, "密码")
					|| ValidateUtil.isEmpty(editPasswordAgain, "再输密码"))
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
	
	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
		case R.id.imgViewYanzhengma:
			sendYanzhengmaRequest();
			break;
		case R.id.btnRegister:
			if (isForgetPassword)
			{
				sendForgetPasswordRequest();
			}
			else
			{
				sendSignUpRequest();
			}
			break;
		case R.id.btnCancel:
    		if (iRegiste != null)
    		{
        		iRegiste.FinishRegister(false);
    		}
			break;
		}
	}
	
    /**
     * 获取验证码
     */
    private void sendYanzhengmaRequest()
    {
		if (editPhone.getText().toString().equals("")
				|| !ValidateUtil.isMobileNumber(editPhone)) {
			ToastUtil.show(getActivity(), this.getString(R.string.signUpPhone));
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
		if (!chBoxRead.isChecked()) {
			ToastUtil.show(getActivity(), this.getString(R.string.signUpUserProtocol));
			return;
		}
		if (isValidate())
		{
			if (!editPassword.getText().toString()
					.contains(editPasswordAgain.getText().toString())) {
				ToastUtil.show(getActivity(), this.getString(R.string.signUpPasswordDifferent));
				return;
			}
			if (!editYanzhengma.getText().toString().contains(verifyString)) {
				ToastUtil.show(getActivity(), this.getString(R.string.signUpValidateError));
				return;
			}
			List<NameValuePair> params = getSignUpParams();
			ShowProgressDialog(this.getString(R.string.signUping));
			addToThreadPool(Config.REGISTER, "singUp", params);
		}
    }
    
    private void sendForgetPasswordRequest()
    {
		if (isValidate()) {
			if (!editPassword.getText().toString()
					.equals(editPasswordAgain.getText().toString())) {
				ToastUtil.show(getActivity(), this.getString(R.string.signUpPasswordDifferent));
				return;
			}
			if (!editYanzhengma.getText().toString().equals(verifyString)) {
				ToastUtil.show(getActivity(), this.getString(R.string.signUpValidateError));
				return;
			}
			List<NameValuePair> params = getSignUpParams();
			ShowProgressDialog(this.getString(R.string.signUpForgetPassword));
			addToThreadPool(Config.forget_password, "forget password", params);
		}

    }
    
    private List<NameValuePair> getSignUpParams()
    {
    	HashMap<String, String> entity = new HashMap<String, String>();
		entity.put("phone", editPhone.getText().toString());
		entity.put("passWord", editPassword.getText().toString());
		entity.put("verify", editYanzhengma.getText().toString());
		if (!isForgetPassword)
		{
			entity.put("shareCode", editShareCode.getText().toString());
		}

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
    
    private List<NameValuePair> getValidateParams()
    {
    	HashMap<String, String> entity = new HashMap<String, String>();
		entity.put("phone", editPhone.getText().toString());
		return JsonUtil.requestForNameValuePair(entity);
    }

	@Override
	public void RequestSuccessful(String jsonString, int taskType) {
    	switch (taskType)
    	{
    		
    	case Config.REGISTER:
    		ToastUtil.show(getActivity(), this.getString(R.string.signUpSuccess));
    		if (iRegiste != null)
    		{
        		iRegiste.FinishRegister(true);
    		}

    		break;
/*    	case Config.forget_password:
    		ToastUtil.show(getActivity(), this.getString(R.string.signUpgetPassword));
    		initialView(true);
    		break;*/
    		
    	case Config.VERIFY:
    		ToastUtil.show(getActivity(), this.getString(R.string.ValidateSuccess));
			Entity<String> validateEntity = gson.fromJson(jsonString,
					new TypeToken<Entity<String>>() {}.getType());
			verifyString = validateEntity.getData();
			if (verifyString != null)
			{
				editYanzhengma.setText(verifyString);
			}
			else
			{
				ToastUtil.show(getActivity(), "验证码获取失败，请 重新获取");
			}
    		break;
    	}
	}

}
