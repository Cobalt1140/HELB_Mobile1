package com.example.helb_mobile1.auth;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthManager {
    private final FirebaseAuth mAuth;
    private static AuthManager instance; //singleton
    private AuthManager(){
        mAuth = FirebaseAuth.getInstance();
    }

    public static synchronized AuthManager getInstance() {
        if (instance == null) {
            instance = new AuthManager();
        }
        return instance;
    }

    public Boolean isLoggedIn(){
        return mAuth.getCurrentUser()!=null;
    }

    public FirebaseUser getCurrentUser(){
        if (isLoggedIn()){
            return mAuth.getCurrentUser();
        } else {
            return null;
        }
    }

    public void deleteUserAccount(){
        if (isLoggedIn()){
            mAuth.getCurrentUser().delete();
        }
    }

    public Task<AuthResult> signInUser(String email, String password){
        return mAuth.signInWithEmailAndPassword(email, password);
    }

    public Task<AuthResult> registerUser(String email, String password){
        return mAuth.createUserWithEmailAndPassword(email, password);
    }


    public void signOutUser(){
        mAuth.signOut();
    }
}
