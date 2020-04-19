package com.alphaCoaching.Model;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Date;

public class recentLecturesModel  {
    private String chapterName;
    private String description;
    private Date lectureDate;
    private String subject;

    private recentLecturesModel(String chapterName, String subject, String description, Date lectureDate) {

        this.chapterName = chapterName;
        this.description = description;
        this.lectureDate = lectureDate;
        this.subject = subject;
    }

    private recentLecturesModel() {
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getLectureDate() {
        return lectureDate;
    }

    public void setLectureDate(Date lectureDate) {
        this.lectureDate = lectureDate;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

}
