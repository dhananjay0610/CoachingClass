package com.alphaCoaching.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.alphaCoaching.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

@SuppressLint("Registered")
public class LectureActivity extends AppCompatActivity {
    FirebaseFirestore mFireBaseDB;
    TextView textViewdescription;
    TextView textViewchapter;
    TextView textViewpdfurl;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecture);

        Toolbar toolbar = findViewById(R.id.toolbarOfLectureActivity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFireBaseDB = FirebaseFirestore.getInstance();

        textViewdescription = findViewById(R.id.textviewdescription);
        textViewchapter = findViewById(R.id.textviewchapter);
        textViewpdfurl = findViewById(R.id.textViewpdfurl);

        //getting data from mainActivity
        Intent intent = getIntent();
        String chaptername = intent.getStringExtra("chaptername");
        String subject = intent.getStringExtra("subject");
        textViewchapter.setText(chaptername);
        getSupportActionBar().setTitle("");

        //for url
        final String[] url = {""};
        final String urlname = "pdf 1";

        //fetching data from firestore
        mFireBaseDB.collection("recentLectures")
                .whereEqualTo("chapterName", chaptername)
                .whereEqualTo("subject", subject)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                Log.d("from firestore", snapshot.getId() + " :" + snapshot.getData());
                                textViewdescription.setText(snapshot.getString("description"));

                                if (snapshot.getString("UrlName") == null) {
                                    textViewpdfurl.setText(urlname);
                                } else {
                                    textViewpdfurl.setText(snapshot.getString("UrlName"));
                                }
                                url[0] += (snapshot.getString("url"));
                            }
                        } else {
                            Log.d("from firestrore", "error was:", task.getException());
                        }
                    }
                });


        //Youtube viewer

        YouTubePlayerView youTubePlayerView = findViewById(R.id.youtube_player_view);
        getLifecycle().addObserver(youTubePlayerView);

        //for pdf url
        textViewpdfurl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uri = url[0];
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(uri));
                startActivity(i);
            }
        });
    }
}

//2020-04-15 07:40:02.191 25268-25268/com.alphaCoaching D/from firestore: 20200409160637 :{subject=Chemistry, chapterName=Biomolecules, description=This chapter focuses on the building blocks of living organisms, the biomolecules, lectureDate=Timestamp(seconds=1586428622, nanoseconds=704000000), url=https://firebasestorage.googleapis.com/v0/b/alphacoaching-403bb.appspot.com/o/uploads%2F1586428597010.pdf?alt=media&token=c56b8d45-8399-4817-abbd-cdf9773d72a5}