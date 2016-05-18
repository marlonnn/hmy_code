package com.BC.entertainmentgravitation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.BC.entertainmentgravitation.dialog.ApplauseGiveConcern;
import com.BC.entertainmentgravitation.dialog.PromptDialog;
import com.BC.entertainmentgravitation.entity.Ranking;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.netease.nim.uikit.common.ui.ptr.PullToRefreshBase;
import com.netease.nim.uikit.common.ui.ptr.PullToRefreshBase.OnRefreshListener2;
import com.netease.nim.uikit.common.ui.ptr.PullToRefreshListView;
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

public class StarLevelActivity extends BaseActivity implements OnClickListener , OnItemClickListener{
	
	private PullToRefreshListView pullToRefreshListView1;
	private RadioGroup radioGroup1;
	private int type = 1;
	private List<Ranking> ranking;
	private ApplauseGiveConcern applauseGC;

	private CommonAdapter<Ranking> adapter;
	// private RadioGroupLayout radio;
	private int pageIndex = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_level);
		init();
		sendReqConnect();
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
		radioGroup1 = (RadioGroup) findViewById(R.id.radioGroup1);
		pullToRefreshListView1 = (PullToRefreshListView) findViewById(R.id.pullToRefreshListView1);
		pullToRefreshListView1.setOnRefreshListener(refreshListener);
		adapter = new CommonAdapter<Ranking>(this, R.layout.item_list_ranking,
				new ArrayList<Ranking>()) {

			@Override
			public void convert(ViewHolder helper, final Ranking item) {
				// helper.setText(R.id.The_picture, item.getThe_picture() + "");
				helper.setText(R.id.Star_names, item.getStar_names());

				ImageView imageView = helper.getView(R.id.Head_portrait);
				Glide.with(StarLevelActivity.this).load(item.getHead_portrait())
						.centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL)
						.placeholder(R.drawable.home_image)
						.into(imageView);
				ImageView imageView2 = helper.getView(R.id.rankingImage);
				TextView textView = helper.getView(R.id.rankingText);
				imageView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(v.getContext(),
								DetailsActivity.class);
						intent.putExtra("userID", item.getStar_ID());
						startActivity(intent);
					}
				});

				switch (helper.getPosition()) {
				case 0:
					imageView2.setVisibility(View.VISIBLE);
					textView.setVisibility(View.GONE);
					imageView2.setImageResource(R.drawable.icon_1);
					break;
				case 1:
					imageView2.setVisibility(View.VISIBLE);
					textView.setVisibility(View.GONE);
					imageView2.setImageResource(R.drawable.icon_2);
					break;
				case 2:
					imageView2.setVisibility(View.VISIBLE);
					textView.setVisibility(View.GONE);
					imageView2.setImageResource(R.drawable.icon_3);
					break;

				default:
					imageView2.setVisibility(View.GONE);
					textView.setVisibility(View.VISIBLE);
					textView.setText((helper.getPosition() + 1) + "");
					break;
				}

				final ApplauseGiveConcern applauseGiveConcern = new ApplauseGiveConcern(
						mContext, item.getStar_ID(), StarLevelActivity.this,
						item.getThe_current_hooted_thumb_up_prices(),
						item.getStar_names());
				/**
				 * 鼓掌
				 */
				View applause = helper.getView(R.id.applause);
				if (applause != null) {
					applause.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							applauseGiveConcern.showApplaudDialog(1);
							applauseGC = applauseGiveConcern;
						}
					});
				}

				/**
				 * 踢红包
				 */
				View bigbird = helper.getView(R.id.bigbird);
				if (bigbird != null) {
					bigbird.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							applauseGiveConcern.showApplaudDialog(2);
							applauseGC = applauseGiveConcern;
						}
					});
				}

				/**
				 * 关注
				 */
				View FocusOn = helper.getView(R.id.FocusOn);
				if (FocusOn != null) {
					FocusOn.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							applauseGiveConcern.sendFocusRequest();
							applauseGC = applauseGiveConcern;
						}
					});
				}

			}

		};
		pullToRefreshListView1.setAdapter(adapter);
		pullToRefreshListView1.setOnItemClickListener(this);
		radioGroup1.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub

				pageIndex = 1;
				switch (checkedId) {
				case R.id.radio0:
					type = 1;
					sendReqConnect();
					break;
				case R.id.radio1:
					type = 2;
					sendReqConnect();
					break;
				case R.id.radio2:
					type = 3;
					sendReqConnect();
					break;

				}
			}
		});
	}

	public void initPersonalInformation() {
		// TODO Auto-generated method stub
		if (ranking == null) {
			// ToastUtil.show(activity, "获取数据失败");
			return;
		}
		// if (arrayList != null && arrayList.size() > 0) {
		if (pageIndex == 1) {// 第一页时，先清空数据集
			adapter.clearAll();
		}
		pageIndex++;
		adapter.add(ranking);
		// findViewById(R.id.noContent).setVisibility(View.GONE);
		// } else if (pageIndex == 1) {
		// findViewById(R.id.noContent).setVisibility(View.VISIBLE);
		// }
	}

	OnRefreshListener2<ListView> refreshListener = new OnRefreshListener2<ListView>() {

		@Override
		public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
			// 下拉刷新
			String time = DateUtils.formatDateTime(StarLevelActivity.this,
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
			String time = DateUtils.formatDateTime(StarLevelActivity.this,
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
	 * 获取信息
	 */
	private void sendReqConnect() {
		if (Config.User == null) {
			ToastUtil.show(this, "无法获取信息");
			return;
		}
		HashMap<String, String> entity = new HashMap<String, String>();

		entity.put("clientID", Config.User.getClientID());
		entity.put("The_page_number", "" + pageIndex);
		entity.put("type", "" + type);

		
		ShowProgressDialog("获取用户基本信息...");		
		List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
		addToThreadPool(Config.in_comparison_to_listApply_to_be_a_platform_star_, "send search request", params);
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

	private void showWarningDialog(String title, String message) {
		// TODO Auto-generated method stub
		final PromptDialog.Builder builder = new PromptDialog.Builder(this);
		builder.setTitle(title);
		builder.setMessage(message);
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
				Intent intent = new Intent(StarLevelActivity.this,
						TopUpActivity.class);
				startActivity(intent);
			}
		});
		PromptDialog dialog = builder.create();

		dialog.show();

	}
	
	@Override
	public void onClick(View v) {
		
	}

	@Override
	public void RequestFailed(int errcode, String message, int taskType) {
		if (message.equals("娱币不足！")) {
			showWarningDialog("是否购买娱币", "您的娱币不足是否去购买");
		}
	}
	@Override
	public void RequestSuccessful(String jsonString, int taskType) {
		Gson gson = new Gson();
		switch (taskType) {
		case Config.in_comparison_to_listApply_to_be_a_platform_star_:
			Entity<List<Ranking>> baseEntity4 = gson.fromJson(jsonString,
					new TypeToken<Entity<List<Ranking>>>() {
					}.getType());
			ranking = baseEntity4.getData();
			if (ranking != null && ranking.size() > 0) {
				initPersonalInformation();
			} else {
				ToastUtil.show(this, "没有更多数据了");
			}
			break;
		case Config.give_applause_booed:
			ToastUtil.show(this, "提交成功");
			if (applauseGC == null) {
				return;
			}
			switch (applauseGC.getType()) {
			case 1:
				applauseGC.showAnimationDialog(R.drawable.circle4,
						R.raw.applaud);
				break;
			case 2:
				applauseGC.showAnimationDialog(R.drawable.circle5,
						R.raw.give_back);
				break;
			default:
				applauseGC.showAnimationDialog(R.drawable.circle4,
						R.raw.applaud);
				break;
			}

			break;
		case Config.and_attention:
			ToastUtil.show(this, "提交成功");
			if (applauseGC == null) {
				return;
			}
			applauseGC.showAnimationDialog(R.drawable.circle6, R.raw.concern);
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
	}

}
