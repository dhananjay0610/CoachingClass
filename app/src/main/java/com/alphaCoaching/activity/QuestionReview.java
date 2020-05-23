package com.alphaCoaching.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alphaCoaching.R;
import com.alphaCoaching.adapter.QuestionAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class QuestionReview extends AppCompatActivity implements View.OnClickListener {
    private TextView question, qCount, timer, timetaken, averagetime, TotalScore;
    private Button option1, option2, option3, option4;
    private List<Question> questionList;
    private int questionNumber;
    private FirebaseFirestore firestore;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth fireAuth;
    //  private Dialog loadingDialog;
    private String score;
    ArrayList<Long> arr = new ArrayList<>();
    HashMap<Integer, Long> mp = new HashMap<>();

    //Array needed to store data in order to add them in the database
    String[] QuestionId = new String[45];
    String[] attemptedanswer = new String[45];

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_review);
        Toolbar toolbar = findViewById(R.id.toolbarofquestionreviewactivity);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Review your Questions");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        question = findViewById(R.id.questionReview);
        qCount = findViewById(R.id.quesNumReview);
        option1 = findViewById(R.id.option1Review);
        option2 = findViewById(R.id.option2Review);
        option3 = findViewById(R.id.option3Review);
        option4 = findViewById(R.id.option4Review);
        Button next = findViewById(R.id.nextReview);
        Button previous = findViewById(R.id.previousReview);
        timetaken = findViewById(R.id.timetakenReview);
        averagetime = findViewById(R.id.averagetimeReview);
        imageView = findViewById(R.id.imageViewfortick);
        TotalScore = findViewById(R.id.totalscore);
        next.setOnClickListener(this);
        previous.setOnClickListener(this);
        firestore = FirebaseFirestore.getInstance();
        getQuestionsList();
        // String docId = getIntent().getStringExtra("docID");
        String quizTakenId = getIntent().getStringExtra("quickened");

//        imageView.setImageResource(R.drawable.ic_launcher_background);


    }

    @Override
    public boolean onSupportNavigateUp() {
        return super.onSupportNavigateUp();
    }

    private void getQuestionsList() {
        String docId = getIntent().getStringExtra("QuizId");
        questionList = new ArrayList<>();
        firestore.collection("questions").whereEqualTo("quizID", docId)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot questions = task.getResult();
                    assert questions != null;
                    int i = 0;
                    for (QueryDocumentSnapshot doc : questions) {
                        Log.d("QuestionActivity", "document id" + doc.getId());
                        QuestionId[i] = doc.getId();
                        i++;
                        questionList.add(new Question(doc.getString("question"),
                                doc.getString("option1"),
                                doc.getString("option2"),
                                doc.getString("option3"),
                                doc.getString("option4"),
                                Integer.parseInt(Objects.requireNonNull(doc.getString("correctOption")))
                                , doc.getId()));
                    }
                    setQuestion();
                } else {
                    Toast.makeText(QuestionReview.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        String quizTakenId = getIntent().getStringExtra("quickened");
        final String[] MaxScore = {null};
        DocumentReference documentReference = db.collection("quizTaken").document(quizTakenId);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        score = (String) documentSnapshot.get("score");
                        MaxScore[0] = (String) documentSnapshot.get("TotalScore");
                        Log.d("QuestionReview", "the score is " + score + "   " + documentSnapshot.get("score"));
                    }
                    Log.d("QuestionReview", "Max Score is : " + MaxScore[0]);
                    TotalScore.setText("Score : " + (score) + "/" + MaxScore[0]);
                }
            }
        });

    }

    int questionNo = 0;

    private void setQuestion() {
        question.setText(questionList.get(0).getQuestion());
        option1.setText(questionList.get(0).getOptionA());
        option2.setText(questionList.get(0).getOptionB());
        option3.setText(questionList.get(0).getOptionC());
        option4.setText(questionList.get(0).getOptionD());
        qCount.setText(1 + "/" + (questionList.size()));
        imageView.setImageResource(R.drawable.ic_autorenew_black_24dp);

        questionNumber = 0;
        //To fetch from the quizTaken collection
        int i = questionList.get(questionNumber).getCorrectOption();
        switch (i) {
            case 1:
                option1.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                break;
            case 2:
                option2.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                break;
            case 3:
                option3.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                break;
            case 4:
                option4.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                break;
        }

        String QuestionId = questionList.get(0).getQuestionId();
        //fetching average time

        DocumentReference documentReference = db.collection("questions").document(QuestionId);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        averagetime.setText("Average : " + documentSnapshot.get("time"));
                        // score = (String) documentSnapshot.get("score");
                        Log.d("QuestionReview", "averagetime is :  " + documentSnapshot.get("time"));
                    }
                }
            }
        });

        //
        String quizTakenId = getIntent().getStringExtra("quickened");
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        CollectionReference yourCollRef = rootRef.collection("quizTakenQuestions");
        Query query = yourCollRef.whereEqualTo("quizTakenId", quizTakenId)
                .whereEqualTo("questionId", QuestionId);
        final Object[] AttemptedAnswer = {null};
        final Object[] timeTaken = {""};
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                        AttemptedAnswer[0] = documentSnapshot.get("attemptedAnswer");
                        timeTaken[0] = documentSnapshot.get("timeTaken");
                        Log.d("QuestionReview", timeTaken[0] + "Attempted answer :   " + AttemptedAnswer[0]);
                    }
                    timetaken.setText("Time : " + timeTaken[0]);
                    // Log.d("QuestionReview", timeTaken[0] + "  ------   " + AttemptedAnswer[0] + "  ------   ");
                    if (AttemptedAnswer[0] == null) {
                        Toast.makeText(QuestionReview.this, "Not attempted any answer", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d("QuestionReview", "------" + i + "  " + AttemptedAnswer[0]);
                        if (AttemptedAnswer[0].equals(i)) {
                            imageView.setImageResource(R.drawable.ic_done_black_24dp);
                        } else {
                            imageView.setImageResource(R.drawable.ic_close_black_24dp);
                            if (AttemptedAnswer[0].equals(questionList.get(0).getOptionA())) {
                                option1.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                            } else if (AttemptedAnswer[0].equals(questionList.get(0).getOptionB())) {
                                option2.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                            } else if (AttemptedAnswer[0].equals(questionList.get(0).getOptionC())) {
                                option3.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                            } else if (AttemptedAnswer[0].equals(questionList.get(0).getOptionD())) {
                                option4.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                            }
                        }
                    }

                }
            }
        });


