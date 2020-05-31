package com.alphaCoaching.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alphaCoaching.Constant.Constant;
import com.alphaCoaching.Model.Note;
import com.alphaCoaching.Model.QuizTaken;
import com.alphaCoaching.R;
import com.alphaCoaching.adapter.NoteAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class QuizDetailActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private NoteAdapter adapter;
    private FirebaseAuth fireAuth;
    private String finalQuiztakenid;
    private RecyclerView recyclerView;
    private ArrayList<Note> quizList;
    private ArrayList<QuizTaken> quizTakenList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_detail);
        Toolbar toolbar = findViewById(R.id.toolbarOfQuizActivity);
        setSupportActionBar(toolbar);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        quizList = new ArrayList<>();
        quizTakenList = new ArrayList<>();
        getQuizAndTakenQuizData();
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
            Intent i = new Intent(QuizDetailActivity.this, QuestionReview.class);
            i.putExtra("QuizId", quizId);
            i.putExtra("quickened", quizTakenId);
            startActivity(i);
        });
    }

    private void getQuizAndTakenQuizData() {
        db.collection(Constant.QUIZ_COLLECTION)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Note quiz = document.toObject(Note.class);
                                quiz.setId(document.getId());
                                quizList.add(quiz);
                            }
                            getTekenQuizDdata(quizList);
                        } else {

                        }
                    }
                });
    }

    private void getTekenQuizDdata(ArrayList<Note> quizList) {
        db.collection(Constant.QUIZ_TAKEN_COLLECTION)
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
                            adapter = new NoteAdapter(QuizDetailActivity.this, quizList, quizTakenList);
                            recyclerView.setAdapter(adapter);
                            recyclerView.setHasFixedSize(true);
                            setupAdapter();
                        } else {

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
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

}