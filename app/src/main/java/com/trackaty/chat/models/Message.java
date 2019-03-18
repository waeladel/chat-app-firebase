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
    private String senderId;
    private String senderName;
    private String senderAvatar;
    private Boolean seen;
    private Object created ;

    public Message() {
    }

    public Message(String message, String senderId, String senderName,String senderAvatar, Boolean seen) {
        this.message = message;
        this.senderId = senderId;
        this.senderName = senderName;
        this.senderAvatar = senderAvatar;
        this.seen = seen;
        //this.created = created;
    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("message", message);
        result.put("senderId", senderId);
        result.put("senderName", senderName);
        result.put("senderAvatar", senderAvatar);
        result.put("seen", seen);
        result.put("created", ServerValue.TIMESTAMP);

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

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
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

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderAvatar() {
        return senderAvatar;
    }

    public void setSenderAvatar(String senderAvatar) {
        this.senderAvatar = senderAvatar;
    }

    // [END post_to_map]
}
