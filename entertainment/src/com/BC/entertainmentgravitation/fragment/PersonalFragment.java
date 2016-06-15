package com.BC.entertainmentgravitation.fragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.BC.entertainment.adapter.PersonalRecycleAdapter;
import com.BC.entertainment.adapter.PersonalRecycleAdapter.OnItemClickListener;
import com.BC.entertainment.cache.InfoCache;
import com.BC.entertainment.cache.PersonalCache;
import com.BC.entertainmentgravitation.AboutActivity;
import com.BC.entertainmentgravitation.AlbumActivity;
import com.BC.entertainmentgravitation.BaseInfoActivity;
import com.BC.entertainmentgravitation.BrokerActivity;
import com.BC.entertainmentgravitation.CareerActivity;
import com.BC.entertainmentgravitation.ChargeActivity;
import com.BC.entertainmentgravitation.EnvelopeActivity;
import com.BC.entertainmentgravitation.FeedbackActivity;
import com.BC.entertainmentgravitation.FocusActivity;
import com.BC.entertainmentgravitation.IncomeActivity;
import com.BC.entertainmentgravitation.LoginActivity;
import com.BC.entertainmentgravitation.R;
import com.BC.entertainmentgravitation.entity.Album;
import com.BC.entertainmentgravitation.entity.EditPersonal;
import com.BC.entertainmentgravitation.entity.Member;
import com.BC.entertainmentgravitation.entity.Personal;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
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
import com.summer.utils.StringUtil;
import com.summer.utils.ToastUtil;
import com.summer.utils.UrlUtil;
import com.summer.view.CircularImage;

/**
 * 首页 个人中心
 * @author zhongwen
 *
 */
public class PersonalFragment extends BaseFragment implements OnClickListener, OnItemClickListener{
	
	protected final int NONE = 0;
	protected boolean canCut = true;// 是否裁剪
	protected final int PHOTO_GRAPH = 1;// 拍照
	protected final int PHOTO_ZOOM = 2; // 相册
	protected final int PHOTO_RESOULT = 3;// 结果
	protected final String IMAGE_UNSPECIFIED = "image/*";
	protected String IMAGE_FILE = "";
	private String fileName = "";
	
	private View rootView;
	
	private CircularImage portrait;
	
	private List<Personal> personals;
	
	private PersonalRecycleAdapter adapter;

	private RecyclerView infoList;
	
	private TextView txtName;
	
	private Gson gson;
	
	/**
	 * 从后台获取的个人信息
	 */
	private EditPersonal info;
	private Activity ativity;
	private SimpleDateFormat format;
	private TextView txtViewTopFocus;
	private TextView txtViewTopFans;
	
	@Override
	public void onAttach(Activity activity) {
		this.ativity = activity;
		super.onAttach(activity);
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		gson = new Gson();
		format = new SimpleDateFormat("yyyyMMddHHmmsssss");
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
		rootView = inflater.inflate(R.layout.fragment_personal, null);
		return rootView;
	}
	
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initView();
	}
	
	private void sendBaseInfoRequest()
	{
		HashMap<String, String> entity = new HashMap<String, String>();
		entity.put("username", Config.User.getUserName());
//		ShowProgressDialog("获取热门用户基本信息...");		
		List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
		addToThreadPool(Config.member_in, "send search request", params);
	}
	
	/**
	 * 提交用户信息
	 */
	private void sendReqSaveUser(String image) {
		if (Config.User == null) {
			ToastUtil.show(getActivity(), "无法获取信息");
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
			ToastUtil.show(getActivity(), "无法获取信息");
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
	
	/**
	 * 获取相册信息
	 */
	private void sendAlbumRequest() {
		if (Config.User == null) {
			ToastUtil.show(getActivity(), "无法获取信息");
			return;
		}
		HashMap<String, String> entity = new HashMap<String, String>();

		entity.put("clientID", Config.User.getClientID());
		
		ShowProgressDialog("获取用户相册...");
		List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
		addToThreadPool(Config.photo_album_management, "send album request", params);
	}
	
    /**
     * 获取用户信息
     */
    private void sendPersonalInfoRequest()
    {
    	if (Config.User == null)
    	{
			ToastUtil.show(getActivity(), StringUtil.getXmlResource(getActivity(), R.string.mainactivity_login_invalidate));
			return;
    	}
    	HashMap<String, String> entity = new HashMap<String, String>();
    	entity.put("clientID", Config.User.getClientID());
    	List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
    	ShowProgressDialog(this.getString(R.string.mainactivity_get_user_info));
    	addToThreadPool(Config.personal_information, "get user info", params);
    }
	
	private String getBtye64String(Bitmap bitmapOrg) {
		ByteArrayOutputStream bao = new ByteArrayOutputStream();

		bitmapOrg.compress(Bitmap.CompressFormat.PNG, 90, bao);

		byte[] ba = bao.toByteArray();

		String ba1 = Base64.encodeToString(ba, Base64.NO_WRAP);
		return ba1;
	}
	
    private void addToThreadPool(int taskType, String Tag, List<NameValuePair> params)
    {
    	HttpBaseTask httpTask = new HttpBaseTask(ThreadPoolConst.THREAD_TYPE_FILE_HTTP, Tag, params, UrlUtil.GetUrl(taskType));
    	httpTask.setTaskType(taskType);
    	InfoHandler handler = new InfoHandler(this);
    	httpTask.setInfoHandler(handler);
    	ThreadPoolFactory.getThreadPoolManager().addTask(httpTask);
    }
    
	private void initView()
	{
		personals = PersonalCache.getInstance().GetPersonalInfos();
		info = InfoCache.getInstance().getPersonalInfo();
		
		portrait = (CircularImage) rootView.findViewById(R.id.cirImagePortrait);
		portrait.setOnClickListener(this);
		txtName = (TextView) rootView.findViewById(R.id.txtName);
		txtViewTopFocus = (TextView) rootView.findViewById(R.id.txtViewTopFocus);
		txtViewTopFans = (TextView) rootView.findViewById(R.id.txtViewTopFans);
		RelativeLayout r = (RelativeLayout) rootView.findViewById(R.id.rLayoutExit);
		r.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Config.manualExit = true;
				Intent intent = new Intent(getActivity(), LoginActivity.class);
				startActivity(intent);
				if (ativity != null)
				{
					ativity.finish();
				}
			}
		});
		
		if (info != null && info.getNickname() != null)
		{
			txtName.setText(info.getNickname());
		}
