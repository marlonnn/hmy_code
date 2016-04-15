package com.BC.entertainmentgravitation;

import com.BC.entertainmentgravitation.fragment.ScrollListener;
import com.BC.entertainmentgravitation.fragment.SurfaceFragment;
import com.BC.entertainmentgravitation.fragment.WatchVideoFragment;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

public class MainActivity_back extends FragmentActivity {
	
	public View rootView;
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
