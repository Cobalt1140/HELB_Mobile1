package com.example.helb_mobile1.managers.db_callbacks;

public interface IDailyWordCallback {
    void onDailyWordFound(String word);
    void onError(String error);

}
