package com.trackaty.chat.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Social implements Parcelable {

    private String key;
    private SocialObj value ;
    private int order;
    private int section;

    public Social() {

    }

    public Social(String key, SocialObj value, int order, int section) {
        this.key = key;
        this.value = value;
        this.order = order;
        this.section = section;
    }


    protected Social(Parcel in) {
        key = in.readString();
        order = in.readInt();
        section = in.readInt();
    }

    public static final Creator<Social> CREATOR = new Creator<Social>() {
        @Override
        public Social createFromParcel(Parcel in) {
            return new Social(in);
        }

        @Override
        public Social[] newArray(int size) {
            return new Social[size];
        }
    };

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public SocialObj getValue() {
        return value;
    }

    public void setValue(SocialObj value) {
        this.value = value;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getSection() {
        return section;
    }

    public void setSection(int section) {
        this.section = section;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
        dest.writeInt(order);
        dest.writeInt(section);
    }
}
