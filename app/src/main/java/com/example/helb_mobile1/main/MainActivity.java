package com.example.helb_mobile1.main;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    private DailyWordFragment wotdFragment = new DailyWordFragment();
    private MapFragment mapFragment = new MapFragment();
    private LeaderboardFragment leaderboardFragment = new LeaderboardFragment();
    private AccountFragment accountFragment = new AccountFragment();
    private Fragment activeFragment = wotdFragment;



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

        bottomNavigationView = findViewById(R.id.main_bottom_navigation);

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
                .add(R.id.main_fragment_container,wotdFragment , "1")
                .commit();

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.word_of_the_day) {
                    loadFragment(wotdFragment);
                    return true;
                } else if (id == R.id.map){
                    loadFragment(mapFragment);
                    return true;
                } else if (id == R.id.leaderboard){
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