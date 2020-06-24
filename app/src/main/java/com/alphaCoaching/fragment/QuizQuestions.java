package com.alphaCoaching.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;

import androidx.fragment.app.Fragment;

import com.alphaCoaching.Constant.Constant;
import com.alphaCoaching.Model.QuizTakenQuestion;
import com.alphaCoaching.R;
import com.alphaCoaching.activity.Question;
import com.alphaCoaching.activity.QuestionReview;
import com.alphaCoaching.activity.SingleQuestionDetailActivity;
import com.alphaCoaching.adapter.GridAdapter;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class QuizQuestions extends Fragment {
    View view;
    String quizId;
    String quizTakenId;
    List<Question> questionList;
    List<QuizTakenQuestion> takenQuestionList;
    FirebaseFirestore FireStore = FirebaseFirestore.getInstance();
    GridView gridView;

    public QuizQuestions() {
        // Required empty public constructor
    }

    public QuizQuestions(String quizId, String quizTakenId) {
        this.quizId = quizId;
        this.quizTakenId = quizTakenId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_quiz_questions, container, false);

        gridView = view.findViewById(R.id.grid);
        Log.d("fragments", quizTakenId + " -=-=- " + quizId);
        getQuestionsList();
        Button button = view.findViewById(R.id.ButtonQuestionReview);
        button.setOnClickListener(view -> {
            Intent i = new Intent(getActivity(), QuestionReview.class);
            i.putExtra("QuizId", quizId);
            i.putExtra("quickened", quizTakenId);
            startActivity(i);
        });
        return view;
    }


    private void getQuestionsList() {
        questionList = new ArrayList<>();
        FireStore.collection(Constant.QUESTION_COLLECTION).whereEqualTo(Constant.QuestionCollectionFields.QUIZ_ID, quizId)
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot questions = task.getResult();
                assert questions != null;
                for (QueryDocumentSnapshot doc : questions) {
                    Question question = doc.toObject(Question.class);
                    question.setId(doc.getId());
                    questionList.add(question);
                }
                getTakenQuestionList();
            } else {
                //   Toast.makeText(this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setBackground() {

        //Array to store the grid text
        String[] number = new String[questionList.size()];

        //Array to store the background of the  grid item
        int[] questionBackground = new int[questionList.size()];

        //Main iteration over all the questions from the quizTakenQuestions Collection
        for (int i = 0; i < questionList.size(); i++) {
            int n = (int) questionList.get(i).getCorrectOption();
            String ans = "";
            switch (n) {
                case 1:
                    ans = (questionList.get(i).getOption1());
                    break;
                case 2:
                    ans = (questionList.get(i).getOption2());
                    break;
                case 3:
                    ans = (questionList.get(i).getOption3());
                    break;
                case 4:
                    ans = (questionList.get(i).getOption4());
                    break;
            }
            String finalAns = ans;
            int background;
            QuizTakenQuestion takenQuestion = getTakenQuestion(questionList.get(i).getId());
            if (takenQuestion == null) {
                return;
            }
            String attemptedAns = takenQuestion.getAttemptedAnswer();
            if (attemptedAns == null) {
                background = -1;
            } else if (attemptedAns.equals(finalAns)) {
                background = 1;
            } else {
                background = 0;
            }
            questionBackground[i] = background;
        }
        for (int i = 0; i < questionList.size(); i++) {
            number[i] = String.valueOf(i + 1);
        }
        GridAdapter adapter = new GridAdapter(requireActivity().getApplicationContext(), number, questionBackground);
        gridView.setAdapter(adapter);

        //OnItemClickListener on the grid item
        gridView.setOnItemClickListener((adapterView, view, position, l) -> {
            Intent i = new Intent(getActivity(), SingleQuestionDetailActivity.class);
            i.putExtra("QuizId", quizId);
            i.putExtra("quickened", quizTakenId);
            i.putExtra("questionNumber", position);
            startActivity(i);
        });
    }

    private void getTakenQuestionList() {
        takenQuestionList = new ArrayList<>();
       CollectionReference yourCollRef = FireStore.collection(Constant.QUIZ_TAKEN_QUESTION_COLLECTION);
        Query query = yourCollRef.whereEqualTo(Constant.QuizTakenQuestionsFields.QUIZ_TAKEN_ID, quizTakenId);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult())) {
                    QuizTakenQuestion takenQuestion = documentSnapshot.toObject(QuizTakenQuestion.class);
                    assert takenQuestion != null;
                    takenQuestion.setId(documentSnapshot.getId());
                    takenQuestionList.add(takenQuestion);
                }
                setBackground();
            }
        });
    }

    private QuizTakenQuestion getTakenQuestion(String id) {
        for (QuizTakenQuestion takenQuestion : takenQuestionList) {
            if (takenQuestion.getQuestionId().equals(id)) {
                return takenQuestion;
            }
        }
        return null;
    }
}