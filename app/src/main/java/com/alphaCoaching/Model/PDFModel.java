package com.alphaCoaching.Model;

public class PDFModel {
    private String PDFName;
    private String subject;

    public PDFModel() {

    }

    public PDFModel(String PDFName, String subject) {
        this.PDFName = PDFName;
        this.subject = subject;
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
