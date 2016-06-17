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
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.BC.entertainmentgravitation.entity.Authenticate;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.summer.activity.BaseActivity;
import com.summer.config.Config;
import com.summer.factory.ThreadPoolFactory;
import com.summer.handler.InfoHandler;
import com.summer.logger.XLog;
import com.summer.task.HttpBaseTask;
import com.summer.treadpool.ThreadPoolConst;
import com.summer.utils.JsonUtil;
import com.summer.utils.ToastUtil;
import com.summer.utils.UrlUtil;
import com.summer.utils.ValidateUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * 认证提交界面
 * @author wen zhong
 *
 */
public class Authenticate3Activity extends BaseActivity implements OnClickListener{

	protected final int NONE = 0;
	protected boolean canCut = true;// 是否裁剪
	protected final int PHOTO_GRAPH = 1;// 拍照
	protected final int PHOTO_ZOOM = 2; // 相册
	protected final int PHOTO_RESOULT = 3;// 结果
	protected final String IMAGE_UNSPECIFIED = "image/*";
	protected String IMAGE_FILE = "";
	private String fileName = "";
	private SimpleDateFormat format;
	
	private Authenticate authenticate;
	private TextView txtViewType;
	private EditText editTextId;
	
	private int currentImage = 0;
	
	private String proPhoto = "";
	private String frontPhoto = "";
	private String backPhoto = "";
	private String fullPhoto = "";
	private EditText editTextPro;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_authenticate_step3);
		getAuthenticate();
		format = new SimpleDateFormat("yyyyMMddHHmmsssss");
		findViewById(R.id.imageViewBack).setOnClickListener(this);
		findViewById(R.id.txtViewSubmit).setOnClickListener(this);
		findViewById(R.id.imgBtnProfesson).setOnClickListener(this);
		findViewById(R.id.imgBtnShenfenFront).setOnClickListener(this);
		findViewById(R.id.imgBtnShenfenBack).setOnClickListener(this);
		findViewById(R.id.imgBtnShenfen).setOnClickListener(this);
		txtViewType = (TextView) findViewById(R.id.txtViewType);
		editTextId = (EditText) findViewById(R.id.editTextMobile);
		editTextPro = (EditText) findViewById(R.id.editTextPro);
		txtViewType.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
			}
			
		});

		
	}
	
	private void getAuthenticate()
	{
		try {
			authenticate = (Authenticate) getIntent().getSerializableExtra("Authenticate");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
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
	
	private void submitRequest(boolean isUpdate)
	{
		HashMap<String, String> entity = new HashMap<String, String>();
		entity.put("clientID", Config.User.getClientID());
		entity.put("realname", authenticate.getName());
		entity.put("mobile", authenticate.getMobile());
		entity.put("bankcard", authenticate.getBankCard());
		entity.put("bankname", authenticate.getBank());
		entity.put("bankbranch", authenticate.getBankBranch());
		entity.put("province", authenticate.getBankProvince());
		entity.put("city", authenticate.getBankCity());
		entity.put("pro_imag", authenticate.getProPhoto());
		entity.put("id_front", authenticate.getIdCardFontPhoto());
		entity.put("id_back", authenticate.getIdCardBackPhoto());
		entity.put("id_hold", authenticate.getIdCardPhoto());
		entity.put("is_update", isUpdate ? "1" : "0");
		ShowProgressDialog("正在提交审核信息，请稍等...");		
		List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
		addToThreadPool(Config.idCheck, "send search request", params);
	}
	
    private void addToThreadPool(int taskType, String Tag, List<NameValuePair> params)
    {
    	HttpBaseTask httpTask = new HttpBaseTask(ThreadPoolConst.THREAD_TYPE_FILE_HTTP, Tag, params, UrlUtil.GetUrl(taskType));
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
		/**
		 * 专业照片
		 */
		case R.id.imgBtnProfesson:
			currentImage = R.id.imgBtnProfesson;
			break;
		/**
		 * 身份证前照
		 */
		case R.id.imgBtnShenfenFront:
			currentImage = R.id.imgBtnShenfenFront;
			break;
		/**
		 * 身份证后照
		 */
		case R.id.imgBtnShenfenBack:
			currentImage = R.id.imgBtnShenfenBack;
			break;
		/**
		 * 身份证全照
		 */
		case R.id.imgBtnShenfen:
			currentImage = R.id.imgBtnShenfen;
			break;
		/**
		 * 提交
		 */
		case R.id.txtViewSubmit:
			if (!checkValidate())
			{
				return;
			}
			submitRequest(false);
			break;
		/**
		 * 返回键
		 */
		case R.id.imageViewBack:
			finish();
			break;
		}
	}
	
	private boolean checkValidate()
	{
		if (ValidateUtil.isEmpty(txtViewType, "证件类型") ||  ValidateUtil.isEmpty(editTextPro, "专业名称"))
		{
			return false;
		}
		if (ValidateUtil.isEmpty(editTextId, "证件号码"))
		{
			ValidateUtil.maybeIsIdentityCard(editTextId);
		}
		if (isNullOrEmpty(proPhoto) || isNullOrEmpty(frontPhoto) || isNullOrEmpty(backPhoto) || isNullOrEmpty(fullPhoto))
		{
			ToastUtil.show(this, "所有信息为必填项，为保证您的利益，请完善所有信息，谢谢");
			return false;
		}
		else
		{
			return true;
		}
	}

	@Override
	public void RequestSuccessful(String jsonString, int taskType) {
		switch(taskType)
		{
		case Config.idCheck:
			ToastUtil.show(this, "审核信息提交成功，请耐心等待，谢谢");
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
				showPhotoImage(currentImage ,photo);
				setImage(new File(Environment.getExternalStorageDirectory(),
						IMAGE_FILE + ".jpg"), photo);
			}

		}

		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private void showPhotoImage(int image, Bitmap photo)
	{
		ImageView imageView = null;
		switch(image)
		{
		case R.id.imgViewProfession:
			imageView = (ImageView) findViewById(R.id.imgViewProfession);
			imageView.setVisibility(View.VISIBLE);
			break;
		case R.id.imgViewShenfenFront:
			imageView = (ImageView) findViewById(R.id.imgViewShenfenFront);
			imageView.setVisibility(View.VISIBLE);
			break;
		case R.id.imgViewShenfenBack:
			imageView = (ImageView) findViewById(R.id.imgViewShenfenBack);
			imageView.setVisibility(View.VISIBLE);
			break;
		case R.id.imgViewShenfen:
			imageView = (ImageView) findViewById(R.id.imgViewShenfen);
			imageView.setVisibility(View.VISIBLE);
			break;
		}
		if (imageView != null)
		{
			setPhoto(imageView, photo);	
		}
	}
	
	private void getPhoto64String(Bitmap photoBitmap)
	{
		switch(currentImage)
		{
		case R.id.imgViewProfession:
			proPhoto = getBtye64String(photoBitmap);
			authenticate.setProPhoto(proPhoto);
			break;
		case R.id.imgViewShenfenFront:
			frontPhoto = getBtye64String(photoBitmap);
			authenticate.setIdCardFontPhoto(frontPhoto);
			break;
		case R.id.imgViewShenfenBack:
			backPhoto = getBtye64String(photoBitmap);
			authenticate.setIdCardBackPhoto(backPhoto);
			break;
		case R.id.imgViewShenfen:
			fullPhoto = getBtye64String(photoBitmap);
			authenticate.setIdCardPhoto(fullPhoto);
			break;
		}
	}
	
	private void setPhoto(ImageView view, Bitmap photo)
	{
		Glide.with(this).load(photo).centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL)
		.placeholder(R.drawable.avatar_def).into(view);
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
		fileName = imagePath;
		File fe = new File(Environment.getExternalStorageDirectory(), imagePath);
		Bitmap bmp = BitmapFactory.decodeFile(fe.getPath());
		getPhoto64String(bmp);
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
