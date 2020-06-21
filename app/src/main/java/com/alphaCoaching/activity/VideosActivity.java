package com.alphaCoaching.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
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
        firestore = FirebaseFirestore.getInstance();

        NavigationView navigationView = findViewById(R.id.nav_viewOfVideosActivity);
        navigationView.getMenu().getItem(4).setChecked(true);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);


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
        return false;
    }

    @Override
    public void onItemClick(VideoModel snapshot, int position) {
        Log.d("Shubham", "onItemClick: item is clicked!");
    }
}
