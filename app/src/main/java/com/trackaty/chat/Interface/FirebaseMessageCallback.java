package com.trackaty.chat.Interface;

import com.trackaty.chat.models.Message;
import com.trackaty.chat.models.User;

// A call back triggered when database sends the required message result
public interface FirebaseMessageCallback {
    void onCallback(Message message);
}
