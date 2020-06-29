package com.alphaCoaching.activity;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alphaCoaching.Constant.Constant;
import com.alphaCoaching.Model.RecentLecturesModel;
import com.alphaCoaching.R;
import com.alphaCoaching.Utils.UserSharedPreferenceManager;
import com.alphaCoaching.adapter.FireStoreAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, FireStoreAdapter.OnListItemclick {
    private DrawerLayout drawerLayout;
    private FirebaseAuth fireAuth;
    private FireStoreAdapter adapter;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    NavigationView navigationView;
    FrameLayout frameLayout;
    ActionBarDrawerToggle toggle;
    private FirebaseFirestore mFireBaseDB;
    private ImageView mNotificationIcon;
    private MenuItem userName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mFireBaseDB = FirebaseFirestore.getInstance();
        fireAuth = FirebaseAuth.getInstance();
        drawerLayout = findViewById(R.id.drawer_layout);
        frameLayout = findViewById(R.id.fragment_container);
        mNotificationIcon = findViewById(R.id.notification_icon);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
//        userName = (MenuItem) findViewById(R.id.nav_name);
//        userName.setTitle(UserSharedPreferenceManager.getUserInfo(getApplicationContext(), UserSharedPreferenceManager.userInfoFields.USER_FIRST_NAME)
//                + UserSharedPreferenceManager.getUserInfo(getApplicationContext(), UserSharedPreferenceManager.userInfoFields.USER_LAST_NAME));
        checkRecentLectures();
        mNotificationIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, NotificationListsActivity.class);
                startActivity(i);
            }
        });
    }

    private void checkRecentLectures() {
        String standard = UserSharedPreferenceManager.getUserInfo(getApplicationContext(), UserSharedPreferenceManager.userInfoFields.USER_STANDARD);
        if (standard == null) {
            FirebaseUser currentUser = fireAuth.getCurrentUser();
            assert currentUser != null;
            String user_Uuid = currentUser.getUid();
            DocumentReference documentReference = mFireBaseDB.collection(Constant.USER_COLLECTION).document(user_Uuid);
            final String[] std = {""};
            documentReference.get().addOnCompleteListener(task1 -> {
                if (task1.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task1.getResult();
                    assert documentSnapshot != null;
                    if (documentSnapshot.exists()) {
                        std[0] = (String) documentSnapshot.get(Constant.UserCollectionFields.STANDARD);
                        getRecentLecture(std[0]);
                        UserSharedPreferenceManager.storeUserField(getApplicationContext(), UserSharedPreferenceManager.userInfoFields.USER_STANDARD, std[0]);
                    }
                }
            });
        } else {
            getRecentLecture(standard);
        }
    }

    private void getRecentLecture(String standard) {
        Query query = mFireBaseDB.collection(Constant.RECENT_LECTURE_COLLECTION)
                .whereEqualTo(Constant.UserCollectionFields.STANDARD, standard)
                .orderBy(Constant.RecentLectureFields.LECTURE_DATE, Query.Direction.DESCENDING);

        //recycler options
        FirestoreRecyclerOptions<RecentLecturesModel> options = new FirestoreRecyclerOptions.Builder<RecentLecturesModel>()
                .setQuery(query, RecentLecturesModel.class)
                .build();
        adapter = new FireStoreAdapter(options, this);
        adapter.startListening();
        //two methods are declared in the recentLectureModel
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemclick(RecentLecturesModel snapshot, int position) {
        Intent intent = new Intent(MainActivity.this, LectureActivity.class);
        //to send data to another activity
        intent.putExtra("date", snapshot.getLectureDate());
        intent.putExtra("chapterName", snapshot.getChapterName());
        intent.putExtra("subject", snapshot.getSubject());
        intent.putExtra("videoUrl",snapshot.getVideoUrl());
        startActivity(intent);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_quiz) {
            loadQuizData();
        } else if (id == R.id.nav_home) {
            onBackPressed();
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
            startVideoActivity();
        } else if (id == R.id.nav_tutos) {
            openTutosActivity();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadPdfList() {
        Intent i = new Intent(MainActivity.this, PdfListActivity.class);
        startActivity(i);
    }

    private void loadUserData() {
        Intent i = new Intent(MainActivity.this, UserProfileActivity.class);
        startActivity(i);
    }

    private void loadQuizData() {
        Intent intent = new Intent(MainActivity.this, QuizListActivity.class);
        startActivity(intent);
    }

    private void startVideoActivity() {
        Intent intent = new Intent(MainActivity.this, VideoCategoryListActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void openTutosActivity() {
        Intent intent = new Intent(this, TutosActivity.class);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adapter.stopListening();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
        navigationView = findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(1).setChecked(true);
        navigationView.setNavigationItemSelectedListener(this);
        adapter.startListening();

    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}