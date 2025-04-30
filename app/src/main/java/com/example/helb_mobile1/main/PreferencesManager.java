package com.example.helb_mobile1.main;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesManager {
    private final SharedPreferences sharedPreferences;

    public PreferencesManager(Context context) {
        sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
    }

    public void saveDailyWord(String word, long timestamp) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("DailyWord", word);
        editor.putLong("lastFetchedWord", timestamp);
        editor.apply();
    }

    public String getWord() {
        return sharedPreferences.getString("DailyWord", null);
    }

    public long getLastFetchedWord() {
        return sharedPreferences.getLong("lastFetchedWord", 0);
    }
}
