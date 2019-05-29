package com.trackaty.chat.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class DatabaseNotification {

    private String key;
    //private String title;
    //private String message;
    private String type;
    private String senderId;
    private String chatId;
    //private String senderName;
    //private String senderAvatar;
    private boolean seen;
    private Object created ;

    public DatabaseNotification() {
    }

    /*public DatabaseNotification(String title, String message, String type, String senderId, String senderName, String senderAvatar) {
        this.title = title;
        this.message = message;
        this.type = type;
        this.senderId = senderId;
        this.senderName = senderName;
        this.senderAvatar = senderAvatar;
        //this.created = created;
    }*/

    public DatabaseNotification(String type, String senderId) {
        //this.title = title;
        //this.message = message;
        this.type = type;
        this.senderId = senderId;
        this.created = ServerValue.TIMESTAMP;
    }

    public DatabaseNotification(String type, String senderId, String chatId) {
        //this.title = title;
        //this.message = message;
        this.type = type;
        this.senderId = senderId;
        this.chatId = chatId;
        this.created = ServerValue.TIMESTAMP;
    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        //result.put("title", title);
        //result.put("message", message);
        result.put("type", type);
        result.put("senderId", senderId);
        result.put("chatId", chatId);
        /*result.put("senderName", senderName);
        result.put("senderAvatar", senderAvatar);*/
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

    /*public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }*/

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    /*public String getSenderName() {
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
    }*/

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    // [END post_to_map]
}
