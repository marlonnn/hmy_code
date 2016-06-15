package com.BC.entertainmentgravitation;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.BC.entertainment.adapter.ViewPagerAdapter;
import com.summer.utils.SharedPreferencesUtils;

import java.io.InputStream;
import java.util.ArrayList;

public class GuidePageActivity extends Activity implements View.OnClickListener,
ViewPager.OnPageChangeListener {

    // 定义ViewPager对象
    private ViewPager viewPager;
    // 定义ViewPager适配器
    private ViewPagerAdapter vpAdapter;
    // 定义一个ArrayList来存放View
    private ArrayList<View> views;
    // 底部小点的图片
    private ImageView[] points;
    // 记录当前选中位置
    private int currentIndex;
    private int currentPageScrollStatus;
    // 引导图片资源
    private static final int[] pics = { R.drawable.activity_guide_page1, R.drawable.activity_guide_page2,
            R.drawable.activity_guide_page3, R.drawable.activity_guide_page4 };
    
    private Bitmap[] bitmapPics = new Bitmap[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
			if ((boolean)SharedPreferencesUtils.getParam(this, "autoLogin", false))
			{
				Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
				startActivity(intent);
				finish();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
        setContentView(R.layout.activity_guide_page);
        initializeView();
        initializeData();
    }
    
	private Bitmap readBitmap(int resId)
	{
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		InputStream is = this.getResources().openRawResource(resId);
		return BitmapFactory.decodeStream(is,null,opt);
	}

    /**
     * 初始化组件
     */
    private void initializeView()
    {
        // 实例化ArrayList对象
        views = new ArrayList<View>();
        // 实例化ViewPager
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        // 实例化ViewPager适配器
        vpAdapter = new ViewPagerAdapter(views);
    }

    /**
     * 初始化数据
     */
    private void initializeData()
    {
        // 定义一个布局并设置参数
        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        // 初始化引导图片列表
        for (int i = 0; i < pics.length; i++) {
        	bitmapPics[i] = readBitmap(pics[i]);
            ImageView iv = new ImageView(this);
            iv.setLayoutParams(mParams);
            //防止图片不能填满屏幕
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
            //加载图片资源
            iv.setImageBitmap(bitmapPics[i]);
//            iv.setImageResource(pics[i]);
            views.add(iv);
        }

        // 设置数据
        viewPager.setAdapter(vpAdapter);
        // 设置监听
        viewPager.setOnPageChangeListener(this);

        // 初始化底部小点
        initializePoint();
    }

    /**
     * 初始化底部小点
     */
    private void initializePoint()
    {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearlayout);

        points = new ImageView[pics.length];

        // 循环取得小点图片
        for (int i = 0; i < pics.length; i++) {
            // 得到一个LinearLayout下面的每一个子元素
            points[i] = (ImageView) linearLayout.getChildAt(i);
            // 默认都设为灰色
            points[i].setEnabled(true);
            // 给每个小点设置监听
            points[i].setOnClickListener(this);
            // 设置位置tag，方便取出与当前位置对应
            points[i].setTag(i);
        }

        // 设置当面默认的位置
        currentIndex = 0;
        // 设置为白色，即选中状态
        points[currentIndex].setEnabled(false);
    }

    @Override
    public void onClick(View v) {
        int position = (Integer)v.getTag();
        setCurrentViewPosition(position);
        setCurrentDotPosition(position);
    }

    /**
     * 当前页面滑动时调用
     */
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (currentIndex == 0)
        {
            //如果offsetPixels是0页面也被滑动了，代表在第一页还要往左划
            if (positionOffsetPixels == 0 && currentPageScrollStatus == 1)
            {
            }
        }
        else if (currentIndex == pics.length - 1)
        {
            //已经在最后一页还想往右划
            if (positionOffsetPixels == 0 && currentPageScrollStatus == 1)
            {
                Intent intent = new Intent();
                intent.setClass(this, LoginActivity.class);
                startActivity(intent);
                this.finish();
            }
        }

    }

    /**
     * 滑动状态改变时调用
     */
    @Override
    public void onPageScrollStateChanged(int state) {
        //记录page滑动状态，如果滑动了state就是1
        currentPageScrollStatus = state;
    }

    /**
     * 新的页面被选中时调用
     */
    @Override
    public void onPageSelected(int position) {
        // 设置底部小点选中状态
        setCurrentDotPosition(position);
    }

    /**
     * 设置当前的小点的位置
     */
    private void setCurrentDotPosition(int positon) {
        if (positon < 0 || positon > pics.length - 1 || currentIndex == positon) {
            return;
        }
        points[positon].setEnabled(false);
        points[currentIndex].setEnabled(true);

        currentIndex = positon;
    }

    /**
     * 设置当前页面的位置
     */
    private void setCurrentViewPosition(int position) {
        if (position < 0 || position >= pics.length) {
            return;
        }
        viewPager.setCurrentItem(position);
    }
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(bitmapPics != null && bitmapPics.length > 0)
		{
			for(int i=0; i<bitmapPics.length; i++)
			{
				if(bitmapPics[i] !=null && bitmapPics[i].isRecycled())
				{
					bitmapPics[i].recycle();
					System.gc();
				}
			}
		}
	}
}
