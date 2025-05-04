package com.example.helb_mobile1;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesManager {
    private final String CACHED_WORD_NAME = "dailyWord";
    private final String CACHED_WORD_TIMESTAMP = "cachedWordTimestamp";
    private final String CACHED_USERNAME_NAME = "username";
    private final String CACHED_POINT_TOTAL_NAME = "pointTotal";
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

    //TODO add a cached username to avoid fetching the username from db each time
    public void saveUsername(String username){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CACHED_USERNAME_NAME, username);
        editor.apply();
    }

    public String getCachedUsername(){
        return sharedPreferences.getString(CACHED_USERNAME_NAME, "");
    }

    //TODO add a cached Point Total to avoid fetching it from db each time

    public String getCachedWord() {
        return sharedPreferences.getString(CACHED_WORD_NAME, null);
    }

    public long getLastTimeFetchedCachedWord() {
        return sharedPreferences.getLong(CACHED_WORD_TIMESTAMP, 0);
    }
}
