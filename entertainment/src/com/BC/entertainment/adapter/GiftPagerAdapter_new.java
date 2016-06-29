package com.BC.entertainment.adapter;

import java.util.ArrayList;
import java.util.List;

import com.BC.entertainment.chatroom.gift.BaseGift_new;
import com.BC.entertainmentgravitation.R;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

public class GiftPagerAdapter_new extends PagerAdapter{
	
    private final int ITEM_COUNT_PER_GRID_VIEW = 8;

    private final Context context;

    private final List<BaseGift_new> gifts;
    private final ViewPager viewPager;
    private final int gridViewCount;

    public GiftPagerAdapter_new(ViewPager viewPager, List<BaseGift_new> gifts) {
        this.context = viewPager.getContext();
        this.gifts = new ArrayList<>(gifts);
        this.viewPager = viewPager;
        this.gridViewCount = (gifts.size() + ITEM_COUNT_PER_GRID_VIEW - 1) / ITEM_COUNT_PER_GRID_VIEW;
    }
    
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        int end = (position + 1) * ITEM_COUNT_PER_GRID_VIEW > gifts.size() ? gifts
                .size() : (position + 1) * ITEM_COUNT_PER_GRID_VIEW;
        List<BaseGift_new> subBaseActions = gifts.subList(position
                * ITEM_COUNT_PER_GRID_VIEW, end);

        GridView gridView = new GridView(context);
        gridView.setAdapter(new GiftGridviewAdapter_new(context, subBaseActions));
        if (gifts.size() >= 4) {
            gridView.setNumColumns(4);

            container.post(new Runnable() {
                @Override
                public void run() {
                    ViewGroup.LayoutParams layoutParams = viewPager.getLayoutParams();
                    layoutParams.height = context.getResources().getDimensionPixelOffset(
                            R.dimen.message_bottom_function_viewpager_height);
                    viewPager.setLayoutParams(layoutParams);
                }
            });
        } else {
            gridView.setNumColumns(gifts.size());

            container.post(new Runnable() {
                @Override
                public void run() {
                    ViewGroup.LayoutParams layoutParams = viewPager.getLayoutParams();
                    layoutParams.height = context.getResources().getDimensionPixelOffset(
                            R.dimen.message_bottom_function_viewpager_height) / 2;
                    viewPager.setLayoutParams(layoutParams);
                }
            });
        }
        gridView.setSelector(R.color.transparent);
        gridView.setHorizontalSpacing(0);
        gridView.setVerticalSpacing(0);
        gridView.setGravity(Gravity.CENTER);
        gridView.setTag(Integer.valueOf(position));
        gridView.setOnItemClickListener(new GridView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int index = ((Integer) parent.getTag()) * ITEM_COUNT_PER_GRID_VIEW + position;
                gifts.get(index).onClick();
            }
        });

        container.addView(gridView);
        return gridView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
    }

	@Override
	public int getCount() {
		return gridViewCount;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
        return view == object;
	}
	
    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

}
