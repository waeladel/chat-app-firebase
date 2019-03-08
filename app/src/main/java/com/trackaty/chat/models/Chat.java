package com.trackaty.chat.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Chat {

    private String key;
    private String sender;
    private String receiver;
    private String lastMessage;
    private Object lastSent ;

    public Chat() {

    }

    public Chat( String sender, String receiver, String lastMessage) {
        this.sender = sender;
        this.receiver = receiver;
        this.lastMessage = lastMessage;
    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(sender, true);
        result.put(receiver, true);
        result.put("lastMessage", lastMessage);
        result.put("lastSent", ServerValue.TIMESTAMP);

        return result;
    }
// [END post_to_map]
}
