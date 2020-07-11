package com.google.sps.servlets;

import com.google.appengine.api.datastore.Entity;

public class Comment {
    private final String text;
    private final String translatedText;
    private final String user;
    private final long timestamp;
    private final int upvotes;
    private final int downvotes;
    private final long key;

    // Constructor to use when comment is created
    public Comment(String text, String translatedText, String user, long timestamp){
        this(text, translatedText, user, timestamp, 0, 0, 0);
    }

    // Constructor to use when comment is imported from Datastore
    public Comment(Entity entity){
        this(
            (String)entity.getProperty("comment"),
            (String)entity.getProperty("translatedComment"), 
            (String)entity.getProperty("name"), 
            (long)entity.getProperty("timestamp"),
            ((Long)entity.getProperty("upvotes")).intValue(), 
            ((Long)entity.getProperty("downvotes")).intValue() ,
            entity.getKey().getId());
    }

    public Comment(String text, String translatedText, String user, long timestamp, int upvotes, int downvotes, long key) {
        this.text = text;
        this.user = user;
        this.timestamp = timestamp;
        this.upvotes = upvotes;
        this.downvotes = downvotes;
        this.key = key;
        this.translatedText = translatedText;
    }

    public String getText() {
        return this.text;
    }

    public String getTranslatedText() {
        return this.translatedText;
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

    public int getDownvotes() {
        return this.downvotes;
    }

    public long getKey() {
        return this.key;
    }
}
