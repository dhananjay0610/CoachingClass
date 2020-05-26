package com.alphaCoaching.activity;

import android.animation.Animator;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.alphaCoaching.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class QuestionReview extends AppCompatActivity implements View.OnClickListener {
    private TextView question, qCount, timetaken, averagetime, TotalScore;
    private Button option1, option2, option3, option4;
    private List<Question> questionList;
    private int questionNumber;
    private FirebaseFirestore firestore;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    // FirebaseAuth fireAuth;
    private String score;
//    ArrayList<Long> arr = new ArrayList<>();
//    HashMap<Integer, Long> mp = new HashMap<>();

    //Array needed to store data in order to add them in the database
    String[] QuestionId = new String[45];
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_review);

        Toolbar toolbar = findViewById(R.id.ToolbarOfQuestionReviewActivity);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Review Your Questions");


        question = findViewById(R.id.questionReview);
        qCount = findViewById(R.id.quesNumReview);
        option1 = findViewById(R.id.option1Review);
        option2 = findViewById(R.id.option2Review);
        option3 = findViewById(R.id.option3Review);
        option4 = findViewById(R.id.option4Review);
        Button next = findViewById(R.id.nextReview);
        Button previous = findViewById(R.id.previousReview);
        timetaken = findViewById(R.id.TimeTakenReview);
        averagetime = findViewById(R.id.AverageTimeReview);
        imageView = findViewById(R.id.imageViewForTick);
        TotalScore = findViewById(R.id.TotalScore);
        next.setOnClickListener(this);
        previous.setOnClickListener(this);
        firestore = FirebaseFirestore.getInstance();
        getQuestionsList();
        // String docId = getIntent().getStringExtra("docID");
        // String quizTakenId = getIntent().getStringExtra("quickened");

    }

    @Override
    public boolean onSupportNavigateUp() {
        return super.onSupportNavigateUp();
    }

    private void getQuestionsList() {
        String docId = getIntent().getStringExtra("QuizId");
        questionList = new ArrayList<>();
        firestore.collection("questions").whereEqualTo("quizID", docId)
                .get().addOnCompleteListener(task -> {
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
        });

        String quizTakenId = getIntent().getStringExtra("quickened");
        final String[] MaxScore = {null};
        assert quizTakenId != null;
        DocumentReference documentReference = db.collection("quizTaken").document(quizTakenId);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    assert documentSnapshot != null;
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

    //int questionNo = 0;

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


        int n = questionList.get(questionNumber).getCorrectOption();
        String ans = "";
//        int[] arr = new int[n];
//        for (int i = 0; i < n; i++)
//            arr[i] = 0;
//        arr[n - 1] = 1;
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
        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                assert documentSnapshot != null;
                if (documentSnapshot.exists()) {
                    averagetime.setText("Average : " + documentSnapshot.get("time"));
                    // score = (String) documentSnapshot.get("score");
                    Log.d("QuestionReview", "Average Time is :  " + documentSnapshot.get("time"));
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
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                for (DocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult())) {
                    AttemptedAnswer[0] = documentSnapshot.get("attemptedAnswer");
                    timeTaken[0] = documentSnapshot.get("timeTaken");
                    Log.d("QuestionReview", timeTaken[0] + "  Attempted ans is : " + AttemptedAnswer[0] + "document snapshot get " + documentSnapshot.get("attemptedAnswer"));

                }
                timetaken.setText("Time : " + timeTaken[0]);
                Log.d("QuestionReview", timeTaken[0] + "  ------   " + AttemptedAnswer[0] + "  ------   ");
                if (AttemptedAnswer[0] == null) {
                    Toast.makeText(QuestionReview.this, "Not attempted any answer", Toast.LENGTH_SHORT).show();
                    imageView.setImageResource(R.drawable.ic_close_black_24dp);
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
        });

    }

//    int k = 0;

    private void previousQuestion() {
        if (questionNumber != 0) {
            --questionNumber;
            playAnim(question, 0, 0);
            playAnim(option1, 0, 1);
            playAnim(option2, 0, 2);
            playAnim(option3, 0, 3);
            playAnim(option4, 0, 4);

            qCount.setText((questionNumber + 1) + "/" + (questionList.size()));

            option1.setEnabled(true);
            option2.setEnabled(true);
            option3.setEnabled(true);
            option4.setEnabled(true);
            imageView.setImageResource(R.drawable.ic_autorenew_black_24dp);
            averagetime.setText("Loading...");
            timetaken.setText("Loading...");
        }
    }

    private void changeQuestion() {
        if (questionNumber < questionList.size() - 1) {
            questionNumber++;

            playAnim(question, 0, 0);
            playAnim(option1, 0, 1);
            playAnim(option2, 0, 2);
            playAnim(option3, 0, 3);
            playAnim(option4, 0, 4);

            qCount.setText((questionNumber + 1) + "/" + (questionList.size()));

            option1.setEnabled(true);
            option2.setEnabled(true);
            option3.setEnabled(true);
            option4.setEnabled(true);
            imageView.setImageResource(R.drawable.ic_autorenew_black_24dp);
            averagetime.setText("Loading...");
            timetaken.setText("Loading...");
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
        } else {
            //boolean examend = true;
            // Go to Score Activity

            // String questionid = questionList.get(0).getQuestionId();
            String quizTakenId = getIntent().getStringExtra("quickened");
            assert quizTakenId != null;
            DocumentReference documentReference = db.collection("quizTaken").document(quizTakenId);
            documentReference.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    assert documentSnapshot != null;
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
//                                int[] arr = new int[n];
//                                for (int i = 0; i < n; i++)
//                                    arr[i] = 0;
//                                arr[n - 1] = 1;
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
                                documentReference.get().addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot documentSnapshot = task.getResult();
                                        assert documentSnapshot != null;
                                        if (documentSnapshot.exists()) {
                                            averagetime.setText("Average : " + documentSnapshot.get("time"));
                                            // score = (String) documentSnapshot.get("score");
                                            Log.d("QuestionReview", "averagetime is :  " + documentSnapshot.get("time"));
                                        }
                                    }
                                });
                                String questionid = questionList.get(questionNumber).getQuestionId();
                                String quizTakenId = getIntent().getStringExtra("quickened");
                                FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
                                CollectionReference yourCollRef = rootRef.collection("quizTakenQuestions");
                                Query query = yourCollRef.whereEqualTo("quizTakenId", quizTakenId)
                                        .whereEqualTo("questionId", questionid);
                                final Object[] AttemptedAnswer = {null};
                                final Object[] timeTaken = {""};
                                String finalAns = ans;
                                query.get().addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {

                                        for (DocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult())) {
                                            AttemptedAnswer[0] = documentSnapshot.get("attemptedAnswer");
                                            timeTaken[0] = documentSnapshot.get("timeTaken");
                                            Log.d("QuestionReview", timeTaken[0] + "  Attempted ans is : " + AttemptedAnswer[0] + "document snapshot get " + documentSnapshot.get("attemptedAnswer"));

                                        }
                                        timetaken.setText("Time : " + timeTaken[0]);
                                        // Log.d("QuestionReview", timeTaken[0] + "  ------   " + AttemptedAnswer[0] + "  ------   ");
                                        if (AttemptedAnswer[0] == null) {
                                            Toast.makeText(QuestionReview.this, "Not attempted any answer", Toast.LENGTH_SHORT).show();
                                            imageView.setImageResource(R.drawable.ic_close_black_24dp);
                                        } else {
                                            //  Log.d("QuestionReview", "------" + finalAns + "  " + AttemptedAnswer[0]);

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
                                });
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