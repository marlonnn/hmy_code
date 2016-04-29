package com.BC.entertainmentgravitation;

import com.BC.entertainmentgravitation.R;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.BC.entertainment.adapter.PersonalRecycleAdapter;
import com.BC.entertainment.adapter.PersonalRecycleAdapter.OnItemClickListener;
import com.BC.entertainment.cache.InfoCache;
import com.BC.entertainment.cache.PersonalCache;
import com.BC.entertainmentgravitation.entity.EditPersonal;
import com.BC.entertainmentgravitation.entity.Personal;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.summer.activity.BaseActivity;
import com.summer.view.CircularImage;

public class PersonalActivity extends BaseActivity implements OnClickListener, OnItemClickListener{
	
	private SimpleDateFormat format;
	
	private CircularImage portrait;
	
	private Bitmap portraitBmp;
	
	private List<Personal> personals;
	
	private PersonalRecycleAdapter adapter;

	private RecyclerView infoList;
	
	private TextView txtName;
	
	/**
	 * 从后台获取的个人信息
	 */
	private EditPersonal info;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal);
		format = new SimpleDateFormat("yyyyMMddHHmmsssss");
		initView();
	}
	
	private void initView()
	{
		findViewById(R.id.imageViewBack).setOnClickListener(this);
		
		personals = PersonalCache.getInstance().GetPersonalInfos();
		info = InfoCache.getInstance().getPersonalInfo();
		
		portrait = (CircularImage) findViewById(R.id.cirImagePortrait);
		txtName = (TextView) findViewById(R.id.txtName);
		
		if (info != null && info.getNickname() != null)
		{
			txtName.setText(info.getNickname());
		}
		Glide.with(this)
		.load(InfoCache.getInstance().getPersonalInfo().getHead_portrait())
		.centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL)
		.placeholder(R.drawable.avatar_def).into(portrait);
		portrait.setOnClickListener(this);
		
		infoList = (RecyclerView)findViewById(R.id.listViewInfo);
		
		adapter = new PersonalRecycleAdapter(this, personals);
		
        adapter.notifyDataSetChanged();
        adapter.setmOnItemClickListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        infoList.setLayoutManager(linearLayoutManager);
        
        infoList.setItemAnimator(new DefaultItemAnimator());//more的动画效果
        
        infoList.setAdapter(adapter);
		
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
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

	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
		/**
		 * 修改头像
		 */
		case R.id.cirImagePortrait:
			showAlertDialog(R.layout.dialog_alert3, R.id.button3, R.id.button1, R.id.button2);
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
	public void ObtainImage(String imagePath) {
		Glide.clear(portrait);

		File fe = new File(Environment.getExternalStorageDirectory(), imagePath);
		portraitBmp = BitmapFactory.decodeFile(fe.getPath());
		portrait.setImageBitmap(portraitBmp);
	}

	@Override
	public void onItemClick(View view, int position) {
		Personal personal = (Personal)view.getTag();
		if(personal != null)
		{
			switch(personal.getResource())
			{
			/**
			 * 基本信息
			 */
			case R.drawable.activity_personal_info:
				break;
			/**
			 * 相册管理
			 */
			case R.drawable.activity_personal_album:
				break;
			/**
			 * 演艺经历
			 */
			case R.drawable.activity_personal_career:
				break;
			/**
			 * 我的经纪
			 */
			case R.drawable.activity_personal_broker:
				break;
			/**
			 * 意见反馈
			 */
			case R.drawable.activity_personal_feedback:
				break;
			/**
			 * 关于我们
			 */
			case R.drawable.activity_personal_about:
				break;
			}
		}

	}

}
