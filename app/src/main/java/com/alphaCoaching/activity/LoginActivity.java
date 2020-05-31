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

import com.alphaCoaching.Constant.Constant;
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
    private RelativeLayout mLoginLayout;
    private RelativeLayout mForgotPass;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText email;
    private EditText password;
    private FirebaseAuth fireAuth;
    private Handler handler = new Handler();
    private FirebaseFirestore mFireBaseDB;
    private CircularProgressButton mLoginBtn;

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
            email.setError("Email Required");
            email.requestFocus();
            mLoginBtn.startMorphRevertAnimation();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(sEmail).matches()) {
            email.setError("Enter a Valid Email");
            email.requestFocus();
            mLoginBtn.startMorphRevertAnimation();
            return;
        }
        if (sPassword.isEmpty()) {
            password.setError("Password is Required ");
            password.requestFocus();
            mLoginBtn.startMorphRevertAnimation();
            return;
        }
        if (sPassword.length() < 6) {
            password.setError("password should be at least 6 character");
            password.requestFocus();
            mLoginBtn.startMorphRevertAnimation();
            return;
        }
        fireAuth.signInWithEmailAndPassword(sEmail, sPassword).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                mFireBaseDB = FirebaseFirestore.getInstance();
                FirebaseUser currentUser = fireAuth.getCurrentUser();
                assert currentUser != null;
                String user_Uuid = currentUser.getUid();
                DocumentReference documentReference = db.collection
                        (Constant.USER_COLLECTION).document(user_Uuid);
                documentReference.get().addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task1.getResult();
                        assert documentSnapshot != null;
                        if (documentSnapshot.exists()) {
                            String userFirstName = (String) documentSnapshot.get(Constant.UserCollectionFields.FIRST_NAME);
                            String userLastName = (String) documentSnapshot.get(Constant.UserCollectionFields.LAST_NAME);
                            String userStandard = (String) documentSnapshot.get(Constant.UserCollectionFields.STANDARD);
                            String dateOfBirth = (String) documentSnapshot.get(Constant.UserCollectionFields.DOB);
                            String userEmail = (String) documentSnapshot.get(Constant.UserCollectionFields.EMAIL);
                            UserSharedPreferenceManager.storeUserDetail(getAppContext(), user_Uuid, userFirstName, userLastName, userStandard, dateOfBirth, userEmail);
                        }
                    }
                });

//                Intent intent1 = new Intent(LoginActivity.this, MainActivity.class);
//                startActivity(intent1);
                Intent intent11 = new Intent(LoginActivity.this, MainActivity.class);
                intent11.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent11);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                mLoginBtn.dispose();
            } else {
                mLoginBtn.startMorphRevertAnimation();
                Toast.makeText(getApplicationContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}