package com.example.helb_mobile1.main;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesManager {
    private final String DAILY_WORD_NAME = "DailyWord";
    private final String LAST_TIME_FETCHED_WORD = "lastFetchedWord";
    private final String PREF_FILE_NAME = "app_prefs";
    private final SharedPreferences sharedPreferences;

    public PreferencesManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
    }

    public void saveDailyWord(String word, long timestamp) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(DAILY_WORD_NAME, word);
        editor.putLong(LAST_TIME_FETCHED_WORD, timestamp);
        editor.apply();
    }

    public String getWord() {
        return sharedPreferences.getString(DAILY_WORD_NAME, null);
    }

    public long getLastFetchedWord() {
        return sharedPreferences.getLong(LAST_TIME_FETCHED_WORD, 0);
    }
}
