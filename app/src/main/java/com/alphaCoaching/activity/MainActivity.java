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
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.alphaCoaching.Model.RecentLecturesModel;
import com.alphaCoaching.R;
import com.alphaCoaching.adapter.FireStoreAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, FireStoreAdapter.OnListItemclick {
    private DrawerLayout drawerLayout;
    private FirebaseAuth fireAuth;
    private FireStoreAdapter adapter;
    private RecyclerView recyclerView;
//    private FirebaseFirestore mFireBaseDB;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        FirebaseFirestore mFireBaseDB = FirebaseFirestore.getInstance();
        fireAuth = FirebaseAuth.getInstance();
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);


        Query query = mFireBaseDB.collection("recentLectures");
//                .orderBy("lectureDate", Query.Direction.ASCENDING);

        //recycler options
        FirestoreRecyclerOptions<RecentLecturesModel> options = new FirestoreRecyclerOptions.Builder<RecentLecturesModel>()
                .setQuery(query, RecentLecturesModel.class)
                .build();
        Log.d("Shubham", "onCreate: size of recent lectures: " + options.getSnapshots().size());
        adapter = new FireStoreAdapter(options, this);

        Log.d("shubham", "onCreate: coount: " + adapter.getItemCount());
        //two methods are declared in the recentLectureModel
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void onItemclick(RecentLecturesModel snapshot, int position) {
        Log.d("Shubham", "onItemclick: recent lecture is clicked");
        Intent intent = new Intent(MainActivity.this, LectureActivity.class);
        //to send data to another activity
        intent.putExtra("date", snapshot.getLectureDate());
        intent.putExtra("chaptername", snapshot.getChapterName());
        intent.putExtra("subject", snapshot.getSubject());
        startActivity(intent);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_quiz) {
            Log.d("Shubham", "onNavigationItemSelected: Quiz");
            loadQuizData();
        } else if (id == R.id.nav_home) {
            Log.d("Shubham", "onNavigationItemSelected: HomeMenu ");
            onBackPressed();
        } else if (id == R.id.nav_logout) {
            Log.d("Shubham", "onNavigationItemSelected: Logout");
            fireAuth.signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadQuizData() {
        Toast.makeText(this, "Into New Acivity", Toast.LENGTH_LONG).show();
//        Intent i=new Intent(MainActivity.this,QuizDetailActivity.class);
//        startActivity(i);
//        finish();
       /* catList.clear();
        firestore.collection("quiz").
                get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots) {
                    Note note=documentSnapshot.toObject(Note.class);
                    note.setDocumentId(documentSnapshot.getId());
                    documentId=note.getDocumentId();
                    String quizNam=note.getQuizName();
                    String questionNum= String.valueOf(note.getQuestionNumber());
                    String quizTime= String.valueOf(note.getQuizTime());
                    catList.add("quizName=  "+quizNam+"\n questionNumber=  "+questionNum+"\n quizTime=  "+quizTime);
                    catList2.add(documentId);
                }
            }
        });*/
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
