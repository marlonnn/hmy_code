package com.BC.entertainmentgravitation;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.BC.entertainment.cache.InfoCache;
import com.BC.entertainment.view.BaseSelectItem;
import com.BC.entertainmentgravitation.entity.EditPersonal;
import com.BC.entertainmentgravitation.util.ConstantArrayListsUtil;
import com.BC.entertainmentgravitation.util.TimestampTool;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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
import com.summer.utils.ToastUtil;
import com.summer.utils.UrlUtil;
import com.summer.view.CircularImage;

public class BaseInfoActivity extends BaseActivity implements OnClickListener{

	private EditText nickname, In_the_mood,email, Mobile_phone;
	private TextView level, copy;
	private BaseSelectItem gender, professional, nationality, region, language,
		constellation, national, height, weight, birthday;
	
	private List<String> genderList = Arrays.asList(ConstantArrayListsUtil.gender);
	
	private List<String> professionalList = Arrays.asList(ConstantArrayListsUtil.professional);
	private List<String> nationalityList = Arrays.asList(ConstantArrayListsUtil.nationality);
	private List<String> languageList = Arrays.asList(ConstantArrayListsUtil.language);
	private List<String> constellationList = Arrays.asList(ConstantArrayListsUtil.constellation);
	private List<String> nationalList = Arrays.asList(ConstantArrayListsUtil.national);
	private List<String> height1List = Arrays.asList(ConstantArrayListsUtil.height1);
	private List<String> height2List = Arrays.asList(ConstantArrayListsUtil.height2);
	private List<String> height3List = Arrays.asList(ConstantArrayListsUtil.height3);
	private List<String> weight1List = Arrays.asList(ConstantArrayListsUtil.weight1);
	private List<String> weight2List = Arrays.asList(ConstantArrayListsUtil.weight2);
	
	private CircularImage Head_portrait;
	private Bitmap Head_portraitbmp;
	
	boolean canEdit = false;
	
