package com.BC.entertainmentgravitation;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.BC.entertainment.adapter.AuthenPictureAdapter;
import com.BC.entertainment.adapter.BankAdapter;
import com.BC.entertainment.cache.AuthenCache;
import com.BC.entertainmentgravitation.dialog.AuthenDialog;
import com.BC.entertainmentgravitation.entity.Authenticate;
import com.BC.entertainmentgravitation.fragment.AuthenPictureFragment;
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
public class Authenticate3Activity extends BaseActivity implements OnClickListener, OnWheelChangedListener {

	private Authenticate authenticate;
	private TextView txtViewType;
	private EditText editTextId;
	
	private Bitmap proPhoto;
	private Bitmap frontPhoto;
	private Bitmap backPhoto;
	private Bitmap fullPhoto;
	
	private String pro64Photo = "";
	private String front64Photo = "";
	private String back64Photo = "";
	private String full64Photo = "";
	private String fileName = "";
	
	private TextView txtViewPro;
	
	private ImageView imgViewProfession;
	private ImageView imgViewShenfenFront;
	private ImageView imgViewShenfenBack;
	private ImageView imgViewShenfen;
	private ImageType currentType;
	private SimpleDateFormat format;
	
	private AuthenDialog.Builder builder;
	private List<Bitmap> getPhotos()
	{
		List<Bitmap> photos = new ArrayList<Bitmap>();
		if (!isNullOrEmpty(proPhoto))
		{
			photos.add(proPhoto);
		}
		if (!isNullOrEmpty(frontPhoto))
		{
			photos.add(frontPhoto);
		}
		if (!isNullOrEmpty(backPhoto))
		{
			photos.add(backPhoto);
		}
		if (!isNullOrEmpty(fullPhoto))
		{
			photos.add(fullPhoto);
		}
		return photos;
	}
	
	private void showPhotoDialog(final AuthenPictureAdapter adapter, int position)
	{
		final AuthenPictureFragment fragment = new AuthenPictureFragment(position);
		fragment.setStyle(R.style.Dialog, DialogFragment.STYLE_NO_FRAME);
		fragment.show(getSupportFragmentManager(), "PictureDialog");
		fragment.setAdapter(adapter);
		fragment.setPage(position);
		
		fragment.setChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
//				if (adapter.getCount() - 1 == arg0) {
//					index++;
//					sendAlbumRequest(index);
//				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
	}
	
	public enum ImageType{
		profession,
		shenfenFront,
		shenfenBack,
		shenfen
	}
	
