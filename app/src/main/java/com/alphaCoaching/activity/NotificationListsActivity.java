package com.alphaCoaching.activity;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.alphaCoaching.Constant.Constant;
import com.alphaCoaching.Model.NotificationModel;
import com.alphaCoaching.Model.PDFModel;
import com.alphaCoaching.R;
import com.alphaCoaching.Utils.UserSharedPreferenceManager;
import com.alphaCoaching.adapter.NotificationAdapter;
import com.alphaCoaching.adapter.PDFAdapter;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

import static com.alphaCoaching.AlphaApplication.getAppContext;

public class NotificationListsActivity extends AppCompatActivity implements NotificationAdapter.OnNotificationClick {

    private static RecyclerView recyclerView;
    private static NotificationAdapter adapter;
    private FirebaseAuth fireAuth;
    private static FirebaseFirestore mFireBaseDB;
    private Toolbar toolbar;
    private static ArrayList<NotificationModel> notifications;
    private LinearLayout mProgressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_lists);

        mFireBaseDB = FirebaseFirestore.getInstance();
        toolbar = findViewById(R.id.ToolbarOfNotificationListActivity);
        mProgressbar = findViewById(R.id.notificationtProgressbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Recent Notifications");
        recyclerView = findViewById(R.id.recycler_viewNotification);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        getAllNotification();

    }

    private void getAllNotification() {
        mProgressbar.setVisibility(View.VISIBLE);
        notifications = new ArrayList<>();
        mFireBaseDB.collection(Constant.NOTIFICATION_COLLECTION)
                .whereEqualTo(Constant.NotificationFields.USER_ID, UserSharedPreferenceManager.getUserInfo(getAppContext(), UserSharedPreferenceManager.userInfoFields.USER_UUID))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot snapshot : Objects.requireNonNull(task.getResult())) {
                            NotificationModel model = snapshot.toObject(NotificationModel.class);
                            model.setId(snapshot.getId());
                            notifications.add(model);
                        }
                        adapter = new NotificationAdapter(notifications, this);
                        recyclerView.setLayoutManager(new LinearLayoutManager(this));
                        recyclerView.setAdapter(adapter);
                        mProgressbar.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    public void onItemClick(NotificationModel snapshot, int position) {
        String subject = snapshot.getNotification_subject();
        String url = snapshot.getSubject_url();
        Intent intent = null;
        if (subject.equals("video")) {
            intent = new Intent(getApplicationContext(), DisplayVideos.class);
            intent.putExtra("force_fullscreen", true);
            intent.putExtra("url", url);
        } else if (subject.equals("pdf")) {
            intent = new Intent(getApplicationContext(), PdfViewActivity.class);
            intent.putExtra("url", url);
        } else if (subject.equals("quiz")) {
            intent = new Intent(getApplicationContext(), QuizDetailActivity.class);
            intent.putExtra("docID", url);
        }
        startActivity(intent);
    }
}
