package com.trackaty.chat.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Social implements Parcelable {

    private String key;
    private String value;
    private String isPublic;
    private int order;
    private int section;

    public Social() {

    }

    public Social(String key, String value, String isPublic, int order, int section) {
        this.key = key;
        this.value = value;
        this.isPublic = isPublic;
        this.order = order;
        this.section = section;
    }

    protected Social(Parcel in) {
        key = in.readString();
        value = in.readString();
        isPublic = in.readString();
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(String isPublic) {
        this.isPublic = isPublic;
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
        dest.writeString(value);
        dest.writeString(isPublic);
        dest.writeInt(order);
        dest.writeInt(section);
    }
}
