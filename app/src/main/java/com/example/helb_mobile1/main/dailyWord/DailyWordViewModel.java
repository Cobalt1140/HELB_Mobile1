package com.example.helb_mobile1.main.dailyWord;

import android.content.Context;
import android.os.CountDownTimer;

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
    /*
    ViewModel for DailyWordFragment, handles data related to the Daily Word tab
     */

    private final MutableLiveData<String> wordLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> countdownLiveData = new MutableLiveData<>();
    private final PreferencesManager prefs;
    private CountDownTimer countDownTimer;

    public DailyWordViewModel(Context context){
        this.prefs = PreferencesManager.getInstance(context.getApplicationContext());
    }

    public LiveData<String> getErrorLiveData(){
        return errorLiveData;
    }

    public LiveData<String> getWordLiveData() {
        return wordLiveData;
    }
    public LiveData<String> getCountdownLiveData(){
        return countdownLiveData;
    }


    public void setCountdownAdapted(){
        /*
        determines to what time the countdown should count down to (New Word Time or Results Time)
        If in submition time, count down to Results
        If after submition time or before new word has arrived, count down to New Word
         */

        ZonedDateTime now = ZonedDateTime.now(ZoneId.of(TimeConfig.SERVER_TIMEZONE));
        ZonedDateTime newWordTime = ZonedDateTime.now(ZoneId.of(TimeConfig.SERVER_TIMEZONE)).withHour(TimeConfig.NEW_WORD_TIME_HOUR)
                .withMinute(0).withSecond(0).withNano(0);
        ZonedDateTime resultsTime = ZonedDateTime.now(ZoneId.of(TimeConfig.SERVER_TIMEZONE)).withHour(TimeConfig.PUBLISH_TIME_HOUR)
                .withMinute(0).withSecond(0).withNano(0);
        if (now.isAfter(newWordTime) && now.isBefore(resultsTime)){
            startCountdownToTargetHour(TimeConfig.PUBLISH_TIME_HOUR, "Temps jusqu'à fin des submissions: ");
        } else {
            startCountdownToTargetHour(TimeConfig.NEW_WORD_TIME_HOUR, "Temps jusqu'au nouveau mot: ");
        }
    }




    public void fetchWordIfNeeded() {
        /*
        determines if it's necessary to fetch the daily word from the DB
        otherwise, gets the cached word from SharedPreferences
         */
        String cachedWord = prefs.getCachedWord();
        ZoneId Belgium = ZoneId.of(TimeConfig.SERVER_TIMEZONE);
        ZonedDateTime newWordTime = ZonedDateTime.now(Belgium).withHour(TimeConfig.NEW_WORD_TIME_HOUR)
                .withMinute(0).withSecond(0).withNano(0);
        ZonedDateTime now = ZonedDateTime.now(Belgium);
        long lastFetchMillis = prefs.getCachedWordTimestamp();

        if (cachedWord == null || cachedWord.isEmpty()) { //if prefs don't have anything
            fetchWordAndSetPref();
        } else if (now.isAfter(newWordTime)) { //if prefs have somt but it's new word time
            ZonedDateTime lastFetchTime = Instant.ofEpochMilli(lastFetchMillis).atZone(Belgium);
            if (lastFetchTime.isBefore(newWordTime)){ //last fetch was before new word Time
                fetchWordAndSetPref();
            }
        }
        setWordLiveDataFromPrefs();
    }




    private void setWordLiveDataFromPrefs(){
        //sets the Daily Word into cache (Shared Preferences)
        String word = prefs.getCachedWord();
        if (word != null) {
            wordLiveData.setValue(word);
        }
    }
    private void startCountdownToTargetHour(int hour, String typeOfCountdown) {
        /*
        Sets up a Countdown Timer to the given hour and sets the LiveData to what the Countdown View should display
         */
        if (countDownTimer != null) countDownTimer.cancel();

        ZoneId belgium = ZoneId.of(TimeConfig.SERVER_TIMEZONE);
        ZonedDateTime now = ZonedDateTime.now(belgium);
        ZonedDateTime target = now.withHour(hour).withMinute(0)
                .withSecond(0).withNano(0);

        if (now.isAfter(target)) {
            target = target.plusDays(1); // Move to next day if time passed
        }

        long millisUntilTarget = java.time.Duration.between(now, target).toMillis();

        countDownTimer = new CountDownTimer(millisUntilTarget, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long hours = millisUntilFinished / (1000 * 60 * 60);
                long minutes = (millisUntilFinished / (1000 * 60)) % 60;
                long seconds = (millisUntilFinished / 1000) % 60;
                String formatted = String.format(typeOfCountdown +"\n%02d:%02d:%02d", hours, minutes, seconds);
                countdownLiveData.postValue(formatted); //uses postValue instead of setValue as this value is updated asynchronously with countdownTimer
            }

            @Override
            public void onFinish() {
                countdownLiveData.postValue("00:00:00");
                setCountdownAdapted();
                /*
                 retriggers setCountdownAdapted so that the timer isn't stuck on 00:00:00
                 and continues to count down to the next phase (Results -> New Word -> Results etc)
                 */
            }
        };

        countDownTimer.start();
    }

    private void fetchWordAndSetPref(){
        /*
        fetches the word from the DB and sets is as the new cached word in SharedPreferences
         */
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
