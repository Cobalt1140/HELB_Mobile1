package com.example.helb_mobile1;

import com.google.firebase.database.DatabaseError;

public interface ICurrentTimeCallback {
    public void onTimeCheckFailed(String message);
    public void onWithinTimeWindow();
    public void onOutsideTimeWindow(int time);
}
