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


        /*
        dailyWordFragment = new DailyWordFragment();
        mapFragment = new MapFragment();
        leaderboardFragment = new LeaderboardFragment();
        accountFragment = new AccountFragment();
        activeFragment = dailyWordFragment;

         */

        if (savedInstanceState == null) {
            // First-time creation
            dailyWordFragment = new DailyWordFragment();
            mapFragment = new MapFragment();
            leaderboardFragment = new LeaderboardFragment();
            accountFragment = new AccountFragment();
            activeFragment = dailyWordFragment;

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_fragment_container, mapFragment, "2")
                    .hide(mapFragment)
                    .commit();

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_fragment_container, leaderboardFragment, "3")
                    .hide(leaderboardFragment)
                    .commit();

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_fragment_container, accountFragment, "4")
                    .hide(accountFragment)
                    .commit();

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_fragment_container, dailyWordFragment, "1")
                    .commit();
        } else {
            // Restore fragments
            dailyWordFragment = (DailyWordFragment) getSupportFragmentManager().findFragmentByTag("1");
            mapFragment = (MapFragment) getSupportFragmentManager().findFragmentByTag("2");
            leaderboardFragment = (LeaderboardFragment) getSupportFragmentManager().findFragmentByTag("3");
            accountFragment = (AccountFragment) getSupportFragmentManager().findFragmentByTag("4");

            // Determine which fragment is visible and set it as active
            if (dailyWordFragment != null && dailyWordFragment.isVisible()) {
                activeFragment = dailyWordFragment;
            } else if (mapFragment != null && mapFragment.isVisible()) {
                activeFragment = mapFragment;
            } else if (leaderboardFragment != null && leaderboardFragment.isVisible()) {
                activeFragment = leaderboardFragment;
            } else if (accountFragment != null && accountFragment.isVisible()) {
                activeFragment = accountFragment;
            } else {
                // Fallback in case none are visible
                activeFragment = dailyWordFragment != null ? dailyWordFragment : new DailyWordFragment();
            }
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



    public void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.hide(activeFragment);
        transaction.show(fragment);
        transaction.commit();
        activeFragment = fragment;
        ((IOnFragmentVisibleListener) fragment).onFragmentVisible();

    }
}