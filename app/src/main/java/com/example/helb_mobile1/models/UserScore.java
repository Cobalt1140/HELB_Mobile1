package com.example.helb_mobile1.models;

public class UserScore {
    /*
    UserScore data model for use with Leaderboard management
     */
    private final String username;
    private final long score;

    public UserScore(String username, long score) {
        this.username = username;
        this.score = score;
    }

    public String getUsername() {
        return username;
    }

    public long getScore() {
        return score;
    }
}
