package com.alphaCoaching.Model;

public class VideoModel {
    private String id;
    private String category;
    private String name;
    private String standard;
    private String subject;
    private String url;

    public VideoModel() {

    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStandard() {
        return standard;
    }

    public String getCategory() {
        return category;
    }

    public String getSubject() {
        return subject;
    }

    public String getUrl() {
        return url;
    }
}
