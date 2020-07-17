package com.AycScienceCoaching.FcmConnection;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;


import java.util.HashMap;
import java.util.Map;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.AycScienceCoaching.Constant.Constant;
import com.AycScienceCoaching.R;
import com.AycScienceCoaching.activity.DisplayVideos;
import com.AycScienceCoaching.Utils.UserSharedPreferenceManager;
import com.AycScienceCoaching.activity.NotificationListsActivity;
import com.AycScienceCoaching.activity.PdfViewActivity;
import com.AycScienceCoaching.activity.QuizDetailActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class FCMMessageReceiver extends FirebaseMessagingService {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFireStoreDb;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Map<String, String> data = remoteMessage.getData();
        String notificationSubject = data.get("subject");
        String subjectUrl = data.get("subjectUrl");
        String entityTitle = data.get("entityName");
        storeNotification(notificationSubject, subjectUrl, entityTitle);
        showNotification(notificationSubject, subjectUrl, entityTitle);
    }

    public void showNotification(String subject, String url, String entityName) {
        String title = "";
        String description = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel =
                    new NotificationChannel("MyNotification", "MyNotification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        Intent intent = null;
        if (subject.equals("video")) {
            title = "Video for " + entityName + " is added";
            description = "Click to watch the video now.";
//            intent = new Intent(getApplicationContext(), DisplayVideos.class);
//            intent.putExtra("force_fullscreen", true);
//            intent.putExtra("url", url);
        } else if (subject.equals("pdf")) {
            title = "PDF for " + entityName +" is added";
            description = "Click to read the PDF now.";
//            intent = new Intent(getApplicationContext(), PdfViewActivity.class);
//            intent.putExtra("url", url);
        } else if (subject.equals("quiz")) {
            title = entityName + " Quiz is added";
            description = "Click to see the details of the quiz";
//            intent = new Intent(getApplicationContext(), QuizDetailActivity.class);
//            intent.putExtra("docID", url);
        } else if (subject.equals("recentLecture")) {
            title = entityName + " Lecture is added for you.";
            description = "Click to see the lecture";
        }
        intent = new Intent(getApplicationContext(), NotificationListsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "MyNotification")
                .setContentTitle(title)
                .setSmallIcon(R.drawable.square_logo)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                .setContentText(description)
//                .setContentInfo("Version No: " + message)
//                .setSubText("Tap to open App")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(999, builder.build());
    }

    @Override
    public void onNewToken(String registrationToken) {
        Log.d("Firebase", registrationToken);
        startService(new Intent(this, FCMTokenReceiver.class));
    }

//    this function stores the notification on the server when user gets the notification.
    private void storeNotification(String subject, String url, String title) {
        mAuth = FirebaseAuth.getInstance();
        mFireStoreDb = FirebaseFirestore.getInstance();

        Map<String, Object> dataToSave = new HashMap<>();
        dataToSave.put(Constant.NotificationFields.SUBJECT, subject);
        dataToSave.put(Constant.NotificationFields.ENTITY_TITLE, title);
        dataToSave.put(Constant.NotificationFields.SUBJECT_URL, url);
        dataToSave.put(Constant.NotificationFields.STATUS, false);
        dataToSave.put(Constant.NotificationFields.TIME, System.currentTimeMillis());
        dataToSave.put(Constant.NotificationFields.USER_ID, UserSharedPreferenceManager.getUserInfo(getApplicationContext(), UserSharedPreferenceManager.userInfoFields.USER_UUID));

        mFireStoreDb.collection(Constant.NOTIFICATION_COLLECTION)
                .document()
                .set(dataToSave)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
//                        notification data is stored into database.
                    }
                });
    }
}
