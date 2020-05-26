package com.alphaCoaching.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alphaCoaching.R;
import com.alphaCoaching.Utils.UserSharedPreferenceManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

import static com.alphaCoaching.AlphaApplication.getAppContext;

public class LoginActivity extends AppCompatActivity {
    RelativeLayout mLoginLayout;
    RelativeLayout mForgotPass;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    EditText email;
    EditText password;
    FirebaseAuth fireAuth;
    Handler handler = new Handler();
    FirebaseFirestore mFireBaseDB;
    CircularProgressButton mLoginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mLoginLayout = findViewById(R.id.loginScreen);
        mForgotPass = findViewById(R.id.forgotPass);
        email = findViewById(R.id.username);
        password = findViewById(R.id.pass);
        mLoginBtn = findViewById(R.id.loginButton);

        handler.postDelayed(() -> {
            mLoginLayout.setVisibility(View.VISIBLE);
            mForgotPass.setVisibility(View.VISIBLE);
        }, 3000);

        fireAuth = FirebaseAuth.getInstance();
        mLoginBtn.setOnClickListener(v -> userLogin());

    }

    /**
     * Function used to authenticate the user input.
     */
    private void userLogin() {
        mLoginBtn.startMorphAnimation();
        final String sEmail = email.getText().toString().trim();
        final String sPassword = password.getText().toString().trim();
        if (sEmail.isEmpty()) {
            email.setError("Email Required bro!!");
            email.requestFocus();
            mLoginBtn.startMorphRevertAnimation();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(sEmail).matches()) {
            email.setError("Enter a Valid Email buddy!!");
            email.requestFocus();
            mLoginBtn.startMorphRevertAnimation();
            return;
        }
        if (sPassword.isEmpty()) {
            password.setError("Don't you know !! \nPassword is Required ");
            password.requestFocus();
            mLoginBtn.startMorphRevertAnimation();
            return;
        }
        if (sPassword.length() < 6) {
            password.setError("Sorry but  \npassword should be at least 6 character");
            password.requestFocus();
            mLoginBtn.startMorphRevertAnimation();
            return;
        }
        fireAuth.signInWithEmailAndPassword(sEmail, sPassword).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {


                mFireBaseDB = FirebaseFirestore.getInstance();
                FirebaseAuth fireAuth;
                fireAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = fireAuth.getCurrentUser();
                assert currentUser != null;
                String user_Uuid = currentUser.getUid();
                DocumentReference documentReference = db.collection
                        ("users").document(user_Uuid);
                documentReference.get().addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task1.getResult();
                        assert documentSnapshot != null;
                        if (documentSnapshot.exists()) {
                            String userFirstName = (String) documentSnapshot.get("firstName");
                            String userLastName = (String) documentSnapshot.get("lastName");
                            String userStandard = (String) documentSnapshot.get("standard");
                            String dateOfBirth = (String) documentSnapshot.get("dateOfBirth");
                            String userEmail = (String) documentSnapshot.get("email");
                            Log.d("LoginActivity", "User detail : "+ documentSnapshot.getId()+" " + userFirstName + "  " + userLastName);
                            UserSharedPreferenceManager.storeUserDetail(getAppContext(), userFirstName, userLastName, userStandard, dateOfBirth, userEmail);
                        }
                    }
                });

                Intent intent1 = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent1);
                Thread thread = new Thread(() -> {
                    try {
                        Thread.sleep(3700);
                        Intent intent11 = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent11);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                        mLoginBtn.dispose();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
            } else {
                mLoginBtn.startMorphRevertAnimation();
                Toast.makeText(getApplicationContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
//public class LoginActivity extends AppCompatActivity {
//    RelativeLayout mLoginLayout;
//    RelativeLayout mForgotPass;
//    EditText email;
//    EditText password;
//    FirebaseAuth fireAuth;
//    Handler handler = new Handler();
//    FirebaseFirestore mFireBaseDB;
//    CircularProgressButton mLoginBtn;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login);
//        mLoginLayout = findViewById(R.id.loginScreen);
//        mForgotPass = findViewById(R.id.forgotPass);
//        email = findViewById(R.id.username);
//        password = findViewById(R.id.pass);
//        mLoginBtn = findViewById(R.id.loginButton);
//
//        handler.postDelayed(() -> {
//            mLoginLayout.setVisibility(View.VISIBLE);
//            mForgotPass.setVisibility(View.VISIBLE);
//        }, 3000);
//
//        fireAuth = FirebaseAuth.getInstance();
//        mLoginBtn.setOnClickListener(v -> userLogin());
//
//    }
//
//    /**
//     * Function used to authenticate the user input.
//     */
//    private void userLogin() {
//        mLoginBtn.startMorphAnimation();
//        final String sEmail = email.getText().toString().trim();
//        final String sPassword = password.getText().toString().trim();
//        if (sEmail.isEmpty()) {
//            email.setError("Email Required bro!!");
//            email.requestFocus();
//            mLoginBtn.startMorphRevertAnimation();
//            return;
//        }
//        if (!Patterns.EMAIL_ADDRESS.matcher(sEmail).matches()) {
//            email.setError("Enter a Valid Email buddy!!");
//            email.requestFocus();
//            mLoginBtn.startMorphRevertAnimation();
//            return;
//        }
//        if (sPassword.isEmpty()) {
//            password.setError("Don't you know !! \nPassword is Required ");
//            password.requestFocus();
//            mLoginBtn.startMorphRevertAnimation();
//            return;
//        }
//        if (sPassword.length() < 6) {
//            password.setError("Sorry but  \npassword should be at least 6 character");
//            password.requestFocus();
//            mLoginBtn.startMorphRevertAnimation();
//            return;
//        }
//        fireAuth.signInWithEmailAndPassword(sEmail, sPassword).addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//
//
//                mFireBaseDB = FirebaseFirestore.getInstance();
//
//                Intent intent1 = new Intent(LoginActivity.this, MainActivity.class);
//                startActivity(intent1);
//                Thread thread = new Thread(() -> {
//                    try {
//                        Thread.sleep(3700);
//                        Intent intent11 = new Intent(LoginActivity.this, MainActivity.class);
//                        startActivity(intent11);
//                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//                        finish();
//                        mLoginBtn.dispose();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                });
//            } else {
//                mLoginBtn.startMorphRevertAnimation();
//                Toast.makeText(getApplicationContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//}