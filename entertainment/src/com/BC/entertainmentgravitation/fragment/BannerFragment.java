package com.BC.entertainmentgravitation.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.BC.entertainmentgravitation.BrowserAcitvity;
import com.BC.entertainmentgravitation.R;
import com.BC.entertainmentgravitation.entity.Activitys;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

public final class BannerFragment extends Fragment {

	private Activitys banner;
	private int resId;

	public static BannerFragment newInstance(Activitys _banner) {
		BannerFragment fragment = new BannerFragment();
		fragment.banner = _banner;
		return fragment;
	}

	public static BannerFragment newInstance(int _resId) {
		BannerFragment fragment = new BannerFragment();
		fragment.resId = _resId;
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ImageView imageView = new ImageView(getActivity());
		imageView.setScaleType(ScaleType.FIT_XY);
		if (banner != null) {
			Glide.with(getActivity()).load(banner.getPicture_address())
					.centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL)
					.placeholder(R.drawable.banner).into(imageView);

			imageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

				}
			});
			imageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(v.getContext(),
							BrowserAcitvity.class);
					intent.putExtra("url", banner.getThe_link_address());
					startActivity(intent);
				}
			});
		} else if (resId != 0) {
			imageView.setImageResource(resId);
			imageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(v.getContext(),
							BrowserAcitvity.class);
					intent.putExtra("url", "http://www.e56hcc.com/app/");
					startActivity(intent);
				}
			});
		}
		return imageView;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
}

