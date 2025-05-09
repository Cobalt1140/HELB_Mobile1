package com.example.helb_mobile1.main;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.helb_mobile1.R;
import com.example.helb_mobile1.main.account.AccountFragment;
import com.example.helb_mobile1.main.dailyWord.DailyWordFragment;
import com.example.helb_mobile1.main.leaderboard.LeaderboardFragment;
import com.example.helb_mobile1.main.map.MapFragment;
import com.example.helb_mobile1.managers.AppNotificationManager;
import com.example.helb_mobile1.managers.NotificationReceiver;
import com.example.helb_mobile1.managers.TimeConfig;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private static final String DAILY_WORD_TAG = "word_tag";
    private static final String MAP_TAG = "map_tag";
    private static final String LEADERBOARD_TAG = "leaderboard_tag";
    private static final String ACCOUNT_TAG = "account_tag";
    private static final String ACTIVE_TAG = "active_tag";


    private DailyWordFragment dailyWordFragment;
    private MapFragment mapFragment;
    private LeaderboardFragment leaderboardFragment;
    private AccountFragment accountFragment;
    private Fragment activeFragment;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_container), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.main_bottom_navigation);

        AppNotificationManager notificationManager = new AppNotificationManager(this);
        notificationManager.scheduleReminder(AppNotificationManager.NOTIF_TYPE_DAILY_RESULTS,
                TimeConfig.PUBLISH_TIME_HOUR, AppNotificationManager.REQUEST_CODE_RESULTS);
        notificationManager.scheduleReminder(AppNotificationManager.NOTIF_TYPE_DAILY_WORD,
                TimeConfig.NEW_WORD_TIME_HOUR, AppNotificationManager.REQUEST_CODE_WORD);


        if (savedInstanceState == null) {
            // First-time creation
            dailyWordFragment = new DailyWordFragment();
            mapFragment = new MapFragment();
            leaderboardFragment = new LeaderboardFragment();
            accountFragment = new AccountFragment();
            activeFragment = dailyWordFragment;

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_fragment_container, mapFragment, MAP_TAG)
                    .hide(mapFragment)
                    .commit();

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_fragment_container, leaderboardFragment, LEADERBOARD_TAG)
                    .hide(leaderboardFragment)
                    .commit();

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_fragment_container, accountFragment, ACCOUNT_TAG)
                    .hide(accountFragment)
                    .commit();

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_fragment_container, dailyWordFragment, DAILY_WORD_TAG)
                    .commit();
        } else {
            String tag = savedInstanceState.getString(ACTIVE_TAG);
            // Restore fragments
            Fragment found = getSupportFragmentManager().findFragmentByTag(DAILY_WORD_TAG);
            if (found instanceof DailyWordFragment) {
                dailyWordFragment = (DailyWordFragment) found;
            } else {
                dailyWordFragment = new DailyWordFragment();
            }
            found = getSupportFragmentManager().findFragmentByTag(MAP_TAG);
            if (found instanceof MapFragment) {
                mapFragment = (MapFragment) found;
            } else {
                mapFragment = new MapFragment();
            }
            found = getSupportFragmentManager().findFragmentByTag(LEADERBOARD_TAG);
            if (found instanceof LeaderboardFragment) {
                leaderboardFragment = (LeaderboardFragment) found;
            } else {
                leaderboardFragment = new LeaderboardFragment();
            }
            found = getSupportFragmentManager().findFragmentByTag(ACCOUNT_TAG);
            if (found instanceof AccountFragment) {
                accountFragment = (AccountFragment) found;
            } else {
                accountFragment = new AccountFragment();
            }


            activeFragment = getSupportFragmentManager().findFragmentByTag(tag);

            hideRestOfFragments();
        }






        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.word_of_the_day) {
                    loadFragment(dailyWordFragment);
                    return true;
                } else if (id == R.id.map){
                    loadFragment(mapFragment);
                    return true;
                } else if (id == R.id.leaderboard_rank_text){
                    loadFragment(leaderboardFragment);
                    return true;
                } else if (id == R.id.account){
                    loadFragment(accountFragment);
                    return true;
                }
                return false;
            }
        });




    }

    private void hideRestOfFragments(){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (!activeFragment.equals(dailyWordFragment)){
            transaction.hide(dailyWordFragment);
        }
        if (!activeFragment.equals(mapFragment)){
            transaction.hide(mapFragment);
        }
        if (!activeFragment.equals(leaderboardFragment)){
            transaction.hide(leaderboardFragment);
        }
        if (!activeFragment.equals(accountFragment)){
            transaction.hide(accountFragment);
        }
        transaction.show(activeFragment);
        transaction.commit();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ACTIVE_TAG, activeFragment.getTag());
    }



    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.hide(activeFragment);
        transaction.show(fragment);
        transaction.commit();
        activeFragment = fragment;
        if (fragment instanceof IOnFragmentVisibleListener) {
            ((IOnFragmentVisibleListener) fragment).onFragmentVisible();
        }

    }
}