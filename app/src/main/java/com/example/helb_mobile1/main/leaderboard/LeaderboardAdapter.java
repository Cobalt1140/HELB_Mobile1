package com.example.helb_mobile1.main.leaderboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helb_mobile1.R;
import com.example.helb_mobile1.models.UserScore;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {
    private List<UserScore> userScores = new ArrayList<>();

    public void updateList(List<UserScore> newScores) {
        userScores = newScores;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_leaderboard, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserScore userScore = userScores.get(position);
        holder.rankText.setText(String.valueOf(position + 1));
        holder.usernameText.setText(userScore.getUsername());
        holder.scoreText.setText(String.valueOf(userScore.getScore()));
    }



    @Override
    public int getItemCount() {
        return userScores.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView rankText, usernameText, scoreText;

        public ViewHolder(View itemView) {
            super(itemView);
            rankText = itemView.findViewById(R.id.leaderboard_rank_text);
            usernameText = itemView.findViewById(R.id.leaderboard_username_text);
            scoreText = itemView.findViewById(R.id.leaderboard_score_text);
        }
    }
}