//        fireAuth = FirebaseAuth.getInstance();
//        FirebaseUser currentUser = fireAuth.getCurrentUser();
//        String user_Uuid = currentUser.getUid();
//        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
//        CollectionReference yourCollRef = rootRef.collection("quizTaken");
//        Query query = yourCollRef.whereEqualTo("quizId", quizId)
//                .whereEqualTo("userId", user_Uuid);
//        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    //String score = "";
//                    //String totalscore = "";
//                    String id = "";
//                    for (QueryDocumentSnapshot documentSnapshot1 : task.getResult()) {
//                        //    score = (String) documentSnapshot1.get("score");
//                        // totalscore = (String) documentSnapshot1.get("TotalScore");
//                        Log.d("QuizDetailActivity", "" + documentSnapshot1.getId() + "   " + documentSnapshot1.getData());
//                        id = documentSnapshot1.getId();
//                    }
//                    CollectionReference yourCollRef = rootRef.collection("quizTakenQuestions");
//                    Query query = yourCollRef.whereEqualTo("quizTakenId", id)
//                            .whereEqualTo("questionId", QuestionId[questionNo++]);
//                    query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                            if (task.isSuccessful()) {
//                                for (QueryDocumentSnapshot documentSnapshot1 : task.getResult()) {
//                                    Log.d("QuizDetailActivity", "" + documentSnapshot1.getId() + "   " + documentSnapshot1.getData());
//                                    timetaken.setText(documentSnapshot1.get("timeTaken") + "");
//                                    //averagetime.setText(documentSnapshot1.get("AverageTime"));
//
//                                }
//                            }
//                        }
//                    });
//
//
//                }
//            }
//        });

    }

//        String questionid = "";
//        fireAuth = FirebaseAuth.getInstance();
//        FirebaseUser currentUser = fireAuth.getCurrentUser();
//        String user_Uuid = currentUser.getUid();
//        DocumentReference documentReference = db.collection("quizTakenQuestions")
//                .whereEqualTo("userId", user_Uuid)
//                .whereEqualTo();
//        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot documentSnapshot = task.getResult();
//                    if (documentSnapshot.exists()) {
//                        Log.d("QuestionReview", "Document values : " + documentSnapshot.getData());
//                        //  holder.textViewQuestion.setText("" + (documentSnapshot.get("question")));
//                        String ans = (String) documentSnapshot.get("correctOption");
//
//                        Log.d("QuestionReview", "  " + ans + "  " + documentSnapshot.get("option4"));
//
//                    }
//                }
//            }
//        });


    //variable to iterate over array Timetaken ,attemptedanswer
    int k = 0;

    //    @Override
