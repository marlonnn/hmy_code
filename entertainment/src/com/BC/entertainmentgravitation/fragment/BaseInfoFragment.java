package com.BC.entertainmentgravitation.fragment;

import com.BC.entertainmentgravitation.MainActivity;
import com.BC.entertainmentgravitation.R;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.BC.entertainment.view.BaseSelectItem;
import com.BC.entertainmentgravitation.entity.EditPersonal;
import com.BC.entertainmentgravitation.util.ConstantArrayListsUtil;
import com.BC.entertainmentgravitation.util.TimestampTool;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.summer.config.Config;
import com.summer.factory.ThreadPoolFactory;
import com.summer.fragment.BaseFragment;
import com.summer.handler.InfoHandler;
import com.summer.json.Entity;
import com.summer.task.HttpBaseTask;
import com.summer.treadpool.ThreadPoolConst;
import com.summer.utils.JsonUtil;
import com.summer.utils.ToastUtil;
import com.summer.utils.UrlUtil;
import com.summer.view.CircularImage;

public class BaseInfoFragment extends BaseFragment implements OnClickListener{

	Activity activity;
	View contentView;
	EditText nickname, In_the_mood,
			email, Mobile_phone;
	TextView level, copy;
	BaseSelectItem gender, professional, nationality, region, language,
			constellation, national, height, weight, birthday;

	List<String> genderList = Arrays.asList(ConstantArrayListsUtil.gender);

	List<String> professionalList = Arrays
			.asList(ConstantArrayListsUtil.professional);
	List<String> nationalityList = Arrays
			.asList(ConstantArrayListsUtil.nationality);
	List<String> languageList = Arrays.asList(ConstantArrayListsUtil.language);
	List<String> constellationList = Arrays
			.asList(ConstantArrayListsUtil.constellation);
	List<String> nationalList = Arrays.asList(ConstantArrayListsUtil.national);
	List<String> height1List = Arrays.asList(ConstantArrayListsUtil.height1);
	List<String> height2List = Arrays.asList(ConstantArrayListsUtil.height2);
	List<String> height3List = Arrays.asList(ConstantArrayListsUtil.height3);
	List<String> weight1List = Arrays.asList(ConstantArrayListsUtil.weight1);
	List<String> weight2List = Arrays.asList(ConstantArrayListsUtil.weight2);

	private CircularImage Head_portrait;
	private Bitmap Head_portraitbmp;
	private String headFile;

	Button editButton, exitEditButton;
	boolean canEdit = false;
	
	private ISelectPicture fragmentSelectPicture;
	
	public ISelectPicture getFragmentSelectPicture() {
		return fragmentSelectPicture;
	}

