package com.trackaty.chat.models;

import android.text.TextUtils;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@IgnoreExtraProperties
public class Message {

    private String key;
    private String message;
    private String senderId;
    private String senderName;
    private String senderAvatar;
    // message delivery status: non, Sent (message sent to server) , Delivered (recipient received a notification) , Seen (message was seen), Revealed (message was scratched)
    private String status; // To know if a message was seen before or not
    private boolean revealed; // To know if a message was revealed before or not
    private int percent; // reveal percent for recycler view. not used on the database
    private Object created ;

    public Message() {
    }

    public Message(String message, String senderId, String senderName,String senderAvatar , String status ,boolean revealed) {
        this.message = message;
        this.senderId = senderId;
        this.senderName = senderName;
        this.senderAvatar = senderAvatar;
        this.status = status;
        this.revealed = revealed;
        this.created = ServerValue.TIMESTAMP;
    }

    /*public Message(String message, String senderId, String senderName,String senderAvatar ,boolean revealed) {
        this.message = message;
        this.senderId = senderId;
        this.senderName = senderName;
        this.senderAvatar = senderAvatar;
        this.revealed = revealed;
        this.created = ServerValue.TIMESTAMP;
    }*/

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("message", message);
        result.put("senderId", senderId);
        result.put("senderName", senderName);
        result.put("senderAvatar", senderAvatar);
        result.put("status", status);
        result.put("revealed", revealed);
        result.put("created", ServerValue.TIMESTAMP);

        return result;
    }// [END post_to_map]

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

    @Exclude
    public int getPercent() {
        return percent;
    }

    @Exclude
    public void setPercent(int percent) {
        this.percent = percent;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isRevealed() {
        return revealed;
    }

    public void setRevealed(boolean revealed) {
        this.revealed = revealed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message1 = (Message) o;
        return revealed == message1.revealed &&
                //Objects.equals(message, message1.message) &&
                //Objects.equals(status, message1.status);

                //message == null ? message1.message == null : message.equals(message1.message) &&
                //status == null ? message1.status == null : status.equals(message1.status);
                TextUtils.equals(message, message1.message)  &&
                TextUtils.equals(status, message1.status);
    }

    @Override
    public int hashCode() {
        //return Objects.hash(message, status, revealed);
        int result = 1;
        /*result = 31 * result + revealed.hashCode();
        result = 31 * result + message.hashCode();
        result = 31 * result + status.hashCode();*/

        result = 31 * result + (revealed ? 1 : 0);
        result = 31 * result + (message == null ? 0 : message.hashCode());
        result = 31 * result + (status == null ? 0 : status.hashCode());
        return result;
    }
}
