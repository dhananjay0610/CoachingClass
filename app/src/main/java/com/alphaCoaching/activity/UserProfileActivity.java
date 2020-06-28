package com.alphaCoaching.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.alphaCoaching.Constant.Constant;
import com.alphaCoaching.R;
import com.alphaCoaching.Utils.UserSharedPreferenceManager;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class UserProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private FirebaseAuth fireAuth;
    private FirebaseFirestore db;
    private Toolbar toolbar;
    // private UserSharedPreferenceManager preferenceManager;
    private Context mContext;
    private String mUserFirstName;
    private String mUserLastName;
    private String mUserEmail;
    private String mUserDateOfBirth;
    private String mUserStandard;
    private TextView userName, user_email, user_name, user_dob, user_standard;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        setContentView(R.layout.activity_user_profile);
        setupWindowAnimation();
        toolbar = findViewById(R.id.ToolbarOfUserProfileActivity);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        fireAuth = FirebaseAuth.getInstance();
        setupUI();

        DocumentReference documentReference = db.collection(Constant.STANDARD_COLLECTION).document(mUserStandard);
        documentReference.get().addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task1.getResult();
                assert documentSnapshot != null;
                if (documentSnapshot.exists()) {
                    long standard = (long) documentSnapshot.get("standard");
                    String Standard = standard + "";
                    user_standard.setText(Standard);
                }
            }
        });
        userName.setText(mUserFirstName);
        String UserFullName = mUserFirstName + " " + mUserLastName;
        user_name.setText(UserFullName);
        user_email.setText(mUserEmail);
        user_dob.setText(mUserDateOfBirth);
    }

    private void setupUI() {
        drawerLayout = findViewById(R.id.userlayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        db = FirebaseFirestore.getInstance();
        userName = findViewById(R.id.UserName);
        user_email = findViewById(R.id.user_email);
        user_name = findViewById(R.id.user_name);
        user_dob = findViewById(R.id.user_dob);
        user_standard = findViewById(R.id.user_standard);
        mUserFirstName = UserSharedPreferenceManager.getUserInfo(mContext, UserSharedPreferenceManager.userInfoFields.USER_FIRST_NAME);
        mUserLastName = UserSharedPreferenceManager.getUserInfo(mContext, UserSharedPreferenceManager.userInfoFields.USER_LAST_NAME);
        mUserEmail = UserSharedPreferenceManager.getUserInfo(mContext, UserSharedPreferenceManager.userInfoFields.USER_EMAIL);
        mUserDateOfBirth = UserSharedPreferenceManager.getUserInfo(mContext, UserSharedPreferenceManager.userInfoFields.USER_DOB);
        mUserStandard = UserSharedPreferenceManager.getUserInfo(mContext, UserSharedPreferenceManager.userInfoFields.USER_STANDARD);
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
            onBackPressed();
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
        Intent i = new Intent(UserProfileActivity.this, PdfListActivity.class);
        startActivity(i);
    }

    private void loadHomeActivity() {
        Intent i = new Intent(UserProfileActivity.this, MainActivity.class);
        startActivity(i);
    }

    private void loadQuizData() {
        Intent i = new Intent(UserProfileActivity.this, QuizListActivity.class);
        startActivity(i);

    }

    private void openTutosActivity() {
        Intent intent = new Intent(this, TutosActivity.class);
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

    private void startVideoActivity() {
        Intent intent = new Intent(UserProfileActivity.this, VideoCategoryListActivity.class);
        startActivity(intent);
    }


    private void setupWindowAnimation() {
        Transition slide = TransitionInflater.from(this).inflateTransition(R.transition.activity_fade);
        getWindow().setEnterTransition(slide);
    }

    @Override
    protected void onStart() {
        super.onStart();
        navigationView = findViewById(R.id.nav_views);
        navigationView.getMenu().getItem(4).setChecked(true);
        navigationView.setNavigationItemSelectedListener(this);
    }
}
