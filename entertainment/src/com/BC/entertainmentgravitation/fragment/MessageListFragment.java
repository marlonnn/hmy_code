package com.BC.entertainmentgravitation.fragment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.BC.entertainmentgravitation.R;
import com.BC.entertainment.cache.InfoCache;
import com.BC.entertainmentgravitation.entity.MessageItem;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.netease.nim.uikit.common.ui.ptr.PullToRefreshBase;
import com.netease.nim.uikit.common.ui.ptr.PullToRefreshBase.OnRefreshListener2;
import com.netease.nim.uikit.common.ui.ptr.PullToRefreshListView;
import com.summer.adapter.CommonAdapter;
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

public class MessageListFragment extends BaseFragment implements
		OnItemClickListener, OnClickListener {

	public interface OnSelectMessageItem {
		public void selectMessageItem(MessageItem messageItem);
	}

	private OnSelectMessageItem onSelectMessageItem;

	Activity activity;
	View contentView, editConnect;
	PullToRefreshListView pullToRefreshListView1;

	MessageItem connect;
	List<MessageItem> connects;
	private CommonAdapter<MessageItem> adapter;
	// private RadioGroupLayout radio;
	private int pageIndex = 1;
	
	protected final int NONE = 0;
	
	protected final int PHOTO_GRAPH = 1;// 拍照
	
	protected final int PHOTO_ZOOM = 2; // 相册
	
	protected final int PHOTO_RESOULT = 3;// 结果
	
	protected boolean canCut = true;// 是否裁剪
	
	protected final String IMAGE_UNSPECIFIED = "image/*";
	
	protected String IMAGE_FILE = "";
	
	private SimpleDateFormat format;

	public List<MessageItem> getConnects() {
		return connects;
	}

	public void setConnects(List<MessageItem> connects) {
		this.connects = connects;
	}

	public MessageItem getConnect() {
		return connect;
	}

	public void setConnect(MessageItem connect) {
		this.connect = connect;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		format = new SimpleDateFormat("yyyyMMddHHmmsssss");
		activity = getActivity();
		contentView = inflater.inflate(R.layout.fragment_message_list, null);
		return contentView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		init();
		sendReqConnect();
		super.onViewCreated(view, savedInstanceState);
	}

	private void init() {
		// TODO Auto-generated method stub

		pullToRefreshListView1 = (PullToRefreshListView) contentView
				.findViewById(R.id.pullToRefreshListView1);
		pullToRefreshListView1.setOnRefreshListener(refreshListener);
		adapter = new CommonAdapter<MessageItem>(activity,
				R.layout.item_list_message, new ArrayList<MessageItem>()) {

			@Override
			public void convert(ViewHolder helper, final MessageItem item) {
				// helper.setText(R.id.The_picture, item.getThe_picture() + "");
				helper.setText(R.id.describe, item.getDescribe());
				helper.setText(R.id.star_nmae, item.getStar_nmae());
				// helper.setText(R.id.payment, item.getPayment() ? "是" : "否");
				ImageView imageView = helper.getView(R.id.Head_portrait);
				Glide.with(activity)
						.load(item.getHead_portrait() == null ? "" : item
								.getHead_portrait()).centerCrop()
						.diskCacheStrategy(DiskCacheStrategy.ALL)
						.placeholder(R.drawable.home_image)
						.into(imageView);

				imageView = helper.getView(R.id.If_there_is_a_picture);
				if (item.getIf_there_is_a_picture() == null
						|| item.getIf_there_is_a_picture().equals("")) {
					imageView.setVisibility(View.GONE);
				} else {
					imageView.setVisibility(View.VISIBLE);
				}
			}
		};
		pullToRefreshListView1.setAdapter(adapter);
		pullToRefreshListView1.setOnItemClickListener(this);

	}

	public void initPersonalInformation() {
		// TODO Auto-generated method stub
		if (connects == null) {
			return;
		}
		if (pageIndex == 1) {// 第一页时，先清空数据集
			adapter.clearAll();
		}
		pageIndex++;
		adapter.add(connects);
		MessageItem item = connects.get(0);
		if (onSelectMessageItem != null) {
			onSelectMessageItem.selectMessageItem(item);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		showAlertDialog(R.layout.dialog_alert3, R.id.button3, R.id.button1,
				R.id.button2);
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

	OnRefreshListener2<ListView> refreshListener = new OnRefreshListener2<ListView>() {

		@Override
		public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
			// 下拉刷新
			String time = DateUtils.formatDateTime(activity,
					System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
							| DateUtils.FORMAT_SHOW_DATE
							| DateUtils.FORMAT_ABBREV_ALL);
			pullToRefreshListView1.getLoadingLayoutProxy().setRefreshingLabel(
					"正在刷新");
			pullToRefreshListView1.getLoadingLayoutProxy().setPullLabel("下拉刷新");
			pullToRefreshListView1.getLoadingLayoutProxy().setReleaseLabel(
					"释放开始刷新");
			refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(
					"最后更新时间:" + time);
			pageIndex = 1;
			// 调用数据

			// sendReq(pageIndex);
			sendReqConnect();

		}

		@Override
		public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
			// 上拉翻页
			String time = DateUtils.formatDateTime(activity,
					System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
							| DateUtils.FORMAT_SHOW_DATE
							| DateUtils.FORMAT_ABBREV_ALL);
			pullToRefreshListView1.getLoadingLayoutProxy().setRefreshingLabel(
					"正在加载");
			pullToRefreshListView1.getLoadingLayoutProxy().setPullLabel("上拉翻页");
			pullToRefreshListView1.getLoadingLayoutProxy().setReleaseLabel(
					"释放开始加载");
			refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(
					"最后加载时间:" + time);
			// 调用数据
			sendReqConnect();
		}
	};

	/**
	 * 获取外部链接信息
	 */
	private void sendReqConnect() {
		if (InfoCache.getInstance().getStartInfo() == null) {
			ToastUtil.show(activity, "无法获取信息");
			return;
		}
		HashMap<String, String> entity = new HashMap<String, String>();

		entity.put("Star_ID", InfoCache.getInstance().getStartInfo().getStar_ID());

		ShowProgressDialog("获取外部链接信息...");
		List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
		addToThreadPool(Config.stars_release_information_list, "send search request", params);
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
	public void onInfoReceived(int errcode, HashMap<String, Object> items) {
		// TODO Auto-generated method stub
		super.onInfoReceived(errcode, items);
		pullToRefreshListView1.onRefreshComplete();
	}

	@Override
	public void RequestSuccessful(String jsonString, int taskType) {
		Gson gson = new Gson();
		switch (taskType) {
		case Config.stars_release_information_list:
			Entity<ArrayList<MessageItem>> baseEntity4 = gson.fromJson(
					jsonString,
					new TypeToken<Entity<ArrayList<MessageItem>>>() {
					}.getType());
			ArrayList<MessageItem> connects = baseEntity4.getData();
			if (connects != null) {
				if (connects.size() > 0) {
					setConnects(connects);
					initPersonalInformation();
				} else {
					ToastUtil.show(activity, "没有消息");
				}

			} else {
				ToastUtil.show(activity, "获取数据失败");
			}
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		MessageItem item = adapter.getItem(position - 1);
		if (onSelectMessageItem != null) {
			onSelectMessageItem.selectMessageItem(item);
		}
	}

	public void setOnSelectMessageItem(MessageCommentFragment commentFragment) {
		this.onSelectMessageItem = commentFragment;
	}
}
