package com.trackaty.chat.Interface;

import com.trackaty.chat.models.Chat;

import java.util.List;

public interface FirebaseChatsCallback {
    Boolean onCallback(List<Chat> chatsList);
}
