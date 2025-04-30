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

import org.json.JSONException;


import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;


public class WordOfTheDayViewModel extends ViewModel {

    private final MutableLiveData<String> wordLiveData = new MutableLiveData<>();
    private final PreferencesManager prefs;
    private final ZonedDateTime NEW_WORD_TIME = ZonedDateTime.now(ZoneId.systemDefault()).withHour(8);
    private final ZoneId LOCATION = ZoneId.systemDefault();
    private final String API_URL = "https://trouve-mot.fr/api/daily";

    public WordOfTheDayViewModel(PreferencesManager prefs){
        this.prefs = prefs;
    }

    public void fetchWordIfNeeded(Context context) {
        String cachedWord = prefs.getWord();
        ZonedDateTime now = ZonedDateTime.now(LOCATION);
        long lastFetchMillis = prefs.getLastFetchedWord();

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
        String word = prefs.getWord();
        if (word != null) {
            wordLiveData.setValue(word);
        }
    }

    public LiveData<String> getWordLiveData() {
        return wordLiveData;
    }

    private void fetchWordAndSetPref(Context context){
// Volley request to fetch and set value
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


    /*TODO change all this so it doesn't stink of chatgpt
    this is optional, I don't need to do this but it'd be nice

     */
    /*
    public long getMillisUntilNext8AM() {
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime now = ZonedDateTime.now(zoneId);

        // Set today at 8 AM
        ZonedDateTime next8AM = now.withHour(8).withMinute(0).withSecond(0).withNano(0);

        // If it's already past 8 AM today, schedule for tomorrow
        if (now.isAfter(next8AM)) {
            next8AM = next8AM.plusDays(1);
        }

        return Duration.between(now, next8AM).toMillis();
    }

    public void startCountdownTo8AM() {
        long millisUntil8AM = getMillisUntilNext8AM();

        new CountDownTimer(millisUntil8AM, 1000) { // tick every second
            public void onTick(long millisUntilFinished) {
                long hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
                long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60;
                long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60;

                Log.d("Countdown", hours + "h " + minutes + "m " + seconds + "s");
                // You can also update a TextView here
            }

            public void onFinish() {
                Log.d("Countdown", "It's 8 AM!");
                // Trigger fetch or whatever action you need
            }
        }.start();
    }

     */


}
