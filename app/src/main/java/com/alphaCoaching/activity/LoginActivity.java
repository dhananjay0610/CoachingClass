package com.alphaCoaching.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alphaCoaching.Constant.Constant;
import com.alphaCoaching.FcmConnection.FCMTokenReceiver;
import com.alphaCoaching.R;
import com.alphaCoaching.Utils.UserSharedPreferenceManager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Objects;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

import static com.alphaCoaching.AlphaApplication.getAppContext;

public class LoginActivity extends AppCompatActivity {
    private RelativeLayout mLoginLayout;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText email;
    private EditText password;
    private TextView getLoginCredentials;
    private static FirebaseAuth fireAuth;
    private Handler handler = new Handler();
    private static FirebaseFirestore mFireBaseDB;
    private CircularProgressButton mLoginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mLoginLayout = findViewById(R.id.loginScreen);
        email = findViewById(R.id.username);
        password = findViewById(R.id.pass);
        mLoginBtn = findViewById(R.id.loginButton);
        getLoginCredentials = findViewById(R.id.loginRequest);

        handler.postDelayed(() -> {
            mLoginLayout.setVisibility(View.VISIBLE);
            getLoginCredentials.setVisibility(View.VISIBLE);
        }, 3000);

        fireAuth = FirebaseAuth.getInstance();
        mLoginBtn.setOnClickListener(v -> userLogin());
        getLoginCredentials.setOnClickListener(v -> openRequestCredentilsActivity());

    }

    private void openRequestCredentilsActivity() {
        Intent intent = new Intent(this, RequestCredentialsActivity.class);
        startActivity(intent);
        finish();
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
                DocumentReference documentReference = mFireBaseDB.collection
                        (Constant.USER_COLLECTION).document(user_Uuid);
                documentReference.get().addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task1.getResult();
                        assert documentSnapshot != null;
                        if (documentSnapshot.exists()) {
                            Boolean loginStatus = (Boolean) documentSnapshot.get(Constant.UserCollectionFields.LOGIN_STATUS);
//                            loginStatus is true when the user is logged in somewhere, otherwise return false for a single device logged in.
                            if (loginStatus) {
                                Toast.makeText(getApplicationContext(), "You are already logged in somewhere, logout from that device first!", Toast.LENGTH_LONG).show();
//                                logoutUser(user_Uuid);

                                fireAuth.signOut();
//                                UserSharedPreferenceManager.removeUserData(getApplicationContext());
                                Intent intent = new Intent(this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
//                                fireAuth.signOut();
//                                UserSharedPreferenceManager.removeUserData(getApplicationContext());
//                                Intent intent = new Intent(this, LoginActivity.class);
//                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                startActivity(intent);
//                                finish();
                            } else {
                                updateLoginStatus(user_Uuid, true);
                                String userFirstName = (String) documentSnapshot.get(Constant.UserCollectionFields.FIRST_NAME);
                                String userLastName = (String) documentSnapshot.get(Constant.UserCollectionFields.LAST_NAME);
                                String userStandard = (String) documentSnapshot.get(Constant.UserCollectionFields.STANDARD);
                                String dateOfBirth = (String) documentSnapshot.get(Constant.UserCollectionFields.DOB);
                                String userEmail = (String) documentSnapshot.get(Constant.UserCollectionFields.EMAIL);
                                UserSharedPreferenceManager.storeUserDetail(getAppContext(), user_Uuid, userFirstName, userLastName, userStandard, dateOfBirth, userEmail);
                                storeSubjects();
                                openMainActivity();
                            }
                        }
                    }
                });
                mLoginBtn.dispose();
            } else {
                mLoginBtn.startMorphRevertAnimation();
                Toast.makeText(getApplicationContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openMainActivity() {
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        Intent intent = new Intent(this, FCMTokenReceiver.class);
        startService(intent);
        finish();
    }

    private void updateLoginStatus(String userId, Boolean status) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference contact = firestore.collection(Constant.USER_COLLECTION).document(userId);
        contact.update(Constant.UserCollectionFields.LOGIN_STATUS, status)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                });
    }

    private void storeSubjects() {
        mFireBaseDB.collection(Constant.SUBJECT_COLLECTION)
                .whereEqualTo(Constant.UserCollectionFields.STANDARD, UserSharedPreferenceManager.getUserInfo(getAppContext(), UserSharedPreferenceManager.userInfoFields.USER_STANDARD))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot snapshot : Objects.requireNonNull(task.getResult())) {
                            UserSharedPreferenceManager.storeUserSubjects(getApplicationContext(), snapshot.getId(), Objects.requireNonNull(snapshot.get("name")).toString());
                        }
                    }
                });
    }


    public void logoutUser(String userId) {
//        Intent intent = new Intent(getAppContext(), LoginActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        FirebaseAuth.getInstance().signOut();
//        UserSharedPreferenceManager.removeUserData(getAppContext());
//        startActivity(intent);
//        finish();
        updateLoginStatus(userId, false);
    }
}