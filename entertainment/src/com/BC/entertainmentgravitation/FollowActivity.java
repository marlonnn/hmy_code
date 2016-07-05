package com.BC.entertainmentgravitation;

import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;

import com.BC.entertainmentgravitation.entity.Follow;
import com.summer.activity.BaseActivity;
import com.summer.adapter.CommonAdapter;
import com.summer.config.Config;
import com.summer.factory.ThreadPoolFactory;
import com.summer.handler.InfoHandler;
import com.summer.ptr.PullToRefreshBase;
import com.summer.ptr.PullToRefreshGridView;
import com.summer.ptr.PullToRefreshBase.OnRefreshListener2;
import com.summer.task.HttpBaseTask;
import com.summer.treadpool.ThreadPoolConst;
import com.summer.utils.JsonUtil;
import com.summer.utils.UrlUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * 关注我的粉丝
 * @author wen zhong
 *
 */
public class FollowActivity extends BaseActivity implements OnClickListener{

	private PullToRefreshGridView pGridViewFollow;
	private CommonAdapter<Follow> adapter;
	private int pageIndex = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_follow);
		findViewById(R.id.imageViewBack).setOnClickListener(this);
		initAdapter();
		initView();
		
	}
	private void initAdapter()
	{
		
	}
	
	private void initView()
	{
		pGridViewFollow = (PullToRefreshGridView) findViewById(R.id.pGridViewFollow);
		pGridViewFollow.getRefreshableView().setNumColumns(1);
		pGridViewFollow.getRefreshableView().setVerticalSpacing(10);
		pGridViewFollow.setMode(PullToRefreshBase.Mode.BOTH);
		pGridViewFollow.setOnRefreshListener(refreshListener);
		pGridViewFollow.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				try {
//					FHNEntity entity = (FHNEntity)view.getTag();
//					if (entity != null)
//					{
///*						Intent i = new Intent(getActivity(), DetailsActivity.class);
//						i.putExtra("userID", entity.getStar_ID());
//						startActivity(i);*/
//					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		pGridViewFollow.setAdapter(adapter);
	}
	
	OnRefreshListener2<GridView> refreshListener = new OnRefreshListener2<GridView>() {

		@Override
		public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
			// 下拉刷新
			String time = DateUtils.formatDateTime(FollowActivity.this,
					System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
							| DateUtils.FORMAT_SHOW_DATE
							| DateUtils.FORMAT_ABBREV_ALL);
			pGridViewFollow.getLoadingLayoutProxy().setRefreshingLabel(
					"正在刷新");
			pGridViewFollow.getLoadingLayoutProxy().setPullLabel("下拉刷新");
			pGridViewFollow.getLoadingLayoutProxy().setReleaseLabel(
					"释放开始刷新");
			refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(
					"最后更新时间:" + time);
			pageIndex = 1;
			sendFollowRequest();

		}

		@Override
		public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
			// 上拉翻页
			String time = DateUtils.formatDateTime(FollowActivity.this,
					System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
							| DateUtils.FORMAT_SHOW_DATE
							| DateUtils.FORMAT_ABBREV_ALL);
			pGridViewFollow.getLoadingLayoutProxy().setRefreshingLabel(
					"正在加载");
			pGridViewFollow.getLoadingLayoutProxy().setPullLabel("上拉翻页");
			pGridViewFollow.getLoadingLayoutProxy().setReleaseLabel(
					"释放开始加载");
			refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(
					"最后加载时间:" + time);
			// 调用数据
			sendFollowRequest();
		}

	};
	
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
		 * 返回键
		 */
		case R.id.imageViewBack:
			finish();
			break;
		}
	}
	
	/**
	 * 获取热门用户基本信息
	 * @param fhnEntity
	 */
	private void sendFollowRequest()
	{
		HashMap<String, String> entity = new HashMap<String, String>();
		entity.put("clientID", Config.User.getClientID());
		entity.put("The_page_number", "" + pageIndex);
		ShowProgressDialog("正在获取数据...");		
		List<NameValuePair> params = JsonUtil.requestForNameValuePair(entity);
		addToThreadPool(Config.member_in, "send search request", params);
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
		
	}

}
