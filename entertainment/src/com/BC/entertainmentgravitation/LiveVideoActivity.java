package com.BC.entertainmentgravitation;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.BC.entertainmentgravitation.fragment.ScrollListener;

/**
 * Ã÷ÐÇÖ±²¥
 * @author zhongwen
 *
 */
public class LiveVideoActivity extends FragmentActivity {
	
	private View rootView;
	private ScrollListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        
//        rootView = findViewById(R.id.root_content);
//
//        VideoFragment fragment = new VideoFragment();
//        listener = fragment.CreateScrollListener();
//        getSupportFragmentManager()
//                .beginTransaction()
//                .add(R.id.layout_video_play, fragment)
//                .commit();
//        new SurfaceFragment(listener).show(getSupportFragmentManager(), "main");
    }
}

