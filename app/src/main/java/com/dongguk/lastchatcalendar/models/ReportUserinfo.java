package com.dongguk.lastchatcalendar.models;

public class ReportUserinfo {
    private String title;
    private String write;
    private String id;

    public ReportUserinfo(String id, String title, String write){
        this.id = id;
        this.title = title;
        this.write = write;
    }
    
    public ReportUserinfo() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id){
        this.id = id;
    }

    public String getTitle(){
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWrite(){
        return write;
    }

    public void setWrite(String write) {
        this.write = write;
    }

}