//    public void onClick(View v) {
//        int selectedOption = 0;
//
//        long a = 0;
//        if (v.getId() == (R.id.next)) {
//            endtime = getCurrentTime();
//            arr.add(arr.get(questionNumber) + endtime - starttime);
//            if (mp.containsKey(questionNumber))
//                mp.put(questionNumber, mp.get(questionNumber) + endtime - starttime);
//            else
//                mp.put(questionNumber, endtime - starttime);
//            Toast.makeText(this, "Time Taken: " + mp.get(questionNumber), Toast.LENGTH_SHORT).show();
//            Timetaken[k] = mp.get(questionNumber);
//            k++;
//        }
//        if (v.getId() == R.id.previous) {
//            starttime = getCurrentTime();
//        }
//        switch (v.getId()) {
//            case R.id.option1:
//                selectedOption = 1;
//                option4.setEnabled(false);
//                option2.setEnabled(false);
//                option3.setEnabled(false);
//                break;
//            case R.id.option2:
//                selectedOption = 2;
//                option1.setEnabled(false);
//                option4.setEnabled(false);
//                option3.setEnabled(false);
//                break;
//            case R.id.option3:
//                selectedOption = 3;
//                option1.setEnabled(false);
//                option2.setEnabled(false);
//                option4.setEnabled(false);
//                break;
//            case R.id.option4:
//                selectedOption = 4;
//                option1.setEnabled(false);
//                option2.setEnabled(false);
//                option3.setEnabled(false);
//                break;
//            case R.id.previous:
//                previousQuestion();
//                return;
//            case R.id.next:
//                changeQuestion();
//                return;
//            default:
//        }
//        checkAnswer(selectedOption, v);
//
//        String option = "";
//        if (selectedOption == 1)
//            option = (questionList.get(0).getOptionA());
//        else if (selectedOption == 2)
//            option = (questionList.get(0).getOptionB());
//        else if (selectedOption == 3)
//            option = (questionList.get(0).getOptionC());
//        else if (selectedOption == 4)
//            option = (questionList.get(0).getOptionD());
//        attemptedanswer[k] = option;
//    }
//
//    private void checkAnswer(int selectedOption, View view) {
//        if (selectedOption == questionList.get(questionNumber).getCorrectOption()) {
//            //Right Answer
//            view.setBackgroundTintList(ColorStateList.valueOf(Color.CYAN));
//            score++;
//        } else {
//            //Wrong Answer
//            (view).setBackgroundTintList(ColorStateList.valueOf(Color.CYAN));
//           /* switch (questionList.get(questionNumber).getCorrectOption())
//            {
//                case 1:
//                    option1.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
//                    break;
//                case 2:
//                    option2.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
//                    break;
//                case 3:
//                    option3.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
//                    break;
//                case 4:
//                    option4.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
//                    break;
//            }*/
//        }
//       /*Handler handler=new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                changeQuestion();
//            }
//        },2000);*/
//    }
    private void previousQuestion() {
        if (questionNumber != 0) {
            --questionNumber;


            playAnim(question, 0, 0);
            playAnim(option1, 0, 1);
            playAnim(option2, 0, 2);
            playAnim(option3, 0, 3);
            playAnim(option4, 0, 4);

            qCount.setText(String.valueOf(questionNumber + 1) + "/" + String.valueOf(questionList.size()));

            option1.setEnabled(true);
            option2.setEnabled(true);
            option3.setEnabled(true);
            option4.setEnabled(true);
            imageView.setImageResource(R.drawable.ic_autorenew_black_24dp);
            averagetime.setText("Loading...");
            timetaken.setText("Loading...");
//
//
//            //
//
//            int n = questionList.get(questionNumber).getCorrectOption();
//            String ans = "";
//            int[] arr = new int[n];
//            for (int i = 0; i < n; i++)
//                arr[i] = 0;
//            arr[n - 1] = 1;
//            switch (n) {
//                case 1:
//                    ans = (questionList.get(questionNumber).getOptionA());
//                    option1.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
//                    break;
//                case 2:
//                    ans = (questionList.get(questionNumber).getOptionB());
//                    option2.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
//                    break;
//                case 3:
//                    ans = (questionList.get(questionNumber).getOptionC());
//                    option3.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
//                    break;
//                case 4:
//                    ans = (questionList.get(questionNumber).getOptionD());
//                    option4.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
//                    break;
//            }
//            String QuestionId = questionList.get(questionNumber).getQuestionId();
//            //fetching average time
//
//            DocumentReference documentReference = db.collection("questions").document(QuestionId);
//            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                    if (task.isSuccessful()) {
//                        DocumentSnapshot documentSnapshot = task.getResult();
//                        assert documentSnapshot != null;
//                        if (documentSnapshot.exists()) {
//                            averagetime.setText(documentSnapshot.get("time") + "");
//                            // score = (String) documentSnapshot.get("score");
//                            Log.d("QuestionReview", "averagetime is :  " + documentSnapshot.get("time"));
//                        }
//                    }
//                }
//            });
//
//            //
//
//            String questionid = questionList.get(questionNumber).getQuestionId();
//            String quizTakenId = getIntent().getStringExtra("quickened");
//            FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
//            CollectionReference yourCollRef = rootRef.collection("quizTakenQuestions");
//            Query query = yourCollRef.whereEqualTo("quizTakenId", quizTakenId)
//                    .whereEqualTo("questionId", questionid);
//            final Object[] AttemptedAnswer = {null};
//            final Object[] timeTaken = {""};
//            String finalAns = ans;
//            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                    if (task.isSuccessful()) {
//
//                        for (DocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult())) {
//                            AttemptedAnswer[0] = documentSnapshot.get("attemptedAnswer");
//                            timeTaken[0] = documentSnapshot.get("timeTaken");
//                            Log.d("QuestionReview", timeTaken[0] + "  ==========    " + AttemptedAnswer[0] + "  =====  " + documentSnapshot.get("attemptedAnswer"));
//
//                        }
//                        timetaken.setText(timeTaken[0] + "");
//                        Log.d("QuestionReview", timeTaken[0] + "  ------   " + AttemptedAnswer[0] + "  ------   ");
//                        if (AttemptedAnswer[0] == null) {
//                            Toast.makeText(QuestionReview.this, "Not attempted any answer", Toast.LENGTH_SHORT).show();
//                        } else {
//                            Log.d("QuestionReview", "------" + finalAns + "  " + AttemptedAnswer[0]);
//
//                            if (AttemptedAnswer[0].equals(finalAns)) {
//                                imageView.setImageResource(R.drawable.ic_done_black_24dp);
//                            } else {
//                                imageView.setImageResource(R.drawable.ic_close_black_24dp);
//                                if (AttemptedAnswer[0].equals(questionList.get(questionNumber).getOptionA())) {
//                                    option1.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
//                                } else if (AttemptedAnswer[0].equals(questionList.get(questionNumber).getOptionB())) {
//                                    option2.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
//                                } else if (AttemptedAnswer[0].equals(questionList.get(questionNumber).getOptionC())) {
//                                    option3.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
//                                } else if (AttemptedAnswer[0].equals(questionList.get(questionNumber).getOptionD())) {
//                                    option4.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
//                                }
//                            }
//                        }
//                    }
//                }
//            });
//
//
//            //////////


        }
    }

    private void changeQuestion() {

        //  starttime = getCurrentTime();
        if (questionNumber < questionList.size() - 1) {
            questionNumber++;

            playAnim(question, 0, 0);
            playAnim(option1, 0, 1);
            playAnim(option2, 0, 2);
            playAnim(option3, 0, 3);
            playAnim(option4, 0, 4);

            qCount.setText(String.valueOf(questionNumber + 1) + "/" + String.valueOf(questionList.size()));

            option1.setEnabled(true);
            option2.setEnabled(true);
            option3.setEnabled(true);
            option4.setEnabled(true);
            imageView.setImageResource(R.drawable.ic_autorenew_black_24dp);
            averagetime.setText("Loading...");
            timetaken.setText("Loading...");

//            //
//
//            int n = questionList.get(questionNumber).getCorrectOption();
//            String ans = "";
//            int[] arr = new int[n];
//            for (int i = 0; i < n; i++)
//                arr[i] = 0;
//            arr[n - 1] = 1;
//            switch (n) {
//                case 1:
//                    ans = (questionList.get(0).getOptionA());
//                    option1.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
//                    break;
//                case 2:
//                    ans = (questionList.get(0).getOptionB());
//                    option2.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
//                    break;
//                case 3:
//                    ans = (questionList.get(0).getOptionC());
//                    option3.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
//                    break;
//                case 4:
//                    ans = (questionList.get(0).getOptionD());
//                    option4.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
//                    break;
//            }
//            String QuestionId = questionList.get(0).getQuestionId();
//            //fetching average time
//
//            DocumentReference documentReference = db.collection("questions").document(QuestionId);
//            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                    if (task.isSuccessful()) {
//                        DocumentSnapshot documentSnapshot = task.getResult();
//                        assert documentSnapshot != null;
//                        if (documentSnapshot.exists()) {
//                            averagetime.setText(documentSnapshot.get("time") + "");
//                            // score = (String) documentSnapshot.get("score");
//                            Log.d("QuestionReview", "averagetime is :  " + documentSnapshot.get("time"));
//                        }
//                    }
//                }
//            });
//
//            //
//
//            String questionid = questionList.get(0).getQuestionId();
//            String quizTakenId = getIntent().getStringExtra("quickened");
//            FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
//            CollectionReference yourCollRef = rootRef.collection("quizTakenQuestions");
//            Query query = yourCollRef.whereEqualTo("quizTakenId", quizTakenId)
//                    .whereEqualTo("questionId", questionid);
//            final Object[] AttemptedAnswer = {null};
//            final Object[] timeTaken = {""};
//            String finalAns = ans;
//            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                    if (task.isSuccessful()) {
//
//                        for (DocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult())) {
//                            AttemptedAnswer[0] = documentSnapshot.get("attemptedAnswer");
//                            timeTaken[0] = documentSnapshot.get("timeTaken");
//                            Log.d("QuestionReview", timeTaken[0] + "  ==========    " + AttemptedAnswer[0] + "  =====  " + documentSnapshot.get("attemptedAnswer"));
//
//                        }
//                        timetaken.setText(timeTaken[0] + "");
//                        Log.d("QuestionReview", timeTaken[0] + "  ------   " + AttemptedAnswer[0] + "  ------   ");
//                        if (AttemptedAnswer[0] == null) {
//                            Toast.makeText(QuestionReview.this, "Not attempted any answer", Toast.LENGTH_SHORT).show();
//                        } else {
//                            Log.d("QuestionReview", "------" + finalAns + "  " + AttemptedAnswer[0]);
//
//                            if (AttemptedAnswer[0].equals(finalAns)) {
//                                imageView.setImageResource(R.drawable.ic_done_black_24dp);
//                            } else {
//                                imageView.setImageResource(R.drawable.ic_close_black_24dp);
//                                if (AttemptedAnswer[0].equals(questionList.get(0).getOptionA())) {
//                                    option1.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
//                                } else if (AttemptedAnswer[0].equals(questionList.get(0).getOptionB())) {
//                                    option2.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
//                                } else if (AttemptedAnswer[0].equals(questionList.get(0).getOptionC())) {
//                                    option3.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
//                                } else if (AttemptedAnswer[0].equals(questionList.get(0).getOptionD())) {
//                                    option4.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
//                                }
//                            }
//                        }
//                    }
//                }
//            });
//
//
//            //////////

            //

            //


            /////////////
            int i = questionList.get(questionNumber).getCorrectOption();
            switch (i) {
                case 1:
                    option1.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                    break;
                case 2:
                    option2.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                    break;
                case 3:
                    option3.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                    break;
                case 4:
                    option4.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                    break;
            }

            //////////

        } else {
            boolean examend = true;
            // Go to Score Activity

            String questionid = questionList.get(0).getQuestionId();
            String quizTakenId = getIntent().getStringExtra("quickened");
            DocumentReference documentReference = db.collection("quizTaken").document(quizTakenId);
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot.exists()) {
                            score = (String) documentSnapshot.get("score");
                            Log.d("QuestionReview", "the score is " + score + "   " + documentSnapshot.get("score"));
                        }
                        Intent intent = new Intent(QuestionReview.this, ScoreActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("SCORE", (score) + "/" + (questionList.size()));
                        startActivity(intent);
                        QuestionReview.this.finish();
                    }
                }
            });
        }

    }

    private void playAnim(final View view, final int value, final int viewNum) {
        view.animate().alpha(value).scaleX(value).scaleY(value).setDuration(500)
                .setStartDelay(100).setInterpolator(new DecelerateInterpolator())
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (value == 0) {

                            switch (viewNum) {
                                case 0:
                                    ((TextView) view).setText(questionList.get(questionNumber).getQuestion());
                                    break;
                                case 1:
                                    ((Button) view).setText(questionList.get(questionNumber).getOptionA());
                                    break;
                                case 2:
                                    ((Button) view).setText(questionList.get(questionNumber).getOptionB());
                                    break;
                                case 3:
                                    ((Button) view).setText(questionList.get(questionNumber).getOptionC());
                                    break;
                                case 4:
                                    ((Button) view).setText(questionList.get(questionNumber).getOptionD());
                                    break;
                            }
                            if (viewNum != 0) {
                                view.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#696880")));


                                int n = questionList.get(questionNumber).getCorrectOption();
                                String ans = "";
                                int[] arr = new int[n];
                                for (int i = 0; i < n; i++)
                                    arr[i] = 0;
                                arr[n - 1] = 1;
                                switch (n) {
                                    case 1:
                                        ans = (questionList.get(questionNumber).getOptionA());
                                        option1.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                                        break;
                                    case 2:
                                        ans = (questionList.get(questionNumber).getOptionB());
                                        option2.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                                        break;
                                    case 3:
                                        ans = (questionList.get(questionNumber).getOptionC());
                                        option3.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                                        break;
                                    case 4:
                                        ans = (questionList.get(questionNumber).getOptionD());
                                        option4.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                                        break;
                                }
                                String QuestionId = questionList.get(questionNumber).getQuestionId();
                                //fetching average time

                                DocumentReference documentReference = db.collection("questions").document(QuestionId);
                                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot documentSnapshot = task.getResult();
                                            assert documentSnapshot != null;
                                            if (documentSnapshot.exists()) {
                                                averagetime.setText("Average : " + documentSnapshot.get("time"));
                                                // score = (String) documentSnapshot.get("score");
                                                Log.d("QuestionReview", "averagetime is :  " + documentSnapshot.get("time"));
                                            }
                                        }
                                    }
                                });

                                //

                                String questionid = questionList.get(questionNumber).getQuestionId();
                                String quizTakenId = getIntent().getStringExtra("quickened");
                                FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
                                CollectionReference yourCollRef = rootRef.collection("quizTakenQuestions");
                                Query query = yourCollRef.whereEqualTo("quizTakenId", quizTakenId)
                                        .whereEqualTo("questionId", questionid);
                                final Object[] AttemptedAnswer = {null};
                                final Object[] timeTaken = {""};
                                String finalAns = ans;
                                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {

                                            for (DocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult())) {
                                                AttemptedAnswer[0] = documentSnapshot.get("attemptedAnswer");
                                                timeTaken[0] = documentSnapshot.get("timeTaken");
                                                Log.d("QuestionReview", timeTaken[0] + "  ==========    " + AttemptedAnswer[0] + "  =====  " + documentSnapshot.get("attemptedAnswer"));

                                            }
                                            timetaken.setText("Time : " + timeTaken[0]);
                                            Log.d("QuestionReview", timeTaken[0] + "  ------   " + AttemptedAnswer[0] + "  ------   ");
                                            if (AttemptedAnswer[0] == null) {
                                                Toast.makeText(QuestionReview.this, "Not attempted any answer", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Log.d("QuestionReview", "------" + finalAns + "  " + AttemptedAnswer[0]);

                                                if (AttemptedAnswer[0].equals(finalAns)) {
                                                    imageView.setImageResource(R.drawable.ic_done_black_24dp);
                                                } else {
                                                    imageView.setImageResource(R.drawable.ic_close_black_24dp);
                                                    if (AttemptedAnswer[0].equals(questionList.get(questionNumber).getOptionA())) {
                                                        option1.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                                                    } else if (AttemptedAnswer[0].equals(questionList.get(questionNumber).getOptionB())) {
                                                        option2.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                                                    } else if (AttemptedAnswer[0].equals(questionList.get(questionNumber).getOptionC())) {
                                                        option3.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                                                    } else if (AttemptedAnswer[0].equals(questionList.get(questionNumber).getOptionD())) {
                                                        option4.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                                                    }
                                                }
                                            }
                                        }
                                    }
                                });


                                //////////


                            }
                            playAnim(view, 1, viewNum);
                        }

                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.previousReview): {
                previousQuestion();
                return;
            }
            case R.id.nextReview: {
                changeQuestion();
                return;
            }
            default:
        }
    }
}


