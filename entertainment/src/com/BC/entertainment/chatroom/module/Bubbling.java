package com.BC.entertainment.chatroom.module;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.BC.entertainmentgravitation.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.RelativeLayout;

public class Bubbling extends RelativeLayout{

	private List<View> views = new ArrayList<View>();
	
	private Random random = new Random();
	
	private final int[] ICONS = new int[] { R.drawable.bubble_simile_1,
		R.drawable.bubble_simile_2, R.drawable.bubble_simile_3, R.drawable.bubble_simile_4,
		R.drawable.bubble_simile_5, R.drawable.bubble_simile_6, R.drawable.bubble_simile_7,
		R.drawable.bubble_simile_8, R.drawable.bubble_simile_9};
	
	private Context mContext;
	
	private final int DEFAULT_COUNT = 9;
	private final int MAX_DEGREES = 38;
	private final long DURATION_TOTAL = 2000;
	private final long DURATION_SCALE = 1500;
	private final long DURATION_ALPHA = 1000;
	private final long DURATION_ROTATE = 10;
	
	private int mCount;
	
	private int index = 0;
	private int mIndex = random.nextInt(DEFAULT_COUNT);//当前的index
	
	private View view;
	
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getmIndex() {
		return mIndex;
	}

	public void setmIndex(int mIndex) {
		this.mIndex = mIndex;
	}

	public Bubbling(Context context) {
		super(context);
		this.mContext = context;
		initView();
	}

	public Bubbling(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		initView();
	}

	public Bubbling(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
		initView();
	}

	private void initView() {
		mCount = DEFAULT_COUNT;
		initChildrenViews();
	}
	
	private void initChildrenViews() {
		removeAllViews();
		views.clear();
		for (int i = 0; i < mCount; i++) {
			View view = new View(mContext);
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
					100, 100);
			lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
			lp.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
			view.setBackgroundResource(ICONS[i % ICONS.length]);
			view.setLayoutParams(lp);
			addView(view);
			view.setVisibility(View.GONE);
			views.add(view);
		}
	}
	
	private void resetView(int index)
	{
//		removeAllViews();
		view = new View(mContext);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				100, 100);
		lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
		lp.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
		view.setBackgroundResource(ICONS[index]);
		view.setLayoutParams(lp);
		addView(view);
		view.setVisibility(View.GONE);
	}
	
	public void start()
	{
		resetView(mIndex);
		view.setVisibility(View.VISIBLE);
		startAnim(mIndex, view);
	}
	
	public void startAnimation() {
		index = random.nextInt(DEFAULT_COUNT);
		View view = views.get(index);
		view.setVisibility(View.VISIBLE);
		startAnim(index, view);
	}
	
	public void startAnimation(int index) {
		if (index <0 || index >8)
		{
			index = 0;
		}
		View view = views.get(index);
		view.setVisibility(View.VISIBLE);
		startAnim(index, view);
	}
	
	public void Stop()
	{
		View view = views.get(index);
		view.setVisibility(View.GONE);
		view.clearAnimation();
	}
	
	private void startAnim(final int index, final View view) {
		Animation anim = getAnimSet(index, view);
		anim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				views.get(index).setVisibility(View.GONE);
				view.setVisibility(View.GONE);
			}
		});
		view.startAnimation(anim);
	}
	
	private Animation getAnimSet(int index, final View view) {
		float degrees;
		if (index < 4 && index < views.size() - 1) {
			degrees = randomDegrees((index % 2 == 0));
		} else {
			degrees = randomDegrees();
		}
		int startOffset = 0;

		AnimationSet anim = new AnimationSet(false);
		anim.addAnimation(getRotateAnim(degrees));

		Animation sAnim = getScale();
		sAnim.setStartOffset(startOffset);
		anim.addAnimation(sAnim);

		Animation tAnim = getTranslateAnim(degrees);
		tAnim.setDuration(DURATION_TOTAL);
		tAnim.setStartOffset(startOffset);
		anim.addAnimation(tAnim);

		Animation aAnim = getAlphaAnim();
		aAnim.setStartOffset(DURATION_TOTAL - DURATION_ALPHA + startOffset);

		anim.addAnimation(aAnim);

		view.startAnimation(anim);
		return anim;
	}
	
	private Animation getScale() {
		Animation anim = new ScaleAnimation(0f, 1.0f, 0f, 1.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				1.0f);
		anim.setDuration(DURATION_SCALE);
		anim.setFillAfter(true);
		return anim;
	}

	private Animation getRotateAnim(final float degrees) {
		Animation anim = new RotateAnimation(0f, degrees,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				1.0f);
		anim.setDuration(DURATION_ROTATE);
		anim.setFillAfter(true);
		anim.start();
		return anim;
	}

	private Animation getTranslateAnim(final float degrees) {
		double dis = Math.tan(degrees * Math.PI / 180) * 100;
		Animation anim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f,
				Animation.ABSOLUTE, (float) dis, Animation.RELATIVE_TO_PARENT,
				0f, Animation.ABSOLUTE, -600);
		anim.start();
		return anim;
	}

	private Animation getAlphaAnim() {
		Animation anim = new AlphaAnimation(1.0f, 0.2f);
		anim.setDuration(DURATION_ALPHA);
		return anim;
	}
	
	private float randomDegrees() {
		Random random = new Random();
		return MAX_DEGREES - random.nextFloat() * MAX_DEGREES * 2;
	}

	private float randomDegrees(boolean isPositive) {
		Random random = new Random();
		if (isPositive) {
			return random.nextFloat() * MAX_DEGREES;
		} else {
			return -random.nextFloat() * MAX_DEGREES;
		}
	}
}
