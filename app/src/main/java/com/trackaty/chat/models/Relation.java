package com.trackaty.chat.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Relation {


    private Object created;
    private String status;

    private Map<String, Boolean> contacts = new HashMap<>();

    // startedAt: firebase.database.ServerValue.TIMESTAMP
    //private Date joined;// annotation to put server timestamp

    public Relation() {
        created = ServerValue.TIMESTAMP;
    }

    public Relation(String status, Map<String, Boolean> contacts) {
        this.status = status;
        this.contacts = contacts;
        this.created = ServerValue.TIMESTAMP;
    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("created", created);
        result.put("status", status );
        result.put("contacts", contacts);

        return result;
    }
    // [END post_to_map]



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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Map<String, Boolean> getContacts() {
        return contacts;
    }

    public void setContacts(Map<String, Boolean> contacts) {
        this.contacts = contacts;
    }
}
// [END blog_user_class]