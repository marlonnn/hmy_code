package com.BC.entertainment.inter;

import com.BC.entertainmentgravitation.entity.FHNEntity;

/**
 * list view 左右滑动回调接口
 * @author wen zhong
 *
 */
public interface SlideCallback {

	/**
	 * 取消关注
	 */
	void unFocus(String starId);
	
	/**
	 * 取消收听
	 */
	void unListen(String starId);
	
	void itemClick(FHNEntity item);
}
