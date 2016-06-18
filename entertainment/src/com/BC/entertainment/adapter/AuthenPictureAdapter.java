package com.BC.entertainment.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import com.BC.entertainmentgravitation.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

/**
 * 查看认证照片
 * @author zhongwen
 *
 */
public class AuthenPictureAdapter extends PagerAdapter {
	private List<Bitmap> images = new ArrayList<Bitmap>();
	private Context context;

	public AuthenPictureAdapter(List<Bitmap> images, Context context) {
		super();
		this.context = context;
		this.images = images;
	}

	public List<Bitmap> getImages() {
		return images;
	}

	public void setImages(List<Bitmap> images) {
		this.images = images;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		if (images.size() > 0)
		{
			return images.size();
		}
		else
		{
			return 0;
		}
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public void destroyItem(ViewGroup view, int position, Object object) {
		if (object instanceof ImageView) {
			view.removeView((ImageView) object);
		}
	}

	@Override
	public Object instantiateItem(ViewGroup view, int position) {
		ImageView imageView = new ImageView(context);
		LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		imageView.setLayoutParams(layoutParams);
		imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);//.centerCrop()
		imageView.setImageBitmap(images.get(position));
//		Glide.with(context).load(images.get(position))
//				.diskCacheStrategy(DiskCacheStrategy.ALL)
//				.placeholder(R.drawable.home_image).into(imageView);
		view.addView(imageView);
		return imageView;
	}
}
