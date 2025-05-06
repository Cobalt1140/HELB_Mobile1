package com.example.helb_mobile1.main.dailyWord;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.helb_mobile1.DatabaseManager;
import com.example.helb_mobile1.IDailyWordCallback;
import com.example.helb_mobile1.PreferencesManager;
import com.example.helb_mobile1.TimeConfig;


import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DailyWordViewModel extends ViewModel {

    private final MutableLiveData<String> wordLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final ZoneId LOCATION = ZoneId.of("Europe/Brussels");
    private final ZonedDateTime NEW_WORD_TIME = ZonedDateTime.now(LOCATION).withHour(TimeConfig.NEW_WORD_TIME_HOUR);
    private final PreferencesManager prefs;

    public DailyWordViewModel(Context context){
        this.prefs = PreferencesManager.getInstance(context.getApplicationContext());
    }

    public LiveData<String> getErrorLiveData(){
        return errorLiveData;
    }

    public LiveData<String> getWordLiveData() {
        return wordLiveData;
    }

    private void setWordLiveDataFromPrefs(){
        String word = prefs.getCachedWord();
        if (word != null) {
            wordLiveData.setValue(word);
        }
    }


    public void fetchWordIfNeeded(Context context) {
        String cachedWord = prefs.getCachedWord();
        ZonedDateTime now = ZonedDateTime.now(LOCATION);
        long lastFetchMillis = prefs.getCachedWordTimestamp();


        if (cachedWord == null || cachedWord.isEmpty()) { //if prefs don't have anything
            fetchWordAndSetPref(context);
        } else if (now.isAfter(NEW_WORD_TIME)) { //if prefs have somt but it's new word time
            ZonedDateTime lastFetchTime = Instant.ofEpochMilli(lastFetchMillis).atZone(LOCATION);
            if (lastFetchTime.isBefore(NEW_WORD_TIME)){ //last fetch was before new word Time
                fetchWordAndSetPref(context);
            }
        }
        setWordLiveDataFromPrefs();
    }

    private void fetchWordAndSetPref(Context context){

        ZoneId zoneId = ZoneId.of("Europe/Brussels");
        ZonedDateTime now = ZonedDateTime.now(zoneId);


        if (now.getHour() < TimeConfig.NEW_WORD_TIME_HOUR) {
            now = now.minusDays(1);
        }

        String targetDate = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        DatabaseManager.getInstance().fetchAndHandleDailyWord(targetDate, new IDailyWordCallback() {
            @Override
            public void onDailyWordFound(String word) {
                prefs.saveDailyWordInCache(word, System.currentTimeMillis());
                errorLiveData.setValue("Fetched Daily Word: "+word);
            }

            @Override
            public void onError(String error) {
                errorLiveData.setValue(error);
            }
        });
    }
}
