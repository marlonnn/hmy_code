package com.BC.entertainmentgravitation;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.BC.entertainment.adapter.PersonalRecycleAdapter;
import com.BC.entertainment.adapter.PersonalRecycleAdapter.OnItemClickListener;
import com.BC.entertainment.cache.InfoCache;
import com.BC.entertainment.cache.PersonalCache;
import com.BC.entertainment.config.Preferences;
import com.BC.entertainmentgravitation.entity.Album;
import com.BC.entertainmentgravitation.entity.EditPersonal;
import com.BC.entertainmentgravitation.entity.Member;
import com.BC.entertainmentgravitation.entity.Personal;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
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
import com.summer.view.CircularImage;
import com.umeng.analytics.MobclickAgent;

/**
 * 首页个人中心
 * @author zhongwen
 *
 */
public class PersonalCenterActivity extends BaseActivity implements OnClickListener, OnItemClickListener {

	private CircularImage portrait;
	private List<Personal> personals;
	private PersonalRecycleAdapter adapter;
	private RecyclerView infoList;
	private TextView txtName;
	private Gson gson;
	private SimpleDateFormat format;
	private ImageView imgViewAuthenticated;
	private TextView txtViewTopFocus;
	private TextView txtViewTopFans;
	private String fileName = "";
	
	private Member member;
	private int lastFragment = 1;
	private Intent intent;
	
