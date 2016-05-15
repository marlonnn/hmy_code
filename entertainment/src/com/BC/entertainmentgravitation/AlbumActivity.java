package com.BC.entertainmentgravitation;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.BC.entertainmentgravitation.entity.Album;
import com.BC.entertainmentgravitation.entity.Photo_images;
import com.netease.nim.uikit.common.ui.ptr.PullToRefreshBase;
import com.netease.nim.uikit.common.ui.ptr.PullToRefreshBase.OnRefreshListener2;
import com.netease.nim.uikit.common.ui.ptr.PullToRefreshGridView;
import com.summer.activity.BaseActivity;
import com.summer.adapter.CommonAdapter;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class AlbumActivity extends BaseActivity implements OnClickListener, OnItemClickListener{

	private Album album;

	PullToRefreshGridView pullToRefreshGridView1;

	boolean canEdit = false;
	String fileName = "";

	ArrayList<Photo_images> more_pictures = new ArrayList<Photo_images>();//生活照
	ArrayList<Photo_images> more_picturesImages = new ArrayList<Photo_images>();//写真
	ArrayList<Photo_images> more_picturesPhotographs = new ArrayList<Photo_images>();//剧照

	private CommonAdapter<Photo_images> adapter1;
	private CommonAdapter<Photo_images> adapter2;
	private CommonAdapter<Photo_images> adapter3;
	private RadioGroup radio;
	private int pageIndex = 1;
	private String setType = "1";
	
	private SimpleDateFormat format;
	
	public Album getAlbum() {
		return album;
	}

	public void setAlbum(Album album) {
		this.album = album;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal_album);
		initView();
		sendAlbumRequest();
	}
	
	private void initView()
	{
		format = new SimpleDateFormat("yyyyMMddHHmmsssss");
		findViewById(R.id.imageViewBack).setOnClickListener(this);
		pullToRefreshGridView1 = (PullToRefreshGridView)findViewById(R.id.pullToRefreshGridView1);
		pullToRefreshGridView1.getRefreshableView().setNumColumns(3);
		pullToRefreshGridView1.setOnRefreshListener(refreshListener);
		initAdapter();
		pullToRefreshGridView1.setAdapter(adapter1);
		pullToRefreshGridView1.setOnItemClickListener(this);

		radio = (RadioGroup) findViewById(R.id.radioGroup1);
		radio.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.radio0:
					pullToRefreshGridView1.setAdapter(adapter1);
					break;
				case R.id.radio1:
					pullToRefreshGridView1.setAdapter(adapter2);
					break;
				case R.id.radio2:
					pullToRefreshGridView1.setAdapter(adapter3);
					break;

				}
			}
		});
	}
	
	private void initAdapter() {
		// TODO Auto-generated method stub
		adapter1 = new CommonAdapter<Photo_images>(this,
				R.layout.activity_album_item_list_album, more_pictures) {

			@Override
			public void convert(ViewHolder helper, final Photo_images item) {

				ImageView imageView = helper.getView(R.id.Picture_address);
				if (item.getPicture_address() == null) {
					Glide.clear(imageView);
					imageView.setImageResource(R.drawable.add_image);
					imageView.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							showAlertDialog(R.layout.dialog_alert3,
									R.id.button3, R.id.button1, R.id.button2);
							setType = "1";
						}
					});
				} else {
					Glide.with(AlbumActivity.this).load(item.getPicture_address())
							.centerCrop()
							.diskCacheStrategy(DiskCacheStrategy.ALL)
							.placeholder(R.drawable.home_image).into(imageView);
					imageView.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							// item.getPicture_address()
							showAlertDialog1(R.layout.dialog_alert6,
									R.id.button3, R.id.button1, R.id.button2, item.getPicture_ID());
						}
					});
				}
			}
		};
		adapter2 = new CommonAdapter<Photo_images>(AlbumActivity.this,
				R.layout.activity_album_item_list_album, more_picturesImages) {

			@Override
			public void convert(ViewHolder helper, final Photo_images item) {

				ImageView imageView = helper.getView(R.id.Picture_address);
				if (item.getPicture_address() == null) {
					Glide.clear(imageView);
					imageView.setImageResource(R.drawable.add_image);
					imageView.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							showAlertDialog(R.layout.dialog_alert3,
									R.id.button3, R.id.button1, R.id.button2 );
							setType = "2";
						}
					});
				} else {
					Glide.with(AlbumActivity.this).load(item.getPicture_address())
							.centerCrop()
							.diskCacheStrategy(DiskCacheStrategy.ALL)
							.placeholder(R.drawable.home_image).into(imageView);
					imageView.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							showAlertDialog1(R.layout.dialog_alert6,
									R.id.button3, R.id.button1, R.id.button2,item.getPicture_ID());
						}
					});
				}
			}
		};
		adapter3 = new CommonAdapter<Photo_images>(this,
				R.layout.activity_album_item_list_album, more_picturesPhotographs) {

			@Override
			public void convert(ViewHolder helper, final Photo_images item) {

				ImageView imageView = helper.getView(R.id.Picture_address);
				if (item.getPicture_address() == null) {
					Glide.clear(imageView);
					imageView.setImageResource(R.drawable.add_image);
					imageView.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							showAlertDialog(R.layout.dialog_alert3,
									R.id.button3, R.id.button1, R.id.button2);
						}
					});
					setType = "3";
				} else {
					Glide.with(AlbumActivity.this).load(item.getPicture_address())
							.centerCrop()
							.diskCacheStrategy(DiskCacheStrategy.ALL)
							.placeholder(R.drawable.home_image).into(imageView);
					imageView.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							showAlertDialog1(R.layout.dialog_alert6,
									R.id.button3, R.id.button1, R.id.button2, item.getPicture_ID());
						}
					});
				}

			}
		};
	}
	
	OnRefreshListener2<GridView> refreshListener = new OnRefreshListener2<GridView>() {

		@Override
		public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
			// 下拉刷新
			String time = DateUtils.formatDateTime(AlbumActivity.this,
					System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
							| DateUtils.FORMAT_SHOW_DATE
							| DateUtils.FORMAT_ABBREV_ALL);
			pullToRefreshGridView1.getLoadingLayoutProxy().setRefreshingLabel(
					"正在刷新");
			pullToRefreshGridView1.getLoadingLayoutProxy().setPullLabel("下拉刷新");
			pullToRefreshGridView1.getLoadingLayoutProxy().setReleaseLabel(
					"释放开始刷新");
			refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(
					"最后更新时间:" + time);
			pageIndex = 1;

			sendAlbumRequest();

		}

		@Override
		public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
			// 上拉翻页
			String time = DateUtils.formatDateTime(AlbumActivity.this,
					System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
							| DateUtils.FORMAT_SHOW_DATE
							| DateUtils.FORMAT_ABBREV_ALL);
			pullToRefreshGridView1.getLoadingLayoutProxy().setRefreshingLabel(
					"正在加载");
			pullToRefreshGridView1.getLoadingLayoutProxy().setPullLabel("上拉翻页");
			pullToRefreshGridView1.getLoadingLayoutProxy().setReleaseLabel(
					"释放开始加载");
			refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(
					"最后加载时间:" + time);
			// 调用数据
			sendAlbumRequest();
		}

	};
	
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
		entity.put("Image_type", setType);
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
	
    private void addToThreadPool(int taskType, String Tag, List<NameValuePair> params)
    {
    	HttpBaseTask httpTask = new HttpBaseTask(ThreadPoolConst.THREAD_TYPE_FILE_HTTP, Tag, params, UrlUtil.GetUrl(taskType));
    	httpTask.setTaskType(taskType);
    	InfoHandler handler = new InfoHandler(this);
    	httpTask.setInfoHandler(handler);
    	ThreadPoolFactory.getThreadPoolManager().addTask(httpTask);
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
	
	private void showAlertDialog1(int layoutId, int noId, int phoneImageId,
			int takePictureImageId, final String image) {
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
						// TODO Auto-generated method stub
						deletepicture(image);
						ad.dismiss();
					}
				});
		window.findViewById(takePictureImageId).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						
						sendReqSaveUser(image);
						ad.dismiss();
					}
				});
	}
	
	/**
	 * 删除照片
	 */
	private void deletepicture(String image) {
		if (Config.User == null) {
			ToastUtil.show(this, "无法获取信息");
			return;
		}
		HashMap<String, String> entity = new HashMap<String, String>();
		
		entity.put("clientID", ""+ Config.User.getClientID());
		entity.put("pictureID", ""+image);
		
		ShowProgressDialog("提交基本信息...");		
		List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
		addToThreadPool(Config.delete_picture, "send delete album request", params);
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
	
	public void initPersonalInformation() {
		// TODO Auto-generated method stub
		if (album == null) {
			ToastUtil.show(this, "获取数据失败");
			return;
		}
		if (pageIndex == 1) {// 第一页时，先清空数据集
			more_pictures.clear();
			more_picturesImages.clear();
			more_picturesPhotographs.clear();

			adapter1.clearAll();
			adapter2.clearAll();
			adapter3.clearAll();

			more_pictures.add(new Photo_images());
			more_picturesImages.add(new Photo_images());
			more_picturesPhotographs.add(new Photo_images());
		}

		more_pictures
				.addAll(more_pictures.size() - 1, album.getMore_pictures());
		more_picturesImages.addAll(more_picturesImages.size() - 1,
				album.getPhoto_images());
		more_picturesPhotographs.addAll(more_picturesPhotographs.size() - 1,
				album.getPhotographs());

		pageIndex++;

		adapter1.setList(more_pictures);
		adapter2.setList(more_picturesImages);
		adapter3.setList(more_picturesPhotographs);
	}
	
	@Override
	public void ObtainImage(String imagePath) {
		// TODO Auto-generated method stub
		fileName = imagePath;
		File fe = new File(Environment.getExternalStorageDirectory(), imagePath);
		Bitmap bmp = BitmapFactory.decodeFile(fe.getPath());
		sendReqSaveAlbum(bmp);
	}
	
	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		/**
		 * 返回键
		 */
		case R.id.imageViewBack:
			finish();
			break;
		}
		

	}
	
	@Override
	public void onInfoReceived(int errorCode, HashMap<String, Object> items) {
		super.onInfoReceived(errorCode, items);
		pullToRefreshGridView1.onRefreshComplete();
	}

	@Override
	public void RequestSuccessful(String jsonString, int taskType) {
		Gson gson = new Gson();
		switch (taskType) {
		case Config.edit_photo_albums:
			ToastUtil.show(this, "保存成功");
			pageIndex = 1;
			sendAlbumRequest();
			break;
		case Config.photo_album_management:
			// if (MainActivity.user.getPermission().equals("2")) {
			Entity<Album> baseEntity5 = gson.fromJson(jsonString,
					new TypeToken<Entity<Album>>() {
					}.getType());
			Album album = baseEntity5.getData();
			if (album != null) {
				setAlbum(album);
				initPersonalInformation();
				XLog.i("success get album: ");
			} else {
				ToastUtil.show(this, "获取数据失败");
			}

			break;
		case Config.image:
			ToastUtil.show(this, "设置成功");
			break;
		case Config.delete_picture:
			pageIndex = 1;
			sendAlbumRequest();
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
	}

}
