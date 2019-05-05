package com.trackaty.chat.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Chat {

    private String key;
    private String lastMessage;
    private String sender;
    private Object lastSent ;
    private Long active;
    //private List<String> members = new ArrayList<>();
    private Map<String, User> members = new HashMap<>();


    public Chat() {

    }

    public Chat( String lastMessage, String sender, Map<String, User> members) {
        this.lastMessage = lastMessage;
        //this.members = members;
        this.members = members;
        this.sender = sender;
    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("lastMessage", lastMessage);
        //result.put("members", members);
        result.put("members", members);
        result.put("active", active);
        result.put("sender", sender);
        result.put("lastSent", ServerValue.TIMESTAMP);

        return result;
    }
// [END post_to_map]


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Object getLastSent() {
        return lastSent;
    }

    @Exclude
    public long getLastSentLong() {
        return (long) lastSent;
    }

    public void setLastSent(Object lastSent) {
        this.lastSent = lastSent;
    }

    public Long getActive() {
        return active;
    }

    public void setActive(Long active) {
        this.active = active;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    /* public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;

    }*/

    public Map<String, User> getMembers() {
        return members;
    }

    public void setMembers(Map<String, User> members) {
        this.members = members;
    }
}
