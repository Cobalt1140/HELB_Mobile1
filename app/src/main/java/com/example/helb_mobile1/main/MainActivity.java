package com.example.helb_mobile1.main;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.helb_mobile1.R;
import com.example.helb_mobile1.auth.AuthManager;
import com.example.helb_mobile1.auth.LoginFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    WordOfTheDayFragment wotdFragment = new WordOfTheDayFragment();
    MapFragment mapFragment = new MapFragment();
    LeaderboardFragment leaderboardFragment = new LeaderboardFragment();
    AccountFragment accountFragment = new AccountFragment();


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

        if (savedInstanceState == null) { // Only add fragment first time
            loadFragment(wotdFragment);
        }

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
        transaction.replace(R.id.main_fragment_container, fragment); // Replaces current fragment
        //transaction.addToBackStack(null); // Optional: Adds fragment to back stack
        transaction.commit();
    }
}