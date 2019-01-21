package com.trackaty.chat.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Profile implements Parcelable {

    private String key;
    private String value;
    private int section;

    public Profile() {

    }

    public Profile(String key, String value, int section) {
        this.key = key;
        this.value = value;
        this.section = section;
    }

    protected Profile(Parcel in) {
        key = in.readString();
        value = in.readString();
        section = in.readInt();
    }

    public static final Creator<Profile> CREATOR = new Creator<Profile>() {
        @Override
        public Profile createFromParcel(Parcel in) {
            return new Profile(in);
        }

        @Override
        public Profile[] newArray(int size) {
            return new Profile[size];
        }
    };

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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
        dest.writeString(value);
        dest.writeInt(section);
    }
}
