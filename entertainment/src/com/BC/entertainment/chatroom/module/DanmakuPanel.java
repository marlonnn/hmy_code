package com.BC.entertainment.chatroom.module;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import master.flame.danmaku.controller.IDanmakuView;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDisplayer;
import master.flame.danmaku.danmaku.model.android.BaseCacheStuffer;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.model.android.SpannedCacheStuffer;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.danmaku.util.IOUtils;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ImageSpan;
import android.view.View;

import com.BC.entertainmentgravitation.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomNotificationAttachment;
import com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum;
import com.netease.nimlib.sdk.msg.constant.NotificationType;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.summer.config.Config;
import com.summer.logger.XLog;
import com.summer.view.CircularImage;

/**
 * 
 * @author zhongwen
 *
 */
@SuppressLint("UseSparseArrays") 
public class DanmakuPanel {
	
	private Container container;
	private View rootView;
	private IDanmakuView danmakuView;
	private BaseDanmakuParser mParser;
	private DanmakuContext mContext;

	public DanmakuPanel(Container container, View rootView)
	{
        this.container = container;
        this.rootView = rootView;
        initView();
	}
	
    private BaseCacheStuffer.Proxy mCacheStufferAdapter = new BaseCacheStuffer.Proxy() {

        private Drawable mDrawable;

        @Override
        public void prepareDrawing(final BaseDanmaku danmaku, boolean fromWorkerThread) {
            if (danmaku.text instanceof Spanned) { // 根据你的条件检查是否需要需要更新弹幕
                // FIXME 这里只是简单启个线程来加载远程url图片，请使用你自己的异步线程池，最好加上你的缓存池
                new Thread() {

                    @Override
                    public void run() {
                        String url = "http://www.bilibili.com/favicon.ico";
                        InputStream inputStream = null;
                        Drawable drawable = mDrawable;
                        if(drawable == null) {
                            try {
                                URLConnection urlConnection = new URL(url).openConnection();
                                inputStream = urlConnection.getInputStream();
                                drawable = BitmapDrawable.createFromStream(inputStream, "bitmap");
                                mDrawable = drawable;
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {
                                IOUtils.closeQuietly(inputStream);
                            }
                        }
                        if (drawable != null) {
                            drawable.setBounds(0, 0, 100, 100);
                            SpannableStringBuilder spannable = createSpannable(drawable);
                            danmaku.text = spannable;
                            if(danmakuView != null) {
                            	danmakuView.invalidateDanmaku(danmaku, false);
                            }
                            return;
                        }
                    }
                }.start();
            }
        }

        @Override
        public void releaseResource(BaseDanmaku danmaku) {
            // TODO 重要:清理含有ImageSpan的text中的一些占用内存的资源 例如drawable
        }
    };
	private void initView()
	{
		danmakuView = (IDanmakuView) rootView.findViewById(R.id.danmaku);
		mContext = DanmakuContext.create();

        // 设置最大显示行数
        HashMap<Integer, Integer> maxLinesPair = new HashMap<Integer, Integer>();
        maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 5); // 滚动弹幕最大显示3行
        // 设置是否禁止重叠
        HashMap<Integer, Boolean> overlappingEnablePair = new HashMap<Integer, Boolean>();
        overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_RL, true);
        overlappingEnablePair.put(BaseDanmaku.TYPE_FIX_TOP, true);
        
        mContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_STROKEN, 3).setDuplicateMergingEnabled(false).setScrollSpeedFactor(1.2f).setScaleTextSize(1.2f)
        .setCacheStuffer(new SpannedCacheStuffer(), mCacheStufferAdapter) // 图文混排使用SpannedCacheStuffer
        .setMaximumLines(maxLinesPair)
        .preventOverlapping(overlappingEnablePair);
		if (danmakuView != null)
		{
			mParser = new BaseDanmakuParser() {

                @Override
                protected Danmakus parse() {
                    return new Danmakus();
                }
            };
			danmakuView.setCallback(new master.flame.danmaku.controller.DrawHandler.Callback(){

				@Override
				public void prepared() {
					danmakuView.start();
				}

				@Override
				public void updateTimer(DanmakuTimer timer) {
				}

				@Override
				public void danmakuShown(BaseDanmaku danmaku) {
					
				}

				@Override
				public void drawingFinished() {
				}});
			danmakuView.prepare(mParser, mContext);
			danmakuView.showFPS(false);
			danmakuView.enableDanmakuDrawingCache(true);
			
		}
	}
	
    public void addDanmaku(boolean islive, String message) {
        BaseDanmaku danmaku = mContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        if (danmaku == null || danmakuView == null) {
            return;
        }
        danmaku.text = message;
        danmaku.padding = 8;
        danmaku.priority = 1;  // 可能会被各种过滤器过滤并隐藏显示
        danmaku.isLive = islive;
        danmaku.time = danmakuView.getCurrentTime() + 1200;
        danmaku.textSize = 20f * (mParser.getDisplayer().getDensity() - 0.6f);
        danmaku.textColor = Color.parseColor("#EEB422");
//        danmaku.textShadowColor = Color.WHITE;
        // danmaku.underlineColor = Color.GREEN;
//        danmaku.borderColor = Color.parseColor("#8B658B");
        danmakuView.addDanmaku(danmaku);
    }
    
    public void addDanmaku(boolean islive, String message, int textColor, int textShadowColor, int boarderColor) {
        BaseDanmaku danmaku = mContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        if (danmaku == null || danmakuView == null) {
            return;
        }
        danmaku.text = message;
        danmaku.padding = 5;
        danmaku.priority = 1;  // 可能会被各种过滤器过滤并隐藏显示
        danmaku.isLive = islive;
        danmaku.time = danmakuView.getCurrentTime() + 1200;
        danmaku.textSize = 25f * (mParser.getDisplayer().getDensity() - 0.6f);
        danmaku.textColor = textColor;
        danmaku.textShadowColor = textShadowColor;
        // danmaku.underlineColor = Color.GREEN;
        danmaku.borderColor = boarderColor;
        danmakuView.addDanmaku(danmaku);

    }
    
    public void addDanmaKuShowTextAndImage(boolean islive) {
        BaseDanmaku danmaku = mContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        Drawable drawable = container.activity.getResources().getDrawable(R.drawable.ic_launcher);
        drawable.setBounds(0, 0, 100, 100);
        SpannableStringBuilder spannable = createSpannable(drawable);
        danmaku.text = spannable;
        danmaku.padding = 5;
        danmaku.priority = 1;  // 一定会显示, 一般用于本机发送的弹幕
        danmaku.isLive = islive;
        danmaku.time = danmakuView.getCurrentTime() + 1200;
        danmaku.textSize = 25f * (mParser.getDisplayer().getDensity() - 0.6f);
        danmaku.textColor = Color.RED;
        danmaku.textShadowColor = 0; // 重要：如果有图文混排，最好不要设置描边(设textShadowColor=0)，否则会进行两次复杂的绘制导致运行效率降低
        danmaku.underlineColor = Color.GREEN;
        danmakuView.addDanmaku(danmaku);
    }
    
    private SpannableStringBuilder createSpannable(Drawable drawable) {
        String text = "bitmap";
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(text);
        ImageSpan span = new ImageSpan(drawable);//ImageSpan.ALIGN_BOTTOM);
        spannableStringBuilder.setSpan(span, 0, text.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.append("图文混排");
        spannableStringBuilder.setSpan(new BackgroundColorSpan(Color.parseColor("#8A2233B1")), 0, spannableStringBuilder.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        return spannableStringBuilder;
    }
    
    public void showDanmaku(final IMMessage message)
    {
        container.activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
            	if(message != null)
            	{
            		ChatRoomMessage chatRoomMessage  = (ChatRoomMessage)message;
        			if (message.getDirect() == MsgDirectionEnum.Out)
        			{
        	    		if (chatRoomMessage != null && chatRoomMessage.getContent() != null)
        	    		{
        	    			XLog.i("show incoming danmakuPanel message in");
        					addDanmaku(false,  Config.User.getNickName() + ":" + chatRoomMessage.getContent());
        	    		}

        			}

            		else if (message.getDirect() == MsgDirectionEnum.In)
            		{
            			if (chatRoomMessage != null && chatRoomMessage.getChatRoomMessageExtension() != null 
            					&& chatRoomMessage.getChatRoomMessageExtension().getSenderNick() != null
            					&& message.getContent() != null)
            			{
            				XLog.i("show incoming danmakuPanel message in");
            				addDanmaku(false,  chatRoomMessage.getChatRoomMessageExtension().getSenderNick() + ":" + message.getContent());
            			}
            		}
            	}
            }
        });

    }
    
    public void showDanmaku(final ChatRoomNotificationAttachment attachment)
    {
        container.activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
        		if (attachment.getType() == NotificationType.ChatRoomMemberIn)
        		{
        			addDanmaku(false, "系统消息：" + "欢迎"+ attachment.getOperatorNick() + "进入直播间");
        		}
        		else if (attachment.getType() == NotificationType.ChatRoomMemberExit)
        		{
        			addDanmaku(false,  (attachment.getOperatorNick() == null ? "" : attachment.getOperatorNick()) + "离开了直播间");
        		}
            }
        });

    }
	
    public void onPause() {
        if (danmakuView != null && danmakuView.isPrepared()) {
            danmakuView.pause();
        }
    }

    public void onResume() {
        if (danmakuView != null && danmakuView.isPrepared() && danmakuView.isPaused()) {
            danmakuView.resume();
        }
    }

    public void onDestroy() {
        if (danmakuView != null) {
            danmakuView.release();
            danmakuView = null;
        }
    }

    public void onBackPressed() {
        if (danmakuView != null) {
            danmakuView.release();
            danmakuView = null;
        }
    }
    
	public void HideDanmakuView()
	{
		danmakuView.setVisibility(View.INVISIBLE);
	}
	
	public void ShowDanmakuView()
	{
		danmakuView.setVisibility(View.VISIBLE);
	}
}