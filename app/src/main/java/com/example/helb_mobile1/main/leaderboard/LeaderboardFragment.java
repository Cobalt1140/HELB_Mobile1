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
    /*
    One of the 4 main fragments in MainActivity, handles what is related to the leaderboard tab
     */

    private LeaderboardAdapter adapter;
    private TabLayout tabLayout;

    private List<UserScore> globalList = new ArrayList<>();
    private List<UserScore> dailyList = new ArrayList<>();

    private static final String GLOBAL = "Global";
    private static final String DAILY = "Quotidien";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /*
        upon creation of fragment, handles views and sets up the leaderboard and it's adapter,
        triggers initial methods
         */
        View view = inflater.inflate(R.layout.fragment_leaderboard, container, false);

        tabLayout = view.findViewById(R.id.tab_layout);
        RecyclerView recyclerLeaderboard = view.findViewById(R.id.recyclerLeaderboard); //Recycler View
        recyclerLeaderboard.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new LeaderboardAdapter(); //adapts the recycler View according to data given
        recyclerLeaderboard.setAdapter(adapter);

        setupTabs();
        fetchLeaderboards();

        return view;
    }

    private void setupTabs() {
        /*
        adds both daily and global tabs, to sort users by daily or global points
        adds listener for the tab selection
         */
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
        /*
        fetches user Data from DB for both the Daily Points ordered List and Global Points ordered List
         */
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
