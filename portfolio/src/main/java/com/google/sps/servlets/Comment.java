package com.google.sps.servlets;

import com.google.appengine.api.datastore.Entity;

public class Comment {
    private final String text;
    private final String user;
    private final long timestamp;
    private int upvotes;
    private int downvotes;
    private long key;

    //Constructor to use when comment is created
    public Comment(String text, String user, long timestamp){
        this.text = text;
        this.user = user;
        this.timestamp = timestamp;
        this.upvotes = 0;
        this.downvotes = 0;
    }

    //Constructor to use when comment is imported from Datastore
    public Comment(Entity entity){
        this.key = entity.getKey().getId();
        this.text = (String)entity.getProperty("text");
        this.user = (String)entity.getProperty("user");
        this.timestamp = (long)entity.getProperty("timestamp");
        this.upvotes = (Integer)entity.getProperty("upvotes");
        this.downvotes = (Integer)entity.getProperty("downvotes");
    }

    public Comment(String text, String user, long timestamp, int upvotes, int downvotes, long key) {
        this.text = text;
        this.user = user;
        this.timestamp = timestamp;
        this.upvotes = upvotes;
        this.downvotes = downvotes;
        this.key = key;
    }

    public String getText() {
        return this.text;
    }


    public String getUser() {
        return this.user;
    }


    public long getTimestamp() {
        return this.timestamp;
    }


    public int getUpvotes() {
        return this.upvotes;
    }

    public void setUpvotes(int upvotes) {
        this.upvotes = upvotes;
    }

    public int getDownvotes() {
        return this.downvotes;
    }

    public void setDownvotes(int downvotes) {
        this.downvotes = downvotes;
    }

    public long getKey() {
        return this.key;
    }

    public void setKey(long key) {
        this.key = key;
    }

    public Comment upvotes(int upvotes) {
        this.upvotes = upvotes;
        return this;
    }

    public Comment downvotes(int downvotes) {
        this.downvotes = downvotes;
        return this;
    }

    public Comment key(long key) {
        this.key = key;
        return this;
    }

}
