package com.alphaCoaching.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import static android.content.ContentValues.TAG;

public class SplashScreenActivity extends Activity {
    private FirebaseFirestore mFireBaseDB;
    private FirebaseAuth fireAuth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Access a Cloud Firestore instance from your Activity
        mFireBaseDB = FirebaseFirestore.getInstance();
        fireAuth = FirebaseAuth.getInstance();
        Log.d(TAG, "onCreate: ");
       // Toast.makeText(getApplication(), "Into the splashscreen", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (fireAuth.getCurrentUser() != null) {
                    openMainActivity();
//                    openLoginActivity();
                } else {
                    openLoginActivity();
                }
            }
        }, 3000);

//        openLoginActivity();
//        openMainActivity();
    }

    private void openLoginActivity() {
        Intent mainActivityIntent = new Intent(getApplicationContext(), LoginActivity.class);
        mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainActivityIntent);
        finish();
    }

    /**
     * To Open the main activity.
     */
    private void openMainActivity() {
        Intent mainActivityIntent = new Intent(getApplicationContext(), MainActivity.class);
        mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainActivityIntent);
        finish();
    }

    public static class CatGridAdapter {
    }
}
