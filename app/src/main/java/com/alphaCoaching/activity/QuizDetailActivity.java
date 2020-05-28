package com.alphaCoaching.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alphaCoaching.Model.Note;
import com.alphaCoaching.R;
import com.alphaCoaching.adapter.NoteAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Objects;

public class QuizDetailActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private NoteAdapter adapter;
    FirebaseAuth fireAuth;
    String finalQuizTakenId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_detail);
        Toolbar toolbar = findViewById(R.id.toolbarOfQuizActivity);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Quiz");
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Query query = db.collection("quiz");
        FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query, Note.class)
                .build();
        adapter = new NoteAdapter(options);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        adapter.setOnItemClickListener((documentSnapshot, position) -> {
            String id = documentSnapshot.getId();
            fireAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = fireAuth.getCurrentUser();
            assert currentUser != null;
            String user_Uuid = currentUser.getUid();
            FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
            CollectionReference yourCollRef = rootRef.collection("quizTaken");
            Query query1 = yourCollRef.whereEqualTo("quizId", id)
                    .whereEqualTo("userId", user_Uuid);
            query1.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String score = "";
                    String totalscore = "";
                    String quiztakenid = null;
                    for (QueryDocumentSnapshot documentSnapshot1 : Objects.requireNonNull(task.getResult())) {
                        score = (String) documentSnapshot1.get("score");
                        totalscore = (String) documentSnapshot1.get("TotalScore");
                        Log.d("QuizDetailActivity", "" + documentSnapshot1.getId() + "   " + documentSnapshot1.getData());
                        quiztakenid = documentSnapshot1.getId();
                    }
                    assert score != null;
                    assert totalscore != null;
                    if (score.isEmpty() || totalscore.isEmpty()) {
                        //  Note note = documentSnapshot.toObject(Note.class);
                        long timeQuiz = (long) documentSnapshot.get("quizTime");
                        Intent i = new Intent(QuizDetailActivity.this, QuestionDetailActivity.class);
                        i.putExtra("docID", id);
                        i.putExtra("quizTime", timeQuiz);
                        startActivity(i);
                    } else {
                        Toast.makeText(QuizDetailActivity.this, "Already appeared for this test", Toast.LENGTH_SHORT).show();

                        finalQuizTakenId = quiztakenid;
                    }
                }
            });
        });
        adapter.setonButtonClickListener((v, position, documentSnapshot) -> {
            String id = documentSnapshot.getId();
            fireAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = fireAuth.getCurrentUser();
            assert currentUser != null;
            String user_Uuid = currentUser.getUid();
            FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
            CollectionReference yourCollRef = rootRef.collection("quizTaken");
            Query query12 = yourCollRef.whereEqualTo("quizId", id)
                    .whereEqualTo("userId", user_Uuid);
            query12.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String score = "";
                    String totalscore = "";
                    String quiztakenid = null;
                    for (QueryDocumentSnapshot documentSnapshot1 : Objects.requireNonNull(task.getResult())) {
                        score = (String) documentSnapshot1.get("score");
                        totalscore = (String) documentSnapshot1.get("TotalScore");
                        Log.d("QuizDetailActivity", "" + documentSnapshot1.getId() + "   " + documentSnapshot1.getData());
                        quiztakenid = documentSnapshot1.getId();
                    }
                    assert score != null;
                    assert totalscore != null;
                    if (score.isEmpty() || totalscore.isEmpty()) {
                        Toast.makeText(QuizDetailActivity.this, "Not Appeared for this test", Toast.LENGTH_SHORT).show();
                    } else {
                        finalQuizTakenId = quiztakenid;
                        Intent i = new Intent(QuizDetailActivity.this, QuestionReview.class);
                        i.putExtra("QuizId", documentSnapshot.getId());
                        i.putExtra("quickened", finalQuizTakenId);
                        startActivity(i);
                        Log.d("QuizDetailActivity", "quiz Taken id and quizId is : " + finalQuizTakenId + "  " + documentSnapshot.getId());
                    }
                }
            });
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        return super.onSupportNavigateUp();
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