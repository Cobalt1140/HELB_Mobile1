package com.example.helb_mobile1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    /*
    This is the first activity the user goes through each time they launch the app, it contains the
    logistics for logging in and registering a new user.
     */

    private FirebaseAuth mAuth;
    private Button loginButton;
    private EditText mail, pwd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //default stuff
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.contr_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //custom code
        mAuth = FirebaseAuth.getInstance();

        Button switchRegisterButton = (Button) findViewById(R.id.Switch_to_Register_Button);
        switchRegisterButton.setOnClickListener(this);

        loginButton = (Button) findViewById(R.id.Login_Button);
        loginButton.setOnClickListener(this);

        mail = (EditText) findViewById(R.id.Login_Email_Input);
        pwd = (EditText) findViewById(R.id.Login_Password_Input);


    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            //does nothing but checks if user is already logged in
        } else {

        }


    }

    public void onClick(View v){

        int id = v.getId();
        if (id == R.id.Switch_to_Register_Button) {
            startActivity(new Intent(this, RegisterActivity.class));
        } else if (id == R.id.Login_Button){
            final String _mail = mail.getText().toString().trim();
            final String _password = pwd.getText().toString().trim();
            if (_mail.isEmpty() || _password.isEmpty()){
                loginButton.setError("");
                loginButton.requestFocus();
                Toast.makeText(getApplicationContext(), "Some fields are empty", Toast.LENGTH_SHORT).show();
                return;
            }
            signInUser(_mail, _password);
        }


    }

    private void signInUser(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(new Intent(MainActivity.this, DailyWordDisplayActivity.class));

                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                    }
                });
    }


}