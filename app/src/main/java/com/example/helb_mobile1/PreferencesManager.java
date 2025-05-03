package com.example.helb_mobile1;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesManager {
    private final String CACHED_WORD_NAME = "DailyWord";
    private final String CACHED_WORD_TIMESTAMP = "cachedWordTimestamp";
    private final String PREF_FILE_NAME = "app_prefs";
    private final SharedPreferences sharedPreferences;

    public PreferencesManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
    }

    public void saveDailyWord(String word, long timestamp) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CACHED_WORD_NAME, word);
        editor.putLong(CACHED_WORD_TIMESTAMP, timestamp);
        editor.apply();
    }

    public String getCachedWord() {
        return sharedPreferences.getString(CACHED_WORD_NAME, null);
    }

    public long getLastFetchedWord() {
        return sharedPreferences.getLong(CACHED_WORD_TIMESTAMP, 0);
    }
}