//public class QuestionReview extends AppCompatActivity {
//    FirestoreRecyclerAdapter adapter;
//    FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_question_review);
//        Toolbar toolbar = findViewById(R.id.toolbarofquestionreviewactivity);
//        setSupportActionBar(toolbar);
//        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
//
//        FirebaseFirestore mFireBaseDB = FirebaseFirestore.getInstance();
//
//        RecyclerView recyclerView = findViewById(R.id.recycler_view_questionreview);
//
//        Intent intent = getIntent();
//        String id = intent.getStringExtra("quickened");
//        Log.d("QuestionReview", "QuizTakenid is : " + id);
//        Query query = mFireBaseDB.collection("quizTakenQuestions")
//                .whereEqualTo("quizTakenId", id);
//        //recycler options
//        FirestoreRecyclerOptions<QuestionModel> options = new FirestoreRecyclerOptions.Builder<QuestionModel>()
//                .setQuery(query, QuestionModel.class).build();
//        adapter = new FirestoreRecyclerAdapter<QuestionModel, QuestionViewHolder>(options) {
//            @NonNull
//            @Override
//            public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.questiondetailss, parent, false);
//                return new QuestionViewHolder(view);
//            }
//
//            int i = 1;
//
//            @Override
//            protected void onBindViewHolder(@NonNull QuestionViewHolder holder, int position, @NonNull QuestionModel model) {
//
//
//                String questionid = model.getQuestionid();
//                DocumentReference documentReference = db.collection("questions").document(questionid);
//                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        if (task.isSuccessful()) {
//                            DocumentSnapshot documentSnapshot = task.getResult();
//                            if (documentSnapshot.exists()) {
//                                Log.d("QuestionReview", "Document values : " + documentSnapshot.getData());
//                                holder.textViewQuestion.setText("" + (documentSnapshot.get("question")));
//                                String ans = (String) documentSnapshot.get("correctOption");
//
//                                switch (ans) {
//                                    case "1":
//                                        holder.textViewCorrectAnswer.setText(""+documentSnapshot.get("option1"));
//                                        break;
//                                    case "2":
//                                        holder.textViewCorrectAnswer.setText(""+documentSnapshot.get("option2"));
//                                        break;
//                                    case "3":
//                                        holder.textViewCorrectAnswer.setText(""+documentSnapshot.get("option3"));
//                                        break;
//                                    case "4":
//                                        holder.textViewCorrectAnswer.setText(""+documentSnapshot.get("option4"));
//                                        break;
//                                }
//                                Log.d("QuestionReview",  "  " + ans+"  "+documentSnapshot.get("option4"));
//
//                            }
//                        }
//                    }
//                });
//
//                holder.textViewanswer.setText(model.getAttemptedAnswer());
//                holder.textViewtime.setText(""+ model.getTimeTaken());
//               // holder.textViewquestionid.setText(model.getQuestionid());
//            }
//        };
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setAdapter(adapter);
//    }
//
//    private class QuestionViewHolder extends RecyclerView.ViewHolder {
//        private TextView textViewtime;
//        private TextView textViewanswer;
//        //private TextView textViewquestionid;
//        private TextView textViewCorrectAnswer;
//        private TextView textViewQuestion;
////        private TextView textViewquiztakenid;
//
//        private QuestionViewHolder(@NonNull View itemView) {
//            super(itemView);
//            textViewtime = itemView.findViewById(R.id.textViewtime);
//            textViewanswer = itemView.findViewById(R.id.textViewanswer);
//          //  textViewquestionid = itemView.findViewById(R.id.textViewquestionid);
//            textViewCorrectAnswer = itemView.findViewById(R.id.textViewCorrectAnswer);
//            textViewQuestion = itemView.findViewById(R.id.textViewQuestion);
//        }
//    }
//
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        adapter.startListening();
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        adapter.stopListening();
//    }
//}

