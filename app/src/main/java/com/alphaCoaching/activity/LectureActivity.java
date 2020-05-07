package com.alphaCoaching.activity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.alphaCoaching.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.BaseMultiplePermissionsListener;

import java.util.List;


public class LectureActivity extends AppCompatActivity {
    FirebaseFirestore mFireBaseDB;
    TextView textViewdescription;
    TextView textViewchapter;
    TextView textViewpdfurl;
    private static final int PICK_PDF_CODE = 1000;

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
        String urlname = "pdf 1";

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

        //for the new work

        //request read and write externmal
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new BaseMultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        super.onPermissionsChecked(report);
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        super.onPermissionRationaleShouldBeShown(permissions, token);
                    }
                })
                .check();

        textViewpdfurl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LectureActivity.this, PdfViewActivity.class);
                intent.putExtra("Viewtype", "internet");
                intent.putExtra("url", ""+url[0]);
                startActivity(intent);
            }
        });

        //till here









        //for pdf url
//        textViewpdfurl.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String uri = url[0];
//                Intent i = new Intent(Intent.ACTION_VIEW);
//                i.setData(Uri.parse(uri));
//                startActivity(i);
//            }
//        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PDF_CODE && resultCode == RESULT_OK && data != null) {
            Uri selectedpdf = data.getData();
            Intent intent = new Intent(LectureActivity.this, PdfViewActivity.class);
            intent.putExtra("Viewtype", "storage");
            intent.putExtra("FileUri", selectedpdf.toString());
            startActivity(intent);
        }
    }




}


//public class LectureActivity extends AppCompatActivity {
//    FirebaseFirestore mFireBaseDB;
//    TextView textViewdescription;
//    TextView textViewchapter;
//    TextView textViewpdfurl;
//
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_lecture);
//
//        Toolbar toolbar = findViewById(R.id.toolbarOfLectureActivity);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//        mFireBaseDB = FirebaseFirestore.getInstance();
//
//        textViewdescription = findViewById(R.id.textviewdescription);
//        textViewchapter = findViewById(R.id.textviewchapter);
//        textViewpdfurl = findViewById(R.id.textViewpdfurl);
//
//        //getting data from mainActivity
//        Intent intent = getIntent();
//        String chaptername = intent.getStringExtra("chaptername");
//        String subject = intent.getStringExtra("subject");
//        textViewchapter.setText(chaptername);
//        getSupportActionBar().setTitle("");
//
//        //for url
//        final String[] url = {""};
//        String urlname = "pdf 1";
//
//        //fetching data from firestore
//        mFireBaseDB.collection("recentLectures")
//                .whereEqualTo("chapterName", chaptername)
//                .whereEqualTo("subject", subject)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot snapshot : task.getResult()) {
//                                Log.d("from firestore", snapshot.getId() + " :" + snapshot.getData());
//                                textViewdescription.setText(snapshot.getString("description"));
//
//                                if (snapshot.getString("UrlName") == null) {
//                                    textViewpdfurl.setText(urlname);
//                                } else {
//                                    textViewpdfurl.setText(snapshot.getString("UrlName"));
//                                }
//                                url[0] += (snapshot.getString("url"));
//                            }
//                        } else {
//                            Log.d("from firestrore", "error was:", task.getException());
//                        }
//                    }
//                });
//
//
//        //Youtube viewer
//
//        YouTubePlayerView youTubePlayerView = findViewById(R.id.youtube_player_view);
//        getLifecycle().addObserver(youTubePlayerView);
//
//        //for pdf url
//        textViewpdfurl.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String uri = url[0];
//                Intent i = new Intent(Intent.ACTION_VIEW);
//                i.setData(Uri.parse(uri));
//                startActivity(i);
//            }
//        });
//    }
//}

