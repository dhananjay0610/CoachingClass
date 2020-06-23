package com.alphaCoaching.FcmConnection;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.alphaCoaching.Constant.Constant;
import com.alphaCoaching.R;
import com.alphaCoaching.Utils.UserSharedPreferenceManager;
import com.alphaCoaching.activity.LoginActivity;
import com.alphaCoaching.activity.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import androidx.annotation.NonNull;

public class FCMTokenReceiver extends IntentService {
    public static String token = "";
    private FirebaseFirestore mFireBaseDB = FirebaseFirestore.getInstance();

    public FCMTokenReceiver() {
        super("FcmTokenRegistrationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.d("Firebase" ,task.getException().toString());
                            return;
                        }
                        // Get new Instance ID token
                        token = task.getResult().getToken();
                        Log.d("Firebase registrat" , token.toString());
                        sendTokenToServer();

                    }
                });
    }



    private void sendTokenToServer() {
        String userId = UserSharedPreferenceManager.getUserInfo(getApplicationContext(), UserSharedPreferenceManager.userInfoFields.USER_UUID);
        if (userId == null)
            return;

        DocumentReference contact = mFireBaseDB.collection(Constant.USER_COLLECTION).document(userId);
        contact.update(Constant.UserCollectionFields.TOKEN, token)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(FCMTokenReceiver.this, "Updated Successfully",
                                Toast.LENGTH_SHORT).show();
                    }
                });

        Intent intent11 = new Intent(FCMTokenReceiver.this, MainActivity.class);
        intent11.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent11);
    }
}