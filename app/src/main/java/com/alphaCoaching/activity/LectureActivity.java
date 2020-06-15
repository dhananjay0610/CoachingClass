package com.alphaCoaching.activity;

import android.content.Intent;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.alphaCoaching.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.Objects;


public class LectureActivity extends AppCompatActivity {
    FirebaseFirestore mFireBaseDB;
    TextView textViewDescription;
    TextView textViewChapter;
    TextView textViewPdfUrl;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecture);
        //toolbar
        Toolbar toolbar = findViewById(R.id.toolbarOfLectureActivity);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        mFireBaseDB = FirebaseFirestore.getInstance();
        textViewDescription = findViewById(R.id.textviewdescription);
        textViewChapter = findViewById(R.id.textviewchapter);
        textViewPdfUrl = findViewById(R.id.textViewpdfurl);
        //getting data from mainActivity
        Intent intent = getIntent();
        String chapterName = intent.getStringExtra("chapterName");
        String subject = intent.getStringExtra("subject");
        textViewChapter.setText(chapterName);
        getSupportActionBar().setTitle("");

        //for url
        String urlName = "pdf 1";

        //Youtube viewer
        YouTubePlayerView youTubePlayerView = findViewById(R.id.youtube_player_view);
        getLifecycle().addObserver(youTubePlayerView);

        //  fetching data from FireStore
        mFireBaseDB.collection("recentLectures")
                .whereEqualTo("chapterName", chapterName)
                .whereEqualTo("subject", subject)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot snapshot : Objects.requireNonNull(task.getResult())) {
                            Log.d("LectureActivity", snapshot.getId() + " :" + snapshot.getData());
                            textViewDescription.setText(snapshot.getString("description"));
                            if (snapshot.getString("UrlName") == null) {
                                textViewPdfUrl.setText(urlName);
                            } else {
                                textViewPdfUrl.setText(snapshot.getString("UrlName"));
                            }
                        }
                    } else {
                        Log.d("LectureActivity", "error was:", task.getException());
                    }
                });

        textViewPdfUrl.setOnClickListener(view -> {

            //fetching data from FireStore
            mFireBaseDB.collection("recentLectures")
                    .whereEqualTo("chapterName", chapterName)
                    .whereEqualTo("subject", subject)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot snapshot : Objects.requireNonNull(task.getResult())) {
                                Log.d("LectureActivity", snapshot.getId() + " :" + snapshot.getData());
                                textViewDescription.setText(snapshot.getString("description"));
                                if (snapshot.getString("UrlName") == null) {
                                    textViewPdfUrl.setText(urlName);
                                } else {
                                    textViewPdfUrl.setText(snapshot.getString("UrlName"));
                                }
                                Intent intent1 = new Intent(LectureActivity.this, PdfViewActivity.class);
                                intent1.putExtra("url", (snapshot.getString("url")));
                                startActivity(intent1);
                            }
                        } else {
                            Log.d("LectureActivity", "error was:", task.getException());
                        }
                    });
        });
        setupWindowAnimation();
    }

    private void setupWindowAnimation() {
        Transition slide = TransitionInflater.from(this).inflateTransition(R.transition.activity_fade);
        getWindow().setEnterTransition(slide);

    }
}