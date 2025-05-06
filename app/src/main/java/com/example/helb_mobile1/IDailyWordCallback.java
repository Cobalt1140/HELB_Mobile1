package com.example.helb_mobile1;

import com.google.firebase.database.DatabaseError;

import java.util.Map;

public interface IDailyWordCallback {
    void onDailyWordFound(String word);
    void onError(String error);

}
