package com.example.helb_mobile1.managers;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesManager {
    /*
    manager for Shared Preferences
    Class suggested by ChatGPT
     */
    private final String CACHED_WORD_NAME = "cachedWord";
    private final String CACHED_WORD_TIMESTAMP = "cachedWordTimestamp";
    private final String CACHED_USERNAME_NAME = "cachedUsername";
    private final String CACHED_POINT_TOTAL_NAME = "cachedPointTotal";
    private final String CACHED_MARKER_LAT_NAME = "cachedMarkerLat";
    private final String CACHED_MARKER_LNG_NAME = "cachedMarkerLng";
    //the names under which these values are stored in the Shared Preferences file
    private final String PREF_FILE_NAME = "app_prefs";
    private final SharedPreferences sharedPreferences; //singleton

    private static PreferencesManager instance;

    private PreferencesManager(Context context) {
        //PreferencesManager can only be accessed through getInstance, as it is a singleton
        sharedPreferences = context.getApplicationContext().getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
    }


    public static synchronized PreferencesManager getInstance(Context context) {
        if (instance == null) {
            instance = new PreferencesManager(context);
        }
        return instance;
    }

    public void saveDailyWordInCache(String word, long timestamp) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CACHED_WORD_NAME, word);
        editor.putLong(CACHED_WORD_TIMESTAMP, timestamp);
        editor.apply();
    }

    public void savePersonalMarkerInCache(Double lat, Double lng){
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putLong(CACHED_MARKER_LAT_NAME, Double.doubleToRawLongBits(lat));
        editor.putLong(CACHED_MARKER_LNG_NAME, Double.doubleToRawLongBits(lng));
        editor.apply();
    }

    public void resetPersonalMarkerInCache(){
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putLong(CACHED_MARKER_LAT_NAME, Double.doubleToRawLongBits(0));
        editor.putLong(CACHED_MARKER_LNG_NAME, Double.doubleToRawLongBits(0));
        editor.apply();
    }

    public double getCachedPersonalMarkerLat(){
        //Double isn't supported as a value type that can be stored in SharedPreferences,
        // however we can store it as a long as it is the same size, we just have to convert it
        //Code suggested by ChatGPT
        return  Double.longBitsToDouble(sharedPreferences.getLong(CACHED_MARKER_LAT_NAME, Double.doubleToLongBits(0)));
    }



    public double getCachedPersonalMarkerLng(){
        //Double isn't supported as a value type that can be stored in SharedPreferences,
        // however we can store it as a long as it is the same size, we just have to convert it
        //Code Suggested by ChatGPT
        return Double.longBitsToDouble(sharedPreferences.getLong(CACHED_MARKER_LNG_NAME, Double.doubleToLongBits(0)));
    }

    public void saveUsernameInCache(String username){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CACHED_USERNAME_NAME, username);
        editor.apply();
    }

    public String getCachedUsername(){
        return sharedPreferences.getString(CACHED_USERNAME_NAME, "");
    }


    public void savePointTotalInCache(long points){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(CACHED_POINT_TOTAL_NAME, points);
        editor.apply();
    }

    public long getCachedPointTotal(){
        return sharedPreferences.getLong(CACHED_POINT_TOTAL_NAME, 0);
    }

    public String getCachedWord() {
        return sharedPreferences.getString(CACHED_WORD_NAME, null);
    }

    public long getCachedWordTimestamp() {
        return sharedPreferences.getLong(CACHED_WORD_TIMESTAMP, 0);
    }
}
