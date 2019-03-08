package com.trackaty.chat.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Message {

    private String key;
    private String message;
    private String sender;
    private String senderName;
    private Boolean seen;
    private Object created ;

    public Message() {
    }

    public Message(String message, String sender, String senderName, Boolean seen, Object created) {
        this.message = message;
        this.sender = sender;
        this.senderName = senderName;
        this.seen = seen;
        this.created = created;
    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("message", message);
        result.put("sender", sender);
        result.put("senderName", senderName);
        result.put("seen", seen);
        result.put("created", created);

        return result;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }


    public Object getCreated() {
        return created;
    }

    @Exclude
    public long getCreatedLong() {
        return (long) created;
    }

    public void setCreated(Object created) {
        this.created = created;
    }

    // [END post_to_map]
}
