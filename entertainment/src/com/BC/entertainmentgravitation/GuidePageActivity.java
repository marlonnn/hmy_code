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

import java.io.InputStream;
import java.util.ArrayList;

public class GuidePageActivity extends Activity implements View.OnClickListener,
ViewPager.OnPageChangeListener {

    // ����ViewPager����
    private ViewPager viewPager;
    // ����ViewPager������
    private ViewPagerAdapter vpAdapter;
    // ����һ��ArrayList�����View
    private ArrayList<View> views;
    // �ײ�С���ͼƬ
    private ImageView[] points;
    // ��¼��ǰѡ��λ��
    private int currentIndex;
    private int currentPageScrollStatus;
    // ����ͼƬ��Դ
    private static final int[] pics = { R.drawable.activity_guide_page1, R.drawable.activity_guide_page2,
            R.drawable.activity_guide_page3, R.drawable.activity_guide_page4 };
    
    private Bitmap[] bitmapPics = new Bitmap[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
     * ��ʼ�����
     */
    private void initializeView()
    {
        // ʵ����ArrayList����
        views = new ArrayList<View>();
        // ʵ����ViewPager
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        // ʵ����ViewPager������
        vpAdapter = new ViewPagerAdapter(views);
    }

    /**
     * ��ʼ������
     */
    private void initializeData()
    {
        // ����һ�����ֲ����ò���
        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        // ��ʼ������ͼƬ�б�
        for (int i = 0; i < pics.length; i++) {
        	bitmapPics[i] = readBitmap(pics[i]);
            ImageView iv = new ImageView(this);
            iv.setLayoutParams(mParams);
            //��ֹͼƬ����������Ļ
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
            //����ͼƬ��Դ
            iv.setImageBitmap(bitmapPics[i]);
//            iv.setImageResource(pics[i]);
            views.add(iv);
        }

        // ��������
        viewPager.setAdapter(vpAdapter);
        // ���ü���
        viewPager.setOnPageChangeListener(this);

        // ��ʼ���ײ�С��
        initializePoint();
    }

    /**
     * ��ʼ���ײ�С��
     */
    private void initializePoint()
    {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearlayout);

        points = new ImageView[pics.length];

        // ѭ��ȡ��С��ͼƬ
        for (int i = 0; i < pics.length; i++) {
            // �õ�һ��LinearLayout�����ÿһ����Ԫ��
            points[i] = (ImageView) linearLayout.getChildAt(i);
            // Ĭ�϶���Ϊ��ɫ
            points[i].setEnabled(true);
            // ��ÿ��С�����ü���
            points[i].setOnClickListener(this);
            // ����λ��tag������ȡ���뵱ǰλ�ö�Ӧ
            points[i].setTag(i);
        }

        // ���õ���Ĭ�ϵ�λ��
        currentIndex = 0;
        // ����Ϊ��ɫ����ѡ��״̬
        points[currentIndex].setEnabled(false);
    }

    @Override
    public void onClick(View v) {
        int position = (Integer)v.getTag();
        setCurrentViewPosition(position);
        setCurrentDotPosition(position);
    }

    /**
     * ��ǰҳ�滬��ʱ����
     */
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (currentIndex == 0)
        {
            //���offsetPixels��0ҳ��Ҳ�������ˣ������ڵ�һҳ��Ҫ����
            if (positionOffsetPixels == 0 && currentPageScrollStatus == 1)
            {
            }
        }
        else if (currentIndex == pics.length - 1)
        {
            //�Ѿ������һҳ�������һ�
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
     * ����״̬�ı�ʱ����
     */
    @Override
    public void onPageScrollStateChanged(int state) {
        //��¼page����״̬�����������state����1
        currentPageScrollStatus = state;
    }

    /**
     * �µ�ҳ�汻ѡ��ʱ����
     */
    @Override
    public void onPageSelected(int position) {
        // ���õײ�С��ѡ��״̬
        setCurrentDotPosition(position);
    }

    /**
     * ���õ�ǰ��С���λ��
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
     * ���õ�ǰҳ���λ��
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
