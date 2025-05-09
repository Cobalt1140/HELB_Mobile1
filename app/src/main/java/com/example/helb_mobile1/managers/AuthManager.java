package com.example.helb_mobile1.managers;


import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthManager {
    /*
    manager for everything related directly to Firebase Authentication
     */
    private final FirebaseAuth mAuth;
    private static AuthManager instance; //singleton
    private AuthManager(){
        mAuth = FirebaseAuth.getInstance();
    }

    public static synchronized AuthManager getInstance() {
        //AuthManager can only be accessed through getInstance, as it is a singleton
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

    public String getCurrentUid(){
        if (isLoggedIn()){
            return getCurrentUser().getUid();
        } else {
            return null;
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
