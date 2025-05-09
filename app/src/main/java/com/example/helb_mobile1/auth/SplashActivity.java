package com.example.helb_mobile1.auth;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.helb_mobile1.managers.AuthManager;
import com.example.helb_mobile1.main.MainActivity;
import com.example.helb_mobile1.managers.PreferencesManager;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {
    /*
    Entry Activity for the app, determines if you have to log In or go straight to MainActivity
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        //Get Firebase User with AuthManager
        if (AuthManager.getInstance().isLoggedIn()) {
            // User already logged in
            Log.d("SplashCheck", "User is still logged in: " + AuthManager.getInstance().getCurrentUser().getEmail());
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {

            // Not logged in, go to Auth screen
            Log.d("SplashCheck", "User is logged out");
            PreferencesManager.getInstance(this).resetPersonalMarkerInCache();
            Intent intent = new Intent(this, AuthActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);        }
        finish();
    }
}