package com.example.helb_mobile1;

import com.google.firebase.database.DatabaseError;

public interface IUserDataCallback {
    void onUserDataReceived(String username, long points);
    void onError(String error);
}