    public static String getStringToday() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssss");
        String dateString = formatter.format(currentTime);
        return dateString;
    }
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_authenticate_step3);
		getAuthenticate();
		initialView();
		format = new SimpleDateFormat("yyyyMMddHHmmsssss");
		findViewById(R.id.imageViewBack).setOnClickListener(this);
		findViewById(R.id.txtViewSubmit).setOnClickListener(this);
		findViewById(R.id.imgBtnProfesson).setOnClickListener(this);
		findViewById(R.id.imgBtnShenfenFront).setOnClickListener(this);
		findViewById(R.id.imgBtnShenfenBack).setOnClickListener(this);
		findViewById(R.id.imgBtnShenfen).setOnClickListener(this);
		
		findViewById(R.id.imgViewProfession).setOnClickListener(this);
		findViewById(R.id.imgViewShenfenFront).setOnClickListener(this);
		findViewById(R.id.imgViewShenfenBack).setOnClickListener(this);
		findViewById(R.id.imgViewShenfen).setOnClickListener(this);
		
		txtViewType = (TextView) findViewById(R.id.txtViewType);
		editTextId = (EditText) findViewById(R.id.editTextId);
		txtViewPro = (TextView) findViewById(R.id.txtViewPro);
		txtViewType.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				showSelectIDType();
			}
			
		});
		txtViewPro.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				showSelectPro();
			}
			
		});
	}
	
	/**
	 * 选择证件类型
	 */
	private void showSelectIDType()
	{
		final String[] IDType = AuthenCache.IDType;
		if (IDType != null && IDType.length > 0)
		{
			BankAdapter adapter = new BankAdapter(this, IDType);
			builder = new AuthenDialog.Builder(this, adapter);
			builder.setWheelChangedListener(this);
			builder.setTitle("证件类型");
			builder.setPositiveButton(new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					int location = builder.getProvince().getCurrentItem();
					String type = IDType[location];
					txtViewType.setText(type);
					editTextId.setText("");
					authenticate.setAuthType(type);
					if (dialog != null)
					{
						dialog.dismiss();
					}
				}});
			
			builder.setNegativeButton(new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (dialog != null)
					{
						dialog.dismiss();
					}
				}
			});
			AuthenDialog dialog = builder.Create(3, null);
			dialog.show();
		}
	}
	
	/**
	 * 选择专业名称
	 */
	private void showSelectPro()
	{
		final String[] pros = AuthenCache.professional;
		if (pros != null && pros.length > 0)
		{
			BankAdapter adapter = new BankAdapter(this, pros);
			builder = new AuthenDialog.Builder(this, adapter);
			builder.setWheelChangedListener(this);
			builder.setTitle("专业名称");
			builder.setPositiveButton(new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					int location = builder.getProvince().getCurrentItem();
					String pro = pros[location];
					authenticate.setProName(pro);
					txtViewPro.setText(pro);
					if (dialog != null)
					{
						dialog.dismiss();
					}
				}});
			
			builder.setNegativeButton(new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (dialog != null)
					{
						dialog.dismiss();
					}
				}
			});
			AuthenDialog dialog = builder.Create(3, null);
			dialog.show();
		}
	}
	
	private void initialView()
	{
		imgViewProfession = (ImageView) findViewById(R.id.imgViewProfession);
		imgViewShenfenFront = (ImageView) findViewById(R.id.imgViewShenfenFront);
		imgViewShenfenBack = (ImageView) findViewById(R.id.imgViewShenfenBack);
		imgViewShenfen = (ImageView) findViewById(R.id.imgViewShenfen);
	}
	
	private void getAuthenticate()
	{
		try {
			authenticate = (Authenticate) getIntent().getSerializableExtra("authenticate");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private boolean isNullOrEmpty(Bitmap o)
	{
		if (o != null)
		{
			return false;
		}
		else
		{
			return true;
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
		entity.put("pro_imag", "data:image/jpg;base64," + authenticate.getProPhoto());
		entity.put("id_front", "data:image/jpg;base64," + authenticate.getIdCardFontPhoto());
		entity.put("id_back", "data:image/jpg;base64," + authenticate.getIdCardBackPhoto());
		entity.put("id_hold", "data:image/jpg;base64," + authenticate.getIdCardPhoto());
		entity.put("cardno", isNullOrEmpty(editTextId.getText().toString()) ? "" : editTextId.getText().toString());
		entity.put("is_update", isUpdate ? "1" : "0");
		ShowProgressDialog("正在提交，请稍等...");		
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
			currentType = ImageType.profession;
			showAlertDialog(R.layout.dialog_alert3,
					R.id.button3, R.id.button1, R.id.button2);
			break;
		/**
		 * 身份证前照
		 */
		case R.id.imgBtnShenfenFront:
			currentType = ImageType.shenfenFront;
			showAlertDialog(R.layout.dialog_alert3,
					R.id.button3, R.id.button1, R.id.button2);
			break;
		/**
		 * 身份证后照
		 */
		case R.id.imgBtnShenfenBack:
			currentType = ImageType.shenfenBack;
			showAlertDialog(R.layout.dialog_alert3,
					R.id.button3, R.id.button1, R.id.button2);
			break;
		/**
		 * 身份证全照
		 */
		case R.id.imgBtnShenfen:
			currentType = ImageType.shenfen;
			showAlertDialog(R.layout.dialog_alert3,
					R.id.button3, R.id.button1, R.id.button2);
			break;
			
		/**
		 * imageview 专业照片
		 */
		case R.id.imgViewProfession:
			showPhotoDialog(new AuthenPictureAdapter(getPhotos(), this), 0);
			break;
		/**
		 * imageview 身份证前照
		 */
		case R.id.imgViewShenfenFront:
			showPhotoDialog(new AuthenPictureAdapter(getPhotos(), this), 1);
			break;
		/**
		 * imageview 身份证后照
		 */
		case R.id.imgViewShenfenBack:
			showPhotoDialog(new AuthenPictureAdapter(getPhotos(), this), 2);
			break;
		/**
		 * imageview 身份证全照
		 */
		case R.id.imgViewShenfen:
			showPhotoDialog(new AuthenPictureAdapter(getPhotos(), this), 3);
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
		if (ValidateUtil.isEmpty(txtViewType, "证件类型") ||  ValidateUtil.isEmpty(txtViewPro, "专业名称"))
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
	
	@SuppressLint("ShowToast") @Override
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
			if (canCut) {
				startPhotoZoom(data.getData());
			} else {
				try {
					Uri originalUri = data.getData(); // 获得图片的uri
					String[] proj = { MediaStore.Images.Media.DATA };
					// 好像是android多媒体数据库的封装接口，具体的看Android文档
					@SuppressWarnings("deprecation")
					Cursor cursor = managedQuery(originalUri, proj, null, null,
							null);
					// 按我个人理解 这个是获得用户选择的图片的索引值
					int column_index = cursor
							.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
					// 将光标移至开头 ，这个很重要，不小心很容易引起越界
					cursor.moveToFirst();
					// 最后根据索引值获取图片路径www.2cto.com
					String path = cursor.getString(column_index);
				} catch (Exception e) {
					XLog.e(e.toString());
				}
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
//				showPhotoImage(currentType ,photo);
			}

		}

	}
	
	private void showPhotoImage(ImageType type, Bitmap photo)
	{
		if (type == ImageType.profession)
		{
			imgViewProfession.setVisibility(View.VISIBLE);
			imgViewProfession.setImageBitmap(photo);
			proPhoto = photo;
			pro64Photo = getBtye64String(photo);
			authenticate.setProPhoto(pro64Photo);
		}
		else if (type == ImageType.shenfenFront)
		{
			imgViewShenfenFront.setVisibility(View.VISIBLE);
			imgViewShenfenFront.setImageBitmap(photo);
			frontPhoto = photo;
			front64Photo = getBtye64String(photo);
			authenticate.setIdCardFontPhoto(front64Photo);
		}
		else if (type == ImageType.shenfenBack)
		{
			imgViewShenfenBack.setVisibility(View.VISIBLE);
			imgViewShenfenBack.setImageBitmap(photo);
			backPhoto = photo;
			back64Photo = getBtye64String(photo);
			authenticate.setIdCardBackPhoto(back64Photo);
		}
		else if (type == ImageType.shenfen)
		{
			imgViewShenfen.setVisibility(View.VISIBLE);
			imgViewShenfen.setImageBitmap(photo);
			fullPhoto = photo;
			full64Photo = getBtye64String(photo);
			authenticate.setIdCardPhoto(full64Photo);
		}

	}
	
	private String getBtye64String(Bitmap bitmapOrg) {
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
	
		bitmapOrg.compress(Bitmap.CompressFormat.PNG, 90, bao);
	
		byte[] ba = bao.toByteArray();
	
		String ba1 = Base64.encodeToString(ba, Base64.NO_WRAP);
		return ba1;
    }
	
	public void ObtainImage(String imagePath) {
		// TODO Auto-generated method stub
		fileName = imagePath;
		File fe = new File(Environment.getExternalStorageDirectory(), imagePath);
		Bitmap bmp = BitmapFactory.decodeFile(fe.getPath());
		showPhotoImage(currentType, bmp);
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

	@Override
	public void onChanged(WheelView wheel, int oldValue, int newValue) {
		
	}

}
