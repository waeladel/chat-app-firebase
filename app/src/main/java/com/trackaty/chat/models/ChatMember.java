package com.trackaty.chat.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@IgnoreExtraProperties
public class ChatMember  {


    private String key;
    private Long lastOnline;
    private String avatar;
    private String name;
    private boolean saw;

    public ChatMember() {
    }

    public ChatMember(String key, String name, String avatar, Long lastOnline, boolean saw) {
        this.key = key;
        this.name = name;
        this.avatar = avatar;
        this.lastOnline = lastOnline;
        this.saw = saw;
    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("lastOnline", lastOnline);
        result.put("avatar", avatar);
        result.put("name", name);
        result.put("saw", saw);

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

    public boolean isSaw() {
        return saw;
    }

    public void setSaw(boolean saw) {
        this.saw = saw;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatMember that = (ChatMember) o;
        return saw == that.saw &&
                //TextUtils.equals(key, that.key) &&
                TextUtils.equals(avatar, that.avatar) &&
                TextUtils.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        //return Objects.hash(key, avatar, name, saw);
        int result = 1;
        result = 31 * result + (saw ? 1 : 0);
        //result = 31 * result + (key == null ? 0 : key.hashCode());
        result = 31 * result + (avatar == null ? 0 : avatar.hashCode());
        result = 31 * result + (name == null ? 0 : name.hashCode());
        return result;
    }
}
// [END blog_user_class]