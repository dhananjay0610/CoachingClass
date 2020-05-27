package com.alphaCoaching.activity;

import android.animation.Animator;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.alphaCoaching.Constant.Constant;
import com.alphaCoaching.R;
import com.alphaCoaching.Utils.UserSharedPreferenceManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static com.alphaCoaching.AlphaApplication.getAppContext;

public class QuestionDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView question, qCount, timer;
    private Button option1, option2, option3, option4;
    private List<Question> questionList;
    private int questionNumber;
    private FirebaseFirestore firestore;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth fireAuth;
    private int score;
    private String quizId;
    private Long quizTime;
    private Button next, previous;
    private Toolbar toolbar;
    private ProgressBar mProgressBar;
    ArrayList<Long> arr = new ArrayList<>();
    HashMap<Integer, Long> mp = new HashMap<>();

    //Array needed to store data in order to add them in the database
    String[] QuestionId = new String[45];
    String[] attemptedanswer = new String[45];
    long[] Timetaken = new long[45];
    int i = 0;

    Boolean examend = false;

    long starttime;
    long endtime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_detail);
        score = 0;
        for (int i = 0; i < 100; i++)
            arr.add((long) 0);

        toolbar = findViewById(R.id.ToolbarOfQuestionDetailActivity);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Bundle bundle = getIntent().getExtras();
        quizId = bundle.getString("docID");
        quizTime = Long.parseLong(bundle.get("quizTime").toString());


        question = findViewById(R.id.question);
        qCount = findViewById(R.id.quesNum);
        timer = findViewById(R.id.countDown);

        option1 = findViewById(R.id.option1);
        option2 = findViewById(R.id.option2);
        option3 = findViewById(R.id.option3);
        option4 = findViewById(R.id.option4);
        next = findViewById(R.id.next);
        previous = findViewById(R.id.previous);
        mProgressBar = findViewById(R.id.progressbar);

        option1.setOnClickListener(this);
        option2.setOnClickListener(this);
        option3.setOnClickListener(this);
        option4.setOnClickListener(this);
        next.setOnClickListener(this);
        previous.setOnClickListener(this);
        firestore = FirebaseFirestore.getInstance();

        getQuestionsList();
    }

    @Override
    public boolean onSupportNavigateUp() {
        return super.onSupportNavigateUp();
    }

    //method to get current time
    public long getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formate = new SimpleDateFormat("HH:mm:ss");
        //String date = formate.format(calendar.getTime());
        return calendar.getTimeInMillis() / 1000;
    }

    private void getQuestionsList() {
//        String docId = getIntent().getStringExtra("docID");
        mProgressBar.setVisibility(View.VISIBLE);
        questionList = new ArrayList<>();
        firestore.collection(Constant.QUESTION_COLLECTION).whereEqualTo(Constant.QuestionCollectionFields.QUIZ_ID, quizId)
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot questions = task.getResult();
                assert questions != null;
                int i = 0;
                for (QueryDocumentSnapshot doc : questions) {
                    QuestionId[i] = doc.getId();
                    i++;
                    questionList.add(new Question(doc.getId(),
                            doc.getString(Constant.QuestionCollectionFields.QUESTION),
                            doc.getString(Constant.QuestionCollectionFields.OPTION_1),
                            doc.getString(Constant.QuestionCollectionFields.OPTION_2),
                            doc.getString(Constant.QuestionCollectionFields.OPTION_3),
                            doc.getString(Constant.QuestionCollectionFields.OPTION_4),
                            doc.getLong(Constant.QuestionCollectionFields.CORRECT_OPTION),
                            doc.getId()
                    ));
                }
                if (questionList.size() == 0) {
                    Toast.makeText(getAppContext(), "Question list is empty in this quiz.", Toast.LENGTH_LONG).show();
                    QuestionDetailActivity.this.finish();
                }
                setQuestion();
            } else {
                Toast.makeText(QuestionDetailActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setQuestion() {
        if (questionList.isEmpty()) {
            mProgressBar.setVisibility(View.GONE);
            Toast.makeText(getAppContext(), "Question list is empty in this quiz.", Toast.LENGTH_LONG).show();
            QuestionDetailActivity.this.finish();
        } else {
            timer.setText("Time: " + (quizTime));
            question.setText(questionList.get(0).getQuestion());
            option1.setText(questionList.get(0).getOptionA());
            option2.setText(questionList.get(0).getOptionB());
            option3.setText(questionList.get(0).getOptionC());
            option4.setText(questionList.get(0).getOptionD());
            qCount.setText((1) + "/" + (questionList.size()));
            startTimer();
            starttime = getCurrentTime();
            questionNumber = 0;
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    private void startTimer() {
        // int ans = 0;
//        long quizTime = getIntent().getLongExtra("quizTime", 2000);
        final long[] mTimeleftinmillis = {quizTime * 60000};
        CountDownTimer countDownperquiz = new CountDownTimer(mTimeleftinmillis[0], 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeleftinmillis[0] = millisUntilFinished;
                int minutes = (int) (mTimeleftinmillis[0] / 1000) / 60;
                int seconds = (int) (mTimeleftinmillis[0] / 1000) % 60;
                String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
                timer.setText("Time :- " + timeLeftFormatted);
            }

            @Override
            public void onFinish() {
                //to check if exam has end
                if (!examend) {
                    Intent intent = new Intent(QuestionDetailActivity.this, ScoreActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("SCORE", (score) + "/" + (questionList.size()));
                    startActivity(intent);
                    QuestionDetailActivity.this.finish();
                    examend = true;
                    // changeQuestion();
                }
            }
        };
        countDownperquiz.start();
    }

    //variable to iterate over array TimeTaken ,AttemptedAnswer
    int k = 0;
    int selectedOption = 0;

    @Override
    public void onClick(View v) {
        // long a = 0;
        if (v.getId() == (R.id.next)) {
            endtime = getCurrentTime();
            arr.add(arr.get(questionNumber) + endtime - starttime);
            if (mp.containsKey(questionNumber))
                mp.put(questionNumber, mp.get(questionNumber) + endtime - starttime);
            else
                mp.put(questionNumber, endtime - starttime);
            Toast.makeText(this, "Time Taken: " + mp.get(questionNumber), Toast.LENGTH_SHORT).show();
            Timetaken[k] = mp.get(questionNumber);

            checkAnswer(selectedOption);

            String option = "";
            if (selectedOption == 1)
                option = (questionList.get(questionNumber).getOptionA());
            else if (selectedOption == 2)
                option = (questionList.get(questionNumber).getOptionB());
            else if (selectedOption == 3)
                option = (questionList.get(questionNumber).getOptionC());
            else if (selectedOption == 4)
                option = (questionList.get(questionNumber).getOptionD());
            attemptedanswer[k] = option;


            k++;
        }
        if (v.getId() == R.id.previous) {
            starttime = getCurrentTime();
        }
        // switch (v.getId()) {
        if (v.getId() == R.id.option1) {
            selectedOption = 1;
//                option4.setEnabled(false);
//                option2.setEnabled(false);
//                option3.setEnabled(false);
            option1.setBackgroundTintList(ColorStateList.valueOf(Color.CYAN));
            option2.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));
            option3.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));
            option4.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));
        }
        // break;
        if (v.getId() == R.id.option2) {
            selectedOption = 2;
//                option1.setEnabled(false);
//                option4.setEnabled(false);
//                option3.setEnabled(false);
            option2.setBackgroundTintList(ColorStateList.valueOf(Color.CYAN));
            option1.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));
            option3.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));
            option4.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));
            //break;
        }
        if (v.getId() == R.id.option3) {
            selectedOption = 3;
//                option1.setEnabled(false);
//                option2.setEnabled(false);
//                option4.setEnabled(false);
            option3.setBackgroundTintList(ColorStateList.valueOf(Color.CYAN));
            option1.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));
            option2.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));
            option4.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));