//package com.alphaCoaching.activity;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.Toolbar;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import android.animation.Animator;
//import android.content.Intent;
//import android.content.res.ColorStateList;
//import android.graphics.Color;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.animation.DecelerateInterpolator;
//import android.widget.Button;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.alphaCoaching.R;
//import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
//import com.firebase.ui.firestore.FirestoreRecyclerOptions;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.firestore.CollectionReference;
//import com.google.firebase.firestore.DocumentReference;
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.Query;
//import com.google.firebase.firestore.QueryDocumentSnapshot;
//import com.google.firebase.firestore.QuerySnapshot;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Objects;
//
//public class QuestionReview extends AppCompatActivity {
//    private TextView question, qCount, timer, timetaken, averagetime;
//    private Button option1, option2, option3, option4;
//    private List<Question> questionList;
//    private int questionNumber;
//    //    private CountDownTimer countDownperquiz;
//    private FirebaseFirestore firestore;
//    FirebaseFirestore db = FirebaseFirestore.getInstance();
//    FirebaseAuth fireAuth;
//    //  private Dialog loadingDialog;
//    private int score;
//    ArrayList<Long> arr = new ArrayList<>();
//    HashMap<Integer, Long> mp = new HashMap<>();
//
//    //Array needed to store data in order to add them in the database
//    String[] QuestionId = new String[45];
//    String[] attemptedanswer = new String[45];
////    long[] Timetaken = new long[45];
////    int i = 0;
////
////    Boolean examend = false;
////
////    long starttime;
////    long endtime;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_question_detail);
////        score = 0;
////        for (int i = 0; i < 100; i++)
////            arr.add((long) 0);
//
//        Toolbar toolbar = findViewById(R.id.toolbarofquestiondetailactivity);
//        setSupportActionBar(toolbar);
//        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
//
//
//        question = findViewById(R.id.question);
//        qCount = findViewById(R.id.quesNum);
//        timer = findViewById(R.id.countDown);
//
//        option1 = findViewById(R.id.option1);
//        option2 = findViewById(R.id.option2);
//        option3 = findViewById(R.id.option3);
//        option4 = findViewById(R.id.option4);
//        Button next = findViewById(R.id.next);
//        Button previous = findViewById(R.id.previous);
//        timetaken = findViewById(R.id.timetaken);
//        averagetime = findViewById(R.id.averagetime);
//        firestore = FirebaseFirestore.getInstance();
//
//        getQuestionsList();
////        String docId = getIntent().getStringExtra("docID");
////        long quizTime = getIntent().getLongExtra("quizTime", 2000);
//
//        //Log.d("QuestionDetailActivity", quizTime+"   "+docId);
//    }
//
//    @Override
//    public boolean onSupportNavigateUp() {
//        return super.onSupportNavigateUp();
//    }
//
//    private void getQuestionsList() {
//        String docId = getIntent().getStringExtra("docID");
//        questionList = new ArrayList<>();
//        firestore.collection("questions").whereEqualTo("quizID", docId)
//                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    QuerySnapshot questions = task.getResult();
//                    assert questions != null;
//                    int i = 0;
//                    for (QueryDocumentSnapshot doc : questions) {
//                        Log.d("QuestionActivity", "document id" + doc.getId());
//                        QuestionId[i] = doc.getId();
//                        i++;
//                        questionList.add(new Question(doc.getString("question"),
//                                doc.getString("option1"),
//                                doc.getString("option2"),
//                                doc.getString("option3"),
//                                doc.getString("option4"),
//                                Integer.parseInt(Objects.requireNonNull(doc.getString("correctOption")))
//                        ));
//                    }
//                    setQuestion();
//                } else {
//                    Toast.makeText(QuestionReview.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
//    }
//
//    int questionNo = 0;
//
//    private void setQuestion() {
//        // long quizTime = getIntent().getLongExtra("quizTime", 2000);
//        // timer.setText("Time: " + String.valueOf(quizTime));
//        question.setText(questionList.get(0).getQuestion());
//        option1.setText(questionList.get(0).getOptionA());
//        option2.setText(questionList.get(0).getOptionB());
//        option3.setText(questionList.get(0).getOptionC());
//        option4.setText(questionList.get(0).getOptionD());
//        qCount.setText(1 + "/" + (questionList.size()));
//
//        questionNumber = 0;
//        //To fetch from the quizTaken collection
//        int i = questionList.get(questionNumber).getCorrectOption();
//        switch (i) {
//            case 1:
//                option1.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
//                break;
//            case 2:
//                option2.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
//                break;
//            case 3:
//                option3.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
//                break;
//            case 4:
//                option4.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
//                break;
//        }
//
//
//        //error
//        String quizId = getIntent().getStringExtra("quizId");
//
//
//        fireAuth = FirebaseAuth.getInstance();
//        FirebaseUser currentUser = fireAuth.getCurrentUser();
//        String user_Uuid = currentUser.getUid();
//        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
//        CollectionReference yourCollRef = rootRef.collection("quizTaken");
//        Query query = yourCollRef.whereEqualTo("quizId", quizId)
//                .whereEqualTo("userId", user_Uuid);
//        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    //String score = "";
//                    //String totalscore = "";
//                    String id = "";
//                    for (QueryDocumentSnapshot documentSnapshot1 : task.getResult()) {
//                        //    score = (String) documentSnapshot1.get("score");
//                        // totalscore = (String) documentSnapshot1.get("TotalScore");
//                        Log.d("QuizDetailActivity", "" + documentSnapshot1.getId() + "   " + documentSnapshot1.getData());
//                        id = documentSnapshot1.getId();
//                    }
//                    CollectionReference yourCollRef = rootRef.collection("quizTakenQuestions");
//                    Query query = yourCollRef.whereEqualTo("quizTakenId", id)
//                            .whereEqualTo("questionId", QuestionId[questionNo++]);
//                    query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                            if (task.isSuccessful()) {
//                                for (QueryDocumentSnapshot documentSnapshot1 : task.getResult()) {
//                                    Log.d("QuizDetailActivity", "" + documentSnapshot1.getId() + "   " + documentSnapshot1.getData());
//                                    timetaken.setText(documentSnapshot1.get("timeTaken") + "");
//                                    //averagetime.setText(documentSnapshot1.get("AverageTime"));
//
//                                }
//                            }
//                        }
//                    });
//
//
//                }
//            }
//        });
//
//    }
//
////        String questionid = "";
////        fireAuth = FirebaseAuth.getInstance();
////        FirebaseUser currentUser = fireAuth.getCurrentUser();
////        String user_Uuid = currentUser.getUid();
////        DocumentReference documentReference = db.collection("quizTakenQuestions")
////                .whereEqualTo("userId", user_Uuid)
////                .whereEqualTo();
////        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
////            @Override
////            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
////                if (task.isSuccessful()) {
////                    DocumentSnapshot documentSnapshot = task.getResult();
////                    if (documentSnapshot.exists()) {
////                        Log.d("QuestionReview", "Document values : " + documentSnapshot.getData());
////                        //  holder.textViewQuestion.setText("" + (documentSnapshot.get("question")));
////                        String ans = (String) documentSnapshot.get("correctOption");
////
////                        Log.d("QuestionReview", "  " + ans + "  " + documentSnapshot.get("option4"));
////
////                    }
////                }
////            }
////        });
//
//
//    //variable to iterate over array Timetaken ,attemptedanswer
//    int k = 0;
//
////    @Override
////    public void onClick(View v) {
////        int selectedOption = 0;
////
////        long a = 0;
////        if (v.getId() == (R.id.next)) {
////            endtime = getCurrentTime();
////            arr.add(arr.get(questionNumber) + endtime - starttime);
////            if (mp.containsKey(questionNumber))
////                mp.put(questionNumber, mp.get(questionNumber) + endtime - starttime);
////            else
////                mp.put(questionNumber, endtime - starttime);
////            Toast.makeText(this, "Time Taken: " + mp.get(questionNumber), Toast.LENGTH_SHORT).show();
////            Timetaken[k] = mp.get(questionNumber);
////            k++;
////        }
////        if (v.getId() == R.id.previous) {
////            starttime = getCurrentTime();
////        }
////        switch (v.getId()) {
////            case R.id.option1:
////                selectedOption = 1;
////                option4.setEnabled(false);
////                option2.setEnabled(false);
////                option3.setEnabled(false);
////                break;
////            case R.id.option2:
////                selectedOption = 2;
////                option1.setEnabled(false);
////                option4.setEnabled(false);
////                option3.setEnabled(false);
////                break;
////            case R.id.option3:
////                selectedOption = 3;
////                option1.setEnabled(false);
////                option2.setEnabled(false);
////                option4.setEnabled(false);
////                break;
////            case R.id.option4:
////                selectedOption = 4;
////                option1.setEnabled(false);
////                option2.setEnabled(false);
////                option3.setEnabled(false);
////                break;
////            case R.id.previous:
////                previousQuestion();
////                return;
////            case R.id.next:
////                changeQuestion();
////                return;
////            default:
////        }
////        checkAnswer(selectedOption, v);
////
////        String option = "";
////        if (selectedOption == 1)
////            option = (questionList.get(0).getOptionA());
////        else if (selectedOption == 2)
////            option = (questionList.get(0).getOptionB());
////        else if (selectedOption == 3)
////            option = (questionList.get(0).getOptionC());
////        else if (selectedOption == 4)
////            option = (questionList.get(0).getOptionD());
////        attemptedanswer[k] = option;
////    }
////
////    private void checkAnswer(int selectedOption, View view) {
////        if (selectedOption == questionList.get(questionNumber).getCorrectOption()) {
////            //Right Answer
////            view.setBackgroundTintList(ColorStateList.valueOf(Color.CYAN));
////            score++;
////        } else {
////            //Wrong Answer
////            (view).setBackgroundTintList(ColorStateList.valueOf(Color.CYAN));
////           /* switch (questionList.get(questionNumber).getCorrectOption())
////            {
////                case 1:
////                    option1.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
////                    break;
////                case 2:
////                    option2.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
////                    break;
////                case 3:
////                    option3.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
////                    break;
////                case 4:
////                    option4.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
////                    break;
////            }*/
////        }
////       /*Handler handler=new Handler();
////        handler.postDelayed(new Runnable() {
////            @Override
////            public void run() {
////                changeQuestion();
////            }
////        },2000);*/
////    }
//
//    private void previousQuestion() {
//        if (questionNumber != 0) {
//            questionNumber--;
//            playAnim(question, 0, 0);
//            playAnim(option1, 0, 1);
//            playAnim(option2, 0, 2);
//            playAnim(option3, 0, 3);
//            playAnim(option4, 0, 4);
//
//            qCount.setText(String.valueOf(questionNumber + 1) + "/" + String.valueOf(questionList.size()));
//
//            timer.setText("Time:- " + String.valueOf(" "));
//            option1.setEnabled(true);
//            option2.setEnabled(true);
//            option3.setEnabled(true);
//            option4.setEnabled(true);
//        }
//    }
//
//    private void changeQuestion() {
//
//        //  starttime = getCurrentTime();
//        if (questionNumber < questionList.size() - 1) {
//
////            questionNumber++;
////
////            playAnim(question, 0, 0);
////            playAnim(option1, 0, 1);
////            playAnim(option2, 0, 2);
////            playAnim(option3, 0, 3);
////            playAnim(option4, 0, 4);
////
////            qCount.setText(String.valueOf(questionNumber + 1) + "/" + String.valueOf(questionList.size()));
////
////            timer.setText("Time:- " + String.valueOf(" "));
////            //  startTimer();
////            option1.setEnabled(true);
//            option2.setEnabled(true);
//            option3.setEnabled(true);
//            option4.setEnabled(true);
//
//        } else {
//            boolean examend = true;
//            // Go to Score Activity
//            Intent intent = new Intent(QuestionReview.this, ScoreActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            intent.putExtra("SCORE", (score) + "/" + (questionList.size()));
//            startActivity(intent);
//            QuestionReview.this.finish();
//
//            //adding data to the firestore quizTaken collection
//            String docId = getIntent().getStringExtra("docID");
//            fireAuth = FirebaseAuth.getInstance();
//            FirebaseUser currentUser = fireAuth.getCurrentUser();
//            String user_Uuid = currentUser.getUid();
//            Map<String, Object> data = new HashMap<>();
//            data.put("quizId", docId);
//            data.put("score", String.valueOf(score));
//            data.put("TotalScore", String.valueOf(questionList.size()));
//            data.put("userId", user_Uuid);
//            firestore.collection("quizTaken")
//                    .add(data)
//                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                        @Override
//                        public void onSuccess(DocumentReference documentReference) {
//                            Log.d("QuestionActivity", "DocumentSnapshot written with ID: " + documentReference.getId());
//
//                            //adding data to the quizTakenQuestions collection
//                            for (int questionnumber = 0; questionnumber < questionList.size(); questionnumber++) {
//                                Map<String, Object> data1 = new HashMap<>();
//                                data1.put("attemptedAnswer", attemptedanswer[questionnumber]);
//                                data1.put("questionId", QuestionId[questionnumber]);
//                                data1.put("quizTakenId", documentReference.getId());
//                                data1.put("timeTaken", mp.get(questionnumber));
//                                firestore.collection("quizTakenQuestions")
//                                        .add(data1)
//                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                                            @Override
//                                            public void onSuccess(DocumentReference documentReference) {
//                                                Log.d("QuestionActivity", "DocumentSnapshot written with ID: " + documentReference.getId());
//                                            }
//                                        })
//                                        .addOnFailureListener(new OnFailureListener() {
//                                            @Override
//                                            public void onFailure(@NonNull Exception e) {
//                                                Log.w("QuestionActivity", "Error adding document", e);
//                                            }
//                                        });
//                            }
//                        }
//                    })
//                    .addOnFailureListener(e -> Log.w("QuestionActivity", "Error adding document", e));
//        }
//    }
//
//    private void playAnim(final View view, final int value, final int viewNum) {
//        view.animate().alpha(value).scaleX(value).scaleY(value).setDuration(500)
//                .setStartDelay(100).setInterpolator(new DecelerateInterpolator())
//                .setListener(new Animator.AnimatorListener() {
//                    @Override
//                    public void onAnimationStart(Animator animation) {
//
//                    }
//
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//                        if (value == 0) {
//                            switch (viewNum) {
//                                case 0:
//                                    ((TextView) view).setText(questionList.get(questionNumber).getQuestion());
//                                    break;
//                                case 1:
//                                    ((Button) view).setText(questionList.get(questionNumber).getOptionA());
//                                    break;
//                                case 2:
//                                    ((Button) view).setText(questionList.get(questionNumber).getOptionB());
//                                    break;
//                                case 3:
//                                    ((Button) view).setText(questionList.get(questionNumber).getOptionC());
//                                    break;
//                                case 4:
//                                    ((Button) view).setText(questionList.get(questionNumber).getOptionD());
//                                    break;
//                            }
//                            if (viewNum != 0)
//                                view.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#696880")));
//                            playAnim(view, 1, viewNum);
//                        }
//
//                    }
//
//                    @Override
//                    public void onAnimationCancel(Animator animation) {
//
//                    }
//
//                    @Override
//                    public void onAnimationRepeat(Animator animation) {
//
//                    }
//                });
//    }
//}
//
//
////public class QuestionReview extends AppCompatActivity {
////    FirestoreRecyclerAdapter adapter;
////    FirebaseFirestore db = FirebaseFirestore.getInstance();
////
////    @Override
////    protected void onCreate(Bundle savedInstanceState) {
////        super.onCreate(savedInstanceState);
////        setContentView(R.layout.activity_question_review);
////        Toolbar toolbar = findViewById(R.id.toolbarofquestionreviewactivity);
////        setSupportActionBar(toolbar);
////        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
////
////        FirebaseFirestore mFireBaseDB = FirebaseFirestore.getInstance();
////
////        RecyclerView recyclerView = findViewById(R.id.recycler_view_questionreview);
////
////        Intent intent = getIntent();
////        String id = intent.getStringExtra("quickened");
////        Log.d("QuestionReview", "QuizTakenid is : " + id);
////        Query query = mFireBaseDB.collection("quizTakenQuestions")
////                .whereEqualTo("quizTakenId", id);
////        //recycler options
////        FirestoreRecyclerOptions<QuestionModel> options = new FirestoreRecyclerOptions.Builder<QuestionModel>()
////                .setQuery(query, QuestionModel.class).build();
////        adapter = new FirestoreRecyclerAdapter<QuestionModel, QuestionViewHolder>(options) {
////            @NonNull
////            @Override
////            public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
////                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.questiondetailss, parent, false);
////                return new QuestionViewHolder(view);
////            }
////
////            int i = 1;
////
////            @Override
////            protected void onBindViewHolder(@NonNull QuestionViewHolder holder, int position, @NonNull QuestionModel model) {
////
////
////                String questionid = model.getQuestionid();
////                DocumentReference documentReference = db.collection("questions").document(questionid);
////                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
////                    @Override
////                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
////                        if (task.isSuccessful()) {
////                            DocumentSnapshot documentSnapshot = task.getResult();
////                            if (documentSnapshot.exists()) {
////                                Log.d("QuestionReview", "Document values : " + documentSnapshot.getData());
////                                holder.textViewQuestion.setText("" + (documentSnapshot.get("question")));
////                                String ans = (String) documentSnapshot.get("correctOption");
////
////                                switch (ans) {
////                                    case "1":
////                                        holder.textViewCorrectAnswer.setText(""+documentSnapshot.get("option1"));
////                                        break;
////                                    case "2":
////                                        holder.textViewCorrectAnswer.setText(""+documentSnapshot.get("option2"));
////                                        break;
////                                    case "3":
////                                        holder.textViewCorrectAnswer.setText(""+documentSnapshot.get("option3"));
////                                        break;
////                                    case "4":
////                                        holder.textViewCorrectAnswer.setText(""+documentSnapshot.get("option4"));
////                                        break;
////                                }
////                                Log.d("QuestionReview",  "  " + ans+"  "+documentSnapshot.get("option4"));
////
////                            }
////                        }
////                    }
////                });
////
////                holder.textViewanswer.setText(model.getAttemptedAnswer());
////                holder.textViewtime.setText(""+ model.getTimeTaken());
////               // holder.textViewquestionid.setText(model.getQuestionid());
////            }
////        };
////        recyclerView.setHasFixedSize(true);
////        recyclerView.setLayoutManager(new LinearLayoutManager(this));
////        recyclerView.setAdapter(adapter);
////    }
////
////    private class QuestionViewHolder extends RecyclerView.ViewHolder {
////        private TextView textViewtime;
////        private TextView textViewanswer;
////        //private TextView textViewquestionid;
////        private TextView textViewCorrectAnswer;
////        private TextView textViewQuestion;
//////        private TextView textViewquiztakenid;
////
////        private QuestionViewHolder(@NonNull View itemView) {
////            super(itemView);
////            textViewtime = itemView.findViewById(R.id.textViewtime);
////            textViewanswer = itemView.findViewById(R.id.textViewanswer);
////          //  textViewquestionid = itemView.findViewById(R.id.textViewquestionid);
////            textViewCorrectAnswer = itemView.findViewById(R.id.textViewCorrectAnswer);
////            textViewQuestion = itemView.findViewById(R.id.textViewQuestion);
////        }
////    }
////
////
////    @Override
////    public void onStart() {
////        super.onStart();
////        adapter.startListening();
////    }
////
////    @Override
////    public void onStop() {
////        super.onStop();
////        adapter.stopListening();
////    }
////}