package com.alphaCoaching.Model;

public class VideoCategoryModel {
    private String uid;
    private String name;
    private String standard;
    private String subject;

    public String getSubject() {
        return subject;
    }

    public String getStandard() {
        return standard;
    }

    public String getName() {
        return name;
    }

    public String getUid() {
        return uid;
    }

    public void setStandard(String standard) {
        this.standard = standard;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