//                break;
        }
        if (v.getId() == R.id.option4) {
            selectedOption = 4;
//                option1.setEnabled(false);
//                option2.setEnabled(false);
//                option3.setEnabled(false);
            option4.setBackgroundTintList(ColorStateList.valueOf(Color.CYAN));
            option1.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));
            option3.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));
            option2.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));
            //break;

        } else if (v.getId() == R.id.previous) {
            previousQuestion();
            //return;
        } else if (v.getId() == R.id.next) {
            changeQuestion();
            //      return;
        }
        //   default:
        // }


    }

    private void checkAnswer(int selectedOption) {
        if (selectedOption == questionList.get(questionNumber).getCorrectOption()) {
            // view.setBackgroundTintList(ColorStateList.valueOf(Color.CYAN));
            score++;
        } else {
            // (view).setBackgroundTintList(ColorStateList.valueOf(Color.CYAN));
        }
    }

    private void previousQuestion() {
        if (questionNumber != 0) {
            questionNumber--;
            playAnim(question, 0, 0);
            playAnim(option1, 0, 1);
            playAnim(option2, 0, 2);
            playAnim(option3, 0, 3);
            playAnim(option4, 0, 4);

            qCount.setText((questionNumber + 1) + "/" + (questionList.size()));

            timer.setText("Time: " + (" "));
            option1.setEnabled(true);
            option2.setEnabled(true);
            option3.setEnabled(true);
            option4.setEnabled(true);

//            checkAnswer(selectedOption);
//
//            String option = "";
//            if (selectedOption == 1)
//                option = (questionList.get(0).getOptionA());
//            else if (selectedOption == 2)
//                option = (questionList.get(0).getOptionB());
//            else if (selectedOption == 3)
//                option = (questionList.get(0).getOptionC());
//            else if (selectedOption == 4)
//                option = (questionList.get(0).getOptionD());
//            attemptedanswer[k] = option;
        }
    }

    private void changeQuestion() {

        starttime = getCurrentTime();
        if (questionNumber < questionList.size() - 1) {

            questionNumber++;

            playAnim(question, 0, 0);
            playAnim(option1, 0, 1);
            playAnim(option2, 0, 2);
            playAnim(option3, 0, 3);
            playAnim(option4, 0, 4);

            qCount.setText((questionNumber + 1) + "/" + (questionList.size()));

            timer.setText("Time: " + " ");
            //  startTimer();
            option1.setEnabled(true);
            option2.setEnabled(true);
            option3.setEnabled(true);
            option4.setEnabled(true);


        } else {
            examend = true;
            // Go to Score Activity
            Intent intent = new Intent(QuestionDetailActivity.this, ScoreActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("SCORE", (score) + "/" + (questionList.size()));
            startActivity(intent);
            QuestionDetailActivity.this.finish();

            //adding data to the FireStore quizTaken collection

            String FirstName = UserSharedPreferenceManager.getUserInfo(getAppContext(), UserSharedPreferenceManager.userInfoFields.USER_FIRST_NAME);
            String LastName = UserSharedPreferenceManager.getUserInfo(getAppContext(), UserSharedPreferenceManager.userInfoFields.USER_LAST_NAME);
            String UserName = FirstName + " " + LastName;
            Map<String, Object> data = new HashMap<>();
            data.put(Constant.QuizTakenCollectionFields.QUIZ_ID, quizId);
            data.put(Constant.QuizTakenCollectionFields.SCORE, String.valueOf(score));
            data.put(Constant.QuizTakenCollectionFields.TOTAL_SCORE, String.valueOf(questionList.size()));
            data.put(Constant.QuizTakenCollectionFields.USER_ID, UserSharedPreferenceManager.getUserInfo(getAppContext(), UserSharedPreferenceManager.userInfoFields.USER_UUID));
            data.put(Constant.QuizTakenCollectionFields.USER_NAME, UserName);
            firestore.collection(Constant.QUIZ_TAKEN_COLLECTION)
                    .add(data)
                    .addOnSuccessListener(documentReference1 -> {
                        Log.d("QuestionActivity", "DocumentSnapshot written with ID: " + documentReference1.getId());

                        //adding data to the quizTakenQuestions collection
                        for (int questionnumber = 0; questionnumber < questionList.size(); questionnumber++) {
                            Map<String, Object> data1 = new HashMap<>();
                            data1.put(Constant.QuizTakenQuestionsFields.ATTEMPTED_ANS, attemptedanswer[questionnumber]);
                            data1.put(Constant.QuizTakenQuestionsFields.QUESTION_ID, questionList.get(questionnumber).getId());
                            data1.put(Constant.QuizTakenQuestionsFields.QUIZ_TAKEN_ID, documentReference1.getId());
                            data1.put(Constant.QuizTakenQuestionsFields.TIME_TAKEN, mp.get(questionnumber));
                            firestore.collection(Constant.QUIZ_TAKEN_QUESTION_COLLECTION)
                                    .add(data1)
                                    .addOnSuccessListener(documentReference11 -> Log.d("QuestionActivity", "DocumentSnapshot written with ID: " + documentReference11.getId()))
                                    .addOnFailureListener(e -> Log.w("QuestionActivity", "Error adding document", e));
                        }
                    })
                    .addOnFailureListener(e -> Log.w("QuestionActivity", "Error adding document", e));

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
                            if (viewNum != 0)
                                view.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#696880")));
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
}