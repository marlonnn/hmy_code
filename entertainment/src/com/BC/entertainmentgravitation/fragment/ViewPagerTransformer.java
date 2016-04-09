package com.BC.entertainmentgravitation.fragment;

import android.support.v4.view.ViewPager;
import android.view.View;

public class ViewPagerTransformer implements ViewPager.PageTransformer {

	@Override
	public void transformPage(View view, float position) {
		/**
		 * -----    -----   -----
         * |   |    |   |   |   |
         * |1  |    |2  |   |3  |
         * |   |    |   |   |   |
         * -----    -----   -----
         * -----
           |   |
           |2  |
           |   |
           -----
		 */
        view.setTranslationX(view.getWidth() * - position);
        float yPosition = position * view.getHeight();
        view.setTranslationY(yPosition);
		
	}

}
