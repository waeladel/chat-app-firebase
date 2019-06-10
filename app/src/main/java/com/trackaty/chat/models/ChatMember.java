package com.trackaty.chat.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class ChatMember  {


    private String key;
    private Long lastOnline;
    private String avatar;
    private String name;


    public ChatMember() {
    }

    public ChatMember(String key, String name, String avatar, Long lastOnline) {
        this.key = key;
        this.name = name;
        this.avatar = avatar;
        this.lastOnline = lastOnline;
    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("lastOnline", lastOnline);
        result.put("avatar", avatar);
        result.put("name", name);

        return result;
    }
    // [END post_to_map]

    @Exclude
    public String getKey() { return key; }
    @Exclude
    public void setKey(String key) { this.key = key; }

    public Long getLastOnline() {
        return lastOnline;
    }

    public void setLastOnline(Long lastOnline) {
        this.lastOnline = lastOnline;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
// [END blog_user_class]