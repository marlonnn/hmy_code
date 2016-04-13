package com.BC.entertainment.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import com.BC.entertainmentgravitation.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.ImageHeaderParser.ImageType;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class PictureAdapter extends PagerAdapter {
	List<String> images = new ArrayList<String>();
	Context context;

	public PictureAdapter(List<String> images, Context context) {
		super();
		this.context = context;
		this.images = images;
	}

	public List<String> getImages() {
		return images;
	}

	public void setImages(List<String> images) {
		this.images = images;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return images.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
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
		Glide.with(context).load(images.get(position))
				.diskCacheStrategy(DiskCacheStrategy.ALL)
				.placeholder(R.drawable.home_image).into(imageView);
		view.addView(imageView);
		return imageView;
	}
}
