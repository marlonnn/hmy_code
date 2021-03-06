package com.BC.entertainmentgravitation;

import java.util.List;

import org.apache.http.NameValuePair;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.BC.entertainmentgravitation.fragment.FocusFragment;
import com.BC.entertainmentgravitation.fragment.HotFragment;
import com.BC.entertainmentgravitation.fragment.NewFragment;
import com.google.gson.Gson;
import com.summer.activity.BaseActivity;
import com.summer.factory.ThreadPoolFactory;
import com.summer.handler.InfoHandler;
import com.summer.task.HttpBaseTask;
import com.summer.treadpool.ThreadPoolConst;
import com.summer.utils.UrlUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * 明星列表
 * @author wen zhong
 *
 */
public class StarListActivity extends BaseActivity implements OnClickListener, OnItemClickListener{

	private ViewPager viewPager;
	private RadioGroup radio;
	private Gson gson;
	private FocusFragment focusFragment;
	private HotFragment hotFragment;
	private NewFragment newFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_star_list);
		initFragment();
		initView();
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
	
	private void initFragment()
	{
		focusFragment = new FocusFragment();
		hotFragment = new HotFragment();
		newFragment = new NewFragment();
	}
	
	@SuppressWarnings("deprecation")
	private void initView()
	{
		gson = new Gson();
		FragmentManager fragmentManager = this.getSupportFragmentManager();
		findViewById(R.id.imgViewBack).setOnClickListener(this);

		radio = (RadioGroup) findViewById(R.id.rGroup);
		radio.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				/**
				 * 关注
				 */
				case R.id.tbtnFocus:
					viewPager.setCurrentItem(0);
					break;
				/**
				 * 热门
				 */
				case R.id.rbtnHot:
					viewPager.setCurrentItem(1);
					break;
				/**
				 * 最新
				 */
				case R.id.rbtnNew:
					viewPager.setCurrentItem(2);
					break;

				}
			}
		});
		
		viewPager = (ViewPager) findViewById(R.id.vPager);
        viewPager.setAdapter(new FragmentPagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
            	Fragment fragment = null;
            	switch(position)
            	{
				/**
				 * 关注
				 */
            	case 0:
//            		radio.check(R.id.tbtnFocus);
            		fragment = focusFragment;
            		break;
				/**
				 * 热门
				 */
            	case 1:
//            		radio.check(R.id.rbtnHot);
            		fragment = hotFragment;
            		break;
				/**
				 * 最新
				 */
            	case 2:
//            		radio.check(R.id.rbtnNew);
            		fragment = newFragment;
            		break;
            	}
                return fragment;
            }

            @Override
            public int getCount() {
                return 3;
            }
        });
        viewPager.setCurrentItem(1);
        radio.check(R.id.rbtnHot);
        
        viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				switch(arg0)
				{
				/**
				 * 关注
				 */
            	case 0:
            		radio.check(R.id.tbtnFocus);
            		break;
				/**
				 * 热门
				 */
            	case 1:
            		radio.check(R.id.rbtnHot);
            		break;
				/**
				 * 最新
				 */
            	case 2:
            		radio.check(R.id.rbtnNew);
            		break;
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
	
    private void addToThreadPool(int taskType, String Tag, List<NameValuePair> params)
    {
    	HttpBaseTask httpTask = new HttpBaseTask(ThreadPoolConst.THREAD_TYPE_FILE_HTTP, Tag, params, UrlUtil.GetUrl(taskType));
    	httpTask.setTaskType(taskType);
    	InfoHandler handler = new InfoHandler(this);
    	httpTask.setInfoHandler(handler);
    	ThreadPoolFactory.getThreadPoolManager().addTask(httpTask);
    }
	
	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		/**
		 * 返回键
		 */
		case R.id.imgViewBack:
			finish();
			break;
		}

	}
	

	@Override
	public void RequestSuccessful(String jsonString, int taskType) {
		switch (taskType) {
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
	}

}
