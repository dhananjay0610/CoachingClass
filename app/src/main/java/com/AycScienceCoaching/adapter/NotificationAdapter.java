package com.AycScienceCoaching.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.AycScienceCoaching.Constant.Constant;
import com.AycScienceCoaching.Model.NotificationModel;
import com.AycScienceCoaching.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    public FirebaseFirestore mFireBaseDB = FirebaseFirestore.getInstance();
    private OnNotificationClick onNotificationClick;
    private ArrayList<NotificationModel> notificationList;
    private static final String quizNotificationTitle = "Quiz is added for you.";
    private static final String quizNotificationDescription = "Quiz of the particular subject.";
    private static final String pdfNotificationTitle = "PDF is added for you.";
    private static final String pdfNotificationDescription = "Particular subjects PDF is added, please check it";
    private static final String videoNotificationTitle = "Video is added for you.";
    private static final String lectureNotificationTitle = "Lecture is added for you.";
    private static final String videoNotificationDescription = "particular subjects video is added, please check it.";

    public NotificationAdapter(@NonNull ArrayList<NotificationModel> options, OnNotificationClick onItemClick) {
        notificationList = options;
        this.onNotificationClick = onItemClick;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_notification_layout, parent, false);
        return new NotificationAdapter.NotificationViewHolder(view, onNotificationClick);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        NotificationModel model = notificationList.get(position);
        setNotificationItemData(holder, model);
    }

    private void setNotificationItemData(NotificationViewHolder holder, NotificationModel model) {
        if (model.getNotification_subject().equals(Constant.QUIZ_TYPE)) {
            holder.notificationTitle.setText(model.getEntity_title());
            holder.notificationDescription.setText(quizNotificationTitle);
        } else if (model.getNotification_subject().equals(Constant.PDF_TYPE)) {
            holder.notificationTitle.setText(model.getEntity_title());
            holder.notificationDescription.setText(pdfNotificationTitle);
        } else if (model.getNotification_subject().equals(Constant.VIDEO_TYPE)) {
            holder.notificationTitle.setText(model.getEntity_title());
            holder.notificationDescription.setText(videoNotificationTitle);
        } else if (model.getNotification_subject().equals(Constant.RECENT_LEECTURE_TYPE)) {
            holder.notificationTitle.setText(model.getEntity_title());
            holder.notificationDescription.setText(lectureNotificationTitle);
        }
        holder.notificationDate.setText(getDate(model.getNotification_time()));
    }

    private String getDate(long timeMillis) {
        DateFormat date = new SimpleDateFormat("dd MMM yyyy");
        Date result = new Date(timeMillis);
        return date.format(result);
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public class NotificationViewHolder extends RecyclerView.ViewHolder {
        private TextView notificationTitle;
        private TextView notificationDescription;
        private TextView notificationDate;
        OnNotificationClick onNotificationClick;

        public NotificationViewHolder(@NonNull View itemView, OnNotificationClick onClick) {
            super(itemView);
            notificationTitle = itemView.findViewById(R.id.notificationTitle);
            notificationDescription = itemView.findViewById(R.id.notificationDescription);
            notificationDate = itemView.findViewById(R.id.notificationDate);
            this.onNotificationClick = onClick;
            itemView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onClick != null) {
                    onClick.onItemClick(notificationList.get(position), position);
                }
            });

        }

    }

    public interface OnNotificationClick {
        void onItemClick(NotificationModel snapshot, int position);
    }


}
