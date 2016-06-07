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
import com.BC.entertainmentgravitation.entity.Photo_images;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class PictureAdapter extends PagerAdapter {
	private List<Photo_images> images = new ArrayList<Photo_images>();
	private Context context;

	public PictureAdapter(List<Photo_images> images, Context context) {
		super();
		this.context = context;
		this.images = images;
	}

	public List<Photo_images> getImages() {
		return images;
	}

	public void setImages(List<Photo_images> images) {
		this.images = images;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return images.size();
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
		Glide.with(context).load(images.get(position).getPicture_address())
				.diskCacheStrategy(DiskCacheStrategy.ALL)
				.placeholder(R.drawable.home_image).into(imageView);
		view.addView(imageView);
		return imageView;
	}
}
