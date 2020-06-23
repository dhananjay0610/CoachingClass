package com.alphaCoaching.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.alphaCoaching.R;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

public class DisplayVideos extends AppCompatActivity {

    TextView textView;
    String title,url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_videos);

//        Toast.makeText(this, "for FULL SCREEN mode ROTATE your device,you can also scroll after that better experience", Toast.LENGTH_LONG).show();
        textView=findViewById(R.id.tvVideoTitle);

        Intent intent=getIntent();
        title=intent.getStringExtra("title");
        textView.setText(title);
        url=intent.getStringExtra("url");

        //getting only ID from URL
        String[] parts= url.split("/");
        url=parts[3];


        YouTubePlayerView youTubePlayerView=findViewById(R.id.youtube_player_view);
        getLifecycle().addObserver(youTubePlayerView);

        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(YouTubePlayer youTubePlayer) {
                youTubePlayer.loadVideo(url,0);
            }
        });

    }
}
