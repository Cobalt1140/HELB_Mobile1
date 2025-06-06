package com.example.helb_mobile1.auth;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.helb_mobile1.managers.AuthManager;
import com.example.helb_mobile1.managers.DatabaseManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class AuthViewModel extends ViewModel {
    /*
    ViewModel for the AuthActivity, handles data for both fragments
     */

    private final AuthManager authManager;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false); //LiveData for loading times
    private final MutableLiveData<String> authError = new MutableLiveData<>(); //toast for errors
    private final MutableLiveData<Boolean> isLoggedIn = new MutableLiveData<>(); //Logs in the user once true

    public AuthViewModel() {
        authManager = AuthManager.getInstance(); //gets singleton authManager
        isLoggedIn.setValue(authManager.isLoggedIn());
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getAuthError() {
        return authError;
    }

    public LiveData<Boolean> getIsLoggedIn() {
        return isLoggedIn;
    }


    public void login(String email, String password) {
        //tries to login the user with given email and password through firebase Authentication
        isLoading.setValue(true);
        authManager.signInUser(email, password)
                .addOnCompleteListener(task -> {
                    isLoading.setValue(false);
                    if (task.isSuccessful()) {

                        isLoggedIn.setValue(true);
                    } else {
                        authError.setValue(task.getException().getMessage());

                    }
                });
    }

    public void register(String email, String password, String username) {
        /*
        creates a new user in Firebase authentication and creates a new UserProfile in DB with given parameters
        checks if username is already taken
         */
        isLoading.setValue(true);

        DatabaseManager.getInstance().handleIsUsernameTaken(username, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){
                    authError.setValue("Username is already taken!");
                    isLoading.setValue(false);
                } else {
                    authManager.registerUser(email, password)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    DatabaseManager.getInstance().createUserProfile(AuthManager.getInstance().getCurrentUid(),username);
                                    isLoading.setValue(false);
                                    isLoggedIn.setValue(true);
                                } else {
                                    authError.setValue(task.getException().getMessage());
                                    isLoading.setValue(false);
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                authError.setValue("Error checking username with database: "+error.getMessage());
                isLoading.setValue(false);
            }
        });


    }


}
