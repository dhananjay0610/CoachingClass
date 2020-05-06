package com.alphaCoaching.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.alphaCoaching.R;
import com.alphaCoaching.adapter.NoteAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.alphaCoaching.model.Note;

public class QuizDetailActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    //CollectionReference notebookRef=db.collection("quiz");
    private NoteAdapter adapter;
Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_detail);


        toolbar=findViewById(R.id.toolbarofquizactivity);
        toolbar.setTitle("Quiz");
        setSupportActionBar(toolbar);
       // setActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);



        RecyclerView recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Query query = db.collection("quiz");
        FirestoreRecyclerOptions<Note> options=new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query,Note.class)
                .build();
        adapter=new NoteAdapter(options);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        adapter.setOnItemClickListener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Note note = documentSnapshot.toObject(Note.class);
                String id = documentSnapshot.getId();
              //  String timequizs= documentSnapshot.getString("quizTime");
                long timequiz= (long) documentSnapshot.get("quizTime");
                Log.d("Dhananjay","mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm"+documentSnapshot.get("quizTime")+" "+timequiz);
                String path = documentSnapshot.getReference().getPath();
              //  Toast.makeText(QuizDetailActivity.this,"Position: " + position + " ID: " + id, Toast.LENGTH_SHORT).show();
                Intent i=new Intent(QuizDetailActivity.this,QuestionDetailActivity.class);
                i.putExtra("docID",id);
                i.putExtra("quizTime",timequiz);
                startActivity(i);
            }
        });
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