	private boolean isNullOrEmpty(String o)
	{
		if (o != null)
		{
			if (o.length() == 0)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			return true;
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		gson = new Gson();
		format = new SimpleDateFormat("yyyyMMddHHmmsssss");
		sendBaseInfoRequest();
		setContentView(R.layout.activity_personal_center);
		findViewById(R.id.imageViewBack).setOnClickListener(this);
		try {
			intent = getIntent();
			lastFragment = intent.getIntExtra("lastFragment", 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void initView(Member member)
	{
		if (member != null)
		{
			findViewById(R.id.lLayoutFocus).setOnClickListener(this);
			findViewById(R.id.lLayoutFans).setOnClickListener(this);
			personals = PersonalCache.getInstance().GetPersonalInfos();
			portrait = (CircularImage) findViewById(R.id.cirImagePortrait);
			portrait.setOnClickListener(this);
			txtName = (TextView) findViewById(R.id.txtName);
			txtViewTopFocus = (TextView) findViewById(R.id.txtViewTopFocus);
			txtViewTopFans = (TextView) findViewById(R.id.txtViewTopFans);
			imgViewAuthenticated = (ImageView) findViewById(R.id.imgViewAuthenticated);
			if (member.getIs_validated().contains("1"))
			{
				imgViewAuthenticated.setVisibility(View.VISIBLE);
			}
			else
			{
				imgViewAuthenticated.setVisibility(View.GONE);
			}

			RelativeLayout r = (RelativeLayout) findViewById(R.id.rLayoutExit);
			r.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
//					SharedPreferencesUtils.setParam(PersonalCenterActivity.this, "autoLogin", false);
					Preferences.saveUserAutoLogin("false");
					Intent intent = new Intent(PersonalCenterActivity.this, LoginActivity.class);
					startActivity(intent);
					finish();
				}
			});
			
			txtName.setText(isNullOrEmpty(member.getNick()) ? "未知用户" : member.getNick());
			txtViewTopFocus.setText(isNullOrEmpty(member.getNick()) ? "未知" : member.getFocus());
			txtViewTopFans.setText(isNullOrEmpty(member.getNick()) ? "未知" : member.getFans());
			Glide.with(this).load(member.getPortrait())
			.centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL)
			.placeholder(R.drawable.avatar_def).into(portrait);
			portrait.setOnClickListener(this);
			infoList = (RecyclerView) findViewById(R.id.listViewInfo);
			
			adapter = new PersonalRecycleAdapter(PersonalCenterActivity.this, personals);
			
	        adapter.notifyDataSetChanged();
	        adapter.setmOnItemClickListener(this);
	        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PersonalCenterActivity.this, LinearLayoutManager.VERTICAL, false);
	        infoList.setVerticalScrollBarEnabled(true);
	        infoList.setLayoutManager(linearLayoutManager);
	        
	        infoList.setItemAnimator(new DefaultItemAnimator());//more的动画效果
	        
	        infoList.setAdapter(adapter);
		}

		
	}
	
	private void setPortrait()
	{
		Glide.with(this)
		.load(InfoCache.getInstance().getPersonalInfo().getHead_portrait())
		.centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL)
		.placeholder(R.drawable.avatar_def).into(portrait);
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
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.lLayoutFocus:
			if (member != null)
			{
				intentToFocus(member);
			}
			break;
		case R.id.lLayoutFans:
			break;
		/**
		 * 修改头像
		 */
		case R.id.cirImagePortrait:
			showAlertDialog(R.layout.dialog_alert3,
					R.id.button3, R.id.button1, R.id.button2);
			
			break;
		/**
		 * 返回键
		 */
		case R.id.imageViewBack:
			intent.putExtra("lastFragment", lastFragment);
			setResult(RESULT_OK, intent);
			finish();
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
			int takePictureImageId) {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final AlertDialog ad = builder.create();
		ad.show();
		Window window = ad.getWindow();

		View view = LayoutInflater.from(this).inflate(layoutId, null);
		window.setContentView(view);
		window.findViewById(noId).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
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
	
	/**
	 * 提交用户信息
	 */
	private void sendReqSaveUser(String image) {
		if (Config.User == null) {
			ToastUtil.show(this, "无法获取信息");
			return;
		}
		HashMap<String, String> entity = new HashMap<String, String>();

		entity.put("clientID", Config.User.getClientID());
		entity.put("pictureID", image);
		ShowProgressDialog("提交基本信息...");		
		List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
		addToThreadPool(Config.image, "send delete album request", params);
	}
	
	/**
	 * 保存相册信息
	 */
	private void sendReqSaveAlbum(Bitmap bitmap) {
		if (Config.User == null) {
			ToastUtil.show(this, "无法获取信息");
			return;
		}
		HashMap<String, String> entity = new HashMap<String, String>();

		entity.put("clientID", Config.User.getClientID());
		entity.put("Image_name", fileName);
		entity.put("Image_type", "1");
		entity.put("Image_location",
				"data:image/jpg;base64," + getBtye64String(bitmap));
		ShowProgressDialog("保存图片...");		
		List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
		addToThreadPool(Config.edit_photo_albums, "send save album request", params);
	}
	
	private String getBtye64String(Bitmap bitmapOrg) {
		ByteArrayOutputStream bao = new ByteArrayOutputStream();

		bitmapOrg.compress(Bitmap.CompressFormat.PNG, 90, bao);

		byte[] ba = bao.toByteArray();

		String ba1 = Base64.encodeToString(ba, Base64.NO_WRAP);
		return ba1;
	}
	
	/**
	 * 获取相册信息
	 */
	private void sendAlbumRequest() {
		if (Config.User == null) {
			ToastUtil.show(this, "无法获取信息");
			return;
		}
		HashMap<String, String> entity = new HashMap<String, String>();

		entity.put("clientID", Config.User.getClientID());
		
		ShowProgressDialog("获取用户相册...");
		List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
		addToThreadPool(Config.photo_album_management, "send album request", params);
	}
	
	/**
	 * 获取认证状态
	 */
	private void sendAuthenticateStatusRequest()
	{
		HashMap<String, String> entity = new HashMap<String, String>();
		entity.put("clientID", Config.User.getClientID());
		List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
		addToThreadPool(Config.idGet, "send authenticate status request", params);
	}
	
	private void sendBaseInfoRequest()
	{
		HashMap<String, String> entity = new HashMap<String, String>();
		entity.put("username", Config.User.getUserName());
		List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
		addToThreadPool(Config.member_in, "send search request", params);
	}
	
    /**
     * 获取用户信息
     */
    private void sendPersonalInfoRequest()
    {
    	if (Config.User == null)
    	{
			ToastUtil.show(this, StringUtil.getXmlResource(this, R.string.mainactivity_login_invalidate));
			return;
    	}
    	HashMap<String, String> entity = new HashMap<String, String>();
    	entity.put("clientID", Config.User.getClientID());
    	List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
    	ShowProgressDialog(this.getString(R.string.mainactivity_get_user_info));
    	addToThreadPool(Config.personal_information, "get user info", params);
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
	public void RequestSuccessful(String jsonString, int taskType) {
		switch(taskType)
		{
		/**
		 * 获取审核信息
		 */
		case Config.idGet:
			Intent it = new Intent(this, AuthenStatusActivity.class);
			Bundle b = new Bundle();
			b.putString("authenStatus", "您已通过审核");
			b.putInt("status", 0);
			it.putExtras(b);
			startActivity(it);
			break;
		case Config.personal_information:
			Entity<EditPersonal> baseEntity = gson.fromJson(jsonString,
					new TypeToken<Entity<EditPersonal>>() {
					}.getType());
			if (baseEntity.getData() != null)
			{
				InfoCache.getInstance().setPersonalInfo(baseEntity.getData());
				setPortrait();
			}

			break;
		case Config.image:
			ToastUtil.show(this, "设置成功");
			sendPersonalInfoRequest();
			break;
		case Config.edit_photo_albums:
			ToastUtil.show(this, "保存成功");
			sendAlbumRequest();
			break;
		case Config.photo_album_management:
			try {
				Entity<Album> baseEntity5 = gson.fromJson(jsonString,
						new TypeToken<Entity<Album>>() {
						}.getType());
				Album album = baseEntity5.getData();
				sendReqSaveUser(album.getMore_pictures().get(0).getPicture_ID());
			} catch (JsonSyntaxException e) {
				e.printStackTrace();
			}
			break;
		case Config.member_in:
			Entity<Member> entity = gson.fromJson(jsonString,
					new TypeToken<Entity<Member>>() {
					}.getType());
			member = entity.getData();
			if (member != null)
			{
				initView(member);
			}
			break;
		}
	}
	
	@Override
	public void onItemClick(View view, int position) {
		Personal personal = (Personal)view.getTag();
		Intent intent = null;
		if(personal != null)
		{
			switch(personal.getResource())
			{
			/**
			 * 基本信息
			 */
			case R.drawable.activity_personal_info:
				intent = new Intent(PersonalCenterActivity.this, BaseInfoActivity.class);
				startActivity(intent);
				break;
			/**
			 * 相册管理
			 */
			case R.drawable.activity_personal_album:
				intent = new Intent(PersonalCenterActivity.this, AlbumActivity.class);
				startActivity(intent);
				break;
			/**
			 * 演艺经历
			 */
			case R.drawable.activity_personal_career:
				intent = new Intent(PersonalCenterActivity.this, CareerActivity.class);
				startActivity(intent);
				break;
			/**
			 * 我的经纪
			 */
			case R.drawable.activity_personal_broker:
				intent = new Intent(PersonalCenterActivity.this, BrokerActivity.class);
				startActivity(intent);
				break;
			/**
			 * 我的收益
			 */
			case R.drawable.activity_modify:
				intent = new Intent(PersonalCenterActivity.this, SignInActivity.class);
				startActivity(intent);
				break;
			/**
			 * 我的收益
			 */
			case R.drawable.activity_personal_income:
				intent = new Intent(PersonalCenterActivity.this, IncomeActivity.class);
				startActivity(intent);
				break;
			/**
			 * 我的娛币
			 */
			case R.drawable.activity_personal_yubi:
				intent = new Intent(PersonalCenterActivity.this, ChargeActivity.class);
				startActivity(intent);
				break;
			/**
			 * 我的红包
			 */
			case R.drawable.activity_personal_envelope:
				intent = new Intent(PersonalCenterActivity.this, EnvelopeActivity.class);
				startActivity(intent);
				break;
			/**
			 * 我的关注
			 */
			case R.drawable.activity_home_bottom_focus:
				if (member != null)
				{
					intentToFocus(member);
				}
				break;
			/**
			 * 身份认证
			 */
			case R.drawable.activity_authenticate:
				sendAuthenticateStatusRequest();
//				intent = new Intent(PersonalCenterActivity, Authenticate1Activity.class);
//				startActivity(intent);
				break;
			/**
			 * 权益中心
			 */
			case R.drawable.activity_rights_center:
				intent = new Intent(PersonalCenterActivity.this, RightsCenterActivity.class);
				startActivity(intent);
				break;
			/**
			 * 意见反馈
			 */
			case R.drawable.activity_personal_feedback:
				intent = new Intent(PersonalCenterActivity.this, FeedbackActivity.class);
				startActivity(intent);
				break;
			/**
			 * 关于我们
			 */
			case R.drawable.activity_personal_about:
				intent = new Intent(PersonalCenterActivity.this, AboutActivity.class);
				startActivity(intent);
				break;
			}
		}
	}
	
	private void intentToFocus(Member member)
	{
		Intent intent = new Intent(this, FocusActivity.class);
		Bundle b2 = new Bundle();
		b2.putSerializable("member", member);
		intent.putExtras(b2);
		startActivity(intent);
	}

	@Override
	public void RequestFailed(int errorCode, String message, int taskType) {
		String msg = "";
		switch(taskType)
		{
		case Config.idGet:
			if (errorCode == 500)
			{
				msg = "数据库错误，请与管理员联系";
			}
			else if (errorCode == 600)
			{
				Intent intent = new Intent(this, Authenticate1Activity.class);
				startActivity(intent);
				return;
			}
			else if (errorCode == 601)
			{
				msg = "信息审核中，请耐心等待";
			}
			else if (errorCode == 602)
			{
				msg= "审核未通过";
			}
			Intent it = new Intent(this, AuthenStatusActivity.class);
			Bundle b = new Bundle();
			b.putString("authenStatus", msg);
			b.putInt("status", errorCode);
			it.putExtras(b);
			startActivity(it);
			break;
			default:
				Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
				break;
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == NONE)
			return;
		// 拍照
		if (requestCode == PHOTO_GRAPH) {
			// 设置文件保存路径
			if (canCut) {
				File picture = new File(
						Environment.getExternalStorageDirectory(), IMAGE_FILE
								+ ".jpg");
				startPhotoZoom(Uri.fromFile(picture));
			} else {
				File picture = new File(
						Environment.getExternalStorageDirectory(), IMAGE_FILE
								+ ".jpg");
				ObtainImage(picture.getPath());
			}

		}
		if (data == null)
			return;

		// 读取相册缩放图片
		if (requestCode == PHOTO_ZOOM) {
			if (canCut) 
			{
				startPhotoZoom(data.getData());
			}
		}
		// 处理结果
		if (requestCode == PHOTO_RESOULT) {
			Bundle extras = data.getExtras();
			if (extras != null) {
				Bitmap photo = extras.getParcelable("data");
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				photo.compress(Bitmap.CompressFormat.JPEG, 75, stream);// (0-100)压缩文件
				// 此处可以把Bitmap保存到sd卡中，具体请看：http://www.cnblogs.com/linjiqin/archive/2011/12/28/2304940.html
				// imageView.setImageBitmap(photo); //把图片显示在ImageView控件上
				setImage(new File(Environment.getExternalStorageDirectory(),
						IMAGE_FILE + ".jpg"), photo);
			}

		}
	}
	
	private void setPortrait(Bitmap bmp)
	{
		if (bmp != null)
		{
			Glide.with(this)
			.load(bmp)
			.centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL)
			.placeholder(R.drawable.avatar_def).into(portrait);
		}

	}
	
	public void ObtainImage(String imagePath) {
		// TODO Auto-generated method stub
		fileName = imagePath;
		File fe = new File(Environment.getExternalStorageDirectory(), imagePath);
		Bitmap bmp = BitmapFactory.decodeFile(fe.getPath());
		sendReqSaveAlbum(bmp);
//		setPortrait(bmp);
	}
	
	private void setImage(File path, Bitmap bmp) {
		try {
			if (!path.exists()) {
				path.createNewFile();
			}
			FileOutputStream iStream = new FileOutputStream(path);
			bmp.compress(CompressFormat.JPEG, 100, iStream);
			iStream.close();
			ObtainImage(path.getName());
			iStream = null;
			path = null;
		} catch (IOException e) {
			e.printStackTrace();
			XLog.e(e.toString());
		}
	}
}
