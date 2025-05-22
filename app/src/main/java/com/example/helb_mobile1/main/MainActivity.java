package com.example.helb_mobile1.main;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.content.ContextCompat;
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
import com.example.helb_mobile1.managers.TimeConfig;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {
    /*
    The Main Activity which holds the 4 main fragments of each tab screen
    handles switching between the different fragments, screen rotation and app notifications
     */

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

        //notifications for Results time and New Word time
        AppNotificationManager notificationManager = new AppNotificationManager(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { //Android API >=33, explicit permission required
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                final ActivityResultLauncher<String> notificationPermissionLauncher =
                        registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                            if (isGranted) {
                                scheduleReminder(notificationManager);
                            } else {
                                Toast.makeText(this,"Notification Permissions denied", Toast.LENGTH_SHORT).show();
                            }
                        });
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        } else { //Android API < 33, no permissions required
            scheduleReminder(notificationManager);

        }

        if (savedInstanceState == null) {
            // First-time creation of fragments

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
            //Code generated by ChatGPT, modified
            // Restore fragments instead of recreating them

            //gets the tag for the active tag before the fragments were destroyed
            String tag = savedInstanceState.getString(ACTIVE_TAG);

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
            /*
            listener for clicking the items in the BottomNavigationView
             */
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
        /*
        if fragments are recreated or restored, they might be shown overlapped
        this method hides all fragments except for the one who had the active tag before being destroyed
         */
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
        //Code suggested by ChatGPT, modified
        super.onSaveInstanceState(outState);
        //before being destroyed, the active fragment will save a tag which will be saved for when fragments have to be restored
        outState.putString(ACTIVE_TAG, activeFragment.getTag());
    }

    private void scheduleReminder(AppNotificationManager notificationManager){
        notificationManager.scheduleReminder(AppNotificationManager.NOTIF_TYPE_DAILY_RESULTS,
                TimeConfig.PUBLISH_TIME_HOUR, AppNotificationManager.REQUEST_CODE_RESULTS);
        notificationManager.scheduleReminder(AppNotificationManager.NOTIF_TYPE_DAILY_WORD,
                TimeConfig.NEW_WORD_TIME_HOUR, AppNotificationManager.REQUEST_CODE_WORD);

    }


    private void loadFragment(Fragment fragment) {
        /*
        shows the given fragment in the parameter, hiding the last active fragment
        we hide and show instead of replacing as the map is costly to recreate every time, and replacing destroys the fragment
         */
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