	public void setFragmentSelectPicture(ISelectPicture fragmentSelectPicture) {
		this.fragmentSelectPicture = fragmentSelectPicture;
	}

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		activity = getActivity();
		contentView = inflater.inflate(R.layout.fragment_base_info, null);
		return contentView;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		init();
		if (MainActivity.personalInformation != null) {
			initPersonalInformation();
		} else {
			sendReqUser();
		}
	}
	
	private void init() {
		gender = (BaseSelectItem) contentView.findViewById(R.id.gender);
		professional = (BaseSelectItem) contentView
				.findViewById(R.id.professional);
		nationality = (BaseSelectItem) contentView
				.findViewById(R.id.nationality);
		region = (BaseSelectItem) contentView.findViewById(R.id.region);
		language = (BaseSelectItem) contentView.findViewById(R.id.language);
		constellation = (BaseSelectItem) contentView
				.findViewById(R.id.constellation);
		national = (BaseSelectItem) contentView.findViewById(R.id.national);
		height = (BaseSelectItem) contentView.findViewById(R.id.height);
		weight = (BaseSelectItem) contentView.findViewById(R.id.weight);
		birthday = (BaseSelectItem) contentView.findViewById(R.id.birthday);

		gender.setSelectContent(genderList);
		professional.setSelectContent(professionalList);
		nationality.setSelectContent(nationalityList);
		language.setSelectContent(languageList);
		constellation.setSelectContent(constellationList);
		national.setSelectContent(nationalList);

		height.setSelectContent(height1List);
		height.setSelect2Content(height2List);
		height.setSelect3Content(height3List);
		height.setWheelTitle1String("米");
		height.setWheelTitle2String("X10厘米");
		height.setWheelTitle3String("厘米");

		weight.setSelectContent(weight1List);
		weight.setSelect2Content(weight2List);
		weight.setWheelTitle1String("X10公斤");
		weight.setWheelTitle2String("公斤");

		nickname = (EditText) contentView.findViewById(R.id.nickname);
		level = (TextView) contentView.findViewById(R.id.level);
		copy = (TextView)contentView.findViewById(R.id.copy);
		In_the_mood = (EditText) contentView.findViewById(R.id.In_the_mood);
		email = (EditText) contentView.findViewById(R.id.email);
		Mobile_phone = (EditText) contentView.findViewById(R.id.phone);
		editButton = (Button) contentView.findViewById(R.id.editButton);
		exitEditButton = (Button) contentView.findViewById(R.id.exitEditButton);
		Head_portrait = (CircularImage) contentView
				.findViewById(R.id.Head_portrait);
		
		Head_portrait.setOnClickListener(this);
		editButton.setOnClickListener(this);
		exitEditButton.setOnClickListener(this);
		copy.setOnClickListener(this);
		exitEditButton.setVisibility(View.GONE);
		canEdit(canEdit);
	}
	
	public void initPersonalInformation() {
		if (MainActivity.personalInformation == null) {
			ToastUtil.show(activity, "获取数据失败");
			return;
		}
		nickname.setText(MainActivity.personalInformation.getNickname());
		In_the_mood.setText(MainActivity.personalInformation.getIn_the_mood());

		gender.setContent(MainActivity.personalInformation.getGender().equals(
				"男") ? "男" : "女");
		birthday.setContent(MainActivity.personalInformation.getBirthday());
		constellation.setContent(MainActivity.personalInformation
				.getThe_constellation());
		national.setContent(MainActivity.personalInformation.getNational());
		height.setContent(MainActivity.personalInformation.getHeight());
		weight.setContent(MainActivity.personalInformation.getWeight());
		region.setContent(MainActivity.personalInformation.getRegion());

		professional.setContent(MainActivity.personalInformation
				.getProfessional());
		nationality.setContent(MainActivity.personalInformation
				.getNationality());
		region.setContent(MainActivity.personalInformation.getRegion());
		language.setContent(MainActivity.personalInformation.getLanguage());

		email.setText(MainActivity.personalInformation.getEmail());
		Mobile_phone
				.setText(MainActivity.personalInformation.getMobile_phone());

		level.setText("Lv." + MainActivity.personalInformation.getLevel());

		Glide.with(activity)
				.load(MainActivity.personalInformation.getHead_portrait())
				.centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL)
				.placeholder(R.drawable.home_image).into(Head_portrait);
	}

	public void canEdit(boolean b) {
		Head_portrait.setClickable(b);
		nickname.setEnabled(b);
		In_the_mood.setEnabled(b);
		email.setEnabled(b);
		Mobile_phone.setEnabled(b);

		gender.setCanClick(b);
		professional.setCanClick(b);
		nationality.setCanClick(b);
		region.setCanClick(b);
		language.setCanClick(b);
		constellation.setCanClick(b);
		national.setCanClick(b);
		height.setCanClick(b);
		weight.setCanClick(b);
		birthday.setCanClick(b);
	}
	
	public void save() {
		if (MainActivity.personalInformation == null) {
			MainActivity.personalInformation = new EditPersonal();
		}

		MainActivity.personalInformation.setNickname(nickname.getText()
				.toString());
		MainActivity.personalInformation.setIn_the_mood(In_the_mood.getText()
				.toString());
		MainActivity.personalInformation.setGender(gender.getContent().equals(
				"男") ? "男" : "女");
		MainActivity.personalInformation.setBirthday(birthday.getContent());
		MainActivity.personalInformation.setAge(""
				+ TimestampTool.dateDiffYear(birthday.getContent(),
						TimestampTool.getCurrentDate()));

		MainActivity.personalInformation.setProfessional(professional
				.getContent());
		MainActivity.personalInformation.setLanguage(language.getContent());
		MainActivity.personalInformation.setThe_constellation(constellation
				.getContent());
		MainActivity.personalInformation.setNational(national.getContent());
		MainActivity.personalInformation.setHeight(height.getContent());
		MainActivity.personalInformation.setWeight(weight.getContent());

		MainActivity.personalInformation.setLanguage(language.getContent());
		MainActivity.personalInformation.setNationality(nationality
				.getContent());
		MainActivity.personalInformation.setRegion(region.getContent());
		MainActivity.personalInformation.setEmail(email.getText().toString());
		MainActivity.personalInformation.setMobile_phone(Mobile_phone.getText()
				.toString());
		sendReqSaveUser();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.editButton:
			canEdit = !canEdit;
			canEdit(canEdit);
			if (!canEdit) {
				editButton.setText("更改");
				save();
				exitEditButton.setVisibility(View.GONE);
			} else {
				editButton.setText("确定");
				exitEditButton.setVisibility(View.VISIBLE);
			}
			break;

		case R.id.exitEditButton:
			canEdit = false;
			canEdit(canEdit);
			editButton.setText("更改");
			exitEditButton.setVisibility(View.GONE);
			save();
			break;

		case R.id.Head_portrait:
			showAlertDialog(R.layout.dialog_alert3, R.id.button3, R.id.button1,
					R.id.button2, this);
			break;
		case R.id.copy:
			copy(Config.User.getShareCode(), activity);
			Toast.makeText(activity, "已复制到剪切板，快去分享给好友吧",
					Toast.LENGTH_LONG).show();
//			showShare(activity);
			break;
		}
	}
	
	/**
	 * 显示一个自定义的选择图片对话框
	 * 
	 * @param layoutId
	 *            布局
	 * @param noId
	 *            取消按钮
	 * @param phoneImageId
	 *            相册选取
	 * @param takePictureImageId
	 *            拍照
	 */
	public void showAlertDialog(int layoutId, int noId, int phoneImageId,
			int takePictureImageId, final BaseFragment flag) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		final AlertDialog ad = builder.create();
		ad.show();
		Window window = ad.getWindow();

		View view = LayoutInflater.from(getActivity()).inflate(layoutId, null);
		window.setContentView(view);
		window.findViewById(noId).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ad.dismiss();
			}
		});
		window.findViewById(phoneImageId).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (fragmentSelectPicture != null) {
							fragmentSelectPicture.phoneImage(flag);
						}
						ad.dismiss();
					}
				});
		window.findViewById(takePictureImageId).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (fragmentSelectPicture != null) {
							fragmentSelectPicture.takePictureImage(flag);
						}
						ad.dismiss();
					}
				});
	}
	
	@SuppressWarnings("deprecation")
	public void copy(String content, Context context)
	{
		// 得到剪贴板管理器
		ClipboardManager cmb = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
		cmb.setText(content.trim());
	}
	
	/**
	 * 获取用户信息
	 */
	private void sendReqUser() {
		if (Config.User== null) {
			ToastUtil.show(activity, "无法获取信息");
			return;
		}
		HashMap<String, String> entity = new HashMap<String, String>();
		entity.put("clientID", Config.User.getClientID());
		
		ShowProgressDialog("获取用户基本信息...");		
		List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
		addToThreadPool(Config.personal_information, "send search request", params);
	}

	/**
	 * 提交用户信息
	 */
	private void sendReqSaveUser() {
		if (Config.User== null
				|| MainActivity.personalInformation == null) {
			ToastUtil.show(activity, "无法获取信息");
			return;
		}
		EditPersonal entity = MainActivity.personalInformation;
		entity.setClientID(Config.User.getClientID());
		entity.setHead_portrait(Head_portraitbmp == null ? MainActivity.personalInformation
				.getHead_portrait() : ("data:image/jpg;base64," + getBtye64String(Head_portraitbmp)));
		ShowProgressDialog("提交基本信息...");
		String content = JsonUtil.toString(entity);
		addToThreadPool(Config.edit_personal_information, "send save user request", content);

	}
	
    private void addToThreadPool(int taskType, String Tag, List<NameValuePair> params)
    {
    	HttpBaseTask httpTask = new HttpBaseTask(ThreadPoolConst.THREAD_TYPE_FILE_HTTP, Tag, params, UrlUtil.GetUrl(taskType));
    	httpTask.setTaskType(taskType);
    	InfoHandler handler = new InfoHandler(this);
    	httpTask.setInfoHandler(handler);
    	ThreadPoolFactory.getThreadPoolManager().addTask(httpTask);
    }
	
    private void addToThreadPool(int taskType, String Tag, String content)
    {
    	HttpBaseTask httpTask = new HttpBaseTask(ThreadPoolConst.THREAD_TYPE_FILE_HTTP, Tag, content, UrlUtil.GetUrl(taskType));
    	httpTask.setTaskType(taskType);
    	InfoHandler handler = new InfoHandler(this);
    	httpTask.setInfoHandler(handler);
    	ThreadPoolFactory.getThreadPoolManager().addTask(httpTask);
    }
    
    private String getBtye64String(Bitmap bitmapOrg) {
		ByteArrayOutputStream bao = new ByteArrayOutputStream();

		bitmapOrg.compress(Bitmap.CompressFormat.PNG, 90, bao);

		byte[] ba = bao.toByteArray();

		String ba1 = Base64.encodeToString(ba, Base64.NO_WRAP);
		return ba1;
	}

	@Override
	public void RequestSuccessful(String jsonString, int taskType) {
		Gson gson = new Gson();
		switch (taskType) {
		case Config.personal_information:
			Entity<EditPersonal> baseEntity = gson.fromJson(jsonString,
					new TypeToken<Entity<EditPersonal>>() {
					}.getType());
			MainActivity.personalInformation = baseEntity.getData();
			if (MainActivity.personalInformation != null) {
				initPersonalInformation();
			} else {
				ToastUtil.show(activity, "获取数据失败");
			}
			break;
		case Config.edit_personal_information:
			ToastUtil.show(getActivity(), "提交成功");
			sendReqUser();
			break;
		}
	}
	
	@Override
	public void obtainImage(String imagePath) {
		Glide.clear(Head_portrait);

		headFile = imagePath;

		File fe = new File(Environment.getExternalStorageDirectory(), imagePath);
		Head_portraitbmp = BitmapFactory.decodeFile(fe.getPath());
		Head_portrait.setImageBitmap(Head_portraitbmp);
	}
	
	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	
}
