//package com.dongguk.lastchatcalendar.utilities;
//
//import java.io.Serializable;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//
//public class Writeinfo implements Serializable {
//    private String title;
//    private String write;
//    private ArrayList<String> contents;
//    private ArrayList<String> formats;
//    private String publisher;
//    private Date createAt;
//    private String id;
//
//    public Writeinfo(String title, ArrayList<String> contents, ArrayList<String> formats, String publisher, Date createAt, String id){
//        this.title = title;
//        this.contents = contents;
//        this.formats = formats;
//        this.publisher = publisher;
//        this.createAt = createAt;
//        this.id = id;
//    }
//
//    public Writeinfo(String title, ArrayList<String> contents, ArrayList<String> formats, String publisher, Date createAt){
//        this.title = title;
//        this.contents = contents;
//        this.formats = formats;
//        this.publisher = publisher;
//        this.createAt = createAt;
//    }
//
//    public Writeinfo(String title, String write, String id) {
//        this.title = title;
//        this.write = write;
//        this.id = id;
//    }
//
//    public Map<String, Object> getWriteinfo(){
//        Map<String, Object> docData = new HashMap<>();
//        docData.put("title",title);
//        docData.put("contents",contents);
//        docData.put("formats",formats);
//        docData.put("publisher",publisher);
//        docData.put("createAt",createAt);
//        return docData;
//    }
//
//    public String getWrite() { return this.write; }
//    public void setWrite(String write){ this.write = write; }
//
//    public String getTitle() { return this.title; }
//    public void setTitle(String title){ this.title = title; }
//
//    public ArrayList<String> getContents() {
//        return this.contents;
//    }
//    public void setContents(ArrayList<String> contents){
//        this.contents = contents;
//    }
//
//    public ArrayList<String> getFormats() {
//        return this.formats;
//    }
//    public void setFormats(ArrayList<String> formats){
//        this.formats = formats;
//    }
//
//    public String getPublisher() {
//        return this.publisher;
//    }
//    public void setPublisher(String publisher){
//        this.publisher = publisher;
//    }
//
//    public Date getCreateAt() { return this.createAt; }
//    public void setCreateAt(Date createAt){ this.createAt = createAt; }
//
//    public String getId() { return this.id; }
//    public void setId(String id){ this.id = id; }
//}
