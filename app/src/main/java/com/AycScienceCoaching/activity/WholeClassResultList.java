package com.AycScienceCoaching.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.AycScienceCoaching.Constant.Constant;
import com.AycScienceCoaching.Model.QuizTaken;
import com.AycScienceCoaching.R;
import com.AycScienceCoaching.adapter.WholeClassResultAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class WholeClassResultList extends AppCompatActivity {

    private static String quizId;
    private FirebaseFirestore Firestore;
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

        Firestore = FirebaseFirestore.getInstance();

        getAllUserList();
    }

    private void getAllUserList() {
        mProgressbar.setVisibility(View.VISIBLE);
        quizTakenList = new ArrayList<>();
        Firestore.collection(Constant.QUIZ_TAKEN_COLLECTION)
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
                        adapter = new WholeClassResultAdapter(quizTakenList, Integer.parseInt(Objects.requireNonNull(quizTakenObject.getDocuments().get(0).get(Constant.QuizTakenCollectionFields.SCORE)).toString()));
                        recyclerView.setAdapter(adapter);
                        recyclerView.setHasFixedSize(true);
                        mProgressbar.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
