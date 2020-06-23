package com.alphaCoaching.Model;

public class NotificationModel {

    private String id;
    private boolean notification_status;
    private String notification_subject;
    private Long notification_time;
    private String subject_url;
    private String user_id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean getNotification_status() {
        return notification_status;
    }

    public String getNotification_subject() {
        return notification_subject;
    }

    public Long getNotification_time() {
        return notification_time;
    }

    public String getSubject_url() {
        return subject_url;
    }

    public String getUser_id() {
        return user_id;
    }
}
