package com.BC.entertainment.adapter;

import java.util.List;

import com.BC.entertainment.chatroom.gift.BaseGift;
import com.BC.entertainmentgravitation.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GiftGridviewAdapter extends BaseAdapter{
	
	private Context context;

	private List<BaseGift> baseGifts;

//	private List<Drawable> drawableCache;
	
	public GiftGridviewAdapter(Context context, List<BaseGift> baseGifts) {
		this.context = context;
		this.baseGifts = baseGifts;
//		this.LoadBitmapsIntoCache();
	}

//	private void LoadBitmapsIntoCache()
//	{
//		for(int i=0; i<baseGifts.size(); i++)
//		{
//			drawableCache = new ArrayList<Drawable>();
//			Drawable drawable = new BitmapDrawable(this.context.getResources(), decodeSampledBitmapFromResource(this.context.getResources(), baseGifts.get(i).getIconResId(), 40, 40));
//			drawableCache.add(drawable);
//		}
//	}
	
	@Override
	public int getCount() {
		return baseGifts.size();
	}

	@Override
	public Object getItem(int position) {
		return baseGifts.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View itemlayout;
		if (convertView == null) {
			itemlayout = LayoutInflater.from(context).inflate(R.layout.fragment_top_surface_gift_item, null);
		} else {
			itemlayout = convertView;
		}
		
		BaseGift viewHolder = baseGifts.get(position);
		
		if(viewHolder != null)
		{
			Drawable drawable = new BitmapDrawable(this.context.getResources(), decodeSampledBitmapFromResource(this.context.getResources(), viewHolder.getIconResId(), 40, 40));
			((ImageView) itemlayout.findViewById(R.id.imageView_gift_icon)).setBackgroundDrawable(drawable);
//			((ImageView) itemlayout.findViewById(R.id.imageView_gift_icon)).setBackgroundResource(viewHolder.getIconResId());
			((TextView) itemlayout.findViewById(R.id.textView_gift_value)).setText(viewHolder.getValue() + "");
			((TextView) itemlayout.findViewById(R.id.textView_gift_experient_points)).setText(viewHolder.getName());
		}

		return itemlayout;
	}
	
	public Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
	        int reqWidth, int reqHeight) {

	    // First decode with inJustDecodeBounds=true to check dimensions
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeResource(res, resId, options);

	    // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeResource(res, resId, options);
	}
	
	public int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
    // Raw height and width of image
    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 1;

    if (height > reqHeight || width > reqWidth) {

        final int halfHeight = height / 2;
        final int halfWidth = width / 2;

        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
        // height and width larger than the requested height and width.
        while ((halfHeight / inSampleSize) > reqHeight
                && (halfWidth / inSampleSize) > reqWidth) {
            inSampleSize *= 2;
        }
    }

    return inSampleSize;
}

}
