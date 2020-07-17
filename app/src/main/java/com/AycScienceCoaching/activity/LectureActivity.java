package com.AycScienceCoaching.activity;

import android.content.Intent;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.AycScienceCoaching.Constant.Constant;
import com.AycScienceCoaching.Model.RecentLecturesModel;
import com.AycScienceCoaching.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.io.Serializable;
import java.util.Objects;

import static com.AycScienceCoaching.AlphaApplication.getAppContext;

public class LectureActivity extends AppCompatActivity {
    private FirebaseFirestore mFireBaseDB;
    private TextView textViewDescription;
    private TextView textViewChapter;
    private TextView textViewPdfUrl;
    private String docId;
    private RecentLecturesModel mLecturesModel;
    private String videoUrl;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecture);
        Toolbar toolbar = findViewById(R.id.toolbarOfLectureActivity);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        mFireBaseDB = FirebaseFirestore.getInstance();
        textViewDescription = findViewById(R.id.textviewdescription);
        textViewChapter = findViewById(R.id.textviewchapter);
        textViewPdfUrl = findViewById(R.id.textViewpdfurl);
        Intent intent = getIntent();
        docId = intent.getStringExtra("docId");
        mLecturesModel = (RecentLecturesModel) intent.getSerializableExtra("lectureObject");
        getSupportActionBar().setTitle("");

        if (mLecturesModel != null) {
            setupVideoPlayer();
            prepareActivity();
        } else {
            getRecentLectureData();
        }
    }

    private void getRecentLectureData() {
        mFireBaseDB.collection(Constant.RECENT_LECTURE_COLLECTION)
                .document(docId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        mLecturesModel = documentSnapshot.toObject(RecentLecturesModel.class);
                        setupVideoPlayer();
                        prepareActivity();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getAppContext(), "try again later", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void prepareActivity() {
        textViewChapter.setText(mLecturesModel.getChapterName());
        textViewPdfUrl.setText(mLecturesModel.getPdfName());
        textViewDescription.setText(mLecturesModel.getDescription());
        textViewPdfUrl.setOnClickListener(view -> {
            Intent intent1 = new Intent(LectureActivity.this, PdfViewActivity.class);
            intent1.putExtra("url", mLecturesModel.getPdfUrl());
            startActivity(intent1);
        });
        setupWindowAnimation();
    }

    private void setupVideoPlayer() {
        //Youtube viewer
        if (mLecturesModel.getVideoUrl() != null && !mLecturesModel.getVideoUrl().isEmpty()) {
            //getting only ID from URL
            String[] parts = mLecturesModel.getVideoUrl().split("/");
            videoUrl = parts[3];
        }

        YouTubePlayerView youTubePlayerView = findViewById(R.id.youtube_player_view_Lectures);
        getLifecycle().addObserver(youTubePlayerView);

        if (videoUrl != null && !videoUrl.isEmpty()) {
            youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                @Override
                public void onReady(YouTubePlayer youTubePlayer) {
                    youTubePlayer.loadVideo(videoUrl, 0);
                }
            });
        } else {
            youTubePlayerView.setVisibility(View.GONE);
        }
    }

    private void setupWindowAnimation() {
        Transition slide = TransitionInflater.from(this).inflateTransition(R.transition.activity_fade);
        getWindow().setEnterTransition(slide);

    }
}