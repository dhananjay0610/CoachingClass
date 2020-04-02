package com.alphaCoaching.fragment;

public class ListItem {
    private String head;
    private  String description;
    private String date;

    public ListItem(String head, String description) {
        this.head = head;
        this.description = description;
        this.date=date;
    }


    public String getHead() {
        return head;
    }

    public String getDescription() {
        return description;
    }
    public String getDate(){
        return  date;
    }
}
