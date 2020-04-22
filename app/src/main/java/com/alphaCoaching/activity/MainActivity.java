package com.alphaCoaching.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alphaCoaching.Adapter.FireStoreAdapter;
import com.alphaCoaching.Model.RecentLecturesModel;
import com.alphaCoaching.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class MainActivity extends AppCompatActivity implements FireStoreAdapter.OnListItemclick {

    private FireStoreAdapter adapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        FirebaseFirestore mFireBaseDB = FirebaseFirestore.getInstance();

        //for toolbar
        Toolbar toolbar = findViewById(R.id.toolbars);
        setSupportActionBar(toolbar);

        //query
        Query query = mFireBaseDB.collection("recentLectures")
                .orderBy("lectureDate", Query.Direction.ASCENDING);

        //recycler options
        FirestoreRecyclerOptions<RecentLecturesModel> options = new FirestoreRecyclerOptions.Builder<RecentLecturesModel>()
                .setQuery(query, RecentLecturesModel.class)
                .build();
        adapter = new FireStoreAdapter(options, this);

        //two methods are declared in the recentLectureModel

        recyclerView.setAdapter(adapter);
    }


    @Override
    public void onItemclick(RecentLecturesModel snapshot, int position) {

        Intent intent = new Intent(MainActivity.this, LectureActivity.class);

        //to send data to another activity
        intent.putExtra("date", snapshot.getLectureDate());
        intent.putExtra("chaptername", snapshot.getChapterName());
        intent.putExtra("subject", snapshot.getSubject());
        startActivity(intent);
    }


    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
