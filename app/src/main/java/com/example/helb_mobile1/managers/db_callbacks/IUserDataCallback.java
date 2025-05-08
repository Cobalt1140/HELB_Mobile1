package com.example.helb_mobile1.managers.db_callbacks;

public interface IUserDataCallback {
    void onUserDataReceived(String username, long points);
    void onError(String error);
}
