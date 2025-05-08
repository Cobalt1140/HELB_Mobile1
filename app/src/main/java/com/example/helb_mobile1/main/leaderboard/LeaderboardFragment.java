package com.example.helb_mobile1.main.leaderboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helb_mobile1.R;
import com.example.helb_mobile1.main.IOnFragmentVisibleListener;
import com.example.helb_mobile1.managers.DatabaseManager;
import com.example.helb_mobile1.managers.db_callbacks.ILeaderboardCallback;
import com.example.helb_mobile1.models.UserScore;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardFragment extends Fragment implements IOnFragmentVisibleListener {

    private LeaderboardAdapter adapter;
    private TabLayout tabLayout;

    private List<UserScore> globalList = new ArrayList<>();
    private List<UserScore> dailyList = new ArrayList<>();

    private static final String GLOBAL = "Global";
    private static final String DAILY = "Daily";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leaderboard, container, false);

        tabLayout = view.findViewById(R.id.tab_layout);
        RecyclerView recyclerLeaderboard = view.findViewById(R.id.recyclerLeaderboard);
        recyclerLeaderboard.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new LeaderboardAdapter();
        recyclerLeaderboard.setAdapter(adapter);

        setupTabs();
        fetchLeaderboards();

        return view;
    }

    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText(GLOBAL));
        tabLayout.addTab(tabLayout.newTab().setText(DAILY));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    adapter.updateList(globalList);
                } else {
                    adapter.updateList(dailyList);
                }
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void fetchLeaderboards() {
        DatabaseManager.getInstance().fetchAndHandleLeaderboard(false,new ILeaderboardCallback() {
            @Override
            public void onSuccess(List<UserScore> scores) {
                globalList = scores;
                if (tabLayout.getSelectedTabPosition() == 0) {
                    adapter.updateList(globalList);
                }
            }
            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), "Global leaderboard error: " + error, Toast.LENGTH_SHORT).show();
            }
        });

        DatabaseManager.getInstance().fetchAndHandleLeaderboard(true,new ILeaderboardCallback() {
            @Override
            public void onSuccess(List<UserScore> scores) {
                dailyList = scores;
                if (tabLayout.getSelectedTabPosition() == 1) {
                    adapter.updateList(dailyList);
                }
            }
            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), "Daily leaderboard error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onFragmentVisible() {

    }
}
