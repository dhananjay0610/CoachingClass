package com.alphaCoaching.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Adapter;
import android.widget.Spinner;

import com.alphaCoaching.Constant.Constant;
import com.alphaCoaching.Model.SpinnerModel;
import com.alphaCoaching.Model.SubjectModel;
import com.alphaCoaching.Model.VideoCategoryModel;
import com.alphaCoaching.Model.VideoModel;
import com.alphaCoaching.R;
import com.alphaCoaching.Utils.UserSharedPreferenceManager;
import com.alphaCoaching.adapter.SpinnerAdapter;
import com.alphaCoaching.adapter.VideoAdapter;
import com.alphaCoaching.adapter.VideoCategoryAdapter;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

import static com.alphaCoaching.AlphaApplication.getAppContext;

public class VideoCategoryListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, VideoCategoryAdapter.OnVideoItemClick {

    private FirebaseFirestore firestore;
    private Toolbar toolbar;
    private static RecyclerView mRecyclerView;
    private Spinner mFilterSpinner;
    private DrawerLayout mDrawerLayout;
    private static ArrayList<VideoCategoryModel> mVideosCategoryList;
    private static VideoCategoryAdapter adapter;
    private NavigationView navigationView;
    private FirebaseAuth fireAuth;
    private static Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_category_list);

        toolbar = findViewById(R.id.ToolbarOfVideosActivity);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Chapters");
        mRecyclerView = findViewById(R.id.recycler_viewVideos);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mFilterSpinner = findViewById(R.id.video_dropdown_menu);
        mDrawerLayout = findViewById(R.id.videosDrawer);
        fireAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        mContext = getAppContext();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        getAllVideoCategories();
        getAllSubjects();
    }

    private void getAllVideoCategories() {
        mVideosCategoryList = new ArrayList<>();
        firestore.collection(Constant.VIDEO_CATEGORY_COLLECTION)
                .whereEqualTo(Constant.UserCollectionFields.STANDARD, UserSharedPreferenceManager.getUserInfo(getAppContext(), UserSharedPreferenceManager.userInfoFields.USER_STANDARD))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot snapshot : Objects.requireNonNull(task.getResult())) {
                            VideoCategoryModel model = snapshot.toObject(VideoCategoryModel.class);
                            mVideosCategoryList.add(model);
                        }
                        adapter = new VideoCategoryAdapter(mVideosCategoryList, this);
                        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                        mRecyclerView.setAdapter(adapter);
                    }
                });
    }

    private void getAllSubjects() {
        ArrayList<SubjectModel> subjects = new ArrayList<>();
        firestore.collection(Constant.SUBJECT_COLLECTION)
                .whereEqualTo(Constant.UserCollectionFields.STANDARD, UserSharedPreferenceManager.getUserInfo(getAppContext(), UserSharedPreferenceManager.userInfoFields.USER_STANDARD))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int i = 0;
                        for (QueryDocumentSnapshot snapshot : Objects.requireNonNull(task.getResult())) {
                            UserSharedPreferenceManager.storeUserSubjects(getApplicationContext(), snapshot.getId(), Objects.requireNonNull(snapshot.get("name")).toString());
                            subjects.add(snapshot.toObject(SubjectModel.class));
                            subjects.get(i).setId(snapshot.getId());
                            i++;
                        }
                        setSpinner(subjects);
                    }
                });
    }

    private void setSpinner(ArrayList<SubjectModel> Subjects) {
        ArrayList<SpinnerModel> list = new ArrayList<>();

        for (SubjectModel s : Subjects) {
            SpinnerModel object = new SpinnerModel();
            object.setTitle(s.getName());
            object.setSelected(false);
            object.setId(s.getId());
            list.add(object);
        }
        SpinnerAdapter myAdapter = new SpinnerAdapter(getApplicationContext(), 0, list, SpinnerAdapter.VIDEO_TYPE);
        mFilterSpinner.setAdapter(myAdapter);
    }

    public void updateAdapter(ArrayList<VideoCategoryModel> list) {
        adapter = new VideoCategoryAdapter(list, this);
        mRecyclerView.setAdapter(adapter);
        adapter.notifyItemRangeChanged(0, list.size());
    }

    public ArrayList<VideoCategoryModel> getmVideosCategoryList() {
        return mVideosCategoryList;
    }

    @Override
    public void onItemClick(VideoCategoryModel snapshot, int position) {
        Intent intent = new Intent(mContext, VideosActivity.class);
        intent.putExtra("categoryId", snapshot.getUid());
        mContext.startActivity(intent);
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
        } else if (id == R.id.nav_tutos) {
            openTutosActivity();
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadHomeActivity() {
        Intent i = new Intent(VideoCategoryListActivity.this, MainActivity.class);
        startActivity(i);
    }

    private void loadQuizData() {
        Intent intent = new Intent(VideoCategoryListActivity.this, QuizListActivity.class);
        startActivity(intent);
    }

    private void loadPdfList() {
        Intent i = new Intent(VideoCategoryListActivity.this, PdfListActivity.class);
        startActivity(i);
    }

    private void loadUserData() {
        Intent i = new Intent(VideoCategoryListActivity.this, UserProfileActivity.class);
        startActivity(i);
    }

    private void openTutosActivity() {
        Intent intent = new Intent(this, TutosActivity.class);
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
