package com.alphaCoaching.FcmConnection;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;


import java.util.Map;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.alphaCoaching.R;
import com.alphaCoaching.activity.DisplayVideos;
import com.alphaCoaching.activity.MainActivity;
import com.alphaCoaching.activity.PdfListActivity;
import com.alphaCoaching.activity.PdfViewActivity;
import com.alphaCoaching.activity.QuizDetailActivity;
import com.alphaCoaching.activity.VideosActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class FCMMessageReceiver extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Map<String, String> data = remoteMessage.getData();
        String notificationSubject = data.get("subject");
        String subjectUrl = data.get("subjectUrl");
        String projectName = data.get("H3");
        showNotification(notificationSubject, subjectUrl, projectName);
    }

    public void showNotification(String subject, String url, String applicatioName) {
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
            title = "Video is added";
            description = "Video is added for you. Click to watch the video now.";
            intent = new Intent(getApplicationContext(), DisplayVideos.class);
            intent.putExtra("force_fullscreen", true);
            intent.putExtra("url", url);
        } else if (subject.equals("pdf")) {
            title = "PDF is added";
            description = "PDF is added for you. Click to read the PDF now.";
            intent = new Intent(getApplicationContext(), PdfViewActivity.class);
            intent.putExtra("url", url);
        } else if (subject.equals("quiz")) {
            title = "Quiz is added";
            description = "Quiz is setup for you. Click to see the details of the quiz";
            intent = new Intent(getApplicationContext(), QuizDetailActivity.class);
            intent.putExtra("docID", url);
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "MyNotification")
                .setContentTitle(title)
                .setSmallIcon(R.drawable.alpha_logo)
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
}
