package com.alphaCoaching.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alphaCoaching.Constant.Constant;
import com.alphaCoaching.Model.Note;
import com.alphaCoaching.Model.QuizTaken;
import com.alphaCoaching.R;
import com.alphaCoaching.Utils.UserSharedPreferenceManager;
import com.alphaCoaching.adapter.NoteAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class QuizListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, Serializable {
    private DrawerLayout drawerLayout;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private NoteAdapter adapter;
    private RecyclerView recyclerView;
    private ArrayList<Note> quizList;
    private ArrayList<QuizTaken> quizTakenList;
    private FirebaseAuth fireAuth;
    private ProgressBar mProgressBar;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_list);
        setupWindowAnimation();
        Toolbar toolbar = findViewById(R.id.toolbarOfQuizActivity);
        setSupportActionBar(toolbar);
        recyclerView = findViewById(R.id.recycler_view);
        mProgressBar = findViewById(R.id.quizListProgressbar);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        quizList = new ArrayList<>();
        quizTakenList = new ArrayList<>();
        drawerLayout = findViewById(R.id.quizDrawer);
        fireAuth = FirebaseAuth.getInstance();
//        navigationView = findViewById(R.id.nav_viewOfQuizActivity);
//        navigationView.getMenu().getItem(2).setChecked(true);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
//        navigationView.setNavigationItemSelectedListener(this);

        getQuizAndTakenQuizData();
    }
    private void setupWindowAnimation() {
        Transition slide = TransitionInflater.from(this).inflateTransition(R.transition.activity_fade);
        getWindow().setEnterTransition(slide);
    }
    private void setupAdapter() {
        adapter.setOnItemClickListener((isQuizTaken, quizModel) -> {
            if (!isQuizTaken) {
                Intent i = new Intent(QuizListActivity.this, QuizDetailActivity.class);
                i.putExtra("quizStartTime", quizModel.getQuizDate().getSeconds() * 1000);
                i.putExtra("docID", quizModel.getId());
                i.putExtra("quizTime", String.valueOf(quizModel.getQuizTime()));
                i.putExtra("quizName", quizModel.getQuizName());
                i.putExtra("quizQuestions", String.valueOf(quizModel.getQuestionNumber()));
                startActivity(i);
            } else {
                Toast.makeText(getApplicationContext(), "You are already attempted the quiz!", Toast.LENGTH_LONG).show();
            }
        });


        adapter.setonButtonClickListener((v, quizTakenId, quizId) -> {
            Intent i = new Intent(QuizListActivity.this, QuizReviewActivity.class);
            i.putExtra("QuizId", quizId);
            i.putExtra("quickened", quizTakenId);
            startActivity(i);
        });
    }

    private void getQuizAndTakenQuizData() {
        mProgressBar.setVisibility(View.VISIBLE);
        db.collection(Constant.QUIZ_COLLECTION)
                .whereEqualTo(Constant.QuizCollectionFields.STANDARD, UserSharedPreferenceManager.getUserInfo(getApplicationContext(), UserSharedPreferenceManager.userInfoFields.USER_STANDARD))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            Note quiz = document.toObject(Note.class);
                            quiz.setId(document.getId());
                            quizList.add(quiz);
                        }
                        getTakenQuizData(quizList);
                    }
//                    else {
//
//                    }
                });
    }

    private void getTakenQuizData(ArrayList<Note> quizList) {
        db.collection(Constant.QUIZ_TAKEN_COLLECTION)
                .whereEqualTo(Constant.QuizTakenCollectionFields.USER_ID, UserSharedPreferenceManager.getUserInfo(getApplicationContext(), UserSharedPreferenceManager.userInfoFields.USER_UUID))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                QuizTaken quiz = document.toObject(QuizTaken.class);
                                quiz.setId(document.getId());
                                quizTakenList.add(quiz);
                            }
                            adapter = new NoteAdapter(QuizListActivity.this, quizList, quizTakenList);
                            recyclerView.setAdapter(adapter);
                            recyclerView.setHasFixedSize(true);
                            mProgressBar.setVisibility(View.GONE);
                            setupAdapter();
                        }
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        return super.onSupportNavigateUp();
    }

    @Override

    protected void onStart() {
        super.onStart();
        navigationView = findViewById(R.id.nav_viewOfQuizActivity);
        navigationView.getMenu().getItem(2).setChecked(true);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @SuppressLint("RtlHardcoded")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_quiz) {
            onBackPressed();
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
            startVideoActivity();
        } else if (id == R.id.nav_tutos) {
            openTutosActivity();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadHomeActivity() {
        Intent i = new Intent(QuizListActivity.this, MainActivity.class);
        startActivity(i);
    }

    private void openTutosActivity() {
        Intent intent = new Intent(this, TutosActivity.class);
        startActivity(intent);
    }

    private void loadUserData() {
        Intent i = new Intent(QuizListActivity.this, UserProfileActivity.class);
        startActivity(i);
    }

    private void loadPdfList() {
        Intent i = new Intent(QuizListActivity.this, PdfListActivity.class);
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

    private void startVideoActivity() {
        Intent intent = new Intent(QuizListActivity.this, VideoCategoryListActivity.class);
        startActivity(intent);
    }
}