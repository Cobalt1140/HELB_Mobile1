package com.example.helb_mobile1.main.leaderboard;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.helb_mobile1.managers.DatabaseManager;
import com.example.helb_mobile1.managers.db_callbacks.ILeaderboardCallback;
import com.example.helb_mobile1.models.UserScore;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardViewModel extends ViewModel {
    private final MutableLiveData<List<UserScore>> globalList = new MutableLiveData<>();
    private final MutableLiveData<List<UserScore>> dailyList = new MutableLiveData<>();

    public LeaderboardViewModel(Context context){
        setEmptyDailyList();
        setEmptyGlobalList();
    }

    public void setEmptyGlobalList(){
        globalList.setValue(new ArrayList<UserScore>());
    }
    public void setEmptyDailyList(){
        dailyList.setValue(new ArrayList<UserScore>());
    }

    public LiveData<List<UserScore>> getGlobalList(){
        return globalList;
    }

    public LiveData<List<UserScore>> getDailyList(){
        return dailyList;
    }

    public void fetchLeaderboards() {
        /*
        fetches user Data from DB for both the Daily Points ordered List and Global Points ordered List
         */
        DatabaseManager.getInstance().fetchAndHandleLeaderboard(false,new ILeaderboardCallback() {
            @Override
            public void onSuccess(List<UserScore> scores) {
                globalList.setValue(scores);

            }
            @Override
            public void onError(String error) {

            }
        });

        DatabaseManager.getInstance().fetchAndHandleLeaderboard(true,new ILeaderboardCallback() {
            @Override
            public void onSuccess(List<UserScore> scores) {
                dailyList.setValue(scores);

            }
            @Override
            public void onError(String error) {

            }
        });
    }

}
