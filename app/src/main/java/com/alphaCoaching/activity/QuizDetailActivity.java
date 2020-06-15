package com.alphaCoaching.activity;

import android.content.Intent;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.MenuItem;
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
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class QuizDetailActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private NoteAdapter adapter;
    private RecyclerView recyclerView;
    private ArrayList<Note> quizList;
    private ArrayList<QuizTaken> quizTakenList;
    private FirebaseAuth fireAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_detail);
        setupWindowAnimation();
        Toolbar toolbar = findViewById(R.id.toolbarOfQuizActivity);
        setSupportActionBar(toolbar);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        quizList = new ArrayList<>();
        quizTakenList = new ArrayList<>();
        drawerLayout = findViewById(R.id.quizDrawer);
        NavigationView navigationView = findViewById(R.id.nav_viewOfQuizActivity);
        navigationView.getMenu().getItem(2).setChecked(true);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        getQuizAndTakenQuizData();
    }
    private void setupWindowAnimation() {
        Transition slide = TransitionInflater.from(this).inflateTransition(R.transition.activity_fade);
        getWindow().setEnterTransition(slide);
    }
    private void setupAdapter() {
        adapter.setOnItemClickListener((isQuizTaken, quizModel) -> {
            if (!isQuizTaken) {
                Intent i = new Intent(QuizDetailActivity.this, QuestionDetailActivity.class);
                i.putExtra("docID", quizModel.getId());
                i.putExtra("quizTime", quizModel.getQuizTime());
                startActivity(i);
            } else {
                Toast.makeText(getApplicationContext(), "You are already attempted the quiz!", Toast.LENGTH_LONG).show();
            }
        });


        adapter.setonButtonClickListener((v, quizTakenId, quizId) -> {
            Intent i = new Intent(QuizDetailActivity.this, QuizReviewActivity.class);
            i.putExtra("QuizId", quizId);
            i.putExtra("quickened", quizTakenId);
            startActivity(i);
        });
    }

    private void getQuizAndTakenQuizData() {
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
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            QuizTaken quiz = document.toObject(QuizTaken.class);
                            quiz.setId(document.getId());
                            quizTakenList.add(quiz);
                        }
                        adapter = new NoteAdapter(QuizDetailActivity.this, quizList, quizTakenList);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setHasFixedSize(true);
                        setupAdapter();
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
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

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
        Intent i = new Intent(QuizDetailActivity.this, MainActivity.class);
        startActivity(i);
    }

    private void loadUserData() {
        Intent i = new Intent(QuizDetailActivity.this, UserProfileActivity.class);
        startActivity(i);
    }

    private void loadPdfList() {
        Intent i = new Intent(QuizDetailActivity.this, PdfListActivity.class);
        startActivity(i);
    }

}