package com.trackaty.chat.models;

import com.google.firebase.database.Exclude;

import androidx.annotation.NonNull;

/**
 * Created by hp on 27/03/2018.
 */

public class UserId {

    @Exclude
    public String UserId;

    public <T extends UserId> T withId(@NonNull final String id) {
        this.UserId = id;
        return (T) this;
    }
}
