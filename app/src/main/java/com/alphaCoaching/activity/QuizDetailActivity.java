package com.alphaCoaching.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.alphaCoaching.R;
import com.alphaCoaching.adapter.NoteAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.alphaCoaching.Model.Note;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class QuizDetailActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private NoteAdapter adapter;
    FirebaseAuth fireAuth;
    String finalQuiztakenid;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_detail);

        Toolbar toolbar = findViewById(R.id.toolbarofquizactivity);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Review your Questions");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Query query = db.collection("quiz");
        FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query, Note.class)
                .build();
        adapter = new NoteAdapter(options);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        adapter.setOnItemClickListener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {

                String id = documentSnapshot.getId();
                fireAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = fireAuth.getCurrentUser();
                assert currentUser != null;
                String user_Uuid = currentUser.getUid();
                FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
                CollectionReference yourCollRef = rootRef.collection("quizTaken");
                Query query = yourCollRef.whereEqualTo("quizId", id)
                        .whereEqualTo("userId", user_Uuid);
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            String score = "";
                            String totalscore = "";
                            String quiztakenid = null;
                            for (QueryDocumentSnapshot documentSnapshot1 : task.getResult()) {
                                score = (String) documentSnapshot1.get("score");
                                totalscore = (String) documentSnapshot1.get("TotalScore");
                                Log.d("QuizDetailActivity", "" + documentSnapshot1.getId() + "   " + documentSnapshot1.getData());
                                quiztakenid = documentSnapshot1.getId();
                            }
                            if (score.isEmpty() || totalscore.isEmpty()) {
                                Note note = documentSnapshot.toObject(Note.class);
                                long timequiz = (long) documentSnapshot.get("quizTime");
                                String path = documentSnapshot.getReference().getPath();
                                Intent i = new Intent(QuizDetailActivity.this, QuestionDetailActivity.class);
                                i.putExtra("docID", id);
                                i.putExtra("quizTime", timequiz);
                                startActivity(i);
                            } else {
                                Toast.makeText(QuizDetailActivity.this, "Already appeared for this test..........", Toast.LENGTH_SHORT).show();

                                finalQuiztakenid = quiztakenid;
                            }
                        }
                    }
                });
            }
        });

        adapter.setonButtonClickListener(new NoteAdapter.MyAdapterListener() {
            @Override
            public void buttonOnClick(View v, int position, DocumentSnapshot documentSnapshot) {


                String id = documentSnapshot.getId();
                fireAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = fireAuth.getCurrentUser();
                String user_Uuid = currentUser.getUid();
                FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
                CollectionReference yourCollRef = rootRef.collection("quizTaken");
                Query query = yourCollRef.whereEqualTo("quizId", id)
                        .whereEqualTo("userId", user_Uuid);
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            String score = "";
                            String totalscore = "";
                            String quiztakenid = null;
                            for (QueryDocumentSnapshot documentSnapshot1 : task.getResult()) {
                                score = (String) documentSnapshot1.get("score");
                                totalscore = (String) documentSnapshot1.get("TotalScore");
                                Log.d("QuizDetailActivity", "" + documentSnapshot1.getId() + "   " + documentSnapshot1.getData());
                                quiztakenid = documentSnapshot1.getId();
                            }
                            if (score.isEmpty() || totalscore.isEmpty()) {
                                Toast.makeText(QuizDetailActivity.this, "Not Appeared for this test", Toast.LENGTH_SHORT).show();
                            } else {
                                finalQuiztakenid = quiztakenid;
                                Intent i = new Intent(QuizDetailActivity.this, QuestionReview.class);
                                i.putExtra("QuizId", documentSnapshot.getId());
                                i.putExtra("quickened", finalQuiztakenid);
                                startActivity(i);
                                Log.d("QuizDetailActivity", "quiztaken id and quizid is : " + finalQuiztakenid + "  " + documentSnapshot.getId());
                                // Toast.makeText(QuizDetailActivity.this, "Quiz Id : " + documentSnapshot.getId(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
            }
        });
    }
}