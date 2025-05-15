package com.example.helb_mobile1.managers.db_callbacks;

public interface ICurrentTimeCallback {
    public void onTimeCheckFailed(String message);
    public void onWithinTimeWindow(int time);
    public void onOutsideTimeWindow(int time);
}
