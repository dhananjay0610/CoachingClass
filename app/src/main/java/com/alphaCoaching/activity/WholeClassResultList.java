package com.alphaCoaching.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alphaCoaching.Constant.Constant;
import com.alphaCoaching.Model.QuizTaken;
import com.alphaCoaching.R;
import com.alphaCoaching.adapter.WholeClassResultAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class WholeClassResultList extends AppCompatActivity {

    private static String quizId;
    private FirebaseFirestore firestore;
    private ArrayList<QuizTaken> quizTakenList;
    private WholeClassResultAdapter adapter;
    private RecyclerView recyclerView;
    private LinearLayout mProgressbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.whole_class_result_list);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mProgressbar = findViewById(R.id.wholeClassListProgressbar);

        Intent intent = getIntent();
        quizId = intent.getStringExtra("QuizId");

        firestore = FirebaseFirestore.getInstance();

        getAllUserList();
    }

    private void getAllUserList() {
        mProgressbar.setVisibility(View.VISIBLE);
        quizTakenList = new ArrayList<>();
        firestore.collection(Constant.QUIZ_TAKEN_COLLECTION)
                .whereEqualTo(Constant.QuizTakenCollectionFields.QUIZ_ID, quizId)
                .orderBy(Constant.QuizTakenCollectionFields.SCORE, Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot quizTakenObject = task.getResult();
                        assert quizTakenObject != null;
                        for (QueryDocumentSnapshot doc : quizTakenObject) {
                            QuizTaken quizTaken = doc.toObject(QuizTaken.class);
                            quizTaken.setId(doc.getId());
                            quizTakenList.add(quizTaken);
                        }
                        adapter = new WholeClassResultAdapter(quizTakenList, Integer.parseInt(quizTakenObject.getDocuments().get(0).get(Constant.QuizTakenCollectionFields.SCORE).toString()));
                        recyclerView.setAdapter(adapter);
                        recyclerView.setHasFixedSize(true);
                        mProgressbar.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
