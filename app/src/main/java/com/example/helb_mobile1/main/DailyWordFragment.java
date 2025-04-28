package com.example.helb_mobile1.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.helb_mobile1.R;
import com.example.helb_mobile1.auth.LoginFragment;
import com.google.firebase.auth.FirebaseAuth;

public class DailyWordFragment extends AppCompatActivity implements View.OnClickListener {
    private Button logOutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.fragment_daily_word);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.contr_layout_splash), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        logOutButton = (Button) findViewById(R.id.LogOut_Button);
        logOutButton.setOnClickListener(this);

    }


    public void onClick(View v){

        int id = v.getId();
        if (id == R.id.LogOut_Button) {
            logOut();
            startActivity(new Intent(this, LoginFragment.class));
        }


    }

    private void logOut(){
        FirebaseAuth.getInstance().signOut();
    }



}