package com.alphaCoaching.Model;

public class PDFModel {
    private String PDFName;
    private String subject;
    private String id;
    private String url;

    public PDFModel() {

    }

    public PDFModel(String id, String PDFName, String subject) {
        this.id = id;
        this.PDFName = PDFName;
        this.subject = subject;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPDFName() {
        return PDFName;
    }

    public void setPDFName(String PDFName) {
        this.PDFName = PDFName;
    }

    public String getSubject() {

        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
