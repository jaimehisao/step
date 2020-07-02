package com.google.sps.servlets;

import com.google.appengine.api.datastore.Entity;

public class Comment {
    private final String text;
    private final String user;
    private final long timestamp;
    private int upvotes;
    private int downvotes;
    private final long key;

    // Constructor to use when comment is created
    public Comment(String text, String user, long timestamp){
        this(text, user, timestamp, 0, 0, 0);
    }

    // Constructor to use when comment is imported from Datastore
    public Comment(Entity entity){
        this(
            (String)entity.getProperty("text"), 
            (String)entity.getProperty("user"), 
            (long)entity.getProperty("timestamp"), 
            (Integer)entity.getProperty("upvotes"), 
            (Integer)entity.getProperty("downvotes") ,
            entity.getKey().getId());
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
}