	private SimpleDateFormat format;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal_info);
		initView();
		initPersonalInformation();
	}
	
	private void initView()
	{
		format = new SimpleDateFormat("yyyyMMddHHmmsssss");
		findViewById(R.id.imageViewBack).setOnClickListener(this);
		findViewById(R.id.imgViewModify).setOnClickListener(this);
		gender = (BaseSelectItem) findViewById(R.id.gender);
		professional = (BaseSelectItem) findViewById(R.id.professional);
		nationality = (BaseSelectItem) findViewById(R.id.nationality);
		region = (BaseSelectItem) findViewById(R.id.region);
		language = (BaseSelectItem) findViewById(R.id.language);
		constellation = (BaseSelectItem) findViewById(R.id.constellation);
		national = (BaseSelectItem) findViewById(R.id.national);
		height = (BaseSelectItem) findViewById(R.id.height);
		weight = (BaseSelectItem) findViewById(R.id.weight);
		birthday = (BaseSelectItem) findViewById(R.id.birthday);

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

		nickname = (EditText) findViewById(R.id.nickname);
		level = (TextView) findViewById(R.id.level);
		copy = (TextView)findViewById(R.id.copy);
		In_the_mood = (EditText) findViewById(R.id.In_the_mood);
		email = (EditText) findViewById(R.id.email);
		Mobile_phone = (EditText) findViewById(R.id.phone);
		Head_portrait = (CircularImage)findViewById(R.id.Head_portrait);
		
		Head_portrait.setOnClickListener(this);
		copy.setOnClickListener(this);
		canEdit(true);
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
	
	public void initPersonalInformation() {
		if (InfoCache.getInstance().getPersonalInfo() == null) {
			ToastUtil.show(this, "获取数据失败");
			return;
		}
		nickname.setText(InfoCache.getInstance().getPersonalInfo().getNickname());
		In_the_mood.setText(InfoCache.getInstance().getPersonalInfo().getIn_the_mood());

		gender.setContent(InfoCache.getInstance().getPersonalInfo().getGender().equals(
				"男") ? "男" : "女");
		birthday.setContent(InfoCache.getInstance().getPersonalInfo().getBirthday());
		constellation.setContent(InfoCache.getInstance().getPersonalInfo()
				.getThe_constellation());
		national.setContent(InfoCache.getInstance().getPersonalInfo().getNational());
		height.setContent(InfoCache.getInstance().getPersonalInfo().getHeight());
		weight.setContent(InfoCache.getInstance().getPersonalInfo().getWeight());
		region.setContent(InfoCache.getInstance().getPersonalInfo().getRegion());

		professional.setContent(InfoCache.getInstance().getPersonalInfo()
				.getProfessional());
		nationality.setContent(InfoCache.getInstance().getPersonalInfo()
				.getNationality());
		region.setContent(InfoCache.getInstance().getPersonalInfo().getRegion());
		language.setContent(InfoCache.getInstance().getPersonalInfo().getLanguage());

		email.setText(InfoCache.getInstance().getPersonalInfo().getEmail());
		Mobile_phone
				.setText(InfoCache.getInstance().getPersonalInfo().getMobile_phone());

		level.setText("Lv." + InfoCache.getInstance().getPersonalInfo().getLevel());

		Glide.with(this).load(InfoCache.getInstance().getPersonalInfo().getHead_portrait())
				.centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL)
				.placeholder(R.drawable.home_image).into(Head_portrait);
	}
	
	public void save() {
		if (InfoCache.getInstance().getPersonalInfo() == null) {
			InfoCache.getInstance().CreateEditPersonalInstance();
		}

		InfoCache.getInstance().getPersonalInfo().setNickname(nickname.getText()
				.toString());
		InfoCache.getInstance().getPersonalInfo().setIn_the_mood(In_the_mood.getText()
				.toString());
		InfoCache.getInstance().getPersonalInfo().setGender(gender.getContent().equals(
				"男") ? "男" : "女");
		InfoCache.getInstance().getPersonalInfo().setBirthday(birthday.getContent());
		InfoCache.getInstance().getPersonalInfo().setAge(""
				+ TimestampTool.dateDiffYear(birthday.getContent(),
						TimestampTool.getCurrentDate()));

		InfoCache.getInstance().getPersonalInfo().setProfessional(professional
				.getContent());
		InfoCache.getInstance().getPersonalInfo().setLanguage(language.getContent());
		InfoCache.getInstance().getPersonalInfo().setThe_constellation(constellation
				.getContent());
		InfoCache.getInstance().getPersonalInfo().setNational(national.getContent());
		InfoCache.getInstance().getPersonalInfo().setHeight(height.getContent());
		InfoCache.getInstance().getPersonalInfo().setWeight(weight.getContent());

		InfoCache.getInstance().getPersonalInfo().setLanguage(language.getContent());
		InfoCache.getInstance().getPersonalInfo().setNationality(nationality
				.getContent());
		InfoCache.getInstance().getPersonalInfo().setRegion(region.getContent());
		InfoCache.getInstance().getPersonalInfo().setEmail(email.getText().toString());
		InfoCache.getInstance().getPersonalInfo().setMobile_phone(Mobile_phone.getText()
				.toString());
		sendReqSaveUser();
	}
	
	/**
	 * 获取用户信息
	 */
	private void sendReqUser() {
		if (Config.User== null) {
			ToastUtil.show(this, "无法获取信息");
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
				|| InfoCache.getInstance().getPersonalInfo() == null) {
			ToastUtil.show(this, "无法获取信息");
			return;
		}
		EditPersonal entity = InfoCache.getInstance().getPersonalInfo();
		entity.setClientID(Config.User.getClientID());
		XLog.i("---client id---: " + Config.User.getClientID());
		entity.setHead_portrait(Head_portraitbmp == null ? InfoCache.getInstance().getPersonalInfo()
				.getHead_portrait() : ("data:image/jpg;base64," + getBtye64String(Head_portraitbmp)));
		ShowProgressDialog("提交基本信息...");
		HashMap<String, String> map = JsonUtil.object2HashMap(entity);
		List<NameValuePair> params = JsonUtil.requestForNameValuePair(map);
		addToThreadPool(Config.edit_personal_information, "send save user request", params, false);

	}
	
    private void addToThreadPool(int taskType, String Tag, List<NameValuePair> params)
    {
    	HttpBaseTask httpTask = new HttpBaseTask(ThreadPoolConst.THREAD_TYPE_FILE_HTTP, Tag, params, UrlUtil.GetUrl(taskType));
    	httpTask.setTaskType(taskType);
    	InfoHandler handler = new InfoHandler(this);
    	httpTask.setInfoHandler(handler);
    	ThreadPoolFactory.getThreadPoolManager().addTask(httpTask);
    }
	
    private void addToThreadPool(int taskType, String Tag, List<NameValuePair> params, boolean isUsePostType)
    {
    	HttpBaseTask httpTask = new HttpBaseTask(ThreadPoolConst.THREAD_TYPE_FILE_HTTP, Tag, params, UrlUtil.GetUrl(taskType), isUsePostType);
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
    
	@SuppressWarnings("deprecation")
	public void copy(String content, Context context)
	{
		// 得到剪贴板管理器
		ClipboardManager cmb = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
		cmb.setText(content.trim());
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
			int takePictureImageId) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final AlertDialog ad = builder.create();
		ad.show();
		Window window = ad.getWindow();

		View view = LayoutInflater.from(this).inflate(layoutId, null);
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
						phoneImage();
						ad.dismiss();
					}
				});
		window.findViewById(takePictureImageId).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						takePictureImage();
						ad.dismiss();
					}
				});
	}
	
	/**
	 * 选择手机相册图片
	 * @param flag
	 */
	private void phoneImage() {
		Intent intent = new Intent(Intent.ACTION_PICK, null);
		intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				IMAGE_UNSPECIFIED);
		Calendar newCalendar = Calendar.getInstance();
		IMAGE_FILE = format.format(newCalendar.getTime());
		startActivityForResult(intent, PHOTO_ZOOM);
	}

	/**
	 * 拍照
	 * @param flag
	 */
	private void takePictureImage() {
		Calendar newCalendar = Calendar.getInstance();
		IMAGE_FILE = format.format(newCalendar.getTime());
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT,
				Uri.fromFile(new File(
						Environment.getExternalStorageDirectory(), IMAGE_FILE
								+ ".jpg")));
		startActivityForResult(intent, PHOTO_GRAPH);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imgViewModify:
			save();
			break;
			
		case R.id.Head_portrait:
			showAlertDialog(R.layout.dialog_alert3, R.id.button3, R.id.button1,
					R.id.button2);
			break;
		case R.id.copy:
			copy(Config.User.getShareCode(), this);
			Toast.makeText(this, "已复制到剪切板，快去分享给好友吧",
					Toast.LENGTH_LONG).show();
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
		Gson gson = new Gson();
		switch (taskType) {
		case Config.personal_information:
			Entity<EditPersonal> baseEntity = gson.fromJson(jsonString,
					new TypeToken<Entity<EditPersonal>>() {
					}.getType());
			if (baseEntity.getData() != null)
			{
				InfoCache.getInstance().setPersonalInfo(baseEntity.getData());
				if (InfoCache.getInstance().getPersonalInfo() != null) {
					initPersonalInformation();
				} else {
					ToastUtil.show(this, "获取数据失败");
				}
			}

			break;
		case Config.edit_personal_information:
			ToastUtil.show(this, "提交成功");
			sendReqUser();
			XLog.i("edit personal information success");
			break;
		}
	}
	
	@Override
	public void ObtainImage(String imagePath) {
		Glide.clear(Head_portrait);

		File fe = new File(Environment.getExternalStorageDirectory(), imagePath);
		Head_portraitbmp = BitmapFactory.decodeFile(fe.getPath());
		Head_portrait.setImageBitmap(Head_portraitbmp);
	}
	

}
