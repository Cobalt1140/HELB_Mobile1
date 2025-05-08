package com.example.helb_mobile1.main.dailyWord;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.helb_mobile1.managers.DatabaseManager;
import com.example.helb_mobile1.managers.db_callbacks.IDailyWordCallback;
import com.example.helb_mobile1.managers.PreferencesManager;
import com.example.helb_mobile1.managers.TimeConfig;


import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class DailyWordViewModel extends ViewModel {

    private final MutableLiveData<String> wordLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
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


    public void fetchWordIfNeeded(Context context) {
        //TODO redo this code, idk about zonedTime
        String cachedWord = prefs.getCachedWord();
        ZoneId Belgium = ZoneId.of("Europe/Brussels");
        ZonedDateTime newWordTime = ZonedDateTime.now(Belgium).withHour(TimeConfig.NEW_WORD_TIME_HOUR);
        ZonedDateTime now = ZonedDateTime.now(Belgium);
        long lastFetchMillis = prefs.getCachedWordTimestamp();

        if (cachedWord == null || cachedWord.isEmpty()) { //if prefs don't have anything
            fetchWordAndSetPref(context);
        } else if (now.isAfter(newWordTime)) { //if prefs have somt but it's new word time
            ZonedDateTime lastFetchTime = Instant.ofEpochMilli(lastFetchMillis).atZone(Belgium);
            if (lastFetchTime.isBefore(newWordTime)){ //last fetch was before new word Time
                fetchWordAndSetPref(context);
            }
        }
        setWordLiveDataFromPrefs();
    }

    private void setWordLiveDataFromPrefs(){
        String word = prefs.getCachedWord();
        if (word != null) {
            wordLiveData.setValue(word);
        }
    }
    //TODO Should I make this public? So that the user can manually refetch the word.
    private void fetchWordAndSetPref(Context context){
        DatabaseManager.getInstance().fetchAndHandleDailyWord(new IDailyWordCallback() {
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
