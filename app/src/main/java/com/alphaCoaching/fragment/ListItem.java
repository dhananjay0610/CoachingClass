package com.alphaCoaching.fragment;

public class ListItem {
    public static final int TEXT_TYPE = 0;
    public static final int IMAGE_TYPE = 0;
   //private int viewType;

    private String head;
    private  String desc;
    private String date;

    public ListItem(String head, String desc,String date) {
     // this.viewType=viewType;
        this.head = head;
        this.desc = desc;
        this.date=date;
    }
    public String getHead() {
        return head;
    }

    public String getDescription() {
        return desc;
    }
    public String getDate(){
        return  date;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public void setDescription(String description) {
        this.desc = description;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
