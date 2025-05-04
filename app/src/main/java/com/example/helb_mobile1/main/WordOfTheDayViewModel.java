package com.example.helb_mobile1.main;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.helb_mobile1.PreferencesManager;
import com.example.helb_mobile1.TimeConfig;

import org.json.JSONException;


import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class WordOfTheDayViewModel extends ViewModel {

    private final MutableLiveData<String> wordLiveData = new MutableLiveData<>();
    private final ZonedDateTime NEW_WORD_TIME = ZonedDateTime.now(ZoneId.systemDefault()).withHour(TimeConfig.NEW_WORD_TIME_HOUR);
    private final ZoneId LOCATION = ZoneId.systemDefault();
    private final String API_URL = "https://trouve-mot.fr/api/daily";
    private final PreferencesManager prefs;

    public WordOfTheDayViewModel(PreferencesManager prefs){
        this.prefs = prefs;
    }

    public void fetchWordIfNeeded(Context context) {
        String cachedWord = prefs.getCachedWord();
        ZonedDateTime now = ZonedDateTime.now(LOCATION);
        long lastFetchMillis = prefs.getLastTimeFetchedCachedWord();
        //TODO change how looking at the timing works

        if (cachedWord == null || cachedWord.isEmpty()) { //if prefs don't have anything
            fetchWordAndSetPref(context);
        } else if (now.isAfter(NEW_WORD_TIME)) { //if prefs have somt but it's new word time and last fetch was before new word Time
            ZonedDateTime lastFetchTime = Instant.ofEpochMilli(lastFetchMillis).atZone(LOCATION);
            if (lastFetchTime.isBefore(NEW_WORD_TIME)){
                fetchWordAndSetPref(context);
            }
        }
        setWordFromPrefs();
    }

    private void setWordFromPrefs(){
        String word = prefs.getCachedWord();
        if (word != null) {
            wordLiveData.setValue(word);
        }
    }

    public LiveData<String> getWordLiveData() {
        return wordLiveData;
    }

    private void fetchWordAndSetPref(Context context){
        //TODO change fetch to DB
        //change the logic from system time

        RequestQueue queue = Volley.newRequestQueue(context);
        Toast.makeText(context, "New Request", Toast.LENGTH_SHORT).show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, API_URL, null,
                response -> {
                    try {
                        prefs.saveDailyWord(response.getString("name"), System.currentTimeMillis());
                        setWordFromPrefs();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Log.e("Volley", "Error: " + error.getMessage());
                }
        );
        queue.add(jsonObjectRequest);
    }
}
