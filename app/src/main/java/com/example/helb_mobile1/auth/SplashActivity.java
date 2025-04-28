package com.example.helb_mobile1.auth;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.helb_mobile1.main.MainActivity;
import com.google.firebase.auth.FirebaseAuth;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //Get Firebase User with AuthManager
        if (AuthManager.getInstance().isLoggedIn() != null) {
            // User already logged in
            startActivity(new Intent(this, MainActivity.class));
        } else {
            // Not logged in, go to Auth screen
            startActivity(new Intent(this, AuthActivity.class));
        }
        finish(); // Close Splash screen
    }
}