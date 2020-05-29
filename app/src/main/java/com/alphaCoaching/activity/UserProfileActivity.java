package com.alphaCoaching.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.alphaCoaching.R;
import com.alphaCoaching.Utils.UserSharedPreferenceManager;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import static com.alphaCoaching.Utils.UserSharedPreferenceManager.USER_DETAIL;

//import com.alphaCoaching.Constant.Constant;

public class UserProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private FirebaseAuth fireAuth;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        toolbar = findViewById(R.id.ToolbarOfUserProfileActivity);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        fireAuth = FirebaseAuth.getInstance();
        drawerLayout = findViewById(R.id.userlayout);
        NavigationView navigationView = findViewById(R.id.nav_views);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        TextView userName = findViewById(R.id.UserName);
        TextView user_email = findViewById(R.id.user_email);
        TextView user_name = findViewById(R.id.user_name);
        TextView user_dob = findViewById(R.id.user_dob);
        TextView user_standard = findViewById(R.id.user_standard);

        SharedPreferences sharedPreferences = this.getSharedPreferences(USER_DETAIL, MODE_PRIVATE);
        String userFirstName = sharedPreferences.getString("UserFirstName", "Username");
        String userLastName = sharedPreferences.getString("UserLastName", "User last Name");
        String userEmail = sharedPreferences.getString("UserEmail", "User email");
        String UserDateOfBirth = sharedPreferences.getString("UserDateOfBirth", "00-00-00");
        String UserStandard = sharedPreferences.getString("UserStandard", "9");
        DocumentReference documentReference = db.collection("standard").document(UserStandard);
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
        userName.setText(userFirstName);
        String UserFullName = userFirstName + " " + userLastName;
        user_name.setText(UserFullName);
        user_email.setText(userEmail);
        user_dob.setText(UserDateOfBirth);
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_quiz) {
            loadQuizData();
        } else if (id == R.id.nav_home) {
            loadHomeActivity();
        } else if (id == R.id.nav_userProfile) {
            onBackPressed();
        } else if (id == R.id.nav_logout) {
            fireAuth.signOut();
            UserSharedPreferenceManager.removeUserData(getApplicationContext());
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadHomeActivity() {
        Intent i = new Intent(UserProfileActivity.this, MainActivity.class);
        startActivity(i);
    }


    private void loadQuizData() {
        Intent i = new Intent(UserProfileActivity.this, QuizDetailActivity.class);
        startActivity(i);

    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

}
