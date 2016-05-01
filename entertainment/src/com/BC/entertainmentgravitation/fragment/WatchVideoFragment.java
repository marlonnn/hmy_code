package com.BC.entertainmentgravitation.fragment;

import com.BC.entertainmentgravitation.R;
import com.BC.entertainmentgravitation.entity.ChatRoom;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.netease.neliveplayer.NELivePlayer;
import com.netease.neliveplayer.NELivePlayer.OnPreparedListener;
import com.netease.neliveplayer.NEMediaPlayer;
import com.summer.fragment.BaseFragment;
import com.summer.logger.XLog;


public class WatchVideoFragment extends BaseFragment implements SurfaceHolder.Callback, OnPreparedListener{
	
	private NELivePlayer mMediaPlayer = null;//MediaPlayer
	
	public NEVideoView mVideoView;//NEVideoView extends SurfaceView;
	
	private RelativeLayout mPlayToolbar;

	private View mBuffer;

	private NEMediaController mMediaController;

	private View rootView;
	
	private boolean pauseInBackgroud = true;
	
    private ScrollListener listener;
    private ChatRoom chatRoom;
    
    private Handler handler;
    
	public Handler getHandler() {
		return handler;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}
	
    public WatchVideoFragment(ChatRoom chatRoom)
    {
    	this.chatRoom = chatRoom;
    }

    public ScrollListener CreateScrollListener() {
        listener = new ScrollListener() {
            @Override
            public void onScroll(final float transY, final boolean goUp) {
                XLog.i("transY " + transY);
                if (WatchVideoFragment.this.isVisible() && null != rootView) {
                    if (goUp) 
                    {
                        rootView.setTranslationY(-transY);
                    } 
                    else 
                    {
                        rootView.setTranslationY(getDeviceHeight(getActivity()) - transY);
                    }

                    if(transY == 0)
                    {
                        rootView.setTranslationY(0);
                    }
                }
            }
        };

        return listener;
    }
    
    private int getDeviceHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(mDisplayMetrics);
        return mDisplayMetrics.heightPixels;
    }

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_watch_video_play, container, false);
	}
	
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initializeView(view);
	}
	
	private void initializeView(View view)
	{
		rootView = view.findViewById(R.id.layout_root);
	    mPlayToolbar = (RelativeLayout)view.findViewById(R.id.play_toolbar);
	    mPlayToolbar.setVisibility(View.INVISIBLE);
		
	    mBuffer = view.findViewById(R.id.buffering_prompt);
		mMediaController = new NEMediaController(this.getActivity());
		
		mVideoView = (NEVideoView) view.findViewById(R.id.video_view);
		
		mVideoView.setMediaController(mMediaController);
		
		mVideoView.setMediaType("livestream");
		
		mVideoView.setVideoPath(chatRoom.getHttpPullUrl());
		
		SurfaceHolder holder = mVideoView.getHolder();
		
		holder.addCallback(this);
		
		mMediaPlayer = new NEMediaPlayer();
		
		mVideoView.setBufferStrategy(0);
		
		mVideoView.setVideoScalingMode(2);
		
		mVideoView.setHardwareDecoder(false);
		
		mVideoView.setPauseInBackground(pauseInBackgroud);
		
		mMediaPlayer.setOnPreparedListener(WatchVideoFragment.this);
		
		mVideoView.requestFocus();
		
		mVideoView.start();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			if(mVideoView != null)
			{
				mVideoView.release_resource();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		try {
			if (pauseInBackgroud)
				mVideoView.pause();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onStart() {
		super.onStart();
	}
	
	@Override
	public void onResume() {
		try {
			if (pauseInBackgroud && !mVideoView.isPaused()) {
				mVideoView.start(); 
				mMediaPlayer.start();
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
		super.onResume();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mMediaPlayer.setDisplay(holder);
		try {
			mMediaPlayer.prepareAsync(getActivity().getApplicationContext());
		} catch (IllegalStateException e) {
			XLog.e("java.lang.IllegalStateException");
			e.printStackTrace();
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		
        if (mMediaPlayer != null) {
        	mVideoView.release_resource();
        	mMediaPlayer.reset();
        	mMediaPlayer.release();
        	mMediaPlayer = null;
        }
	}

	@Override
	public void onPrepared(NELivePlayer arg0) {
		try {
			mMediaPlayer.start();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void RequestSuccessful(String jsonString, int taskType) {
		
	}

	
}
