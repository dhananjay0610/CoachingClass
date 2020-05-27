package com.alphaCoaching.Model;
import java.util.Date;

public class RecentLecturesModel {
    private String chapterName;
    private String description;
    private Date lectureDate;
    private String subject;

    private RecentLecturesModel(String chapterName, String subject, String description, Date lectureDate) {

        this.chapterName = chapterName;
        this.description = description;
        this.lectureDate = lectureDate;
        this.subject = subject;
    }

    private RecentLecturesModel() {
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
