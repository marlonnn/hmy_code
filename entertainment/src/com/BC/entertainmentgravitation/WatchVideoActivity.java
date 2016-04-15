package com.BC.entertainmentgravitation;

import com.BC.entertainmentgravitation.entity.ChatRoom;
import com.BC.entertainmentgravitation.fragment.PushVideoFragment;
import com.BC.entertainmentgravitation.fragment.ScrollListener;
import com.BC.entertainmentgravitation.fragment.SurfaceFragment;
import com.BC.entertainmentgravitation.fragment.WatchVideoFragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

/**
 * ¿´Ã÷ÐÇÖ±²¥
 * @author zhongwen
 *
 */
public class WatchVideoActivity extends FragmentActivity {
	
	public View rootView;
	private ScrollListener listener;

	private ChatRoom chatRoom;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        
        rootView = findViewById(R.id.root_content);
        
        Intent intent = getIntent();
        if (intent != null)
        {
            String cid = intent.getStringExtra("cid");
        	String pushUrl = intent.getStringExtra("pushUrl");
        	String chatroomid = intent.getStringExtra("chatroomid");
        	chatRoom = new ChatRoom();
        	chatRoom.setCid(cid);
        	chatRoom.setChatroomid(chatroomid);
        	chatRoom.setPushUrl(pushUrl);
        }

        WatchVideoFragment fragment = new WatchVideoFragment();
        listener = fragment.CreateScrollListener();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.layout_video_play, fragment)
                .commit();
        new SurfaceFragment(listener, chatRoom).show(getSupportFragmentManager(), "watch video");
    }
}
