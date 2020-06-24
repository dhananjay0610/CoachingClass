package com.alphaCoaching.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Spinner;

import com.alphaCoaching.Constant.Constant;
import com.alphaCoaching.Model.VideoModel;
import com.alphaCoaching.R;
import com.alphaCoaching.Utils.UserSharedPreferenceManager;
import com.alphaCoaching.adapter.PDFAdapter;
import com.alphaCoaching.adapter.VideoAdapter;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

import static com.alphaCoaching.AlphaApplication.getAppContext;

public class VideosActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, VideoAdapter.OnVideoItemClick {

    private FirebaseFirestore firestore;
    private Toolbar toolbar;
    private static RecyclerView mRecyclerView;
    private Spinner mFilterSpinner;
    private DrawerLayout mDrawerLayout;
    private ArrayList<VideoModel> mVideosList;
    private static VideoAdapter adapter;
    private NavigationView navigationView;
    private FirebaseAuth fireAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos);

        toolbar = findViewById(R.id.ToolbarOfVideosActivity);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Videos");
        mRecyclerView = findViewById(R.id.recycler_viewVideos);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mFilterSpinner = findViewById(R.id.video_dropdown_menu);
        mDrawerLayout = findViewById(R.id.videosDrawer);
        fireAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        getAllVideos();
    }

    private void getAllVideos() {
        mVideosList = new ArrayList<>();
        firestore.collection(Constant.VIDEO_COLLECTION)
                .whereEqualTo(Constant.UserCollectionFields.STANDARD, UserSharedPreferenceManager.getUserInfo(getAppContext(), UserSharedPreferenceManager.userInfoFields.USER_STANDARD))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot snapshot : Objects.requireNonNull(task.getResult())) {
                            VideoModel model = snapshot.toObject(VideoModel.class);
                            model.setId(snapshot.getId());
                            mVideosList.add(model);
                        }
                        adapter = new VideoAdapter(mVideosList, this);
                        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                        mRecyclerView.setAdapter(adapter);
                    }
                });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_quiz) {
            loadQuizData();
        } else if (id == R.id.nav_home) {
            loadHomeActivity();
        } else if (id == R.id.nav_Pdf) {
            loadPdfList();
        } else if (id == R.id.nav_userProfile) {
            loadUserData();
        } else if (id == R.id.nav_logout) {
            new LoginActivity().logoutUser(UserSharedPreferenceManager.getUserInfo(getApplicationContext(), UserSharedPreferenceManager.userInfoFields.USER_UUID));
            fireAuth.signOut();
            UserSharedPreferenceManager.removeUserData(getApplicationContext());
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_videos) {
            onBackPressed();
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadHomeActivity() {
        Intent i = new Intent(VideosActivity.this, MainActivity.class);
        startActivity(i);
    }

    private void loadQuizData() {
        Intent intent = new Intent(VideosActivity.this, QuizListActivity.class);
        startActivity(intent);
    }

    private void loadPdfList() {
        Intent i = new Intent(VideosActivity.this, PdfListActivity.class);
        startActivity(i);
    }

    private void loadUserData() {
        Intent i = new Intent(VideosActivity.this, UserProfileActivity.class);
        startActivity(i);
    }

    @Override
    public void onItemClick(VideoModel snapshot, int position) {
        Intent intent=new Intent(VideosActivity.this,DisplayVideos.class);
        intent.putExtra("force_fullscreen",true);
        intent.putExtra("url",snapshot.getUrl());
        intent.putExtra("title",snapshot.getName());
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        navigationView = findViewById(R.id.nav_viewOfVideosActivity);
        navigationView.getMenu().getItem(5).setChecked(true);
        navigationView.setNavigationItemSelectedListener(this);
    }
}
