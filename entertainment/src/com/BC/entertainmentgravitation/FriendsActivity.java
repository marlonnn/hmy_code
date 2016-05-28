package com.BC.entertainmentgravitation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts.Phones;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.BC.entertainmentgravitation.R;
import com.BC.entertainmentgravitation.dialog.PromptDialog;
import com.BC.entertainmentgravitation.entity.Friend;
import com.BC.entertainmentgravitation.entity.Friends;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.netease.nim.uikit.common.ui.ptr.PullToRefreshBase;
import com.netease.nim.uikit.common.ui.ptr.PullToRefreshBase.OnRefreshListener2;
import com.netease.nim.uikit.common.ui.ptr.PullToRefreshGridView;
import com.summer.activity.BaseActivity;
import com.summer.adapter.CommonAdapter;
import com.summer.config.Config;
import com.summer.factory.ThreadPoolFactory;
import com.summer.handler.InfoHandler;
import com.summer.json.Entity;
import com.summer.task.HttpBaseTask;
import com.summer.treadpool.ThreadPoolConst;
import com.summer.utils.JsonUtil;
import com.summer.utils.ToastUtil;
import com.summer.utils.UrlUtil;
import com.umeng.analytics.MobclickAgent;

public class FriendsActivity extends BaseActivity implements
		OnItemClickListener {

	Friends friends;
	Button button2, button3, button4, addButton1;
	View add, give;

	PullToRefreshGridView pullToRefreshGridView1;

	private CommonAdapter<Friend> adapter1;
	private int pageIndex = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hao_you);
		init();
		sendReqAlbum();
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
	
	private void init() {
		// TODO Auto-generated method stub
		add = findViewById(R.id.add);
		give = findViewById(R.id.give);
		button2 = (Button) findViewById(R.id.button2);
		button3 = (Button) findViewById(R.id.button3);
		button4 = (Button) findViewById(R.id.button4);
		addButton1 = (Button) findViewById(R.id.addButton1);

		pullToRefreshGridView1 = (PullToRefreshGridView) findViewById(R.id.pullToRefreshGridView1);
		pullToRefreshGridView1.getRefreshableView().setNumColumns(2);
		pullToRefreshGridView1.setOnRefreshListener(refreshListener);
		initAdapter();
		pullToRefreshGridView1.setAdapter(adapter1);
		pullToRefreshGridView1.setOnItemClickListener(this);

		pullToRefreshGridView1.getRefreshableView().setHorizontalSpacing(1);
		pullToRefreshGridView1.getRefreshableView().setVerticalSpacing(1);
		add.setVisibility(View.GONE);
		button2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				add.setVisibility(View.VISIBLE);
			}
		});
		button3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sendReqAddFriendsAlbum();
				add.setVisibility(View.GONE);
			}
		});
		button4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				add.setVisibility(View.GONE);
			}
		});
		addButton1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivityForResult(new Intent(Intent.ACTION_PICK,
						ContactsContract.Contacts.CONTENT_URI), 11);
			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 11:
			if (resultCode == Activity.RESULT_OK) {
				if (data == null) {
					return;
				}
				String phoneNumber = null;
				Uri contactData = data.getData();
				if (contactData == null) {
					return;
				}
				Cursor cursor = managedQuery(contactData, null, null, null,
						null);
				if (cursor.moveToFirst()) {
					// String name = cursor.getString(cursor
					// .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
					String hasPhone = cursor
							.getString(cursor
									.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
					String id = cursor.getString(cursor
							.getColumnIndex(ContactsContract.Contacts._ID));
					if (hasPhone.equalsIgnoreCase("1")) {
						hasPhone = "true";
					} else {
						hasPhone = "false";
					}
					if (Boolean.parseBoolean(hasPhone)) {
						Cursor phones = getContentResolver()
								.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
										null,
										ContactsContract.CommonDataKinds.Phone.CONTACT_ID
												+ " = " + id, null, null);
						while (phones.moveToNext()) {
							phoneNumber = phones
									.getString(phones
											.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
							setTitle(phoneNumber);
						}
						phones.close();
					}
				}
				setText(R.id.editText1, phoneNumber);
				// Uri contactData = data.getData();
				// Log.d("shuzhi", "" + contactData);
				// Cursor cursor = getContentResolver().query(contactData, null,
				// null, null, null);
				// Log.d("shuzhi", "" + cursor.getColumnCount());
				//
				// if (cursor.moveToFirst()) {
				// String name = cursor.getString(cursor
				// .getColumnIndex(Contacts.DISPLAY_NAME));
				// System.out.println(name);
				// setText(R.id.editText1, name);
				// }

			}

			break;
		}

	}

	// setText(R.id.editText1, name);
	private void initAdapter() {
		// TODO Auto-generated method stub
		adapter1 = new CommonAdapter<Friend>(this, R.layout.item_list_friend,
				new ArrayList<Friend>()) {

			@Override
			public void convert(ViewHolder helper, final Friend item, int position) {

				helper.setText(R.id.The_name_of_the, item.getThe_name_of_the());
				helper.setText(R.id.level, "Lv." + item.getLevel());

				ImageView imageView = helper.getView(R.id.Head_portrait);
				Glide.with(FriendsActivity.this).load(item.getHead_portrait())
						.centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL)
						.placeholder(R.drawable.home_image).into(imageView);
				helper.getView(R.id.editButton).setOnClickListener(
						new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								give.setVisibility(View.VISIBLE);
								give.findViewById(R.id.button6)
										.setOnClickListener(
												new OnClickListener() {

													@Override
													public void onClick(View v) {
														// TODO Auto-generated
														// method stub
														sendReqGiveFriends(
																item.getA_friend_ID(),
																getTextViewContent(R.id.give1));
														give.setVisibility(View.GONE);
													}
												});
								give.findViewById(R.id.button7)
										.setOnClickListener(
												new OnClickListener() {

													@Override
													public void onClick(View v) {
														// TODO Auto-generated
														// method stub
														give.setVisibility(View.GONE);
													}
												});
							}
						});
			}
		};
	}

	public void initPersonalInformation() {
		// TODO Auto-generated method stub
		if (friends == null) {
			ToastUtil.show(this, "获取数据失败");
			return;
		}
		if (pageIndex == 1) {// 第一页时，先清空数据集
			adapter1.clearAll();
		}
		pageIndex++;
		adapter1.add(friends.getFriends());

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
	 * 获取数据
	 */
	private void sendReqAlbum() {
		if (Config.User == null) {
			ToastUtil.show(this, "无法获取信息");
			return;
		}
		HashMap<String, String> entity = new HashMap<String, String>();

		entity.put("clientID", Config.User.getClientID());

		ShowProgressDialog("获取数据...");
		List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
		addToThreadPool(Config.friends_list, "send delete album request", params);
	}

	/**
	 * 添加好友
	 */
	private void sendReqAddFriendsAlbum() {
		if (Config.User == null) {
			ToastUtil.show(this, "无法获取信息");
			return;
		}
		HashMap<String, String> entity = new HashMap<String, String>();

		entity.put("clientID", Config.User.getClientID());
		entity.put("Mobile_phone_no", getTextViewContent(R.id.editText1)
				.replace(" ", ""));
		ShowProgressDialog("获取数据...");
		List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
		addToThreadPool(Config.add_buddy, "send delete album request", params);
	}
	
	public String getTextViewContent(int id) {
		String s = new String();
		TextView t = (TextView) findViewById(id);
		s = t.getText().toString();
		return s;
	}

	/**
	 * 赠送娱币
	 */
	private void sendReqGiveFriends(String fID, String Given_the_amount_of) {
		if (Config.User == null) {
			ToastUtil.show(this, "无法获取信息");
			return;
		}
		HashMap<String, String> entity = new HashMap<String, String>();

		entity.put("clientID", Config.User.getClientID());
		entity.put("The_donee_ID", fID);
		entity.put("Given_the_amount_of", "" + Given_the_amount_of);

		ShowProgressDialog("获取数据...");
		List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
		addToThreadPool(Config.give, "send delete album request", params);
	}

	OnRefreshListener2<GridView> refreshListener = new OnRefreshListener2<GridView>() {

		@Override
		public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
			// 下拉刷新
			String time = DateUtils.formatDateTime(FriendsActivity.this,
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
			// 调用数据

			// sendReq(pageIndex);
			sendReqAlbum();

		}

		@Override
		public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
			// 上拉翻页
			String time = DateUtils.formatDateTime(FriendsActivity.this,
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
			sendReqAlbum();
		}

	};

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Friend friend = friends.getFriends().get(position);
		if (friend.getPermission().equals("2")) {
			Intent intent = new Intent(this, DetailsActivity.class);
			intent.putExtra("userID", friend.getA_friend_ID());
			startActivity(intent);
		} else {
//			Intent intent = new Intent(this, Details4UserActivity.class);
//			intent.putExtra("userID", friend.getA_friend_ID());
//			startActivity(intent);
		}

	}

	@Override
	public void onInfoReceived(int errcode, HashMap<String, Object> items) {
		// TODO Auto-generated method stub
		super.onInfoReceived(errcode, items);
		pullToRefreshGridView1.onRefreshComplete();
	}

	@Override
	public void RequestFailed(int errcode, String message, int taskType) {
		if (taskType == Config.add_buddy && errcode != 0
				&& "用户不存在！".equals(message)) {
			showWarningDialog("",
					getTextViewContent(R.id.editText1).replace(" ", ""));
		}

	}

	private void showWarningDialog(String title, String message) {
		// TODO Auto-generated method stub
		final PromptDialog.Builder builder = new PromptDialog.Builder(this);
		builder.setTitle(title);
		builder.setMessage(message + "和您还不是好友，是否邀请。");
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				Uri smsToUri = Uri.parse("smsto:"
						+ getTextViewContent(R.id.editText1).replace(" ", ""));
				Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
				intent.putExtra(
						"sms_body",
						"我发现了一个好玩的客户端，里面有我们最喜欢的明星，赶快下载注册吧。http://120.25.107.200/php/DOWNLOAD/EntertainmentGravitation.apk    ,记得填上我的分享码  "
								+ Config.User.getShareCode() + "  有惊喜哦");
				startActivity(intent);
			}
		});
		PromptDialog dialog = builder.create();

		dialog.show();

	}

	@Override
	public void RequestSuccessful(String jsonString, int taskType) {
		Gson gson = new Gson();
		switch (taskType) {
		case Config.friends_list:
			Entity<Friends> baseEntity5 = gson.fromJson(jsonString,
					new TypeToken<Entity<Friends>>() {
					}.getType());
			friends = baseEntity5.getData();
			if (friends != null) {
				initPersonalInformation();
			} else {
				ToastUtil.show(FriendsActivity.this, "获取数据失败");
			}
			break;
		case Config.add_buddy:
			ToastUtil.show(FriendsActivity.this, "添加成功");
			sendReqAlbum();
			break;
		}
		
	}
}

