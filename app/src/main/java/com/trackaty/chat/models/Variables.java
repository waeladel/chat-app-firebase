package com.trackaty.chat.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Variables implements Parcelable {

    private Boolean value;

    public Variables(Boolean value) {
        this.value = value;
    }

    protected Variables(Parcel in) {
        byte tmpValue = in.readByte();
        value = tmpValue == 0 ? null : tmpValue == 1;
    }

    public static final Creator<Variables> CREATOR = new Creator<Variables>() {
        @Override
        public Variables createFromParcel(Parcel in) {
            return new Variables(in);
        }

        @Override
        public Variables[] newArray(int size) {
            return new Variables[size];
        }
    };

    public Boolean getValue() {
        return value;
    }

    public void setValue(Boolean value) {
        this.value = value;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (value == null ? 0 : value ? 1 : 2));
    }
}
