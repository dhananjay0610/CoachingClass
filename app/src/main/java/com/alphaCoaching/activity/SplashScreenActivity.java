package com.alphaCoaching.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import com.google.firebase.firestore.FirebaseFirestore;

import static android.content.ContentValues.TAG;

public class SplashScreenActivity extends Activity {
    private FirebaseFirestore mFireBaseDB;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Access a Cloud Firestore instance from your Activity
        mFireBaseDB = FirebaseFirestore.getInstance();
        Log.d(TAG, "onCreate: ");
        Toast.makeText(getApplication(), "Into the splashscreen", Toast.LENGTH_SHORT).show();

        openMainActivity();
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
}
