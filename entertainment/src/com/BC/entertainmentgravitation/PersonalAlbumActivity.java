package com.BC.entertainmentgravitation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.format.DateUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.BC.entertainment.adapter.PictureAdapter;
import com.BC.entertainmentgravitation.entity.Album;
import com.BC.entertainmentgravitation.entity.Photo_images;
import com.BC.entertainmentgravitation.fragment.PictureFragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.summer.ptr.PullToRefreshBase;
import com.summer.ptr.PullToRefreshBase.OnRefreshListener2;
import com.summer.ptr.PullToRefreshGridView;
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
import com.umeng.analytics.MobclickAgent;

public class PersonalAlbumActivity extends BaseActivity implements OnClickListener, OnItemClickListener{

	private Album album;

	private PullToRefreshGridView pullToRefreshGridView1;

	boolean canEdit = false;

	private ArrayList<Photo_images> more_pictures = new ArrayList<Photo_images>();//生活照
	private ArrayList<Photo_images> more_picturesImages = new ArrayList<Photo_images>();//写真
	private ArrayList<Photo_images> more_picturesPhotographs = new ArrayList<Photo_images>();//剧照
	
	private CommonAdapter<Photo_images> adapter1;
	private CommonAdapter<Photo_images> adapter2;
	private CommonAdapter<Photo_images> adapter3;
	private RadioGroup radio;
	private int pageIndex = 1;
	private int index = 1;
	
	private String clientId;

	private PictureAdapter adapter;
	
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
		try {
			Intent intent = this.getIntent();
			clientId = (String)intent.getSerializableExtra("clientId");
		} catch (Exception e) {
			e.printStackTrace();
		}
		initView();
		sendAlbumRequest();
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
	
	private void initView()
	{
		findViewById(R.id.imageViewBack).setOnClickListener(this);
		pullToRefreshGridView1 = (PullToRefreshGridView)findViewById(R.id.pullToRefreshGridView1);
		pullToRefreshGridView1.getRefreshableView().setNumColumns(3);
		pullToRefreshGridView1.getRefreshableView().setVerticalSpacing(15);
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
	
	private void showImageDialog(final PictureAdapter adapter, int position) {
		final PictureFragment fragment = new PictureFragment(position);
		fragment.setStyle(R.style.Dialog, DialogFragment.STYLE_NO_FRAME);
		fragment.show(getSupportFragmentManager(), "PictureDialog");
		fragment.setAdapter(adapter);
		fragment.setChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				if (adapter.getCount() - 1 == arg0) {
					index++;
					sendAlbumRequest(index);
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
	}
	
	/**
	 * 获取相册信息
	 */
	private void sendAlbumRequest(int pageIndex) {
		HashMap<String, String> entity = new HashMap<String, String>();

		entity.put("clientID", Config.User.getClientID());
		entity.put("The_page_number", "" + pageIndex);
		
		List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
		addToThreadPool(Config.photo_album_management, "send album info request", params);
	}
	
	private void initAdapter() {
		adapter1 = new CommonAdapter<Photo_images>(this,
				R.layout.activity_personal_album_item, more_pictures) {

			@Override
			public void convert(ViewHolder helper, final Photo_images item, final int position) {

				ImageView imageView = helper.getView(R.id.Picture_address);
				if (item.getPicture_address() != null) {
					Glide.with(PersonalAlbumActivity.this).load(item.getPicture_address())
					.centerCrop()
					.diskCacheStrategy(DiskCacheStrategy.ALL)
					.placeholder(R.drawable.home_image).into(imageView);
					imageView.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							adapter = new PictureAdapter(more_pictures, mContext);
							showImageDialog(adapter, position);
						}
					});
				}
			}
		};
		adapter2 = new CommonAdapter<Photo_images>(PersonalAlbumActivity.this,
				R.layout.activity_personal_album_item, more_picturesImages) {

			@Override
			public void convert(ViewHolder helper, final Photo_images item, final int position) {

				ImageView imageView = helper.getView(R.id.Picture_address);
				if (item.getPicture_address() != null) {
					Glide.with(PersonalAlbumActivity.this).load(item.getPicture_address())
					.centerCrop()
					.diskCacheStrategy(DiskCacheStrategy.ALL)
					.placeholder(R.drawable.home_image).into(imageView);
					imageView.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							adapter = new PictureAdapter(more_picturesImages, mContext);
							showImageDialog(adapter, position);
						}
					});
				}
			}
		};
		adapter3 = new CommonAdapter<Photo_images>(this,
				R.layout.activity_personal_album_item, more_picturesPhotographs) {

			@Override
			public void convert(ViewHolder helper, final Photo_images item, final int position) {

				ImageView imageView = helper.getView(R.id.Picture_address);
				if (item.getPicture_address() != null) {
					Glide.with(PersonalAlbumActivity.this).load(item.getPicture_address())
					.centerCrop()
					.diskCacheStrategy(DiskCacheStrategy.ALL)
					.placeholder(R.drawable.home_image).into(imageView);
					imageView.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							adapter = new PictureAdapter(more_picturesPhotographs, mContext);
							showImageDialog(adapter, position);
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
			String time = DateUtils.formatDateTime(PersonalAlbumActivity.this,
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
			String time = DateUtils.formatDateTime(PersonalAlbumActivity.this,
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
		if (clientId == null || clientId.length() == 0) {
			ToastUtil.show(this, "无法获取信息");
			return;
		}
		HashMap<String, String> entity = new HashMap<String, String>();

		entity.put("clientID", clientId);
		
		ShowProgressDialog("获取用户相册...");		
		List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
		addToThreadPool(Config.photo_album_management, "send album request", params);
	}

	
    private void addToThreadPool(int taskType, String Tag, List<NameValuePair> params)
    {
    	HttpBaseTask httpTask = new HttpBaseTask(ThreadPoolConst.THREAD_TYPE_FILE_HTTP, Tag, params, UrlUtil.GetUrl(taskType));
    	httpTask.setTaskType(taskType);
    	InfoHandler handler = new InfoHandler(this);
    	httpTask.setInfoHandler(handler);
    	ThreadPoolFactory.getThreadPoolManager().addTask(httpTask);
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
