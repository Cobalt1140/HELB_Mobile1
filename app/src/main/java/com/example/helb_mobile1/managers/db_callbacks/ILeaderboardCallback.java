package com.example.helb_mobile1.managers.db_callbacks;

import com.example.helb_mobile1.models.UserScore;

import java.util.List;

public interface ILeaderboardCallback {
    public void onSuccess(List<UserScore> userScores);
    public void onError(String message);
}