//		txtViewTopFocus.setText(isNullOrEmpty(info.get) ? "" : )
		setPortrait();
		portrait.setOnClickListener(this);
		infoList = (RecyclerView) rootView.findViewById(R.id.listViewInfo);
		
		adapter = new PersonalRecycleAdapter(getActivity(), personals);
		
        adapter.notifyDataSetChanged();
        adapter.setmOnItemClickListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        infoList.setVerticalScrollBarEnabled(true);
        infoList.setLayoutManager(linearLayoutManager);
        
        infoList.setItemAnimator(new DefaultItemAnimator());//more的动画效果
        
        infoList.setAdapter(adapter);
		
	}
	
	private void setPortrait()
	{
		Glide.with(this)
		.load(InfoCache.getInstance().getPersonalInfo().getHead_portrait())
		.centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL)
		.placeholder(R.drawable.avatar_def).into(portrait);
	}
	
//	private boolean isNullOrEmpty(String o)
//	{
//		if (o != null)
//		{
//			if (o.length() == 0)
//			{
//				return true;
//			}
//			else
//			{
//				return false;
//			}
//		}
//		else
//		{
//			return true;
//		}
//	}

	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
		/**
		 * 修改头像
		 */
		case R.id.cirImagePortrait:
			showAlertDialog(R.layout.dialog_alert3,
					R.id.button3, R.id.button1, R.id.button2);
//			sendBaseInfoRequest();
			
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
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		final AlertDialog ad = builder.create();
		ad.show();
		Window window = ad.getWindow();

		View view = LayoutInflater.from(getActivity()).inflate(layoutId, null);
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
				intent = new Intent(getActivity(), BaseInfoActivity.class);
				startActivity(intent);
				break;
			/**
			 * 相册管理
			 */
			case R.drawable.activity_personal_album:
				intent = new Intent(getActivity(), AlbumActivity.class);
				startActivity(intent);
				break;
			/**
			 * 演艺经历
			 */
			case R.drawable.activity_personal_career:
				intent = new Intent(getActivity(), CareerActivity.class);
				startActivity(intent);
				break;
			/**
			 * 我的经纪
			 */
			case R.drawable.activity_personal_broker:
				intent = new Intent(getActivity(), BrokerActivity.class);
				startActivity(intent);
				break;
			/**
			 * 我的收益
			 */
			case R.drawable.activity_personal_income:
				intent = new Intent(getActivity(), IncomeActivity.class);
				startActivity(intent);
				break;
			/**
			 * 我的娛币
			 */
			case R.drawable.activity_personal_yubi:
				intent = new Intent(getActivity(), ChargeActivity.class);
				startActivity(intent);
				break;
			/**
			 * 我的红包
			 */
			case R.drawable.activity_personal_envelope:
				intent = new Intent(getActivity(), EnvelopeActivity.class);
				startActivity(intent);
				break;
			/**
			 * 我的关注
			 */
			case R.drawable.activity_home_bottom_focus:
				sendBaseInfoRequest();
				break;
			/**
			 * 意见反馈
			 */
			case R.drawable.activity_personal_feedback:
				intent = new Intent(getActivity(), FeedbackActivity.class);
				startActivity(intent);
				break;
			/**
			 * 关于我们
			 */
			case R.drawable.activity_personal_about:
				intent = new Intent(getActivity(), AboutActivity.class);
				startActivity(intent);
				break;
			}
		}
	}

	@Override
	public void RequestSuccessful(String jsonString, int taskType) {
		switch(taskType)
		{
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
			ToastUtil.show(getActivity(), "设置成功");
			sendPersonalInfoRequest();
			break;
		case Config.edit_photo_albums:
			ToastUtil.show(getActivity(), "保存成功");
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
			if (entity != null && entity.getData() != null)
			{
				intentToFocus(entity.getData());
			}
			break;
		}
	}
	
	private void intentToFocus(Member member)
	{
		Intent intent = new Intent(getActivity(), FocusActivity.class);
		Bundle b2 = new Bundle();
		b2.putSerializable("clientId", member.getId());
		intent.putExtras(b2);
		startActivity(intent);
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

		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 收缩图片
	 * 
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, IMAGE_UNSPECIFIED);
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 4);
		intent.putExtra("aspectY", 3);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 400);
		intent.putExtra("outputY", 300);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, PHOTO_RESOULT);
	}
	
	public void ObtainImage(String imagePath) {
		// TODO Auto-generated method stub
		fileName = imagePath;
		File fe = new File(Environment.getExternalStorageDirectory(), imagePath);
		Bitmap bmp = BitmapFactory.decodeFile(fe.getPath());
		sendReqSaveAlbum(bmp);
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
